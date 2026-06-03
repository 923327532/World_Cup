package scoring_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievement_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "badge_id")
    private Long badgeId;

    @Column(name = "required_points")
    private Integer requiredPoints;

    @Column(name = "required_streak")
    private Integer requiredStreak;

    @Column(name = "required_position")
    private Integer requiredPosition;
}