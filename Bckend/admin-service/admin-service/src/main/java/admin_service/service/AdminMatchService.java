package admin_service.service;

import admin_service.dto.ManualMatchRequest;
import admin_service.dto.ManualMatchResponse;
import admin_service.dto.MatchResultUpdateRequest;
import admin_service.entity.ManualMatch;
import admin_service.exception.ResourceNotFoundException;
import admin_service.repository.ManualMatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminMatchService {

    private final ManualMatchRepository matchRepository;
    private final AdminAuditService auditService;
    private final RabbitTemplate rabbitTemplate;

    public List<ManualMatchResponse> getAllMatches() {
        return matchRepository.findAll().stream()
                .map(ManualMatchResponse::fromEntity)
                .toList();
    }

    public ManualMatchResponse getMatchById(Long id) {
        ManualMatch match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ManualMatch", id));
        return ManualMatchResponse.fromEntity(match);
    }

    public List<ManualMatchResponse> getMatchesByStatus(String status) {
        ManualMatch.MatchStatus matchStatus = ManualMatch.MatchStatus.valueOf(status.toUpperCase());
        return matchRepository.findByStatus(matchStatus).stream()
                .map(ManualMatchResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ManualMatchResponse createMatch(ManualMatchRequest request, String adminEmail) {
        ManualMatch match = new ManualMatch();
        match.setHomeTeam(request.getHomeTeam());
        match.setAwayTeam(request.getAwayTeam());
        match.setStartTime(request.getStartTime());
        match.setVenue(request.getVenue());
        match.setStage(request.getStage());
        match.setGroupName(request.getGroupName());
        match.setStatus(ManualMatch.MatchStatus.SCHEDULED);
        match.setSourceType(ManualMatch.SourceType.MANUAL);
        match.setCreatedBy(adminEmail);
        match.setHomeScore(0);
        match.setAwayScore(0);

        ManualMatch saved = matchRepository.save(match);

        auditService.log("CREATE", "ManualMatch", saved.getId(),
                "Match created: " + request.getHomeTeam() + " vs " + request.getAwayTeam(),
                null, adminEmail);

        log.info("Manual match created: {} vs {} by {}", request.getHomeTeam(), request.getAwayTeam(), adminEmail);
        return ManualMatchResponse.fromEntity(saved);
    }

    @Transactional
    public ManualMatchResponse updateMatch(Long id, ManualMatchRequest request, String adminEmail) {
        ManualMatch match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ManualMatch", id));

        match.setHomeTeam(request.getHomeTeam());
        match.setAwayTeam(request.getAwayTeam());
        match.setStartTime(request.getStartTime());
        match.setVenue(request.getVenue());
        match.setStage(request.getStage());
        match.setGroupName(request.getGroupName());
        match.setUpdatedBy(adminEmail);

        ManualMatch saved = matchRepository.save(match);

        auditService.log("UPDATE", "ManualMatch", saved.getId(),
                "Match updated: " + request.getHomeTeam() + " vs " + request.getAwayTeam(),
                null, adminEmail);

        log.info("Manual match updated: id={} by {}", id, adminEmail);
        return ManualMatchResponse.fromEntity(saved);
    }

    @Transactional
    public ManualMatchResponse updateMatchResult(Long id, MatchResultUpdateRequest request, String adminEmail) {
        ManualMatch match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ManualMatch", id));

        match.setHomeScore(request.getHomeScore());
        match.setAwayScore(request.getAwayScore());
        match.setWinner(request.getWinner());
        match.setStatus(ManualMatch.MatchStatus.FINISHED);
        match.setUpdatedBy(adminEmail);

        ManualMatch saved = matchRepository.save(match);

        // Publish match.finished event for scoring/leaderboard to process
        rabbitTemplate.convertAndSend("scoring.exchange", "match.finished", saved);

        auditService.log("RESULT_UPDATE", "ManualMatch", saved.getId(),
                "Result updated: " + request.getHomeScore() + "-" + request.getAwayScore()
                        + " Winner: " + request.getWinner(),
                null, adminEmail);

        log.info("Match result updated: id={} {} - {} by {}",
                id, request.getHomeScore(), request.getAwayScore(), adminEmail);
        return ManualMatchResponse.fromEntity(saved);
    }

    @Transactional
    public ManualMatchResponse updateMatchStatus(Long id, String status, String adminEmail) {
        ManualMatch match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ManualMatch", id));

        ManualMatch.MatchStatus newStatus = ManualMatch.MatchStatus.valueOf(status.toUpperCase());
        match.setStatus(newStatus);
        match.setUpdatedBy(adminEmail);

        ManualMatch saved = matchRepository.save(match);

        auditService.log("STATUS_UPDATE", "ManualMatch", saved.getId(),
                "Status changed to: " + newStatus,
                null, adminEmail);

        log.info("Match status updated: id={} status={} by {}", id, newStatus, adminEmail);
        return ManualMatchResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteMatch(Long id, String adminEmail) {
        ManualMatch match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ManualMatch", id));

        matchRepository.delete(match);

        auditService.log("DELETE", "ManualMatch", id,
                "Match deleted: " + match.getHomeTeam() + " vs " + match.getAwayTeam(),
                null, adminEmail);

        log.info("Manual match deleted: id={} by {}", id, adminEmail);
    }
}
