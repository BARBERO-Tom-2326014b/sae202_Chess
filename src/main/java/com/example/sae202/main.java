package com.example.sae202;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class main extends Application {
    @FXML
    private Button startGameButton;

    @FXML
    private Button exitButton;



    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Sae Chess");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}