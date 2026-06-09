package scoring_service.dto;

public record RoomRankingEntryDTO(
    Long roomId,
    Long userId,
    Integer rankingPosition,
    Long totalPoints
) {}
