package com.example.ourapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private Long userId;

    private String locationName;

    private int rating;

    @Column(length = 500)
    private String reviewText;

    private LocalDateTime createdAt = LocalDateTime.now(); // 기본값 현재 시간
}
