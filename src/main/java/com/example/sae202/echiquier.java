package com.example.sae202;

import javafx.application.Application;
import javafx.scene.Node;
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
import javafx.scene.shape.Circle;

import javax.swing.*;
import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.List;

public class echiquier extends Application {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8;
    private Pieces[][] pieces;
    private boolean whiteTurn = true;
    private boolean gameActive = true;

    Circle circle = createIndicatorCircle();
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
    private GridPane gridPane = new GridPane();

    public static void main(String[] args) {
        launch(args);
    }

    private Circle createIndicatorCircle() {
        Circle circle = new Circle(TILE_SIZE / 8);
        circle.setFill(Color.BLACK);
        circle.setOpacity(0.5);
        return circle;
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

    private void initializePieces(){
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
    }

    @Override
    public void start(Stage primaryStage) {

        creationEchequier(gridPane);
        pieces = new Pieces[8][8];
        initializePieces();

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

                    // Highlight valid moves for the selected piece
                    List<int[]> validMoves = getValidMoves(row, col, pieceType);
                    highlightValidMoves(validMoves);
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

                // Reset the color of the rectangles for the previously selected piece and highlighted moves
                resetColors();

                selectedPiecePane = null;
                selectedPieceType = null;
                whiteTurn = !whiteTurn;
                switchChrono();
                turnText.setText(whiteTurn ? "White's turn" : "Black's turn");
            } else {
                // Deselect the piece if move is not valid
                resetColors();

                selectedPiecePane = null;
                selectedPieceType = null;
            }
        }
    }

    private void highlightValidMoves(List<int[]> validMoves) {
        // Highlight the valid moves on the chessboard
        for (int[] move : validMoves) {
            int moveRow = move[0];
            int moveCol = move[1];
            // Access the corresponding StackPane in the gridPane and change its appearance
            StackPane stackPane = (StackPane) gridPane.getChildren().get(moveRow * BOARD_SIZE + moveCol);
            Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
            rectangle.setFill(Color.LIGHTGREEN);
        }
    }

    private void resetColors() {
        // Reset the color of the rectangles for the previously selected piece and highlighted moves
        for (Node node : gridPane.getChildren()) {
            StackPane stackPane = (StackPane) node;
            Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
            if (rectangle.getFill() == Color.YELLOW || rectangle.getFill() == Color.LIGHTGREEN) {
                rectangle.setFill((GridPane.getRowIndex(stackPane) + GridPane.getColumnIndex(stackPane)) % 2 == 0 ? Color.web("#ebebd0", 1.0) : Color.web("#779455", 1.0));
            }
        }
    }

    private List<int[]> getValidMoves(int row, int col, String pieceType) {
        // Call the appropriate method based on the piece type
        if (pieceType.startsWith("pion")) {
            return getValidMovesForPawn(row, col, pieceType);
        } else if (pieceType.startsWith("tour")) {
            return getValidMovesForRook(row, col, pieceType);
        } else if (pieceType.startsWith("cavalier")) {
            return getValidMovesForKnight(row, col, pieceType);
        } else if (pieceType.startsWith("fou")) {
            return getValidMovesForBishop(row, col, pieceType);
        } else if (pieceType.startsWith("reine")) {
            return getValidMovesForQueen(row, col, pieceType);
        } else if (pieceType.startsWith("roi")) {
            return getValidMovesForKing(row, col, pieceType);
        }
        return new ArrayList<>();
    }


    private List<int[]> getValidMovesForRook(int row, int col, String pieceType) {
        List<int[]> validMoves = new ArrayList<>();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] dir : directions) {
            int newRow = row;
            int newCol = col;
            while (true) {
                newRow += dir[0];
                newCol += dir[1];
                if (!isValidPosition(newRow, newCol) || (!isEmpty(newRow, newCol) && !isEnemyPiece(newRow, newCol))) {
                    break;
                }
                validMoves.add(new int[]{newRow, newCol});
                if (!isEmpty(newRow, newCol)) {
                    break;
                }
            }
        }
        return validMoves;
    }

    private List<int[]> getValidMovesForKnight(int row, int col, String pieceType) {
        List<int[]> validMoves = new ArrayList<>();
        int[][] moves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] move : moves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isValidPosition(newRow, newCol) && (isEmpty(newRow, newCol) || isEnemyPiece(newRow, newCol))) {
                validMoves.add(new int[]{newRow, newCol});
            }
        }
        return validMoves;
    }

    private List<int[]> getValidMovesForBishop(int row, int col, String pieceType) {
        List<int[]> validMoves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] dir : directions) {
            int newRow = row;
            int newCol = col;
            while (true) {
                newRow += dir[0];
                newCol += dir[1];
                if (!isValidPosition(newRow, newCol) || (!isEmpty(newRow, newCol) && !isEnemyPiece(newRow, newCol))) {
                    break;
                }
                validMoves.add(new int[]{newRow, newCol});
                if (!isEmpty(newRow, newCol)) {
                    break;
                }
            }
        }
        return validMoves;
    }

    private List<int[]> getValidMovesForQueen(int row, int col, String pieceType) {
        List<int[]> validMoves = new ArrayList<>();
        validMoves.addAll(getValidMovesForRook(row, col, pieceType));
        validMoves.addAll(getValidMovesForBishop(row, col, pieceType));
        return validMoves;
    }

    private List<int[]> getValidMovesForKing(int row, int col, String pieceType) {
        List<int[]> validMoves = new ArrayList<>();
        int[][] moves = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        for (int[] move : moves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isValidPosition(newRow, newCol) && (isEmpty(newRow, newCol) || isEnemyPiece(newRow, newCol))) {
                validMoves.add(new int[]{newRow, newCol});
            }
        }
        return validMoves;
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

    private List<int[]> getValidMovesForPawn(int row, int col, String pieceType) {
        List<int[]> validMoves = new ArrayList<>();

        // Calculer les mouvements valides pour le pion
        int direction = pieceType.endsWith("B") ? -1 : 1; // La direction dépend de la couleur de la pièce
        int startRow = pieceType.endsWith("B") ? 6 : 1; // La rangée de départ dépend de la couleur de la pièce
        int[] singleStep = {direction, 0}; // Un pas en avant
        int[] doubleStep = {2 * direction, 0}; // Deux pas en avant au premier mouvement
        int[][] attackMoves = {{direction, 1}, {direction, -1}}; // Mouvements d'attaque

        // Vérifier le mouvement simple en avant
        if (isEmpty(row + direction, col)) {
            validMoves.add(new int[]{row + direction, col});
            // Vérifier le mouvement double en avant au premier mouvement
            if (row == startRow && isEmpty(row + 2 * direction, col)) {
                validMoves.add(new int[]{row + 2 * direction, col});
            }
        }

        // Vérifier les mouvements d'attaque diagonaux
        for (int[] move : attackMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isValidPosition(newRow, newCol) && isEnemyPiece(newRow, newCol)) {
                validMoves.add(new int[]{newRow, newCol});
            }
        }

        return validMoves;
    }



    // Fonction pour vérifier si la position est valide sur l'échiquier
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
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
        return ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) && ((isEmpty(row, col) || isEnemyPiece(row, col)));
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