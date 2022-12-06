package com.example.icebreaker.chats;

public class Message {
    String currentTime;
    String message;
    String senderId;

    public Message(String currentTime, String message, String senderId) {
        this.currentTime = currentTime;
        this.message = message;
        this.senderId = senderId;
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getCurrentTime() {
        return currentTime;
    }
}
