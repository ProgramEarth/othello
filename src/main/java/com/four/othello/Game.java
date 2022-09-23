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

        VBox textbox = new VBox();
        textbox.setAlignment(Pos.CENTER);

        Text endTitle = new Text();
        endTitle.setText("END GAME");
        endTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
        endTitle.setFill(Color.WHITE);

        textbox.getChildren().add(endTitle);

        Player black = new Player(Color.BLACK);
        Player white = new Player(Color.WHITE);

        players[0] = black;
        players[1] = white;

        setup();
        board.highlightPossibleMoves(black);

        mainpane.getChildren().add(board.gridpane);

        root.setCenter(mainpane);

        stage.setScene(scene);
        stage.show();

        board.gridpane.setOnMouseClicked(e -> {
            if (endgame == true) {
                setup();
                endgame = false;
            } else {
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
                            board.getScore(players);

                            Text whiteScore = new Text();
                            whiteScore.setText("WHITE: " + white.score);
                            whiteScore.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
                            whiteScore.setFill(Color.WHITE);
                            Text blackScore = new Text();
                            blackScore.setText("BLACK: " + black.score);
                            blackScore.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
                            blackScore.setFill(Color.WHITE);

                            textbox.getChildren().add(whiteScore);
                            textbox.getChildren().add(blackScore);

                            board.gridpane.setOpacity(0.5);
                            mainpane.getChildren().add(textbox);
                            endgame = true;
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
    }

    public static void main(String[] args) {
        launch();
    }
}
