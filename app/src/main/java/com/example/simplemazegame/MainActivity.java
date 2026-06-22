package com.example.simplemazegame;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MazeView mazeView;
    private TextView levelTitleText;
    private TextView levelIndexText;
    private TextView timerText;
    private TextView moveCountText;
    private MaterialButton prevButton;
    private MaterialButton nextButton;
    private MaterialButton restartButton;

    private MazeLevel[] levels;
    private int currentLevelIndex;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private boolean timerRunning;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (timerRunning && mazeView != null) {
                timerText.setText(formatTime(mazeView.getElapsedMs()));
                timerHandler.postDelayed(this, 200);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        levels = MazeLevel.getLevels();
        bindViews();
        setupMazeView();
        setupButtons();
        loadLevel(0);
    }

    private void bindViews() {
        mazeView = findViewById(R.id.mazeView);
        levelTitleText = findViewById(R.id.levelTitleText);
        levelIndexText = findViewById(R.id.levelIndexText);
        timerText = findViewById(R.id.timerText);
        moveCountText = findViewById(R.id.moveCountText);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        restartButton = findViewById(R.id.restartButton);
    }

    private void setupMazeView() {
        mazeView.setGameListener(new MazeView.GameListener() {
            @Override
            public void onLevelComplete(long elapsedMs) {
                timerRunning = false;
                showWinDialog(elapsedMs);
            }

            @Override
            public void onMoveCountChanged(int count) {
                moveCountText.setText(getString(R.string.move_count_format, count));
            }
        });
    }

    private void setupButtons() {
        prevButton.setOnClickListener(v -> {
            if (currentLevelIndex > 0) {
                loadLevel(currentLevelIndex - 1);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentLevelIndex < levels.length - 1) {
                loadLevel(currentLevelIndex + 1);
            }
        });

        restartButton.setOnClickListener(v -> loadLevel(currentLevelIndex));
    }

    private void loadLevel(int index) {
        currentLevelIndex = index;
        MazeLevel level = levels[index];

        mazeView.loadLevel(level);
        levelTitleText.setText(level.name);
        levelIndexText.setText(getString(R.string.level_index_format, index + 1, levels.length));
        moveCountText.setText(getString(R.string.move_count_format, 0));
        timerText.setText(formatTime(0));

        prevButton.setEnabled(index > 0);
        nextButton.setEnabled(index < levels.length - 1);

        startTimer();
    }

    private void startTimer() {
        timerRunning = true;
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.post(timerRunnable);
    }

    private void showWinDialog(long elapsedMs) {
        String timeStr = formatTime(elapsedMs);
        String message = getString(R.string.win_message, timeStr, mazeView.getMoveCount());

        new AlertDialog.Builder(this)
                .setTitle(R.string.win_title)
                .setMessage(message)
                .setPositiveButton(R.string.next_level, (dialog, which) -> {
                    if (currentLevelIndex < levels.length - 1) {
                        loadLevel(currentLevelIndex + 1);
                    } else {
                        showAllCompleteDialog();
                    }
                })
                .setNegativeButton(R.string.replay, (dialog, which) -> loadLevel(currentLevelIndex))
                .setCancelable(false)
                .show();
    }

    private void showAllCompleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.all_complete_title)
                .setMessage(R.string.all_complete_message)
                .setPositiveButton(R.string.replay_from_start, (dialog, which) -> loadLevel(0))
                .setNegativeButton(R.string.stay_here, null)
                .show();
    }

    private static String formatTime(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        long tenths = (millis / 100) % 10;
        return String.format(Locale.getDefault(), "%02d:%02d.%d", minutes, seconds, tenths);
    }

    @Override
    protected void onDestroy() {
        timerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
        super.onDestroy();
    }
}
