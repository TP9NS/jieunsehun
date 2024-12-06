package com.example.ourapp.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long targetId; // 신고 대상 ID (게시글 ID 또는 댓글 ID)
    private String reason; // 신고 사유
    private Long postId;

    @Enumerated(EnumType.STRING)
    private ReportType type; // 신고 타입 (POST 또는 COMMENT)

    private LocalDateTime reportedAt; // 신고 시간
    
    private Long reportedBy;
    
    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden = false;

    public Report() {
        this.reportedAt = LocalDateTime.now(); // 기본 신고 시간 설정
    }

    public Report(Long targetId, String reason, ReportType type, Long reportedBy, Long postId) {
        this.targetId = targetId;
        this.reason = reason;
        this.type = type;
        this.reportedBy = reportedBy;
        this.reportedAt = LocalDateTime.now();
        this.isHidden = false; // 기본값 설정
        this.postId = postId;
    }


    public enum ReportType {
        POST,   // 게시글 신고
        COMMENT // 댓글 신고
    }
    
    @Transient
    private String formattedReportedAt;

    public String getFormattedReportedAt() {
        return formattedReportedAt;
    }

    public void setFormattedReportedAt(String formattedReportedAt) {
        this.formattedReportedAt = formattedReportedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTargetId() {
        return targetId;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public ReportType getType() {
        return type;
    }
    public Long getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(Long reportedBy) {
        this.reportedBy = reportedBy;
    }
    // Getter & Setter
    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
