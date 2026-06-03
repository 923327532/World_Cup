package group_service.dto;

import group_service.entity.RoomBan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BanResponse {

    private Long id;
    private Long roomId;
    private Long userId;
    private Long bannedBy;
    private String reason;
    private LocalDateTime expiresAt;
    private Boolean isPermanent;
    private LocalDateTime createdAt;

    public static BanResponse fromEntity(RoomBan ban) {
        return BanResponse.builder()
                .id(ban.getId())
                .roomId(ban.getRoomId())
                .userId(ban.getUserId())
                .bannedBy(ban.getBannedBy())
                .reason(ban.getReason())
                .expiresAt(ban.getExpiresAt())
                .isPermanent(ban.getIsPermanent())
                .createdAt(ban.getCreatedAt())
                .build();
    }
}
