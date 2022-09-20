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
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Code borrowed from In Class activities
    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;
    private static final int BOMB_COUNT = 4;
    private HashSet<Pair> bombs;
    private ArrayList<TextView> cell_tvs;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cell_tvs = new ArrayList<TextView>();

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
                if(!bombs.contains(currLoc)) {
                    tv.setText(String.valueOf(i)+String.valueOf(j));
                }

                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GRAY);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);
                grid.addView(tv, lp);
                cell_tvs.add(tv);
            }
        }

    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
        Pair<Integer, Integer> currLoc = new Pair(i,j);
        if(bombs.contains(currLoc)) {
            // It should actually show all bombs and end

            tv.setText(new String("\uD83D\uDCA3"));
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