package com.anonymous.chat.websocket.dto;

public class MessagePayload {

    private String text;
    private String reason;

    public String getText() {
        return text;
    }

    public String getReason() {
        return reason;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
