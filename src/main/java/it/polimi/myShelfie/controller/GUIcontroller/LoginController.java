package it.polimi.myShelfie.controller.GUIcontroller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Objects;


public class LoginController {
    private String nickname;
    private String connectionProtocol;
    private Stage stage;
    @FXML
    RadioButton TCPrbtn, RMIrbtn;
    @FXML
    TextField nicknameField;
    @FXML
    Label nicknameDeniedLbl;
    @FXML
    JFXHamburger myHamburger;
    @FXML
    JFXDrawer drawer;

    public void initialize() {
        Client.getInstance();
        VBox configPanel = null;
        URL url = getClass().getResource("/configPanel.fxml");
        try {
            configPanel = FXMLLoader.load(url);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        drawer.setSidePane(configPanel);
        drawer.setVisible(false);

        GUIClient.getInstance().getStage().setWidth(GUIClient.getInstance().getStage().getWidth() + 1);
        GUIClient.getInstance().getStage().centerOnScreen();
        GUIClient.getInstance().getStage().setOnCloseRequest(e -> {
            GUIClient.getInstance().getStage().close();
            System.exit(0);
        });



        //hamburger transition
        HamburgerBasicCloseTransition transition = new HamburgerBasicCloseTransition(myHamburger);
        transition.setRate(-1);
        myHamburger.addEventHandler(MouseEvent.MOUSE_CLICKED,(e)->{
            transition.setRate(transition.getRate()*-1);
            transition.play();
            if(drawer.isOpened()){
                drawer.close();
                drawer.setVisible(false);
            }else{
                drawer.open();
                drawer.setVisible(true);
            }
        });
    }

    public void doLogin(ActionEvent actionEvent) throws RemoteException {
        stage = (Stage)((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        if (TCPrbtn.isSelected()) {
            connectionProtocol = "TCP";
        } else if (RMIrbtn.isSelected()) {
            connectionProtocol = "RMI";
        }

        if (!nicknameField.getText().equals("")) {
            nickname = nicknameField.getText();
        }else{
            nickname=" ";
        }

        System.out.println("Nickname: " + nickname);
        System.out.println("Protocol: " + connectionProtocol);

        Client client = Client.getInstance();
        if(client.getLoginController() == null) {
            client.setGuiLoginController(this);
            client.setNickname(nickname);
            client.setConnectionProtocol(connectionProtocol);
            client.setGUI(true);
            client.run();
        }
        client.addGuiAction(nickname);
    }

    public void nicknameDenied(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run(){
                nicknameDeniedLbl.setText(message);
                nicknameDeniedLbl.setVisible(true);
            }

        });
    }

    public void loginAccepted() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Parent menuPage = null;
                try {
                    menuPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/menuPanel.fxml")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stage.setScene(new Scene(menuPage));
            }
        });
    }

    public void doShutdown(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void serverOffline() throws IOException {
        GUIClient guiClient = GUIClient.getInstance();
        guiClient.showErrorBanner("Server is offline");
    }


}
