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

import java.util.ArrayList;
import java.util.List;

public class echiquier extends Application {
    private static final int tailleCase = 80;
    private static final int taillePlateau = 8;
    private Pieces[][] pieces;
    private boolean tourBlanc = true;
    private boolean enJeux = true;

    private StackPane selectedPiecePane = null;
    private String typePieceSelec = null;
    private int lignePieceSelec;
    private int colPieceSelec;
    private Text tourJoueurText = new Text("White's turn");
    private Chronometre chronoBlanc = new Chronometre(300, this);
    private Chronometre chronoNoir = new Chronometre(300, this);
    private VBox vbox = new VBox();
    private VBox whiteVBox = new VBox();
    private VBox blackVBox = new VBox();
    private GridPane gridPane = new GridPane();

    public static void main(String[] args) {
        launch(args);
    }


    public void switchChrono() {
        if (tourBlanc) {
            chronoBlanc.start();
            chronoNoir.stop();
        } else {
            chronoNoir.start();
            chronoBlanc.stop();
        }
        updateChronoDisplay();
    }

    private void updateChronoDisplay() {
        whiteVBox.getChildren().clear();
        blackVBox.getChildren().clear();

        if (tourBlanc) {
            whiteVBox.getChildren().addAll(tourJoueurText, chronoBlanc.getTimeLabel());
            blackVBox.getChildren().addAll(new Text("Black's turn"), chronoNoir.getTimeLabel());
        } else {
            whiteVBox.getChildren().addAll(new Text("White's turn"), chronoBlanc.getTimeLabel());
            blackVBox.getChildren().addAll(tourJoueurText, chronoNoir.getTimeLabel());
        }
    }

    public void stopGame() {
        enJeux = false;
        chronoBlanc.stop();
        chronoNoir.stop();
        String winner = tourBlanc ? "Black" : "White";
        tourJoueurText.setText(winner + " wins by timeout!");
    }

    public GridPane creationEchequier(GridPane gridPane) {
        // Create chess board
        for (int row = 0; row < taillePlateau; row++) {
            for (int col = 0; col < taillePlateau; col++) {
                StackPane stackPane = new StackPane();
                Rectangle rectangle = new Rectangle(tailleCase, tailleCase);
                if ((row + col) % 2 == 0) {
                    rectangle.setFill(Color.web("#ebebd0", 1.0));
                } else {
                    rectangle.setFill(Color.web("#779455", 1.0));
                }
                stackPane.getChildren().add(rectangle);
                gridPane.add(stackPane, col, row);

                int finalRow = row;
                int finalCol = col;
                stackPane.setOnMouseClicked(event -> clickSouris(event, finalRow, finalCol, stackPane));
            }
        }
        return gridPane;
    }

    private void initializePieces() {
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
                    Image image = new Image(getClass().getResourceAsStream("/img/" + piece + ".png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(tailleCase);
                    imageView.setFitHeight(tailleCase);
                    imageView.setUserData(piece); // Set piece type as user data
                    StackPane stackPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
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

        vbox.getChildren().addAll(blackVBox, gridPane, whiteVBox);
        Scene scene = new Scene(vbox, tailleCase * taillePlateau, tailleCase * taillePlateau + 70);
        primaryStage.setTitle("Chess Board");
        primaryStage.setScene(scene);
        primaryStage.show();
        chronoBlanc.start();
    }

    private void clickSouris(MouseEvent event, int row, int col, StackPane clickedPane) {
        if (!enJeux) return;
        if (selectedPiecePane == null) {
            // Select piece
            if (!clickedPane.getChildren().isEmpty() && clickedPane.getChildren().size() > 1) {
                ImageView pieceImageView = (ImageView) clickedPane.getChildren().get(1);
                String pieceType = (String) pieceImageView.getUserData();

                if ((tourBlanc && pieceType.endsWith("B")) || (!tourBlanc && pieceType.endsWith("N"))) {
                    selectedPiecePane = clickedPane;
                    typePieceSelec = pieceType;
                    lignePieceSelec = row;
                    colPieceSelec = col;

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
            List<int[]> validMoves = getValidMoves(lignePieceSelec, colPieceSelec, typePieceSelec);
            boolean isValidMove = false;
            for (int[] move : validMoves) {
                if (move[0] == row && move[1] == col) {
                    isValidMove = true;
                    break;
                }
            }
            if (isValidMove) {
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
                typePieceSelec = null;
                tourBlanc = !tourBlanc;
                switchChrono();
                tourJoueurText.setText(tourBlanc ? "White's turn" : "Black's turn");
            } else {
                // Deselect the piece if move is not valid
                resetColors();

                selectedPiecePane = null;
                typePieceSelec = null;
            }
        }
    }

    private List<int[]> getValidMoves(int row, int col, String pieceType) {
        // Call the appropriate method based on the piece type
        if (pieceType.startsWith("pion")) {
            return pion.getValidMovesForPawn(row, col, pieceType, this);
        } else if (pieceType.startsWith("tour")) {
            return tour.getValidMovesForRook(row, col, pieceType,this);
        } else if (pieceType.startsWith("cavalier")) {
            return cavalier.getValidMovesForKnight(row, col, pieceType,this);
        } else if (pieceType.startsWith("fou")) {
            return fou.getValidMovesForBishop(row, col, pieceType,this);
        } else if (pieceType.startsWith("reine")) {
            return renne.getValidMovesForQueen(row, col, pieceType,this);
        } else if (pieceType.startsWith("roi")) {
            return roi.getValidMovesForKing(row, col, pieceType,this);
        }
        return new ArrayList<>();
    }
    private void highlightValidMoves(List<int[]> validMoves) {
        // Highlight the valid moves on the chessboard
        for (int[] move : validMoves) {
            int moveRow = move[0];
            int moveCol = move[1];
            // Access the corresponding StackPane in the gridPane and change its appearance
            StackPane stackPane = (StackPane) gridPane.getChildren().get(moveRow * taillePlateau + moveCol);
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
    boolean isValidPosition(int row, int col) {
        return row >= 0 && row < taillePlateau && col >= 0 && col < taillePlateau;
    }

    boolean isEmpty(int row, int col) {
        GridPane gridPane = (GridPane) selectedPiecePane.getParent();
        StackPane targetPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
        return targetPane.getChildren().size() == 1;
    }

    boolean isEnemyPiece(int row, int col) {
        GridPane gridPane = (GridPane) selectedPiecePane.getParent();
        StackPane targetPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
        if (targetPane.getChildren().size() > 1) {
            ImageView pieceImageView = (ImageView) targetPane.getChildren().get(1);
            String pieceType = (String) pieceImageView.getUserData();
            return (tourBlanc && pieceType.endsWith("N")) || (!tourBlanc && pieceType.endsWith("B"));
        }
        return false;
    }

}