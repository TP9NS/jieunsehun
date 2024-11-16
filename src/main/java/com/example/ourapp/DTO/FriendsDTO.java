package com.example.ourapp.DTO;

public class FriendsDTO {
    private Long friendId;  // 친구 ID
    private String friendName;  // 친구 이름

    public FriendsDTO(Long friendId, String friendName) {
        this.friendId = friendId;
        this.friendName = friendName;
    }

    // Getters and Setters
    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
