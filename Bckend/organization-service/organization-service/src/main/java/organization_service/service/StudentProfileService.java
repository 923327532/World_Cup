package organization_service.service;

import organization_service.dto.StudentProfileDTO;
import organization_service.entity.StudentProfile;
import organization_service.repository.CampusRepository;
import organization_service.repository.CareerRepository;
import organization_service.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final CampusRepository campusRepository;
    private final CareerRepository careerRepository;

    public StudentProfileDTO createProfile(StudentProfileDTO dto) {
        StudentProfile profile = new StudentProfile();
        profile.setUserId(dto.getUserId());
        profile.setCampus(campusRepository.findById(dto.getCampusId())
            .orElseThrow(() -> new RuntimeException("Campus not found")));
        profile.setCareer(careerRepository.findById(dto.getCareerId())
            .orElseThrow(() -> new RuntimeException("Career not found")));
        profile.setStudentCode(dto.getStudentCode());
        profile.setCycle(dto.getCycle());

        profile = studentProfileRepository.save(profile);
        return toDTO(profile);
    }

    public StudentProfileDTO getProfileByUserId(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        return toDTO(profile);
    }

    private StudentProfileDTO toDTO(StudentProfile profile) {
        return new StudentProfileDTO(
            profile.getId(),
            profile.getUserId(),
            profile.getCampus().getId(),
            profile.getCareer().getId(),
            profile.getStudentCode(),
            profile.getCycle()
        );
    }
}