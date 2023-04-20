package it.polimi.myShelfie.utilities.beans;

public class Response {
    private ChatMessage chatMessage;
    private View view;
    private String infoMessage = "";
    private ResponseType responseType;

    public Response(String sender, String message) {
        this.responseType = ResponseType.CHATMESSAGE;
        this.chatMessage = new ChatMessage(sender, message);
        this.view = null;
    }

    public Response(View view){
        this.responseType = ResponseType.UPDATE;
        this.chatMessage = null;
        this.view = view;
    }

    public Response(ResponseType responseType, String infoMessage) {
        this.responseType = responseType;
        this.infoMessage = infoMessage;
        this.chatMessage = null;
        this.view = null;
    }
    public Response(String infoMessage) {
        this.responseType = ResponseType.INFO;
        this.infoMessage = infoMessage;
        this.chatMessage = null;
        this.view = null;
    }

    public enum ResponseType{
        UPDATE,
        CHATMESSAGE,
        VALID,
        DENIED,
        INFO
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
