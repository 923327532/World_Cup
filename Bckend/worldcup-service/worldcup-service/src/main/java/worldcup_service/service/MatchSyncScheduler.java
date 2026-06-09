package worldcup_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import worldcup_service.client.WorldCupApiClient;
import worldcup_service.entity.Group;
import worldcup_service.entity.Match;
import worldcup_service.entity.Stage;
import worldcup_service.entity.Stadium;
import worldcup_service.entity.Team;
import worldcup_service.entity.Tournament;
import worldcup_service.repository.GroupRepository;
import worldcup_service.repository.MatchRepository;
import worldcup_service.repository.StageRepository;
import worldcup_service.repository.StadiumRepository;
import worldcup_service.repository.TeamRepository;
import worldcup_service.repository.TournamentRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchSyncScheduler {

    private static final Map<String, String> STAGE_NAMES = Map.of(
        "group", "Group Stage",
        "r32", "Round of 32",
        "r16", "Round of 16",
        "qf", "Quarter-finals",
        "sf", "Semi-finals",
        "third", "Third Place",
        "final", "Final"
    );

    private final WorldCupApiClient worldCupApiClient;
    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;
    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final StageRepository stageRepository;
    private final GroupRepository groupRepository;
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartup() {
        syncWorldCupData();
    }

    @Scheduled(fixedRate = 300000)
    public void syncWorldCupData() {
        log.info("Starting World Cup 2026 sync...");
        try {
            syncTeams();
            syncStadiums();
            syncMatches();
            log.info("World Cup 2026 sync completed.");
        } catch (Exception e) {
            log.error("Error during World Cup sync", e);
        }
    }

    private void syncTeams() {
        String response = worldCupApiClient.getTeams().block();
        if (response == null || response.isBlank()) {
            log.warn("Empty teams response from World Cup API");
            return;
        }

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode teams = root.path("response");
            if (!teams.isArray()) {
                log.warn("No teams array found in World Cup API response");
                return;
            }

            int saved = 0;
            for (JsonNode node : teams) {
                Long apiTeamId = node.path("id").asLong();
                String name = text(node, "name_en", "name");
                String code = text(node, "fifa_code", "code");
                String flag = text(node, "flag", "logo");

                Team team = teamRepository.findByApiTeamId(apiTeamId)
                    .or(() -> Optional.ofNullable(code).flatMap(teamRepository::findByCode))
                    .or(() -> Optional.ofNullable(name).flatMap(teamRepository::findByName))
                    .orElseGet(Team::new);

                team.setApiTeamId(apiTeamId);
                team.setName(name);
                team.setCode(code);
                team.setFlagUrl(flag);
                teamRepository.save(team);
                saved++;
            }

            log.info("Saved/updated {} teams", saved);
        } catch (Exception e) {
            log.error("Error syncing teams", e);
        }
    }

    private void syncStadiums() {
        String response = worldCupApiClient.getStadiums().block();
        if (response == null || response.isBlank()) {
            log.warn("Empty stadiums response from World Cup API");
            return;
        }

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode stadiums = root.path("response");
            if (!stadiums.isArray()) {
                log.warn("No stadiums array found in World Cup API response");
                return;
            }

            int saved = 0;
            for (JsonNode node : stadiums) {
                Long apiStadiumId = node.path("id").asLong();
                Stadium stadium = stadiumRepository.findByApiStadiumId(apiStadiumId).orElseGet(Stadium::new);

                stadium.setApiStadiumId(apiStadiumId);
                stadium.setName(text(node, "name_en", "name"));
                stadium.setFifaName(text(node, "fifa_name"));
                stadium.setCity(text(node, "city_en", "city"));
                stadium.setCountry(text(node, "country_en", "country"));
                stadium.setCapacity(node.path("capacity").isMissingNode() ? null : node.path("capacity").asInt());
                stadiumRepository.save(stadium);
                saved++;
            }

            log.info("Saved/updated {} stadiums", saved);
        } catch (Exception e) {
            log.error("Error syncing stadiums", e);
        }
    }

    private void syncMatches() {
        String response = worldCupApiClient.getGames().block();
        if (response == null || response.isBlank()) {
            log.warn("Empty matches response from World Cup API");
            return;
        }

        Tournament tournament = tournamentRepository.findByYear(2026)
            .orElseGet(() -> tournamentRepository.findTopByOrderByYearDesc().orElse(null));
        if (tournament == null) {
            log.warn("No tournament found to attach matches");
            return;
        }

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode matches = root.path("response");
            if (!matches.isArray()) {
                log.warn("No matches array found in World Cup API response");
                return;
            }

            int saved = 0;
            for (JsonNode node : matches) {
                Long apiMatchId = node.path("id").asLong();
                Match match = matchRepository.findByApiMatchId(apiMatchId).orElseGet(Match::new);

                match.setApiMatchId(apiMatchId);
                match.setTournament(tournament);
                match.setStage(resolveStage(tournament, text(node, "type")));
                match.setGroup(resolveGroup(tournament, text(node, "group")));

                Stadium stadium = resolveStadium(node.path("stadium_id").asLong(0L));
                match.setStadium(stadium);
                match.setVenue(stadium != null ? stadium.getName() : text(node, "venue"));

                match.setHomeTeam(resolveTeam(node.path("home_team_id").asLong(0L)));
                match.setAwayTeam(resolveTeam(node.path("away_team_id").asLong(0L)));
                match.setHomeTeamLabel(text(node, "home_team_label"));
                match.setAwayTeamLabel(text(node, "away_team_label"));
                match.setKickoffTime(parseDateTime(text(node, "local_date", "date")));
                match.setStatus(resolveStatus(node));
                match.setHomeScore(node.path("home_score").isMissingNode() ? null : node.path("home_score").asInt());
                match.setAwayScore(node.path("away_score").isMissingNode() ? null : node.path("away_score").asInt());

                matchRepository.save(match);
                saved++;
            }

            log.info("Saved/updated {} matches", saved);
        } catch (Exception e) {
            log.error("Error syncing matches", e);
        }
    }

    private Stadium resolveStadium(Long apiStadiumId) {
        if (apiStadiumId == null || apiStadiumId <= 0) {
            return null;
        }
        return stadiumRepository.findByApiStadiumId(apiStadiumId).orElse(null);
    }

    private Team resolveTeam(Long apiTeamId) {
        if (apiTeamId == null || apiTeamId <= 0) {
            return null;
        }
        return teamRepository.findByApiTeamId(apiTeamId).orElse(null);
    }

    private Stage resolveStage(Tournament tournament, String type) {
        String stageName = STAGE_NAMES.getOrDefault(type == null ? "" : type.toLowerCase(), null);
        if (stageName == null) {
            return null;
        }

        return stageRepository.findByTournamentIdAndName(tournament.getId(), stageName)
            .orElseGet(() -> stageRepository.save(new Stage(null, tournament, stageName)));
    }

    private Group resolveGroup(Tournament tournament, String groupName) {
        if (groupName == null || groupName.isBlank() || !groupName.matches("[A-L]")) {
            return null;
        }

        return groupRepository.findByTournamentIdAndName(tournament.getId(), groupName)
            .orElseGet(() -> groupRepository.save(new Group(null, tournament, groupName)));
    }

    private String resolveStatus(JsonNode matchNode) {
        if (matchNode.path("finished").asBoolean(false)) {
            return "FINISHED";
        }

        String rawStatus = matchNode.path("status").path("short").asText(null);
        if (rawStatus == null || rawStatus.isBlank()) {
            rawStatus = matchNode.path("status").asText(null);
        }

        if (rawStatus == null || rawStatus.isBlank()) {
            return "SCHEDULED";
        }

        return switch (rawStatus.toUpperCase()) {
            case "FT", "AET", "PEN", "CANC", "POST", "FINISHED" -> "FINISHED";
            case "1H", "2H", "ET", "LIVE", "HT" -> "LIVE";
            default -> "SCHEDULED";
        };
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }

        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (Exception ignored) {
                // Try next formatter.
            }
        }

        log.warn("Could not parse date: {}", dateStr);
        return null;
    }

    private String text(JsonNode node, String... fields) {
        for (String field : fields) {
            JsonNode value = node.path(field);
            if (!value.isMissingNode() && !value.isNull()) {
                String text = value.asText(null);
                if (text != null && !text.isBlank()) {
                    return text;
                }
            }
        }
        return null;
    }
}
