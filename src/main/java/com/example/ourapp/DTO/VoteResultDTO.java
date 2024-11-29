package com.example.ourapp.DTO;

public class VoteResultDTO {
    private String locationName;
    private int voteCount;
    private String imageName;
    private int rank; // 순위 추가

    public VoteResultDTO(String locationName, int voteCount, String imageName) {
        this.locationName = locationName;
        this.voteCount = voteCount;
        this.imageName = imageName;
    }

    // Getters and Setters
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
