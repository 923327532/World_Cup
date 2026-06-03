package group_service.repository;

import group_service.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    List<UserReport> findByRoomId(Long roomId);
    List<UserReport> findByReportedUserId(Long reportedUserId);
    List<UserReport> findByStatus(UserReport.ReportStatus status);
}
