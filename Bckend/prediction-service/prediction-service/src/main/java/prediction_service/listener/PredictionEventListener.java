package prediction_service.listener;

import prediction_service.entity.Prediction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PredictionEventListener {

    @RabbitListener(queues = "prediction.created")
    public void handlePredictionCreated(Prediction prediction) {
        log.info("Prediction created: {}", prediction.getId());
        // Handle prediction created event
    }
}