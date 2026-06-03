package admin_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultUpdateRequest {

    @NotNull(message = "Home score is required")
    private Integer homeScore;

    @NotNull(message = "Away score is required")
    private Integer awayScore;

    private String winner;
}
