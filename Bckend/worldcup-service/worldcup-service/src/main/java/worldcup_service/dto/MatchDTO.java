package worldcup_service.dto;

import java.time.LocalDateTime;

public record MatchDTO(
    Long id,
    Long stadiumId,
    String stadiumName,
    String stadiumCity,
    String stadiumCountry,
    Integer stadiumCapacity,
    String venue,
    String stage,
    String groupName,
    Long homeTeamId,
    String homeTeam,
    Long awayTeamId,
    String awayTeam,
    LocalDateTime kickoffTime,
    Integer homeScore,
    Integer awayScore,
    String status
) {}
