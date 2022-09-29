package com.four.othello;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class Piece {
    boolean empty = true;

    StackPane square = new StackPane();
    Player occupiedBy;

    int score;

    Piece() {
        square.setBackground(new Background(new BackgroundFill(Color.MEDIUMSEAGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        square.setPrefSize(Game.SQUARE_SIZE, Game.SQUARE_SIZE);
        square.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    Piece(Piece originalPiece) {
        this.empty = originalPiece.empty;
        this.square = new StackPane();
        this.square.setBackground(new Background(new BackgroundFill(Color.MEDIUMSEAGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        this.square.setPrefSize(Game.SQUARE_SIZE, Game.SQUARE_SIZE);
        this.square.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        this.occupiedBy = originalPiece.occupiedBy;
        this.score = originalPiece.score;
    }
}
