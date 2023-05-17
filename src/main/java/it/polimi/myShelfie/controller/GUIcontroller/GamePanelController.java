package it.polimi.myShelfie.controller.GUIcontroller;

import it.polimi.myShelfie.application.GUIClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;

public class GamePanelController {
    @FXML
    ImageView boardImg;

    @FXML
    AnchorPane boardPane;
    @FXML
    GridPane boardGrid;
    public void initialize() {
        boardImg.fitWidthProperty().bind(boardPane.widthProperty());
        boardImg.fitHeightProperty().bind(boardPane.heightProperty());

        boardGrid.prefWidth(boardPane.widthProperty().get());
        boardGrid.prefHeight(boardPane.heightProperty().get());
    }

    public void quit(ActionEvent actionEvent) {
        GUIClient.getInstance().showBackToMenu();
    }
}
