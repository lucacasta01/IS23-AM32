package it.polimi.myShelfie.view.GUIcontroller.banners;

import it.polimi.myShelfie.utilities.JsonParser;
import it.polimi.myShelfie.utilities.pojo.GUIRank;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class RankController {

    @FXML
    GridPane rankGrid;
    @FXML
    Label pos1,pos2,pos3,pos4,nick1,nick2,nick3,nick4,score1,score2,score3,score4;

    Map<Integer,Label[]> rankRow;

    public void initialize(){
        GridPane.setHalignment(rankGrid, HPos.CENTER);
    }

    /**
     * Gets the rank from JSON string and parse it into a charming graphic final ranking
     * @param rank
     */
    public void setRank(String rank) {
        GUIRank guiRank = JsonParser.getGUIRank(rank);
        rankRow = generateRankMap();
        Platform.runLater(()->{
            for(int i=0;i<guiRank.getNicknames().size();i++){
                if(guiRank.getPos().get(i).equals("1")){
                    for(int j=0;j<3;j++) {
                        rankRow.get(i)[j].setTextFill(Color.GREEN);
                        rankRow.get(i)[j].setFont(Font.font("SYSTEM", FontWeight.BOLD, FontPosture.ITALIC,16));
                    }
                }
                for(int j=0;j<3;j++) {
                    rankRow.get(i)[j].setVisible(true);
                }
                rankRow.get(i)[0].setText(guiRank.getPos().get(i));
                rankRow.get(i)[1].setText(guiRank.getNicknames().get(i));
                rankRow.get(i)[2].setText(guiRank.getScores().get(i));
            }
        });
    }

    private Map<Integer,Label[]> generateRankMap(){
        Map<Integer,Label[]> map = new HashMap<>();
        map.put(0,new Label[]{pos1,nick1,score1});
        map.put(1,new Label[]{pos2,nick2,score2});
        map.put(2,new Label[]{pos3,nick3,score3});
        map.put(3,new Label[]{pos4,nick4,score4});
        return map;
    }

    public void closeStage(ActionEvent actionEvent) {
        Node source = (Node)actionEvent.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.getOnCloseRequest().handle(null);
        stage.close();
    }
}
