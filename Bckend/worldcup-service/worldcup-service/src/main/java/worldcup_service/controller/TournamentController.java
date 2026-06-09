package worldcup_service.controller;

import worldcup_service.dto.TournamentDTO;
import worldcup_service.exception.ResourceNotFoundException;
import worldcup_service.service.TournamentService;
import worldcup_service.service.WorldCupPublicApiBridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;
    private final WorldCupPublicApiBridgeService worldCupPublicApiBridgeService;

    @GetMapping
    public ResponseEntity<List<TournamentDTO>> getAllTournaments() {
        List<TournamentDTO> localTournaments = tournamentService.getAllTournaments();
        if (!localTournaments.isEmpty()) {
            return ResponseEntity.ok(localTournaments);
        }

        return ResponseEntity.ok(List.of(worldCupPublicApiBridgeService.getCurrentTournament()));
    }

    @GetMapping("/current")
    public ResponseEntity<TournamentDTO> getCurrentTournament() {
        try {
            return ResponseEntity.ok(tournamentService.getCurrentTournament());
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(worldCupPublicApiBridgeService.getCurrentTournament());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentDTO> getTournamentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(tournamentService.getTournamentById(id));
        } catch (ResourceNotFoundException ex) {
            TournamentDTO externalTournament = worldCupPublicApiBridgeService.getCurrentTournament();
            if (externalTournament.id().equals(id)) {
                return ResponseEntity.ok(externalTournament);
            }
            throw ex;
        }
    }
}
