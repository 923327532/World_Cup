package admin_service.repository;

import admin_service.entity.ManualMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ManualMatchRepository extends JpaRepository<ManualMatch, Long> {
    List<ManualMatch> findByStatus(ManualMatch.MatchStatus status);
    List<ManualMatch> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<ManualMatch> findByGroupName(String groupName);
    List<ManualMatch> findByStage(String stage);
}
