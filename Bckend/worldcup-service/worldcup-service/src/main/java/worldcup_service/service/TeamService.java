package worldcup_service.service;

import worldcup_service.dto.TeamDTO;
import worldcup_service.entity.Team;
import worldcup_service.exception.ResourceNotFoundException;
import worldcup_service.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Team", id));
        return toDTO(team);
    }

    public List<TeamDTO> searchTeams(String name) {
        return teamRepository.findByNameContainingIgnoreCase(name).stream()
            .map(this::toDTO)
            .toList();
    }

    private TeamDTO toDTO(Team team) {
        return new TeamDTO(
            team.getId(),
            team.getApiTeamId(),
            team.getName(),
            team.getCode(),
            team.getFlagUrl()
        );
    }
}
