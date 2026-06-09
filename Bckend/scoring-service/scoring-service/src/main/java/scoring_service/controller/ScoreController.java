package scoring_service.controller;

import scoring_service.dto.ScoreHistoryDTO;
import scoring_service.dto.PredictionScoreRequest;
import scoring_service.dto.RoomRankingEntryDTO;
import scoring_service.dto.ScoreBreakdownDTO;
import scoring_service.dto.UserScoreDTO;
import scoring_service.service.ScoreCalculatorService;
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
    private final ScoreCalculatorService scoreCalculatorService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserScoreDTO> getUserScore(@PathVariable Long userId) {
        return ResponseEntity.ok(userScoreService.getUserScore(userId));
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<ScoreHistoryDTO>> getUserScoreHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(userScoreService.getUserScoreHistory(userId));
    }

    @GetMapping("/rooms/{roomId}/ranking")
    public ResponseEntity<List<RoomRankingEntryDTO>> getRoomRanking(@PathVariable Long roomId) {
        return ResponseEntity.ok(userScoreService.getRoomRanking(roomId));
    }

    @PostMapping("/prediction/calculate")
    public ResponseEntity<ScoreBreakdownDTO> calculatePredictionScore(
        @RequestBody PredictionScoreRequest request
    ) {
        return ResponseEntity.ok(scoreCalculatorService.calculatePredictionScore(request));
    }
}
