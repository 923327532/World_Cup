package worldcup_service.service;

import worldcup_service.dto.MatchDTO;
import worldcup_service.entity.Match;
import worldcup_service.exception.ResourceNotFoundException;
import worldcup_service.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public List<MatchDTO> getAllMatches() {
        return matchRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public MatchDTO getMatchById(Long id) {
        Match match = matchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Match", id));
        return toDTO(match);
    }

    @Transactional(readOnly = true)
    public List<MatchDTO> getLiveMatches() {
        return matchRepository.findByStatus("LIVE").stream()
            .map(this::toDTO)
            .toList();
    }

    private MatchDTO toDTO(Match match) {
        return new MatchDTO(
            match.getId(),
            match.getStadium() != null ? match.getStadium().getId() : null,
            match.getStadium() != null ? match.getStadium().getName() : null,
            match.getStadium() != null ? match.getStadium().getCity() : null,
            match.getStadium() != null ? match.getStadium().getCountry() : null,
            match.getStadium() != null ? match.getStadium().getCapacity() : null,
            match.getVenue(),
            match.getStage() != null ? match.getStage().getName() : null,
            match.getGroup() != null ? match.getGroup().getName() : null,
            match.getHomeTeam() != null ? match.getHomeTeam().getId() : null,
            match.getHomeTeam() != null ? match.getHomeTeam().getName() : match.getHomeTeamLabel(),
            match.getAwayTeam() != null ? match.getAwayTeam().getId() : null,
            match.getAwayTeam() != null ? match.getAwayTeam().getName() : match.getAwayTeamLabel(),
            match.getKickoffTime(),
            match.getHomeScore(),
            match.getAwayScore(),
            match.getStatus()
        );
    }
}
