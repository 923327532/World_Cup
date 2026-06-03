package tecsup.worldcup.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionScoredEvent implements Serializable {

    private Long predictionId;
    private Long userId;
    private int pointsEarned;
    private boolean isCorrect;
    private boolean streakBonus;
    private int streakCount;
    private int totalPoints;
    private LocalDateTime scoredAt;
}