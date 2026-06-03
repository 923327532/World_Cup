package group_service.controller;

import group_service.dto.*;
import group_service.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // ========== ROOMS ==========

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @Valid @RequestBody RoomRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.createRoom(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(roomService.updateRoom(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        roomService.deleteRoom(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/join/{inviteCode}")
    public ResponseEntity<RoomResponse> joinByCode(
            @PathVariable String inviteCode,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(roomService.joinByCode(inviteCode, userId));
    }

    // ========== MEMBERS ==========

    @GetMapping("/{roomId}/members")
    public ResponseEntity<List<MemberResponse>> getRoomMembers(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomMembers(roomId));
    }

    @DeleteMapping("/{roomId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long roomId,
            @PathVariable Long memberId,
            @RequestHeader("X-User-Id") Long userId) {
        roomService.removeMember(roomId, memberId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{roomId}/members/{memberId}/role")
    public ResponseEntity<Void> updateMemberRole(
            @PathVariable Long roomId,
            @PathVariable Long memberId,
            @RequestParam String role,
            @RequestHeader("X-User-Id") Long userId) {
        roomService.updateMemberRole(roomId, memberId, role, userId);
        return ResponseEntity.noContent().build();
    }

    // ========== INVITES ==========

    @GetMapping("/{roomId}/invites")
    public ResponseEntity<List<InviteResponse>> getRoomInvites(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomInvites(roomId));
    }

    @PostMapping("/{roomId}/invites")
    public ResponseEntity<InviteResponse> createInvite(
            @PathVariable Long roomId,
            @Valid @RequestBody InviteRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.createInvite(roomId, request, userId));
    }

    @PostMapping("/invites/{inviteId}/respond")
    public ResponseEntity<InviteResponse> respondToInvite(
            @PathVariable Long inviteId,
            @RequestParam String response,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(roomService.respondToInvite(inviteId, response, userId));
    }

    // ========== REPORTS ==========

    @GetMapping("/{roomId}/reports")
    public ResponseEntity<List<ReportResponse>> getRoomReports(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomReports(roomId));
    }

    @PostMapping("/{roomId}/reports")
    public ResponseEntity<ReportResponse> createReport(
            @PathVariable Long roomId,
            @Valid @RequestBody ReportRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.createReport(roomId, request, userId));
    }

    @PutMapping("/reports/{reportId}/resolve")
    public ResponseEntity<ReportResponse> resolveReport(
            @PathVariable Long reportId,
            @RequestParam String resolution,
            @RequestParam(required = false) String note,
            @RequestHeader("X-User-Id") Long adminId) {
        return ResponseEntity.ok(roomService.resolveReport(reportId, resolution, note, adminId));
    }

    // ========== BANS ==========

    @GetMapping("/{roomId}/bans")
    public ResponseEntity<List<BanResponse>> getRoomBans(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomBans(roomId));
    }

    @PostMapping("/{roomId}/bans")
    public ResponseEntity<BanResponse> banUser(
            @PathVariable Long roomId,
            @RequestParam Long userId,
            @RequestParam String reason,
            @RequestParam(defaultValue = "false") boolean permanent,
            @RequestHeader("X-User-Id") Long requesterId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.banUser(roomId, userId, reason, permanent, requesterId));
    }

    @DeleteMapping("/{roomId}/bans/{userId}")
    public ResponseEntity<Void> unbanUser(
            @PathVariable Long roomId,
            @PathVariable Long userId,
            @RequestHeader("X-User-Id") Long requesterId) {
        roomService.unbanUser(roomId, userId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
