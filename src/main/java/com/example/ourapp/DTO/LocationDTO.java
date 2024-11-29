package com.example.ourapp.DTO;

public class LocationDTO {
    private String locationName;
    private String address;
    private String category;
    private String locationAlias;
    private String imageName; // 이미지 파일 이름

    // MyMapPointDTO에서 변환하는 생성자
    public LocationDTO(MyMapPointDTO point) {
        this.locationName = point.getLocationName();
        this.address = point.getAddress();
        this.category = point.getCategory();
        this.locationAlias = point.getLocationAlias();
        this.imageName = generateImageName(point.getLocationName());
    }

    // GroupMapPointDTO에서 변환하는 생성자
    public LocationDTO(GroupMapPointDTO point) {
        this.locationName = point.getLocationName();
        this.address = point.getAddress();
        this.category = point.getCategory();
        this.locationAlias = point.getLocationAlias();
        this.imageName = generateImageName(point.getLocationName());
    }

    // 이미지 이름 생성 메서드
    private String generateImageName(String locationName) {
        // 공백을 "-"로 대체하여 파일 이름으로 사용
        return locationName.replace(" ", "-") + ".jpg";
    }

    // Getters & Setters
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocationAlias() {
        return locationAlias;
    }

    public void setLocationAlias(String locationAlias) {
        this.locationAlias = locationAlias;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
