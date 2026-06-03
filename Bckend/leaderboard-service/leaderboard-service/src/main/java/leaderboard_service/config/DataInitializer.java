package leaderboard_service.config;

import leaderboard_service.entity.Leaderboard;
import leaderboard_service.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final LeaderboardRepository leaderboardRepository;

    @Override
    public void run(String... args) {
        if (leaderboardRepository.count() == 0) {
            leaderboardRepository.save(new Leaderboard(null, "Global Ranking", "GLOBAL"));
            leaderboardRepository.save(new Leaderboard(null, "Lima Campus", "CAMPUS"));
            leaderboardRepository.save(new Leaderboard(null, "Arequipa Campus", "CAMPUS"));
            leaderboardRepository.save(new Leaderboard(null, "Trujillo Campus", "CAMPUS"));
            leaderboardRepository.save(new Leaderboard(null, "Students Ranking", "STUDENT"));
            leaderboardRepository.save(new Leaderboard(null, "Teachers Ranking", "TEACHER"));
        }
    }
}