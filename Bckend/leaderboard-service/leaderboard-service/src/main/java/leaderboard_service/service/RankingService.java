package leaderboard_service.service;

import leaderboard_service.dto.LeaderboardEntryDTO;
import leaderboard_service.entity.Leaderboard;
import leaderboard_service.entity.LeaderboardEntry;
import leaderboard_service.repository.LeaderboardEntryRepository;
import leaderboard_service.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final LeaderboardRepository leaderboardRepository;
    private final LeaderboardEntryRepository entryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<LeaderboardEntryDTO> getGlobalRanking(int limit) {
        String cacheKey = "ranking:global:" + limit;

        @SuppressWarnings("unchecked")
        List<LeaderboardEntryDTO> cached = (List<LeaderboardEntryDTO>) redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            return cached;
        }

        Leaderboard globalLeaderboard = leaderboardRepository.findByType("GLOBAL")
            .orElseThrow(() -> new RuntimeException("Global leaderboard not found"));

        List<LeaderboardEntry> entries = entryRepository
            .findByLeaderboardIdOrderByPointsDesc(globalLeaderboard.getId())
            .stream()
            .limit(limit)
            .toList();

        List<LeaderboardEntryDTO> result = entries.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());

        redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);

        return result;
    }

    public void updateUserRanking(Long userId, Long points) {
        // Update all leaderboards where user appears
        List<Leaderboard> leaderboards = leaderboardRepository.findAll();

        leaderboards.forEach(leaderboard -> {
            LeaderboardEntry entry = entryRepository
                .findByLeaderboardIdAndUserId(leaderboard.getId(), userId)
                .orElse(new LeaderboardEntry());

            entry.setLeaderboard(leaderboard);
            entry.setUserId(userId);
            entry.setPoints(points);

            entryRepository.save(entry);
        });

        // Recalculate positions
        recalculatePositions();

        // Clear cache
        clearCache();
    }

    private void recalculatePositions() {
        List<Leaderboard> leaderboards = leaderboardRepository.findAll();

        leaderboards.forEach(leaderboard -> {
            List<LeaderboardEntry> entries = entryRepository
                .findByLeaderboardIdOrderByPointsDesc(leaderboard.getId());

            for (int i = 0; i < entries.size(); i++) {
                entries.get(i).setRankingPosition(i + 1);
                entryRepository.save(entries.get(i));
            }
        });
    }

    private void clearCache() {
        // Clear all ranking caches
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    private LeaderboardEntryDTO toDTO(LeaderboardEntry entry) {
        return new LeaderboardEntryDTO(
            entry.getId(),
            entry.getUserId(),
            entry.getRankingPosition(),
            entry.getPoints()
        );
    }
}