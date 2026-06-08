package worldcup_service.controller;

import worldcup_service.dto.PlayerDTO;
import worldcup_service.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(playerService.getPlayersByTeam(teamId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }
}
