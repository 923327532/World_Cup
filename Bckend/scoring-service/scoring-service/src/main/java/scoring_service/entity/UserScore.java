package scoring_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserScore {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "total_points")
    private Long totalPoints = 0L;
}