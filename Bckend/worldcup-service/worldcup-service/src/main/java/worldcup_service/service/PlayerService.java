package worldcup_service.service;

import worldcup_service.dto.PlayerDTO;
import worldcup_service.entity.Player;
import worldcup_service.exception.ResourceNotFoundException;
import worldcup_service.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public List<PlayerDTO> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId).stream()
            .map(this::toDTO)
            .toList();
    }

    public PlayerDTO getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Player", id));
        return toDTO(player);
    }

    private PlayerDTO toDTO(Player p) {
        return new PlayerDTO(
            p.getId(),
            p.getApiPlayerId(),
            p.getTeam() != null ? p.getTeam().getId() : null,
            p.getTeam() != null ? p.getTeam().getName() : null,
            p.getFullName(),
            p.getPosition(),
            p.getNumber()
        );
    }
}
