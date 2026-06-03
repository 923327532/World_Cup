package leaderboard_service.controller;

import leaderboard_service.dto.LeaderboardEntryDTO;
import leaderboard_service.service.CampusRankingService;
import leaderboard_service.service.CareerRankingService;
import leaderboard_service.service.DepartmentRankingService;
import leaderboard_service.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final RankingService rankingService;
    private final CampusRankingService campusRankingService;
    private final CareerRankingService careerRankingService;
    private final DepartmentRankingService departmentRankingService;

    @GetMapping("/global")
    public ResponseEntity<List<LeaderboardEntryDTO>> getGlobalRanking(
        @RequestParam(defaultValue = "100") int limit
    ) {
        return ResponseEntity.ok(rankingService.getGlobalRanking(limit));
    }

    @GetMapping("/campus/{campusId}")
    public ResponseEntity<List<LeaderboardEntryDTO>> getCampusRanking(
        @PathVariable Long campusId,
        @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(campusRankingService.getCampusRanking(campusId, limit));
    }

    @GetMapping("/career/{careerId}")
    public ResponseEntity<List<LeaderboardEntryDTO>> getCareerRanking(
        @PathVariable Long careerId,
        @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(careerRankingService.getCareerRanking(careerId, limit));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<LeaderboardEntryDTO>> getDepartmentRanking(
        @PathVariable Long departmentId,
        @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(departmentRankingService.getDepartmentRanking(departmentId, limit));
    }
}