package com.example.ourapp.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    // 사용자 ID와 세션 ID를 매핑하는 Map
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

    // WebSocket 연결 시 사용자 ID와 세션 ID 매핑
    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = accessor.getFirstNativeHeader("user-id"); // 클라이언트에서 전달한 user-id 헤더
        String sessionId = accessor.getSessionId();

        if (userId != null) {
            userSessionMap.put(userId, sessionId);
            System.out.println("User ID " + userId + " is connected with session ID: " + sessionId);
        }
    }

    // WebSocket 연결 종료 시 매핑 제거
    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        userSessionMap.values().remove(sessionId);
        System.out.println("Session ID " + sessionId + " is disconnected.");
    }

    // 사용자 ID로 세션 ID 가져오기
    public String getSessionIdForUser(String userId) {
        return userSessionMap.get(userId);
    }
}
