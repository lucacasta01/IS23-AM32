package it.polimi.myShelfie.view.GUIcontroller;

import com.jfoenix.controls.JFXDrawer;
import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import it.polimi.myShelfie.utilities.Position;
import it.polimi.myShelfie.utilities.Settings;
import it.polimi.myShelfie.view.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.apache.commons.io.FileUtils;

import javax.print.attribute.standard.JobKOctets;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GamePanelController implements Initializable {
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
    VBox collectedTilesBox;
    @FXML
    Button collectRstBtn;
    @FXML
    VBox col1Box, col2Box, col3Box, col4Box, col5Box;
    @FXML
    ImageView chatNotificationImg;
    @FXML
    ImageView myChairImg, p2ChairImg, p3ChairImg, p4ChairImg;
    @FXML
    ImageView sharedGoal1Stack, sharedGoal2Stack;
    final List<ImageView> collectedTiles = new ArrayList<>();
    int column = -1;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void init(){
        initializeChatPanel();
        Client.getInstance().setGamePanelController(this);
        addGridEvent();
        setResetEnabled(false);
        chatNotificationImg.setVisible(false);
        System.out.println("init chairs");
        myChairImg.setVisible(false);
        p2ChairImg.setVisible(false);
        p3ChairImg.setVisible(false);
        p4ChairImg.setVisible(false);
        GUIClient.getInstance().getStage().setOnCloseRequest(e -> {
            GUIClient.getInstance().getStage().close();
            Client.getInstance().addGuiAction("/quit");
        });
    }

    private void setResetEnabled(boolean value){
        collectRstBtn.setDisable(!value);
    }

    private void addGridEvent() {
        boardGrid.getChildren().forEach(item -> item.setOnMouseClicked(event -> {
            if(collectedTiles.size()<3&&isMyTurn()) {
                Node clickedNode = event.getPickResult().getIntersectedNode();
                ImageView im = (ImageView) clickedNode;
                if(isCatchable(im,GridPane.getRowIndex(clickedNode),GridPane.getColumnIndex(clickedNode))) {
                    clickedNode.setVisible(false);
                    collectedTiles.add(im);
                    printCollectedTiles();
                    setResetEnabled(true);
                }
                else{
                    GUIClient.getInstance().showDenyDialog("You can't pick this tile!");
                }
            }
        }));
    }

    private boolean isCatchable(ImageView tile, int row, int column) {
        if (tile.getImage().getUrl().contains("transparent.png")) {
            return false;
        }
        for(ImageView imv : collectedTiles){
            if(row != GridPane.getRowIndex(imv) && column != GridPane.getColumnIndex(imv)) {
                return false;
            }
        }
        for(ImageView im:collectedTiles){
            if((Math.abs(row-GridPane.getRowIndex(im))>2)||(Math.abs(column-GridPane.getColumnIndex(im))>2)){
                return false;

            }
        }

        Boolean[][] grid = new Boolean[Settings.BOARD_DIM][Settings.BOARD_DIM];
        for(int i=0;i<Settings.BOARD_DIM;i++){
            for(int j=0;j<Settings.BOARD_DIM;j++){
                grid[i][j] = !((ImageView) Objects.requireNonNull(getTileImgView(i, j, boardGrid))).getImage().getUrl().contains("transparent.png");
            }
        }

        return  ((!grid[Math.min(row + 1, Settings.BOARD_DIM - 1)][column]) &&  !tilesMatch(Math.min(row + 1, Settings.BOARD_DIM - 1), column, row, column)) ||
                ((!grid[row][Math.min(column + 1, Settings.BOARD_DIM - 1)]) && !tilesMatch(row,Math.min(column + 1, Settings.BOARD_DIM - 1),row,column)) ||
                ((!grid[Math.max(row - 1, 0)][column]) && !tilesMatch(Math.max(row - 1, 0),column, row, column)) ||
                ((!grid[row][Math.max(column - 1, 0)]) && !tilesMatch(row,Math.max(column - 1, 0),row , column));
    }

    private boolean tilesMatch(int x1, int y1, int x2, int y2){
        return (x1==x2)&&(y1==y2);
    }

    private boolean isMyTurn() {
        return  Client.getInstance().getView().getCurrentPlayer().equals(Client.getInstance().getNickname());
    }

    private void printCollectedTiles() {
        collectedTilesBox.getChildren().clear();
        List<ImageView> reversed = new ArrayList<>(collectedTiles);
        Collections.reverse(reversed);
        for(ImageView im: reversed){
            ImageView imv = new ImageView(im.getImage());
            imv.setFitHeight(69);
            imv.setFitWidth(69);
            HBox.setMargin(imv, new Insets(0,0,5,0));
            collectedTilesBox.getChildren().add(imv);
        }
    }

    /**
     * Updates the GUI view
     */
    public void updateView() {
        System.out.println("Updating view");
        View view = Client.getInstance().getView();
        String curNickname = Client.getInstance().getNickname();
        int playersNumber = view.getPlayers().size();
        int curPlayerIndex = view.getPlayers().indexOf(curNickname);
        List<String> otherPlayers = new ArrayList<>(view.getPlayers());
        List<Integer> otherScores = new ArrayList<>(view.getGUIScoring());
        otherPlayers.remove(curPlayerIndex);
        otherScores.remove(curPlayerIndex);

        Platform.runLater(()->{
            if(curNickname.equals(view.getPlayers().get(0))){
                myChairImg.setVisible(true);
            }else{
                for(int i=0; i<otherPlayers.size(); i++){
                    String s = otherPlayers.get(i);
                    if(s.equals(view.getPlayers().get(0))) {
                        switch (i) {
                            case 0 -> {
                                p2ChairImg.setVisible(true);
                            }
                            case 1 -> {
                                p3ChairImg.setVisible(true);
                            }
                            case 2 -> {
                                p4ChairImg.setVisible(true);
                            }
                        }
                    }
                }
            }
            boardGrid.getChildren().forEach(item->item.setVisible(true));
        });


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
                            Image image = new Image(Objects.requireNonNull(getClass().getResource(view.getOthersGUIShelves().get(0).get(i))).toString());
                            ImageView im = (ImageView) n;
                            im.setImage(image);
                            im.setVisible(true);
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
                            Image image = new Image(Objects.requireNonNull(getClass().getResource(view.getOthersGUIShelves().get(0).get(i))).toString());
                            ImageView im = (ImageView) n;
                            im.setImage(image);
                            im.setVisible(true);
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
                            Image image = new Image(Objects.requireNonNull(getClass().getResource(view.getOthersGUIShelves().get(1).get(i))).toString());
                            ImageView im = (ImageView) n;
                            im.setImage(image);
                            im.setVisible(true);
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

                            Image image = new Image(Objects.requireNonNull(getClass().getResource(view.getOthersGUIShelves().get(0).get(i))).toString());
                            ImageView im = (ImageView) n;
                            im.setImage(image);
                            im.setVisible(true);
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
                            Image image = new Image(Objects.requireNonNull(getClass().getResource(view.getOthersGUIShelves().get(1).get(i))).toString());
                            ImageView im = (ImageView) n;
                            im.setImage(image);
                            im.setVisible(true);
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
                            Image image = new Image(Objects.requireNonNull(getClass().getResource(view.getOthersGUIShelves().get(2).get(i))).toString());
                            ImageView im = (ImageView) n;
                            im.setImage(image);
                            im.setVisible(true);
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
                                Image image = new Image(Objects.requireNonNull(getClass().getResource(view.getGUIboard().get(i))).toString());
                                ImageView im = (ImageView) n;
                                im.setImage(image);
                                im.setVisible(true);
                                i++;
                            }
                        }
                    }
                    boardGrid.setVisible(true);
                });

        //personal goal update
        Platform.runLater(()->{
            personalGoal.setImage(new Image(Objects.requireNonNull(getClass().getResource(view.getGUIpersonalCard())).toString()));
            personalGoal.setVisible(true);
        });


        //common goals update
        Platform.runLater(()->{
            sharedGoal1.setImage(new Image(Objects.requireNonNull(getClass().getResource(view.getGUIsharedCards().get(0))).toString()));
            sharedGoal2.setImage(new Image(Objects.requireNonNull(getClass().getResource(view.getGUIsharedCards().get(1))).toString()));
            sharedGoal1.setVisible(true);
            sharedGoal2.setVisible(true);
            sharedGoal1Stack.setImage(new Image(Objects.requireNonNull(getClass().getResource("/graphics/scoringTokens/" + view.getSharedCardsPointTokens().get(0).toString() + ".jpg")).toString()));
            sharedGoal2Stack.setImage(new Image(Objects.requireNonNull(getClass().getResource("/graphics/scoringTokens/" + view.getSharedCardsPointTokens().get(1).toString() + ".jpg")).toString()));
            sharedGoal1Stack.setVisible(true);
            sharedGoal2Stack.setVisible(true);
        });

        //myShelf update
        Platform.runLater(()->{
            int i = 0;
            for (int row = 0; row < Settings.SHELFROW; row++) {
                for (int col = 0; col < Settings.SHELFCOLUMN; col++) {
                    Node n = getTileImgView(row, col, myShelfGrid);
                    if (n != null) {
                        Image image = new Image(getClass().getResource(view.getCurPlayerGUIShelf().get(i)).toString());
                        ImageView im = (ImageView) n;
                        im.setImage(image);
                        im.setVisible(true);
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
        VBox chatPanel;
        try {
            chatPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/chatPanel.fxml")));
        }
        catch (IOException e){
            throw new RuntimeException("Chat panel loading failed");
        }
        chatDrawer.setSidePane(chatPanel);
        chatDrawer.setVisible(false);
    }

    /**
     * Opens a dialog that ask if you really want leave the game
     * @param actionEvent
     */
    public void quit(ActionEvent actionEvent) {
        GUIClient.getInstance().showBackToMenu();
    }


    /**
     * Show chat panel
     * @param actionEvent
     */
    public void openChatPanel(ActionEvent actionEvent) {
        if(chatDrawer.isOpened()){
            chatDrawer.close();
            chatDrawer.setVisible(false);
        }else{
            chatDrawer.open();
            chatDrawer.setVisible(true);
            chatNotificationImg.setVisible(false);
        }
    }

    /**
     * Copies from resources the pdf file to local machine and opens it with default app
     * @param actionEvent
     */
    public void openRules(ActionEvent actionEvent) {
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(System.getProperty("user.dir")+"/config/tmpRules.pdf");
                FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/MyShelfie_Rulebook_ITA.pdf"), myFile);
                Desktop.getDesktop().open(myFile);
            } catch (Exception e) {
                GUIClient.getInstance().showDenyDialog("Rules file not found");
            }
        }
    }

    /**
     * Put back to the board the collected tiles
     * @param actionEvent
     */
    public void resetCollectedTiles(ActionEvent actionEvent) {
        setResetEnabled(false);
        collectedTiles.clear();
        boardGrid.getChildren().forEach(tile->tile.setVisible(true));
        collectedTilesBox.getChildren().clear();
    }

    public void glowColumn1(MouseEvent mouseEvent) {
        if(collectedTiles.size()>0) {
            Platform.runLater(() -> {
                col1Box.setBackground(new Background(new BackgroundFill(Color.web("#7cb8d6"), CornerRadii.EMPTY, Insets.EMPTY)));
                col1Box.setOpacity(0.5);
            });
        }
    }

    public void deglowColumn1(MouseEvent mouseEvent) {
        Platform.runLater(() -> {
            col1Box.setBackground(null);
            col1Box.setOpacity(0);
        });
    }

    public void glowColumn2(MouseEvent mouseEvent) {
        if(collectedTiles.size()>0) {
            Platform.runLater(() -> {
                col2Box.setBackground(new Background(new BackgroundFill(Color.web("#7cb8d6"), CornerRadii.EMPTY, Insets.EMPTY)));
                col2Box.setOpacity(0.5);
            });
        }
    }

    public void deglowColumn2(MouseEvent mouseEvent) {
        Platform.runLater(()->{
            col2Box.setBackground(null);
            col2Box.setOpacity(0);
        });
    }
    public void glowColumn3(MouseEvent mouseEvent) {
        if(collectedTiles.size()>0) {
            Platform.runLater(() -> {
                col3Box.setBackground(new Background(new BackgroundFill(Color.web("#7cb8d6"), CornerRadii.EMPTY, Insets.EMPTY)));
                col3Box.setOpacity(0.5);
            });
        }
    }

    public void deglowColumn3(MouseEvent mouseEvent) {
        Platform.runLater(()->{
            col3Box.setBackground(null);
            col3Box.setOpacity(0);
        });

    }
    public void glowColumn4(MouseEvent mouseEvent) {
        if(collectedTiles.size()>0) {
            Platform.runLater(() -> {
                col4Box.setBackground(new Background(new BackgroundFill(Color.web("#7cb8d6"), CornerRadii.EMPTY, Insets.EMPTY)));
                col4Box.setOpacity(0.5);
            });
        }
    }

    public void deglowColumn4(MouseEvent mouseEvent) {
        Platform.runLater(()->{
            col4Box.setBackground(null);
            col4Box.setOpacity(0);
        });
    }
    public void glowColumn5(MouseEvent mouseEvent) {
        if(collectedTiles.size()>0) {
            Platform.runLater(() -> {
                col5Box.setBackground(new Background(new BackgroundFill(Color.web("#7cb8d6"), CornerRadii.EMPTY, Insets.EMPTY)));
                col5Box.setOpacity(0.5);
            });
        }
    }

    public void deglowColumn5(MouseEvent mouseEvent) {
        Platform.runLater(()->{
            col5Box.setBackground(null);
            col5Box.setOpacity(0);
        });
    }

    private void collectTiles(int col) {
        if(collectedTiles.size() > freeSpots(col)){
            GUIClient.getInstance().showDenyDialog("Column "+col+" can contain\na maximum of "+freeSpots(col)+" other tiles");
        }
        else {
            column = col;
            if (collectedTiles.size() > 0) {
                StringBuilder tiles = new StringBuilder();
                List<Position> positions = new ArrayList<>();
                for (ImageView imv : collectedTiles) {
                    tiles.append(GridPane.getRowIndex(imv) + 1).append(",").append(GridPane.getColumnIndex(imv) + 1).append(" ");
                    positions.add(new Position(GridPane.getRowIndex(imv),GridPane.getColumnIndex(imv)));
                }
                if(Position.areAdjacent(positions)) {
                    Client.getInstance().addGuiAction("/collect " + tiles.toString().trim());
                }else{
                    GUIClient.getInstance().showDenyDialog("Tiles must be adjacent");
                }
            }
        }
    }

    /**
     * adds a /column action
     */
    public void insertInColumn(){
        if(column>0) {
            Client.getInstance().addGuiAction("/column " + column);
            collectedTiles.clear();
            Platform.runLater(()->{
                collectedTilesBox.getChildren().clear();
                setResetEnabled(false);
            });

        }
    }

    private int freeSpots(int col) {
        int count = 0;
        int i=0;

        for(Node n : myShelfGrid.getChildren()){
            if(i % Settings.SHELFCOLUMN == col-1){
                try{
                    if(((ImageView)n).getImage().getUrl().contains("transparent.png")){
                        count++;
                    }
                }catch (ClassCastException e){
                    //ignore
                }
            }
            i++;
        }
        return count;
    }

    public void insertTiles1(MouseEvent mouseEvent) {
        collectTiles(1);
    }

    public void insertTiles2(MouseEvent mouseEvent) {
        collectTiles(2);
    }
    public void insertTiles3(MouseEvent mouseEvent) {
        collectTiles(3);
    }
    public void insertTiles4(MouseEvent mouseEvent) {
        collectTiles(4);
    }
    public void insertTiles5(MouseEvent mouseEvent) {
        collectTiles(5);
    }

    /**
     * show chat push-notification
     */
    public void chatNotification() {
        chatNotificationImg.setVisible(true);
    }
}
