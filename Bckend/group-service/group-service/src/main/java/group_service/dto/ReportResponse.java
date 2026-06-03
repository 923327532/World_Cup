package group_service.dto;

import group_service.entity.UserReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long id;
    private Long roomId;
    private Long reportedBy;
    private Long reportedUserId;
    private String reason;
    private String description;
    private String status;
    private Long resolvedBy;
    private String resolutionNote;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public static ReportResponse fromEntity(UserReport report) {
        return ReportResponse.builder()
                .id(report.getId())
                .roomId(report.getRoomId())
                .reportedBy(report.getReportedBy())
                .reportedUserId(report.getReportedUserId())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus().name())
                .resolvedBy(report.getResolvedBy())
                .resolutionNote(report.getResolutionNote())
                .createdAt(report.getCreatedAt())
                .resolvedAt(report.getResolvedAt())
                .build();
    }
}
