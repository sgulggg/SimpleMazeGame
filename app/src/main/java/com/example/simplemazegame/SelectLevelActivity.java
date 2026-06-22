package com.example.simplemazegame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SelectLevelActivity extends AppCompatActivity {

    private RecyclerView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);

        grid = findViewById(R.id.selectGrid);
        MaterialButton back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> {
            startActivity(new android.content.Intent(SelectLevelActivity.this, StartActivity.class));
            finish();
        });

        MazeLevel[] levels = MazeLevel.getLevels();
        grid.setLayoutManager(new GridLayoutManager(this, 2));
        grid.setAdapter(new LevelAdapter(levels));
    }

    private Bitmap generateThumbnail(MazeLevel level, int w, int h) {
        if (w == 0) w = 320; if (h == 0) h = 240;
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.parseColor("#0B1220"));

        Paint wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wallPaint.setColor(Color.parseColor("#3D5AFE"));

        Paint pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setColor(Color.parseColor("#101826"));

        int rows = level.rows;
        int cols = level.cols;
        float cellW = (float) w / cols;
        float cellH = (float) h / rows;

        for (int r = 0; r < rows; r++) {
            for (int col = 0; col < cols; col++) {
                float left = col * cellW;
                float top = r * cellH;
                float right = left + cellW;
                float bottom = top + cellH;
                if (level.isWall(r, col)) {
                    c.drawRect(left, top, right, bottom, wallPaint);
                } else {
                    c.drawRect(left, top, right, bottom, pathPaint);
                }
            }
        }

        Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sPaint.setColor(Color.parseColor("#334155"));
        float sx = (level.startCol + 0.5f) * cellW;
        float sy = (level.startRow + 0.5f) * cellH;
        c.drawCircle(sx, sy, Math.min(cellW, cellH) * 0.2f, sPaint);

        Paint ePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ePaint.setColor(Color.parseColor("#FFD54F"));
        float ex = (level.exitCol + 0.5f) * cellW;
        float ey = (level.exitRow + 0.5f) * cellH;
        c.drawCircle(ex, ey, Math.min(cellW, cellH) * 0.3f, ePaint);

        return bmp;
    }

    private class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.VH> {
        private final MazeLevel[] levels;

        LevelAdapter(MazeLevel[] levels) {
            this.levels = levels;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.select_level_item, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            MazeLevel level = levels[position];
            holder.name.setText(level.name);

            int w = holder.thumb.getWidth();
            int h = holder.thumb.getHeight();
            Bitmap bmp = generateThumbnail(level, w, h);
            holder.thumb.setImageBitmap(bmp);

            // show regenerate only for 'test' level
            if ("test".equals(level.name)) {
                holder.regenBtn.setVisibility(View.VISIBLE);
                holder.regenBtn.setOnClickListener(v -> {
                    MazeLevel newLevel = MazeLevel.createPrimLevel(level.name, level.rows, level.cols);
                    levels[position] = newLevel;
                    Bitmap nb = generateThumbnail(newLevel, w, h);
                    holder.thumb.setImageBitmap(nb);
                });
            } else {
                holder.regenBtn.setVisibility(View.GONE);
                holder.regenBtn.setOnClickListener(null);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(SelectLevelActivity.this, MainActivity.class);
                i.putExtra("start_level", position);
                startActivity(i);
                finish();
            });
        }

        @Override
        public int getItemCount() {
            return levels.length;
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView thumb;
            TextView name;
            Button regenBtn;

            VH(View itemView) {
                super(itemView);
                thumb = itemView.findViewById(R.id.thumbImage);
                name = itemView.findViewById(R.id.levelName);
                regenBtn = itemView.findViewById(R.id.regenerateButton);
            }
        }
    }
}
