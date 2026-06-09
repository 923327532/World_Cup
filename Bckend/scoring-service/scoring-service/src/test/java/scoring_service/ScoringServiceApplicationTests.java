package scoring_service;

import org.junit.jupiter.api.Test;
import scoring_service.dto.PredictionScoreRequest;
import scoring_service.dto.ScoreBreakdownDTO;
import scoring_service.service.ScoreCalculatorService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoringServiceApplicationTests {

	private final ScoreCalculatorService scoreCalculatorService =
		new ScoreCalculatorService(null, null, null, null, null);

	@Test
	void exactScoreGetsFivePoints() {
		ScoreBreakdownDTO result = scoreCalculatorService.calculatePredictionScore(
			new PredictionScoreRequest(2, 1, 2, 1, null, null, 0)
		);

		assertEquals(5, result.basePoints());
		assertEquals(5, result.totalPoints());
		assertEquals("EXACT_SCORE", result.ruleApplied());
	}

	@Test
	void correctWinnerGetsThreePoints() {
		ScoreBreakdownDTO result = scoreCalculatorService.calculatePredictionScore(
			new PredictionScoreRequest(1, 0, 2, 1, null, null, 0)
		);

		assertEquals(3, result.basePoints());
		assertEquals("WINNER_OR_DRAW", result.ruleApplied());
	}

	@Test
	void earlyPredictionAddsOnePoint() {
		LocalDateTime kickoffTime = LocalDateTime.of(2026, 6, 20, 18, 0);

		ScoreBreakdownDTO result = scoreCalculatorService.calculatePredictionScore(
			new PredictionScoreRequest(2, 1, 2, 1, kickoffTime.minusHours(25), kickoffTime, 0)
		);

		assertEquals(1, result.earlyBonus());
		assertEquals(6, result.totalPoints());
	}

	@Test
	void thirdConsecutiveCorrectPredictionAddsStreakBonus() {
		ScoreBreakdownDTO result = scoreCalculatorService.calculatePredictionScore(
			new PredictionScoreRequest(2, 1, 2, 1, null, null, 2)
		);

		assertEquals(2, result.streakBonus());
		assertEquals(7, result.totalPoints());
	}

}
