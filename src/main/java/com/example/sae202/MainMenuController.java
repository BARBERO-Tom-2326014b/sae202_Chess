package com.example.sae202;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class MainMenuController {

    @FXML
    private Button startGameButton;

    @FXML
    private Button exitButton;

    @FXML
    private void handleStartAccueil(ActionEvent event) {
        accueil  debut= new accueil();
        try {
            debut.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleExit(ActionEvent event) {
        // Code pour fermer l'application
        System.exit(0);
    }
}