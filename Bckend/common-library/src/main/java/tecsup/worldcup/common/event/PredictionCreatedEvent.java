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
public class PredictionCreatedEvent implements Serializable {

    private Long predictionId;
    private Long userId;
    private Long matchId;
    private Long predictionTypeId;
    private String predictionValue;
    private LocalDateTime createdAt;
}