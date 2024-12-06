package com.example.ourapp.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ourapp.entity.Report;
import com.example.ourapp.entity.Report.ReportType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // 특정 타입의 신고 내역 조회 (POST 또는 COMMENT)
    List<Report> findByType(ReportType type);

    // 특정 대상 ID의 신고 내역 조회
    List<Report> findByTargetId(Long targetId);
    
    @Query("SELECT c.post.id FROM Comment c WHERE c.id = :commentId")
    Optional<Long> findPostIdByCommentId(@Param("commentId") Long commentId);
    
    
 // ReportRepository
    @Query("SELECT r FROM Report r JOIN Post p ON r.targetId = p.id WHERE r.type = :type AND p.hidden = :hidden")
    List<Report> findReportsByPostHidden(@Param("type") Report.ReportType type, @Param("hidden") boolean hidden);

    @Query("SELECT r FROM Report r JOIN Comment c ON r.targetId = c.id WHERE r.type = :type AND c.hidden = :hidden")
    List<Report> findReportsByCommentHidden(@Param("type") Report.ReportType type, @Param("hidden") boolean hidden);





}
