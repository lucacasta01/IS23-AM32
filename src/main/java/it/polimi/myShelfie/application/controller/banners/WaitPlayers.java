package it.polimi.myShelfie.application.controller.banners;

import it.polimi.myShelfie.application.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.IOException;
import java.nio.file.Paths;


public class WaitPlayers {
    @FXML
    Label waitLabel;
    private Client client;
    public void updateLabel(String label){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
               waitLabel.setText(label);
            }
        });
    }
    public void doShutdown(ActionEvent actionEvent) {
        client= Client.getInstance();
        if(client==null) {
            System.exit(0);
        }else{
            client.addGuiAction("/quit");
        }
    }
}
