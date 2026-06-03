package organization_service.controller;

import organization_service.dto.CareerDTO;
import organization_service.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/careers")
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;

    @GetMapping
    public ResponseEntity<List<CareerDTO>> getAllCareers() {
        return ResponseEntity.ok(careerService.getAllCareers());
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<CareerDTO>> getCareersByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(careerService.getCareersByDepartment(departmentId));
    }
}