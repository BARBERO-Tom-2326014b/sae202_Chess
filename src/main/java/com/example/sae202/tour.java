package com.example.sae202;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class tour extends Pieces{
    public tour(String type, String color) {
        super(type, color);
    }

    public static List<int[]> getValidMovesForRook(int row, int col, String pieceType, echiquier board) {
        List<int[]> validMoves = new ArrayList<>();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] dir : directions) {
            int newRow = row;
            int newCol = col;
            while (true) {
                newRow += dir[0];
                newCol += dir[1];
                if (!board.isValidPosition(newRow, newCol) || (!board.isEmpty(newRow, newCol) && !board.isEnemyPiece(newRow, newCol))) {
                    break;
                }
                validMoves.add(new int[]{newRow, newCol});
                if (!board.isEmpty(newRow, newCol)) {
                    break;
                }
            }
        }
        return validMoves;
    }
}