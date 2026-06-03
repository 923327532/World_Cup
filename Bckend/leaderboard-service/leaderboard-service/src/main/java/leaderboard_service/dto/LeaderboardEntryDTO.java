package leaderboard_service.dto;

public record LeaderboardEntryDTO(
    Long id,
    Long userId,
    Integer rankingPosition,
    Long points
) {}