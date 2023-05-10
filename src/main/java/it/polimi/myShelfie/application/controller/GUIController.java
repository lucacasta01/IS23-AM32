package it.polimi.myShelfie.application.controller;

import it.polimi.myShelfie.application.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;


public class GUIController {
    String nickname;
    String connectionProtocol;



    @FXML
    RadioButton TCPrbtn, RMIrbtn;
    @FXML
    TextField nicknameField;

    public void doLogin(ActionEvent actionEvent) {
        if(TCPrbtn.isSelected()){
            connectionProtocol = "TCP";
        }
        else if(RMIrbtn.isSelected()){
            connectionProtocol = "RMI";
        }

        if(!nicknameField.getText().equals("")){
            nickname = nicknameField.getText();
        }
        System.out.println("Nickname: "+nickname);
        System.out.println("Protocol: "+connectionProtocol);

    }

    public void newGameAction(ActionEvent actionEvent) {
    }

    public void loadLastGameAction(ActionEvent actionEvent) {
    }

    public void randomGameAction(ActionEvent actionEvent) {
    }

    public void searchOldGameAction(ActionEvent actionEvent) {
    }

    public void doShutdown(ActionEvent actionEvent) {
        System.exit(0);
    }
}
