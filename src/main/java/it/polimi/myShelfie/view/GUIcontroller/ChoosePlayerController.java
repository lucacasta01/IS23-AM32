package it.polimi.myShelfie.view.GUIcontroller;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import java.util.Objects;

public class ChoosePlayerController {
    Client client;
    Stage stage;


    @FXML
    AnchorPane backgroundPlayerChoose;

    public void initialize(){
        GUIClient.getInstance().getStage().setWidth(GUIClient.getInstance().getStage().getWidth()+5);
        GUIClient.getInstance().getStage().setWidth(GUIClient.getInstance().getStage().getWidth()-5);
        GUIClient.getInstance().getStage().centerOnScreen();
        GUIClient.getInstance().getStage().setOnCloseRequest(e -> {
            GUIClient.getInstance().getStage().close();
            Client.getInstance().addGuiAction("/quit");
        });
    }
    public void twoPlayersChoice(ActionEvent actionEvent) {
        stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        client = Client.getInstance();
        client.addGuiAction("2");
        client.setWaitPlayerStatus("(1/2)");
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.load(Objects.requireNonNull(getClass().getResource("/waitPlayerBanner.fxml")));
        }catch(Exception e){
            e.printStackTrace();
        }
        client.setWaitPlayersController(fxmlLoader.getController());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Parent waitPlayers;
                try {
                    waitPlayers = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/waitPlayerBanner.fxml")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stage.setMinWidth(300);
                stage.setMinHeight(200);
                stage.setWidth(300);
                stage.setHeight(200);
                stage.setScene(new Scene(waitPlayers));
            }
        });
    }

    public void threePlayersChoice(ActionEvent actionEvent) {
        stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        client = Client.getInstance();
        client.addGuiAction("3");
        client.setWaitPlayerStatus("(1/3)");
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.load(Objects.requireNonNull(getClass().getResource("/waitPlayerBanner.fxml")));
        }catch(Exception e){
            e.printStackTrace();
        }
        client.setWaitPlayersController(fxmlLoader.getController());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Parent waitPlayers;
                try {
                    waitPlayers = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/waitPlayerBanner.fxml")));
                } catch (IOException e) {
                    throw new RuntimeException("Wait players banner loading failed");
                }
                stage.setMinWidth(300);
                stage.setMinHeight(200);
                stage.setWidth(300);
                stage.setHeight(200);
                stage.setScene(new Scene(waitPlayers));
            }
        });
    }

    public void fourPlayersChoice(ActionEvent actionEvent) {
        stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        client = Client.getInstance();
        client.addGuiAction("4");
        client.setWaitPlayerStatus("(1/4)");
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.load(Objects.requireNonNull(getClass().getResource("/waitPlayerBanner.fxml")));
        }catch(Exception e){
            throw new RuntimeException("Wait players banner loading failed");
        }
        client.setWaitPlayersController(fxmlLoader.getController());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Parent waitPlayers;
                try {
                    waitPlayers = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/waitPlayerBanner.fxml")));
                } catch (IOException e) {
                    throw new RuntimeException("Wait players banner loading failed");
                }
                stage.setMinWidth(300);
                stage.setMinHeight(200);
                stage.setWidth(300);
                stage.setHeight(200);
                stage.setScene(new Scene(waitPlayers));
            }
        });
    }

    public void doShutdown(ActionEvent actionEvent) {
        client=Client.getInstance();
        if(client==null) {
            System.exit(0);
        }else{
            client.addGuiAction("/menu");
        }
    }
}
