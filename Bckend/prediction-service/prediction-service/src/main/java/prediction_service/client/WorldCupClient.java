package prediction_service.client;

import prediction_service.dto.MatchSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WorldCupClient {

    private final RestTemplate restTemplate;
    private final String worldCupServiceUrl;

    public WorldCupClient(
        RestTemplateBuilder restTemplateBuilder,
        @Value("${services.worldcup.url:http://localhost:8084}") String worldCupServiceUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.worldCupServiceUrl = worldCupServiceUrl;
    }

    public MatchSummary getMatch(Long matchId) {
        return restTemplate.getForObject(worldCupServiceUrl + "/matches/" + matchId, MatchSummary.class);
    }
}
