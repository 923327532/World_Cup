package prediction_service.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePredictionRequest(
    @NotBlank String predictedValue
) {}
