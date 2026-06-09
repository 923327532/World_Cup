package prediction_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePredictionRequest {

    @NotNull
    private Long matchId;

    @NotNull
    private Long roomId;

    @NotNull
    private Long predictionTypeId;

    @NotNull
    private String predictionValue;
}
