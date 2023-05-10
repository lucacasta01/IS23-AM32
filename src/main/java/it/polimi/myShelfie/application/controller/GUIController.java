package it.polimi.myShelfie.application.controller;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.utilities.beans.Response;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


public class GUIController {
    private String nickname;
    private String connectionProtocol;
    private Client client;
    private Stage stage;




    @FXML
    RadioButton TCPrbtn, RMIrbtn;
    @FXML
    TextField nicknameField;
    @FXML
    Label nicknameDeniedLbl;

    public void doLogin(ActionEvent actionEvent) throws RemoteException {
        stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
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
        client.setGuiController(this);
        client.setConnectionProtocol(connectionProtocol);
        client.run();
        client.addGuiAction(nickname);
    }

    public void nicknameDenied(){
        nicknameDeniedLbl.setVisible(true);
    }

    public void loginAccepted() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Parent menuPage = null;
                try {
                    menuPage = FXMLLoader.load(Paths.get("src/resources/menuPage.fxml").toUri().toURL());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stage.setScene(new Scene(menuPage));
            }
        });
    }

    public void doShutdown(ActionEvent actionEvent) {
        System.exit(0);
    }
}
