package worldcup_service.service;

import worldcup_service.client.ApiFootballClient;
import worldcup_service.entity.Match;
import worldcup_service.repository.MatchRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchSyncScheduler {

    private final ApiFootballClient apiFootballClient;
    private final MatchRepository matchRepository;
    private final ObjectMapper objectMapper;

    @Value("${api.football.league-id:1}")
    private Long leagueId;

    @Value("${api.football.season:2024}")
    private Integer season;

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void syncMatches() {
        log.info("Starting match synchronization for league={}, season={}...", leagueId, season);

        try {
            apiFootballClient.getMatches(leagueId, season)
                .doOnError(error -> log.error("API call failed: {}", error.getMessage()))
                .subscribe(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode matches = root.get("response");

                        if (matches != null && matches.isArray()) {
                            log.info("Received {} matches from API", matches.size());
                            int saved = 0;

                            for (JsonNode matchNode : matches) {
                                try {
                                    Long apiMatchId = matchNode.get("fixture").get("id").asLong();

                                    // Skip if already exists
                                    if (matchRepository.findByApiMatchId(apiMatchId).isPresent()) {
                                        continue;
                                    }

                                    Match match = new Match();
                                    match.setApiMatchId(apiMatchId);
                                    match.setKickoffTime(parseDateTime(
                                        matchNode.get("fixture").get("date").asText()
                                    ));
                                    match.setStatus(matchNode.get("fixture").get("status").get("short").asText());
                                    match.setVenue(matchNode.get("fixture").get("venue").get("name").asText(""));

                                    // Teams
                                    String homeTeamName = matchNode.get("teams").get("home").get("name").asText();
                                    String awayTeamName = matchNode.get("teams").get("away").get("name").asText();

                                    // Score
                                    JsonNode score = matchNode.get("score").get("fulltime");
                                    if (score != null && !score.isNull()) {
                                        match.setHomeScore(score.get("home").asInt(0));
                                        match.setAwayScore(score.get("away").asInt(0));
                                    }

                                    matchRepository.save(match);
                                    saved++;
                                    log.info("Saved match {}: {} vs {}", apiMatchId, homeTeamName, awayTeamName);
                                } catch (Exception e) {
                                    log.error("Error processing individual match", e);
                                }
                            }

                            log.info("Match synchronization completed. Saved {} new matches", saved);
                        } else {
                            log.warn("No matches found in API response");
                        }
                    } catch (Exception e) {
                        log.error("Error processing match data", e);
                    }
                });
        } catch (Exception e) {
            log.error("Error during match synchronization", e);
        }
    }

    private LocalDateTime parseDateTime(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("Could not parse date: {}", dateStr);
            return null;
        }
    }
}
