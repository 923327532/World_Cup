package organization_service.controller;

import organization_service.dto.StudentProfileDTO;
import organization_service.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student-profiles")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @PostMapping
    public ResponseEntity<StudentProfileDTO> createProfile(@RequestBody StudentProfileDTO dto) {
        return ResponseEntity.ok(studentProfileService.createProfile(dto));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<StudentProfileDTO> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(studentProfileService.getProfileByUserId(userId));
    }
}