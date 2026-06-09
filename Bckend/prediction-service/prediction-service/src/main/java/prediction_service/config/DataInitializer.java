package prediction_service.config;

import prediction_service.entity.PredictionType;
import prediction_service.repository.PredictionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PredictionTypeRepository predictionTypeRepository;

    @Override
    public void run(String... args) {
        if (predictionTypeRepository.count() == 0) {
            predictionTypeRepository.save(new PredictionType(null, "WINNER", "Ganador del partido", 3));
            predictionTypeRepository.save(new PredictionType(null, "DRAW", "Empate", 3));
            predictionTypeRepository.save(new PredictionType(null, "EXACT_SCORE", "Marcador exacto", 5));
            predictionTypeRepository.save(new PredictionType(null, "FIRST_GOAL_SCORER", "Primer goleador", 30));
            predictionTypeRepository.save(new PredictionType(null, "BOTH_SCORE", "Ambos anotan", 10));
            predictionTypeRepository.save(new PredictionType(null, "OVER_2_5", "Más de 2.5 goles", 10));
            predictionTypeRepository.save(new PredictionType(null, "UNDER_2_5", "Menos de 2.5 goles", 10));
        }
    }
}
