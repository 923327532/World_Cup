package gamification_service.dto;

import java.time.LocalDateTime;

public record UserBadgeDTO(
    Long id,
    Long userId,
    BadgeDTO badge,
    LocalDateTime earnedAt
) {}