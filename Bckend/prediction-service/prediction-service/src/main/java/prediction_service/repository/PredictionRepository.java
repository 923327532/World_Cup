package prediction_service.repository;

import prediction_service.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByUserId(Long userId);
    List<Prediction> findByMatchId(Long matchId);
    List<Prediction> findByRoomId(Long roomId);
    Optional<Prediction> findByUserIdAndRoomIdAndMatchId(Long userId, Long roomId, Long matchId);
    boolean existsByUserIdAndRoomIdAndMatchId(Long userId, Long roomId, Long matchId);
}
