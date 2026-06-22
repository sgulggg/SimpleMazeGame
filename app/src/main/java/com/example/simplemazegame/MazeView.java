package com.example.simplemazegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 迷宫游戏画布：Canvas 绘制、触控移动、圆形与矩形碰撞检测
 */
public class MazeView extends View {

    public interface GameListener {
        void onLevelComplete(long elapsedMs);

        void onMoveCountChanged(int count);
    }

    private static final float PLAYER_RADIUS_RATIO = 0.32f;
    private static final float EXIT_RADIUS_RATIO = 0.38f;
    /** 一次手势中角色位移超过此值，抬手时计为 1 次移动 */
    private static final float GESTURE_MOVE_THRESHOLD_RATIO = 0.08f;

    private MazeLevel level;
    private GameListener listener;

    private float cellSize;
    private float offsetX;
    private float offsetY;

    private float playerX;
    private float playerY;
    private float playerRadius;

    private float exitCenterX;
    private float exitCenterY;
    private float exitRadius;

    private float lastTouchX;
    private float lastTouchY;
    private boolean touching;
    private float gestureStartPlayerX;
    private float gestureStartPlayerY;

    private int moveCount;
    private long startTimeMs;
    private boolean completed;

    private float pulsePhase;

    private final Paint floorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint floorDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint wallHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint wallShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint playerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint playerGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint playerHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint exitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint exitGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint startMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF tempRect = new RectF();

    public MazeView(Context context) {
        super(context);
        init();
    }

    public MazeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MazeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setupPaints();
    }

    private void setupPaints() {
        floorPaint.setColor(Color.parseColor("#1A2332"));
        floorDotPaint.setColor(Color.parseColor("#243044"));
        floorDotPaint.setStyle(Paint.Style.FILL);

        wallPaint.setStyle(Paint.Style.FILL);
        wallHighlightPaint.setStyle(Paint.Style.STROKE);
        wallHighlightPaint.setStrokeWidth(2f);
        wallHighlightPaint.setColor(Color.parseColor("#6B8CFF"));

        wallShadowPaint.setStyle(Paint.Style.FILL);
        wallShadowPaint.setColor(Color.parseColor("#080C14"));

        playerGlowPaint.setStyle(Paint.Style.FILL);
        playerHighlightPaint.setStyle(Paint.Style.FILL);
        playerHighlightPaint.setColor(Color.parseColor("#E8FFFF"));

        exitGlowPaint.setStyle(Paint.Style.FILL);
        startMarkerPaint.setStyle(Paint.Style.FILL);
        startMarkerPaint.setColor(Color.parseColor("#334155"));
    }

    public void setGameListener(GameListener listener) {
        this.listener = listener;
    }

    public void loadLevel(MazeLevel level) {
        this.level = level;
        this.completed = false;
        this.moveCount = 0;
        this.touching = false;
        this.startTimeMs = System.currentTimeMillis();
        this.pulsePhase = 0f;
        requestLayout();
        applyLevelLayout(getWidth(), getHeight());
        post(() -> {
            applyLevelLayout(getWidth(), getHeight());
            invalidate();
        });
        invalidate();
    }

    public int getMoveCount() {
        return moveCount;
    }

    public long getElapsedMs() {
        return System.currentTimeMillis() - startTimeMs;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (level != null && width > 0 && height > 0) {
            float cellW = (float) width / level.cols;
            float cellH = (float) height / level.rows;
            cellSize = Math.min(cellW, cellH);
            int mazeWidth = Math.round(cellSize * level.cols);
            int mazeHeight = Math.round(cellSize * level.rows);
            setMeasuredDimension(mazeWidth, mazeHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        applyLevelLayout(w, h);
    }

    /** 根据当前关卡与视图尺寸，统一计算格子大小、偏移与角色/出口位置 */
    private void applyLevelLayout(int w, int h) {
        if (level == null || w <= 0 || h <= 0) {
            return;
        }
        cellSize = Math.min((float) w / level.cols, (float) h / level.rows);
        offsetX = (w - cellSize * level.cols) / 2f;
        offsetY = (h - cellSize * level.rows) / 2f;
        playerRadius = cellSize * PLAYER_RADIUS_RATIO;
        exitRadius = cellSize * EXIT_RADIUS_RATIO;
        resetPlayerPosition();
    }

    private void resetPlayerPosition() {
        if (level == null || cellSize <= 0) {
            return;
        }
        playerX = offsetX + (level.startCol + 0.5f) * cellSize;
        playerY = offsetY + (level.startRow + 0.5f) * cellSize;
        updateExitPosition();
    }

    private void updateExitPosition() {
        if (level == null) {
            return;
        }
        exitCenterX = offsetX + (level.exitCol + 0.5f) * cellSize;
        exitCenterY = offsetY + (level.exitRow + 0.5f) * cellSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (level == null || cellSize <= 0) {
            return;
        }

        pulsePhase += 0.06f;
        drawFloor(canvas);
        drawWalls(canvas);
        drawStartMarker(canvas);
        drawExit(canvas);
        drawPlayer(canvas);

        if (!completed) {
            postInvalidateOnAnimation();
        }
    }

    private void drawFloor(Canvas canvas) {
        tempRect.set(offsetX, offsetY, offsetX + level.cols * cellSize, offsetY + level.rows * cellSize);
        canvas.drawRoundRect(tempRect, 12f, 12f, floorPaint);

        float dotRadius = cellSize * 0.04f;
        for (int row = 0; row < level.rows; row++) {
            for (int col = 0; col < level.cols; col++) {
                if (!level.isWall(row, col)) {
                    float cx = offsetX + (col + 0.5f) * cellSize;
                    float cy = offsetY + (row + 0.5f) * cellSize;
                    if ((row + col) % 2 == 0) {
                        canvas.drawCircle(cx, cy, dotRadius, floorDotPaint);
                    }
                }
            }
        }
    }

    private void drawWalls(Canvas canvas) {
        float inset = cellSize * 0.06f;
        float corner = cellSize * 0.12f;

        for (int row = 0; row < level.rows; row++) {
            for (int col = 0; col < level.cols; col++) {
                if (!level.isWall(row, col)) {
                    continue;
                }
                float left = offsetX + col * cellSize + inset;
                float top = offsetY + row * cellSize + inset;
                float right = offsetX + (col + 1) * cellSize - inset;
                float bottom = offsetY + (row + 1) * cellSize - inset;

                tempRect.set(left + 2f, top + 4f, right + 2f, bottom + 4f);
                canvas.drawRoundRect(tempRect, corner, corner, wallShadowPaint);

                wallPaint.setShader(new LinearGradient(
                        left, top, right, bottom,
                        Color.parseColor("#3D5AFE"),
                        Color.parseColor("#1A237E"),
                        Shader.TileMode.CLAMP));
                tempRect.set(left, top, right, bottom);
                canvas.drawRoundRect(tempRect, corner, corner, wallPaint);
                wallPaint.setShader(null);

                canvas.drawRoundRect(tempRect, corner, corner, wallHighlightPaint);
            }
        }
    }

    private void drawStartMarker(Canvas canvas) {
        float sx = offsetX + (level.startCol + 0.5f) * cellSize;
        float sy = offsetY + (level.startRow + 0.5f) * cellSize;
        canvas.drawCircle(sx, sy, cellSize * 0.12f, startMarkerPaint);
    }

    private void drawExit(Canvas canvas) {
        float pulse = (float) (0.85f + 0.15f * Math.sin(pulsePhase));
        float glowRadius = exitRadius * (1.4f + 0.1f * (float) Math.sin(pulsePhase));

        exitGlowPaint.setShader(new RadialGradient(
                exitCenterX, exitCenterY, glowRadius,
                Color.parseColor("#80FFD54F"),
                Color.parseColor("#00FFD54F"),
                Shader.TileMode.CLAMP));
        canvas.drawCircle(exitCenterX, exitCenterY, glowRadius, exitGlowPaint);
        exitGlowPaint.setShader(null);

        exitPaint.setShader(new RadialGradient(
                exitCenterX - exitRadius * 0.2f, exitCenterY - exitRadius * 0.2f, exitRadius * pulse,
                Color.parseColor("#FFE082"),
                Color.parseColor("#FF8F00"),
                Shader.TileMode.CLAMP));
        canvas.drawCircle(exitCenterX, exitCenterY, exitRadius * pulse, exitPaint);
        exitPaint.setShader(null);
    }

    private void drawPlayer(Canvas canvas) {
        playerGlowPaint.setShader(new RadialGradient(
                playerX, playerY, playerRadius * 2.2f,
                Color.parseColor("#6600E5FF"),
                Color.parseColor("#0000E5FF"),
                Shader.TileMode.CLAMP));
        canvas.drawCircle(playerX, playerY, playerRadius * 2.2f, playerGlowPaint);
        playerGlowPaint.setShader(null);

        playerPaint.setShader(new RadialGradient(
                playerX - playerRadius * 0.25f, playerY - playerRadius * 0.25f, playerRadius,
                Color.parseColor("#18FFFF"),
                Color.parseColor("#0097A7"),
                Shader.TileMode.CLAMP));
        canvas.drawCircle(playerX, playerY, playerRadius, playerPaint);
        playerPaint.setShader(null);

        canvas.drawCircle(
                playerX - playerRadius * 0.28f,
                playerY - playerRadius * 0.28f,
                playerRadius * 0.22f,
                playerHighlightPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (completed || level == null) {
            return false;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                gestureStartPlayerX = playerX;
                gestureStartPlayerY = playerY;
                touching = true;
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;

            case MotionEvent.ACTION_MOVE:
                if (!touching) {
                    return true;
                }
                float dx = event.getX() - lastTouchX;
                float dy = event.getY() - lastTouchY;
                if (dx != 0f || dy != 0f) {
                    movePlayer(dx, dy);
                    lastTouchX = event.getX();
                    lastTouchY = event.getY();
                    invalidate();
                    checkWinCondition();
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (touching) {
                    registerGestureMoveIfNeeded();
                    checkWinCondition();
                }
                touching = false;
                return true;

            default:
                return super.onTouchEvent(event);
        }
    }

    /**
     * 一次完整触控（按下→抬手）且角色有明显位移时，计为 1 次移动
     */
    private void registerGestureMoveIfNeeded() {
        float threshold = cellSize * GESTURE_MOVE_THRESHOLD_RATIO;
        float moved = (float) Math.hypot(playerX - gestureStartPlayerX, playerY - gestureStartPlayerY);
        if (moved >= threshold) {
            moveCount++;
            if (listener != null) {
                listener.onMoveCountChanged(moveCount);
            }
        }
    }

    /**
     * 分离轴移动：先尝试 X 轴，再尝试 Y 轴，实现沿墙滑动
     */
    private void movePlayer(float dx, float dy) {
        float newX = playerX + dx;
        if (!collidesWithWall(newX, playerY)) {
            playerX = newX;
        }
        float newY = playerY + dy;
        if (!collidesWithWall(playerX, newY)) {
            playerY = newY;
        }
    }

    /**
     * 圆形角色与矩形墙块的碰撞检测
     */
    private boolean collidesWithWall(float cx, float cy) {
        int minCol = Math.max(0, (int) Math.floor((cx - playerRadius - offsetX) / cellSize));
        int maxCol = Math.min(level.cols - 1, (int) Math.floor((cx + playerRadius - offsetX) / cellSize));
        int minRow = Math.max(0, (int) Math.floor((cy - playerRadius - offsetY) / cellSize));
        int maxRow = Math.min(level.rows - 1, (int) Math.floor((cy + playerRadius - offsetY) / cellSize));

        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                if (!level.isWall(row, col)) {
                    continue;
                }
                float wallLeft = offsetX + col * cellSize;
                float wallTop = offsetY + row * cellSize;
                float wallRight = wallLeft + cellSize;
                float wallBottom = wallTop + cellSize;

                if (circleIntersectsRect(cx, cy, playerRadius, wallLeft, wallTop, wallRight, wallBottom)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean circleIntersectsRect(float cx, float cy, float radius,
                                                float left, float top, float right, float bottom) {
        float closestX = clamp(cx, left, right);
        float closestY = clamp(cy, top, bottom);
        float dx = cx - closestX;
        float dy = cy - closestY;
        return dx * dx + dy * dy < radius * radius;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private void checkWinCondition() {
        float dist = (float) Math.hypot(playerX - exitCenterX, playerY - exitCenterY);
        if (dist <= exitRadius + playerRadius * 0.5f) {
            completed = true;
            if (listener != null) {
                listener.onLevelComplete(getElapsedMs());
            }
        }
    }
}
