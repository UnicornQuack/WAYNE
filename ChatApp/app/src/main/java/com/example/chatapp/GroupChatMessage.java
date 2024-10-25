package com.example.chatapp;

public class GroupChatMessage {
    private String sender;
    private String message;
    private String senderName;
    private long timestamp;

    public GroupChatMessage() {}

    public GroupChatMessage(String sender, String message, String senderName, long timestamp) {
        this.sender = sender;
        this.message = message;
        this.senderName = senderName;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
