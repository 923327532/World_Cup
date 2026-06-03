package scoring_service.dto;

import java.time.LocalDateTime;

public record ScoreHistoryDTO(
    Long id,
    Long userId,
    Integer points,
    String reason,
    LocalDateTime createdAt
) {}