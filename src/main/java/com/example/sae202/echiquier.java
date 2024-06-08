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
    // Déclaration des constantes pour la taille des cases et du plateau
    static final int tailleCase = 80;
    static final int taillePlateau = 8;
    // Déclaration des variables de jeu
    private Pieces[][] pieces;
    private boolean tourBlanc = true;
    private boolean enJeux = true;
    // Déclaration des variables pour le suivi de la pièce sélectionnée
    private StackPane selectedPiecePane = null;
    private String typePieceSelec = null;
    private int lignePieceSelec;
    private List<ImageView> PieceCapNoir = new ArrayList<>();
    private List<ImageView> PieceCapBlanc = new ArrayList<>();
    private HBox capturedPiecesBlancHBox = new HBox();
    private HBox capturedPiecesNoirHBox = new HBox();
    private int colPieceSelec;
    private Text tourJoueurText = new Text("White's turn");
    private Chronometre chronoBlanc;
    private Chronometre chronoNoir;
    private VBox vbox = new VBox();
    private VBox whiteVBox = new VBox();
    private VBox blackVBox = new VBox();
    static GridPane gridPane = new GridPane();
    private Stage primaryStage;
    private int initialTime;

    // Constructeur par défaut
    public echiquier() {
        this(300); // Défaut à 5 minutes si aucun temps n'est spécifié
    }

    // Constructeur avec temps initial spécifié
    public echiquier(int initialTime) {
        this.initialTime = initialTime;
        // Initialisation des chronomètres
        chronoBlanc = new Chronometre(initialTime, this);
        chronoNoir = new Chronometre(initialTime, this);
    }

    // Méthode principale pour lancer l'application JavaFX
    public static void main(String[] args) {
        launch(args);
    }

    // Méthode pour basculer le chronomètre
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

    // Méthode pour mettre à jour l'affichage du chronomètre
    private void updateChronoDisplay() {
        whiteVBox.getChildren().clear();
        blackVBox.getChildren().clear();

        if (tourBlanc) {
            whiteVBox.getChildren().addAll(new Text("Tour Blanc"), chronoBlanc.getTimeLabel());
            blackVBox.getChildren().addAll(new Text(""), chronoNoir.getTimeLabel());
        } else {
            whiteVBox.getChildren().addAll(new Text(""), chronoBlanc.getTimeLabel());
            blackVBox.getChildren().addAll(new Text("Tour Noir"), chronoNoir.getTimeLabel());
        }
    }

    // Méthode pour arrêter le jeu
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

    // Méthode pour créer l'échiquier
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

    // Méthode pour initialiser les pièces sur l'échiquier
    private void initializePieces() {
        // Tableau représentant les pièces sur l'échiquier, avec leur position initiale
        String[][] pieces = {
                {"tourN", "cavalierN", "fouN", "reineN", "roiN", "fouN", "cavalierN", "tourN"}, // Les pièces noires de la première rangée
                {"pionN", "pionN", "pionN", "pionN", "pionN", "pionN", "pionN", "pionN"}, // Les pions noirs
                {null, null, null, null, null, null, null, null}, // Cases vides
                {null, null, null, null, null, null, null, null}, // Cases vides
                {null, null, null, null, null, null, null, null}, // Cases vides
                {null, null, null, null, null, null, null, null}, // Cases vides
                {"pionB", "pionB", "pionB", "pionB", "pionB", "pionB", "pionB", "pionB"}, // Les pions blancs
                {"tourB", "cavalierB", "fouB", "reineB", "roiB", "fouB", "cavalierB", "tourB"} // Les pièces blanches de la première rangée
        };

        // Parcourt le tableau des pièces
        for (int row = 0; row < taillePlateau; row++) {
            for (int col = 0; col < taillePlateau; col++) {
                String piece = pieces[row][col];
                // Si la case contient une pièce
                if (piece != null) {
                    // Charge l'image de la pièce depuis les ressources
                    Image image = new Image(getClass().getResourceAsStream("/img/" + piece + ".png"));
                    // Crée une vue d'image pour afficher la pièce sur l'échiquier
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(tailleCase);
                    imageView.setFitHeight(tailleCase);
                    imageView.setUserData(piece); // Définit le type de pièce comme données utilisateur
                    // Obtient le StackPane correspondant à cette case sur l'échiquier
                    StackPane stackPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
                    // Ajoute l'image de la pièce au StackPane correspondant
                    stackPane.getChildren().add(imageView);
                }
            }
        }
    }


    // Méthode principale pour démarrer l'application JavaFX
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        creationEchequier(gridPane);
        Pieces.initializePieces();
        updateChronoDisplay();
        vbox.getChildren().addAll(blackVBox, capturedPiecesNoirHBox, gridPane, whiteVBox, capturedPiecesBlancHBox);
        Scene scene = new Scene(vbox, tailleCase * taillePlateau, tailleCase * taillePlateau + 150);
        primaryStage.setTitle("Chess Board");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Méthode pour gérer le clic de la souris
    private void clickSouris(MouseEvent event, int row, int col, StackPane clickedPane) {
        // Vérifie si le jeu est en cours, sinon ne fait rien
        if (!enJeux) return;

        // Si aucune pièce n'est sélectionnée
        if (selectedPiecePane == null) {
            // Vérifie si la case cliquée contient une pièce du joueur en cours
            if (!clickedPane.getChildren().isEmpty() && clickedPane.getChildren().size() > 1) {
                ImageView pieceImageView = (ImageView) clickedPane.getChildren().get(1);
                String pieceType = (String) pieceImageView.getUserData();
                // Vérifie si la pièce appartient au joueur en cours
                if ((tourBlanc && pieceType.endsWith("B")) || (!tourBlanc && pieceType.endsWith("N"))) {
                    // Sélectionne la pièce et affiche ses mouvements valides
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
            // Si une pièce est déjà sélectionnée
            List<int[]> validMoves = getValidMoves(lignePieceSelec, colPieceSelec, typePieceSelec);
            boolean isValidMove = false;
            // Vérifie si le mouvement sélectionné est valide
            for (int[] move : validMoves) {
                if (move[0] == row && move[1] == col) {
                    isValidMove = true;
                    break;
                }
            }
            // Si le mouvement est valide
            if (isValidMove) {
                // Si la case cible contient une pièce ennemie
                if (!clickedPane.getChildren().isEmpty() && clickedPane.getChildren().size() > 1) {
                    ImageView targetPieceImageView = (ImageView) clickedPane.getChildren().get(1);
                    String targetPieceType = (String) targetPieceImageView.getUserData();
                    // Si la pièce ennemie est un roi, le jeu se termine
                    if (targetPieceType.endsWith("roiB") || targetPieceType.endsWith("roiN")) {
                        stopGame();
                        return;
                    }
                    // Capture la pièce ennemie
                    clickedPane.getChildren().remove(targetPieceImageView);
                    capturePiece(targetPieceImageView);
                }
                // Déplace la pièce sélectionnée vers la nouvelle case
                ImageView pieceImageView = (ImageView) selectedPiecePane.getChildren().get(1);
                selectedPiecePane.getChildren().remove(pieceImageView);
                clickedPane.getChildren().add(pieceImageView);
                resetColors();
                boolean currentPlayerIsWhite = !tourBlanc;
                // Vérifie si le roi du joueur actuel est en échec
                if (isKingInCheck(currentPlayerIsWhite)) {
                    // Affiche une alerte indiquant que le roi est en échec
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Check");
                    alert.setHeaderText(null);
                    alert.setContentText((currentPlayerIsWhite ? "White" : "Black") + " is in check!");
                    alert.showAndWait();
                }
                // Réinitialise les variables et passe au tour suivant
                selectedPiecePane = null;
                typePieceSelec = null;
                tourBlanc = !tourBlanc;
                switchChrono();
                tourJoueurText.setText(tourBlanc ? "Tour noir" : "Tour Blanc");
            } else {
                // Si le mouvement n'est pas valide, réinitialise les couleurs et les variables de sélection
                resetColors();
                selectedPiecePane = null;
                typePieceSelec = null;
            }
        }
    }


    // Méthode pour obtenir les mouvements valides pour une pièce donnée
    private List<int[]> getValidMoves(int row, int col, String pieceType) {
        if (pieceType.startsWith("pion")) {
            return pion.getValidMovesForPawn(row, col, pieceType, this); // Obtenir les mouvements valides pour un pion
        } else if (pieceType.startsWith("tour")) {
            return tour.getValidMovesForRook(row, col, pieceType, this); // Obtenir les mouvements valides pour une tour
        } else if (pieceType.startsWith("cavalier")) {
            return cavalier.getValidMovesForKnight(row, col, pieceType, this); // Obtenir les mouvements valides pour un cavalier
        } else if (pieceType.startsWith("fou")) {
            return fou.getValidMovesForBishop(row, col, pieceType, this); // Obtenir les mouvements valides pour un fou
        } else if (pieceType.startsWith("reine")) {
            return renne.getValidMovesForQueen(row, col, pieceType, this); // Obtenir les mouvements valides pour une reine
        } else if (pieceType.startsWith("roi")) {
            return roi.getValidMovesForKing(row, col, pieceType, this); // Obtenir les mouvements valides pour un roi
        }
        return new ArrayList<>(); // Retourner une liste vide si le type de pièce est inconnu
    }

    // Méthode pour mettre en surbrillance les mouvements valides
    private void highlightValidMoves(List<int[]> validMoves) {
        for (int[] move : validMoves) {
            int moveRow = move[0];
            int moveCol = move[1];
            StackPane stackPane = (StackPane) gridPane.getChildren().get(moveRow * taillePlateau + moveCol);
            Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
            rectangle.setFill(Color.LIGHTGREEN); // Mettre en surbrillance les cases avec un mouvement valide
        }
    }

    // Méthode pour réinitialiser les couleurs des cases
    private void resetColors() {
        for (Node node : gridPane.getChildren()) {
            StackPane stackPane = (StackPane) node;
            Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
            if (rectangle.getFill() == Color.YELLOW || rectangle.getFill() == Color.LIGHTGREEN) {
                // Rétablir la couleur d'origine des cases
                rectangle.setFill((GridPane.getRowIndex(stackPane) + GridPane.getColumnIndex(stackPane)) % 2 == 0 ? Color.web("#ebebd0", 1.0) : Color.web("#779455", 1.0));
            }
        }
    }

    // Méthode pour vérifier si le roi est en échec
    private boolean isKingInCheck(boolean isWhite) {
        int kingRow = -1, kingCol = -1;
        String kingType = isWhite ? "roiB" : "roiN";

        // Trouve la position du roi
        for (int row = 0; row < taillePlateau; row++) {
            for (int col = 0; col < taillePlateau; col++) {
                // Obtient le StackPane correspondant à cette case
                StackPane stackPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
                if (!stackPane.getChildren().isEmpty() && stackPane.getChildren().size() > 1) {
                    // Récupère l'ImageView de la pièce
                    ImageView pieceImageView = (ImageView) stackPane.getChildren().get(1);
                    String pieceType = (String) pieceImageView.getUserData();
                    // Vérifie si c'est le roi recherché
                    if (pieceType.equals(kingType)) {
                        kingRow = row;
                        kingCol = col;
                        break;
                    }
                }
            }
        }

        // Vérifie si une pièce adverse peut attaquer le roi
        for (int row = 0; row < taillePlateau; row++) {
            for (int col = 0; col < taillePlateau; col++) {
                // Obtient le StackPane correspondant à cette case
                StackPane stackPane = (StackPane) gridPane.getChildren().get(row * taillePlateau + col);
                if (!stackPane.getChildren().isEmpty() && stackPane.getChildren().size() > 1) {
                    // Récupère l'ImageView de la pièce
                    ImageView pieceImageView = (ImageView) stackPane.getChildren().get(1);
                    String pieceType = (String) pieceImageView.getUserData();
                    // Vérifie si c'est une pièce ennemie
                    if ((isWhite && pieceType.endsWith("N")) || (!isWhite && pieceType.endsWith("B"))) {
                        List<int[]> validMoves = getValidMoves(row, col, pieceType); // Obtient les mouvements valides pour cette pièce
                        // Parcourt tous les mouvements valides de la pièce
                        for (int[] move : validMoves) {
                            // Si le roi est dans l'un des mouvements valides, il est en échec
                            if (move[0] == kingRow && move[1] == kingCol) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false; // Le roi n'est pas en échec
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
