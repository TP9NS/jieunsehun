package com.example.ourapp.post;

import com.example.ourapp.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId); // 특정 게시글의 댓글 조회
    @Query("SELECT c.post.id FROM Comment c WHERE c.id = :commentId")
    Optional<Long> findPostIdByCommentId(@Param("commentId") Long commentId);
}
