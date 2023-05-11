package it.polimi.myShelfie.application.controller;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.controller.banners.WaitPlayersController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;

public class GUIChoosePlayerController {
    Client client;
    Stage stage;



    public void twoPlayersChoice(ActionEvent actionEvent) {
        stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        client = Client.getInstance();
        client.addGuiAction("2");
        client.playerNumber = "2";
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.load(Paths.get("src/resources/waitPlayerBan.fxml").toUri().toURL());
        }catch(Exception e){
            e.printStackTrace();
        }
        client.setWaitPlayersController(fxmlLoader.getController());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    public void threePlayersChoice(ActionEvent actionEvent) {
        stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        client = Client.getInstance();
        client.addGuiAction("3");
        client.playerNumber = "3";
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.load(Paths.get("src/resources/waitPlayerBan.fxml").toUri().toURL());
        }catch(Exception e){
            e.printStackTrace();
        }
        client.setWaitPlayersController(fxmlLoader.getController());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    public void fourPlayersChoice(ActionEvent actionEvent) {
        stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        client = Client.getInstance();
        client.addGuiAction("4");
        client.playerNumber = "4";
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.load(Paths.get("src/resources/waitPlayerBan.fxml").toUri().toURL());
        }catch(Exception e){
            e.printStackTrace();
        }
        client.setWaitPlayersController(fxmlLoader.getController());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    public void doShutdown(ActionEvent actionEvent) {
        client=Client.getInstance();
        if(client==null) {
            System.exit(0);
        }else{
            client.addGuiAction("/quit");
        }
    }
}
