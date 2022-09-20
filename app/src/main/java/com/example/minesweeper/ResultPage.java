package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultPage extends AppCompatActivity {
    public static final String GET_TIME = "time";
    public static final String WIN = "win";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_page);
        Intent intent = getIntent();
        int timeLeft = intent.getIntExtra(GET_TIME, -1);
        boolean win = intent.getBooleanExtra(WIN, false);
        TextView resultView = (TextView) findViewById(R.id.resultMessage);
        if(win) {
            String winMessage = "Used " + String.valueOf(timeLeft) + " second(s).\nYou won.\nGood job!\n";
            resultView.setText(winMessage);
        }
        else {
            String lostMessage = "Used " + String.valueOf(timeLeft) + " second(s).\nYou suck.\nYou Lost\n";
            resultView.setText(lostMessage);
        }

    }
}
