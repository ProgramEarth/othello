package com.four.othello;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

        mainpane.setOnMouseClicked(e -> {
            if (this.endgame) {
                System.out.println("endgame true");
                mainpane.getChildren().remove(textbox);
                board.gridpane.setOpacity(1);
                board.refreshBoard();
                black.score = 0;
                white.score = 0;

                setup();

                this.endgame = false;
                currentPlayerBlack = true;
            } else {
                System.out.println("endgame false");
                int col = (int) Math.round(e.getX()) / SQUARE_SIZE;
                int row = (int) Math.round(e.getY()) / SQUARE_SIZE;

                Player currentPlayer;
                Player otherPlayer;

                if (currentPlayerBlack) {
                    currentPlayer = black;
                    otherPlayer = white;
                } else {
                    currentPlayer = white;
                    otherPlayer = black;
                }

                boolean possible = board.checkIfPossible(row, col, currentPlayer);
                if (possible) {
                    board.updatePiece(row, col, currentPlayer);
                    board.flipPieces(row, col, currentPlayer);
                    boolean hasMoves = board.highlightPossibleMoves(otherPlayer);

                    if (!hasMoves) {
                        hasMoves = board.highlightPossibleMoves(currentPlayer);
                        if (!hasMoves) {
                            this.endgame = true;
                            board.getScore(players);

                            whiteScore.setText("WHITE: " + white.score);
                            whiteScore.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
                            whiteScore.setFill(Color.WHITE);

                            blackScore.setText("BLACK: " + black.score);
                            blackScore.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
                            blackScore.setFill(Color.WHITE);

                            board.gridpane.setOpacity(0.5);
                            mainpane.getChildren().add(textbox);
                        }
                    }
                    else {
                        if (currentPlayerBlack) {
                            currentPlayerBlack = false;
                        } else {
                            currentPlayerBlack = true;
                        }
                    }
                }
            }
        });
    }

    public void setup() {
        Player black = players[0];
        Player white = players[1];

        board.updatePiece(3, 3, black);
        board.updatePiece(4, 4, black);
        board.updatePiece(3, 4, white);
        board.updatePiece(4, 3, white);

        board.highlightPossibleMoves(black);
    }

    public static void main(String[] args) {
        launch();
    }
}
