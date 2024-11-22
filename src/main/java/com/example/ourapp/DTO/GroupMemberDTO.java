package com.example.ourapp.DTO;

import com.example.ourapp.entity.GroupMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupMemberDTO {

    private Long id;  // GroupMember ID
    private Long userId;  // 사용자 ID
    private String username;  // 사용자 이름
    private Long groupId;  // 그룹 ID
    private String groupName;  // 그룹 이름
    private Integer permission;  // 권한 (1: 소유자, 2: 관리자, 3: 일반)

 // Constructor to convert from GroupMember entity to DTO
    public GroupMemberDTO(GroupMember groupMember) {
        this.id = groupMember.getId();
        this.userId = groupMember.getUser() != null ? groupMember.getUser().getUserId() : null; // Null check added
        this.username = groupMember.getUser() != null ? groupMember.getUser().getUsername() : "Unknown"; // Null check added
        this.groupId = groupMember.getGroup() != null ? groupMember.getGroup().getGroupId() : null; // Null check added
        this.groupName = groupMember.getGroup() != null ? groupMember.getGroup().getGroupName() : "Unknown"; // Null check added
        this.permission = groupMember.getPermission();
    }

    public String getUserName() {
        return username;
    }
    public void setUserName(String userName) {
        this.username = userName;
    }

    public Integer getPermission() {
        return permission;
    }
    public void setPermission(Integer permission) {
        this.permission = permission;
    }
    
    @Override
    public String toString() {
        return "GroupMemberDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", permission=" + permission +
                '}';
    }
}