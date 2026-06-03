package social_service.service;

import social_service.dto.CommentDTO;
import social_service.entity.Comment;
import social_service.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment createComment(Long userId, Long matchId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setMatchId(matchId);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public List<CommentDTO> getMatchComments(Long matchId) {
        return commentRepository.findByMatchIdOrderByCreatedAtDesc(matchId).stream()
            .map(this::toDTO)
            .toList();
    }

    public CommentDTO toDTO(Comment comment) {
        return new CommentDTO(
            comment.getId(),
            comment.getUserId(),
            comment.getMatchId(),
            comment.getContent(),
            comment.getCreatedAt()
        );
    }
}