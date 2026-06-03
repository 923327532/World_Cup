package scoring_service.repository;

import scoring_service.entity.UserScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, Long> {
    List<UserScore> findAllByOrderByTotalPointsDesc();
}