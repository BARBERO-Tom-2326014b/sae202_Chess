package com.example.sae202;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class accueil extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Chess.com");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sae202/acceuil.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();

            Button jouerEnLigne = (Button) scene.lookup("#jouerEnLigne");
            jouerEnLigne.setOnAction(e -> chargerFXML("JouerAvecAmi.fxml"));

            Button jouerContreOrdinateur = (Button) scene.lookup("#jouerContreOrdinateur");
            jouerContreOrdinateur.setOnAction(e -> chargerFXML("JouerAvecOrdi.fxml"));
        } catch (IOException e) {
            System.err.println("Erreur au chargement: " + e);
        }
    }

    private void chargerFXML(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Erreur au chargement: " + e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
