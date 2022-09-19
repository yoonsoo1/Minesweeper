package com.example.minesweeper;

public class GameSetup {
    private Grid gameGrid;
    public GameSetup(int row, int col) {
        gameGrid = new Grid(row, col);
    }

    public Grid getGameGrid() {return gameGrid;}
}
