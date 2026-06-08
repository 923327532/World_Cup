package scoring_service.service;

import scoring_service.dto.ScoreHistoryDTO;
import scoring_service.dto.UserScoreDTO;
import scoring_service.entity.ScoreHistory;
import scoring_service.entity.UserScore;
import scoring_service.repository.ScoreHistoryRepository;
import scoring_service.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private UserScoreDTO toDTO(UserScore score) {
        return new UserScoreDTO(score.getUserId(), score.getTotalPoints());
    }

    private ScoreHistoryDTO toHistoryDTO(ScoreHistory h) {
        return new ScoreHistoryDTO(
            h.getId(),
            h.getUserId(),
            h.getPoints(),
            h.getReason(),
            h.getCreatedAt()
        );
    }
}
