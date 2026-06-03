package organization_service.service;

import organization_service.dto.DepartmentDTO;
import organization_service.entity.Department;
import organization_service.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found"));
        return toDTO(department);
    }

    private DepartmentDTO toDTO(Department department) {
        return new DepartmentDTO(department.getId(), department.getName());
    }
}