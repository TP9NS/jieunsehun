package com.example.ourapp.DTO;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.ourapp.entity.Group;
import com.example.ourapp.entity.GroupMember;

@Getter
@Setter
public class GroupDTO {

    private Long groupId; // 그룹 ID
    private String groupName; // 그룹 이름
    private String createdBy; // 생성자 이름
    private Long createdById; // 생성자 ID
    private String description;
    private List<MemberPermissionDTO> members; // 구성원과 권한 정보
    private LocalDateTime createdDate; // 생성일자
    private Integer myPermission;
    public GroupDTO() {}
    // Group -> GroupDTO 변환
    public GroupDTO(Group group) {
        this.groupId = group.getGroupId();
        this.groupName = group.getGroupName();
        this.createdBy = group.getCreatedBy().getUsername();
        this.createdById = group.getCreatedBy().getUserId();
        this.createdDate = group.getCreatedDate();
        this.description =group.getDescription();
        // 구성원 리스트를 MemberPermissionDTO로 변환
        this.members = group.getMembers().stream()
                .map(MemberPermissionDTO::new)
                .collect(Collectors.toList());
    }
    public GroupDTO(Group group, Integer myPermission) {
        this.groupId = group.getGroupId();
        this.groupName = group.getGroupName();
        this.description = group.getDescription();
        this.myPermission = myPermission; // 나의 권한 설정
    }
    @Getter
    @Setter
    public static class MemberPermissionDTO {
        private Long userId; // 사용자 ID
        private String username; // 사용자 이름
        private Integer permission; // 권한 (1: 소유자, 2: 관리자, 3: 일반)

        public MemberPermissionDTO(GroupMember groupMember) {
            this.userId = groupMember.getUser().getUserId();
            this.username = groupMember.getUser().getUsername();
            this.permission = groupMember.getPermission();
        }
    }
}
