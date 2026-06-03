package organization_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfileDTO {
    private Long id;
    private Long userId;

    @NotNull
    private Long campusId;

    @NotNull
    private Long departmentId;
}