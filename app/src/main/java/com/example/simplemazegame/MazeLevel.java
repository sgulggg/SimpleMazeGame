package com.example.simplemazegame;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * 迷宫关卡数据：0=通道，1=墙壁
 */
public class MazeLevel {

    public static final int WALL = 1;
    public static final int PATH = 0;

    public final int[][] grid;
    public final int rows;
    public final int cols;
    public final int startRow;
    public final int startCol;
    public final int exitRow;
    public final int exitCol;
    public final String name;

    public MazeLevel(String name, int[][] grid, int startRow, int startCol, int exitRow, int exitCol) {
        this.name = name;
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
        this.startRow = startRow;
        this.startCol = startCol;
        this.exitRow = exitRow;
        this.exitCol = exitCol;
    }

    public boolean isWall(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return true;
        }
        return grid[row][col] == WALL;
    }

    public static MazeLevel[] getLevels() {
        return new MazeLevel[]{
                createLevel1(),
                createLevel2(),
                createLevel3()
        };
    }

    private static MazeLevel createLevel1() {
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        return new MazeLevel("初探迷域", grid, 1, 1, 9, 9);
    }

    private static MazeLevel createLevel2() {
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                {1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        return new MazeLevel("曲折回廊", grid, 1, 1, 11, 11);
    }

    private static MazeLevel createLevel3() {
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                {1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        return new MazeLevel("深渊终局", grid, 1, 1, 13, 13);
    }

    /**
     * 生成随机迷宫，rows/cols 会被调整为奇数以便生成网格路径结构。
     * 起点/出口默认放在 (1,1) 和 (rows-2, cols-2)，保证至少有一条可达路径。
     */
    public static MazeLevel createRandomLevel(String name, int rows, int cols, long seed) {
        if (rows < 3) rows = 3;
        if (cols < 3) cols = 3;
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        int[][] grid = new int[rows][cols];
        // fill walls
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = WALL;
            }
        }

        // carved cells are at odd coordinates; use iterative DFS (recursive backtracker)
        boolean[][] visited = new boolean[rows][cols];
        Deque<int[]> stack = new ArrayDeque<>();
        Random rand = new Random(seed);

        int sr = 1, sc = 1;
        visited[sr][sc] = true;
        grid[sr][sc] = PATH;
        stack.push(new int[]{sr, sc});

        int[][] dirs = new int[][]{{0, -2}, {0, 2}, {-2, 0}, {2, 0}};

        while (!stack.isEmpty()) {
            int[] cur = stack.peek();
            int r = cur[0], c = cur[1];

            List<int[]> nbrs = new ArrayList<>();
            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (nr > 0 && nr < rows - 1 && nc > 0 && nc < cols - 1 && !visited[nr][nc]) {
                    nbrs.add(new int[]{nr, nc});
                }
            }

            if (!nbrs.isEmpty()) {
                int[] next = nbrs.get(rand.nextInt(nbrs.size()));
                int nr = next[0], nc = next[1];
                visited[nr][nc] = true;
                grid[(r + nr) / 2][(c + nc) / 2] = PATH; // knock down wall between
                grid[nr][nc] = PATH;
                stack.push(new int[]{nr, nc});
            } else {
                stack.pop();
            }
        }

        int er = rows - 2, ec = cols - 2;
        // ensure start/exit are paths
        grid[sr][sc] = PATH;
        grid[er][ec] = PATH;

        return new MazeLevel(name, grid, sr, sc, er, ec);
    }

    public static MazeLevel createRandomLevel(String name, int rows, int cols) {
        return createRandomLevel(name, rows, cols, System.currentTimeMillis());
    }

    /**
     * 生成一个以主路径为骨架并带有多个死胡同的迷宫：
     * - 先用迭代回溯（DFS）从起点刻出一条到出口的主路径（停止条件：到达出口）
     * - 然后在主路径的各节点上随机生长若干分支（仅在未访问且不会与其它通路相连的格子上刻通道）
     * 结果是起点到出口之间仍只有唯一可达路径，但沿主路径有许多死路分支，符合需求。
     */
    public static MazeLevel createBranchyRandomLevel(String name, int rows, int cols, long seed) {
        if (rows < 3) rows = 3;
        if (cols < 3) cols = 3;
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        int[][] grid = new int[rows][cols];
        for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) grid[r][c] = WALL;

        boolean[][] visited = new boolean[rows][cols];
        Random rand = new Random(seed);

        int sr = 1, sc = 1;
        int er = rows - 2, ec = cols - 2;

        int[][] dirs = new int[][]{{0, -2}, {0, 2}, {-2, 0}, {2, 0}};

        // carve main path using DFS until reach exit
        Deque<int[]> stack = new ArrayDeque<>();
        visited[sr][sc] = true;
        grid[sr][sc] = PATH;
        stack.push(new int[]{sr, sc});

        boolean reachedExit = false;
        while (!stack.isEmpty() && !reachedExit) {
            int[] cur = stack.peek();
            int r = cur[0], c = cur[1];

            List<int[]> nbrs = new ArrayList<>();
            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr > 0 && nr < rows - 1 && nc > 0 && nc < cols - 1 && !visited[nr][nc]) {
                    nbrs.add(new int[]{nr, nc});
                }
            }

            if (!nbrs.isEmpty()) {
                // bias neighbors towards exit: sort by manhattan distance
                Collections.shuffle(nbrs, rand);
                nbrs.sort((a, b) -> {
                    int da = Math.abs(a[0] - er) + Math.abs(a[1] - ec);
                    int db = Math.abs(b[0] - er) + Math.abs(b[1] - ec);
                    return Integer.compare(da, db);
                });
                int[] next = nbrs.get(0);
                int nr = next[0], nc = next[1];
                visited[nr][nc] = true;
                grid[(r + nr) / 2][(c + nc) / 2] = PATH;
                grid[nr][nc] = PATH;
                stack.push(new int[]{nr, nc});
                if (nr == er && nc == ec) reachedExit = true;
            } else {
                stack.pop();
            }
        }

        // If main DFS didn't reach exit (very unlikely), carve a direct corridor
        if (!visited[er][ec]) {
            int r = er, c = ec;
            while (r != sr || c != sc) {
                grid[r][c] = PATH;
                visited[r][c] = true;
                if (r > sr) r -= 2; else if (r < sr) r += 2;
                if (c > sc) c -= 2; else if (c < sc) c += 2;
                grid[(r + sr) / 2][(c + sc) / 2] = PATH;
            }
        }

        // collect main path cells
        List<int[]> mainPath = new ArrayList<>();
        for (int r = 1; r < rows; r += 2) {
            for (int c = 1; c < cols; c += 2) {
                if (grid[r][c] == PATH && visited[r][c]) mainPath.add(new int[]{r, c});
            }
        }

        // grow branches from main path cells
        double branchProb = 0.7; // probability to start a branch at a main path cell
        int maxBranchLength = Math.max(rows, cols); // cap

        for (int[] cell : mainPath) {
            if (rand.nextDouble() > branchProb) continue;
            int r = cell[0], c = cell[1];
            // try available directions
            List<int[]> candidates = new ArrayList<>();
            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr > 0 && nr < rows - 1 && nc > 0 && nc < cols - 1 && !visited[nr][nc]) {
                    candidates.add(new int[]{nr, nc});
                }
            }
            Collections.shuffle(candidates, rand);

            for (int[] start : candidates) {
                // ensure carving start won't touch other visited cells (avoid cycles)
                if (adjacentVisitedCount(start[0], start[1], visited) > 1) continue;
                int pr = r, pc = c;
                int nr = start[0], nc = start[1];
                // carve one step
                grid[(pr + nr) / 2][(pc + nc) / 2] = PATH;
                grid[nr][nc] = PATH;
                visited[nr][nc] = true;

                // now grow further randomly
                int length = 1 + rand.nextInt(Math.max(1, maxBranchLength / 4));
                int cr = nr, cc = nc;
                for (int L = 0; L < length; L++) {
                    List<int[]> nexts = new ArrayList<>();
                    for (int[] d : dirs) {
                        int ar = cr + d[0], ac = cc + d[1];
                        if (ar > 0 && ar < rows - 1 && ac > 0 && ac < cols - 1 && !visited[ar][ac]) {
                            // only allow if carving there won't connect to existing visited cells
                            if (adjacentVisitedCount(ar, ac, visited) == 0) {
                                nexts.add(new int[]{ar, ac});
                            }
                        }
                    }
                    if (nexts.isEmpty()) break;
                    int[] pick = nexts.get(rand.nextInt(nexts.size()));
                    int ar = pick[0], ac = pick[1];
                    grid[(cr + ar) / 2][(cc + ac) / 2] = PATH;
                    grid[ar][ac] = PATH;
                    visited[ar][ac] = true;
                    cr = ar; cc = ac;
                }
            }
        }

        // ensure start/exit are open
        grid[sr][sc] = PATH;
        grid[er][ec] = PATH;

        return new MazeLevel(name, grid, sr, sc, er, ec);
    }

    private static int adjacentVisitedCount(int r, int c, boolean[][] visited) {
        int cnt = 0;
        int rows = visited.length;
        int cols = visited[0].length;
        int[][] adj = new int[][]{{0,1},{0,-1},{1,0},{-1,0}};
        for (int[] d : adj) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >=0 && nr < rows && nc >=0 && nc < cols && visited[nr][nc]) cnt++;
        }
        return cnt;
    }

    public static MazeLevel createBranchyRandomLevel(String name, int rows, int cols) {
        return createBranchyRandomLevel(name, rows, cols, System.currentTimeMillis());
    }

    /**
     * 使用随机 Prim 算法生成迷宫，生成均匀分布的走廊，避免大面积未雕刻的实心区域。
     * 结果是一个无环的树（唯一路径连接任意两点），起点到终点有唯一解但分支较多，视觉更均衡。
     */
    public static MazeLevel createPrimLevel(String name, int rows, int cols, long seed) {
        if (rows < 3) rows = 3;
        if (cols < 3) cols = 3;
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        int[][] grid = new int[rows][cols];
        for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) grid[r][c] = WALL;

        Random rand = new Random(seed);
        List<int[]> frontier = new ArrayList<>();
        boolean[][] inFrontier = new boolean[rows][cols];
        boolean[][] visited = new boolean[rows][cols];

        int sr = 1, sc = 1;
        visited[sr][sc] = true;
        grid[sr][sc] = PATH;

        int[][] dirs = new int[][]{{0, -2}, {0, 2}, {-2, 0}, {2, 0}};

        // add neighbors of start to frontier
        for (int[] d : dirs) {
            int nr = sr + d[0], nc = sc + d[1];
            if (nr > 0 && nr < rows - 1 && nc > 0 && nc < cols - 1 && !inFrontier[nr][nc]) {
                frontier.add(new int[]{nr, nc});
                inFrontier[nr][nc] = true;
            }
        }

        while (!frontier.isEmpty()) {
            int idx = rand.nextInt(frontier.size());
            int[] cell = frontier.remove(idx);
            int r = cell[0], c = cell[1];
            inFrontier[r][c] = false;

            // find visited neighbors
            List<int[]> visitedNbrs = new ArrayList<>();
            for (int[] d : dirs) {
                int vr = r + d[0], vc = c + d[1];
                if (vr > 0 && vr < rows - 1 && vc > 0 && vc < cols - 1 && visited[vr][vc]) {
                    visitedNbrs.add(new int[]{vr, vc});
                }
            }
            if (!visitedNbrs.isEmpty()) {
                int[] pick = visitedNbrs.get(rand.nextInt(visitedNbrs.size()));
                // knock down wall between pick and cell
                int wr = (pick[0] + r) / 2, wc = (pick[1] + c) / 2;
                grid[wr][wc] = PATH;
                grid[r][c] = PATH;
                visited[r][c] = true;

                // add neighbors of cell to frontier
                for (int[] d : dirs) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr > 0 && nr < rows - 1 && nc > 0 && nc < cols - 1 && !visited[nr][nc] && !inFrontier[nr][nc]) {
                        frontier.add(new int[]{nr, nc});
                        inFrontier[nr][nc] = true;
                    }
                }
            }
        }

        int er = rows - 2, ec = cols - 2;
        grid[sr][sc] = PATH;
        grid[er][ec] = PATH;
        return new MazeLevel(name, grid, sr, sc, er, ec);
    }

    public static MazeLevel createPrimLevel(String name, int rows, int cols) {
        return createPrimLevel(name, rows, cols, System.currentTimeMillis());
    }
}

