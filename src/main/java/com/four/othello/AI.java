package com.four.othello;

import java.util.ArrayList;

public class AI {

    Player player;

    AI(Player player) {
        this.player = player;
    }

    // greedy algorithm
    public int[] greedyAlgorithm(Board board) {
        int[] outputPos = new int[2];

        Piece outputPiece = new Piece();

        ArrayList<Piece> possibleMoves = board.findPossibleMoves(this.player);

        int max_score = 0;

        for (Piece move : possibleMoves) {
            board.updatePiece(move, this.player);
            int score = evaluateBoard(board, this.player);
            if (score > max_score) {
                outputPiece = move;
                max_score = score;
            }

            board.emptyPiece(move);
        }

        outputPos = board.getIndex(outputPiece);

        return outputPos;
    }

    // minimax algorithm, looking n moves ahead
    public Piece minimax(Board board, int n, Player ai_player, Player human_player, Player currPlayer, Player otherPlayer) {
        ArrayList<Piece> possibleMoves = board.findPossibleMoves(currPlayer);

        Piece outputPiece = new Piece();
        int max_score = 0;
        int next_n = n-1;

        System.out.println(next_n);

        // bottom of search tree
        if (possibleMoves.isEmpty()) {
            possibleMoves = board.findPossibleMoves(otherPlayer);
            if (possibleMoves.isEmpty()) {
                int score = evaluateBoard(board, otherPlayer);
                Piece dummyPiece = new Piece();
                dummyPiece.score = score;
                return dummyPiece;
            }
        }

        if (next_n <= 0) {
            // incentivised to maximise its own score
            for (Piece move : possibleMoves) {
                // make move
                board.updatePiece(move, currPlayer);
                int score = evaluateBoard(board, currPlayer);
                if (score > max_score) {
                    outputPiece = move;
                    max_score = score;
                }

                board.emptyPiece(move);
            }

            // return the move with the maximum score
            outputPiece.score = max_score;
            return outputPiece;
        } else {
            Piece tempPiece;

            for (Piece move : possibleMoves) {
                board.updatePiece(move, currPlayer);

                if (currPlayer == human_player) {
                    tempPiece = minimax(board, next_n, ai_player, human_player, ai_player, human_player);
                } else {
                    tempPiece = minimax(board, next_n, ai_player, human_player, human_player, ai_player);
                }

                if (tempPiece.score > max_score) {
                    max_score = tempPiece.score;
                    outputPiece = move;
                }
                board.emptyPiece(move);
            }

            outputPiece.score = max_score;
            return outputPiece;
        }
    }

    // calculate a score based on the state of the board
    public int evaluateBoard(Board board, Player player) {

        // raw number of discs by the player - higher is better
        int count = 0;
        for (int i=0; i<Game.NUM_SQUARES; i++) {
            for (int j=0; j<Game.NUM_SQUARES; j++) {
                Piece piece = board.grid[i][j];

                if (piece.occupiedBy == player) {
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
