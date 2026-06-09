package worldcup_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import worldcup_service.dto.StadiumDTO;
import worldcup_service.entity.Stadium;
import worldcup_service.exception.ResourceNotFoundException;
import worldcup_service.repository.StadiumRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StadiumService {

    private final StadiumRepository stadiumRepository;

    public List<StadiumDTO> getAllStadiums() {
        return stadiumRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    public StadiumDTO getStadiumById(Long id) {
        Stadium stadium = stadiumRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Stadium", id));
        return toDTO(stadium);
    }

    private StadiumDTO toDTO(Stadium stadium) {
        return new StadiumDTO(
            stadium.getId(),
            stadium.getApiStadiumId(),
            stadium.getName(),
            stadium.getFifaName(),
            stadium.getCity(),
            stadium.getCountry(),
            stadium.getCapacity()
        );
    }
}
