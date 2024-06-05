package com.example.sae202;

import java.util.ArrayList;
import java.util.List;

public class renne {
    public static List<int[]> getValidMovesForPawn(int row, int col, String pieceType, echiquier board) {
        List<int[]> validMoves = new ArrayList<>();

        // Calculer les mouvements valides pour le pion
        int direction = pieceType.endsWith("B") ? -1 : 1; // La direction dépend de la couleur de la pièce
        int startRow = pieceType.endsWith("B") ? 6 : 1; // La rangée de départ dépend de la couleur de la pièce
        int[] singleStep = {direction, 0}; // Un pas en avant
        int[] doubleStep = {2 * direction, 0}; // Deux pas en avant au premier mouvement
        int[][] attackMoves = {{direction, 1}, {direction, -1}}; // Mouvements d'attaque

        // Vérifier le mouvement simple en avant
        if (board.isEmpty(row + direction, col)) {
            validMoves.add(new int[]{row + direction, col});
            // Vérifier le mouvement double en avant au premier mouvement
            if (row == startRow && board.isEmpty(row + 2 * direction, col)) {
                validMoves.add(new int[]{row + 2 * direction, col});
            }
        }

        // Vérifier les mouvements d'attaque diagonaux
        for (int[] move : attackMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (board.isValidPosition(newRow, newCol) && board.isEnemyPiece(newRow, newCol)) {
                validMoves.add(new int[]{newRow, newCol});
            }
        }

        return validMoves;
    }
}
