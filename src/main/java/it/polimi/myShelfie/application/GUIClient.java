package it.polimi.myShelfie.application;

import it.polimi.myShelfie.view.GUIcontroller.ChatController;
import it.polimi.myShelfie.view.GUIcontroller.banners.ErrorBannerController;
import it.polimi.myShelfie.view.GUIcontroller.banners.RankController;
import it.polimi.myShelfie.view.GUIcontroller.banners.ServerOfflineController;
import it.polimi.myShelfie.utilities.pojo.ChatMessage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class GUIClient extends Application {
    private static GUIClient instance;
    private Stage stage;

    /**
     * get instance method for singleton pattern
     * @return a new instance of GUIclient if instance is null, instance if it's not null
     */
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
        Parent root;
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

    /**
     *
     * @return this gui application's stage
     */
    public Stage getStage() {
        return stage;
    }


    public static void main(String[] args) {
        launch();
    }

    /**
     * show old game choice banner
     */
    public void showOldGameChoice(){
        System.out.println("Show old game choice");
        Platform.runLater(()->{
            Parent choice;
            try {
                choice = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/oldGameJoinBanner.fxml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage banStage = new Stage();
            banStage.setTitle("My Shelfie");
            banStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/graphics/publisherMaterial/Icon.png")).toString()));
            banStage.setMinWidth(300);
            banStage.setMinHeight(200);
            banStage.setWidth(300);
            banStage.setHeight(200);
            banStage.setScene(new Scene(choice));
            banStage.show();
        });
    }

    /**
     * switch the scene to waiting for players scene
     */
    public void switchToWaitingScene() {
        Platform.runLater(()->{
            Parent waitPlayers;
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

    /**
     * switch the scene to menu scene
     */
    public void switchToMenu() {
        Platform.runLater(()->{
            Parent menu;
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

    /**
     * switch scene to game scene
     */
    public void switchToGame() {
        Platform.runLater(()->{
            Parent gameParent;
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

    /**
     * adds my chat message to the chat room
     * @param message
     */
    public void addMyChatMessage(String message){
        ChatController chatController = Client.getInstance().getChatController();
        chatController.addMessage(new ChatMessage(Client.getInstance().getNickname(), message, null));
    }

    /**
     * show a generic error message
     * @param errorMessage to be shown
     */
    public void showDenyDialog(String errorMessage) {
        Platform.runLater(()->{
            System.out.println("Show deny ban");
            Parent denyPanel;
            FXMLLoader loader;
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
            ban.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/graphics/icons/alert.png")).toString()));
            ban.setScene(new Scene(denyPanel));
            ban.show();
        });
    }

    /**
     * switch the scene to choose player number scene
     */
    public void switchToPlayerNumber() {
        Platform.runLater(()->{
            Parent numberPanel;
            try {
                numberPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/chosePlayerNumber.fxml")));
            } catch (IOException e) {
                throw new RuntimeException("Player number chose form loading failed");
            }
            stage.setScene(new Scene(numberPanel));
        });
    }

    /**
     * show exit game choice banner
     */
    public void showBackToMenu() {
        Platform.runLater(()->{
            Parent choice;
            try {
                choice = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/confirmExitBanner.fxml")));
            } catch (IOException e) {
                throw new RuntimeException("Menu loading failed");
            }
            Stage banStage = new Stage();
            banStage.setTitle("Back to menu");
            banStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/graphics/icons/alert.png")).toString()));
            banStage.setMinWidth(300);
            banStage.setMinHeight(200);
            banStage.setWidth(300);
            banStage.setHeight(200);
            banStage.setFullScreen(false);
            banStage.setScene(new Scene(choice));
            banStage.show();
        });
    }

    /**
     * show the game rank
     * @param rank computed by the server
     */
    public void showRank(String rank) {
        Platform.runLater(()->{
            Parent rankPanel;
            FXMLLoader loader;
            try {
                loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/rankPanel.fxml")));
                rankPanel = loader.load();
            } catch (Exception e) {
                throw new RuntimeException("Rank panel loading failed");
            }
            RankController ctrl = loader.getController();
            ctrl.setRank(rank);
            Stage rankStage = new Stage();
            rankStage.setOnCloseRequest(event -> switchToMenu());
            rankStage.setScene(new Scene(rankPanel));
            rankStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/graphics/icons/rank.png")).toString()));
            rankStage.show();
        });
    }

    public void showServerOfflineBan() {
        Platform.runLater(()->{
            Parent banner;
            FXMLLoader loader;
            try {
                loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/serverOfflineBanner.fxml")));
                banner = loader.load();
            } catch (Exception e) {
                throw new RuntimeException("Server offline banner loading failed");
            }
            ServerOfflineController ctrl = loader.getController();

            Stage bannerStage = new Stage();
            bannerStage.setOnCloseRequest(event -> ctrl.doShutdown(null));
            bannerStage.setScene(new Scene(banner));
            bannerStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/graphics/icons/alert.png")).toString()));
            bannerStage.show();
        });
    }
}