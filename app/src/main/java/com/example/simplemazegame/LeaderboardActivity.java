package com.example.simplemazegame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView list;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        list = findViewById(R.id.leaderboardList);
        emptyView = findViewById(R.id.leaderboardEmpty);
        MaterialButton back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> {
            startActivity(new android.content.Intent(LeaderboardActivity.this, StartActivity.class));
            finish();
        });

        list.setLayoutManager(new LinearLayoutManager(this));

        List<MainActivity.Score> scores = loadScores();
        if (scores.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            list.setAdapter(new Adapter(scores));
        }
    }

    private List<MainActivity.Score> loadScores() {
        List<MainActivity.Score> list = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("leaderboard_prefs", MODE_PRIVATE);
        String raw = prefs.getString("leaderboard_key", null);
        if (raw == null) return list;
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(MainActivity.Score.fromJson(o));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String formatTime(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        long tenths = (millis / 100) % 10;
        return String.format(Locale.getDefault(), "%02d:%02d.%d", minutes, seconds, tenths);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.VH> {
        private final List<MainActivity.Score> items;

        Adapter(List<MainActivity.Score> items) {
            this.items = items;
        }

        @Override
        public VH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View v = getLayoutInflater().inflate(R.layout.leaderboard_item, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            MainActivity.Score s = items.get(position);
            holder.rank.setText(String.valueOf(position + 1));
            holder.detail.setText(getString(R.string.leaderboard_entry_format, position + 1, s.levelIndex + 1, formatTime(s.timeMs), s.moves));
            holder.sub.setText("");
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView rank, detail, sub;

            VH(View itemView) {
                super(itemView);
                rank = itemView.findViewById(R.id.rankText);
                detail = itemView.findViewById(R.id.detailText);
                sub = itemView.findViewById(R.id.subText);
            }
        }
    }
}
