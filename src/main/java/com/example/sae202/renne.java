package com.example.sae202;

import java.util.ArrayList;
import java.util.List;

public class renne {
    public static List<int[]> getValidMovesForQueen(int row, int col, String pieceType, echiquier board) {
        List<int[]> validMoves = new ArrayList<>();
        validMoves.addAll(tour.getValidMovesForRook(row, col, pieceType, board));
        validMoves.addAll(fou.getValidMovesForBishop(row, col, pieceType, board));
        return validMoves;
    }
}
