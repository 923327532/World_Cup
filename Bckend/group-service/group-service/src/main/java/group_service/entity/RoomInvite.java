package group_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_invites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "invited_user_id")
    private Long invitedUserId;

    @Column(name = "invited_email", length = 150)
    private String invitedEmail;

    @Column(name = "invited_by", nullable = false)
    private Long invitedBy;

    @Column(name = "token", unique = true, length = 100)
    private String token;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private InviteStatus status;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = InviteStatus.PENDING;
        }
    }

    public enum InviteStatus {
        PENDING, ACCEPTED, DECLINED, EXPIRED, CANCELLED
    }
}
