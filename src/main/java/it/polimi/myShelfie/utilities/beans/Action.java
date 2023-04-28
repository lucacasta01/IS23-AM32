package it.polimi.myShelfie.utilities.beans;

import it.polimi.myShelfie.model.Position;
import it.polimi.myShelfie.model.Tile;
import it.polimi.myShelfie.server.Lobby;
import it.polimi.myShelfie.utilities.ColorPosition;

import java.util.List;

public class Action {
    private ActionType actionType;
    private String nickname;
    private String chatMessage;
    private String info;
    private List<Position> chosenTiles;
    private Integer chosenColumn;

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

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setChatMessage(ActionType actionType, String string) {
        if(actionType == ActionType.CHAT){
            this.chatMessage = string;
        }
        else if(actionType == ActionType.INFO){
            this.info = string;
        }
    }

    public void setChosenTiles(List<Position> chosenTiles) {
        this.chosenTiles = chosenTiles;
    }

    public void setChosenColumn(Integer chosenColumn) {
        this.chosenColumn = chosenColumn;
    }

    public enum ActionType{
        UPDATE,
        CHAT,
        PICKTILES,
        SELECTCOLUMN,
        INFO,
        PING,
        QUIT,
        PRINTBOARD,
        ORDER,
        LOBBYKILL
    }
}
