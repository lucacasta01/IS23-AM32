package it.polimi.myShelfie.controller.GUIcontroller;

import com.jfoenix.controls.JFXDrawer;
import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.utilities.beans.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePanelController{
    @FXML
    GridPane boardGrid, myShelfGrid, otherShelfGrid1, otherShelfGrid2, otherShelfGrid3;
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
    @FXML
    HBox collectedTilesBox;
    @FXML
    Button col1Btn, col2Btn, col3Btn, col4Btn, col5Btn, collectRstBtn;
    Button[] colBtns;
    List<Image> collectedTiles = new ArrayList<>();


    public void initialize() {
        initializeChatPanel();
        colBtns  = new Button[]{col1Btn,col2Btn,col3Btn,col4Btn,col5Btn,collectRstBtn};
        Client.getInstance().setGamePanelController(this);
        addGridEvent();
        setColBtnsEnabled(false);
    }

    private void setColBtnsEnabled(boolean value){
        for (Button colBtn : colBtns) {
            colBtn.setDisable(!value);
        }
    }

    private void addGridEvent() {
        boardGrid.getChildren().forEach(item -> {
            item.setOnMouseClicked(event -> {
                if(collectedTiles.size()<3&&isMyTurn()) {
                    Node clickedNode = event.getPickResult().getIntersectedNode();
                    ImageView im = (ImageView) clickedNode;
                    clickedNode.setVisible(false);
                    collectedTiles.add(im.getImage());
                    printCollectedTiles();
                    setColBtnsEnabled(true);
                }
            });
        });
    }

    private boolean isMyTurn() {
        return  Client.getInstance().getView().getCurrentPlayer().equals(Client.getInstance().getNickname());
    }

    private void printCollectedTiles() {
        collectedTilesBox.getChildren().clear();
        for(Image im: collectedTiles){
            ImageView imv = new ImageView(im);
            imv.setFitHeight(69);
            imv.setFitWidth(69);
            HBox.setMargin(imv, new Insets(0,0,0,5));
            collectedTilesBox.getChildren().add(imv);
        }
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

        //current player update & turn handling
        Platform.runLater(()->{
            curPlayerLbl.setText(curNickname);
            handleTurn(view,curNickname,curPlayerLbl);
            curPlScoreLbl.setText("Score: "+view.getGUIScoring().get(view.getPlayers().indexOf(curNickname)).toString());
        });



        //other players update
        if(playersNumber == 2) {
            Platform.runLater(()->{
                player3Box.setVisible(false);
                player4Box.setVisible(false);

                player2Lbl.setText(otherPlayers.get(0));
                handleTurn(view,otherPlayers.get(0),player2Lbl);
                pl2ScoreLbl.setText("Score: "+otherScores.get(0));

                //update other player shelf
                int i = 0;
                for (int row = 0; row < Settings.SHELFROW; row++) {
                    for (int col = 0; col < Settings.SHELFCOLUMN; col++) {
                        Node n = getTileImgView(row, col, otherShelfGrid1);
                        if (n != null) {
                            if (!view.getOthersGUIShelves().get(0).get(i).contains("transparent.png")) {
                                System.out.println("putting not transparent tile, row: "+row+" column: "+col);
                                Image image = new Image(Paths.get(view.getOthersGUIShelves().get(0).get(i)).toUri().toString());
                                ImageView im = (ImageView) n;
                                im.setImage(image);
                                im.setVisible(true);
                            }
                            i++;
                        }
                    }
                }
                otherShelfGrid1.setVisible(true);

            });
        }
        else if(playersNumber == 3){
            Platform.runLater(()->{
                player4Box.setVisible(false);

                player2Lbl.setText(otherPlayers.get(0));
                handleTurn(view,otherPlayers.get(0),player2Lbl);
                pl2ScoreLbl.setText("Score: "+otherScores.get(0));

                player3Lbl.setText(otherPlayers.get(1));
                handleTurn(view,otherPlayers.get(1),player3Lbl);
                pl3ScoreLbl.setText("Score: "+otherScores.get(1));

                //update first other player shelf
                int i = 0;
                for (int row = 0; row < Settings.SHELFROW; row++) {
                    for (int col = 0; col < Settings.SHELFCOLUMN; col++) {
                        Node n = getTileImgView(row, col, otherShelfGrid1);
                        if (n != null) {
                            if (!view.getOthersGUIShelves().get(0).get(i).contains("transparent.png")) {
                                System.out.println("putting not transparent tile, row: "+row+" column: "+col);
                                Image image = new Image(Paths.get(view.getOthersGUIShelves().get(0).get(i)).toUri().toString());
                                ImageView im = (ImageView) n;
                                im.setImage(image);
                                im.setVisible(true);
                            }
                            i++;
                        }
                    }
                }
                otherShelfGrid1.setVisible(true);

                //update second other player shelf
                i = 0;
                for (int row = 0; row < Settings.SHELFROW; row++) {
                    for (int col = 0; col < Settings.SHELFCOLUMN; col++) {
                        Node n = getTileImgView(row, col, otherShelfGrid2);
                        if (n != null) {
                            if (!view.getOthersGUIShelves().get(1).get(i).contains("transparent.png")) {
                                System.out.println("putting not transparent tile, row: "+row+" column: "+col);
                                Image image = new Image(Paths.get(view.getOthersGUIShelves().get(1).get(i)).toUri().toString());
                                ImageView im = (ImageView) n;
                                im.setImage(image);
                                im.setVisible(true);
                            }
                            i++;
                        }
                    }
                }
                otherShelfGrid2.setVisible(true);

            });
        }
        else{
            Platform.runLater(()->{
                player2Lbl.setText(otherPlayers.get(0));
                handleTurn(view,otherPlayers.get(0),player2Lbl);
                pl2ScoreLbl.setText("Score: "+otherScores.get(0));

                player3Lbl.setText(otherPlayers.get(1));
                handleTurn(view,otherPlayers.get(1),player3Lbl);
                pl2ScoreLbl.setText("Score: "+otherScores.get(1));

                player3Lbl.setText(otherPlayers.get(2));
                handleTurn(view,otherPlayers.get(2),player3Lbl);
                pl2ScoreLbl.setText("Score: "+otherScores.get(3));

                //update first other player shelf
                int i = 0;
                for (int row = 0; row < Settings.SHELFROW; row++) {
                    for (int col = 0; col < Settings.SHELFCOLUMN; col++) {
                        Node n = getTileImgView(row, col, otherShelfGrid1);
                        if (n != null) {
                            if (!view.getOthersGUIShelves().get(0).get(i).contains("transparent.png")) {
                                System.out.println("putting not transparent tile, row: "+row+" column: "+col);
                                Image image = new Image(Paths.get(view.getOthersGUIShelves().get(0).get(i)).toUri().toString());
                                ImageView im = (ImageView) n;
                                im.setImage(image);
                                im.setVisible(true);
                            }
                            i++;
                        }
                    }
                }
                otherShelfGrid1.setVisible(true);

                //update second other player shelf
                i = 0;
                for (int row = 0; row < Settings.SHELFROW; row++) {
                    for (int col = 0; col < Settings.SHELFCOLUMN; col++) {
                        Node n = getTileImgView(row, col, otherShelfGrid2);
                        if (n != null) {
                            if (!view.getOthersGUIShelves().get(1).get(i).contains("transparent.png")) {
                                System.out.println("putting not transparent tile, row: "+row+" column: "+col);
                                Image image = new Image(Paths.get(view.getOthersGUIShelves().get(1).get(i)).toUri().toString());
                                ImageView im = (ImageView) n;
                                im.setImage(image);
                                im.setVisible(true);
                            }
                            i++;
                        }
                    }
                }
                otherShelfGrid2.setVisible(true);

                //update third other player shelf
                i = 0;
                for (int row = 0; row < Settings.SHELFROW; row++) {
                    for (int col = 0; col < Settings.SHELFCOLUMN; col++) {
                        Node n = getTileImgView(row, col, otherShelfGrid3);
                        if (n != null) {
                            if (!view.getOthersGUIShelves().get(2).get(i).contains("transparent.png")) {
                                System.out.println("putting not transparent tile, row: "+row+" column: "+col);
                                Image image = new Image(Paths.get(view.getOthersGUIShelves().get(2).get(i)).toUri().toString());
                                ImageView im = (ImageView) n;
                                im.setImage(image);
                                im.setVisible(true);
                            }
                            i++;
                        }
                    }
                }
                otherShelfGrid3.setVisible(true);
            });
        }
        //board update

        Platform.runLater(()-> {
                    int i = 0;
                    for (int row = 0; row < Settings.BOARD_DIM; row++) {
                        for (int col = 0; col < Settings.BOARD_DIM; col++) {
                            Node n = getTileImgView(row, col, boardGrid);
                            if (n != null) {
                                if (!view.getGUIboard().get(i).contains("transparent.png")) {
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
        Platform.runLater(()->{
            personalGoal.setImage(new Image(Paths.get(view.getGUIpersonalCard()).toUri().toString()));
            personalGoal.setVisible(true);
        });


        //common goals update
        Platform.runLater(()->{
            sharedGoal1.setImage(new Image(Paths.get(view.getGUIsharedCards().get(0)).toUri().toString()));
            sharedGoal2.setImage(new Image(Paths.get(view.getGUIsharedCards().get(1)).toUri().toString()));
            sharedGoal1.setVisible(true);
            sharedGoal2.setVisible(true);
        });

        //myShelf update
        Platform.runLater(()->{
            int i = 0;
            for (int row = 0; row < Settings.SHELFROW; row++) {
                for (int col = 0; col < Settings.SHELFCOLUMN; col++) {
                    Node n = getTileImgView(row, col, myShelfGrid);
                    if (n != null) {
                        if (!view.getMyShelf().get(i).contains("transparent.png")) {
                            System.out.println("putting not transparent tile, row: "+row+" column: "+col);
                            Image image = new Image(Paths.get(view.getMyShelf().get(i)).toUri().toString());
                            ImageView im = (ImageView) n;
                            im.setImage(image);
                            im.setVisible(true);
                        }
                        i++;
                    }
                }
            }
            myShelfGrid.setVisible(true);
        });
        System.out.println("GUI VIEW UPDATED");
    }

    private void handleTurn(View v, String nickname, Label l){
        if(v.getCurrentPlayer().equals(nickname)){
            l.setText(l.getText()+" (your turn)");
        }
    }

    private Node getTileImgView(int row, int column, GridPane grid){
        for(Node n: grid.getChildren()){
            Integer r, c;
            r=GridPane.getRowIndex(n);
            c=GridPane.getColumnIndex(n);
            if(r==null||c==null){
                return null;
            }else{
                if(r==row && c==column){
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

    public void insertCol1(ActionEvent actionEvent) {
    }

    public void insertCol2(ActionEvent actionEvent) {
    }

    public void insertCol3(ActionEvent actionEvent) {
    }

    public void insertCol4(ActionEvent actionEvent) {
    }

    public void insertCol5(ActionEvent actionEvent) {
    }

    public void resetCollectedTiles(ActionEvent actionEvent) {
        setColBtnsEnabled(false);
        collectedTiles.clear();
        boardGrid.getChildren().forEach(tile->tile.setVisible(true));
        collectedTilesBox.getChildren().clear();
    }
}
