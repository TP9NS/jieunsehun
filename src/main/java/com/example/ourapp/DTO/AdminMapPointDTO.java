package com.example.ourapp.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdminMapPointDTO {
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
    private String topic; // 관리자가 입력한 주제
}
