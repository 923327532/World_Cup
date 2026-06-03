package group_service.repository;

import group_service.entity.RoomBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomBanRepository extends JpaRepository<RoomBan, Long> {
    List<RoomBan> findByRoomId(Long roomId);
    List<RoomBan> findByUserId(Long userId);
    Optional<RoomBan> findByRoomIdAndUserId(Long roomId, Long userId);
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);
}
