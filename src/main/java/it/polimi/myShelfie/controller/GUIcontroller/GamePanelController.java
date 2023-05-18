package it.polimi.myShelfie.controller.GUIcontroller;

import com.jfoenix.controls.JFXDrawer;
import it.polimi.myShelfie.application.GUIClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class GamePanelController {

    @FXML
    ImageView boardImg;

    @FXML
    AnchorPane boardPane;
    @FXML
    GridPane boardGrid;
    @FXML
    JFXDrawer chatDrawer;

    @FXML
    Button openChatBtn;
    public void initialize() {
        boardImg.fitWidthProperty().bind(boardPane.widthProperty());
        boardImg.fitHeightProperty().bind(boardPane.heightProperty());

        boardGrid.prefWidth(boardPane.widthProperty().get());
        boardGrid.prefHeight(boardPane.heightProperty().get());

        //initializing chatroom panel
        VBox chatPanel = null;
        try {
            chatPanel = FXMLLoader.load(Paths.get("src/resources/chatPanel.fxml").toUri().toURL());
        }
        catch (IOException e){
            e.printStackTrace();
        }

        chatDrawer.setSidePane(chatPanel);
        chatDrawer.setVisible(false);

    }

    public void quit(ActionEvent actionEvent) {
        GUIClient.getInstance().showBackToMenu();
    }

    public void openChatPanel(ActionEvent actionEvent) {
        if(chatDrawer.isOpened()){
            chatDrawer.close();
            chatDrawer.setVisible(false);
        }else{
            chatDrawer.open();
            chatDrawer.setVisible(true);
        }
    }
}
