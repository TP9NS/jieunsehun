package com.example.ourapp.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AdminMapPoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String category;
    private String locationName;
    private String locationAlias;
    private String locationDesc;
    private Double latitude;
    private Double longitude;
    private String address;
    private String phone;
    private LocalDateTime searchTime;
    
    private String topic; // 주제 정보
    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    // 기본 생성자
    public AdminMapPoint() {}

    // 필요한 경우 매개변수가 있는 생성자 추가
}
