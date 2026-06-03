package scoring_service.listener;

import scoring_service.service.ScoreCalculatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchFinishedListener {

    private final ScoreCalculatorService scoreCalculatorService;
    private final ObjectMapper objectMapper;

    /**
     * Escucha eventos de partidos finalizados publicados por admin-service.
     * Cuando un partido termina, se gatilla el recalculo de puntos
     * para todas las predicciones asociadas a ese partido.
     */
    @RabbitListener(queues = "match.finished")
    public void handleMatchFinished(Object event) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.convertValue(event, Map.class);
            Long matchId = data.get("matchId") != null ? Long.valueOf(data.get("matchId").toString()) : null;
            String homeTeam = (String) data.get("homeTeam");
            String awayTeam = (String) data.get("awayTeam");
            Integer homeScore = data.get("homeScore") != null ? Integer.valueOf(data.get("homeScore").toString()) : null;
            Integer awayScore = data.get("awayScore") != null ? Integer.valueOf(data.get("awayScore").toString()) : null;
            String winner = (String) data.get("winner");
            String sourceType = (String) data.get("sourceType");

            log.info("Match finished event received: {} {} vs {} ({} - {}), winner={}, source={}",
                    matchId, homeTeam, awayTeam, homeScore, awayScore, winner, sourceType);

            if (matchId != null && homeScore != null && awayScore != null) {
                scoreCalculatorService.calculateAndUpdateScores(matchId, homeScore, awayScore, winner);
                log.info("Scores updated for match {} after finish event", matchId);
            } else {
                log.warn("Incomplete match finished event data: {}", data);
            }
        } catch (Exception e) {
            log.error("Error processing match.finished event: {}", e.getMessage(), e);
        }
    }
}
