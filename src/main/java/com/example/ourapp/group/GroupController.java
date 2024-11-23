package com.example.ourapp.group;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.ourapp.DTO.GroupDTO;
import com.example.ourapp.DTO.GroupMemberDTO;
import com.example.ourapp.DTO.GroupRequestDTO;
import com.example.ourapp.DTO.UserDTO;
import com.example.ourapp.entity.Group;
import com.example.ourapp.entity.GroupMember;
import com.example.ourapp.entity.GroupRequest;
import com.example.ourapp.entity.User;
import com.example.ourapp.user.UserRepository;
import com.example.ourapp.user.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.ui.Model;

@RequiredArgsConstructor
@Controller
@RequestMapping("/groups")
public class GroupController {

	private final GroupService groupService;
	private final UserService userService;
    private final UserRepository userRepository;
	
    @GetMapping
    public String groupsPage(HttpSession session, Model model) {
        // 세션에서 user_id 가져오기
        Object userId = session.getAttribute("user_id");

        // user_id가 없으면 로그인 페이지로 리다이렉트
        if (userId == null) {
            return "redirect:/user/login";
        }

        // user_id를 모델에 추가하여 뷰에서 사용할 수 있도록 설정
        model.addAttribute("user_id", userId);
        System.out.println("User ID: " + session.getAttribute("user_id"));

        return "groups.html";
    
    }
    
    @GetMapping("/my-groups")
    @ResponseBody
    public List<GroupDTO> getMyGroups(HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            throw new IllegalArgumentException("User not logged in");
        }

        // 사용자가 생성하거나 소속된 그룹 조회
        List<Group> groups = groupService.findGroupsByUser(userId);

        // GroupDTO로 변환하여 반환 (권한 정보 포함)
        return groups.stream()
                     .map(group -> {
                         Integer permission = groupService.getMyPermission(group, userId);
                         return new GroupDTO(group, permission);
                     })
                     .collect(Collectors.toList());
    }

    
    @GetMapping("/{groupId}/members")
    public String getGroupMembersPage(@PathVariable Long groupId, Model model) {
        Group group = groupService.findGroupById(groupId);
        model.addAttribute("groupName", group.getGroupName());
        return "groups/members"; // HTML 템플릿 렌더링
    }
    @GetMapping("/{groupId}/requests")
    @ResponseBody
    public List<GroupRequestDTO> getGroupRequestsForGroup(@PathVariable Long groupId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return groupService.getGroupRequestsForGroup(groupId, userId);
    }
    @PostMapping("/apply/{groupId}")
    public ResponseEntity<?> applyForGroup(@PathVariable Long groupId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인 후 이용 가능합니다.");
        }

        // 세션에서 userId를 기반으로 UserDTO를 찾음
        UserDTO userDTO = userService.findUserById(userId);

        // UserDTO를 그대로 넘겨서 그룹 신청 처리
        groupService.applyForGroup(groupId, userDTO);
        return ResponseEntity.ok("그룹 신청이 완료되었습니다.");
    }
    
    @GetMapping("/requests")
    @ResponseBody
    public List<GroupRequestDTO> getGroupRequests(HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            throw new IllegalArgumentException("로그인 후 이용 가능합니다.");
        }

        // 로그인한 사용자가 소유한 그룹 목록 가져오기
        UserDTO userDTO = userService.findUserById(userId);
        List<Group> groupsOwned = groupService.findGroupsByUser(userDTO.getUser_id());

        // 그룹 소유자가 관리하는 그룹에 대한 신청 목록 가져오기
        List<GroupRequest> requests = groupService.findRequestsForGroups(groupsOwned);

        // GroupRequest -> GroupRequestDTO 변환 후 반환
        return requests.stream()
                       .map(GroupRequestDTO::new)
                       .collect(Collectors.toList());
    }


    @PostMapping("/requests/{requestId}/update")
    @ResponseBody
    public ResponseEntity<?> updateRequestStatus(@PathVariable Long requestId, @RequestParam String status, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }

        try {
            groupService.updateRequestStatus(requestId, userId, status);
            return ResponseEntity.ok("신청이 처리되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createGroup(@RequestBody GroupDTO groupDTO, HttpSession session) {
        // 세션에서 user_id 가져오기
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }

        // 사용자 엔티티 조회
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new IllegalArgumentException("User not found"));
        System.out.println(groupDTO.getDescription());
        // 그룹 생성
        Group createdGroup = groupService.createGroup(groupDTO.getGroupName(), groupDTO.getDescription(), user);

        // JSON 응답 반환
        return ResponseEntity.ok("그룹이 성공적으로 생성되었습니다: " + createdGroup.getGroupName());
    }
    @GetMapping("/search")
    @ResponseBody
    public List<GroupDTO> searchGroups(@RequestParam("keyword") String keyword) {
        List<Group> groups = groupService.searchGroups(keyword);

        // Group -> GroupDTO 변환
        return groups.stream()
                     .map(GroupDTO::new)
                     .collect(Collectors.toList());
    }
    @PostMapping("/{groupId}/edit-description")
    @ResponseBody
    public ResponseEntity<?> editGroupDescription(@PathVariable Long groupId, @RequestBody Map<String, String> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        String description = body.get("description");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }

        try {
            groupService.editGroupDescription(groupId, userId, description);
            return ResponseEntity.ok("그룹 설명이 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/{groupId}/remove-member")
    @ResponseBody
    public ResponseEntity<?> removeMember(@PathVariable Long groupId, @RequestBody Map<String, Long> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Long memberId = body.get("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }

        try {
            groupService.removeMember(groupId, userId, memberId);
            return ResponseEntity.ok("멤버가 추방되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/{groupId}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }

        try {
            groupService.deleteGroup(groupId, userId);
            return ResponseEntity.ok("그룹이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/{groupId}/change-permission")
    @ResponseBody
    public ResponseEntity<?> changeMemberPermission(@PathVariable Long groupId, @RequestBody Map<String, Object> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Long targetUserId = Long.valueOf(body.get("userId").toString());
        Integer newPermission = Integer.valueOf(body.get("permission").toString());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }

        try {
            groupService.changeMemberPermission(groupId, userId, targetUserId, newPermission);
            return ResponseEntity.ok("권한이 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/{groupId}")
    @ResponseBody
    public GroupDTO getGroupDetails(@PathVariable Long groupId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        Group group = groupService.findGroupById(groupId);
        return new GroupDTO(group);
    }
    @GetMapping("/my-requests")
    @ResponseBody
    public List<GroupRequestDTO> getMyRequests(HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            throw new IllegalArgumentException("User not logged in");
        }

        List<GroupRequest> requests = groupService.findByUser(userId);
        return requests.stream()
                       .map(GroupRequestDTO::new)
                       .collect(Collectors.toList());
    }

    @DeleteMapping("/requests/{id}/delete")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
        	groupService.deleteRequest(id, userId);
            return ResponseEntity.ok().body("Request deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}