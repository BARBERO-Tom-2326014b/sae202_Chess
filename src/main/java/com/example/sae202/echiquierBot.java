package com.example.sae202;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class echiquierBot extends echiquier {
    static final int tailleCase = 80;
    static final int taillePlateau = 8;
    private Pieces[][] pieces;
    private boolean tourBlanc = true;
    private boolean enJeux = true;

    private StackPane selectedPiecePane = null;
    private String typePieceSelec = null;
    private int lignePieceSelec;
    private List<ImageView> PieceCapNoir = new ArrayList<>();
    private List<ImageView> PieceCapBlanc = new ArrayList<>();
    private HBox capturedPiecesBlancHBox = new HBox();
    private HBox capturedPiecesNoirHBox = new HBox();
    private int colPieceSelec;
    private Text tourJoueurText = new Text("White's turn");
    private Chronometre chronoBlanc = new Chronometre(300, this);
    private Chronometre chronoNoir = new Chronometre(300, this);
    private VBox vbox = new VBox();
    private VBox whiteVBox = new VBox();
    private VBox blackVBox = new VBox();
    static GridPane gridPane = new GridPane();
    private Stage primaryStage;
    private int initialTime;

    public echiquierBot() {
        this(300); // Default to 5 minutes if no time is specified
    }

    public echiquierBot(int initialTime) {
        this.initialTime = initialTime;
        chronoBlanc = new Chronometre(initialTime, this);
        chronoNoir = new Chronometre(initialTime, this);
    }
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
            whiteVBox.getChildren().addAll(new Text("Tour Blanc"), chronoBlanc.getTimeLabel());
            blackVBox.getChildren().addAll(new Text(""), chronoNoir.getTimeLabel());
        } else {
            whiteVBox.getChildren().addAll(new Text(""), chronoBlanc.getTimeLabel());
            blackVBox.getChildren().addAll(new Text("Tour Noir(Bot) "), chronoNoir.getTimeLabel());
        }
    }

    public void stopGame() {
        enJeux = false;
        chronoBlanc.stop();
        chronoNoir.stop();
        String winner = tourBlanc ? "Noir" : "Blanc";
        tourJoueurText.setText(winner + " wins!");

        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Game Over ! ");
            alert.setHeaderText(null);
            alert.setContentText("Les " + winner + " ont gagnés");
            primaryStage.close();
            alert.showAndWait();
            Platform.exit();
        });
    }

    public GridPane creationEchequier(GridPane gridPane) {
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
                    imageView.setUserData(piece);
                    StackPane stackPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
                    stackPane.getChildren().add(imageView);
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        creationEchequier(gridPane);
        Pieces.initializePiecesBot();
        updateChronoDisplay();
        vbox.getChildren().addAll(blackVBox, capturedPiecesNoirHBox, gridPane, whiteVBox, capturedPiecesBlancHBox);
        Scene scene = new Scene(vbox, tailleCase * taillePlateau, tailleCase * taillePlateau + 70);
        primaryStage.setTitle("Chess Board");
        primaryStage.setScene(scene);
        primaryStage.show();
        chronoBlanc.start();
        playComputerTurn();
    }

    private void clickSouris(MouseEvent event, int row, int col, StackPane clickedPane) {
        if (!enJeux) return;

        if (tourBlanc) {
            if (selectedPiecePane == null) {
                if (!clickedPane.getChildren().isEmpty() && clickedPane.getChildren().size() > 1) {
                    ImageView pieceImageView = (ImageView) clickedPane.getChildren().get(1);
                    String pieceType = (String) pieceImageView.getUserData();
                    if (pieceType.endsWith("B")) {
                        selectedPiecePane = clickedPane;
                        typePieceSelec = pieceType;
                        lignePieceSelec = row;
                        colPieceSelec = col;
                        Rectangle rectangle = (Rectangle) clickedPane.getChildren().get(0);
                        rectangle.setFill(Color.YELLOW);
                        List<int[]> validMoves = getValidMoves(row, col, pieceType);
                        highlightValidMoves(validMoves);
                    }
                }
            } else {
                List<int[]> validMoves = getValidMoves(lignePieceSelec, colPieceSelec, typePieceSelec);
                boolean isValidMove = false;
                for (int[] move : validMoves) {
                    if (move[0] == row && move[1] == col) {
                        isValidMove = true;
                        break;
                    }
                }
                if (isValidMove) {
                    if (!clickedPane.getChildren().isEmpty() && clickedPane.getChildren().size() > 1) {
                        ImageView targetPieceImageView = (ImageView) clickedPane.getChildren().get(1);
                        String targetPieceType = (String) targetPieceImageView.getUserData();
                        if (targetPieceType.endsWith("roiB") || targetPieceType.endsWith("roiN")) {
                            stopGame();
                            return;
                        }
                        clickedPane.getChildren().remove(targetPieceImageView);
                        capturePiece(targetPieceImageView);
                    }
                    ImageView pieceImageView = (ImageView) selectedPiecePane.getChildren().get(1);
                    selectedPiecePane.getChildren().remove(pieceImageView);
                    clickedPane.getChildren().add(pieceImageView);
                    resetColors();
                    boolean currentPlayerIsWhite = !tourBlanc;
                    if (isKingInCheck(currentPlayerIsWhite)) {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Check");
                        alert.setHeaderText(null);
                        alert.setContentText((currentPlayerIsWhite ? "White" : "Black") + " is in check!");
                        alert.showAndWait();
                    }

                    selectedPiecePane = null;
                    typePieceSelec = null;
                    tourBlanc = !tourBlanc;
                    switchChrono();
                    tourJoueurText.setText(tourBlanc ? "Tour noir" : "Tour Blanc");
                } else {
                    resetColors();
                    selectedPiecePane = null;
                    typePieceSelec = null;
                }
            }
        } else {
            // Gérer le mouvement automatique pour les pièces noires
            if (selectedPiecePane == null && !tourBlanc) {
                // Sélectionner une pièce noire au hasard
                int randomRow = new Random().nextInt(taillePlateau);
                int randomCol = new Random().nextInt(taillePlateau);
                StackPane randomPane = (StackPane) gridPane.getChildren().get(randomRow * taillePlateau + randomCol);
                if (!randomPane.getChildren().isEmpty() && randomPane.getChildren().size() > 1) {
                    ImageView pieceImageView = (ImageView) randomPane.getChildren().get(1);
                    String pieceType = (String) pieceImageView.getUserData();
                    if (pieceType.endsWith("N")) {
                        selectedPiecePane = randomPane;
                        typePieceSelec = pieceType;
                        lignePieceSelec = randomRow;
                        colPieceSelec = randomCol;
                        Rectangle rectangle = (Rectangle) randomPane.getChildren().get(0);
                        rectangle.setFill(Color.YELLOW);
                        List<int[]> validMoves = getValidMoves(randomRow, randomCol, pieceType);
                        if (!validMoves.isEmpty()) {
                            // Choisir un mouvement valide au hasard
                            int[] randomMove = validMoves.get(new Random().nextInt(validMoves.size()));
                            int moveRow = randomMove[0];
                            int moveCol = randomMove[1];
                            StackPane targetPane = (StackPane) gridPane.getChildren().get(moveRow * taillePlateau + moveCol);
                            ImageView pieceToMove = (ImageView) selectedPiecePane.getChildren().get(1);
                            selectedPiecePane.getChildren().remove(pieceToMove);
                            targetPane.getChildren().add(pieceToMove);
                            resetColors();
                            tourBlanc = !tourBlanc;
                            switchChrono();
                            tourJoueurText.setText(tourBlanc ? "Tour noir" : "Tour Blanc");
                        }
                    }
                }
            }
        }
    }


    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < taillePlateau && col >= 0 && col < taillePlateau;
    }


    public boolean isEmpty(int row, int col) {
        GridPane gridPane = (GridPane) selectedPiecePane.getParent();
        StackPane targetPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
        return targetPane.getChildren().size() == 1;
    }


    public boolean isEnemyPiece(int row, int col) {
        GridPane gridPane = (GridPane) selectedPiecePane.getParent();
        StackPane targetPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
        if (targetPane.getChildren().size() > 1) {
            ImageView pieceImageView = (ImageView) targetPane.getChildren().get(1);
            String pieceType = (String) pieceImageView.getUserData();
            return (tourBlanc && pieceType.endsWith("N")) || (!tourBlanc && pieceType.endsWith("B"));
        }
        return false;
    }

    private void playComputerTurn() {
        // Vérifiez si c'est le tour de l'ordinateur (les noirs) et que le jeu est en cours
        if (!tourBlanc && enJeux) {
            // Jouez le tour de l'ordinateur
            playComputerMove();
        }
    }


    private void resetColors() {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane) {
                StackPane stackPane = (StackPane) node;
                Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
                int row = GridPane.getRowIndex(stackPane);
                int col = GridPane.getColumnIndex(stackPane);
                if ((row + col) % 2 == 0) {
                    rectangle.setFill(Color.web("#ebebd0", 1.0));
                } else {
                    rectangle.setFill(Color.web("#779455", 1.0));
                }
            }
        }
    }

    private List<int[]> getValidMoves(int row, int col, String pieceType) {
        if (pieceType.startsWith("pion")) {
            return pion.getValidMovesForPawn(row, col, pieceType, this);
        } else if (pieceType.startsWith("tour")) {
            return tour.getValidMovesForRook(row, col, pieceType, this);
        } else if (pieceType.startsWith("cavalier")) {
            return cavalier.getValidMovesForKnight(row, col, pieceType, this);
        } else if (pieceType.startsWith("fou")) {
            return fou.getValidMovesForBishop(row, col, pieceType, this);
        } else if (pieceType.startsWith("reine")) {
            return renne.getValidMovesForQueen(row, col, pieceType, this);
        } else if (pieceType.startsWith("roi")) {
            return roi.getValidMovesForKing(row, col, pieceType, this);
        }
        return new ArrayList<>();
    }


    private void highlightValidMoves(List<int[]> validMoves) {
        for (int[] move : validMoves) {
            int moveRow = move[0];
            int moveCol = move[1];
            StackPane stackPane = (StackPane) gridPane.getChildren().get(moveRow * taillePlateau + moveCol);
            Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
            rectangle.setFill(Color.LIGHTGREEN);
        }
    }

    private void capturePiece(ImageView pieceImageView) {
        String pieceType = (String) pieceImageView.getUserData();
        if (pieceType.endsWith("B")) {
            PieceCapNoir.add(pieceImageView);
            capturedPiecesNoirHBox.getChildren().add(pieceImageView);
        } else {
            PieceCapBlanc.add(pieceImageView);
            capturedPiecesBlancHBox.getChildren().add(pieceImageView);
        }
    }

    private boolean isKingInCheck(boolean currentPlayerIsWhite) {
        String kingType = currentPlayerIsWhite ? "roiB" : "roiN";
        int kingRow = -1, kingCol = -1;

        for (int row = 0; row < taillePlateau; row++) {
            for (int col = 0; col < taillePlateau; col++) {
                StackPane stackPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
                if (!stackPane.getChildren().isEmpty() && stackPane.getChildren().size() > 1) {
                    ImageView pieceImageView = (ImageView) stackPane.getChildren().get(1);
                    String pieceType = (String) pieceImageView.getUserData();
                    if (pieceType.equals(kingType)) {
                        kingRow = row;
                        kingCol = col;
                        break;
                    }
                }
            }
        }

        for (int row = 0; row < taillePlateau; row++) {
            for (int col = 0; col < taillePlateau; col++) {
                StackPane stackPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
                if (!stackPane.getChildren().isEmpty() && stackPane.getChildren().size() > 1) {
                    ImageView pieceImageView = (ImageView) stackPane.getChildren().get(1);
                    String pieceType = (String) pieceImageView.getUserData();
                    if ((currentPlayerIsWhite && pieceType.endsWith("N")) || (!currentPlayerIsWhite && pieceType.endsWith("B"))) {
                        List<int[]> validMoves = getValidMoves(row, col, pieceType);
                        for (int[] move : validMoves) {
                            if (move[0] == kingRow && move[1] == kingCol) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private List<int[]> getAllPossibleMovesForBlack() {
        List<int[]> possibleMoves = new ArrayList<>();
        for (int row = 0; row < taillePlateau; row++) {
            for (int col = 0; col < taillePlateau; col++) {
                StackPane stackPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
                if (!stackPane.getChildren().isEmpty() && stackPane.getChildren().size() > 1) {
                    ImageView pieceImageView = (ImageView) stackPane.getChildren().get(1);
                    String pieceType = (String) pieceImageView.getUserData();
                    if (pieceType.endsWith("N")) { // Check if it's a black piece
                        List<int[]> validMoves = getValidMoves(row, col, pieceType);
                        for (int[] move : validMoves) {
                            possibleMoves.add(new int[]{row, col, move[0], move[1]}); // Add current position and move position
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    private void playComputerMove() {
        List<int[]> possibleMoves = getAllPossibleMovesForBlack();
        while(possibleMoves.isEmpty()) {
            possibleMoves = getAllPossibleMovesForBlack(); // No possible moves for black
        }

        // Select a random move
        int[] selectedMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
        while(selectedMove.length==0) {
            selectedMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
        }

        int startRow = selectedMove[0];
        int startCol = selectedMove[1];
        int endRow = selectedMove[2];
        int endCol = selectedMove[3];

        StackPane startPane = (StackPane) gridPane.getChildren().get(startRow * taillePlateau + startCol);
        StackPane endPane = (StackPane) gridPane.getChildren().get(endRow * taillePlateau + endCol);

        if (!endPane.getChildren().isEmpty() && endPane.getChildren().size() > 1) {
            ImageView targetPieceImageView = (ImageView) endPane.getChildren().get(1);
            endPane.getChildren().remove(targetPieceImageView);
            capturePiece(targetPieceImageView);
        }

        ImageView pieceImageView = (ImageView) startPane.getChildren().get(1);
        startPane.getChildren().remove(pieceImageView);
        endPane.getChildren().add(pieceImageView);

        boolean currentPlayerIsWhite = !tourBlanc;
        if (isKingInCheck(currentPlayerIsWhite)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Check");
            alert.setHeaderText(null);
            alert.setContentText((currentPlayerIsWhite ? "White" : "Black") + " is in check!");
            alert.showAndWait();
        }

        tourBlanc = true;
        switchChrono();
        tourJoueurText.setText("White's turn");
    }
}