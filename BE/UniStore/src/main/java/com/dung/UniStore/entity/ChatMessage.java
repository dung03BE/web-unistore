package com.dung.UniStore.entity;

import lombok.Data;

@Data
public class ChatMessage {
    private String content;
    private String sender;
    private MessageType type;
    private Long productId;
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}