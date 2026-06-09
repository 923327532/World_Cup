package scoring_service.service;

import scoring_service.dto.ScoreHistoryDTO;
import scoring_service.dto.RoomRankingEntryDTO;
import scoring_service.dto.UserScoreDTO;
import scoring_service.entity.ScoreHistory;
import scoring_service.entity.UserScore;
import scoring_service.repository.ScoreHistoryRepository;
import scoring_service.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserScoreService {

    private final UserScoreRepository userScoreRepository;
    private final ScoreHistoryRepository scoreHistoryRepository;

    public UserScoreDTO getUserScore(Long userId) {
        UserScore score = userScoreRepository.findById(userId)
            .orElse(new UserScore(userId, 0L));
        return toDTO(score);
    }

    public List<ScoreHistoryDTO> getUserScoreHistory(Long userId) {
        return scoreHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(this::toHistoryDTO)
            .toList();
    }

    public List<RoomRankingEntryDTO> getRoomRanking(Long roomId) {
        Map<Long, Long> pointsByUser = scoreHistoryRepository.findByRoomId(roomId).stream()
            .collect(Collectors.groupingBy(
                ScoreHistory::getUserId,
                LinkedHashMap::new,
                Collectors.summingLong(history -> history.getPoints() != null ? history.getPoints() : 0)
            ));

        AtomicInteger position = new AtomicInteger(1);

        return pointsByUser.entrySet().stream()
            .sorted(Map.Entry.<Long, Long>comparingByValue(Comparator.reverseOrder()))
            .map(entry -> new RoomRankingEntryDTO(
                roomId,
                entry.getKey(),
                position.getAndIncrement(),
                entry.getValue()
            ))
            .toList();
    }

    private UserScoreDTO toDTO(UserScore score) {
        return new UserScoreDTO(score.getUserId(), score.getTotalPoints());
    }

    private ScoreHistoryDTO toHistoryDTO(ScoreHistory h) {
        return new ScoreHistoryDTO(
            h.getId(),
            h.getUserId(),
            h.getRoomId(),
            h.getMatchId(),
            h.getPredictionId(),
            h.getPoints(),
            h.getBasePoints(),
            h.getStreakBonus(),
            h.getEarlyBonus(),
            h.getReason(),
            h.getCreatedAt()
        );
    }
}
