package group_service.dto;

import group_service.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long id;
    private String name;
    private String description;
    private String inviteCode;
    private Long createdBy;
    private Integer maxMembers;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RoomResponse fromEntity(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .inviteCode(room.getInviteCode())
                .createdBy(room.getCreatedBy())
                .maxMembers(room.getMaxMembers())
                .status(room.getStatus().name())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
