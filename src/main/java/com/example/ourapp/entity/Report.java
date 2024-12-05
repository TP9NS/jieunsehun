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

    @Enumerated(EnumType.STRING)
    private ReportType type; // 신고 타입 (POST 또는 COMMENT)

    private LocalDateTime reportedAt; // 신고 시간

    public Report() {
        this.reportedAt = LocalDateTime.now(); // 기본 신고 시간 설정
    }

    public Report(Long targetId, String reason, ReportType type) {
        this.targetId = targetId;
        this.reason = reason;
        this.type = type;
        this.reportedAt = LocalDateTime.now();
    }

    public enum ReportType {
        POST,   // 게시글 신고
        COMMENT // 댓글 신고
    }

}
