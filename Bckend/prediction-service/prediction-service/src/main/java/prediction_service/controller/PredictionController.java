package prediction_service.controller;

import prediction_service.dto.CreatePredictionRequest;
import prediction_service.dto.PredictionDTO;
import prediction_service.dto.UpdatePredictionRequest;
import prediction_service.service.PredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping
    public ResponseEntity<PredictionDTO> createPrediction(
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody CreatePredictionRequest request
    ) {
        return ResponseEntity.ok(predictionService.createPrediction(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PredictionDTO> updatePrediction(
        @RequestHeader("X-User-Id") Long userId,
        @PathVariable Long id,
        @Valid @RequestBody UpdatePredictionRequest request
    ) {
        return ResponseEntity.ok(predictionService.updatePrediction(userId, id, request.predictionValue()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PredictionDTO>> getUserPredictions(@PathVariable Long userId) {
        return ResponseEntity.ok(predictionService.getUserPredictions(userId));
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<PredictionDTO>> getMatchPredictions(@PathVariable Long matchId) {
        return ResponseEntity.ok(predictionService.getMatchPredictions(matchId));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<PredictionDTO>> getRoomPredictions(@PathVariable Long roomId) {
        return ResponseEntity.ok(predictionService.getRoomPredictions(roomId));
    }
}
