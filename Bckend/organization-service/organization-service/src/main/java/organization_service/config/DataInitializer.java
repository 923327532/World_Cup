package organization_service.config;

import organization_service.entity.Campus;
import organization_service.entity.Department;
import organization_service.repository.CampusRepository;
import organization_service.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CampusRepository campusRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) {
        if (campusRepository.count() == 0) {
            campusRepository.save(new Campus(null, "Lima"));
            campusRepository.save(new Campus(null, "Arequipa"));
            campusRepository.save(new Campus(null, "Trujillo"));
        }

        if (departmentRepository.count() == 0) {
            departmentRepository.save(new Department(null, "Tecnología Digital"));
            departmentRepository.save(new Department(null, "Mecánica y Aviación"));
            departmentRepository.save(new Department(null, "Electricidad y Electrónica"));
            departmentRepository.save(new Department(null, "Mecatrónica"));
            departmentRepository.save(new Department(null, "Minería, Procesos Químicos y Metalúrgicos"));
            departmentRepository.save(new Department(null, "Gestión y Producción"));
            departmentRepository.save(new Department(null, "Seguridad y Salud en el Trabajo"));
            departmentRepository.save(new Department(null, "Tecnología Agrícola"));
        }
    }
}