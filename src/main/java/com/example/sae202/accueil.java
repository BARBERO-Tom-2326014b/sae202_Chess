package com.example.sae202;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
            jouerEnLigne.setOnAction(e -> jouerAvecAmi());

            Button jouerContreOrdinateur = (Button) scene.lookup("#jouerContreOrdinateur");
            jouerContreOrdinateur.setOnAction(e -> jouerContreOrdinateur());
        } catch (IOException e) {
            System.err.println("Erreur au chargement: " + e);
        }
    }
    @FXML
    private void jouerAvecAmi() {
        // Interface pour entrer les noms des joueurs
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

//        Label label = new Label("Entrez les noms et prénoms des joueurs");
//        root.getChildren().add(label);

        Label label1 = new Label("Entrez le nom et prénom de joueur1");
        root.getChildren().add(label1);
        TextField joueur1Nom = new TextField();
        joueur1Nom.setPromptText("Nom du joueur 1");
        root.getChildren().add(joueur1Nom);

        TextField joueur1Prenom = new TextField();
        joueur1Prenom.setPromptText("Prénom du joueur 1");
        root.getChildren().add(joueur1Prenom);

        Label label2 = new Label("Entrez le nom et prénom de joueur2");
        root.getChildren().add(label2);
        TextField joueur2Nom = new TextField();
        joueur2Nom.setPromptText("Nom du joueur 2");
        root.getChildren().add(joueur2Nom);

        TextField joueur2Prenom = new TextField();
        joueur2Prenom.setPromptText("Prénom du joueur 2");
        root.getChildren().add(joueur2Prenom);

        Button validerJoueurs = new Button("Valider");
        validerJoueurs.setOnAction(e -> {
            // Enregistrer le joueur 1
            String nom = joueur1Nom.getText();
            String prenom = joueur1Prenom.getText();
            enregistrerJoueur(nom, prenom, 0, 0);
            String nom2 = joueur2Nom.getText();
            String prenom2 = joueur2Prenom.getText();
            enregistrerJoueur(nom2, prenom2, 0, 0);
            echiquier echiquier = new echiquier();
            try {
                primaryStage.close();
                echiquier.start(new Stage());  // Ouvrir l'interface de l'échiquier dans une nouvelle fenêtre
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        root.getChildren().add(validerJoueurs);

        Scene scene = new Scene(root, 780, 650);
        primaryStage.setScene(scene);
    }

    @FXML
    private void jouerContreOrdinateur() {
        // Interface pour entrer le nom du joueur humain
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        Label label = new Label("Entrez votre nom et prénom");
        root.getChildren().add(label);

        TextField joueurNom = new TextField();
        joueurNom.setPromptText("Nom");
        root.getChildren().add(joueurNom);

        TextField joueurPrenom = new TextField();
        joueurPrenom.setPromptText("Prénom");
        root.getChildren().add(joueurPrenom);

        Button valider = new Button("Valider");
        valider.setOnAction(e -> {
            // Enregistrer le joueur
            String nom = joueurNom.getText();
            String prenom = joueurPrenom.getText();
            enregistrerJoueur(nom, prenom, 0, 0);
            echiquier echiquier = new echiquier();
            try {
                primaryStage.close();
                echiquier.start(new Stage());  // Ouvrir l'interface de l'échiquier dans une nouvelle fenêtre
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        root.getChildren().add(valider);

        Scene scene = new Scene(root, 780, 650);
        primaryStage.setScene(scene);
    }

    private void enregistrerJoueur(String nom, String prenom, int partiesJouees, int partiesGagnees) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("joueur.txt", true))) {
            writer.println(nom + ";" + prenom + ";" + partiesJouees + ";" + partiesGagnees);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

