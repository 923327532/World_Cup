package group_service.dto;

import group_service.entity.RoomMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private Long roomId;
    private Long userId;
    private String role;
    private LocalDateTime joinedAt;

    public static MemberResponse fromEntity(RoomMember member) {
        return MemberResponse.builder()
                .id(member.getId())
                .roomId(member.getRoomId())
                .userId(member.getUserId())
                .role(member.getRole().name())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
