package com.example.ourapp.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDTO {
    private Long id;
    private Long targetId;
    private String reason;
    private String type;
    private String reportedAt; // 포맷된 시간
    private String username; // 신고자 이름
    private Long reportedBy;
    private boolean isHidden; // 숨김 여부 추가
    private Long postId;
    private Long userId;

    // 생성자
    public ReportDTO(Long id, Long targetId, String reason, String type, String reportedAt, String username, Long reportedBy, boolean isHidden, Long postId,Long userId) {
        this.id = id;
        this.targetId = targetId;
        this.reason = reason;
        this.type = type;
        this.reportedAt = reportedAt;
        this.username = username;
        this.reportedBy = reportedBy;
        this.isHidden = isHidden; // 숨김 여부 초기화
        this.postId = postId;
        this.userId = userId;
    }

    // Getter와 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReportedAt() { return reportedAt; }
    public void setReportedAt(String reportedAt) { this.reportedAt = reportedAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public Long getReportedBy() { return reportedBy; }
    public void setReportedBy(Long reportedBy) { this.reportedBy = reportedBy; }

    public boolean isHidden() { return isHidden; } // Getter for isHidden
    public void setHidden(boolean hidden) { this.isHidden = hidden; } // Setter for isHidden

    // 기존의 Getter와 Setter들
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
