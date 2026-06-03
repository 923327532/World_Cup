package prediction_service.service;

import prediction_service.dto.CreatePredictionRequest;
import prediction_service.dto.PredictionDTO;
import prediction_service.entity.Prediction;
import prediction_service.entity.PredictionType;
import prediction_service.repository.PredictionRepository;
import prediction_service.repository.PredictionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final PredictionTypeRepository predictionTypeRepository;
    private final RabbitTemplate rabbitTemplate;

    public PredictionDTO createPrediction(Long userId, CreatePredictionRequest request) {
        PredictionType predictionType = predictionTypeRepository.findById(request.getPredictionTypeId())
            .orElseThrow(() -> new RuntimeException("Prediction type not found"));

        Prediction prediction = new Prediction();
        prediction.setUserId(userId);
        prediction.setMatchId(request.getMatchId());
        prediction.setPredictionType(predictionType);
        prediction.setPredictionValue(request.getPredictionValue());
        prediction.setCreatedAt(LocalDateTime.now());
        prediction.setUpdatedAt(LocalDateTime.now());

        prediction = predictionRepository.save(prediction);

        // Publish event
        rabbitTemplate.convertAndSend("prediction.exchange", "prediction.created", prediction);

        return toDTO(prediction);
    }

    public PredictionDTO updatePrediction(Long userId, Long predictionId, String newValue) {
        Prediction prediction = predictionRepository.findById(predictionId)
            .orElseThrow(() -> new RuntimeException("Prediction not found"));

        if (!prediction.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        // Check if locked
        if (isPredictionLocked(predictionId)) {
            throw new RuntimeException("Prediction is locked");
        }

        prediction.setPredictionValue(newValue);
        prediction.setUpdatedAt(LocalDateTime.now());

        prediction = predictionRepository.save(prediction);

        return toDTO(prediction);
    }

    public List<PredictionDTO> getUserPredictions(Long userId) {
        return predictionRepository.findByUserId(userId).stream()
            .map(this::toDTO)
            .toList();
    }

    public List<PredictionDTO> getMatchPredictions(Long matchId) {
        return predictionRepository.findByMatchId(matchId).stream()
            .map(this::toDTO)
            .toList();
    }

    private boolean isPredictionLocked(Long predictionId) {
        return false; // Implement lock logic
    }

    private PredictionDTO toDTO(Prediction prediction) {
        return new PredictionDTO(
            prediction.getId(),
            prediction.getUserId(),
            prediction.getMatchId(),
            null, // homeTeam - would need to fetch from worldcup-service
            null, // awayTeam - would need to fetch from worldcup-service
            prediction.getPredictionType().getId(),
            prediction.getPredictionType().getName(),
            prediction.getPredictionValue(),
            prediction.getPredictionType().getPoints(),
            prediction.getCreatedAt(),
            prediction.getUpdatedAt(),
            false
        );
    }
}