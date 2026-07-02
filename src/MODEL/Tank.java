package MODEL;

import COMMON.Constants.*;

import java.awt.Rectangle;


public class Tank {
    protected int x;                // 坦克X坐標（像素）
    protected int y;                // 坦克Y坐標（像素）
    protected Direction direction;  // 坦克移動方向
    protected int speed;            // 坦克移動速度
    protected int bullet_speed;     // 子彈速度
    protected int hp;               // 當前生命值/剩餘擊中次數
    protected int max_hp;           // 最大生命值
    protected boolean alive;        // 是否存活
    protected boolean is_player;    // 是否為玩家坦克
    protected int player_id;        // 玩家編號（0=玩家1, 1=玩家2）
    protected EnemyType enemy_type; // 敵人類型（僅敵方坦克有效）
    protected int shoot_cooldown;   // 射擊冷卻計時器
    protected int move_frame;       // 移動幀計數器
    protected boolean moving;       // 是否正在移動

    public Tank(int x, int y, Direction direction, int speed, int bullet_speed, int max_hp, boolean is_player) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.bullet_speed = bullet_speed;
        this.max_hp = max_hp;
        this.hp = max_hp;
        this.alive = true;
        this.is_player = is_player;
        this.player_id = -1;
        this.enemy_type = null;
        this.shoot_cooldown = 0;
        this.move_frame = 0;
        this.moving = false;
    }

    public int get_x() {
        return x;
    }

    public void set_x(int x) {
        this.x = x;
    }

    public int get_y() {
        return y;
    }

    public void set_y(int y) {
        this.y = y;
    }

    public Direction get_direction() {
        return direction;
    }

    public void set_direction(Direction d) {
        this.direction = d;
    }

    public int get_speed() {
        return speed;
    }

    public void set_speed(int s) {
        this.speed = s;
    }

    public int get_bullet_speed() {
        return bullet_speed;
    }

    public int get_hp() {
        return hp;
    }

    public int get_max_hp() {
        return max_hp;
    }

    public boolean is_alive() {
        return alive;
    }

    public void set_alive(boolean a) {
        this.alive = a;
    }

    public boolean is_player() {
        return is_player;
    }

    public int get_player_id() {
        return player_id;
    }

    public void set_player_id(int id) {
        this.player_id = id;
    }

    public EnemyType get_enemy_type() {
        return enemy_type;
    }

    public void set_enemy_type(EnemyType t) {
        this.enemy_type = t;
    }

    public int get_shoot_cooldown() {
        return shoot_cooldown;
    }

    public void set_shoot_cooldown(int c) {
        this.shoot_cooldown = c;
    }

    public boolean is_moving() {
        return moving;
    }

    public void set_moving(boolean m) {
        this.moving = m;
    }

    // 減少冷卻計數
    public void decrement_cooldown() {
        if (shoot_cooldown > 0) shoot_cooldown--;
    }

    // 被擊中，減少生命值
    public void hit() {
        hp--;
        if (hp <= 0) {
            alive = false;
        }
    }

    // 獲取碰撞矩形
    public Rectangle get_bounds() {
        return new Rectangle(x, y, COMMON.Constants.TANK_WIDTH, COMMON.Constants.TANK_HEIGHT);
    }

    // 向前移動（根據方向）
    public void move_forward() {
        moving = true;
        move_frame++;
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

    // 向後移動（倒退）
    public void move_backward() {
        switch (direction) {
            case UP:
                y += speed;
                break;
            case DOWN:
                y -= speed;
                break;
            case LEFT:
                x += speed;
                break;
            case RIGHT:
                x -= speed;
                break;
        }
    }

    // 設置位置
    public void set_position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return String.format("Tank[x=%d,y=%d,dir=%s,hp=%d,alive=%b]", x, y, direction, hp, alive);
    }
}