package com.example.sae202;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public abstract class Pieces extends echiquier {
    protected String type;
    protected String color;

    public Pieces(String type, String color) {
        this.type = type;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    static void initializePieces() {
        String[][] pieces = {
                {"tourN", "cavalierN", "fouN", "reineN", "roiN", "fouN", "cavalierN", "tourN"},
                {"pionN", "pionN", "pionN", "pionN", "pionN", "pionN", "pionN", "pionN"},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {"pionB", "pionB", "pionB", "pionB", "pionB", "pionB", "pionB", "pionB"},
                {"tourB", "cavalierB", "fouB", "reineB", "roiB", "fouB", "cavalierB", "tourB"}
        };

        for (int row = 0; row < taillePlateau; row++) {
            for (int col = 0; col < taillePlateau; col++) {
                String piece = pieces[row][col];
                if (piece != null) {
                    Image image = new Image(Pieces.class.getResourceAsStream("/img/" + piece + ".png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(echiquier.tailleCase);
                    imageView.setFitHeight(tailleCase);
                    imageView.setUserData(piece); // Set piece type as user data
                    StackPane stackPane = (StackPane) echiquier.gridPane.getChildren().get(row * taillePlateau + col);
                    stackPane.getChildren().add(imageView);
                }
            }
        }
    }
}