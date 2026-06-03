package scoring_service.repository;

import scoring_service.entity.AchievementRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRuleRepository extends JpaRepository<AchievementRule, Long> {
}