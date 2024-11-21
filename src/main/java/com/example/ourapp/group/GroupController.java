package com.example.ourapp.group;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.ourapp.DTO.GroupDTO;
import com.example.ourapp.DTO.GroupRequestDTO;
import com.example.ourapp.DTO.UserDTO;
import com.example.ourapp.entity.Group;
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
    @ResponseBody  // JSON 응답을 보낼 수 있게 해주는 어노테이션
    public List<GroupDTO> getMyGroups(HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            throw new IllegalArgumentException("User not logged in");
        }

        UserDTO userDTO = userService.findUserById(userId);
        List<Group> groups = groupService.findGroupsByUser(userDTO.getUser_id());
        
        // GroupDTO 리스트로 변환하여 반환
        return groups.stream()
                     .map(GroupDTO::new)  // Group을 GroupDTO로 변환
                     .collect(Collectors.toList());
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


    // 그룹 신청 상태 수락 또는 거절
    @PostMapping("/requests/{requestId}/update")
    public ResponseEntity<?> updateGroupRequestStatus(@PathVariable Long requestId,
                                                       @RequestParam String status) {
        GroupRequest.RequestStatus requestStatus = GroupRequest.RequestStatus.valueOf(status.toUpperCase());
        groupService.updateGroupRequestStatus(requestId, requestStatus);
        return ResponseEntity.ok("신청 상태가 업데이트되었습니다.");
    }
    
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<?> acceptGroupRequest(@PathVariable Long requestId) {
        try {
            // 그룹 신청 수락 처리
            groupService.acceptGroupRequest(requestId);
            return ResponseEntity.ok("그룹 신청이 수락되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    
    @PostMapping("/create")
    public String createGroup(@RequestParam("groupName") String groupName,
                              @RequestParam("description") String description,
                              HttpSession session,
                              Model model) {
        // 세션에서 user_id 가져오기
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            return "redirect:/user/login"; // 로그인 필요
        }

        // 사용자 엔티티 조회
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new IllegalArgumentException("User not found"));

        // 그룹 생성
        Group createdGroup = groupService.createGroup(groupName, description, user);

        // 뷰로 성공 메시지 전달
        model.addAttribute("message", "Group created successfully: " + createdGroup.getGroupName());
        return "redirect:/groups";
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
}