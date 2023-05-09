package it.polimi.myShelfie.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class GUIClient extends Application {
    @Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(Paths.get("src/resources/Untitled.fxml").toUri().toURL());
       stage.setTitle("My Shelfie");
       stage.setScene(new Scene(root));
       stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}