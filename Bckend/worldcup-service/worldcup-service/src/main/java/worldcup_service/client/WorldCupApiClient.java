package worldcup_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WorldCupApiClient {

    private final WebClient webClient;

    public WorldCupApiClient(@Value("${api.worldcup.base-url:https://worldcup26.ir}") String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Accept", "application/json")
            .build();
    }

    public Mono<String> getGames() {
        return webClient.get()
            .uri("/get/games")
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<String> getGameById(Long matchId) {
        return webClient.get()
            .uri("/get/game/{matchId}", matchId)
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<String> getTeams() {
        return webClient.get()
            .uri("/get/teams")
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<String> getStadiums() {
        return webClient.get()
            .uri("/get/stadiums")
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<String> getTeamById(Long teamId) {
        return webClient.get()
            .uri("/get/team/{teamId}", teamId)
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<String> getStadiumById(Long stadiumId) {
        return webClient.get()
            .uri("/get/stadium/{stadiumId}", stadiumId)
            .retrieve()
            .bodyToMono(String.class);
    }
}
