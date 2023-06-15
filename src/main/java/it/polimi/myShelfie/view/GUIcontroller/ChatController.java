package it.polimi.myShelfie.view.GUIcontroller;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.utilities.pojo.ChatMessage;
import it.polimi.myShelfie.view.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
    @FXML
    ComboBox<String> messageToCombo;
    @FXML
    TextArea chatTxt;
    @FXML
    TextField messageTxt;

    public void initialize(){
        Client.getInstance().setChatController(this);
        View view = Client.getInstance().getView();
        messageToCombo.getItems().add("Broadcast");
        if(view!=null) {
            for (String p : view.getPlayers()) {
                if (!p.equals(Client.getInstance().getNickname())) {
                    messageToCombo.getItems().add(p);
                }
            }
            messageToCombo.getSelectionModel().selectFirst();
        }

    }
    public void sendMessage(ActionEvent actionEvent) {
        if(messageTxt.getText().length()>0){
            if(messageToCombo.getValue().equals("Broadcast")) {
                Client.getInstance().addGuiAction("/chat " + messageTxt.getText());
            }
            else{
                Client.getInstance().addGuiAction("/pvt-"+ messageToCombo.getValue() + " " + messageTxt.getText());
            }
            messageTxt.setText("");
        }
    }

    public void addMessage(ChatMessage chatMessage) {
        chatTxt.appendText(chatMessage.getSender()+": "+chatMessage.getMessage()+"\n");
    }
}
