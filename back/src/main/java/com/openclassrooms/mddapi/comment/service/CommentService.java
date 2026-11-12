package com.openclassrooms.mddapi.comment.service;

import com.openclassrooms.mddapi.article.entity.Article;
import com.openclassrooms.mddapi.article.repository.ArticleRepository;
import com.openclassrooms.mddapi.comment.dto.CommentResponse;
import com.openclassrooms.mddapi.comment.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.comment.entity.Comment;
import com.openclassrooms.mddapi.comment.repository.CommentRepository;
import com.openclassrooms.mddapi.common.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.user.entity.User;
import com.openclassrooms.mddapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(CreateCommentRequest request, Long userId, Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setArticle(article);
        comment.setAuthor(user);
        Comment savedComment = commentRepository.save(comment);
        return mapToCommentResponse(savedComment);
    }

    @Transactional
    public List<CommentResponse> getCommentsByArticleId(Long articleId) {
        List<Comment> comments = commentRepository.findByArticleIdOrderByCreatedAtAsc(articleId);
        return comments.stream()
                .map(this::mapToCommentResponse)
                .toList();
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getUsername(),
                comment.getCreatedAt()
        );
    }
}
