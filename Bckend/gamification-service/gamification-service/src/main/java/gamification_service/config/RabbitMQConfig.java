package gamification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SCORE_UPDATES_QUEUE = "score-updates";
    public static final String GAMIFICATION_EXCHANGE = "gamification.exchange";
    public static final String BADGE_UNLOCKED_ROUTING_KEY = "badge.unlocked";

    @Bean
    public Queue scoreUpdatesQueue() {
        return QueueBuilder.durable(SCORE_UPDATES_QUEUE).build();
    }

    @Bean
    public TopicExchange gamificationExchange() {
        return new TopicExchange(GAMIFICATION_EXCHANGE);
    }

    @Bean
    public Binding badgeUnlockedBinding() {
        return BindingBuilder.bind(scoreUpdatesQueue())
            .to(gamificationExchange())
            .with(BADGE_UNLOCKED_ROUTING_KEY);
    }
}
