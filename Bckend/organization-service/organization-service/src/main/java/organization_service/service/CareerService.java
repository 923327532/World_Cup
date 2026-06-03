package organization_service.service;

import organization_service.dto.CareerDTO;
import organization_service.entity.Career;
import organization_service.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;

    public List<CareerDTO> getAllCareers() {
        return careerRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    public List<CareerDTO> getCareersByDepartment(Long departmentId) {
        return careerRepository.findByDepartmentId(departmentId).stream()
            .map(this::toDTO)
            .toList();
    }

    private CareerDTO toDTO(Career career) {
        return new CareerDTO(
            career.getId(),
            career.getDepartment().getId(),
            career.getDepartment().getName(),
            career.getName()
        );
    }
}