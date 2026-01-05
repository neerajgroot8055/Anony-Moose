package com.anonymous.chat.websocket.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SocketMessage {

    private String type;
    private MessagePayload payload;

    public String getType() {
        return type;
    }

    public MessagePayload getPayload() {
        return payload;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPayload(MessagePayload payload) {
        this.payload = payload;
    }
}
