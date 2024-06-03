package com.example.sae202;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class echiquier extends Application {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();

        // Create chess board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                StackPane stackPane = new StackPane();
                Rectangle rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
                if ((row + col) % 2 == 0) {
                    rectangle.setFill(Color.web("#ebebd0",1.0));
                } else {
                    rectangle.setFill(Color.web("#779455",1.0));
                }
                stackPane.getChildren().add(rectangle);
                gridPane.add(stackPane, col, row);
            }
        }

        // Add pieces
        String[][] pieces = {
                {"tourB", "cavalierB", "fouB", "reineB", "roiB", "fouB", "cavalierB", "tourB"},
                {"pionB", "pionB", "pionB", "pionB", "pionB", "pionB", "pionB", "pionB"},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {"pionN", "pionN", "pionN", "pionN", "pionN", "pionN", "pionN", "pionN"},
                {"tourN", "cavalierN", "fouN", "reineN", "roiN", "fouN", "cavalierN", "tourN"}
        };

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                String piece = pieces[row][col];
                if (piece != null) {
                    Image image = new Image(getClass().getResourceAsStream("/img"+"/" + piece + ".png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(TILE_SIZE);
                    imageView.setFitHeight(TILE_SIZE);
                    StackPane stackPane = (StackPane) gridPane.getChildren().get(row * BOARD_SIZE + col);
                    stackPane.getChildren().add(imageView);
                }
            }
        }

        Scene scene = new Scene(gridPane, TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE);
        primaryStage.setTitle("Chess Board");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
