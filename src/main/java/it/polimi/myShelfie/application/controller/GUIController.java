package it.polimi.myShelfie.application.controller;

import it.polimi.myShelfie.application.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;


public class GUIController {
    private String nickname;
    private String connectionProtocol;
    private Client client;



    @FXML
    RadioButton TCPrbtn, RMIrbtn;
    @FXML
    TextField nicknameField;

    public void doLogin(ActionEvent actionEvent) throws RemoteException {
        if (TCPrbtn.isSelected()) {
            connectionProtocol = "TCP";
        } else if (RMIrbtn.isSelected()) {
            connectionProtocol = "RMI";
        }

        if (!nicknameField.getText().equals("")) {
            nickname = nicknameField.getText();
        }
        System.out.println("Nickname: " + nickname);
        System.out.println("Protocol: " + connectionProtocol);

        client = new Client(true);
        client.setConnectionProtocol(connectionProtocol);
        client.run();
        client.addGuiAction(nickname);

    }

    public void doShutdown(ActionEvent actionEvent) {
        System.exit(0);
    }
}
