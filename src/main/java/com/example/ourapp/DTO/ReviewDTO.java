package com.example.ourapp.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDTO {
    private Long id;
    private Long userId;
    private String locationName;
    private int rating;
    private String reviewText;
    private LocalDateTime createdAt;
    private boolean editMode; // 수정 모드 여부를 나타내는 필드

    public ReviewDTO(Long id, Long userId, String locationName, int rating, String reviewText, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.locationName = locationName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
        this.editMode = false; // 기본값은 false로 설정
    }


    // 기본 생성자
    public ReviewDTO() {}
}