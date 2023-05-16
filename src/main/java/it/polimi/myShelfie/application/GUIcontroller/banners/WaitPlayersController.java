package it.polimi.myShelfie.application.GUIcontroller.banners;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
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
        String label = "Waiting for players... " + client.getWaitPlayerStatus();
        waitLabel.setVisible(true);
        waitLabel.setText(label);
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
            client.addGuiAction("/menu");
        }
    }
}
