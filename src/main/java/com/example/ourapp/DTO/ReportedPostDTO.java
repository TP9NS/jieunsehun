package com.example.ourapp.DTO;

import java.time.LocalDateTime;

public class ReportedPostDTO {
    private Long id;
    private String postTitle;
    private String username;
    private String reason;
    private LocalDateTime createdAt;

    // 기본 생성자
    public ReportedPostDTO() {}

    // 생성자 (HQL 쿼리에서 사용한 필드와 동일한 순서로 작성)
    public ReportedPostDTO(Long id, String postTitle, String username, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.postTitle = postTitle;
        this.username = username;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    // Getter와 Setter 추가
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}