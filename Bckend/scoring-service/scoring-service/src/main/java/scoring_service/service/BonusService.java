package scoring_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BonusService {

@Value("${scoring.streak-3-bonus:2}")
    private int streak3Bonus;

    @Value("${scoring.streak-5-bonus:4}")
    private int streak5Bonus;

    @Value("${scoring.streak-10-bonus:8}")
    private int streak10Bonus;

    @Value("${scoring.perfect-match-bonus:3}")
    private int perfectMatchBonus;

    public int calculateStreakBonus(int streak) {
        return switch (streak) {
            case 3 -> streak3Bonus;
            case 5 -> streak5Bonus;
            case 10 -> streak10Bonus;
            default -> 0;
        };
    }

    public int getPerfectMatchBonus() {
        return perfectMatchBonus;
    }
}