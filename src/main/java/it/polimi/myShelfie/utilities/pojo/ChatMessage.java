package it.polimi.myShelfie.utilities.pojo;

import java.io.Serializable;

/**
 * Represents a chat message composed by the sender, the body message and the color associated to the sender
 */
public class ChatMessage implements Serializable {
    private final String sender;
    private final String message;
    private final String senderColor;

    public ChatMessage(String sender, String message, String senderColor) {
        this.sender = sender;
        this.message = message;
        this.senderColor = senderColor;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderColor() {
        return senderColor;
    }
}
