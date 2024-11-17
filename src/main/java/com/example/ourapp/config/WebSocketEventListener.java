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
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("Headers: " + headerAccessor.getMessageHeaders());

        // userId 확인
        String userIdString = headerAccessor.getFirstNativeHeader("userId");
        if (userIdString == null) {
            System.err.println("userId is null! Headers: " + headerAccessor.getMessageHeaders());
            return;
        }

        Long userId = Long.valueOf(userIdString);
        String sessionId = headerAccessor.getSessionId();

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