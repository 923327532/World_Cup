package admin_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "manual_matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "home_team", nullable = false, length = 100)
    private String homeTeam;

    @Column(name = "away_team", nullable = false, length = 100)
    private String awayTeam;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(length = 100)
    private String winner;

    @Column(name = "source_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String venue;

    @Column(name = "stage", length = 50)
    private String stage;

    @Column(name = "group_name", length = 10)
    private String groupName;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = MatchStatus.SCHEDULED;
        }
        if (sourceType == null) {
            sourceType = SourceType.MANUAL;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum MatchStatus {
        SCHEDULED, LIVE, FINISHED, CANCELED
    }

    public enum SourceType {
        API, MANUAL
    }
}
