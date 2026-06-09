package worldcup_service.controller;

import worldcup_service.dto.TeamDTO;
import worldcup_service.exception.ResourceNotFoundException;
import worldcup_service.service.TeamService;
import worldcup_service.service.WorldCupPublicApiBridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final WorldCupPublicApiBridgeService worldCupPublicApiBridgeService;

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams(
        @RequestParam(required = false) String name
    ) {
        List<TeamDTO> localTeams;
        if (name != null && !name.isBlank()) {
            localTeams = teamService.searchTeams(name);
        } else {
            localTeams = teamService.getAllTeams();
        }

        if (!localTeams.isEmpty()) {
            return ResponseEntity.ok(localTeams);
        }

        List<TeamDTO> externalTeams = worldCupPublicApiBridgeService.getTeams();
        if (name != null && !name.isBlank()) {
            String query = name.toLowerCase();
            externalTeams = externalTeams.stream()
                .filter(team -> team.name() != null && team.name().toLowerCase().contains(query))
                .toList();
        }

        return ResponseEntity.ok(externalTeams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(teamService.getTeamById(id));
        } catch (ResourceNotFoundException ex) {
            return worldCupPublicApiBridgeService.getTeamById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ex);
        }
    }
}
