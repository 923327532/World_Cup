package worldcup_service.repository;

import worldcup_service.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByKickoffTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Match> findByStatus(String status);
    Optional<Match> findByApiMatchId(Long apiMatchId);
}
