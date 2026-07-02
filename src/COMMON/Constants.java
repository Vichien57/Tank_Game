package COMMON;


// 遊戲全局常量定義
public class Constants {
    // 窗口與畫布
    public static final int WINDOW_WIDTH = 832;
    public static final int WINDOW_HEIGHT = 640;
    public static final int GAME_WIDTH = 640;
    public static final int GAME_HEIGHT = 640;
    public static final int PANEL_WIDTH = 192;
    public static final int CELL_SIZE = 40;
    public static final int COLS = GAME_WIDTH / CELL_SIZE;
    public static final int ROWS = GAME_HEIGHT / CELL_SIZE;

    // 坦克尺寸
    public static final int TANK_WIDTH = 34;
    public static final int TANK_HEIGHT = 34;

    // 玩家
    public static final int PLAYER_INITIAL_LIVES = 3;
    public static final int PLAYER_SPEED = 3;
    public static final int PLAYER_BULLET_SPEED = 6;

    // 敵方坦克速度
    public static final int ENEMY_SPEED_SLOW = 1;
    public static final int ENEMY_SPEED_MEDIUM = 2;
    public static final int ENEMY_SPEED_FAST = 3;

    // 子彈速度
    public static final int BULLET_SPEED_NORMAL = 4;
    public static final int BULLET_SPEED_FAST = 8;

    // 刷新計時
    public static final int ENEMY_SPAWN_INTERVAL = 120;
    public static final int MAX_ACTIVE_ENEMIES = 6;

    // 數據庫
    public static final String DB_NAME = "T_GAME.DB";

    // 方向枚舉
    public enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    // 障礙物類型枚舉
    public enum ObstacleType {
        BRICK, STEEL, RIVER
    }

    // 敵方坦克類型枚舉
    public enum EnemyType {
        NORMAL(100, 0.70f, 1, Constants.BULLET_SPEED_NORMAL),
        FAST_BULLET(200, 0.15f, 1, Constants.BULLET_SPEED_FAST),
        FAST_MOVE(300, 0.10f, 1, Constants.BULLET_SPEED_NORMAL),
        HEAVY(400, 0.04f, 4, Constants.BULLET_SPEED_NORMAL),
        BOSS(1000, 0.01f, 6, Constants.BULLET_SPEED_FAST);

        public final int score;
        public final float spawn_weight;
        public final int hits_required;
        public final int bullet_speed;

        EnemyType(int score, float spawn_weight, int hits_required, int bullet_speed) {
            this.score = score;
            this.spawn_weight = spawn_weight;
            this.hits_required = hits_required;
            this.bullet_speed = bullet_speed;
        }
    }

    // 遊戲模式枚舉
    public enum Game_Mode {
        NORMAL, CUSTOM
    }

    // 玩家模式枚舉
    public enum Player_Mode {
        SINGLE, DOUBLE
    }
}