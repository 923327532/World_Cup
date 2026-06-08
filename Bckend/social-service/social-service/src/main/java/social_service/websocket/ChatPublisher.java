package social_service.websocket;

import social_service.dto.CommentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishComment(Long matchId, CommentDTO comment) {
        String destination = "/topic/chat/" + matchId;
        messagingTemplate.convertAndSend(destination, comment);
        log.debug("Published comment to {}", destination);
    }

    public void publishEvent(Long matchId, String eventMessage) {
        String destination = "/topic/events/" + matchId;
        messagingTemplate.convertAndSend(destination, eventMessage);
        log.debug("Published event to {}", destination);
    }
}
