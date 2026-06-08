package worldcup_service.repository;

import worldcup_service.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByApiTeamId(Long apiTeamId);
    Optional<Team> findByName(String name);
    List<Team> findByNameContainingIgnoreCase(String name);
}
