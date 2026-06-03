package auth_service.config;

import auth_service.entity.Role;
import auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, "ADMIN", "Administrator"));
            roleRepository.save(new Role(null, "STUDENT", "Student"));
            roleRepository.save(new Role(null, "TEACHER", "Teacher"));
        }
    }
}