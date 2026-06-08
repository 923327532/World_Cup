package worldcup_service.repository;

import worldcup_service.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findByYear(Integer year);
    Optional<Tournament> findTopByOrderByYearDesc();
}
