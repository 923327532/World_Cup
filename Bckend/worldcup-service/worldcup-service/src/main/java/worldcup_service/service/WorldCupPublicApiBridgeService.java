package worldcup_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import worldcup_service.client.WorldCupApiClient;
import worldcup_service.dto.MatchDTO;
import worldcup_service.dto.StadiumDTO;
import worldcup_service.dto.TeamDTO;
import worldcup_service.dto.TournamentDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorldCupPublicApiBridgeService {

    private final WorldCupApiClient worldCupApiClient;
    private final ObjectMapper objectMapper;

    public List<TeamDTO> getTeams() {
        try {
            String body = worldCupApiClient.getTeams().block();
            return extractItems(body).stream()
                .map(this::toTeamDTO)
                .toList();
        } catch (Exception ex) {
            log.warn("Could not load teams from external World Cup API: {}", ex.getMessage());
            return List.of();
        }
    }

    public Optional<TeamDTO> getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            return Optional.empty();
        }

        try {
            String body = worldCupApiClient.getTeamById(teamId).block();
            List<JsonNode> items = extractItems(body);
            if (!items.isEmpty()) {
                return Optional.of(toTeamDTO(items.get(0)));
            }
        } catch (Exception ex) {
            log.debug("Team detail endpoint failed for id={}, falling back to teams list", teamId);
        }

        return getTeams().stream()
            .filter(team -> teamId.equals(team.apiTeamId()) || teamId.equals(team.id()))
            .findFirst();
    }

    public List<StadiumDTO> getStadiums() {
        try {
            String body = worldCupApiClient.getStadiums().block();
            return extractItems(body).stream()
                .map(this::toStadiumDTO)
                .toList();
        } catch (Exception ex) {
            log.warn("Could not load stadiums from external World Cup API: {}", ex.getMessage());
            return List.of();
        }
    }

    public Optional<StadiumDTO> getStadiumById(Long stadiumId) {
        if (stadiumId == null || stadiumId <= 0) {
            return Optional.empty();
        }

        try {
            String body = worldCupApiClient.getStadiumById(stadiumId).block();
            List<JsonNode> items = extractItems(body);
            if (!items.isEmpty()) {
                return Optional.of(toStadiumDTO(items.get(0)));
            }
        } catch (Exception ex) {
            log.debug("Stadium detail endpoint failed for id={}, falling back to stadiums list", stadiumId);
        }

        return getStadiums().stream()
            .filter(stadium -> stadiumId.equals(stadium.apiStadiumId()) || stadiumId.equals(stadium.id()))
            .findFirst();
    }

    public List<MatchDTO> getGames() {
        try {
            String body = worldCupApiClient.getGames().block();
            return extractItems(body).stream()
                .map(this::toMatchDTO)
                .toList();
        } catch (Exception ex) {
            log.warn("Could not load games from external World Cup API: {}", ex.getMessage());
            return List.of();
        }
    }

    public Optional<MatchDTO> getGameById(Long gameId) {
        if (gameId == null || gameId <= 0) {
            return Optional.empty();
        }

        try {
            String body = worldCupApiClient.getGameById(gameId).block();
            List<JsonNode> items = extractItems(body);
            if (!items.isEmpty()) {
                return Optional.of(toMatchDTO(items.get(0)));
            }
        } catch (Exception ex) {
            log.debug("Game detail endpoint failed for id={}, falling back to games list", gameId);
        }

        return getGames().stream()
            .filter(match -> gameId.equals(match.id()))
            .findFirst();
    }

    public List<MatchDTO> getGamesByDate(LocalDate date) {
        if (date == null) {
            return List.of();
        }

        return getGames().stream()
            .filter(match -> match.kickoffTime() != null && date.equals(match.kickoffTime().toLocalDate()))
            .toList();
    }

    public List<MatchDTO> getLiveGames() {
        return getGames().stream()
            .filter(match -> "LIVE".equalsIgnoreCase(match.status()))
            .toList();
    }

    public TournamentDTO getCurrentTournament() {
        List<MatchDTO> games = getGames();

        LocalDate startDate = games.stream()
            .map(MatchDTO::kickoffTime)
            .filter(time -> time != null)
            .map(LocalDateTime::toLocalDate)
            .min(Comparator.naturalOrder())
            .orElse(LocalDate.of(2026, 6, 11));

        LocalDate endDate = games.stream()
            .map(MatchDTO::kickoffTime)
            .filter(time -> time != null)
            .map(LocalDateTime::toLocalDate)
            .max(Comparator.naturalOrder())
            .orElse(LocalDate.of(2026, 7, 19));

        return new TournamentDTO(1L, "FIFA World Cup", 2026, startDate, endDate);
    }

    private List<JsonNode> extractItems(String body) {
        if (body == null || body.isBlank()) {
            return List.of();
        }

        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode responseNode = root.path("response");
            if (responseNode.isArray()) {
                List<JsonNode> items = new ArrayList<>();
                responseNode.forEach(items::add);
                return items;
            }

            if (!responseNode.isMissingNode() && !responseNode.isNull()) {
                return List.of(responseNode);
            }

            if (root.isArray()) {
                List<JsonNode> items = new ArrayList<>();
                root.forEach(items::add);
                return items;
            }

            return List.of(root);
        } catch (Exception ex) {
            log.warn("Could not parse external World Cup API body: {}", ex.getMessage());
            return List.of();
        }
    }

    private TeamDTO toTeamDTO(JsonNode node) {
        Long apiTeamId = readLong(node, "id", "team_id");
        String name = text(node, "name_en", "name");
        String code = text(node, "fifa_code", "code");
        String flagUrl = text(node, "flag", "logo");
        Long id = apiTeamId != null ? apiTeamId : 0L;

        return new TeamDTO(id, apiTeamId, name, code, flagUrl);
    }

    private StadiumDTO toStadiumDTO(JsonNode node) {
        Long apiStadiumId = readLong(node, "id", "stadium_id");
        Long id = apiStadiumId != null ? apiStadiumId : 0L;

        Integer capacity = null;
        JsonNode capacityNode = firstNode(node, "capacity");
        if (capacityNode != null && !capacityNode.isNull() && !capacityNode.isMissingNode()) {
            capacity = capacityNode.asInt();
        }

        return new StadiumDTO(
            id,
            apiStadiumId,
            text(node, "name_en", "name"),
            text(node, "fifa_name"),
            text(node, "city_en", "city"),
            text(node, "country_en", "country"),
            capacity
        );
    }

    private MatchDTO toMatchDTO(JsonNode node) {
        Long id = readLong(node, "id", "game_id");
        Long stadiumId = readLong(node, "stadium_id");
        Long homeTeamId = readLong(node, "home_team_id");
        Long awayTeamId = readLong(node, "away_team_id");

        return new MatchDTO(
            id != null ? id : 0L,
            stadiumId,
            text(node, "stadium_name", "venue"),
            text(node, "stadium_city"),
            text(node, "stadium_country"),
            parseInteger(node, "stadium_capacity"),
            text(node, "venue"),
            text(node, "type"),
            text(node, "group"),
            homeTeamId,
            text(node, "home_team_en", "home_team", "home_team_label"),
            awayTeamId,
            text(node, "away_team_en", "away_team", "away_team_label"),
            parseDateTime(text(node, "local_date", "date", "kickoff_time")),
            parseInteger(node, "home_score"),
            parseInteger(node, "away_score"),
            resolveStatus(node)
        );
    }

    private JsonNode firstNode(JsonNode node, String... fields) {
        for (String field : fields) {
            JsonNode value = node.path(field);
            if (!value.isMissingNode()) {
                return value;
            }
        }
        return null;
    }

    private Long readLong(JsonNode node, String... fields) {
        JsonNode value = firstNode(node, fields);
        if (value == null || value.isMissingNode() || value.isNull()) {
            return null;
        }

        if (value.isIntegralNumber()) {
            return value.asLong();
        }

        try {
            String text = value.asText(null);
            if (text == null || text.isBlank()) {
                return null;
            }
            return Long.parseLong(text);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Integer parseInteger(JsonNode node, String... fields) {
        JsonNode value = firstNode(node, fields);
        if (value == null || value.isMissingNode() || value.isNull()) {
            return null;
        }

        if (value.isIntegralNumber()) {
            return value.asInt();
        }

        try {
            String text = value.asText(null);
            if (text == null || text.isBlank()) {
                return null;
            }
            return Integer.parseInt(text);
        } catch (Exception ignored) {
            return null;
        }
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

        return null;
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
            case "1H", "2H", "ET", "LIVE", "HT", "IN_PLAY" -> "LIVE";
            default -> "SCHEDULED";
        };
    }
}
