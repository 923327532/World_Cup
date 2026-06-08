package worldcup_service.service;

import worldcup_service.client.ApiFootballClient;
import worldcup_service.entity.Player;
import worldcup_service.entity.Team;
import worldcup_service.repository.PlayerRepository;
import worldcup_service.repository.TeamRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiFootballService {

    private final ApiFootballClient apiFootballClient;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final ObjectMapper objectMapper;

    @Value("${api.football.league-id:1}")
    private Long leagueId;

    @Value("${api.football.season:2026}")
    private Integer season;

    public void syncTeams() {
        log.info("Syncing teams from API-Football for league={}, season={}", leagueId, season);
        apiFootballClient.getTeams(leagueId, season)
            .doOnError(e -> log.error("Failed to fetch teams: {}", e.getMessage()))
            .subscribe(response -> {
                try {
                    JsonNode root = objectMapper.readTree(response);
                    JsonNode teamsNode = root.get("response");
                    if (teamsNode == null || !teamsNode.isArray()) {
                        log.warn("No teams in API response");
                        return;
                    }
                    int saved = 0;
                    for (JsonNode node : teamsNode) {
                        JsonNode teamNode = node.get("team");
                        if (teamNode == null) continue;
                        Long apiTeamId = teamNode.get("id").asLong();
                        if (teamRepository.findByApiTeamId(apiTeamId).isPresent()) continue;

                        Team team = new Team();
                        team.setApiTeamId(apiTeamId);
                        team.setName(teamNode.get("name").asText());
                        team.setCode(teamNode.path("code").asText(null));
                        team.setFlagUrl(teamNode.path("logo").asText(null));
                        teamRepository.save(team);
                        saved++;
                    }
                    log.info("Teams sync complete. Saved {} new teams", saved);
                } catch (Exception e) {
                    log.error("Error processing teams response", e);
                }
            });
    }

    public void syncPlayers(Long apiTeamId) {
        log.info("Syncing players for apiTeamId={}", apiTeamId);
        apiFootballClient.getPlayers(leagueId, season, apiTeamId)
            .doOnError(e -> log.error("Failed to fetch players: {}", e.getMessage()))
            .subscribe(response -> {
                try {
                    JsonNode root = objectMapper.readTree(response);
                    JsonNode playersNode = root.get("response");
                    if (playersNode == null || !playersNode.isArray()) return;

                    Team team = teamRepository.findByApiTeamId(apiTeamId).orElse(null);
                    if (team == null) {
                        log.warn("Team with apiTeamId={} not found, skipping players sync", apiTeamId);
                        return;
                    }

                    int saved = 0;
                    for (JsonNode node : playersNode) {
                        JsonNode playerNode = node.get("player");
                        if (playerNode == null) continue;
                        Long apiPlayerId = playerNode.get("id").asLong();
                        if (playerRepository.findByApiPlayerId(apiPlayerId).isPresent()) continue;

                        Player player = new Player();
                        player.setApiPlayerId(apiPlayerId);
                        player.setTeam(team);
                        player.setFullName(playerNode.path("name").asText(null));
                        player.setPosition(playerNode.path("position").asText(null));
                        player.setNumber(playerNode.path("number").isNull() ? null : playerNode.path("number").asInt());
                        playerRepository.save(player);
                        saved++;
                    }
                    log.info("Players sync complete for team {}. Saved {} new players", apiTeamId, saved);
                } catch (Exception e) {
                    log.error("Error processing players response", e);
                }
            });
    }
}
