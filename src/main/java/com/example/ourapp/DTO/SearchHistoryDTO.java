package com.example.ourapp.DTO;

import com.example.ourapp.entity.UserSearchHistory;
import com.example.ourapp.entity.UserSearchHistory.SaveType;

public class SearchHistoryDTO {
    private String locationName;
    private Double latitude;
    private Double longitude;
    private UserSearchHistory.SaveType saveType;

    // Getters and Setters
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public UserSearchHistory.SaveType getSaveType() {
        return saveType;
    }

    public void setSaveType(UserSearchHistory.SaveType saveType) {
        this.saveType = saveType;
    }
    
    @Override
    public String toString() {
        return "SearchHistoryDTO{" +
                "locationName='" + locationName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", saveType=" + saveType +
                '}';
    }
}
