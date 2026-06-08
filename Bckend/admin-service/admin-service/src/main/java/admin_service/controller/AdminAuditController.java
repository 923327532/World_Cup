package admin_service.controller;

import admin_service.dto.AdminAuditLogResponse;
import admin_service.service.AdminAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AdminAuditController {

    private final AdminAuditService auditService;

    @GetMapping
    public ResponseEntity<List<AdminAuditLogResponse>> getAllLogs() {
        return ResponseEntity.ok(auditService.getAllLogs());
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<AdminAuditLogResponse>> getLogsByAdmin(@PathVariable Long adminId) {
        return ResponseEntity.ok(auditService.getLogsByAdmin(adminId));
    }

    @GetMapping("/entity")
    public ResponseEntity<List<AdminAuditLogResponse>> getLogsByEntity(
            @RequestParam String entityType,
            @RequestParam Long entityId) {
        return ResponseEntity.ok(auditService.getLogsByEntity(entityType, entityId));
    }
}
