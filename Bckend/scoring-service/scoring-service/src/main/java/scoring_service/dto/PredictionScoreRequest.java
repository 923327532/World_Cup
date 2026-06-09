package scoring_service.dto;

import java.time.LocalDateTime;

public record PredictionScoreRequest(
    Integer predictedHomeScore,
    Integer predictedAwayScore,
    Integer actualHomeScore,
    Integer actualAwayScore,
    LocalDateTime predictedAt,
    LocalDateTime kickoffTime,
    Integer currentStreakBeforeMatch
) {}
