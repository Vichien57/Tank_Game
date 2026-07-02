package MODEL;

import COMMON.Constants.*;

import java.util.ArrayList;
import java.util.List;


// 遊戲地圖類
public class Game_Map {
    private Obstacle[][] grid;
    private int rows;
    private int cols;
    private int cell_size;

    public Game_Map(int rows, int cols, int cell_size) {
        this.rows = rows;
        this.cols = cols;
        this.cell_size = cell_size;
        this.grid = new Obstacle[rows][cols];
    }

    public int get_rows() {
        return rows;
    }

    public int get_cols() {
        return cols;
    }

    public int get_cell_size() {
        return cell_size;
    }

    // 在指定網格位置放置障礙物
    public void set_obstacle(int row, int col, ObstacleType type) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            grid[row][col] = new Obstacle(col, row, type, cell_size);
        }
    }

    // 移除指定位置的障礙物
    public void remove_obstacle(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            grid[row][col] = null;
        }
    }

    // 獲取指定位置的障礙物
    public Obstacle get_obstacle(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            return grid[row][col];
        }
        return null;
    }

    // 獲取所有非空障礙物列表
    public List<Obstacle> get_all_obstacles() {
        List<Obstacle> list = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] != null) {
                    list.add(grid[r][c]);
                }
            }
        }
        return list;
    }

    // 根據像素坐標獲取網格位置
    public int get_grid_row(int pixelY) {
        return pixelY / cell_size;
    }

    public int get_grid_col(int pixelX) {
        return pixelX / cell_size;
    }

    // 檢查坦克是否能移動到指定位置
    public boolean can_tank_move_to(int x, int y, int width, int height) {
        // 邊界檢查
        if (x < 0 || y < 0 || x + width > cols * cell_size || y + height > rows * cell_size) {
            return false;
        }
        // 障礙物碰撞檢查
        java.awt.Rectangle tank_rect = new java.awt.Rectangle(x, y, width, height);
        for (Obstacle obs : get_all_obstacles()) {
            if (tank_rect.intersects(obs.get_bounds())) {
                return false;
            }
        }
        return true;
    }

    // 子彈碰撞檢查 返回碰撞的障礙物
    public Obstacle check_bullet_collision(int bx, int by, int bw, int bh) {
        java.awt.Rectangle bullet_rect = new java.awt.Rectangle(bx, by, bw, bh);
        for (Obstacle obs : get_all_obstacles()) {
            if (bullet_rect.intersects(obs.get_bounds())) {
                return obs;
            }
        }
        return null;
    }

    // 清空地圖
    public void clear() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = null;
            }
        }
    }

    // 從二維整數數組加載地圖
    public void load_from_array(int[][] mapData) {
        clear();
        for (int r = 0; r < Math.min(rows, mapData.length); r++) {
            for (int c = 0; c < Math.min(cols, mapData[r].length); c++) {
                int val = mapData[r][c];
                if (val == 1) {
                    set_obstacle(r, c, ObstacleType.BRICK);
                } else if (val == 2) {
                    set_obstacle(r, c, ObstacleType.STEEL);
                } else if (val == 3) {
                    set_obstacle(r, c, ObstacleType.RIVER);
                }
            }
        }
    }

    // 重置地圖為默認布局
    public void load_default_map() {
        clear();
        // 在地圖四周添加一些障礙物形成迷宮布局
        int[][] defaultMap = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0},
                {0, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0},
                {0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0},
                {0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 1, 1, 0, 0, 0},
                {0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };
        load_from_array(defaultMap);
    }
}