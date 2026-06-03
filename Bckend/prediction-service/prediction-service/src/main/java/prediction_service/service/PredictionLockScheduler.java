package prediction_service.service;

import prediction_service.entity.Prediction;
import prediction_service.entity.UserPredictionLock;
import prediction_service.repository.PredictionLockRepository;
import prediction_service.repository.PredictionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionLockScheduler {

    private final PredictionRepository predictionRepository;
    private final PredictionLockRepository predictionLockRepository;

    @Scheduled(fixedRate = 60000) // Every minute
    public void lockPredictions() {
        LocalDateTime lockTime = LocalDateTime.now().plusMinutes(5);

        // Find predictions for matches starting within 5 minutes
        List<Prediction> predictions = predictionRepository.findAll();

        predictions.forEach(prediction -> {
            if (!isPredictionLocked(prediction.getId())) {
                UserPredictionLock lock = new UserPredictionLock();
                lock.setPrediction(prediction);
                lock.setLockedAt(LocalDateTime.now());
                lock.setReason("Match starting in 5 minutes");

                predictionLockRepository.save(lock);
                log.info("Locked prediction {} for match {}", prediction.getId(), prediction.getMatchId());
            }
        });
    }

    private boolean isPredictionLocked(Long predictionId) {
        return predictionLockRepository.findByPredictionId(predictionId).isPresent();
    }
}