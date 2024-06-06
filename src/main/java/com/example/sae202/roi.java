package com.example.sae202;

import java.util.ArrayList;
import java.util.List;

public class roi extends Pieces{
    public roi(String type, String color) {
        super(type, color);
    }

    public static List<int[]> getValidMovesForKing(int row, int col, String pieceType, echiquier board) {
        List<int[]> validMoves = new ArrayList<>();
        int[][] moves = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        for (int[] move : moves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (board.isValidPosition(newRow, newCol) && (board.isEmpty(newRow, newCol) || board.isEnemyPiece(newRow, newCol))) {
                validMoves.add(new int[]{newRow, newCol});
            }
        }
        return validMoves;
    }
}