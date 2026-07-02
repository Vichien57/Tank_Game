package ENGINE;

import COMMON.Constants;
import COMMON.Constants.*;
import MODEL.*;

import java.util.*;

// 關卡管理器
public class Level_Manager {
    private Level_Data current_level;

    private Level_Data[] standard_levels;

    private int enemies_defeated;

    private int enemies_spawned;

    private int spawn_timer;

    private List<Tank> active_enemies;

    private boolean is_custom;

    private int custom_speed;
    private int custom_count;
    private boolean custom_boss;

    private Random random;

    public Level_Manager() {
        this.standard_levels = Level_Data.get_standard_levels();
        this.random = new Random();
        this.active_enemies = new ArrayList<>();
        this.is_custom = false;
    }

    // 初始化正常模式關卡
    public void init_normal_level(int stage_number) {
        if (stage_number >= 1 && stage_number <= standard_levels.length) {
            this.current_level = standard_levels[stage_number - 1];
        }
        this.is_custom = false;
        this.enemies_defeated = 0;
        this.enemies_spawned = 0;
        this.spawn_timer = 0;
        this.active_enemies.clear();
    }

    // 初始化自選模式關卡
    public void init_custom_level(int speed, int count, boolean has_boss) {
        this.is_custom = true;
        this.custom_speed = speed;
        this.custom_count = count;
        this.custom_boss = has_boss;
        this.current_level = new Level_Data(0, speed, count, has_boss);
        this.enemies_defeated = 0;
        this.enemies_spawned = 0;
        this.spawn_timer = 0;
        this.active_enemies.clear();
    }

    public Level_Data get_current_level() {
        return current_level;
    }

    public int get_enemies_defeated() {
        return enemies_defeated;
    }

    public int get_enemies_spawned() {
        return enemies_spawned;
    }

    public int get_enemies_remaining() {
        return current_level.get_enemy_count() - enemies_defeated;
    }

    public List<Tank> get_active_enemies() {
        return active_enemies;
    }

    public boolean is_custom() {
        return is_custom;
    }

    // 記錄消滅一個敵人
    public void on_enemy_defeated() {
        enemies_defeated++;
    }

    // 判斷是否通關（已消滅所有敵人）
    // 返回true表示關卡完成
    public boolean is_stage_cleared() {
        return enemies_defeated >= current_level.get_enemy_count();
    }

    // 每幀更新 - 處理敵方坦克刷新
    // 返回新生成的坦克列表
    public List<Tank> update(Game_Map game_map) {
        List<Tank> new_spawns = new ArrayList<>();
        spawn_timer++;

        // 檢查是否需要生成敵方坦克
        if (spawn_timer >= Constants.ENEMY_SPAWN_INTERVAL
                && active_enemies.size() < Constants.MAX_ACTIVE_ENEMIES
                && enemies_spawned < current_level.get_enemy_count()) {

            spawn_timer = 0;
            Tank enemy = spawn_enemy(game_map);
            if (enemy != null) {
                active_enemies.add(enemy);
                new_spawns.add(enemy);
                enemies_spawned++;
            }
        }

        // 清理已死亡敵人
        active_enemies.removeIf(e -> !e.is_alive());

        return new_spawns;
    }

    // 生成一個敵方坦克
    // 在三個生成點（頂部左/中/右）隨機選擇一個空位
    private Tank spawn_enemy(Game_Map game_map) {
        int cell_size = Constants.CELL_SIZE;
        // 三個生成點：左上、頂中、右上
        int[][] spawn_points = {
                {0 * cell_size + 3, 0},           // 左上
                {8 * cell_size + 3, 0},           // 頂中
                {14 * cell_size + 3, 0},          // 右上
        };

        // 隨機選擇生成點
        List<int[]> valid_points = new ArrayList<>();
        for (int[] pt : spawn_points) {
            if (game_map.can_tank_move_to(pt[0], pt[1], Constants.TANK_WIDTH, Constants.TANK_HEIGHT)) {
                valid_points.add(pt);
            }
        }

        if (valid_points.isEmpty()) return null;

        int[] spawn_pt = valid_points.get(random.nextInt(valid_points.size()));

        // 選擇敵人類型
        EnemyType type = select_enemy_type();
        int speed = current_level.get_enemy_speed();
        // 紅色坦克移動更快
        if (type == EnemyType.FAST_MOVE) {
            speed = speed * 2;
        }
        // BOSS移動更快
        if (type == EnemyType.BOSS) {
            speed = speed * 2;
        }

        Tank enemy = new Tank(
                spawn_pt[0], spawn_pt[1],
                Direction.DOWN,  // 向下移動
                speed,
                type.bullet_speed,
                type.hits_required,
                false
        );
        enemy.set_enemy_type(type);

        // 給敵人一個隨機的初始射擊冷卻
        enemy.set_shoot_cooldown(random.nextInt(60) + 30);

        return enemy;
    }

    // 根據權重選擇敵人類型
    private EnemyType select_enemy_type() {
        // 檢查是否應該生成BOSS
        if (current_level.has_boss() && enemies_spawned >= current_level.get_enemy_count() - 1) {
            // 最後一個敵人是BOSS
            // 檢查是否已有BOSS
            for (Tank e : active_enemies) {
                if (e.get_enemy_type() == EnemyType.BOSS) return EnemyType.NORMAL;
            }
            return EnemyType.BOSS;
        }

        // 按權重隨機選擇
        float roll = random.nextFloat();
        float cumulative = 0;
        EnemyType[] types = {EnemyType.NORMAL, EnemyType.FAST_BULLET, EnemyType.FAST_MOVE, EnemyType.HEAVY};

        for (EnemyType t : types) {
            cumulative += t.spawn_weight;
            if (roll <= cumulative) {
                return t;
            }
        }
        return EnemyType.NORMAL;
    }

    // 清理所有敵人
    public void clear_enemies() {
        active_enemies.clear();
        enemies_defeated = 0;
        enemies_spawned = 0;
        spawn_timer = 0;
    }

    // 重置
    public void reset() {
        clear_enemies();
        current_level = null;
    }
}
