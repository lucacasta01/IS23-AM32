package it.polimi.myShelfie.application;

import it.polimi.myShelfie.application.controller.GUILoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class GUIClient extends Application {
    @Override
    public void start(Stage stage) throws Exception {
       FXMLLoader fxmlLoader = new FXMLLoader();
       Parent root = fxmlLoader.load(Paths.get("src/resources/loginPage.fxml").toUri().toURL());
       GUILoginController guiLoginController = fxmlLoader.getController();
       //todo handle guiController methods from there, pass actions to a client
        new Thread(()->{

        }).start();

       stage.setTitle("My Shelfie");
       stage.getIcons().add(new Image(Paths.get("src/resources/graphics/publisherMaterial/Icon.png").toUri().toURL().openStream()));
       stage.setScene(new Scene(root));
       stage.setMinWidth(820);
       stage.setMinHeight(520);
       stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}