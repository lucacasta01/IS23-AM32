package it.polimi.myShelfie.application;

import it.polimi.myShelfie.controller.GUIcontroller.LoginController;
import it.polimi.myShelfie.controller.GUIcontroller.ChatController;
import it.polimi.myShelfie.controller.GUIcontroller.banners.ErrorBannerController;
import it.polimi.myShelfie.utilities.beans.ChatMessage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class GUIClient extends Application {
    private static GUIClient instance;
    private Stage stage;
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
    public void start(Stage stage) {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/loginPanel.fxml"));
        fxmlLoader.setController(new LoginController());
        Parent root=null;
        try {
            root = fxmlLoader.load();
        }catch (Exception e){
            throw new RuntimeException("Login panel loading failed");
        }
        stage.setTitle("My Shelfie");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/graphics/publisherMaterial/Icon.png")).toString()));
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
                    loader = new FXMLLoader(getClass().getResource("/errorBanner.fxml"));
                } catch (Exception e) {
                    throw new RuntimeException("Error banner loading failed");
                }
                Parent root = null;
                try {
                    root = (Parent) loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ErrorBannerController controller = loader.getController();
                controller.setLabel(errorLabel);
                bannerStage.setScene(new Scene(root));
                bannerStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/graphics/icons/error.png")).getPath()));
                Screen.getPrimary().getBounds().getHeight();
                bannerStage.setResizable(false);
                bannerStage.show();
            }
        });
    }

    public Stage getStage() {
        return stage;
    }


    public static void main(String[] args) {
        launch();
    }

    public void showOldGameChoice(){
        System.out.println("Show old game choice");
        Platform.runLater(()->{
            Parent choice = null;
            try {
                choice = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/oldGameJoinBanner.fxml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage banStage = new Stage();
            banStage.setTitle("My Shelfie");
            banStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/graphics/publisherMaterial/Icon.png")).getPath()));
            banStage.setMinWidth(300);
            banStage.setMinHeight(200);
            banStage.setWidth(300);
            banStage.setHeight(200);
            banStage.setScene(new Scene(choice));
            banStage.show();
        });
    }

    public void switchToWaitingScene() {
        Platform.runLater(()->{
            Parent waitPlayers = null;
            try {
                waitPlayers = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/waitPlayerBanner.fxml")));
            } catch (IOException e) {
                throw new RuntimeException("Waiting scene failed to load");
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
                menu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/menuPanel.fxml")));
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
            Parent gameParent = null;
            try {
                gameParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/gamePanel.fxml")));
            } catch (IOException e) {
                throw new RuntimeException("Game panel loading failed");
            }

            stage.setMinWidth(1440);
            stage.setMinHeight(850);
            stage.setHeight(850);
            stage.setWidth(1440);
            stage.setScene(new Scene(gameParent));
            //stage.setFullScreen(true);
        });
    }
    public void addMyChatMessage(String message){
        ChatController chatController = Client.getInstance().getChatController();
        chatController.addMessage(new ChatMessage(Client.getInstance().getNickname(), message, null));
    }

    public void showDenyDialog(String errorMessage) {
        Platform.runLater(()->{
            System.out.println("Show deny ban");
            Parent denyPanel = null;
            FXMLLoader loader = null;
            try {
                loader = new FXMLLoader(getClass().getResource("/errorBanner.fxml"));
            } catch (Exception e) {
                throw new RuntimeException("Error banner loading failed");
            }

            try {
                denyPanel = loader.load();
            } catch (IOException e) {
                throw new RuntimeException("Error banner loading failed");
            }
            ErrorBannerController controller = loader.getController();
            controller.setLabel(errorMessage);
            Stage ban = new Stage();
            ban.setTitle("Warning!");
            ban.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/graphics/icons/alert.png")).getPath()));
            ban.setScene(new Scene(denyPanel));
            ban.show();
        });
    }

    public void switchToPlayerNumber() {
        Platform.runLater(()->{
            Parent numberPanel = null;
            try {
                numberPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/chosePlayerNumber.fxml")));
            } catch (IOException e) {
                throw new RuntimeException("Player number chose form loading failed");
            }
            stage.setScene(new Scene(numberPanel));
        });
    }


    public void showBackToMenu() {
        Platform.runLater(()->{
            Parent choice = null;
            try {
                choice = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/confirmExitBanner.fxml")));
            } catch (IOException e) {
                throw new RuntimeException("Menu loading failed");
            }
            Stage banStage = new Stage();
            banStage.setTitle("Back to menu");
            banStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/graphics/icons/alert.png")).getPath()));
            banStage.setMinWidth(300);
            banStage.setMinHeight(200);
            banStage.setWidth(300);
            banStage.setHeight(200);
            banStage.setFullScreen(false);
            banStage.setScene(new Scene(choice));
            banStage.show();
        });
    }
}