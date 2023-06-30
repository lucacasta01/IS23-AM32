package it.polimi.myShelfie.view.GUIcontroller;

import it.polimi.myShelfie.application.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class ConfigPanelController {

    @FXML
    TextField ipTxt;
    @FXML
    TextField tcpPortTxt;
    @FXML
    TextField rmiPortTxt;
    @FXML
    Label errorLbl;

    public void initialize(){
        errorLbl.setVisible(false);
        ipTxt.setText(Client.getInstance().getServerIP());
        tcpPortTxt.setText(Integer.toString(Client.getInstance().getTCPPort()));
        rmiPortTxt.setText(Integer.toString(Client.getInstance().getRMIPort()));
    }

    /**
     * Set ip and ports, obviously checking if their format is right.
     * @param actionEvent
     */
    public void doSet(ActionEvent actionEvent) {
        StringBuilder error = new StringBuilder();
        if(!checkIp(ipTxt.getText())){
            error.append("Invalid IP Address\n");
            ipTxt.setText(Client.getInstance().getServerIP());
        }else{
            Client.getInstance().setServerIP(ipTxt.getText());
        }

        if(!checkTcpPort(tcpPortTxt.getText())){
            error.append("Invalid TCP port\n");
            tcpPortTxt.setText(Integer.toString(Client.getInstance().getTCPPort()));
        }else{
            Client.getInstance().setTCPPort(Integer.parseInt(tcpPortTxt.getText()));
        }

        if(!checkRmiPort(rmiPortTxt.getText())){
            error.append("Invalid RMI port\n");
            rmiPortTxt.setText(Integer.toString(Client.getInstance().getRMIPort()));
        }else{
            Client.getInstance().setRMIPort(Integer.parseInt(rmiPortTxt.getText()));
        }

        if(!error.toString().equals("")){
            errorLbl.setText(error.toString());
            errorLbl.setVisible(true);
        }
        else{
            errorLbl.setVisible(false);
        }
    }

    private boolean checkIp(String ip){
        Pattern IPV4 = Pattern.compile("^localhost|((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
        return IPV4.matcher(ip).matches();
    }

    private boolean checkTcpPort(String tcpPort){
        Pattern port = Pattern.compile("^\\d{1,5}$");
        return port.matcher(tcpPort).matches() && !tcpPort.equals(rmiPortTxt.getText());
    }

    private boolean checkRmiPort(String rmiPort){
        Pattern port = Pattern.compile("^\\d{1,5}$");
        return port.matcher(rmiPort).matches() && !rmiPort.equals(tcpPortTxt.getText());
    }
}
