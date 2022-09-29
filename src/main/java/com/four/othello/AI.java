package com.four.othello;

import javafx.util.Pair;

import java.io.*;
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
    public Pair<Piece, Integer> minimax(Board board, int n, Player ai_player, Player human_player, Player currPlayer, Player otherPlayer) {
        ArrayList<Piece> possibleMoves = board.findPossibleMoves(currPlayer);
        for (Piece move : possibleMoves) {
            if (move == board.grid[0][0]) {
                System.out.println("corner is a possible move");
            }
        }

        Piece outputPiece;
        int[] outputPos = new int[2];
        int outputScore = 0;
        int max_score = -10000;
        int min_score = 10000;
        int next_n = n-1;

        // bottom of search tree
        if (possibleMoves.isEmpty()) {
            ArrayList<Piece> nextMoves = board.findPossibleMoves(otherPlayer);
            if (nextMoves.isEmpty()) {
                int score = evaluateBoard(board, otherPlayer);
                outputPiece = new Piece();
                return new Pair(outputPiece, score);
            }
        }

        if (next_n <= 0) {
            // incentivised to maximise its own score
            for (Piece move : possibleMoves) {
                Board tempBoard = new Board(board);
                // make move
                int[] position = board.getIndex(move);
                int row = position[0];
                int col = position[1];
                System.out.println("move: " + row + " " + col);
                tempBoard.updatePiece(row, col, currPlayer);
                tempBoard.flipPieces(row, col, currPlayer);
                int score = evaluateBoard(tempBoard, ai_player) - evaluateBoard(tempBoard, human_player);
                if (currPlayer == ai_player) {
                    if (score > max_score) {
                        outputPos = position;
                        max_score = score;
                        outputScore = score;
                    }
                } else {
                    if (score < min_score) {
                        outputPos = position;
                        min_score = score;
                        outputScore = score;
                    }
                }
            }
        } else {
            for (Piece move : possibleMoves) {
                Board tempBoard = new Board(board);
                int[] position = board.getIndex(move);
                int row = position[0];
                int col = position[1];
                System.out.println("move: " + row + " " + col);
                tempBoard.updatePiece(row, col, currPlayer);
                tempBoard.flipPieces(row, col, currPlayer);

                if (currPlayer == ai_player) {
                    Pair<Piece, Integer> tempPair = minimax(tempBoard, next_n, ai_player, human_player, human_player, ai_player);
                    if (tempPair.getValue() > max_score) {
                        outputPos = position;
                        max_score = tempPair.getValue();
                        outputScore = tempPair.getValue();
                    }
                } else {
                    Pair<Piece, Integer> tempPair = minimax(tempBoard, next_n, ai_player, human_player, ai_player, human_player);
                    if (tempPair.getValue() < min_score) {
                        outputPos = position;
                        min_score = tempPair.getValue();
                        outputScore = tempPair.getValue();
                    }
                }
            }
        }

        int row = outputPos[0];
        int col = outputPos[1];
        System.out.println(row + " " + col);
        outputPiece = board.grid[row][col];

        return new Pair(outputPiece, outputScore);
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
