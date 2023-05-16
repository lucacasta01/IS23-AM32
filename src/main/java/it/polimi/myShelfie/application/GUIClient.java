package it.polimi.myShelfie.application;

import it.polimi.myShelfie.application.GUIcontroller.LoginController;
import it.polimi.myShelfie.application.GUIcontroller.banners.ErrorBannerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

public class GUIClient extends Application {
    private static GUIClient instance;
    private static Stage stage;
    public static synchronized GUIClient getInstance() {
        if(instance == null){
            System.out.println("created new client");
            instance = new GUIClient();
        }
        return instance;
    }


    public GUIClient(){
        super();
        instance = this;
    }
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
       FXMLLoader fxmlLoader = new FXMLLoader();
       Parent root = fxmlLoader.load(Paths.get("src/resources/loginPage.fxml").toUri().toURL());
       LoginController loginController = fxmlLoader.getController();
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

    public void showErrorBanner(String errorLabel){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage bannerStage = new Stage();
                FXMLLoader loader = null;
                try {
                    loader = new FXMLLoader(Paths.get("src/resources/errorBanner.fxml").toUri().toURL());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Parent root = null;
                try {
                    root = (Parent) loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ErrorBannerController controller = loader.getController();
                controller.setLabel(errorLabel);
                //Parent root = FXMLLoader.load(Paths.get("src/resources/errorBanner.fxml").toUri().toURL());
                bannerStage.setScene(new Scene(root));
                try {
                    bannerStage.getIcons().add(new Image(Paths.get("src/resources/graphics/icons/error.png").toUri().toURL().openStream()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                bannerStage.setResizable(false);
                bannerStage.show();
            }
        });
    }

    public Stage getStage() {
        return stage;
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void switchToWaitingScene() {
        Platform.runLater(()->{
            Parent waitPlayers = null;
            try {
                waitPlayers = FXMLLoader.load(Paths.get("src/resources/waitPlayerBan.fxml").toUri().toURL());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setMinWidth(300);
            stage.setMinHeight(200);
            stage.setWidth(300);
            stage.setHeight(200);
            stage.setScene(new Scene(waitPlayers));
        });
    }
    public void switchToMenu() {
        Platform.runLater(()->{
            Parent menu = null;
            try {
                menu = FXMLLoader.load(Paths.get("src/resources/menuPage.fxml").toUri().toURL());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setMinWidth(820);
            stage.setMinHeight(520);
            stage.setWidth(820);
            stage.setHeight(520);
            stage.setScene(new Scene(menu));
        });
    }

    public void switchToGame() {
        Platform.runLater(()->{
            Parent gamePanel = null;
            try {
                gamePanel = FXMLLoader.load(Paths.get("src/resources/gamePanel.fxml").toUri().toURL());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setMinWidth(820);
            stage.setMinHeight(520);
            stage.setScene(new Scene(gamePanel));
            stage.setFullScreen(true);
        });
    }

    public void showDenyBan() {
        Platform.runLater(()->{
            System.out.println("Show deny ban");
            Parent denyPanel = null;
            FXMLLoader loader = null;
            try {
                loader = new FXMLLoader(Paths.get("src/resources/errorBanner.fxml").toUri().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            try {
                denyPanel =(Parent) loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ErrorBannerController controller = loader.getController();
            controller.setLabel("NO OLD GAME FOUND");
            Stage ban = new Stage();
            ban.setScene(new Scene(denyPanel));
            ban.show();
        });
    }
}