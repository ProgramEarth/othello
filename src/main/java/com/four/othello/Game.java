package com.four.othello;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;

public class Game extends Application {
    Player[] players = new Player[2];
    Board board = new Board();
    boolean currentPlayerBlack = true;
    static final int BOARD_SIZE = 800;
    static final int NUM_SQUARES = 8;
    static final int SQUARE_SIZE = BOARD_SIZE/NUM_SQUARES;
    boolean endgame = false;

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, BOARD_SIZE, BOARD_SIZE);
        stage.setTitle("Othello");

        StackPane mainpane = new StackPane();
        mainpane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Text endTitle = new Text();
        endTitle.setText("END GAME");
        endTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
        endTitle.setFill(Color.WHITE);

        VBox textbox = new VBox();
        textbox.setAlignment(Pos.CENTER);
        Text whiteScore = new Text();
        Text blackScore = new Text();

        textbox.getChildren().add(endTitle);
        textbox.getChildren().add(whiteScore);
        textbox.getChildren().add(blackScore);

        Player black = new Player(Color.BLACK);
        Player white = new Player(Color.WHITE);

        players[0] = black;
        players[1] = white;

        setup();

        mainpane.getChildren().add(board.gridpane);

        root.setCenter(mainpane);

        stage.setScene(scene);
        stage.show();

        // one player, one AI
        Player human_player = black;
        AI ai_player = new AI(white);

        mainpane.setOnMouseClicked(e -> {
            if (!this.endgame) {
                int col = (int) Math.round(e.getX()) / SQUARE_SIZE;
                int row = (int) Math.round(e.getY()) / SQUARE_SIZE;

                boolean possible = board.checkIfPossible(row, col, human_player);

                if (possible) {
                    board.updatePiece(row, col, human_player);
                    board.flipPieces(row, col, human_player);
                    board.resetBackgroundColour();

                    // check if there are any possible moves for the AI
                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            // wait 2s
                            ArrayList<Piece> hasMoves = board.findPossibleMoves(ai_player.player);
                            while (!hasMoves.isEmpty()) {
//                                int[] move = ai_player.greedyAlgorithm(board);
                                Piece piece = ai_player.minimax(board, 4, ai_player.player, human_player, ai_player.player, human_player);
                                int[] move = board.getIndex(piece);
                                int row = move[0];
                                int col = move[1];

                                board.updatePiece(row, col, ai_player.player);
                                board.flipPieces(row, col, ai_player.player);

                                ArrayList<Piece> humanHasMoves = board.findPossibleMoves(human_player);
                                if (!humanHasMoves.isEmpty()) {
                                    board.highlightPossibleMoves(human_player);
                                    board.grid[row][col].square.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                                    break;
                                } else {
                                    hasMoves = board.findPossibleMoves(ai_player.player);
                                }
                            }
                            while (hasMoves.isEmpty()) {
                                ArrayList<Piece> humanHasMoves = board.findPossibleMoves(human_player);
                                if (!humanHasMoves.isEmpty()) {
                                    board.highlightPossibleMoves(human_player);
                                    break;
                                } else {
                                    endgame = true;
                                    // reset square background colour
                                    board.resetBackgroundColour();
                                    board.getScore(players);

                                    whiteScore.setText("WHITE: " + white.score);
                                    whiteScore.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
                                    whiteScore.setFill(Color.WHITE);

                                    blackScore.setText("BLACK: " + black.score);
                                    blackScore.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
                                    blackScore.setFill(Color.WHITE);

                                    board.gridpane.setOpacity(0.5);
                                    mainpane.getChildren().add(textbox);
                                    break;
                                }
                            }
                        }
                    }));
                    timeline.setCycleCount(1);
                    timeline.play();
                }
            } else {
                mainpane.getChildren().remove(textbox);
                board.gridpane.setOpacity(1);
                board.refreshBoard();
                black.score = 0;
                white.score = 0;

                setup();

                this.endgame = false;
            }
        });


        // two players
//        mainpane.setOnMouseClicked(e -> {
//            if (this.endgame) {
//                mainpane.getChildren().remove(textbox);
//                board.gridpane.setOpacity(1);
//                board.refreshBoard();
//                black.score = 0;
//                white.score = 0;
//
//                setup();
//
//                this.endgame = false;
//                currentPlayerBlack = true;
//            } else {
//                int col = (int) Math.round(e.getX()) / SQUARE_SIZE;
//                int row = (int) Math.round(e.getY()) / SQUARE_SIZE;
//
//                Player currentPlayer;
//                Player otherPlayer;
//
//                if (currentPlayerBlack) {
//                    currentPlayer = black;
//                    otherPlayer = white;
//                } else {
//                    currentPlayer = white;
//                    otherPlayer = black;
//                }
//
//                boolean possible = board.checkIfPossible(row, col, currentPlayer);
//                if (possible) {
//                    board.updatePiece(row, col, currentPlayer);
//                    board.flipPieces(row, col, currentPlayer);
//                    boolean hasMoves = board.highlightPossibleMoves(otherPlayer);
//
//                    if (!hasMoves) {
//                        hasMoves = board.highlightPossibleMoves(currentPlayer);
//                        if (!hasMoves) {
//                            this.endgame = true;
//                            board.getScore(players);
//
//                            whiteScore.setText("WHITE: " + white.score);
//                            whiteScore.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
//                            whiteScore.setFill(Color.WHITE);
//
//                            blackScore.setText("BLACK: " + black.score);
//                            blackScore.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
//                            blackScore.setFill(Color.WHITE);
//
//                            board.gridpane.setOpacity(0.5);
//                            mainpane.getChildren().add(textbox);
//                        }
//                    }
//                    else {
//                        if (currentPlayerBlack) {
//                            currentPlayerBlack = false;
//                        } else {
//                            currentPlayerBlack = true;
//                        }
//                    }
//                }
//            }
//        });
    }

    public void setup() {
        Player black = players[0];
        Player white = players[1];

        board.updatePiece(3, 3, white);
        board.updatePiece(4, 4, white);
        board.updatePiece(3, 4, black);
        board.updatePiece(4, 3, black);

        board.highlightPossibleMoves(black);
    }

    public boolean checkEndgame(Player currentPlayer, Player nextPlayer) {
        ArrayList<Piece> hasMoves = board.findPossibleMoves(nextPlayer);
        System.out.println(hasMoves.size());

        if (hasMoves.isEmpty()) {
            hasMoves = board.findPossibleMoves(currentPlayer);
            System.out.println(hasMoves.size());
            if (hasMoves.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch();
    }
}
