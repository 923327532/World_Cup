package social_service.dto;

import lombok.Data;

@Data
public class CreateReactionRequest {
    private Long commentId;
    private String reaction;
}