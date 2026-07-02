package ENGINE;

import COMMON.Constants.*;
import java.util.Map;
import MODEL.*;
import java.util.List;

// 遊戲狀態快照
public class Game_State {
    public int current_stage;
    public Game_Mode game_mode;
    public Player_Mode player_mode;
    public boolean game_over;
    public int enemies_remaining;
    public List<PlayerState> player_states;
    public List<TankState> enemy_states;
    public List<BulletState> bullet_states;
    public List<Obstacle> obstacles;
    public Map<EnemyType, Integer> enemy_kill_stats;

    // 玩家狀態
    public static class PlayerState {
        public int id;
        public int lives;
        public int total_score;
        public int stage_score;
        public int tank_x;
        public int tank_y;
        public Direction tank_dir;
        public boolean tank_alive;
    }

    // 坦克狀態
    public static class TankState {
        public int x;
        public int y;
        public Direction direction;
        public EnemyType type;
        public int hp;
        public int max_hp;
    }

    // 子彈狀態
    public static class BulletState {
        public int x;
        public int y;
        public Direction direction;
        public boolean from_player;
    }
}
