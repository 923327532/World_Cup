package worldcup_service.controller;

import worldcup_service.dto.MatchDTO;
import worldcup_service.exception.ResourceNotFoundException;
import worldcup_service.service.MatchService;
import worldcup_service.service.WorldCupPublicApiBridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final WorldCupPublicApiBridgeService worldCupPublicApiBridgeService;

    @GetMapping("/date/{date}")
    public ResponseEntity<List<MatchDTO>> getMatchesByDate(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<MatchDTO> localMatches = matchService.getMatchesByDate(date);
        if (!localMatches.isEmpty()) {
            return ResponseEntity.ok(localMatches);
        }

        return ResponseEntity.ok(worldCupPublicApiBridgeService.getGamesByDate(date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatchById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(matchService.getMatchById(id));
        } catch (ResourceNotFoundException ex) {
            return worldCupPublicApiBridgeService.getGameById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ex);
        }
    }

    @GetMapping("/live")
    public ResponseEntity<List<MatchDTO>> getLiveMatches() {
        List<MatchDTO> localMatches = matchService.getLiveMatches();
        if (!localMatches.isEmpty()) {
            return ResponseEntity.ok(localMatches);
        }

        return ResponseEntity.ok(worldCupPublicApiBridgeService.getLiveGames());
    }
}
