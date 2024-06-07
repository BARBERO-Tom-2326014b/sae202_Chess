package com.example.sae202;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class JouerAvecOrdiController {

    public static int tempsChoisi;
    @FXML
    private ComboBox<String> timeComboBox;
    @FXML
    private TextField joueurNom;


    @FXML
    private TextField joueurPrenom;


    @FXML
    public void valider(ActionEvent event) {
        String nom = joueurNom.getText();
        String prenom = joueurPrenom.getText();
        String tempsSelectionne = timeComboBox.getSelectionModel().getSelectedItem();

        // Vérifier si les noms des joueurs sont valides (par exemple, s'ils ne sont pas vides)
        if (!nom.isEmpty() && !prenom.isEmpty() && tempsSelectionne != null) {
            // Les noms des joueurs sont valides, démarrer le jeu d'échecs avec le temps choisi
            switch (tempsSelectionne) {
                case "1 minutes":
                    tempsChoisi = 60; // 15 minutes en secondes
                    break;
                case "5 minutes":
                    tempsChoisi = 300; // 5 minutes en secondes
                    break;
                case "10 minutes":
                    tempsChoisi = 600; // 10 minutes en secondes
                    break;
                case "15 minutes":
                    tempsChoisi = 900; // 15 minutes en secondes
                    break;
                case "30 minutes":
                    tempsChoisi = 1800; // 15 minutes en secondes
                    break;
                default:
                    tempsChoisi = 300; // Par défaut, 5 minutes en secondes
                    break;
            }

            echiquierBot echecs = new echiquierBot(tempsChoisi);
            Stage stage = new Stage();
            try {
                echecs.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) joueurNom.getScene().getWindow();
            currentStage.close();
        } else {
            // Afficher un message d'erreur dans une fenêtre pop-up
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer les noms des deux joueurs et choisir le temps de partie.");
            alert.showAndWait();
        }
    }
}


