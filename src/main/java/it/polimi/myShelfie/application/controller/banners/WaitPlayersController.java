package it.polimi.myShelfie.application.controller.banners;

import it.polimi.myShelfie.application.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class WaitPlayersController {
    @FXML
    Label waitLabel;
    private Client client;

    @FXML
    public void initialize() {
        client = Client.getInstance();
        client.setWaitPlayersController(this);
        System.out.println("WaitPlayerController initialized");
        waitLabel.setText("Waiting for players... (1/"+client.playerNumber+")");
    }
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
