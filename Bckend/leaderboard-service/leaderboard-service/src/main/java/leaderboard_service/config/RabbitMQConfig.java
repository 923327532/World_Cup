package leaderboard_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String LEADERBOARD_UPDATED_QUEUE = "leaderboard.updated";

    @Bean
    public Queue leaderboardUpdatedQueue() {
        return QueueBuilder.durable(LEADERBOARD_UPDATED_QUEUE).build();
    }
}
