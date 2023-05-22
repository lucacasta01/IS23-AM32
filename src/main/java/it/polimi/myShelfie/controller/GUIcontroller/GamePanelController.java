package it.polimi.myShelfie.controller.GUIcontroller;

import com.jfoenix.controls.JFXDrawer;
import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.utilities.beans.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GamePanelController {
    @FXML
    GridPane boardGrid;
    @FXML
    JFXDrawer chatDrawer;
    @FXML
    Label curPlayerLbl, player2Lbl, player3Lbl, player4Lbl;
    @FXML
    Label curPlScoreLbl, pl2ScoreLbl, pl3ScoreLbl, pl4ScoreLbl;
    @FXML
    VBox player2Box, player3Box, player4Box;
    @FXML
    ImageView sharedGoal1, sharedGoal2, personalGoal;

    public void initialize() {
        initializeChatPanel();
        Client.getInstance().setGamePanelController(this);
        //updateView();
    }

    public void updateView() {
        View view = Client.getInstance().getView();
        String curNickname = Client.getInstance().getNickname();

        int playersNumber = view.getPlayers().size();
        int curPlayerIndex = view.getPlayers().indexOf(curNickname);
        List<String> otherPlayers = new ArrayList<>(view.getPlayers());
        List<Integer> otherScores = new ArrayList<>(view.getGUIScoring());

        otherPlayers.remove(curPlayerIndex);
        otherScores.remove(curPlayerIndex);

        //current player update
        Platform.runLater(()->{
            curPlayerLbl.setText(curNickname);
            curPlScoreLbl.setText("Score: "+view.getGUIScoring().get(view.getPlayers().indexOf(curNickname)).toString());
        });



        //other players update
        if(playersNumber == 2) {
            Platform.runLater(()->{
                player3Box.setVisible(false);
                player4Box.setVisible(false);

                player2Lbl.setText(otherPlayers.get(0));
                pl2ScoreLbl.setText("Score: "+otherScores.get(0));
            });
        }
        else if(playersNumber == 3){
            Platform.runLater(()->{
                player4Box.setVisible(false);

                player2Lbl.setText(otherPlayers.get(0));
                pl2ScoreLbl.setText("Score: "+otherScores.get(0));

                player3Lbl.setText(otherPlayers.get(1));
                pl3ScoreLbl.setText("Score: "+otherScores.get(1));
            });
        }
        else{
            Platform.runLater(()->{
                player2Lbl.setText(otherPlayers.get(0));
                pl2ScoreLbl.setText("Score: "+otherScores.get(0));

                player3Lbl.setText(otherPlayers.get(1));
                pl2ScoreLbl.setText("Score: "+otherScores.get(1));

                player3Lbl.setText(otherPlayers.get(2));
                pl2ScoreLbl.setText("Score: "+otherScores.get(2));
            });
        }
        //board update

        Platform.runLater(()-> {
                    int i = 0;
                    for (int row = 0; row < Settings.BOARD_DIM; row++) {
                        for (int col = 0; col < Settings.BOARD_DIM; col++) {
                            //System.out.println("creating tile image: "+view.getGUIboard().get(i)+"\n");
                            Node n = getTileImgView(row, col, boardGrid);
                            if (n != null) {
                                if (!view.getGUIboard().get(i).contains("transparent.png")) {
                                    System.out.println("image url: " + view.getGUIboard().get(i));
                                    Image image = new Image(Paths.get(view.getGUIboard().get(i)).toUri().toString());
                                    ImageView im = (ImageView) n;
                                    im.setImage(image);
                                    im.setVisible(true);
                                }
                                i++;
                            }
                        }
                    }
                    boardGrid.setVisible(true);
                });

        //personal goal update
        personalGoal.setImage(new Image(Paths.get(view.getGUIpersonalCard()).toUri().toString()));
        personalGoal.setVisible(true);
        //todo

        //common goals update
        sharedGoal1.setImage(new Image(Paths.get(view.getGUIsharedCards().get(0)).toUri().toString()));
        sharedGoal2.setImage(new Image(Paths.get(view.getGUIsharedCards().get(1)).toUri().toString()));
        sharedGoal1.setVisible(true);
        sharedGoal2.setVisible(true);
        //todo


        System.out.println("GUI VIEW UPDATED");
    }

    private Node getTileImgView(int row, int column, GridPane grid){
        for(Node n: grid.getChildren()){
            Integer r, c;
            r=GridPane.getRowIndex(n);
            c=GridPane.getColumnIndex(n);
            if(r==null||c==null){
                System.out.println("null children");
                return null;
            }else{
                if(r==row && c==column){
                    System.out.println("not null children, row "+row+" column "+column);
                    return n;
                }
            }
        }
        return null;
    }

    private void initializeChatPanel() {
        VBox chatPanel = null;
        try {
            chatPanel = FXMLLoader.load(Paths.get("src/resources/chatPanel.fxml").toUri().toURL());
        }
        catch (IOException e){
            e.printStackTrace();
        }
        chatDrawer.setSidePane(chatPanel);
        chatDrawer.setVisible(false);
    }

    public void quit(ActionEvent actionEvent) {
        GUIClient.getInstance().showBackToMenu();
    }

    public void openChatPanel(ActionEvent actionEvent) {
        if(chatDrawer.isOpened()){
            chatDrawer.close();
            chatDrawer.setVisible(false);
        }else{
            chatDrawer.open();
            chatDrawer.setVisible(true);
        }
    }

    public void openRules(ActionEvent actionEvent) {
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File("src/resources/MyShelfie_Rulebook_ITA.pdf");
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }
    }
}