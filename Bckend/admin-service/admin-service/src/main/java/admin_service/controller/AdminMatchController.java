package admin_service.controller;

import admin_service.dto.ManualMatchRequest;
import admin_service.dto.ManualMatchResponse;
import admin_service.dto.MatchResultUpdateRequest;
import admin_service.service.AdminMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class AdminMatchController {

    private final AdminMatchService adminMatchService;

    @GetMapping
    public ResponseEntity<List<ManualMatchResponse>> getAllMatches() {
        return ResponseEntity.ok(adminMatchService.getAllMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManualMatchResponse> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(adminMatchService.getMatchById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ManualMatchResponse>> getMatchesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(adminMatchService.getMatchesByStatus(status));
    }

    @PostMapping
    public ResponseEntity<ManualMatchResponse> createMatch(
            @Valid @RequestBody ManualMatchRequest request,
            @RequestHeader("X-User-Id") Long adminId,
            @RequestHeader("X-Admin-Email") String adminEmail) {
        ManualMatchResponse response = adminMatchService.createMatch(request, adminId, adminEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManualMatchResponse> updateMatch(
            @PathVariable Long id,
            @Valid @RequestBody ManualMatchRequest request,
            @RequestHeader("X-User-Id") Long adminId,
            @RequestHeader("X-Admin-Email") String adminEmail) {
        return ResponseEntity.ok(adminMatchService.updateMatch(id, request, adminId, adminEmail));
    }

    @PutMapping("/{id}/result")
    public ResponseEntity<ManualMatchResponse> updateMatchResult(
            @PathVariable Long id,
            @Valid @RequestBody MatchResultUpdateRequest request,
            @RequestHeader("X-User-Id") Long adminId,
            @RequestHeader("X-Admin-Email") String adminEmail) {
        return ResponseEntity.ok(adminMatchService.updateMatchResult(id, request, adminId, adminEmail));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ManualMatchResponse> updateMatchStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("X-User-Id") Long adminId,
            @RequestHeader("X-Admin-Email") String adminEmail) {
        return ResponseEntity.ok(adminMatchService.updateMatchStatus(id, status, adminId, adminEmail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long adminId,
            @RequestHeader("X-Admin-Email") String adminEmail) {
        adminMatchService.deleteMatch(id, adminId, adminEmail);
        return ResponseEntity.noContent().build();
    }
}
