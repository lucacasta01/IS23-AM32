package it.polimi.myShelfie.view.GUIcontroller.banners;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class ConfirmExitController {
    /**
     * adds a /menu action
     * @param actionEvent
     */
    public void yesChoice(ActionEvent actionEvent) {
        Stage stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        Client.getInstance().addGuiAction("/menu");
        GUIClient.getInstance().switchToMenu();
        stage.close();
    }

    /**
     * close the dialog
     * @param actionEvent
     */
    public void noChoice(ActionEvent actionEvent) {
        Stage stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
