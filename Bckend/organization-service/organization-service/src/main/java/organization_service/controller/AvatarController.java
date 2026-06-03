package organization_service.controller;

import organization_service.dto.AvatarDTO;
import organization_service.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avatars")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @GetMapping
    public ResponseEntity<List<AvatarDTO>> getAllAvatars() {
        return ResponseEntity.ok(avatarService.getAllAvatars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvatarDTO> getAvatarById(@PathVariable Long id) {
        return ResponseEntity.ok(avatarService.getAvatarById(id));
    }
}