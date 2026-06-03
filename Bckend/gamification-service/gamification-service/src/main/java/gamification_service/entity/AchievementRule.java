package gamification_service.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id")
    private Badge badge;

    @Column(name = "required_points")
    private Long requiredPoints;

    @Column(name = "required_streak")
    private Integer requiredStreak;
}