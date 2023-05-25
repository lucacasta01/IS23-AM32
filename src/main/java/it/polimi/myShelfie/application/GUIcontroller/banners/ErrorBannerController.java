package it.polimi.myShelfie.application.GUIcontroller.banners;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorBannerController {
    @FXML
    Label bannerLabel;
    public void doShutdown(ActionEvent actionEvent) {
        Stage stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
    public void setLabel(String label){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bannerLabel.setText(label);
            }
        });
    }
}
