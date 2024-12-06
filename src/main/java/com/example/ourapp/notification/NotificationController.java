package com.example.ourapp.notification;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.ourapp.entity.GroupRequest;
import com.example.ourapp.entity.Group;
import com.example.ourapp.entity.User;
import com.example.ourapp.friends.FriendRequestRepository;
import com.example.ourapp.friends.FriendRequestStatus;
import com.example.ourapp.group.GroupRequestRepository;
import com.example.ourapp.user.UserRepository;
import com.example.ourapp.group.GroupMemberRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private GroupRequestRepository groupRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Long>> getPendingNotifications(HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        System.out.println("asd");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", 0L));
        }
        User user = userRepository.findByUserId(userId);
        // 대기 중인 친구 요청 개수
        long pendingFriendRequests = friendRequestRepository.countByReceiverAndStatus(user, FriendRequestStatus.PENDING);
        List<Long> groupIds = groupMemberRepository.findGroupIdsByUserIdAndPermission(userId);
       Long pendingGroupRequests = groupRequestRepository.countByGroup_GroupIdInAndStatus(groupIds, GroupRequest.RequestStatus.대기);
        return ResponseEntity.ok(Map.of(
                "friendRequests", pendingFriendRequests,
                "groupRequests", pendingGroupRequests
            ));
    }
}