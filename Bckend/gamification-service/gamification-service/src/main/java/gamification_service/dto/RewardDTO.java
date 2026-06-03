package gamification_service.dto;

public record RewardDTO(
    Long id,
    Integer position,
    String title,
    String description
) {}