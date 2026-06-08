package worldcup_service.dto;

public record PlayerDTO(
    Long id,
    Long apiPlayerId,
    Long teamId,
    String teamName,
    String fullName,
    String position,
    Integer number
) {}
