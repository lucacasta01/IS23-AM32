package it.polimi.myShelfie.application.controller;

import it.polimi.myShelfie.application.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.*;

public class GUIChoosePlayerController {
    Client client;
    @FXML
    TextField textField;
    @FXML
    Label denyLabel;



    public void confirmPlayersNumber(ActionEvent actionEvent) {
        client = Client.getInstance();
        client.addGuiAction(textField.getText());
    }
    public void denyNumberOfPlayers(){
        denyLabel.setVisible(true);
    }
    public void acceptPlayerNumber(){

    }
    public void doShutdown(ActionEvent actionEvent) {
        client=Client.getInstance();
        if(client==null) {
            System.exit(0);
        }else{
            client.addGuiAction("/quit");
        }
    }
}
