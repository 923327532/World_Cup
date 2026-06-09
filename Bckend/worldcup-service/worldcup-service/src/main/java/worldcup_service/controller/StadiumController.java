package worldcup_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import worldcup_service.dto.StadiumDTO;
import worldcup_service.exception.ResourceNotFoundException;
import worldcup_service.service.StadiumService;
import worldcup_service.service.WorldCupPublicApiBridgeService;

import java.util.List;

@RestController
@RequestMapping("/stadiums")
@RequiredArgsConstructor
public class StadiumController {

    private final StadiumService stadiumService;
    private final WorldCupPublicApiBridgeService worldCupPublicApiBridgeService;

    @GetMapping
    public ResponseEntity<List<StadiumDTO>> getAllStadiums() {
        List<StadiumDTO> localStadiums = stadiumService.getAllStadiums();
        if (!localStadiums.isEmpty()) {
            return ResponseEntity.ok(localStadiums);
        }

        return ResponseEntity.ok(worldCupPublicApiBridgeService.getStadiums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StadiumDTO> getStadiumById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(stadiumService.getStadiumById(id));
        } catch (ResourceNotFoundException ex) {
            return worldCupPublicApiBridgeService.getStadiumById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ex);
        }
    }
}
