package organization_service.service;

import organization_service.dto.AvatarDTO;
import organization_service.entity.Avatar;
import organization_service.repository.AvatarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final AvatarRepository avatarRepository;

    public List<AvatarDTO> getAllAvatars() {
        return avatarRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    public AvatarDTO getAvatarById(Long id) {
        Avatar avatar = avatarRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Avatar not found"));
        return toDTO(avatar);
    }

    private AvatarDTO toDTO(Avatar avatar) {
        return new AvatarDTO(avatar.getId(), avatar.getName(), avatar.getImageUrl());
    }
}