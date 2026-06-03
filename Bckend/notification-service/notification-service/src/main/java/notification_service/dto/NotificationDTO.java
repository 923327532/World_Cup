package notification_service.dto;

import java.time.LocalDateTime;

public record NotificationDTO(
    Long id,
    Long userId,
    String title,
    String message,
    Boolean isRead,
    LocalDateTime createdAt
) {}