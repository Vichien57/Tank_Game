package MODEL;


// 玩家數據類
public class Player {
    private final int id;     // 玩家編號
    private int lives;        // 剩餘生命值
    private int total_score;  // 累計總分
    private int stage_score;  // 當前關卡得分
    private boolean alive;    // 是否存活
    private Tank tank;        // 關聯的坦克

    public Player(int id, int initial_lives) {
        this.id = id;
        this.lives = initial_lives;
        this.total_score = 0;
        this.stage_score = 0;
        this.alive = true;
        this.tank = null;
    }

    public int get_id() {
        return id;
    }

    public int get_lives() {
        return lives;
    }

    public void set_lives(int l) {
        this.lives = l;
    }

    public int get_total_score() {
        return total_score;
    }

    public int get_stage_score() {
        return stage_score;
    }

    public boolean is_alive() {
        return alive;
    }

    public void set_alive(boolean a) {
        this.alive = a;
    }

    public Tank get_tank() {
        return tank;
    }

    public void set_tank(Tank t) {
        this.tank = t;
    }

    // 增加分數
    public void add_score(int points) {
        this.stage_score += points;
        this.total_score += points;
    }

    // 扣除生命值
    public void lose_life() {
        lives--;
        if (lives <= 0) {
            alive = false;
        }
    }

    // 重置關卡得分
    public void reset_stage_score() {
        this.stage_score = 0;
    }

    // 復活坦克
    public void respawn_tank(int x, int y) {
        if (tank != null) {
            tank.set_position(x, y);
            tank.set_alive(true);
            tank.set_direction(COMMON.Constants.Direction.UP);
        }
    }

    @Override
    public String toString() {
        return String.format("Player[id=%d,lives=%d,score=%d,total=%d]", id, lives, stage_score, total_score);
    }
}