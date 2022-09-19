package com.example.minesweeper;

public class Cell {
    private boolean isFlagged;
    private boolean visited;
    // if Bomb, numBombs = -1
    private int numBombs;

    public Cell(int numBombs) {
        this.numBombs = numBombs;
        this.visited = false;
        this.isFlagged = false;
    }

    public int getNumBombs() {return numBombs;}
    public boolean isVisted() {return visited;}
    public boolean isFlagged() {return isFlagged;}
    public void visit() {visited = true;}
    public void flag() {isFlagged = true;}
}
