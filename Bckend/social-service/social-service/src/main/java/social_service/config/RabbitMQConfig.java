package social_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.match-events:match-events}")
    private String matchEventsQueue;

    @Bean
    public Queue matchEventsQueue() {
        return new Queue(matchEventsQueue, true);
    }
}
