package scoring_service.service;

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
            .map(sh -> new ScoreHistoryDTO(sh.getId(), sh.getUserId(), sh.getPoints(), sh.getReason(), sh.getCreatedAt()))
            .toList();
    }
}