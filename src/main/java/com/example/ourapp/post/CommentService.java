package com.example.ourapp.post;

import com.example.ourapp.entity.Comment;
import com.example.ourapp.entity.Report;
import com.example.ourapp.post.CommentRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    
    public CommentService(CommentRepository commentRepository, ReportRepository reportRepository) {
        this.commentRepository = commentRepository;
        this.reportRepository = reportRepository;
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

    /**
     * 댓글 삭제
     * @param commentId 삭제할 댓글 ID
     */
    @Transactional
    public void deleteComment(Long commentId) {
        // 연관된 신고 기록 삭제
        reportRepository.deleteByTargetId(commentId, Report.ReportType.COMMENT);

        // 댓글 삭제
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + commentId));
        commentRepository.delete(comment);
    }


    /**
     * 댓글 수정
     * @param commentId 수정할 댓글 ID
     * @param newContent 수정된 댓글 내용
     */
    public void updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        comment.setContent(newContent); // 수정된 내용 설정
        commentRepository.save(comment); // 수정된 댓글 저장
    }

    /**
     * 특정 게시글의 댓글 수 계산
     * @param postId 게시글 ID
     * @return 댓글 수
     */
    public int countCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).size();
    }
    
    public void hideComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        comment.setHidden(true); // 댓글 숨김 처리
        commentRepository.save(comment); // 변경 사항 저장
    }

    public Long getPostIdByCommentId(Long commentId) {
        // 댓글 ID로 게시글 ID 조회
        return reportRepository.findPostIdByCommentId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
    }

    public void unhideComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        comment.setHidden(false); // 숨김 상태 해제
        commentRepository.save(comment);
    }


}

