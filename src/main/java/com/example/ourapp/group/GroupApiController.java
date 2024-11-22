package com.example.ourapp.group;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ourapp.DTO.GroupMemberDTO;
import com.example.ourapp.entity.GroupMember;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupApiController {

    private final GroupService groupService;

    @GetMapping("/{groupId}/members")
    public List<GroupMemberDTO> getGroupMembers(@PathVariable Long groupId) {
        // 그룹 구성원 목록 조회
        List<GroupMember> members = groupService.findGroupMembers(groupId);

        // DTO 변환 후 반환
        return members.stream()
                .map(GroupMemberDTO::new)
                .collect(Collectors.toList());
    }
}
