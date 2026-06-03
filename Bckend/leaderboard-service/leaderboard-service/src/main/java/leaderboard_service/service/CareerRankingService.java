package leaderboard_service.service;

import leaderboard_service.dto.LeaderboardEntryDTO;
import leaderboard_service.entity.Leaderboard;
import leaderboard_service.entity.LeaderboardEntry;
import leaderboard_service.repository.LeaderboardEntryRepository;
import leaderboard_service.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerRankingService {

    private final LeaderboardRepository leaderboardRepository;
    private final LeaderboardEntryRepository entryRepository;

    public List<LeaderboardEntryDTO> getCareerRanking(Long careerId, int limit) {
        Leaderboard careerLeaderboard = leaderboardRepository
            .findByTypeAndName("CAREER", "Career " + careerId)
            .orElseThrow(() -> new RuntimeException("Career leaderboard not found"));

        List<LeaderboardEntry> entries = entryRepository
            .findByLeaderboardIdOrderByPointsDesc(careerLeaderboard.getId())
            .stream()
            .limit(limit)
            .toList();

        return entries.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
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