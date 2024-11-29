package com.example.ourapp.DTO;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GroupMapPointDTO {
	private Long userId;
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
    private String markerColor;
    
    public String getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(String markerColor) {
        this.markerColor = markerColor;
    }

    public Long getUserId() {
        return userId;
    }

    // Setter 메서드
    public void setUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        this.userId = userId;
    }
    

 // Getter 메서드
 public String getLocationName() {
     return locationName;
 }

 // Setter 메서드
 public void setLocationName(String locationName) {
     if (locationName == null || locationName.trim().isEmpty()) {
         throw new IllegalArgumentException("Location name cannot be null or empty.");
     }
     this.locationName = locationName;
 }
}