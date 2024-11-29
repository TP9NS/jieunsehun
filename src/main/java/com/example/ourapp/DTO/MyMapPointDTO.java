package com.example.ourapp.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Getter
@Setter
public class MyMapPointDTO {
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
    private String markerColor;
    
    public String getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(String markerColor) {
        this.markerColor = markerColor;
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
