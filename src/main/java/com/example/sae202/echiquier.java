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

public class echiquier extends Application {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8;
    private boolean whiteTurn = true;
    private StackPane selectedPiecePane = null;
    private String selectedPieceType = null;
    private int selectedPieceRow;
    private int selectedPieceCol;
    private Text turnText = new Text("White's turn");

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

        VBox vbox = new VBox(turnText, gridPane);
        Scene scene = new Scene(vbox, TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE + 20);
        primaryStage.setTitle("Chess Board");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleMouseClick(MouseEvent event, int row, int col, StackPane clickedPane) {
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
                }
            }
        } else {
            // Move piece
            if (isValidMove(row, col)) {
                ImageView pieceImageView = (ImageView) selectedPiecePane.getChildren().get(1);
                selectedPiecePane.getChildren().remove(pieceImageView);
                clickedPane.getChildren().add(pieceImageView);
                selectedPiecePane = null;
                selectedPieceType = null;
                whiteTurn = !whiteTurn;
                turnText.setText(whiteTurn ? "White's turn" : "Black's turn");
            } else {
                // Deselect the piece if move is not valid
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
