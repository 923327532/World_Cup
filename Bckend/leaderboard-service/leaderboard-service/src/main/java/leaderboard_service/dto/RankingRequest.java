package leaderboard_service.dto;

public record RankingRequest(
    String type,
    String filter,
    Integer limit
) {}
