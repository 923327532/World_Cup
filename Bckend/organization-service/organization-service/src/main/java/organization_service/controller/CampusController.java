package organization_service.controller;

import organization_service.dto.CampusDTO;
import organization_service.service.CampusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campuses")
@RequiredArgsConstructor
public class CampusController {

    private final CampusService campusService;

    @GetMapping
    public ResponseEntity<List<CampusDTO>> getAllCampuses() {
        return ResponseEntity.ok(campusService.getAllCampuses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampusDTO> getCampusById(@PathVariable Long id) {
        return ResponseEntity.ok(campusService.getCampusById(id));
    }
}