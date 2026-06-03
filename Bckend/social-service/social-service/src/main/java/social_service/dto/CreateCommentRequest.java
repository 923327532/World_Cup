package social_service.dto;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private Long matchId;
    private String content;
}