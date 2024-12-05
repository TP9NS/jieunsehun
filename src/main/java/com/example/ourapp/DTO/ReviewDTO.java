package com.example.ourapp.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDTO {
    private Long userId;
    private String locationName;
    private int rating;
    private String reviewText;
    private LocalDateTime createdAt;

    public ReviewDTO(Long userId, String locationName, int rating, String reviewText, LocalDateTime createdAt) {
        this.userId = userId;
        this.locationName = locationName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
    }
}