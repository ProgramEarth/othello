package com.four.othello;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.*;
import java.util.ArrayList;

public class Board {
    GridPane gridpane = new GridPane();
    Piece[][] grid = new Piece[Game.NUM_SQUARES][Game.NUM_SQUARES];

    Board() {
        for (int i=0; i<Game.NUM_SQUARES; i++) {
            for (int j=0; j<Game.NUM_SQUARES; j++) {
                Piece piece = new Piece();
                grid[i][j] = piece;
                gridpane.add(piece.square, j, i);
            }
        }
    }

    public Board(Board original) {
        this.gridpane = original.gridpane;
        for (int i=0; i<Game.NUM_SQUARES; i++) {
            for (int j=0; j<Game.NUM_SQUARES; j++) {
                Piece originalPiece = original.grid[i][j];
                this.grid[i][j] = new Piece(originalPiece);
            }
        }
    }

    public ArrayList<Piece> findPossibleMoves(Player player) {
        ArrayList<Piece> possibleMoves = new ArrayList<>();
        // for every square, check if that square is of the opposite colour
        for (int row=0; row<Game.NUM_SQUARES; row++) {
            for (int col=0; col<Game.NUM_SQUARES; col++) {
                Piece piece = grid[row][col];
                if (!piece.empty && piece.occupiedBy != player) {
                    // get adjacent empty squares
                    for (int i=row-1; i<=row+1; i++) {
                        for (int j=col-1; j<=col+1; j++) {
                            if (i == row && j == col) {
                                continue;
                            } else if (i >= Game.NUM_SQUARES || j >= Game.NUM_SQUARES) {
                                continue;
                            } else if (i < 0 || j < 0) {
                                continue;
                            }

                            // empty adjacent squares
                            boolean possibleMove = false;
                            piece = grid[i][j];
                            if (piece.empty) {
                                int p = row;
                                int q = col;
                                Piece nextPiece = null;
                                while (possibleMove == false) { // while a piece of the player haven't been reached
                                    Direction direction = getDirection(row, col, i, j);

                                    p -= direction.row_change;
                                    q -= direction.col_change;

                                    // index out of bounds
                                    if (p >= Game.NUM_SQUARES || q >= Game.NUM_SQUARES) {
                                        break;
                                    } else if (p < 0 || q < 0) {
                                        break;
                                    }

                                    nextPiece = grid[p][q];
                                    if (nextPiece.occupiedBy == player) {
                                        possibleMove = true;
                                    } else if (nextPiece.empty) {
                                        break;
                                    }
                                }
                                if (possibleMove) {
                                    possibleMoves.add(piece);
                                }
                            }
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }

    public Direction getDirection(int row, int col, int i, int j) {
        Direction direction = null;
        if (i == row-1 && j == col-1) { // if left up: check along right down diagonal
            direction = new Direction(0, -1, -1);
        } else if (i == row-1 && j == col) { // if directly up: check down
            direction = new Direction(0, -1, 0);
        } else if (i == row-1 && j == col+1) { // right up: check left down diagonal
            direction = new Direction(0, -1, 1);
        } else if (i == row && j == col-1) { // left: check right
            direction = new Direction(0, 0, -1);
        } else if (i == row && j == col+1) { // right: check left
            direction = new Direction(0, 0, 1);
        } else if (i == row+1 && j == col-1) { // left down: check right up diagonal
            direction = new Direction(0, 1, -1);
        } else if (i == row+1 && j == col) { // down: check up
            direction = new Direction(0, 1, 0);
        } else if (i == row+1 && j == col+1) { // right down: check left up diagonal
            direction = new Direction(0, 1, 1);
        }

        return direction;
    }

    public boolean highlightPossibleMoves(Player player) {
        ArrayList<Piece> possibleMoves = findPossibleMoves(player);

        // reset background colour
        resetBackgroundColour();

        if (possibleMoves.size() == 0) {
            return false;
        }

        // highlight possible moves
        for (Piece piece:possibleMoves) {
            piece.square.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        }

        return true;
    }

    public void resetBackgroundColour() {
        for (int row=0; row<Game.NUM_SQUARES; row++) {
            for (int col=0; col<Game.NUM_SQUARES; col++) {
                Piece piece = grid[row][col];
                piece.square.setBackground(new Background(new BackgroundFill(Color.MEDIUMSEAGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }

    public int[] getIndex(Piece piece) {
        int[] positions = new int[2];

        for (int i=0; i<Game.NUM_SQUARES; i++) {
            for (int j=0; j<Game.NUM_SQUARES; j++) {
                if (piece == grid[i][j]) {
                    positions[0] = i;
                    positions[1] = j;
                }
            }
        }

        return positions;
    }

    public boolean checkIfPossible(int row, int col, Player player) {
        ArrayList<Piece> possibleMoves = findPossibleMoves(player);
        Piece piece = grid[row][col];
        if (possibleMoves.contains(piece)) {
            return true;
        } else {
            return false;
        }
    }

    public void flipPieces(int row, int col, Player player) {
        ArrayList<Direction> directions = new ArrayList<>(); // store possible directions
        for (int i=row-1; i<=row+1; i++) {
            for (int j=col-1; j<=col+1; j++) {
                if (i == row && j == col) {
                    continue;
                } else if (i >= Game.NUM_SQUARES || j >= Game.NUM_SQUARES) {
                    continue;
                } else if (i < 0 || j < 0) {
                    continue;
                }

                int p = i;
                int q = j;
                Piece piece = grid[i][j];

                while (!piece.empty && piece.occupiedBy != player) {
                    Direction direction = getDirection(row, col, i, j);

                    p += direction.row_change;
                    q += direction.col_change;

                    // index out of bounds
                    if (p >= Game.NUM_SQUARES || q >= Game.NUM_SQUARES) {
                        break;
                    } else if (p < 0 || q < 0) {
                        break;
                    }

                    piece = grid[p][q];
                    if (piece.occupiedBy == player) {
                        directions.add(direction);
                        break;
                    } else if (piece.empty) {
                        break;
                    }
                }
            }
        }

        for (Direction direction:directions) {
            updateLine(row, col, direction, player);
        }
    }

    public void updateLine(int row, int col, Direction direction, Player player) {
        int p = row + direction.row_change;
        int q = col + direction.col_change;
        Piece piece = grid[p][q];
        while (!piece.empty && piece.occupiedBy != player) {

            updatePiece(p, q, player);

            p += direction.row_change;
            q += direction.col_change;

            // index out of bounds
            if (p >= Game.NUM_SQUARES || q >= Game.NUM_SQUARES) {
                break;
            } else if (p < 0 || q < 0) {
                break;
            }

            piece = grid[p][q];
            if (piece.occupiedBy == player) {
                break;
            }
        }
    }

    public void updatePiece(int row, int col, Player player) {
        Piece piece = grid[row][col];
        piece.empty = false;
        piece.occupiedBy = player;
        Circle circle = new Circle();
        circle.setRadius(Game.SQUARE_SIZE/2 - 10);
        circle.setFill(player.colour);
        piece.square.getChildren().add(circle);
    }

    public void updatePiece(Piece piece, Player player) {
        piece.empty = false;
        piece.occupiedBy = player;
        Circle circle = new Circle();
        circle.setRadius(Game.SQUARE_SIZE/2 - 10);
        circle.setFill(player.colour);
        piece.square.getChildren().add(circle);
    }

    public void emptyPiece(Piece piece) {
        for (int i=0; i<Game.NUM_SQUARES; i++) {
            for (int j=0; j<Game.NUM_SQUARES; j++) {
                if (grid[i][j] == piece) {
                    piece.empty = true;
                    piece.occupiedBy = null;
                    piece.score = 0;
                    piece.square.getChildren().clear();
                }
            }
        }
    }

    public void getScore(Player[] players) {
        Player black = players[0];
        Player white = players[1];

        for (int i=0; i<Game.NUM_SQUARES; i++) {
            for (int j=0; j<Game.NUM_SQUARES; j++) {
                Piece piece = grid[i][j];
                if (piece.occupiedBy == black) {
                    black.score++;
                } else if (piece.occupiedBy == white) {
                    white.score++;
                }
            }
        }
    }

    public void refreshBoard() {
        for (int i=0; i<Game.NUM_SQUARES; i++) {
            for (int j=0; j<Game.NUM_SQUARES; j++) {
                Piece piece = new Piece();
                grid[i][j] = piece;
                gridpane.add(piece.square, j, i);
            }
        }
    }
}
