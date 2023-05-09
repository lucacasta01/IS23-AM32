package it.polimi.myShelfie.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class GUIClient extends Application {
    @Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(Paths.get("src/resources/loginPage.fxml").toUri().toURL());
       stage.setTitle("My Shelfie");
       stage.getIcons().add(new Image(Paths.get("src/resources/graphics/publisherMaterial/Icon.png").toUri().toURL().openStream()));
       stage.setScene(new Scene(root));
       stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}