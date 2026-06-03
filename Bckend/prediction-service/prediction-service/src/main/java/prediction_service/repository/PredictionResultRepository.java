package prediction_service.repository;

import prediction_service.entity.PredictionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictionResultRepository extends JpaRepository<PredictionResult, Long> {
}