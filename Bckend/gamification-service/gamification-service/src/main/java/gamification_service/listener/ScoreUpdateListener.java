package gamification_service.listener;

import gamification_service.service.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreUpdateListener {

    private final AchievementService achievementService;

    @RabbitListener(queues = "${rabbitmq.queue.score-updates:score-updates}")
    public void handleScoreUpdate(ScoreUpdateEvent event) {
        log.info("Checking achievements for user: {}", event.userId());
        achievementService.checkAchievements(event.userId(), event.totalPoints(), event.streak());
    }

    // Inner class for deserialization
    public record ScoreUpdateEvent(Long userId, Long totalPoints, int streak) {}
}