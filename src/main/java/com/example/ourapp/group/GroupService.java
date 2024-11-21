package com.example.ourapp.group;

import com.example.ourapp.DTO.UserDTO;
import com.example.ourapp.entity.Group;
import com.example.ourapp.entity.GroupMember;
import com.example.ourapp.entity.GroupRequest;
import com.example.ourapp.entity.User;
import com.example.ourapp.user.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    
    public List<Group> findGroupsByUser(Long userId) {
        // 해당 userId에 해당하는 User 객체를 DB에서 찾고
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // 해당 사용자가 생성한 그룹 목록을 반환
        return groupRepository.findByCreatedBy(user);  // User 객체를 기준으로 그룹 찾기
    }

 // 그룹 신청 처리
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


}
