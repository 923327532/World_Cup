package prediction_service.repository;

import prediction_service.entity.PredictionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictionTypeRepository extends JpaRepository<PredictionType, Long> {
}