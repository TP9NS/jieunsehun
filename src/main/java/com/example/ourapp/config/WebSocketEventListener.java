package com.example.ourapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private SessionRegistry sessionRegistry;

    @EventListener
    public void handleSessionConnect(SessionConnectEvent event) {
        // STOMP 헤더 접근
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("Headers: " + headerAccessor.getMessageHeaders());

        // userId 값 가져오기
        String userIdString = headerAccessor.getFirstNativeHeader("userId");

        // userId 값 검증
        if (userIdString == null || userIdString.trim().isEmpty() || userIdString.equalsIgnoreCase("null")) {
            System.err.println("Invalid userId detected! userId=" + userIdString + 
                               ", Headers: " + headerAccessor.getMessageHeaders());
            return; // 잘못된 userId는 처리하지 않고 종료
        }

        Long userId;
        try {
            userId = Long.valueOf(userIdString);
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse userId: " + userIdString + 
                               ", Headers: " + headerAccessor.getMessageHeaders());
            e.printStackTrace();
            return; // 파싱 실패 시 종료
        }

        String sessionId = headerAccessor.getSessionId();

        // 세션 등록
        sessionRegistry.registerSession(sessionId, userId);
        System.out.println("Connected: sessionId=" + sessionId + ", userId=" + userId);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        // 세션 ID 가져오기
        String sessionId = event.getSessionId();

        // 세션 제거
        sessionRegistry.removeSession(sessionId);
        System.out.println("Disconnected: sessionId=" + sessionId);
    }
}
