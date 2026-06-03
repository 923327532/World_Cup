package organization_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareerDTO {
    private Long id;
    private Long departmentId;
    private String departmentName;
    private String name;
}