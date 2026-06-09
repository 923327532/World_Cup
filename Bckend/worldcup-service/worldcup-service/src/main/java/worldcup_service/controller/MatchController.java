package worldcup_service.controller;

import worldcup_service.dto.MatchDTO;
import worldcup_service.service.MatchService;
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

    @GetMapping("/date/{date}")
    public ResponseEntity<List<MatchDTO>> getMatchesByDate(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(matchService.getMatchesByDate(date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @GetMapping("/live")
    public ResponseEntity<List<MatchDTO>> getLiveMatches() {
        return ResponseEntity.ok(matchService.getLiveMatches());
    }
}
