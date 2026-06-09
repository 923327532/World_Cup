package scoring_service.service;

import scoring_service.dto.PredictionScoreRequest;
import scoring_service.dto.ScoreBreakdownDTO;
import scoring_service.dto.ScoreHistoryDTO;
import scoring_service.entity.ScoreHistory;
import scoring_service.entity.UserScore;
import scoring_service.repository.ScoreHistoryRepository;
import scoring_service.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreCalculatorService {

    private final UserScoreRepository userScoreRepository;
    private final ScoreHistoryRepository scoreHistoryRepository;
    private final RabbitTemplate rabbitTemplate;
    private final StreakService streakService;
    private final BonusService bonusService;

    public ScoreBreakdownDTO calculatePredictionScore(PredictionScoreRequest request) {
        validateScoreRequest(request);

        int basePoints;
        String ruleApplied;

        if (isExactScore(request)) {
            basePoints = 5;
            ruleApplied = "EXACT_SCORE";
        } else if (isSameOutcome(request)) {
            basePoints = 3;
            ruleApplied = "WINNER_OR_DRAW";
        } else if (isSameGoalDifference(request)) {
            basePoints = 2;
            ruleApplied = "GOAL_DIFFERENCE";
        } else {
            basePoints = 0;
            ruleApplied = "NO_POINTS";
        }

        boolean correctForStreak = basePoints > 0;
        int streakBefore = request.currentStreakBeforeMatch() != null ? request.currentStreakBeforeMatch() : 0;
        int streakAfter = correctForStreak ? streakBefore + 1 : 0;
        int streakBonus = correctForStreak && streakAfter % 3 == 0 ? 2 : 0;
        int earlyBonus = isEarlyPrediction(request) ? 1 : 0;

        return new ScoreBreakdownDTO(
            basePoints,
            streakBonus,
            earlyBonus,
            basePoints + streakBonus + earlyBonus,
            ruleApplied,
            correctForStreak
        );
    }

    @Transactional
    public void addPoints(Long userId, Integer points, String reason) {
        UserScore userScore = userScoreRepository.findById(userId)
            .orElse(new UserScore(userId, 0L));

        userScore.setTotalPoints(userScore.getTotalPoints() + points);
        userScoreRepository.save(userScore);

        // Record history
        ScoreHistory history = new ScoreHistory();
        history.setUserId(userId);
        history.setPoints(points);
        history.setBasePoints(points);
        history.setStreakBonus(0);
        history.setEarlyBonus(0);
        history.setReason(reason);
        history.setCreatedAt(LocalDateTime.now());
        scoreHistoryRepository.save(history);

        // Check for streaks
        int currentStreak = streakService.getCurrentStreak(userId);
        int bonusPoints = bonusService.calculateStreakBonus(currentStreak);

        if (bonusPoints > 0) {
            addPoints(userId, bonusPoints, "Streak bonus: " + currentStreak);
        }

        // Publish event
        rabbitTemplate.convertAndSend("scoring.exchange", "score.updated", userId);
    }

    @Transactional
    public void calculateAndUpdateScores(Long matchId, Integer homeScore, Integer awayScore, String winner) {
        log.info("Calculating scores for match {}: {} - {}, winner={}", matchId, homeScore, awayScore, winner);
        // This method is called when a match finishes.
        // The actual scoring logic for predictions is handled by the prediction-service
        // which publishes events to the "prediction.scored" queue.
        // Here we just log and trigger any additional scoring updates needed.
        // The prediction-service will call addPoints for each user's prediction.
        rabbitTemplate.convertAndSend("scoring.exchange", "match.scored", 
            java.util.Map.of("matchId", matchId, "homeScore", homeScore, "awayScore", awayScore, "winner", winner));
    }

    public Long getUserScore(Long userId) {
        return userScoreRepository.findById(userId)
            .map(UserScore::getTotalPoints)
            .orElse(0L);
    }

    public List<ScoreHistoryDTO> getUserScoreHistory(Long userId) {
        return scoreHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(sh -> new ScoreHistoryDTO(
                sh.getId(),
                sh.getUserId(),
                sh.getRoomId(),
                sh.getMatchId(),
                sh.getPredictionId(),
                sh.getPoints(),
                sh.getBasePoints(),
                sh.getStreakBonus(),
                sh.getEarlyBonus(),
                sh.getReason(),
                sh.getCreatedAt()
            ))
            .toList();
    }

    private void validateScoreRequest(PredictionScoreRequest request) {
        if (request.predictedHomeScore() == null || request.predictedAwayScore() == null ||
            request.actualHomeScore() == null || request.actualAwayScore() == null) {
            throw new IllegalArgumentException("Scores are required");
        }

        if (request.predictedHomeScore() < 0 || request.predictedAwayScore() < 0 ||
            request.actualHomeScore() < 0 || request.actualAwayScore() < 0) {
            throw new IllegalArgumentException("Scores cannot be negative");
        }
    }

    private boolean isExactScore(PredictionScoreRequest request) {
        return request.predictedHomeScore().equals(request.actualHomeScore()) &&
            request.predictedAwayScore().equals(request.actualAwayScore());
    }

    private boolean isSameOutcome(PredictionScoreRequest request) {
        return Integer.signum(request.predictedHomeScore() - request.predictedAwayScore()) ==
            Integer.signum(request.actualHomeScore() - request.actualAwayScore());
    }

    private boolean isSameGoalDifference(PredictionScoreRequest request) {
        int predictedDifference = request.predictedHomeScore() - request.predictedAwayScore();
        int actualDifference = request.actualHomeScore() - request.actualAwayScore();
        return predictedDifference == actualDifference &&
            Integer.signum(predictedDifference) == Integer.signum(actualDifference);
    }

    private boolean isEarlyPrediction(PredictionScoreRequest request) {
        return request.predictedAt() != null &&
            request.kickoffTime() != null &&
            request.predictedAt().isBefore(request.kickoffTime().minusHours(24));
    }
}
