package com.dnebinger.websockets.chat;

/**
 * class ChatMessage: A container for a chat message.
 *
 * @author dnebinger
 */
public class ChatMessage {
    private String from;
    private String message;

    public ChatMessage() {
    }

    public ChatMessage(String from, String message) {
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
