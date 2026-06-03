package group_service.repository;

import group_service.entity.RoomInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomInviteRepository extends JpaRepository<RoomInvite, Long> {
    List<RoomInvite> findByRoomId(Long roomId);
    List<RoomInvite> findByInvitedUserId(Long invitedUserId);
    List<RoomInvite> findByInvitedBy(Long invitedBy);
    Optional<RoomInvite> findByToken(String token);
    Optional<RoomInvite> findByRoomIdAndInvitedUserId(Long roomId, Long invitedUserId);
}
