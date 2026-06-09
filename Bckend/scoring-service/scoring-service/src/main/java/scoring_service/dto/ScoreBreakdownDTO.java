package scoring_service.dto;

public record ScoreBreakdownDTO(
    Integer basePoints,
    Integer streakBonus,
    Integer earlyBonus,
    Integer totalPoints,
    String ruleApplied,
    Boolean correctForStreak
) {}
