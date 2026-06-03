package social_service.controller;

import social_service.dto.CreateReactionRequest;
import social_service.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping
    public ResponseEntity<Void> addReaction(
        @RequestHeader("X-User-Id") Long userId,
        @RequestBody CreateReactionRequest request
    ) {
        reactionService.addReaction(userId, request.getCommentId(), request.getReaction());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> removeReaction(
        @RequestHeader("X-User-Id") Long userId,
        @PathVariable Long commentId
    ) {
        reactionService.removeReaction(userId, commentId);
        return ResponseEntity.ok().build();
    }
}