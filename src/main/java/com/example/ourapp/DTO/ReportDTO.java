package com.example.ourapp.DTO;

public class ReportDTO {
    private Long id;
    private Long targetId;
    private String reason;
    private String type;
    private String reportedAt; // 포맷된 시간
    private String username; // 신고자 이름
    private Long reportedBy;

    // 생성자
    public ReportDTO(Long id, Long targetId, String reason, String type, String reportedAt, String username, Long reportedBy) {
        this.id = id;
        this.targetId = targetId;
        this.reason = reason;
        this.type = type;
        this.reportedAt = reportedAt;
        this.username = username;
        this.reportedBy = reportedBy;
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
}
