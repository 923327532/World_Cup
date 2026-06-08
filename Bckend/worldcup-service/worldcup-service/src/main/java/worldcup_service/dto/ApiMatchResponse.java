package worldcup_service.dto;

import java.time.LocalDateTime;

public record ApiMatchResponse(
    Long fixtureId,
    String date,
    String venueName,
    String statusShort,
    String statusLong,
    String round,
    Long homeTeamApiId,
    String homeTeamName,
    String homeTeamLogo,
    Long awayTeamApiId,
    String awayTeamName,
    String awayTeamLogo,
    Integer homeScore,
    Integer awayScore
) {}
