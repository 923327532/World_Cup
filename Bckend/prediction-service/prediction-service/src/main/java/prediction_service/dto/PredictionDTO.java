package prediction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionDTO {
    private Long id;
    private Long userId;
    private Long matchId;
    private String homeTeam;
    private String awayTeam;
    private Long predictionTypeId;
    private String predictionType;
    private String predictionValue;
    private Integer points;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isLocked;
}