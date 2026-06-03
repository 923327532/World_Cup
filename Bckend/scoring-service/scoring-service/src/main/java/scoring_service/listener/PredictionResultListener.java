package scoring_service.listener;

import scoring_service.service.ScoreCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PredictionResultListener {

    private final ScoreCalculatorService scoreCalculatorService;

    @RabbitListener(queues = "prediction.scored")
    public void handlePredictionScored(Object event) {
        log.info("Processing prediction scored event: {}", event);
        // Handle prediction scored event
    }
}