package worldcup_service.service;

import worldcup_service.dto.TournamentDTO;
import worldcup_service.entity.Tournament;
import worldcup_service.exception.ResourceNotFoundException;
import worldcup_service.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public List<TournamentDTO> getAllTournaments() {
        return tournamentRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    public TournamentDTO getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));
        return toDTO(tournament);
    }

    public TournamentDTO getCurrentTournament() {
        Tournament tournament = tournamentRepository.findTopByOrderByYearDesc()
            .orElseThrow(() -> new ResourceNotFoundException("Tournament", 0L));
        return toDTO(tournament);
    }

    private TournamentDTO toDTO(Tournament t) {
        return new TournamentDTO(
            t.getId(),
            t.getName(),
            t.getYear(),
            t.getStartDate(),
            t.getEndDate()
        );
    }
}
