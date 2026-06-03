package organization_service.service;

import organization_service.dto.TeacherProfileDTO;
import organization_service.entity.TeacherProfile;
import organization_service.repository.CampusRepository;
import organization_service.repository.DepartmentRepository;
import organization_service.repository.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherProfileService {

    private final TeacherProfileRepository teacherProfileRepository;
    private final CampusRepository campusRepository;
    private final DepartmentRepository departmentRepository;

    public TeacherProfileDTO createProfile(TeacherProfileDTO dto) {
        TeacherProfile profile = new TeacherProfile();
        profile.setUserId(dto.getUserId());
        profile.setCampus(campusRepository.findById(dto.getCampusId())
            .orElseThrow(() -> new RuntimeException("Campus not found")));
        profile.setDepartment(departmentRepository.findById(dto.getDepartmentId())
            .orElseThrow(() -> new RuntimeException("Department not found")));

        profile = teacherProfileRepository.save(profile);
        return toDTO(profile);
    }

    public TeacherProfileDTO getProfileByUserId(Long userId) {
        TeacherProfile profile = teacherProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        return toDTO(profile);
    }

    private TeacherProfileDTO toDTO(TeacherProfile profile) {
        return new TeacherProfileDTO(
            profile.getId(),
            profile.getUserId(),
            profile.getCampus().getId(),
            profile.getDepartment().getId()
        );
    }
}