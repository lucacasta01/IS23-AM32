package it.polimi.myShelfie.utilities.pojo;
import it.polimi.myShelfie.utilities.Position;
import java.util.List;

/**
 * Represent the network request performed by the client in order to do any game action
 */
public class Action {
    private final ActionType actionType;
    private String nickname;
    private final String chatMessage;
    private final String info;
    private final List<Position> chosenTiles;
    private final Integer chosenColumn;

    public Action(ActionType actionType, String nickname, String chatMessage, String info, List<Position> chosenTiles, Integer chosenColumn) {
        this.actionType = actionType;
        this.nickname = nickname;
        this.chatMessage = chatMessage;
        this.info = info;
        this.chosenTiles = chosenTiles;
        this.chosenColumn = chosenColumn;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getNickname() {
        return nickname;
    }
    public String getChatMessage() {
        return chatMessage;
    }
    public List<Position> getChosenTiles() {
        return chosenTiles;
    }
    public Integer getChosenColumn() {
        return chosenColumn;
    }
    public String getInfo() {
        return info;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * enumeration of all the possible actions
     */
    public enum ActionType{
        CHAT,
        PRIVATEMESSAGE,
        PICKTILES,
        SELECTCOLUMN,
        INFO,
        PING,
        PONG,
        QUIT,
        ORDER,
        LOBBYKILL,
        HELP,
        VOID,
        REQUEST_MENU
    }
}
