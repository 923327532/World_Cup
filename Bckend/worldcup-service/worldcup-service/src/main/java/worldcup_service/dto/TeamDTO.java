package worldcup_service.dto;

public record TeamDTO(
    Long id,
    Long apiTeamId,
    String name,
    String code,
    String flagUrl
) {}
