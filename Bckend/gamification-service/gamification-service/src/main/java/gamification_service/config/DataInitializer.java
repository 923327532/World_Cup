package gamification_service.config;

import gamification_service.entity.AchievementRule;
import gamification_service.entity.Badge;
import gamification_service.entity.Reward;
import gamification_service.repository.AchievementRuleRepository;
import gamification_service.repository.BadgeRepository;
import gamification_service.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BadgeRepository badgeRepository;
    private final RewardRepository rewardRepository;
    private final AchievementRuleRepository achievementRuleRepository;

    @Override
    public void run(String... args) {
        if (badgeRepository.count() == 0) {
            Badge firstGoal = badgeRepository.save(new Badge(null, "Primer Gol", "Primera predicción acertada", "⚽"));
            Badge streak3 = badgeRepository.save(new Badge(null, "Racha 3", "3 aciertos consecutivos", "🔥"));
            Badge streak5 = badgeRepository.save(new Badge(null, "Racha 5", "5 aciertos consecutivos", "🚀"));
            Badge streak10 = badgeRepository.save(new Badge(null, "Racha 10", "10 aciertos consecutivos", "💎"));
            Badge expert = badgeRepository.save(new Badge(null, "Experto Mundialista", "1000 puntos", "🏆"));
            Badge master = badgeRepository.save(new Badge(null, "Maestro de Predicciones", "2500 puntos", "👑"));
            Badge legend = badgeRepository.save(new Badge(null, "Leyenda Tecsup", "Top 10", "🌟"));
            Badge champion = badgeRepository.save(new Badge(null, "Campeón Mundial", "Top 1", "🥇"));

            // Create achievement rules
            AchievementRule rule1 = new AchievementRule();
            rule1.setBadge(firstGoal);
            rule1.setRequiredPoints(10L);
            achievementRuleRepository.save(rule1);

            AchievementRule rule2 = new AchievementRule();
            rule2.setBadge(streak3);
            rule2.setRequiredStreak(3);
            achievementRuleRepository.save(rule2);

            AchievementRule rule3 = new AchievementRule();
            rule3.setBadge(expert);
            rule3.setRequiredPoints(1000L);
            achievementRuleRepository.save(rule3);
        }

        if (rewardRepository.count() == 0) {
            rewardRepository.save(new Reward(null, 1, "Laptop Gamer", "Laptop de alta gama"));
            rewardRepository.save(new Reward(null, 2, "Tablet Pro", "Tablet profesional"));
            rewardRepository.save(new Reward(null, 3, "Gift Card $500", "Tarjeta de regalo"));
            rewardRepository.save(new Reward(null, 4, "Gift Card $300", "Tarjeta de regalo"));
            rewardRepository.save(new Reward(null, 5, "Gift Card $200", "Tarjeta de regalo"));
            rewardRepository.save(new Reward(null, 6, "Gift Card $100", "Tarjeta de regalo"));
            rewardRepository.save(new Reward(null, 7, "Gift Card $50", "Tarjeta de regalo"));
            rewardRepository.save(new Reward(null, 8, "Merchandising Tecsup", "Kit de merchandising"));
            rewardRepository.save(new Reward(null, 9, "Merchandising Tecsup", "Kit de merchandising"));
            rewardRepository.save(new Reward(null, 10, "Merchandising Tecsup", "Kit de merchandising"));
        }
    }
}