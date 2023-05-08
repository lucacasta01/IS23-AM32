package it.polimi.myShelfie.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GUIClient extends Application {
    @Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(getClass().getResource("src/resources/Untitled.fxml"));
       stage.setTitle("My Shelfie");
       stage.setScene(new Scene(root));
       stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}