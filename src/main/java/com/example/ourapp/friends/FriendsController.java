package com.example.ourapp.friends;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ourapp.DTO.FriendRequestDTO;
import com.example.ourapp.DTO.FriendRequestDetailDTO;
import com.example.ourapp.DTO.FriendsDTO;
import com.example.ourapp.DTO.UserDTO;
import com.example.ourapp.config.SessionRegistry;

@Controller
@RequestMapping("/friends")
public class FriendsController {

    @Autowired
    private FriendsService friendsService;

    private final SimpMessagingTemplate messagingTemplate;

    private final SessionRegistry sessionRegistry; // 세션 관리 클래스 추가

    @Autowired
    public FriendsController(SimpMessagingTemplate messagingTemplate, SessionRegistry sessionRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * 친구 페이지 렌더링
     */
    @GetMapping
    public String friendsPage(Model model) {
        return "friends.html"; // friends.html 템플릿 렌더링
    }

    /**
     * 친구 목록 조회 API
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, String>>> getFriendList() {
        return ResponseEntity.ok(friendsService.getFriends());
    }

    /**
     * 친구 요청 전송 API
     */
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendRequestDTO request) {
        System.out.println("Receiver ID: " + request.getReceiverId()); // 디버깅용 로그
        friendsService.sendFriendRequest(request.getReceiverId());

        // WebSocket 알림 전송
        String sessionId = sessionRegistry.getSessionId(request.getReceiverId());
        if (sessionId != null) {
            messagingTemplate.convertAndSend(
                "/topic/friend-requests/" + sessionId,
                Map.of("message", "You have a new friend request.")
            );
            System.out.println("Sent friend request notification to session ID: " + sessionId);
        } else {
            System.out.println("No active session found for user ID: " + request.getReceiverId());
        }

        return ResponseEntity.ok(Collections.singletonMap("message", "Friend request sent successfully."));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestDetailDTO>> getFriendRequests() {
        Long currentUserId = friendsService.getCurrentUserId(); // 현재 사용자 ID 가져오기
        List<FriendRequestDetailDTO> requests = friendsService.getPendingFriendRequests(currentUserId);
        return ResponseEntity.ok(requests);
    }

    /**
     * 친구 요청 수락 API
     */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requestId) {
        friendsService.acceptFriendRequest(requestId);

        Long senderId = friendsService.getSenderIdFromRequest(requestId);

        // 친구 요청 수락 WebSocket 메시지 전송
        String sessionId = sessionRegistry.getSessionId(senderId);
        if (sessionId != null) {
            messagingTemplate.convertAndSend(
                "/topic/friend-accept/" + sessionId,
                Map.of("message", "Your friend request has been accepted.")
            );
            System.out.println("Sent friend accept notification to session ID: " + sessionId);
        } else {
            System.out.println("No active session found for sender ID: " + senderId);
        }

        return ResponseEntity.ok(Collections.singletonMap("message", "Friend request accepted successfully."));
    }

    @PostMapping("/decline/{requestId}")
    public ResponseEntity<?> declineFriendRequest(@PathVariable Long requestId) {
        friendsService.declineFriendRequest(requestId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Friend request declined successfully."));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchFriends(@RequestParam("query") String query) {
        System.out.println("Search query received: " + query); // 디버깅용 로그 추가
        List<UserDTO> searchResults = friendsService.searchUsers(query);
        return ResponseEntity.ok(searchResults);
    }

    @PostMapping("/delete/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long friendId) {
        friendsService.removeFriend(friendId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Friend removed successfully."));
    }
}
