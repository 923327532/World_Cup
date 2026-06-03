package scoring_service.dto;

public record UserScoreDTO(
    Long userId,
    Long totalPoints
) {}