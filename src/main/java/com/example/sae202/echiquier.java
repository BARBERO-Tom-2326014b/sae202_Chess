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
    private int colPieceSelec;
    private Text tourJoueurText = new Text("White's turn");
    private Chronometre chronoBlanc;
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
        String winner = tourBlanc ? "White" : "Black";
        tourJoueurText.setText(winner + " wins!");

        // Close the primary stage and show the alert
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(winner + " wins by capturing the king!");
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
                    clickedPane.setStyle("-fx-border-color: blue; -fx-border-width: 3px;");
                }
            }
        } else {
            // Move piece
            selectedPiecePane.setStyle(null);
            if (isValidMove(row, col, typePieceSelec)) {
                // If the target cell contains an image, remove it
                if (clickedPane.getChildren().size() > 1) {
                    ImageView targetPieceImageView = (ImageView) clickedPane.getChildren().get(1);
                    String targetPieceType = (String) targetPieceImageView.getUserData();
                    if (targetPieceType.equals("roiB") || targetPieceType.equals("roiN")) {
                        stopGame();
                    }
                    clickedPane.getChildren().remove(1);
                }

                ImageView pieceImageView = (ImageView) selectedPiecePane.getChildren().get(1);
                selectedPiecePane.getChildren().remove(pieceImageView);
                clickedPane.getChildren().add(pieceImageView);
                selectedPiecePane = null;
                switchPlayer();
            } else {
                selectedPiecePane = null;
            }
        }
    }

    private boolean isValidMove(int targetRow, int targetCol, String pieceType) {
        // Add your own piece-specific movement rules here
        return true;
    }

    private void switchPlayer() {
        tourBlanc = !tourBlanc;
        tourJoueurText.setText(tourBlanc ? "White's turn" : "Black's turn");
        switchChrono();
    }

    // Method to check if a position is valid on the chessboard
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < taillePlateau && col >= 0 && col < taillePlateau;
    }

    // Method to check if a position is empty
    protected boolean isEmpty(int newRow, int newCol) {
        return pieces[newRow][newCol] == null;
    }

    // Method to check if a piece at a position is an enemy piece
    // Method to check if a piece at a position is an enemy piece
    protected boolean isEnemyPiece(int newRow, int newCol) {
        Pieces piece = pieces[newRow][newCol];
        if (piece == null) {
            return false;
        }
        boolean pieceIsWhite = isWhite(piece);
        return tourBlanc ? !pieceIsWhite : pieceIsWhite;
    }


    private boolean isWhite(Pieces piece) {
        if (piece == null) {
            return false; // Return false if the piece is null (not a valid scenario for your board)
        }

        // Assuming your Pieces class or equivalent has a way to determine its color
        return piece.getColor().equals("white"); // Adjust this based on how you determine piece color
    }

}
