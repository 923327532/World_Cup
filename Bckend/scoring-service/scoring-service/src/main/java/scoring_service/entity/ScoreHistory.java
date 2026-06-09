package scoring_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "score_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "match_id")
    private Long matchId;

    @Column(name = "prediction_id")
    private Long predictionId;

    private Integer points;

    @Column(name = "base_points")
    private Integer basePoints;

    @Column(name = "streak_bonus")
    private Integer streakBonus;

    @Column(name = "early_bonus")
    private Integer earlyBonus;

    private String reason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
