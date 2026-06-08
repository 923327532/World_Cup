package worldcup_service.controller;

import worldcup_service.dto.TeamDTO;
import worldcup_service.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams(
        @RequestParam(required = false) String name
    ) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(teamService.searchTeams(name));
        }
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }
}
