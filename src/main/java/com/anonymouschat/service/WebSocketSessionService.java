package com.anonymouschat.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSessionService {

    private final ConcurrentHashMap<String, String> userSessions = new ConcurrentHashMap<>();

    public void addUser(String userId, String sessionId) {
        userSessions.put(userId, sessionId);
    }

    public void removeUser(String userId) {
        userSessions.remove(userId);
    }

    public String getSession(String userId) {
        return userSessions.get(userId);
    }
}
