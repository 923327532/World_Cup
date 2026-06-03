package admin_service.service;

import admin_service.dto.AdminAuditLogResponse;
import admin_service.entity.AdminAuditLog;
import admin_service.repository.AdminAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAuditService {

    private final AdminAuditLogRepository auditLogRepository;

    public void log(String action, String entityType, Long entityId, String details, Long adminId, String adminEmail) {
        AdminAuditLog log = new AdminAuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setAdminId(adminId);
        log.setAdminEmail(adminEmail);
        auditLogRepository.save(log);
    }

    public List<AdminAuditLogResponse> getAllLogs() {
        return auditLogRepository.findAll().stream()
                .map(AdminAuditLogResponse::fromEntity)
                .toList();
    }

    public List<AdminAuditLogResponse> getLogsByAdmin(Long adminId) {
        return auditLogRepository.findByAdminIdOrderByCreatedAtDesc(adminId).stream()
                .map(AdminAuditLogResponse::fromEntity)
                .toList();
    }

    public List<AdminAuditLogResponse> getLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId).stream()
                .map(AdminAuditLogResponse::fromEntity)
                .toList();
    }
}
