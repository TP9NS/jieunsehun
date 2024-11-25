package com.example.ourapp.DTO;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GroupMapPointDTO {
    private String category;
    private String locationName;
    private String locationDesc;
    private String locationAlias;
    private Double latitude;
    private Double longitude;
    private String address;
    private String phone;
    private Long groupId;
    private LocalDateTime searchTime;
    private String addedBy; // 추가한 사용자 정보 추가
}