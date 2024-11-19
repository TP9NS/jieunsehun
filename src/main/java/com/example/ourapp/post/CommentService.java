package com.example.ourapp.post;

import com.example.ourapp.entity.Comment;
import com.example.ourapp.post.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * 특정 게시글의 댓글 조회
     * @param postId 게시글 ID
     * @return 댓글 리스트
     */
    public List<Comment> findCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }
    
    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));
    }


    /**
     * 댓글 저장
     * @param comment 저장할 댓글 객체
     * @return 저장된 댓글
     */
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }
        commentRepository.deleteById(commentId);
    }

    
    public void updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        comment.setContent(newContent); // 수정된 내용 설정
        commentRepository.save(comment); // 수정된 댓글 저장
    }



}
