package com.example.sae202;

import java.util.ArrayList;
import java.util.List;

public class renne extends Pieces{
    public renne(String type, String color) {
        super(type, color);
    }

    public static List<int[]> getValidMovesForQueen(int row, int col, String pieceType, echiquier board) {
        List<int[]> validMoves = new ArrayList<>();
        validMoves.addAll(tour.getValidMovesForRook(row, col, pieceType, board));
        validMoves.addAll(fou.getValidMovesForBishop(row, col, pieceType, board));
        return validMoves;
    }
}
