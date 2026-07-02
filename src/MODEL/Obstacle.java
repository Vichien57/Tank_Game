package MODEL;

import COMMON.Constants.*;

import java.awt.Rectangle;


public class Obstacle {
    private final int col;      // 網格列
    private final int row;      // 網格行
    private ObstacleType type;  // 障礙物類型
    private final int x;        // 像素X
    private final int y;        // 像素Y
    private final int size;     // 單元格大小

    public Obstacle(int col, int row, ObstacleType type, int cell_size) {
        this.col = col;
        this.row = row;
        this.type = type;
        this.size = cell_size;
        this.x = col * cell_size;
        this.y = row * cell_size;
    }

    public int get_col() {
        return col;
    }

    public int get_row() {
        return row;
    }

    public ObstacleType get_type() {
        return type;
    }

    public void set_type(ObstacleType t) {
        this.type = t;
    }

    public int get_x() {
        return x;
    }

    public int get_y() {
        return y;
    }

    public int get_size() {
        return size;
    }

    // 獲取碰撞矩形
    public Rectangle get_bounds() {
        return new Rectangle(x, y, size, size);
    }

    // 子彈能否穿透此障礙物
    public boolean is_bullet_passable() {
        return type == ObstacleType.RIVER;
    }

    // 子彈能否摧毀此障礙物
    public boolean is_destructible() {
        return type == ObstacleType.BRICK;
    }

    public String toString() {
        return String.format("Obstacle[%d,%d type=%s]", col, row, type);
    }
}