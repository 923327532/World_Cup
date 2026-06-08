package scoring_service.controller;

import scoring_service.dto.ScoreHistoryDTO;
import scoring_service.dto.UserScoreDTO;
import scoring_service.service.UserScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scoring")
@RequiredArgsConstructor
public class ScoreController {

    private final UserScoreService userScoreService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserScoreDTO> getUserScore(@PathVariable Long userId) {
        return ResponseEntity.ok(userScoreService.getUserScore(userId));
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<ScoreHistoryDTO>> getUserScoreHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(userScoreService.getUserScoreHistory(userId));
    }
}