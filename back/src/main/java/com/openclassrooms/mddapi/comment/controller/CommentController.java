package com.openclassrooms.mddapi.comment.controller;

import com.openclassrooms.mddapi.comment.dto.CommentResponse;
import com.openclassrooms.mddapi.comment.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles/{articleId}/comments")
public class CommentController {
    private final CommentService commentService;
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long articleId,
            @Valid @RequestBody CreateCommentRequest commentRequest,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(commentRequest, userId, articleId));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long articleId
    ) {
        return ResponseEntity.ok(
                commentService.getCommentsByArticleId(articleId)
        );
    }
}
