package com.anonymous.chat.websocket;

import com.anonymous.chat.websocket.dto.SystemMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.anonymous.chat.websocket.dto.SocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {


    private final Map<String, Integer> reportCounts = new ConcurrentHashMap<>();

    // sessionId -> last message timestamp
    private final Map<String, Long> lastMessageTime = new ConcurrentHashMap<>();

    // blocked sessionIds
    private final Set<String> blockedSessions = ConcurrentHashMap.newKeySet();

    // limits (MVP)
    private static final int MAX_REPORTS = 3;
    private static final long MESSAGE_COOLDOWN_MS = 500;


    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, UserState> userStates = new ConcurrentHashMap<>();


    private final Queue<String> waitingQueue = new ConcurrentLinkedQueue<>();


    private final Map<String, ChatRoom> activeRooms = new ConcurrentHashMap<>();


    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();


    private final Map<String, String> sessionToRoom = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        if (blockedSessions.contains(session.getId())) {
            session.close();
            return;
        }

        sessions.put(sessionId, session);

        System.out.println("Connected: " + sessionId);

        tryMatch(sessionId);
    }
    private boolean isRateLimited(String sessionId) {
        long now = System.currentTimeMillis();
        Long lastTime = lastMessageTime.get(sessionId);

        if (lastTime != null && (now - lastTime) < MESSAGE_COOLDOWN_MS) {
            return true;
        }

        lastMessageTime.put(sessionId, now);
        return false;
    }


    private void handleSkipOrEnd(String sessionId) {
        String roomId = sessionToRoom.remove(sessionId);
        if (roomId == null) return;

        ChatRoom room = activeRooms.remove(roomId);
        if (room == null) return;

        String partnerId = room.getPartner(sessionId);

        if (partnerId != null) {
            sessionToRoom.remove(partnerId);
            sendSystemMessage(partnerId, "Stranger skipped the chat");

            waitingQueue.add(partnerId);
        }

        waitingQueue.add(sessionId);

        tryMatch(sessionId);
    }
    private void blockSession(String blockedSessionId) {
        blockedSessions.add(blockedSessionId);

        // Remove blocked user from room WITHOUT requeueing them
        String roomId = sessionToRoom.remove(blockedSessionId);
        if (roomId != null) {
            ChatRoom room = activeRooms.remove(roomId);
            if (room != null) {
                String partnerId = room.getPartner(blockedSessionId);

                if (partnerId != null) {
                    sessionToRoom.remove(partnerId);
                    sendSystemMessage(partnerId, "Stranger was blocked");

                    // ✅ Only the innocent partner re-enters queue
                    waitingQueue.add(partnerId);
                    tryMatch(partnerId);
                }
            }
        }

        WebSocketSession session = sessions.get(blockedSessionId);
        try {
            if (session != null) session.close();
        } catch (Exception ignored) {}
    }



    private void handleReport(String reporterSessionId) {
        String roomId = sessionToRoom.get(reporterSessionId);
        if (roomId == null) return;

        ChatRoom room = activeRooms.get(roomId);
        if (room == null) return;

        String reportedSessionId = room.getPartner(reporterSessionId);
        if (reportedSessionId == null) return;

        int count = reportCounts.getOrDefault(reportedSessionId, 0) + 1;
        reportCounts.put(reportedSessionId, count);

        sendSystemMessage(reporterSessionId, "Report received");

        if (count >= MAX_REPORTS) {
            blockSession(reportedSessionId);
        }
    }





    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String sessionId = session.getId();

        if (blockedSessions.contains(sessionId)) {
            return;
        }


        try {
            SocketMessage socketMessage =
                    objectMapper.readValue(message.getPayload(), SocketMessage.class);

            if (socketMessage.getType() == null) {
                sendSystemMessage(sessionId, "Missing message type");
                return;
            }

            switch (socketMessage.getType()) {

                case "message":
                    if (isRateLimited(sessionId)) {
                        sendSystemMessage(sessionId, "You are sending messages too fast");
                        return;
                    }
                    handleChatMessage(sessionId, socketMessage);
                    break;


                case "skip":
                case "end":
                    handleSkipOrEnd(sessionId);
                    break;

                case "report":
                    handleReport(sessionId);
                    break;

                default:
                    sendSystemMessage(sessionId, "Unknown message type");
            }

        } catch (Exception e) {
            sendSystemMessage(sessionId, "Invalid JSON format");
        }
    }
    private void sendMessageToUser(String sessionId, SocketMessage msg) {
        WebSocketSession session = sessions.get(sessionId);
        if (session == null || !session.isOpen()) return;

        try {
            String json = objectMapper.writeValueAsString(msg);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleChatMessage(String sessionId, SocketMessage msg) {
        String roomId = sessionToRoom.get(sessionId);
        if (roomId == null) return;

        ChatRoom room = activeRooms.get(roomId);
        if (room == null) return;

        String partnerId = room.getPartner(sessionId);
        if (partnerId == null) return;

        sendMessageToUser(partnerId, msg);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();

        // 1. Remove live session
        sessions.remove(sessionId);

        // 2. Remove from waiting queue
        waitingQueue.remove(sessionId);

        // 3. Handle active room (if any)
        String roomId = sessionToRoom.remove(sessionId);
        if (roomId != null) {
            ChatRoom room = activeRooms.remove(roomId);

            if (room != null) {
                String partnerId = room.getPartner(sessionId);

                if (partnerId != null) {
                    sessionToRoom.remove(partnerId);
                    sendSystemMessage(partnerId, "Stranger disconnected");

                    // Partner goes back to matching flow
                    waitingQueue.add(partnerId);
                    tryMatch(partnerId);
                }
            }
        }

        // 4. Cleanup moderation state (STEP 8)
        reportCounts.remove(sessionId);
        lastMessageTime.remove(sessionId);
        blockedSessions.remove(sessionId);

        System.out.println("Disconnected & cleaned up: " + sessionId);
    }

    private void sendMatch(String sessionId, String roomId) {
        WebSocketSession session = sessions.get(sessionId);
        if (session == null || !session.isOpen()) return;

        try {
            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "match");
            msg.put("payload", Map.of("roomId", roomId));

            session.sendMessage(
                    new TextMessage(objectMapper.writeValueAsString(msg))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void tryMatch(String sessionId) {
        String waitingUser;

        while (true) {
            waitingUser = waitingQueue.poll();

            // No one waiting → put back and exit
            if (waitingUser == null) {
                waitingQueue.add(sessionId);
                return;
            }

            // ❌ NEVER match user with themselves
            if (waitingUser.equals(sessionId)) {
                continue;
            }

            break;
        }


        String roomId = UUID.randomUUID().toString();
        ChatRoom room = new ChatRoom(roomId, waitingUser, sessionId);

        activeRooms.put(roomId, room);
        sessionToRoom.put(waitingUser, roomId);
        sessionToRoom.put(sessionId, roomId);

        // Send MATCH message to both users
        sendMatch(waitingUser, roomId);
        sendMatch(sessionId, roomId);

        System.out.println(
                "Room created: " + roomId +
                        " [" + waitingUser + " <-> " + sessionId + "]"
        );
    }



    private void handleDisconnect(String sessionId) {
        // Remove from waiting queue if present
        waitingQueue.remove(sessionId);

        String roomId = sessionToRoom.remove(sessionId);
        if (roomId == null) {
            return;
        }

        ChatRoom room = activeRooms.remove(roomId);
        if (room == null) {
            return;
        }

        String partnerId = room.getPartner(sessionId);

        if (partnerId != null) {
            sessionToRoom.remove(partnerId);
            sendSystemMessage(partnerId, "Stranger disconnected");


            waitingQueue.add(partnerId);
            System.out.println("Partner re-queued: " + partnerId);
        }
    }

    private void sendSystemMessage(String sessionId, String text) {
        WebSocketSession session = sessions.get(sessionId);
        if (session == null || !session.isOpen()) return;

        try {
            String json = objectMapper.writeValueAsString(
                    new SystemMessage(text)
            );
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
