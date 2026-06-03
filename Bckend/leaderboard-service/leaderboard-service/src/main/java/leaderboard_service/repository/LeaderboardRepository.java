package leaderboard_service.repository;

import leaderboard_service.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    Optional<Leaderboard> findByType(String type);
    Optional<Leaderboard> findByTypeAndName(String type, String name);
}