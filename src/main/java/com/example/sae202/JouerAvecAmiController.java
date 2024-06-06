package com.example.sae202;




import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class JouerAvecAmiController {




    @FXML
    private TextField joueur1Nom;




    @FXML
    private TextField joueur1Prenom;




    @FXML
    private TextField joueur2Nom;




    @FXML
    private TextField joueur2Prenom;




    @FXML
    public void valider(ActionEvent event) {
        String nom1 = joueur1Nom.getText();
        String prenom1 = joueur1Prenom.getText();
        String nom2 = joueur2Nom.getText();
        String prenom2 = joueur2Prenom.getText();

        // Vérifier si les noms des joueurs sont valides (par exemple, s'ils ne sont pas vides)
        if (!nom1.isEmpty() && !prenom1.isEmpty() && !nom2.isEmpty() && !prenom2.isEmpty()) {
            // Les noms des joueurs sont valides, démarrer le jeu d'échecs
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
            alert.setContentText("Veuillez entrer les noms des deux joueurs.");
            alert.showAndWait();
        }
    }

}




