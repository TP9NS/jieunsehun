package com.example.ourapp.friends;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ourapp.DTO.FriendRequestDTO;
import com.example.ourapp.DTO.FriendRequestDetailDTO;
import com.example.ourapp.DTO.FriendsDTO;
import com.example.ourapp.DTO.MessageDTO;
import com.example.ourapp.DTO.UserDTO;
import com.example.ourapp.config.SessionRegistry;
import com.example.ourapp.entity.Message;
import com.example.ourapp.entity.User;
import com.example.ourapp.user.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/friends")
public class FriendsController {

    @Autowired
    private FriendsService friendsService;

    private final SimpMessagingTemplate messagingTemplate;

    private final SessionRegistry sessionRegistry; // 세션 관리 클래스 추가

    private final MessageRepository messageRepository;
    
    private final UserRepository userRepository;
    @Autowired
    public FriendsController(SimpMessagingTemplate messagingTemplate, SessionRegistry sessionRegistry, MessageRepository messageRepository, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.sessionRegistry = sessionRegistry;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
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
    
    @PostMapping("/messages/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> messageData) {
        // 입력 데이터 검증
        String senderId = messageData.get("senderId");
        String receiverId = messageData.get("receiverId");
        String messageContent = messageData.get("content");

        System.out.println("Received request data: senderId=" + senderId + ", receiverId=" + receiverId + ", content=" + messageContent);

        if (senderId == null || receiverId == null || messageContent == null || messageContent.trim().isEmpty()) {
            // JSON 형식으로 반환
            return ResponseEntity.badRequest().body(Map.of("error", "올바른 입력값이 필요합니다."));
        }

        try {
            friendsService.sendMessage(senderId, receiverId, messageContent);
            System.out.println("Message saved successfully.");

            messagingTemplate.convertAndSend(
                "/topic/private/messages/" + receiverId,
                Map.of("senderId", senderId, "content", messageContent)
            );
            System.out.println("WebSocket message sent successfully.");

            // 성공 응답도 JSON 형식으로 반환
            return ResponseEntity.ok(Map.of("message", "쪽지가 성공적으로 전송되었습니다."));
        } catch (Exception e) {
            System.out.println("Error occurred while sending message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "쪽지 전송에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/messages/inbox")
    public String getInboxPage(HttpSession session, Model model) {
        // 세션에서 userId 가져오기
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login"; // 세션에 userId가 없으면 로그인 페이지로 리다이렉트
        }

        // 받은 쪽지 조회
        List<Message> messages = messageRepository.findByReceiverId(String.valueOf(userId));

        // Message 엔티티를 MessageDTO로 변환
        List<MessageDTO> result = messages.stream()
            .map(message -> {
                // senderName 조회
                String senderName = userRepository.findById(Long.valueOf(message.getSenderId()))
                    .map(User::getName)
                    .orElse("알 수 없음"); // 이름이 없으면 기본값 설정
                return new MessageDTO(
                    message.getId(),
                    message.getSenderId(),
                    senderName, // 발신자 이름 추가
                    message.getReceiverId(),
                    message.getContent(),
                    message.getTimestamp(),
                    message.getStatus().name()
                );
            })
            .collect(Collectors.toList());

        // 모델에 데이터 추가
        model.addAttribute("messages", result);
        model.addAttribute("userId", userId);

        // HTML 템플릿 렌더링
        return "inbox"; // templates/inbox.html 파일 렌더링
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageDTO> getMessageDetails(@PathVariable Long id) {
        System.out.println("Fetching message details for ID: " + id);

        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Message not found"));

        // senderName 조회
        String senderName = userRepository.findById(Long.valueOf(message.getSenderId()))
            .map(User::getName)
            .orElse("알 수 없음");

        // MessageDTO에 senderName 추가
        MessageDTO dto = new MessageDTO(
            message.getId(),
            message.getSenderId(),
            senderName, // 발신자 이름 추가
            message.getReceiverId(),
            message.getContent(),
            message.getTimestamp(),
            message.getStatus().name()
        );

        return ResponseEntity.ok(dto);
    }



    @PutMapping("/messages/{id}/read")
    public ResponseEntity<?> markMessageAsRead(@PathVariable Long id) {
        try {
            friendsService.markAsRead(id);
            return ResponseEntity.ok(Map.of("message", "메시지가 읽음 처리되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "메시지 읽음 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/messages/reply")
    public ResponseEntity<Map<String, String>> sendReply(
        HttpSession session,
        @RequestBody Map<String, String> replyData
    ) {
        Long senderId = (Long) session.getAttribute("user_id"); // 세션에서 senderId 가져오기
        if (senderId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "로그인이 필요합니다."));
        }

        String receiverId = replyData.get("receiverId");
        String content = replyData.get("content");

        if (receiverId == null || content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "올바른 입력값이 필요합니다."));
        }

        try {
            // 메시지 저장 및 전송 처리
            friendsService.sendMessage(senderId.toString(), receiverId, content);

            return ResponseEntity.ok(Map.of("message", "답장이 성공적으로 전송되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "답장 전송 중 오류가 발생했습니다: " + e.getMessage()));
        }
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
