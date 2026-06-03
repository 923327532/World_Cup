package admin_service.repository;

import admin_service.entity.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
    List<AdminAuditLog> findByAdminIdOrderByCreatedAtDesc(Long adminId);
    List<AdminAuditLog> findByActionOrderByCreatedAtDesc(String action);
    List<AdminAuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
}
