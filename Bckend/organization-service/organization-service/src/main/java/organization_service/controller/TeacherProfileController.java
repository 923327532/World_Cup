package organization_service.controller;

import organization_service.dto.TeacherProfileDTO;
import organization_service.service.TeacherProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher-profiles")
@RequiredArgsConstructor
public class TeacherProfileController {

    private final TeacherProfileService teacherProfileService;

    @PostMapping
    public ResponseEntity<TeacherProfileDTO> createProfile(@RequestBody TeacherProfileDTO dto) {
        return ResponseEntity.ok(teacherProfileService.createProfile(dto));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<TeacherProfileDTO> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(teacherProfileService.getProfileByUserId(userId));
    }
}