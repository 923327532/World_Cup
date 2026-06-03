package gamification_service.service;

import gamification_service.dto.RewardDTO;
import gamification_service.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;

    public List<RewardDTO> getAllRewards() {
        return rewardRepository.findAll().stream()
            .map(r -> new RewardDTO(r.getId(), r.getPosition(), r.getTitle(), r.getDescription()))
            .toList();
    }
}