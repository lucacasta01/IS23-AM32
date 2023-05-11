package it.polimi.myShelfie.application.controller;

import it.polimi.myShelfie.application.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

public class GUIMenuController {
    private Stage stage;

    public void doShutdown(ActionEvent actionEvent) {
        Client client = Client.getInstance();
        if(client==null){
            System.exit(15);
        }else{
            client.addGuiAction("/quit");
        }
    }

    public void newGameAction(ActionEvent actionEvent) {
        stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Parent numbeofplayer = null;
                try {
                    numbeofplayer = FXMLLoader.load(Paths.get("src/resources/chosePlayerNumber.fxml").toUri().toURL());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stage.setScene(new Scene(numbeofplayer));

            }
        });
        Client client = Client.getInstance();
        client.addGuiAction("1");
    }

    public void randomGameAction(ActionEvent actionEvent) {
    }

    public void restoreGameAction(ActionEvent actionEvent) {
    }

    public void searchRestoredAction(ActionEvent actionEvent) {
    }
}
