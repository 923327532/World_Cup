package scoring_service.repository;

import scoring_service.entity.ScoringRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoringRuleRepository extends JpaRepository<ScoringRule, Long> {
}