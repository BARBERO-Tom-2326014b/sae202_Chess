package com.example.sae202;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class accueil extends Application{

    private Stage primaryStage;
    private boolean joueur1Valide = false;
    private boolean joueur2Valide = false;


    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Echecs");

        // Interface principale
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        Label label = new Label("Entrez le nom et prenom des joueurs");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        root.getChildren().add(label);

        Button jouerAvecAmi = new Button("Jouer avec un ami");
        jouerAvecAmi.setOnAction(e -> jouerAvecAmi());
        root.getChildren().add(jouerAvecAmi);

        Button jouerContreOrdinateur = new Button("Jouer contre l'ordinateur");
        jouerContreOrdinateur.setOnAction(e -> jouerContreOrdinateur());
        root.getChildren().add(jouerContreOrdinateur);

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void jouerAvecAmi() {
        // Interface pour entrer les noms des joueurs
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        Label label = new Label("Entrez les noms et prenoms des joueurs");
        root.getChildren().add(label);

        TextField joueur1Nom = new TextField();
        joueur1Nom.setPromptText("Nom du joueur 1");
        root.getChildren().add(joueur1Nom);

        TextField joueur1Prenom = new TextField();
        joueur1Prenom.setPromptText("Prénom du joueur 1");
        root.getChildren().add(joueur1Prenom);

        Button validerJoueur1 = new Button("Valider");
        validerJoueur1.setOnAction(e -> {
            if (!joueur1Valide) {
                String nom = joueur1Nom.getText();
                String prenom = joueur1Prenom.getText();
                enregistrerJoueur(nom, prenom, 0, 0);
                joueur1Valide = true;
                joueur1Nom.setDisable(true);
                joueur1Prenom.setDisable(true);
                validerJoueur1.setDisable(true);
                joueur2(root);
            }
        });
        root.getChildren().add(validerJoueur1);

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);

        // Déplace le focus vers le bouton pour laisser le texte indicatif visible
        validerJoueur1.requestFocus();
    }

    private void joueur2(VBox root) {
        if (!joueur2Valide) {
            TextField joueur2Nom = new TextField();
            joueur2Nom.setPromptText("Nom du joueur 2");
            root.getChildren().add(joueur2Nom);

            TextField joueur2Prenom = new TextField();
            joueur2Prenom.setPromptText("Prénom du joueur 2");
            root.getChildren().add(joueur2Prenom);

            Button validerJoueur2 = new Button("Valider");
            validerJoueur2.setOnAction(e -> {
                if (!joueur2Valide) {
                    String nom = joueur2Nom.getText();
                    String prenom = joueur2Prenom.getText();
                    enregistrerJoueur(nom, prenom, 0, 0);
                    joueur2Valide = true;
                    joueur2Nom.setDisable(true);
                    joueur2Prenom.setDisable(true);
                    validerJoueur2.setDisable(true);
                    // Démarrer la partie d'échecs ici
                }
            });
            root.getChildren().add(validerJoueur2);

            // Déplace le focus vers le bouton pour laisser le texte indicatif visible
            validerJoueur2.requestFocus();
        }
    }

    private void jouerContreOrdinateur() {
        // Interface pour entrer le nom du joueur humain
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        Label label = new Label("Entrez votre nom");
        root.getChildren().add(label);

        TextField joueurNom = new TextField();
        joueurNom.setPromptText("Nom");
        root.getChildren().add(joueurNom);

        TextField joueurPrenom = new TextField();
        joueurPrenom.setPromptText("Prénom");
        root.getChildren().add(joueurPrenom);

        Button valider = new Button("Valider");
        valider.setOnAction(e -> {
            String nom = joueurNom.getText();
            String prenom = joueurPrenom.getText();
            enregistrerJoueur(nom, prenom, 0, 0);
            // Démarrer la partie d'échecs contre l'ordinateur ici
        });
        root.getChildren().add(valider);

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);

        // Déplace le focus vers le bouton pour laisser le texte indicatif visible
        valider.requestFocus();
    }

    private void enregistrerJoueur(String nom, String prenom, int partiesJouees, int partiesGagnees) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("joueur.txt", true))) {
            writer.println(nom + ";" + prenom + ";" + partiesJouees + ";" + partiesGagnees);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


