package social_service.websocket;

import social_service.dto.CommentDTO;
import social_service.entity.Comment;
import social_service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatHandler {

    private final CommentService commentService;

    @MessageMapping("/chat/{matchId}")
    @SendTo("/topic/chat/{matchId}")
    public CommentDTO sendMessage(@DestinationVariable Long matchId, CommentDTO comment) {
        Comment saved = commentService.createComment(comment.userId(), matchId, comment.content());
        return commentService.toDTO(saved);
    }
}