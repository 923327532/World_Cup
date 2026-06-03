package worldcup_service.service;

import worldcup_service.dto.MatchDTO;
import worldcup_service.entity.Match;
import worldcup_service.exception.ResourceNotFoundException;
import worldcup_service.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<MatchDTO> getMatchesByDate(LocalDate date) {
        String cacheKey = "matches:" + date;

        @SuppressWarnings("unchecked")
        List<MatchDTO> cached = (List<MatchDTO>) redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            return cached;
        }

        List<Match> matches = matchRepository.findByKickoffTimeBetween(
            date.atStartOfDay(),
            date.plusDays(1).atStartOfDay()
        );

        List<MatchDTO> result = matches.stream()
            .map(this::toDTO)
            .toList();

        redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);

        return result;
    }

    public MatchDTO getMatchById(Long id) {
        Match match = matchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Match", id));
        return toDTO(match);
    }

    private MatchDTO toDTO(Match match) {
        return new MatchDTO(
            match.getId(),
            match.getHomeTeam().getName(),
            match.getAwayTeam().getName(),
            match.getKickoffTime(),
            match.getHomeScore(),
            match.getAwayScore(),
            match.getStatus()
        );
    }
}
