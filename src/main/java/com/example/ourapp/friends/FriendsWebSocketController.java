package com.example.ourapp.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.example.ourapp.DTO.FriendRequestDTO;

import java.util.Map;

@RestController
@RequestMapping("/ws/friends")
public class FriendsWebSocketController {

    private final FriendsService friendsService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public FriendsWebSocketController(FriendsService friendsService, SimpMessagingTemplate messagingTemplate) {
        this.friendsService = friendsService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/request")
    public void sendFriendRequest(@RequestBody FriendRequestDTO request) {
        friendsService.sendFriendRequest(request.getReceiverId());

        // WebSocket 알림 전송
        Map<String, String> notification = Map.of("message", "You have a new friend request.");
        messagingTemplate.convertAndSend(
            "/topic/friend-requests/" + request.getReceiverId(),
            notification
        );
        System.out.println("Sent friend request notification to receiver ID: " + request.getReceiverId());
    }

    /**
     * 친구 요청을 수락합니다.
     */
    @PostMapping("/accept/{requestId}")
    public void acceptFriendRequest(@PathVariable Long requestId) {
        friendsService.acceptFriendRequest(requestId);

        Long senderId = friendsService.getSenderIdFromRequest(requestId);
        messagingTemplate.convertAndSend(
            "/topic/friend-accept/" + senderId,
            Map.of("message", "Your friend request has been accepted.")
        );
    }
}
