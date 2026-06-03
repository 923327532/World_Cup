package gamification_service.service;

import gamification_service.dto.BadgeDTO;
import gamification_service.dto.UserBadgeDTO;
import gamification_service.entity.Badge;
import gamification_service.entity.UserBadge;
import gamification_service.repository.BadgeRepository;
import gamification_service.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public List<BadgeDTO> getAllBadges() {
        return badgeRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    public List<UserBadgeDTO> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserId(userId).stream()
            .map(this::toUserBadgeDTO)
            .toList();
    }

    private BadgeDTO toDTO(Badge badge) {
        return new BadgeDTO(badge.getId(), badge.getName(), badge.getDescription(), badge.getIcon());
    }

    private UserBadgeDTO toUserBadgeDTO(UserBadge userBadge) {
        return new UserBadgeDTO(
            userBadge.getId(),
            userBadge.getUserId(),
            toDTO(userBadge.getBadge()),
            userBadge.getEarnedAt()
        );
    }
}