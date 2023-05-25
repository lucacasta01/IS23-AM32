package it.polimi.myShelfie.application.GUIcontroller;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.io.IOException;
import java.nio.file.Paths;

public class MenuController {
    private Stage stage;


    public void initialize() {
        GUIClient.getInstance().getStage().setWidth(GUIClient.getInstance().getStage().getWidth() + 1);
        GUIClient.getInstance().getStage().centerOnScreen();
        GUIClient.getInstance().getStage().setOnCloseRequest(e -> {
            GUIClient.getInstance().getStage().close();
            Client.getInstance().addGuiAction("/quit");
        });
    }

    public void doShutdown(ActionEvent actionEvent) {
        Client client = Client.getInstance();
        if (client == null) {
            System.exit(15);
        } else {
            client.addGuiAction("/quit");
        }
    }

    public void newGameAction(ActionEvent actionEvent) {
        GUIClient.getInstance().switchToPlayerNumber();
        Client.getInstance().addGuiAction("1");
    }

    public void randomGameAction(ActionEvent actionEvent) {
        Client.getInstance().addGuiAction("3");
    }

    public void oldGameFound() {

    }

    public void restoreGameAction(ActionEvent actionEvent) {
        Client.getInstance().addGuiAction("2");
        //todo
    }

    public void searchRestoredAction(ActionEvent actionEvent) {
        Client.getInstance().addGuiAction("4");
        //todo
    }
}


