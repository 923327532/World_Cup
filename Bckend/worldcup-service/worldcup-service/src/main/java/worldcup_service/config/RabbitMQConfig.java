package worldcup_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue matchGoalQueue() {
        return QueueBuilder.durable("match.goal").build();
    }

    @Bean
    public Queue matchFinishedQueue() {
        return QueueBuilder.durable("match.finished").build();
    }

    @Bean
    public TopicExchange matchExchange() {
        return new TopicExchange("match.exchange");
    }

    @Bean
    public Binding matchGoalBinding() {
        return BindingBuilder.bind(matchGoalQueue())
            .to(matchExchange())
            .with("match.goal");
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter());
        return template;
    }
}