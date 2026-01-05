package com.anonymous.chat.websocket.dto;
public class SystemMessage {

    private final String type = "system";
    private final MessagePayload payload;

    public SystemMessage(String text) {
        this.payload = new MessagePayload();
        this.payload.setText(text);
    }

    public String getType() {
        return type;
    }

    public MessagePayload getPayload() {
        return payload;
    }
}
