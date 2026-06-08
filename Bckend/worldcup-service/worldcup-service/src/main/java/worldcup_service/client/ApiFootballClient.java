package worldcup_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ApiFootballClient {

    private final WebClient webClient;

    public ApiFootballClient(
            @Value("${api.football.base-url}") String baseUrl,
            @Value("${api.football.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-rapidapi-key", apiKey)
                .defaultHeader("x-rapidapi-host", "v3.football.api-sports.io")
                .build();
    }

    public Mono<String> getMatches(Long leagueId, Integer season) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("league", leagueId)
                        .queryParam("season", season)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getMatchById(Long matchId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("id", matchId)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getTeams(Long leagueId, Integer season) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/teams")
                        .queryParam("league", leagueId)
                        .queryParam("season", season)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getPlayers(Long leagueId, Integer season, Long teamId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/players")
                        .queryParam("league", leagueId)
                        .queryParam("season", season)
                        .queryParam("team", teamId)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getLiveMatches(Long leagueId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("league", leagueId)
                        .queryParam("live", "all")
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}