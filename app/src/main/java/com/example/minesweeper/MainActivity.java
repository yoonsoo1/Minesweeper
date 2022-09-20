package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.gridlayout.widget.GridLayout;

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
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Code borrowed from In Class activities
    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;
    private static final int BOMB_COUNT = 4;
    private HashSet<Pair> bombs;
    private List<List<TextView>> cell_tvs;
    private List<List<Integer>> cells;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    // Uninitialized cells are -2
                    cells.get(i).add(-2);
                }

//                tv.setTextColor(Color.GRAY);
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

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/10 - 1;
        int j = n%10;
        Pair<Integer, Integer> currLoc = new Pair(i,j);
        if(bombs.contains(currLoc)) {
            // It should actually show all bombs and end
            Iterator<Pair> it = bombs.iterator();
            while(it.hasNext()) {
                Pair<Integer, Integer> coord = it.next();
                int x = coord.first;
                int y = coord.second;
                TextView bomb = cell_tvs.get(x).get(y);
                bomb.setText(new String("\uD83D\uDCA3"));
            }
        }

        if (tv.getCurrentTextColor() == Color.GREEN) {
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.parseColor("lime"));
        }else {
            tv.setTextColor(Color.GREEN);
            tv.setBackgroundColor(Color.LTGRAY);
        }
    }

}