package leaderboard_service.service;

import leaderboard_service.dto.LeaderboardEntryDTO;
import leaderboard_service.repository.LeaderboardEntryRepository;
import leaderboard_service.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardCacheService {

    private final LeaderboardRepository leaderboardRepository;
    private final LeaderboardEntryRepository entryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long CACHE_TTL_MINUTES = 5;

    public void invalidateCache(String type) {
        String pattern = "ranking:" + type + ":*";
        var keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Invalidated {} cache keys for type={}", keys.size(), type);
        }
    }

    public void invalidateAllCaches() {
        invalidateCache("global");
        invalidateCache("campus");
        invalidateCache("career");
        invalidateCache("department");
        log.info("All leaderboard caches invalidated");
    }

    @SuppressWarnings("unchecked")
    public List<LeaderboardEntryDTO> getCachedRanking(String cacheKey) {
        return (List<LeaderboardEntryDTO>) redisTemplate.opsForValue().get(cacheKey);
    }

    public void cacheRanking(String cacheKey, List<LeaderboardEntryDTO> ranking) {
        redisTemplate.opsForValue().set(cacheKey, ranking, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
    }
}
