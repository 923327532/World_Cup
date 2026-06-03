package group_service.repository;

import group_service.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByInviteCode(String inviteCode);
    List<Room> findByCreatedBy(Long createdBy);
    List<Room> findByStatus(Room.RoomStatus status);
}
