package scoring_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PREDICTION_SCORED_QUEUE = "prediction.scored";
    public static final String SCORING_EXCHANGE = "scoring.exchange";
    public static final String SCORE_UPDATES_QUEUE = "score-updates";
    public static final String SCORE_UPDATED_ROUTING_KEY = "score.updated";
    public static final String LEADERBOARD_UPDATED_QUEUE = "leaderboard.updated";
    public static final String MATCH_FINISHED_QUEUE = "match.finished";

    @Bean
    public Queue predictionScoredQueue() {
        return QueueBuilder.durable(PREDICTION_SCORED_QUEUE).build();
    }

    @Bean
    public Queue matchFinishedQueue() {
        return QueueBuilder.durable(MATCH_FINISHED_QUEUE).build();
    }

    @Bean
    public Queue leaderboardUpdatedQueue() {
        return QueueBuilder.durable(LEADERBOARD_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue scoreUpdatesQueue() {
        return QueueBuilder.durable(SCORE_UPDATES_QUEUE).build();
    }

    @Bean
    public TopicExchange scoringExchange() {
        return new TopicExchange(SCORING_EXCHANGE);
    }

    @Bean
    public Binding scoreUpdatedBinding() {
        return BindingBuilder.bind(leaderboardUpdatedQueue())
            .to(scoringExchange())
            .with(SCORE_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding scoreUpdatesBinding() {
        return BindingBuilder.bind(scoreUpdatesQueue())
            .to(scoringExchange())
            .with(SCORE_UPDATED_ROUTING_KEY);
    }
}
