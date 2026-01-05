package com.anonymous.chat.websocket;

public class ChatRoom {

    private final String roomId;
    private final String userA;
    private final String userB;

    public ChatRoom(String roomId, String userA, String userB) {
        this.roomId = roomId;
        this.userA = userA;
        this.userB = userB;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getUserA() {
        return userA;
    }

    public String getUserB() {
        return userB;
    }

    public String getPartner(String sessionId) {
        if (userA.equals(sessionId)) return userB;
        if (userB.equals(sessionId)) return userA;
        return null;
    }
}

