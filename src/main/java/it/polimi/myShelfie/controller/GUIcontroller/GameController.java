package it.polimi.myShelfie.controller.GUIcontroller;

import it.polimi.myShelfie.application.Client;
import javafx.event.ActionEvent;

public class GameController {

    public void doShutdown(ActionEvent actionEvent) {
        Client client = Client.getInstance();
        if(client==null){
            System.exit(15);
        }else{
            client.addGuiAction("/quit");
        }
    }
}
