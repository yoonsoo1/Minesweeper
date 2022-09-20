package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Code borrowed from In Class activities
    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;
    private static final int BOMB_COUNT = 4;
    private int flagCounter = 4;
    private int time = 0;
    private boolean isPick = true;
    private boolean gameOver = false;
    private boolean win = false;
    private int countRevealed = 0; // if this num is == row*col - bomb, end game
    private HashSet<Pair> bombs;
    private List<List<TextView>> cell_tvs;
    private List<List<Integer>> cells;
    private TextView flagCount;
    private boolean visited[][] = new boolean[ROW_COUNT][COLUMN_COUNT];


    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView option = findViewById(R.id.flagOpt);
        option.setOnClickListener(this::onClickFlag);

        flagCount = findViewById(R.id.flagCount);
        flagCount.setText(String.valueOf(flagCounter));

        cell_tvs = new ArrayList<>(ROW_COUNT);
        cells = new ArrayList<>(ROW_COUNT);
        for(int i = 0; i < ROW_COUNT; i++) {
            cell_tvs.add(new ArrayList<TextView>(COLUMN_COUNT));
            cells.add(new ArrayList<Integer>(COLUMN_COUNT));
        }

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        LayoutInflater li = LayoutInflater.from(this);

        // Randomly select points (row,col) to set bombs at
        bombs = new HashSet<>();
        Random rand = new Random();
        for(int i = 0; i < BOMB_COUNT; i++) {
            Integer randRow = rand.nextInt(ROW_COUNT);
            Integer randCol = rand.nextInt(COLUMN_COUNT);
            Pair<Integer,Integer> bombLoc = new Pair(randRow, randCol);
            if(!bombs.contains(bombLoc)) {
                bombs.add(bombLoc);
            }
            else {
                i--;
            }
        }

        for (int i = 0; i<ROW_COUNT; i++) {
            for (int j=0; j<COLUMN_COUNT; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                Pair<Integer, Integer> currLoc = new Pair(i, j);
                if(bombs.contains(currLoc)) {
                    // A bomb is represented as -1
                    cells.get(i).add(-1);
                }
                else {
                    // If not bomb location, check the 3x3 kernel around currLoc
                    int lowerX = Math.max((i-1), 0);
                    int upperX = Math.min((i+1), ROW_COUNT-1);
                    int lowerY = Math.max((j-1), 0);
                    int upperY = Math.min((j+1), COLUMN_COUNT-1);
                    int counter = 0;
                    for(int x = lowerX; x <= upperX; x++) {
                        for(int y = lowerY; y <= upperY; y++) {
                            Pair<Integer, Integer> checker = new Pair(x,y);
                            if(bombs.contains(checker)) {
                                counter++;
                            }
                        }
                    }
                    cells.get(i).add(counter);
                    tv.setText(String.valueOf(counter));
                }

                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GRAY);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);
                grid.addView(tv, lp);
                cell_tvs.get(i).add(tv);
            }
        }
    }

    private void onClickFlag(View view) {
        TextView currState = (TextView) view;
        String currLogo = (String) currState.getText();
        if(currLogo == getString(R.string.pick)) {
            currState.setText(R.string.flag);
            isPick = false;
        }
        else {
            currState.setText(R.string.pick);
            isPick = true;
        }
    }

    private int findIndexOfCellTextView(TextView tv) {
        for(int i = 0; i < ROW_COUNT; i++) {
            for(int j = 0; j < COLUMN_COUNT; j++) {
                if(cell_tvs.get(i).get(j) == tv) {
                    return (10 * (i + 1) + j);
                }
            }
        }
        return -1; // handle exception if time
    }

    public void sendToNext(View view, int time, boolean win) {
        Intent intent = new Intent(this, ResultPage.class);
        intent.putExtra("time", time);
        intent.putExtra("win", win);
        startActivity(intent);
    }

    public void onClickTV(View view){
        if(gameOver) {
            sendToNext(view, time, win);
        }
        else {
            TextView tv = (TextView) view;
            int n = findIndexOfCellTextView(tv);
            int i = n/10 - 1;
            int j = n%10;
            if(isPick) {
                int currValue = cells.get(i).get(j);
                if(currValue == -1) {
                    // Show all bombs
                    Iterator<Pair> it = bombs.iterator();
                    while(it.hasNext()) {
                        Pair<Integer, Integer> coord = it.next();
                        int x = coord.first;
                        int y = coord.second;
                        TextView bomb = cell_tvs.get(x).get(y);
                        bomb.setText(R.string.bomb);
                    }
                    gameOver = true;
                }
                else if(currValue == 0) {
                    // If it's 0, expand with BFS until non-zero
                    Queue<Pair> q = new LinkedList<>();
                    visited[i][j] = true;
                    countRevealed++;
                    Pair<Integer,Integer> p = new Pair<>(i, j);
                    q.add(p);
                    tv.setTextColor(Color.LTGRAY);
                    tv.setBackgroundColor(Color.LTGRAY);
                    TextView currTv;
                    while(q.size() > 0) {
                        p = q.poll();
                        i = p.first;
                        j = p.second;
                        int lowerX = Math.max((i-1), 0);
                        int upperX = Math.min((i+1), ROW_COUNT-1);
                        int lowerY = Math.max((j-1), 0);
                        int upperY = Math.min((j+1), COLUMN_COUNT-1);
                        for(int x = lowerX; x <= upperX; x++) {
                            for(int y = lowerY; y <= upperY; y++) {
                                currValue = cells.get(x).get(y);
                                if(!visited[x][y] && currValue == 0) {
                                    visited[x][y] = true;
                                    q.add(new Pair(x, y));
                                    currTv = cell_tvs.get(x).get(y);
                                    currTv.setTextColor(Color.LTGRAY);
                                    currTv.setBackgroundColor(Color.LTGRAY);
                                    countRevealed++;
                                }
                                else if(!visited[x][y] && currValue > 0) {
                                    visited[x][y] = true;
                                    currTv = cell_tvs.get(x).get(y);
                                    currTv.setTextColor(Color.BLACK);
                                    currTv.setBackgroundColor(Color.LTGRAY);
                                    countRevealed++;
                                }
                            }
                        }
                    }
                }
                else {
                    tv.setTextColor(Color.BLACK);
                    tv.setBackgroundColor(Color.LTGRAY);
                    visited[i][j] = true;
                    countRevealed++;
                }
            }
            else {
                String currState = (String) tv.getText();
                if(currState == getString(R.string.flag)) {
                    flagCounter++;
                    int replaceVal = cells.get(i).get(j);
                    tv.setText(String.valueOf(replaceVal));
                    countRevealed--;
                }
                else if(!visited[i][j] && flagCounter > 0) {
                    flagCounter--;
                    tv.setText(R.string.flag);
                    countRevealed++;
                }
                flagCount.setText(String.valueOf(flagCounter));
            }
            if(countRevealed == ROW_COUNT*COLUMN_COUNT-BOMB_COUNT) {
                win = true;
                gameOver = true;
            }
        }
    }
}