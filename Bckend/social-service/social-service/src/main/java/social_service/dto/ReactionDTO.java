package social_service.dto;

public record ReactionDTO(
    Long id,
    Long userId,
    Long commentId,
    String reaction
) {}