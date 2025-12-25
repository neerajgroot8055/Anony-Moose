package com.anonymouschat.model;

import lombok.Data;

@Data
public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String content;
}
