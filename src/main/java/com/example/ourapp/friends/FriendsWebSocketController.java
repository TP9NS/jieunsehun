package com.example.ourapp.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.example.ourapp.DTO.FriendRequestDTO;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/ws/friends")
public class FriendsWebSocketController {

    @Autowired
    private FriendsService friendsService;

    
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 친구 요청 전송 API
     */
    @PostMapping("/request")
    public void sendFriendRequest(@RequestBody FriendRequestDTO request) {
        friendsService.sendFriendRequest(request.getReceiverId());

        // WebSocket 알림 전송
        Map<String, String> notification = Collections.singletonMap(
                "message", "You have a new friend request."
        );
        messagingTemplate.convertAndSend(
                "/topic/friend-requests/" + request.getReceiverId(),
                notification
        );
    }

    /**
     * 친구 요청 수락 API
     */
    @PostMapping("/accept/{requestId}")
    public void acceptFriendRequest(@PathVariable Long requestId) {
        friendsService.acceptFriendRequest(requestId);

        // WebSocket 알림 전송
        Long senderId = friendsService.getCurrentUserId(); // 세션을 통해 현재 사용자 ID 가져오기
        Map<String, String> notification = Collections.singletonMap(
                "message", "Your friend request has been accepted."
        );
        messagingTemplate.convertAndSend(
                "/topic/friend-accept/" + senderId,
                notification
        );
    }
}
