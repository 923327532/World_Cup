package prediction_service.dto;

public record PredictionTypeDTO(
    Long id,
    String code,
    String name,
    Integer points
) {}