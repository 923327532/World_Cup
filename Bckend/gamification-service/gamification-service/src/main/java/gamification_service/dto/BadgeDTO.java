package gamification_service.dto;

public record BadgeDTO(
    Long id,
    String name,
    String description,
    String icon
) {}