package social_service.dto;

import java.time.LocalDateTime;

public record CommentDTO(
    Long id,
    Long userId,
    Long matchId,
    String content,
    LocalDateTime createdAt
) {}