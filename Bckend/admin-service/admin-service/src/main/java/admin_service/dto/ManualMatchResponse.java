package admin_service.dto;

import admin_service.entity.ManualMatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualMatchResponse {

    private Long id;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime startTime;
    private String status;
    private Integer homeScore;
    private Integer awayScore;
    private String winner;
    private String sourceType;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String venue;
    private String stage;
    private String groupName;

    public static ManualMatchResponse fromEntity(ManualMatch match) {
        return ManualMatchResponse.builder()
                .id(match.getId())
                .homeTeam(match.getHomeTeam())
                .awayTeam(match.getAwayTeam())
                .startTime(match.getStartTime())
                .status(match.getStatus().name())
                .homeScore(match.getHomeScore())
                .awayScore(match.getAwayScore())
                .winner(match.getWinner())
                .sourceType(match.getSourceType().name())
                .createdBy(match.getCreatedBy())
                .updatedBy(match.getUpdatedBy())
                .createdAt(match.getCreatedAt())
                .updatedAt(match.getUpdatedAt())
                .venue(match.getVenue())
                .stage(match.getStage())
                .groupName(match.getGroupName())
                .build();
    }
}
