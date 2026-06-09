package scoring_service.dto;

import java.time.LocalDateTime;

public record ScoreHistoryDTO(
    Long id,
    Long userId,
    Long roomId,
    Long matchId,
    Long predictionId,
    Integer points,
    Integer basePoints,
    Integer streakBonus,
    Integer earlyBonus,
    String reason,
    LocalDateTime createdAt
) {}
