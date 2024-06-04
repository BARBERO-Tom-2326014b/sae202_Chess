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

    //@FXML
//    private void handleStartGame(ActionEvent event) {
//        // Crée une instance de Echequier
//        echiquier echequier = new echiquier();
//
//        // Utilise la méthode createScene pour obtenir la nouvelle scène
//
//        // Obtenir la scène actuelle et définir la nouvelle scène
//        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
//        Scene scene = echequier.start(stage);
//        stage.setTitle("Sae Chess");
//        stage.setScene(scene);
//        stage.show();
//    }
    @FXML
    private void handleExit(ActionEvent event) {
        // Code pour fermer l'application
        System.exit(0);
    }
}
