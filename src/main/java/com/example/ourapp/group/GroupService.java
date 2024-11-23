package com.example.ourapp.group;

import com.example.ourapp.DTO.GroupRequestDTO;
import com.example.ourapp.DTO.UserDTO;
import com.example.ourapp.entity.Group;
import com.example.ourapp.entity.GroupMember;
import com.example.ourapp.entity.GroupRequest;
import com.example.ourapp.entity.GroupRequest.RequestStatus;
import com.example.ourapp.entity.User;
import com.example.ourapp.user.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final GroupMemberRepository groupMemberRepository;
    
    @Transactional
    public List<Group> searchGroups(String keyword) {
        List<Group> groups = groupRepository.findByGroupNameContainingIgnoreCase(keyword);

        // 그룹 검색 시마다 그룹 이름과 관련 정보를 출력
        groups.forEach(group -> {
            // 그룹 정보 출력
            System.out.println("Group Name: " + group.getGroupName());
            System.out.println("Created By: " + (group.getCreatedBy() != null ? group.getCreatedBy().getUsername() : "Unknown"));
            System.out.println("Created Date: " + group.getCreatedDate());
            
            // Lazy Loading 강제 초기화
            if (group.getCreatedBy() != null) {
                group.getCreatedBy().getUsername(); // Lazy 필드 강제 로드
            }
        });
        
        return groups;
    }

    @Transactional
    public Group createGroup(String groupName, String description, User createdBy) {
        // 그룹 엔티티 생성
        Group group = new Group();
        group.setGroupName(groupName);
        group.setCreatedBy(createdBy);
        group.setDescription(description);
        group.setCreatedDate(LocalDateTime.now());
        // GroupMember 추가 (그룹 생성자는 기본적으로 소유자 권한을 가짐)
        GroupMember owner = new GroupMember();
        owner.setGroup(group);
        owner.setUser(createdBy);
        owner.setPermission(1); // 1: 소유자
        group.setMembers(List.of(owner));

        // 그룹 저장
        return groupRepository.save(group);
    }
    public Integer getMyPermission(Group group, Long userId) {
        return group.getMembers().stream()
                    .filter(member -> member.getUser().getUserId().equals(userId))
                    .map(GroupMember::getPermission)
                    .findFirst()
                    .orElse(3); // 기본값: 일반 사용자 (3)
    }
    public List<Group> findGroupsByUser(Long userId) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 사용자가 생성한 그룹
        List<Group> groupsCreatedByUser = groupRepository.findByCreatedBy(user);

        // 사용자가 소속된 그룹
        List<Group> groupsUserIsMemberOf = groupMemberRepository.findByUser(user).stream()
                .map(GroupMember::getGroup) // GroupMember에서 Group 추출
                .distinct() // 중복 제거
                .collect(Collectors.toList());

        // 모든 그룹 합치기
        Set<Group> allGroups = new HashSet<>(groupsCreatedByUser);
        allGroups.addAll(groupsUserIsMemberOf);

        // 로그 출력
        allGroups.forEach(group -> {
            System.out.println("Group ID: " + group.getGroupId());
            System.out.println("Group Name: " + group.getGroupName());
        });

        // 결과 반환
        return new ArrayList<>(allGroups);
    }
 // 그룹 ID로 그룹 조회
    public Group findGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));
    }
    public List<GroupMember> findGroupMembers(Long groupId) {
        // 그룹이 존재하는지 확인
        groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        // 그룹의 멤버 조회
        return groupMemberRepository.findByGroup_GroupId(groupId);
    }
 // 그룹 신청 처리
    public void applyForGroup(Long groupId, UserDTO userDTO) {
        // UserDTO에서 userId를 바로 사용할 수 있음
        Long userId = userDTO.getUser_id(); // UserDTO에서 userId를 가져옵니다.

        // 해당 userId에 해당하는 User 객체를 DB에서 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        // 이미 신청한 그룹인지 확인
        boolean alreadyApplied = groupRequestRepository.findByUserAndStatus(user, GroupRequest.RequestStatus.대기).stream()
                .anyMatch(gr -> gr.getGroup().equals(group));

        if (alreadyApplied) {
            throw new IllegalArgumentException("이미 신청한 그룹입니다.");
        }

        // 그룹 신청 생성
        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setUser(user);
        groupRequest.setGroup(group);
        groupRequest.setStatus(GroupRequest.RequestStatus.대기);

        groupRequestRepository.save(groupRequest);
    }
    
 // 그룹 신청 목록 가져오기 (그룹 소유자가 관리하는 그룹에 대한 신청)
    public List<GroupRequest> findRequestsForGroups(List<Group> groupsOwned) {
        return groupRequestRepository.findByGroupInAndStatus(groupsOwned, GroupRequest.RequestStatus.대기);
    }

    

 // 그룹 신청 상태 수락/거절
    public void updateGroupRequestStatus(Long requestId, GroupRequest.RequestStatus status) {
        GroupRequest groupRequest = groupRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        groupRequest.setStatus(status);
        groupRequestRepository.save(groupRequest);
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(groupRequest.getGroup()); // 신청된 그룹
        groupMember.setUser(groupRequest.getUser());   // 신청한 사용자
        groupMember.setPermission(3);                 // 기본 권한: 일반 사용자 (3)

            // 저장
        groupMemberRepository.save(groupMember);
        
    }
    @Transactional
    public void acceptGroupRequest(Long requestId) {
        // 1. 요청된 그룹 신청 조회
        GroupRequest groupRequest = groupRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        // 2. 신청한 사용자의 그룹 정보 조회
        Group group = groupRequest.getGroup();
        User user = groupRequest.getUser();

        // 3. 그룹 신청 상태를 '수락'으로 변경
        groupRequest.setStatus(GroupRequest.RequestStatus.수락); // 수락 상태로 변경
        groupRequestRepository.save(groupRequest);

        // 4. 그룹에 신청자를 '일반 사용자 (3)'로 추가
        GroupMember groupMember = new GroupMember();
        groupMember.setUser(user);
        groupMember.setGroup(group);
        groupMember.setPermission(3);  // '일반 사용자'로 설정
        System.out.println("Saving Group Member: " + groupMember);
        groupMemberRepository.save(groupMember);  // 신청자를 그룹에 추가

        // 5. 이후 그룹에 소유자가 없다면, 그룹을 만든 사람에게 '소유자 (1)' 권한을 부여
        // 그룹 생성자를 소유자로 설정
        GroupMember ownerMember = new GroupMember();
        ownerMember.setUser(group.getCreatedBy());  // 그룹의 생성자
        ownerMember.setGroup(group);
        ownerMember.setPermission(1);  // '소유자' 권한 설정

        // 소유자 정보가 없으면 추가
        Optional<GroupMember> existingOwner = group.getMembers().stream()
                .filter(member -> member.getPermission() == 1)
                .findFirst();

        if (!existingOwner.isPresent()) {
            groupMemberRepository.save(ownerMember);  // 그룹의 소유자가 없으면 소유자 추가
        }

        // 6. 그룹 신청이 수락되었으므로 해당 그룹 신청을 삭제할 수 있음 (필요시)
        // groupRequestRepository.delete(groupRequest); // 필요시 삭제
    }
    public void editGroupDescription(Long groupId, Long userId, String description) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        // 소유자 여부 확인
        if (!group.getCreatedBy().getUserId().equals(userId)) {
            throw new IllegalArgumentException("그룹 설명을 수정할 권한이 없습니다.");
        }

        group.setDescription(description);
        groupRepository.save(group);
    }
    public void removeMember(Long groupId, Long userId, Long memberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        // 소유자 여부 확인
        if (!group.getCreatedBy().getUserId().equals(userId)) {
            throw new IllegalArgumentException("멤버를 추방할 권한이 없습니다.");
        }

        GroupMember member = groupMemberRepository.findByGroup_groupIdAndUser_userId(groupId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

        groupMemberRepository.delete(member);
    }
    public void deleteGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        if (!group.getCreatedBy().getUserId().equals(userId)) {
            throw new IllegalArgumentException("그룹을 삭제할 권한이 없습니다.");
        }

        groupRepository.delete(group);
    }
    public void changeMemberPermission(Long groupId, Long userId, Long targetUserId, Integer newPermission) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        if (!group.getCreatedBy().getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한을 변경할 권한이 없습니다.");
        }

        GroupMember member = groupMemberRepository.findByGroup_groupIdAndUser_userId(groupId, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

        if (member.getPermission() == 1) {
            throw new IllegalArgumentException("소유자의 권한은 변경할 수 없습니다.");
        }

        member.setPermission(newPermission);
        groupMemberRepository.save(member);
    }
    public List<GroupRequestDTO> getGroupRequestsForGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        if (!group.getCreatedBy().getUserId().equals(userId)) {
            throw new IllegalArgumentException("그룹 신청을 조회할 권한이 없습니다.");
        }

        List<GroupRequest> requests = groupRequestRepository.findByGroupAndStatus(group, GroupRequest.RequestStatus.대기);
        return requests.stream()
                .map(GroupRequestDTO::new)
                .collect(Collectors.toList());
    }
    public void updateRequestStatus(Long requestId, Long userId, String status) {
        GroupRequest groupRequest = groupRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        Group group = groupRequest.getGroup();
        if (!group.getCreatedBy().getUserId().equals(userId)) {
            throw new IllegalArgumentException("신청을 처리할 권한이 없습니다.");
        }

        GroupRequest.RequestStatus newStatus = GroupRequest.RequestStatus.valueOf(status.toUpperCase());
        groupRequest.setStatus(newStatus);
        groupRequestRepository.save(groupRequest);

        if (newStatus == GroupRequest.RequestStatus.수락) {
            GroupMember member = new GroupMember();
            member.setUser(groupRequest.getUser());
            member.setGroup(group);
            member.setPermission(3); // 기본 권한: 그룹 구성원
            groupMemberRepository.save(member);
        }
    }
    public List<GroupRequest> findByUser(Long userId) {
        User user = userRepository.findById(userId)
                      .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return groupRequestRepository.findByUserAndStatus(user, GroupRequest.RequestStatus.대기);
    }

    public void deleteRequest(Long requestId, Long userId) {
        GroupRequest request = groupRequestRepository.findById(requestId)
                                   .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!request.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this request");
        }

        groupRequestRepository.delete(request);
    }

}
