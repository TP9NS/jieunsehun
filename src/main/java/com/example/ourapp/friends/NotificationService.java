package com.example.ourapp.friends;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.ourapp.config.WebSocketSessionManager;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    public NotificationService(SimpMessagingTemplate messagingTemplate, WebSocketSessionManager sessionManager) {
        this.messagingTemplate = messagingTemplate;
        this.sessionManager = sessionManager;
    }

    public void sendNotificationToUser(String userId, String message) {
        String sessionId = sessionManager.getSessionIdForUser(userId);

        if (sessionId != null) {
            messagingTemplate.convertAndSendToUser(sessionId, "/topic/friend-requests", Map.of("message", message));
            System.out.println("Message sent to user " + userId + " with session ID: " + sessionId);
        } else {
            System.out.println("No active session for user ID: " + userId);
        }
    }
}
