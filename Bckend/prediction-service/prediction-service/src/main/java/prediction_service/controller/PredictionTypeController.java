package prediction_service.controller;

import prediction_service.dto.PredictionTypeDTO;
import prediction_service.service.PredictionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prediction-types")
@RequiredArgsConstructor
public class PredictionTypeController {

    private final PredictionTypeService predictionTypeService;

    @GetMapping
    public ResponseEntity<List<PredictionTypeDTO>> getAllTypes() {
        return ResponseEntity.ok(predictionTypeService.getAllTypes());
    }
}