package com.example.simplemazegame;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private MaterialButton backHomeButton;
    private MaterialButton refreshButton;

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
        int startLevel = getIntent().getIntExtra("start_level", -1);
        if (startLevel >= 0 && startLevel < levels.length) {
            loadLevel(startLevel);
        } else {
            loadLevel(0);
        }
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
        backHomeButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
    }

    private void setupMazeView() {
        mazeView.setGameListener(new MazeView.GameListener() {
            @Override
            public void onLevelComplete(long elapsedMs) {
                timerRunning = false;
                saveScore(elapsedMs, mazeView.getMoveCount(), currentLevelIndex);
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

        refreshButton.setOnClickListener(v -> {
            MazeLevel cur = levels[currentLevelIndex];
            if ("test".equals(cur.name)) {
                MazeLevel newL = MazeLevel.createPrimLevel(cur.name, cur.rows, cur.cols);
                levels[currentLevelIndex] = newL;
                loadLevel(currentLevelIndex);
            } else {
                Toast.makeText(MainActivity.this, "当前关不可刷新", Toast.LENGTH_SHORT).show();
            }
        });

        backHomeButton.setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, StartActivity.class));
            finish();
        });
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

    private void saveScore(long timeMs, int moves, int levelIndex) {
        try {
            List<Score> scores = loadScores();
            scores.add(new Score(timeMs, moves, levelIndex, System.currentTimeMillis()));
            // sort by time then moves
            Collections.sort(scores, new Comparator<Score>() {
                @Override
                public int compare(Score a, Score b) {
                    if (a.timeMs != b.timeMs) return Long.compare(a.timeMs, b.timeMs);
                    return Integer.compare(a.moves, b.moves);
                }
            });
            // keep top 20
            if (scores.size() > 20) scores = scores.subList(0, 20);

            JSONArray arr = new JSONArray();
            for (Score s : scores) {
                arr.put(s.toJson());
            }
            SharedPreferences prefs = getSharedPreferences("leaderboard_prefs", MODE_PRIVATE);
            prefs.edit().putString("leaderboard_key", arr.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Score> loadScores() {
        List<Score> list = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("leaderboard_prefs", MODE_PRIVATE);
        String raw = prefs.getString("leaderboard_key", null);
        if (raw == null) return list;
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(Score.fromJson(o));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void showLeaderboardDialog() {
        List<Score> scores = loadScores();
        if (scores.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.leaderboard_title)
                    .setMessage(R.string.leaderboard_empty)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (Score s : scores) {
            String timeStr = formatTime(s.timeMs);
            sb.append(getString(R.string.leaderboard_entry_format, rank, s.levelIndex + 1, timeStr, s.moves));
            if (rank < scores.size()) sb.append('\n');
            rank++;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.leaderboard_title)
                .setMessage(sb.toString())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public static class Score {
        long timeMs;
        int moves;
        int levelIndex;
        long createdAt;

        public Score(long timeMs, int moves, int levelIndex, long createdAt) {
            this.timeMs = timeMs;
            this.moves = moves;
            this.levelIndex = levelIndex;
            this.createdAt = createdAt;
        }

        JSONObject toJson() throws JSONException {
            JSONObject o = new JSONObject();
            o.put("timeMs", timeMs);
            o.put("moves", moves);
            o.put("levelIndex", levelIndex);
            o.put("createdAt", createdAt);
            return o;
        }

        public static Score fromJson(JSONObject o) throws JSONException {
            return new Score(o.getLong("timeMs"), o.getInt("moves"), o.getInt("levelIndex"), o.optLong("createdAt", 0));
        }
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
