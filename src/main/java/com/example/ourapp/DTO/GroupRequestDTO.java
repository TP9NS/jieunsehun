package com.example.ourapp.DTO;

import com.example.ourapp.entity.GroupRequest;

public class GroupRequestDTO {

    private Long id;
    private String groupName;
    private String userName;

    public GroupRequestDTO(GroupRequest groupRequest) {
        this.id = groupRequest.getId();
        this.groupName = groupRequest.getGroup().getGroupName();
        this.userName = groupRequest.getUser().getUsername();
    }

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
