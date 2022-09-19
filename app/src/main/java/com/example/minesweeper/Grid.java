package com.example.minesweeper;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    private List<Cell> cells;
    private int row;
    private int col;

    public Grid(int row, int col) {
        this.row = row;
        this.col = col;
        cells = new ArrayList<>();
        for(int i = 0; i < row*col; i++) {
            cells.add(new Cell(0));
        }
    }

    public List<Cell> getCells() {return cells;}
}
