package organization_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {
    private Long id;
    private Long userId;

    @NotNull
    private Long campusId;

    @NotNull
    private Long careerId;

    @NotBlank
    private String studentCode;

    private String cycle;
}