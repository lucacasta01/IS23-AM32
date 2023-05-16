package it.polimi.myShelfie.application.GUIcontroller;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

public class MenuController {
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
        GUIClient.getInstance().switchToPlayerNumber();
        Client.getInstance().addGuiAction("1");
    }

    public void randomGameAction(ActionEvent actionEvent) {
        Client.getInstance().addGuiAction("3");
    }
    public void oldGameFound(){

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
