package leaderboard_service.listener;

import leaderboard_service.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreUpdateListener {

    private final RankingService rankingService;

    @RabbitListener(queues = "leaderboard.updated")
    public void handleScoreUpdated(Long userId) {
        log.info("Updating ranking for user: {}", userId);
        // Get user score and update ranking
        // rankingService.updateUserRanking(userId, score);
    }
}