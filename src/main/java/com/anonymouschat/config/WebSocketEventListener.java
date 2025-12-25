package com.anonymouschat.config;

import com.anonymouschat.service.WebSocketSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketSessionService sessionService;

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = (String) accessor.getSessionAttributes().get("userId");
        String sessionId = accessor.getSessionId();

        if (userId != null) {
            sessionService.addUser(userId, sessionId);
            System.out.println("🟢 User connected: " + userId);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = (String) accessor.getSessionAttributes().get("userId");

        if (userId != null) {
            sessionService.removeUser(userId);
            System.out.println("🔴 User disconnected: " + userId);
        }
    }
}
