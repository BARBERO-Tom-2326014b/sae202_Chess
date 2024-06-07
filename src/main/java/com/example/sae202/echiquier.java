package com.example.sae202;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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

public class echiquier extends Application {
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
    private Chronometre chronoBlanc; // Initialize the chronometers
    private Chronometre chronoNoir;
    private VBox vbox = new VBox();
    private VBox whiteVBox = new VBox();
    private VBox blackVBox = new VBox();
    static GridPane gridPane = new GridPane();
    private Stage primaryStage; // Add a reference to the primary stage
    private int initialTime;

    public echiquier() {
        this(300); // Default to 5 minutes if no time is specified
    }

    public echiquier(int initialTime) {
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
            blackVBox.getChildren().addAll(new Text("\n"), chronoNoir.getTimeLabel());
        } else {
            whiteVBox.getChildren().addAll(new Text("\n"), chronoBlanc.getTimeLabel());
            blackVBox.getChildren().addAll(new Text("Tour Noir"), chronoNoir.getTimeLabel());
        }
    }

    public void stopGame() {
        enJeux = false;
        chronoBlanc.stop();
        chronoNoir.stop();
        String winner = tourBlanc ? "Noir" : "Blanc";
        tourJoueurText.setText(winner + " wins!");

        // Close the primary stage and show the alert
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
                    imageView.setUserData(piece); // Set piece type as user data
                    StackPane stackPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
                    stackPane.getChildren().add(imageView);
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Initialize the primary stage reference
        creationEchequier(gridPane);
        Pieces.initializePieces();
        updateChronoDisplay();
        vbox.getChildren().addAll(blackVBox, capturedPiecesNoirHBox, gridPane, whiteVBox, capturedPiecesBlancHBox);
        Scene scene = new Scene(vbox, tailleCase * taillePlateau, tailleCase * taillePlateau + 70);
        primaryStage.setTitle("Chess Board");
        primaryStage.setScene(scene);
        primaryStage.show();
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
                boolean currentPlayerIsWhite = !tourBlanc; // The player who just moved
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


    private void resetColors() {
        for (Node node : gridPane.getChildren()) {
            StackPane stackPane = (StackPane) node;
            Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
            if (rectangle.getFill() == Color.YELLOW || rectangle.getFill() == Color.LIGHTGREEN) {
                rectangle.setFill((GridPane.getRowIndex(stackPane) + GridPane.getColumnIndex(stackPane)) % 2 == 0 ? Color.web("#ebebd0", 1.0) : Color.web("#779455", 1.0));
            }
        }
    }


    private boolean isKingInCheck(boolean isWhite) {
        int kingRow = -1, kingCol = -1;
        String kingType = isWhite ? "roiB" : "roiN";


        // Find the king's position
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


        // Check if any enemy piece can attack the king
        for (int row = 0; row < taillePlateau; row++) {
            for (int col = 0; col < taillePlateau; col++) {
                StackPane stackPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
                if (!stackPane.getChildren().isEmpty() && stackPane.getChildren().size() > 1) {
                    ImageView pieceImageView = (ImageView) stackPane.getChildren().get(1);
                    String pieceType = (String) pieceImageView.getUserData();
                    if ((isWhite && pieceType.endsWith("N")) || (!isWhite && pieceType.endsWith("B"))) {
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

    private void capturePiece(ImageView targetPieceImageView) {
        targetPieceImageView.setFitWidth(35);
        targetPieceImageView.setFitHeight(35);
        String targetPieceType = (String) targetPieceImageView.getUserData();

        // Ajouter l'image de la pièce capturée à la liste appropriée
        if (targetPieceType.endsWith("N")) {
            PieceCapNoir.add(targetPieceImageView);
        } else if (targetPieceType.endsWith("B")) {
            PieceCapBlanc.add(targetPieceImageView);
        }

        // Mettre à jour l'affichage dans les HBox
        updateCapturedPiecesDisplay();
    }

    private void updateCapturedPiecesDisplay() {
        // Effacer les enfants des HBox
        capturedPiecesNoirHBox.getChildren().clear();
        capturedPiecesBlancHBox.getChildren().clear();

        // Ajouter toutes les images des pièces capturées à la HBox appropriée
        capturedPiecesNoirHBox.getChildren().addAll(PieceCapNoir);
        capturedPiecesBlancHBox.getChildren().addAll(PieceCapBlanc);
    }

}

