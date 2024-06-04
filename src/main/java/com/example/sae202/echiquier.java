package com.example.sae202;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.List;

public class echiquier extends Application {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8;
    private boolean whiteTurn = true;
    private boolean gameActive = true;
    private StackPane selectedPiecePane = null;
    private String selectedPieceType = null;
    private int selectedPieceRow;
    private int selectedPieceCol;
    private Text turnText = new Text("White's turn");
    private Chronometre whiteChronometre = new Chronometre(300, this);
    private Chronometre blackChronometre = new Chronometre(300, this);
    private VBox vbox = new VBox();
    private VBox whiteVBox = new VBox();
    private VBox blackVBox = new VBox();

    public static void main(String[] args) {
        launch(args);
    }

    public void switchChrono() {
        if (whiteTurn) {
            whiteChronometre.start();
            blackChronometre.stop();
        } else {
            blackChronometre.start();
            whiteChronometre.stop();
        }
        updateChronoDisplay();
    }

    private void updateChronoDisplay() {
        whiteVBox.getChildren().clear();
        blackVBox.getChildren().clear();

        if (whiteTurn) {
            whiteVBox.getChildren().addAll(turnText, whiteChronometre.getTimeLabel());
            blackVBox.getChildren().addAll(new Text("Black's turn"), blackChronometre.getTimeLabel());
        } else {
            whiteVBox.getChildren().addAll(new Text("White's turn"), whiteChronometre.getTimeLabel());
            blackVBox.getChildren().addAll(turnText, blackChronometre.getTimeLabel());
        }
    }

    public void stopGame() {
        gameActive = false;
        whiteChronometre.stop();
        blackChronometre.stop();
        String winner = whiteTurn ? "Black" : "White";
        turnText.setText(winner + " wins by timeout!");
    }

    public GridPane creationEchequier(GridPane gridPane){
        // Create chess board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                StackPane stackPane = new StackPane();
                Rectangle rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
                if ((row + col) % 2 == 0) {
                    rectangle.setFill(Color.web("#ebebd0", 1.0));
                } else {
                    rectangle.setFill(Color.web("#779455", 1.0));
                }
                stackPane.getChildren().add(rectangle);
                gridPane.add(stackPane, col, row);

                int finalRow = row;
                int finalCol = col;
                stackPane.setOnMouseClicked(event -> handleMouseClick(event, finalRow, finalCol, stackPane));
            }
        }
        return gridPane;
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        creationEchequier(gridPane);

        // Add pieces
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

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                String piece = pieces[row][col];
                if (piece != null) {
                    Image image = new Image(getClass().getResourceAsStream("/img/" + piece + ".png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(TILE_SIZE);
                    imageView.setFitHeight(TILE_SIZE);
                    imageView.setUserData(piece); // Set piece type as user data
                    StackPane stackPane = (StackPane) gridPane.getChildren().get(row * BOARD_SIZE + col);
                    stackPane.getChildren().add(imageView);
                }
            }
        }

        updateChronoDisplay();

        vbox.getChildren().addAll(whiteVBox, gridPane, blackVBox);
        Scene scene = new Scene(vbox, TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE + 70);
        primaryStage.setTitle("Chess Board");
        primaryStage.setScene(scene);
        primaryStage.show();
        whiteChronometre.start();
    }

    private void handleMouseClick(MouseEvent event, int row, int col, StackPane clickedPane) {
        if (!gameActive) return;
        if (selectedPiecePane == null) {
            // Select piece
            if (!clickedPane.getChildren().isEmpty() && clickedPane.getChildren().size() > 1) {
                ImageView pieceImageView = (ImageView) clickedPane.getChildren().get(1);
                String pieceType = (String) pieceImageView.getUserData();

                if ((whiteTurn && pieceType.endsWith("B")) || (!whiteTurn && pieceType.endsWith("N"))) {
                    selectedPiecePane = clickedPane;
                    selectedPieceType = pieceType;
                    selectedPieceRow = row;
                    selectedPieceCol = col;

                    // Change the color of the rectangle to yellow
                    Rectangle rectangle = (Rectangle) clickedPane.getChildren().get(0);
                    rectangle.setFill(Color.YELLOW);
                }
            }
        } else {
            // Move piece
            if (isValidMove(row, col)) {
                // Remove the target piece if it exists (is an enemy piece)
                if (!clickedPane.getChildren().isEmpty() && clickedPane.getChildren().size() > 1) {
                    clickedPane.getChildren().remove(1);
                }

                // Move the piece
                ImageView pieceImageView = (ImageView) selectedPiecePane.getChildren().get(1);
                selectedPiecePane.getChildren().remove(pieceImageView);
                clickedPane.getChildren().add(pieceImageView);

                // Reset the color of the rectangle for the previously selected piece
                Rectangle prevRectangle = (Rectangle) selectedPiecePane.getChildren().get(0);
                prevRectangle.setFill((selectedPieceRow + selectedPieceCol) % 2 == 0 ? Color.web("#ebebd0", 1.0) : Color.web("#779455", 1.0));

                selectedPiecePane = null;
                selectedPieceType = null;
                whiteTurn = !whiteTurn;
                switchChrono();
                turnText.setText(whiteTurn ? "White's turn" : "Black's turn");
            } else {
                // Deselect the piece if move is not valid
                Rectangle prevRectangle = (Rectangle) selectedPiecePane.getChildren().get(0);
                prevRectangle.setFill((selectedPieceRow + selectedPieceCol) % 2 == 0 ? Color.web("#ebebd0", 1.0) : Color.web("#779455", 1.0));

                selectedPiecePane = null;
                selectedPieceType = null;
            }
        }
    }



    private boolean isValidMove(int row, int col) {
        if (selectedPieceType.startsWith("pion")) {
            return isValidPawnMove(row, col);
        } else if (selectedPieceType.startsWith("tour")) {
            return isValidRookMove(row, col);
        } else if (selectedPieceType.startsWith("cavalier")) {
            return isValidKnightMove(row, col);
        } else if (selectedPieceType.startsWith("fou")) {
            return isValidBishopMove(row, col);
        } else if (selectedPieceType.startsWith("reine")) {
            return isValidQueenMove(row, col);
        } else if (selectedPieceType.startsWith("roi")) {
            return isValidKingMove(row, col);
        }
        return false;
    }

    private boolean isValidPawnMove(int row, int col) {
        if (selectedPieceType.endsWith("B")) {
            return (selectedPieceRow == 6 && row == 4 && col == selectedPieceCol && isEmpty(row, col)) ||
                    (row == selectedPieceRow - 1 && col == selectedPieceCol && isEmpty(row, col)) ||
                    (row == selectedPieceRow - 1 && Math.abs(col - selectedPieceCol) == 1 && isEnemyPiece(row, col));
        } else if (selectedPieceType.endsWith("N")) {
            return (selectedPieceRow == 1 && row == 3 && col == selectedPieceCol && isEmpty(row, col)) ||
                    (row == selectedPieceRow + 1 && col == selectedPieceCol && isEmpty(row, col)) ||
                    (row == selectedPieceRow + 1 && Math.abs(col - selectedPieceCol) == 1 && isEnemyPiece(row, col));
        }
        return false;
    }

    private boolean isValidRookMove(int row, int col) {
        if (row == selectedPieceRow) {
            for (int c = Math.min(col, selectedPieceCol) + 1; c < Math.max(col, selectedPieceCol); c++) {
                if (!isEmpty(row, c)) {
                    return false;
                }
            }
            return isEmpty(row, col) || isEnemyPiece(row, col);
        } else if (col == selectedPieceCol) {
            for (int r = Math.min(row, selectedPieceRow) + 1; r < Math.max(row, selectedPieceRow); r++) {
                if (!isEmpty(r, col)) {
                    return false;
                }
            }
            return isEmpty(row, col) || isEnemyPiece(row, col);
        }
        return false;
    }

    private boolean isValidKnightMove(int row, int col) {
        int rowDiff = Math.abs(row - selectedPieceRow);
        int colDiff = Math.abs(col - selectedPieceCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2) && (isEmpty(row, col) || isEnemyPiece(row, col));
    }

    private boolean isValidBishopMove(int row, int col) {
        if (Math.abs(row - selectedPieceRow) == Math.abs(col - selectedPieceCol)) {
            int rowDirection = (row - selectedPieceRow) / Math.abs(row - selectedPieceRow);
            int colDirection = (col - selectedPieceCol) / Math.abs(col - selectedPieceCol);
            for (int i = 1; i < Math.abs(row - selectedPieceRow); i++) {
                if (!isEmpty(selectedPieceRow + i * rowDirection, selectedPieceCol + i * colDirection)) {
                    return false;
                }
            }
            return isEmpty(row, col) || isEnemyPiece(row, col);
        }
        return false;
    }

    private boolean isValidQueenMove(int row, int col) {
        return isValidRookMove(row, col) || isValidBishopMove(row, col);
    }

    private boolean isValidKingMove(int row, int col) {
        int rowDiff = Math.abs(row - selectedPieceRow);
        int colDiff = Math.abs(col - selectedPieceCol);
        return (rowDiff <= 1 && colDiff <= 1) && (isEmpty(row, col) || isEnemyPiece(row, col));
    }

    private boolean isEmpty(int row, int col) {
        GridPane gridPane = (GridPane) selectedPiecePane.getParent();
        StackPane targetPane = (StackPane) gridPane.getChildren().get(row * BOARD_SIZE + col);
        return targetPane.getChildren().size() == 1;
    }

    private boolean isEnemyPiece(int row, int col) {
        GridPane gridPane = (GridPane) selectedPiecePane.getParent();
        StackPane targetPane = (StackPane) gridPane.getChildren().get(row * BOARD_SIZE + col);
        if (targetPane.getChildren().size() > 1) {
            ImageView pieceImageView = (ImageView) targetPane.getChildren().get(1);
            String pieceType = (String) pieceImageView.getUserData();
            return (whiteTurn && pieceType.endsWith("N")) || (!whiteTurn && pieceType.endsWith("B"));
        }
        return false;
    }
}
