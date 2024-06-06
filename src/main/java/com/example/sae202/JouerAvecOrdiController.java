package com.example.sae202;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class JouerAvecOrdiController {


    @FXML
    private TextField joueurNom;


    @FXML
    private TextField joueurPrenom;


    @FXML
    public void valider(ActionEvent event) {
        String nom = joueurNom.getText();
        String prenom = joueurPrenom.getText();

        // Vérifier si le nom du joueur est valide (par exemple, s'il n'est pas vide)
        if (!nom.isEmpty() && !prenom.isEmpty()) {
            // Le nom du joueur est valide, démarrer le jeu d'échecs
            echiquier echecs = new echiquier();
            Stage stage = new Stage();
            try {
                echecs.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Afficher un message d'erreur dans une fenêtre pop-up
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer votre nom et prénom.");
            alert.showAndWait();
        }
    }

}



