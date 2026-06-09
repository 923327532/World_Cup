package worldcup_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import worldcup_service.service.MatchSyncScheduler;

import java.util.Map;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class WorldCupSyncController {

    private final MatchSyncScheduler matchSyncScheduler;

    @PostMapping
    public ResponseEntity<Map<String, String>> syncNow() {
        matchSyncScheduler.syncWorldCupData();
        return ResponseEntity.ok(Map.of("message", "World Cup sync executed"));
    }
}
