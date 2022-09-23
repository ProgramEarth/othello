package com.four.othello;

public class Direction {
    int index;
    int row_change;
    int col_change;

    Direction(int index, int row_change, int col_change) {
        this.index = index;
        this.row_change = row_change;
        this.col_change = col_change;
    }
}
