package com.example.ourapp.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ourapp.DTO.ReportedCommentDTO;
import com.example.ourapp.DTO.ReportedPostDTO;
import com.example.ourapp.entity.Report;
import com.example.ourapp.entity.Report.ReportType;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // 특정 타입의 신고 내역 조회 (POST 또는 COMMENT)
    List<Report> findByType(ReportType type);

    // 특정 대상 ID의 신고 내역 조회
    List<Report> findByTargetId(Long targetId);
    
    @Query("SELECT new com.example.dto.ReportedPostDTO(r.post.id, r.post.title, r.post.username, r.reason, r.createdAt) " +
    	       "FROM Report r WHERE r.type = 'POST'")
    	List<ReportedPostDTO> findAllReportedPosts();


     @Query("SELECT new com.example.dto.ReportedCommentDTO(r.id, c.content, c.username, r.reason, r.createdAt) " +
            "FROM Report r JOIN Comment c ON r.targetId = c.id WHERE r.type = :type")
     List<ReportedCommentDTO> findAllReportedComments();

     @Query("SELECT new com.example.dto.ReportedPostDTO(r.id, p.title, p.username, r.reason, r.createdAt) " +
            "FROM Report r JOIN Post p ON r.targetId = p.id WHERE r.type = :type AND p.username = :username")
     List<ReportedPostDTO> findReportsByUsername(@Param("username") String username, @Param("type") ReportType type);

     @Query("SELECT new com.example.dto.ReportedPostDTO(r.id, p.title, p.username, r.reason, r.createdAt) " +
            "FROM Report r JOIN Post p ON r.targetId = p.id WHERE r.type = :type AND r.reason LIKE %:reason%")
     List<ReportedPostDTO> findReportsByReason(@Param("reason") String reason, @Param("type") ReportType type);

     void deleteByTargetIdAndType(Long targetId, ReportType type);
}
