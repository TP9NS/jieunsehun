package com.example.ourapp.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class SessionRegistry {
    private final Map<String, Long> sessionIdToUserId = new ConcurrentHashMap<>();

    public void registerSession(String sessionId, Long userId) {
        sessionIdToUserId.put(sessionId, userId);
    }

    public Long getUserId(String sessionId) {
        return sessionIdToUserId.get(sessionId);
    }

    public String getSessionId(Long userId) {
        return sessionIdToUserId.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(userId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public void removeSession(String sessionId) {
        sessionIdToUserId.remove(sessionId);
    }
}
