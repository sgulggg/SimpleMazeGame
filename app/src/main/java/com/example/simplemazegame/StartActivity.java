package com.example.simplemazegame;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        MaterialButton startBtn = findViewById(R.id.startButton);
        MaterialButton endlessBtn = findViewById(R.id.endlessButton);
        MaterialButton selectBtn = findViewById(R.id.selectLevelButton);
        MaterialButton leaderboardBtn = findViewById(R.id.leaderboardButtonStart);
        MaterialButton exitBtn = findViewById(R.id.exitButtonStart);

        startBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("start_level", 0);
            i.putExtra("endless_mode", false);
            startActivity(i);
            finish();
        });

        endlessBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("endless_mode", true);
            startActivity(i);
            finish();
        });

        selectBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, SelectLevelActivity.class);
            startActivity(i);
            finish();
        });

        leaderboardBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, LeaderboardActivity.class);
            startActivity(i);
            finish();
        });

        exitBtn.setOnClickListener(v -> {
            finishAffinity();
        });
    }
}
