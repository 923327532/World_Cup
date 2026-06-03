package leaderboard_service.repository;

import leaderboard_service.entity.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, Long> {
    List<LeaderboardEntry> findByLeaderboardIdOrderByPointsDesc(Long leaderboardId);
    Optional<LeaderboardEntry> findByLeaderboardIdAndUserId(Long leaderboardId, Long userId);
}