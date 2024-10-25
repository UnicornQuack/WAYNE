package com.example.chatapp;

public class ChatMessage {
    private String sender;
    private String message;
    private long timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String sender, String message, long timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
