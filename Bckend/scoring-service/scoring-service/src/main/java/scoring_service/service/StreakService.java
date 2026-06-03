package scoring_service.service;

import scoring_service.entity.ScoreHistory;
import scoring_service.repository.ScoreHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final ScoreHistoryRepository scoreHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public int getCurrentStreak(Long userId) {
        String cacheKey = "streak:" + userId;

        Integer cachedStreak = (Integer) redisTemplate.opsForValue().get(cacheKey);
        if (cachedStreak != null) {
            return cachedStreak;
        }

        // Calculate streak from database
        List<ScoreHistory> recentHistory = scoreHistoryRepository
            .findRecentCorrectPredictions(userId, LocalDateTime.now().minusDays(7));

        int streak = calculateStreak(recentHistory);

        // Cache for 1 hour
        redisTemplate.opsForValue().set(cacheKey, streak, 3600);

        return streak;
    }

    private int calculateStreak(List<ScoreHistory> history) {
        int streak = 0;
        for (ScoreHistory h : history) {
            if (h.getReason().contains("correct")) {
                streak++;
            } else {
                streak = 0;
            }
        }
        return streak;
    }

    public void resetStreak(Long userId) {
        String cacheKey = "streak:" + userId;
        redisTemplate.delete(cacheKey);
    }
}