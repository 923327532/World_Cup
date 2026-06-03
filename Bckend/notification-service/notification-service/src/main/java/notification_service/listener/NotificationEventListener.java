package notification_service.listener;

import notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.notifications:notifications}")
    public void handleNotification(NotificationEvent event) {
        log.info("Creating notification for user: {}", event.userId());
        notificationService.createNotification(event.userId(), event.title(), event.message());
    }

    public record NotificationEvent(Long userId, String title, String message) {}
}