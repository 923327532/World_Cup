package group_service.service;

import group_service.dto.*;
import group_service.entity.*;
import group_service.exception.ResourceNotFoundException;
import group_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository memberRepository;
    private final RoomInviteRepository inviteRepository;
    private final UserReportRepository reportRepository;
    private final RoomBanRepository banRepository;

    // ========== ROOMS ==========

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomResponse::fromEntity)
                .toList();
    }

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        return RoomResponse.fromEntity(room);
    }

    @Transactional
    public RoomResponse createRoom(RoomRequest request, Long userId) {
        Room room = new Room();
        room.setName(request.getName());
        room.setDescription(request.getDescription());
        room.setMaxMembers(request.getMaxMembers());
        room.setCreatedBy(userId);
        room.setStatus(Room.RoomStatus.ACTIVE);
        room.setInviteCode(generateInviteCode());

        Room saved = roomRepository.save(room);

        // Add creator as OWNER
        RoomMember owner = new RoomMember();
        owner.setRoomId(saved.getId());
        owner.setUserId(userId);
        owner.setRole(RoomMember.MemberRole.OWNER);
        memberRepository.save(owner);

        log.info("Room created: id={} name={} by userId={}", saved.getId(), request.getName(), userId);
        return RoomResponse.fromEntity(saved);
    }

    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request, Long userId) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));

        validateMemberRole(room.getId(), userId, RoomMember.MemberRole.ADMIN);

        room.setName(request.getName());
        room.setDescription(request.getDescription());
        room.setMaxMembers(request.getMaxMembers());

        Room saved = roomRepository.save(room);
        log.info("Room updated: id={} by userId={}", id, userId);
        return RoomResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteRoom(Long id, Long userId) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));

        validateMemberRole(room.getId(), userId, RoomMember.MemberRole.OWNER);

        room.setStatus(Room.RoomStatus.ARCHIVED);
        roomRepository.save(room);
        log.info("Room archived: id={} by userId={}", id, userId);
    }

    public RoomResponse joinByCode(String inviteCode, Long userId) {
        Room room = roomRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with code: " + inviteCode));

        if (memberRepository.existsByRoomIdAndUserId(room.getId(), userId)) {
            throw new IllegalStateException("User is already a member of this room");
        }

        if (banRepository.existsByRoomIdAndUserId(room.getId(), userId)) {
            throw new IllegalStateException("User is banned from this room");
        }

        if (room.getMaxMembers() != null && memberRepository.countByRoomId(room.getId()) >= room.getMaxMembers()) {
            throw new IllegalStateException("Room is full");
        }

        RoomMember member = new RoomMember();
        member.setRoomId(room.getId());
        member.setUserId(userId);
        member.setRole(RoomMember.MemberRole.MEMBER);
        memberRepository.save(member);

        log.info("User {} joined room {} via code", userId, room.getId());
        return RoomResponse.fromEntity(room);
    }

    // ========== MEMBERS ==========

    public List<MemberResponse> getRoomMembers(Long roomId) {
        return memberRepository.findByRoomId(roomId).stream()
                .map(MemberResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void removeMember(Long roomId, Long memberUserId, Long requesterId) {
        validateMemberRole(roomId, requesterId, RoomMember.MemberRole.ADMIN);

        RoomMember member = memberRepository.findByRoomIdAndUserId(roomId, memberUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in room"));

        if (member.getRole() == RoomMember.MemberRole.OWNER) {
            throw new IllegalStateException("Cannot remove the room owner");
        }

        memberRepository.delete(member);
        log.info("User {} removed from room {} by {}", memberUserId, roomId, requesterId);
    }

    @Transactional
    public void updateMemberRole(Long roomId, Long memberUserId, String newRole, Long requesterId) {
        validateMemberRole(roomId, requesterId, RoomMember.MemberRole.OWNER);

        RoomMember member = memberRepository.findByRoomIdAndUserId(roomId, memberUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in room"));

        member.setRole(RoomMember.MemberRole.valueOf(newRole.toUpperCase()));
        memberRepository.save(member);
        log.info("User {} role updated to {} in room {} by {}", memberUserId, newRole, roomId, requesterId);
    }

    // ========== INVITES ==========

    public List<InviteResponse> getRoomInvites(Long roomId) {
        return inviteRepository.findByRoomId(roomId).stream()
                .map(InviteResponse::fromEntity)
                .toList();
    }

    @Transactional
    public InviteResponse createInvite(Long roomId, InviteRequest request, Long userId) {
        validateMemberRole(roomId, userId, RoomMember.MemberRole.ADMIN);

        RoomInvite invite = new RoomInvite();
        invite.setRoomId(roomId);
        invite.setInvitedUserId(request.getInvitedUserId());
        invite.setInvitedEmail(request.getInvitedEmail());
        invite.setInvitedBy(userId);
        invite.setToken(UUID.randomUUID().toString());
        invite.setStatus(RoomInvite.InviteStatus.PENDING);
        invite.setExpiresAt(LocalDateTime.now().plusDays(7));

        RoomInvite saved = inviteRepository.save(invite);
        log.info("Invite created for room {} by userId={}", roomId, userId);
        return InviteResponse.fromEntity(saved);
    }

    @Transactional
    public InviteResponse respondToInvite(Long inviteId, String response, Long userId) {
        RoomInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Invite", inviteId));

        if (invite.getInvitedUserId() != null && !invite.getInvitedUserId().equals(userId)) {
            throw new IllegalStateException("This invite is not for you");
        }

        if (invite.getStatus() != RoomInvite.InviteStatus.PENDING) {
            throw new IllegalStateException("Invite is no longer pending");
        }

        RoomInvite.InviteStatus newStatus = RoomInvite.InviteStatus.valueOf(response.toUpperCase());
        invite.setStatus(newStatus);
        invite.setRespondedAt(LocalDateTime.now());

        if (newStatus == RoomInvite.InviteStatus.ACCEPTED) {
            RoomMember member = new RoomMember();
            member.setRoomId(invite.getRoomId());
            member.setUserId(userId);
            member.setRole(RoomMember.MemberRole.MEMBER);
            memberRepository.save(member);
        }

        RoomInvite saved = inviteRepository.save(invite);
        log.info("Invite {} responded with {} by userId={}", inviteId, response, userId);
        return InviteResponse.fromEntity(saved);
    }

    // ========== REPORTS ==========

    public List<ReportResponse> getRoomReports(Long roomId) {
        return reportRepository.findByRoomId(roomId).stream()
                .map(ReportResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ReportResponse createReport(Long roomId, ReportRequest request, Long userId) {
        if (!memberRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new IllegalStateException("You must be a member to report users");
        }

        UserReport report = new UserReport();
        report.setRoomId(roomId);
        report.setReportedBy(userId);
        report.setReportedUserId(request.getReportedUserId());
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setStatus(UserReport.ReportStatus.PENDING);

        UserReport saved = reportRepository.save(report);
        log.info("Report created in room {} by userId={} against userId={}", roomId, userId, request.getReportedUserId());
        return ReportResponse.fromEntity(saved);
    }

    @Transactional
    public ReportResponse resolveReport(Long reportId, String resolution, String note, Long adminId) {
        UserReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report", reportId));

        report.setStatus(UserReport.ReportStatus.valueOf(resolution.toUpperCase()));
        report.setResolvedBy(adminId);
        report.setResolutionNote(note);
        report.setResolvedAt(LocalDateTime.now());

        UserReport saved = reportRepository.save(report);
        log.info("Report {} resolved as {} by adminId={}", reportId, resolution, adminId);
        return ReportResponse.fromEntity(saved);
    }

    // ========== BANS ==========

    public List<BanResponse> getRoomBans(Long roomId) {
        return banRepository.findByRoomId(roomId).stream()
                .map(BanResponse::fromEntity)
                .toList();
    }

    @Transactional
    public BanResponse banUser(Long roomId, Long userIdToBan, String reason, boolean permanent, Long requesterId) {
        validateMemberRole(roomId, requesterId, RoomMember.MemberRole.ADMIN);

        if (banRepository.existsByRoomIdAndUserId(roomId, userIdToBan)) {
            throw new IllegalStateException("User is already banned from this room");
        }

        RoomBan ban = new RoomBan();
        ban.setRoomId(roomId);
        ban.setUserId(userIdToBan);
        ban.setBannedBy(requesterId);
        ban.setReason(reason);
        ban.setIsPermanent(permanent);
        if (!permanent) {
            ban.setExpiresAt(LocalDateTime.now().plusDays(30));
        }

        RoomBan saved = banRepository.save(ban);

        // Remove member if they are in the room
        memberRepository.findByRoomIdAndUserId(roomId, userIdToBan)
                .ifPresent(memberRepository::delete);

        log.info("User {} banned from room {} by userId={}", userIdToBan, roomId, requesterId);
        return BanResponse.fromEntity(saved);
    }

    @Transactional
    public void unbanUser(Long roomId, Long userIdToUnban, Long requesterId) {
        validateMemberRole(roomId, requesterId, RoomMember.MemberRole.ADMIN);

        RoomBan ban = banRepository.findByRoomIdAndUserId(roomId, userIdToUnban)
                .orElseThrow(() -> new ResourceNotFoundException("Ban not found"));

        banRepository.delete(ban);
        log.info("User {} unbanned from room {} by userId={}", userIdToUnban, roomId, requesterId);
    }

    // ========== HELPERS ==========

    private void validateMemberRole(Long roomId, Long userId, RoomMember.MemberRole requiredRole) {
        RoomMember member = memberRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalStateException("User is not a member of this room"));

        boolean isOwner = member.getRole() == RoomMember.MemberRole.OWNER;
        boolean isAdmin = member.getRole() == RoomMember.MemberRole.ADMIN;

        if (requiredRole == RoomMember.MemberRole.OWNER && !isOwner) {
            throw new IllegalStateException("Only the room owner can perform this action");
        }

        if (requiredRole == RoomMember.MemberRole.ADMIN && !(isOwner || isAdmin)) {
            throw new IllegalStateException("Only admins can perform this action");
        }
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
