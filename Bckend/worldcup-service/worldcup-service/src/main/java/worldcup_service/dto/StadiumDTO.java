package worldcup_service.dto;

public record StadiumDTO(
    Long id,
    Long apiStadiumId,
    String name,
    String fifaName,
    String city,
    String country,
    Integer capacity
) {}
