package it.polimi.myShelfie.utilities.beans;
public class Response {
    private ChatMessage chatMessage;
    private View view;
    private String infoMessage = "";
    private ResponseType responseType;

    public Response(ResponseType responseType, ChatMessage chatMessage, View view, String infoMessage) {
        this.chatMessage = chatMessage;
        this.view = view;
        this.infoMessage = infoMessage;
        this.responseType = responseType;
    }

    public enum ResponseType{

        UPDATE,
        CHATMESSAGE,
        PRIVATEMESSAGE,
        VALID,
        DENIED,
        INFO,
        PING,
        PONG,
        SHUTDOWN,
        LOBBY_JOINED,
        LOBBY_CREATED,
        NICKNAME_DENIED,
        NICKNAME_ACCEPTED,
        GAME_NOT_FOUND,
        GAME_STARTED,
        DENY_LOAD_GAME,
        SOMEONE_JOINED_LOBBY,
        ACCEPT_LOAD_GAME,
        RETURN_TO_MENU,
        FOUND_OLD_GAME,
        OLD_GAME_NOT_FOUND
    }

    public static class ChatMessage{
        private final String sender;
        private final String message;

        public ChatMessage(String sender, String message) {
            this.sender = sender;
            this.message = message;
        }

        public String getSender() {
            return sender;
        }

        public String getMessage() {
            return message;
        }
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public View getView() {
        return view;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }
}
