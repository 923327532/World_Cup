package worldcup_service.dto;

import java.time.LocalDateTime;

public record MatchDTO(
    Long id,
    String homeTeam,
    String awayTeam,
    LocalDateTime kickoffTime,
    Integer homeScore,
    Integer awayScore,
    String status
) {}