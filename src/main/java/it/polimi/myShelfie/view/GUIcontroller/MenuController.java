package it.polimi.myShelfie.view.GUIcontroller;

import it.polimi.myShelfie.application.Client;
import it.polimi.myShelfie.application.GUIClient;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class MenuController {
    public void initialize() {
        GUIClient.getInstance().getStage().setWidth(GUIClient.getInstance().getStage().getWidth() + 1);
        GUIClient.getInstance().getStage().centerOnScreen();
        GUIClient.getInstance().getStage().setOnCloseRequest(e -> {
            GUIClient.getInstance().getStage().close();
            Client.getInstance().addGuiAction("/quit");
        });
    }

    public void doShutdown(ActionEvent actionEvent) {
        Client client = Client.getInstance();
        if (client == null) {
            System.exit(15);
        } else {
            client.addGuiAction("/quit");
        }
    }

    /**
     * equivalent to select option 1 in the tui main menu
     * @param actionEvent
     */
    public void newGameAction(ActionEvent actionEvent) {
        GUIClient.getInstance().switchToPlayerNumber();
        Client.getInstance().addGuiAction("1");
    }


    /**
     * equivalent to select option 3 in the tui main menu
     * @param actionEvent
     */
    public void randomGameAction(ActionEvent actionEvent) {
        Client.getInstance().addGuiAction("3");
    }

    /**
     * equivalent to select option 2 in the tui main menu
     * @param actionEvent
     */
    public void restoreGameAction(ActionEvent actionEvent) {
        Client.getInstance().addGuiAction("2");
    }

    /**
     * equivalent to select option 4 in the tui main menu
     * @param actionEvent
     */
    public void searchRestoredAction(ActionEvent actionEvent) {
        Client.getInstance().addGuiAction("4");
    }
}


