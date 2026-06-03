package organization_service.service;

import organization_service.dto.CampusDTO;
import organization_service.entity.Campus;
import organization_service.repository.CampusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampusService {

    private final CampusRepository campusRepository;

    public List<CampusDTO> getAllCampuses() {
        return campusRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    public CampusDTO getCampusById(Long id) {
        Campus campus = campusRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Campus not found"));
        return toDTO(campus);
    }

    private CampusDTO toDTO(Campus campus) {
        return new CampusDTO(campus.getId(), campus.getName());
    }
}