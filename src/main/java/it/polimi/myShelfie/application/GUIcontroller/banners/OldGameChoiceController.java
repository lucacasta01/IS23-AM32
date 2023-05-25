package it.polimi.myShelfie.application.GUIcontroller.banners;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class OldGameChoiceController {

    public void yesChoice(ActionEvent actionEvent) {
        Stage stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        Client.getInstance().addGuiAction("Y");
        GUIClient.getInstance().switchToWaitingScene();
        stage.close();
    }

    public void noChoice(ActionEvent actionEvent) {
        Stage stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        Client.getInstance().addGuiAction("N");
        stage.close();
    }
}
