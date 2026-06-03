package tecsup.worldcup.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchFinishedEvent implements Serializable {

    private Long matchId;
    private Long homeTeamId;
    private Long awayTeamId;
    private int homeScore;
    private int awayScore;
}