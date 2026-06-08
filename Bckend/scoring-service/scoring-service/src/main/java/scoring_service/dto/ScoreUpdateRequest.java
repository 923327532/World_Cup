package scoring_service.dto;

public record ScoreUpdateRequest(
    Long userId,
    Integer points,
    String reason,
    Long matchId,
    Long predictionId
) {}
