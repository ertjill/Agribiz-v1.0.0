package com.example.agribiz_v100.entities;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class ChatModel implements Serializable {

    String chatId;
    String chatSenderUserId;
    String chatMessage;
    Timestamp chatDate;
    boolean chatSeen = false;

    public boolean isChatSeen() {
        return chatSeen;
    }

    public void setChatSeen(boolean chatSeen) {
        this.chatSeen = chatSeen;
    }

    public ChatModel() {

    }

    public ChatModel(String chatId, String chatSenderUserId, String chatMessage, Timestamp chatDate, boolean chatSeen) {
        this.chatId = chatId;
        this.chatSenderUserId = chatSenderUserId;
        this.chatMessage = chatMessage;
        this.chatDate = chatDate;
        this.chatSeen = chatSeen;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatSenderUserId() {
        return chatSenderUserId;
    }

    public void setChatSenderUserId(String chatSenderUserId) {
        this.chatSenderUserId = chatSenderUserId;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public Timestamp getChatDate() {
        return chatDate;
    }

    public void setChatDate(Timestamp chatDate) {
        this.chatDate = chatDate;
    }
}
