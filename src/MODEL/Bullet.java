package MODEL;

import COMMON.Constants.*;

import java.awt.Rectangle;


public class Bullet {
    private int x;                      // 子彈 X 坐標
    private int y;                      // 子彈 Y 坐標
    private final Direction direction;  // 飛行方向
    private final int speed;            // 飛行速度
    private boolean active;             // 是否活躍
    private final int owner_id;         // 發射此子彈的坦克 ID
    private final boolean from_player;  // 是否為玩家子彈
    public static final int WIDTH = 6;  // 子彈寬度
    public static final int HEIGHT = 6; // 子彈高度

    public Bullet(int x, int y, Direction direction, int speed, int owner_id, boolean from_player) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.active = true;
        this.owner_id = owner_id;
        this.from_player = from_player;
    }

    public int get_x() {
        return x;
    }

    public int get_y() {
        return y;
    }

    public Direction get_direction() {
        return direction;
    }

    public int get_speed() {
        return speed;
    }

    public boolean is_active() {
        return active;
    }

    public void set_active(boolean a) {
        this.active = a;
    }

    public int get_owner_id() {
        return owner_id;
    }

    public boolean is_from_player() {
        return from_player;
    }

    // 每幀更新位置
    public void update() {
        switch (direction) {
            case UP:
                y -= speed;
                break;
            case DOWN:
                y += speed;
                break;
            case LEFT:
                x -= speed;
                break;
            case RIGHT:
                x += speed;
                break;
        }
    }

    // 獲取碰撞矩形
    public Rectangle get_bounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    // 檢查子彈是否超出邊界
    public boolean is_out_of_bounds(int map_width, int map_height) {
        return x < 0 || x > map_width || y < 0 || y > map_height;
    }

    @Override
    public String toString() {
        return String.format("Bullet[x=%d,y=%d,dir=%s,active=%b]", x, y, direction, active);
    }
}