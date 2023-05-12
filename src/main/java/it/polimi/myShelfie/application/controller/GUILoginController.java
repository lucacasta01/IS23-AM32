package it.polimi.myShelfie.application.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import it.polimi.myShelfie.application.controller.banners.ErrorBannerController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.rmi.RemoteException;


public class GUILoginController {
    private String nickname;
    private String connectionProtocol;
    private Client client=null;
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
    @FXML
    ImageView bannerImg;

    @FXML
    HBox bannerContainer;

    public void initialize() {
        VBox configPanel = null;
        try {
            configPanel = FXMLLoader.load(Paths.get("src/resources/configPanel.fxml").toUri().toURL());
        }
        catch (IOException e){
            e.printStackTrace();
        }
        drawer.setSidePane(configPanel);
        drawer.setVisible(false);



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
        }
        System.out.println("Nickname: " + nickname);
        System.out.println("Protocol: " + connectionProtocol);

        client = Client.getInstance();
        client.setGuiLoginController(this);
        client.setNickname(nickname);
        client.setConnectionProtocol(connectionProtocol);
        client.setGUI(true);
        client.run();
        client.addGuiAction(nickname);
    }

    public void nicknameDenied(){
        nicknameDeniedLbl.setVisible(true);
    }

    public void loginAccepted() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Parent menuPage = null;
                try {
                    menuPage = FXMLLoader.load(Paths.get("src/resources/menuPage.fxml").toUri().toURL());
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

    /*
    public void openConfigPanel(MouseEvent mouseEvent) {
        HamburgerBasicCloseTransition transition = new HamburgerBasicCloseTransition(myHamburger);
        transition.setRate(transition.getRate()*-1);
        transition.play();



        drawer.open();


        if (GUIClient.getInstance().isConfigPanelOpen) {
            System.out.println("drawer is opened");
            drawer.close();
            drawer.setVisible(false);
            GUIClient.getInstance().isConfigPanelOpen = false;

        } else {
            System.out.println("drawer is closed");
            drawer.setVisible(true);
            drawer.open();
            GUIClient.getInstance().isConfigPanelOpen = true;
        }
    }
     */
}
