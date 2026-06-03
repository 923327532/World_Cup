package social_service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MatchEventListener {

    @RabbitListener(queues = "${rabbitmq.queue.match-events:match-events}")
    public void handleMatchEvent(String message) {
        log.info("Received match event: {}", message);
        // Process match events (e.g., goal scored, match started/ended)
        // This can be used to broadcast notifications via WebSocket
    }
}