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
    private void handleStartGame(ActionEvent event) {
        echiquier echiquier = new echiquier();
        try {
            echiquier.start(new Stage());  // Ouvrir l'interface de l'échiquier dans une nouvelle fenêtre
            //   primaryStage.close();  // Fermer la fenêtre d'accueil
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