package social_service.service;

import social_service.entity.Comment;
import social_service.entity.CommentReaction;
import social_service.repository.CommentReactionRepository;
import social_service.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final CommentReactionRepository reactionRepository;
    private final CommentRepository commentRepository;

    public CommentReaction addReaction(Long userId, Long commentId, String reaction) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if user already reacted
        reactionRepository.findByCommentIdAndUserId(commentId, userId)
            .ifPresent(existing -> {
                reactionRepository.delete(existing);
            });

        CommentReaction newReaction = new CommentReaction();
        newReaction.setComment(comment);
        newReaction.setUserId(userId);
        newReaction.setReaction(reaction);

        return reactionRepository.save(newReaction);
    }

    public void removeReaction(Long userId, Long commentId) {
        reactionRepository.findByCommentIdAndUserId(commentId, userId)
            .ifPresent(reactionRepository::delete);
    }
}