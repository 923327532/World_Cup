package prediction_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PREDICTION_CREATED_QUEUE = "prediction.created";
    public static final String PREDICTION_EXCHANGE = "prediction.exchange";
    public static final String PREDICTION_CREATED_ROUTING_KEY = "prediction.created";

    @Bean
    public Queue predictionCreatedQueue() {
        return QueueBuilder.durable(PREDICTION_CREATED_QUEUE).build();
    }

    @Bean
    public TopicExchange predictionExchange() {
        return new TopicExchange(PREDICTION_EXCHANGE);
    }

    @Bean
    public Binding predictionCreatedBinding() {
        return BindingBuilder.bind(predictionCreatedQueue())
            .to(predictionExchange())
            .with(PREDICTION_CREATED_ROUTING_KEY);
    }
}
