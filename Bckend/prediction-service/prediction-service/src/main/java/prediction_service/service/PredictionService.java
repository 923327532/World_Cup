package prediction_service.service;

import prediction_service.client.WorldCupClient;
import prediction_service.dto.CreatePredictionRequest;
import prediction_service.dto.MatchSummary;
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
    private final WorldCupClient worldCupClient;

    public PredictionDTO createPrediction(Long userId, CreatePredictionRequest request) {
        MatchSummary match = requireOpenMatch(request.getMatchId());

        if (predictionRepository.existsByUserIdAndRoomIdAndMatchId(userId, request.getRoomId(), request.getMatchId())) {
            throw new RuntimeException("User already has a prediction for this match in this room");
        }

        PredictionType predictionType = predictionTypeRepository.findById(request.getPredictionTypeId())
            .orElseThrow(() -> new RuntimeException("Prediction type not found"));

        Prediction prediction = new Prediction();
        prediction.setUserId(userId);
        prediction.setMatchId(request.getMatchId());
        prediction.setRoomId(request.getRoomId());
        prediction.setPredictionType(predictionType);
        prediction.setPredictionValue(request.getPredictionValue());
        prediction.setCreatedAt(LocalDateTime.now());
        prediction.setUpdatedAt(LocalDateTime.now());

        prediction = predictionRepository.save(prediction);

        // Publish event
        rabbitTemplate.convertAndSend("prediction.exchange", "prediction.created", prediction);

        return toDTO(prediction, match);
    }

    public PredictionDTO updatePrediction(Long userId, Long predictionId, String newValue) {
        Prediction prediction = predictionRepository.findById(predictionId)
            .orElseThrow(() -> new RuntimeException("Prediction not found"));

        if (!prediction.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        MatchSummary match = getMatch(prediction.getMatchId());
        if (isPredictionLocked(match)) {
            throw new RuntimeException("Prediction is locked");
        }

        prediction.setPredictionValue(newValue);
        prediction.setUpdatedAt(LocalDateTime.now());

        prediction = predictionRepository.save(prediction);

        return toDTO(prediction, match);
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

    public List<PredictionDTO> getRoomPredictions(Long roomId) {
        return predictionRepository.findByRoomId(roomId).stream()
            .map(this::toDTO)
            .toList();
    }

    private MatchSummary requireOpenMatch(Long matchId) {
        MatchSummary match = getMatch(matchId);
        if (isPredictionLocked(match)) {
            throw new RuntimeException("Prediction is locked");
        }
        return match;
    }

    private MatchSummary getMatch(Long matchId) {
        MatchSummary match = worldCupClient.getMatch(matchId);
        if (match == null) {
            throw new RuntimeException("Match not found");
        }
        return match;
    }

    private boolean isPredictionLocked(MatchSummary match) {
        return match.kickoffTime() != null && !LocalDateTime.now().isBefore(match.kickoffTime());
    }

    private PredictionDTO toDTO(Prediction prediction) {
        MatchSummary match = null;
        try {
            match = getMatch(prediction.getMatchId());
        } catch (RuntimeException ignored) {
            // Keep prediction history available even if match-service is temporarily unavailable.
        }
        return toDTO(prediction, match);
    }

    private PredictionDTO toDTO(Prediction prediction, MatchSummary match) {
        return new PredictionDTO(
            prediction.getId(),
            prediction.getUserId(),
            prediction.getMatchId(),
            prediction.getRoomId(),
            match != null ? match.homeTeam() : null,
            match != null ? match.awayTeam() : null,
            prediction.getPredictionType().getId(),
            prediction.getPredictionType().getName(),
            prediction.getPredictionValue(),
            prediction.getPredictionType().getPoints(),
            prediction.getCreatedAt(),
            prediction.getUpdatedAt(),
            match != null && isPredictionLocked(match)
        );
    }
}
