package gamification_service.service;

import gamification_service.entity.AchievementRule;
import gamification_service.entity.Badge;
import gamification_service.entity.UserBadge;
import gamification_service.repository.AchievementRuleRepository;
import gamification_service.repository.BadgeRepository;
import gamification_service.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final AchievementRuleRepository achievementRuleRepository;
    private final RabbitTemplate rabbitTemplate;

    public void checkAchievements(Long userId, Long totalPoints, int streak) {
        achievementRuleRepository.findAll().forEach(rule -> {
            if (meetsCriteria(rule, totalPoints, streak)) {
                awardBadge(userId, rule.getBadge());
            }
        });
    }

    private boolean meetsCriteria(AchievementRule rule, Long points, int streak) {
        if (rule.getRequiredPoints() != null && points < rule.getRequiredPoints()) {
            return false;
        }
        if (rule.getRequiredStreak() != null && streak < rule.getRequiredStreak()) {
            return false;
        }
        return true;
    }

    public void awardBadge(Long userId, Badge badge) {
        if (userBadgeRepository.findByUserIdAndBadgeId(userId, badge.getId()).isPresent()) {
            return; // Already has badge
        }

        UserBadge userBadge = new UserBadge();
        userBadge.setUserId(userId);
        userBadge.setBadge(badge);
        userBadge.setEarnedAt(LocalDateTime.now());

        userBadgeRepository.save(userBadge);

        // Publish event
        rabbitTemplate.convertAndSend("gamification.exchange", "badge.unlocked", userId);
    }
}