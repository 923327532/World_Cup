package notification_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.notifications:notifications}")
    private String notificationsQueue;

    @Bean
    public Queue notificationsQueue() {
        return new Queue(notificationsQueue, true);
    }
}
