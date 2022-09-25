package com.four.othello;

import java.util.ArrayList;

public class AI {

    Player player;

    AI(Player player) {
        this.player = player;
    }

    // minimax for 1 turn oops
    public int[] miniMax(Board board) {
        int[] outputPos = new int[2];

        Piece outputPiece = new Piece();

        ArrayList<Piece> possibleMoves = board.findPossibleMoves(this.player);

        int max_score = 0;

        for (Piece move : possibleMoves) {
            board.updatePiece(move, this.player);
            int score = evaluateBoard(board);
            if (score > max_score) {
                outputPiece = move;
                max_score = score;
            }

            board.emptyPiece(move);
        }

        outputPos = board.getIndex(outputPiece);

        return outputPos;
    }

    // calculate a score based on the state of the board
    public int evaluateBoard(Board board) {

        // raw number of discs by the player - higher is better
        int count = 0;
        for (int i=0; i<Game.NUM_SQUARES; i++) {
            for (int j=0; j<Game.NUM_SQUARES; j++) {
                Piece piece = board.grid[i][j];

                if (piece.occupiedBy == this.player) {
                    count++;

                    // edge pieces are worth more - 2
                    if (i == 0 || j == 0 || i == Game.NUM_SQUARES-1 || j == Game.NUM_SQUARES-1) {
                        count++;

                        // corner pieces are worth even more - 5
                        if ((i == 0 && j == 0) || (i == 0 && j == Game.NUM_SQUARES-1) || (i == Game.NUM_SQUARES-1 && j == 0) || (i == Game.NUM_SQUARES-1 && j == Game.NUM_SQUARES-1)) {
                            count += 3;
                        }
                    }
                }
            }
        }

        return count;
    }
}
