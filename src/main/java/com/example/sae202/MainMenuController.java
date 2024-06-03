package com.example.sae202;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuController {

    @FXML
    private Button startGameButton;

    @FXML
    private Button exitButton;

    @FXML
    void handleStartGame(ActionEvent event) {
        // Code to start the game
    }

    @FXML
    void handleExit(ActionEvent event) {
        // Code to exit the application
        System.exit(0);
    }
}
