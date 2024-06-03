package com.example.sae202;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ehiquier2 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        //Constantes
        final int wHeight = 800;
        final int wWitdth = 800;
        final int taillePieces = 50;

        //Vars
        Group root = new Group();
        Scene scene = new Scene(root,wHeight,wWitdth);
        primaryStage.setScene(scene);
        int x = 0;
        int y = 0;
        int cmp = 1;
        int nbPiecesHauteur = wHeight/taillePieces;

        //Tableu qui définit la "map"
        int map[][] = new int[nbPiecesHauteur][nbPiecesHauteur];

        //Remplissage tableau avec des 0 et 1
        for(int i = 0; i < nbPiecesHauteur; i++){
            for(int j = 0; j < nbPiecesHauteur; j++){

                if(cmp % 2 == 0)
                    map[i][j] = 1;
                else
                    map[i][j] = 0;
                cmp++;
            }
            if(i % 2 == 0)
                cmp = 0;
            else
                cmp = 1;
        }


        //Parcours chaque ligne du tableau
        for(int i = 0; i < nbPiecesHauteur; i++){

            //Parcours chaque colonne du tableau
            for(int j = 0; j < nbPiecesHauteur; j++){

                //Dessine les carrés noirs
                if( map[i][j] == 0){
                    Rectangle rect = new Rectangle();
                    rect.setWidth(taillePieces);
                    rect.setHeight(taillePieces);
                    rect.setX(x);
                    rect.setY(y);
                    rect.setFill(Color.BLACK);
                    rect.setSmooth(true);
                    rect.setCursor(Cursor.WAIT);
                    root.getChildren().add(rect);
                }
                //Carrées blancs
                else if(map[i][j] == 1){
                    Rectangle rect = new Rectangle();
                    rect.setWidth(taillePieces);
                    rect.setHeight(taillePieces);
                    rect.setX(x);
                    rect.setY(y);
                    rect.setFill(Color.WHITE);
                    rect.setSmooth(true);
                    root.getChildren().add(rect);
                }

                //Change l'emplacements des futurs pièces
                x += taillePieces;
            }
            y += taillePieces;
            x = 0;
        }

        //Prop. de la fenêtre
        primaryStage.setTitle("Echéquier");
        primaryStage.setResizable(false);

        //Affichage
        primaryStage.show();
    }
}

