package prediction_service.repository;

import prediction_service.entity.UserPredictionLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PredictionLockRepository extends JpaRepository<UserPredictionLock, Long> {
    Optional<UserPredictionLock> findByPredictionId(Long predictionId);
}