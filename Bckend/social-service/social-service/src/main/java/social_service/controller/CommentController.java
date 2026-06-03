package social_service.controller;

import social_service.dto.CommentDTO;
import social_service.dto.CreateCommentRequest;
import social_service.entity.Comment;
import social_service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
        @RequestHeader("X-User-Id") Long userId,
        @RequestBody CreateCommentRequest request
    ) {
        Comment comment = commentService.createComment(userId, request.getMatchId(), request.getContent());
        return ResponseEntity.ok(commentService.toDTO(comment));
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<CommentDTO>> getMatchComments(@PathVariable Long matchId) {
        return ResponseEntity.ok(commentService.getMatchComments(matchId));
    }
}