package it.polimi.myShelfie.utilities.pojo;

import it.polimi.myShelfie.view.View;

/**
 * Represent the network response of the server in order to do inform the client about the outcome of the request
 */
public class Response {
    private final ChatMessage chatMessage;
    private View view;
    private final String infoMessage;
    private final ResponseType responseType;

    public Response(ResponseType responseType, ChatMessage chatMessage, View view, String infoMessage) {
        this.chatMessage = chatMessage;
        this.view = view;
        this.infoMessage = infoMessage;
        this.responseType = responseType;
    }

    public enum ResponseType{

        UPDATE,
        CHATMESSAGE,
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
        GAME_STARTED,
        DENY_LOAD_GAME,
        SOMEONE_JOINED_LOBBY,
        ACCEPT_LOAD_GAME,
        RETURN_TO_MENU,
        FOUND_OLD_GAME,
        RANDOM_GAME_NOT_FOUND, ACCEPT_COLLECT, GAME_ENDED, OLD_GAME_NOT_FOUND
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
    public void setView(View view) {
        this.view = view;
    }
}
