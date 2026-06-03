package group_service.dto;

import group_service.entity.RoomInvite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteResponse {

    private Long id;
    private Long roomId;
    private Long invitedUserId;
    private String invitedEmail;
    private Long invitedBy;
    private String token;
    private String status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    public static InviteResponse fromEntity(RoomInvite invite) {
        return InviteResponse.builder()
                .id(invite.getId())
                .roomId(invite.getRoomId())
                .invitedUserId(invite.getInvitedUserId())
                .invitedEmail(invite.getInvitedEmail())
                .invitedBy(invite.getInvitedBy())
                .token(invite.getToken())
                .status(invite.getStatus().name())
                .expiresAt(invite.getExpiresAt())
                .createdAt(invite.getCreatedAt())
                .respondedAt(invite.getRespondedAt())
                .build();
    }
}
