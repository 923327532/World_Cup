package scoring_service.repository;

import scoring_service.entity.ScoreHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScoreHistoryRepository extends JpaRepository<ScoreHistory, Long> {
    List<ScoreHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT sh FROM ScoreHistory sh WHERE sh.userId = ?1 AND sh.createdAt >= ?2 AND sh.reason LIKE '%correct%'")
    List<ScoreHistory> findRecentCorrectPredictions(Long userId, LocalDateTime since);
}