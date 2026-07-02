package ENGINE;

import COMMON.Constants;
import COMMON.Constants.*;
import MODEL.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


// 遊戲引擎
public class Game_Engine {
    private final Game_Map game_map;  // 遊戲地圖
    private final Level_Manager level_manager;  // 關卡管理器
    private final List<Player> players;// 玩家列表
    private final List<Bullet> bullets;// 所有子彈
    private Player_Mode player_mode;// 玩家模式
    private Game_Mode game_mode;// 遊戲模式
    private int current_stage;// 當前關卡編號
    private boolean running;// 遊戲是否運行中
    private boolean paused;// 遊戲是否暫停
    private boolean game_over;// 遊戲是否結束
    private boolean stage_advance_pending;// 通關後等待客戶端確認進入下一關
    private Random random;// 隨機數
    private Map<EnemyType, Integer> enemy_kill_stats;// 本关各类型敌坦击杀统计
    private GameEventListener listener;// 遊戲事件監聽器

    // 遊戲事件回調接口
    public interface GameEventListener {
        void on_stage_cleared(int stage, int score);

        void on_game_over(int total_score);

        void on_player_died(int player_id, int remaining_lives);

        void on_new_high_score(int total_score);

        void on_enemy_killed(EnemyType type, int x, int y);
    }

    public Game_Engine() {
        this.game_map = new Game_Map(Constants.ROWS, Constants.COLS, Constants.CELL_SIZE);
        this.level_manager = new Level_Manager();
        this.players = new ArrayList<>();
        this.bullets = new CopyOnWriteArrayList<>();
        this.random = new Random();
        this.enemy_kill_stats = new ConcurrentHashMap<>();
        this.running = false;
        this.paused = false;
        this.game_over = false;
        this.stage_advance_pending = false;
        this.current_stage = 0;
    }

    // ==================== Getter ====================
    public Game_Map get_game_map() {
        return game_map;
    }

    public Level_Manager get_level_manager() {
        return level_manager;
    }

    public List<Player> get_players() {
        return players;
    }

    public List<Bullet> get_bullets() {
        return bullets;
    }

    public List<Tank> get_active_enemies() {
        return level_manager.get_active_enemies();
    }

    public int get_current_stage() {
        return current_stage;
    }

    public boolean is_running() {
        return running;
    }

    public boolean is_paused() {
        return paused;
    }

    public boolean is_game_over() {
        return game_over;
    }

    public Player_Mode get_player_mode() {
        return player_mode;
    }

    public Game_Mode get_game_mode() {
        return game_mode;
    }

    public void set_event_listener(GameEventListener l) {
        this.listener = l;
    }

    // 開始正常模式遊戲
    public void normal_start(Player_Mode mode) {
        this.player_mode = mode;
        this.game_mode = Game_Mode.NORMAL;
        this.current_stage = 1;
        this.game_over = false;
        this.running = true;
        this.paused = false;
        init_players();
        init_stage(current_stage);
    }

    // 開始自選模式遊戲
    public void start_custom_game(Player_Mode mode, int speed, int enemy_count, boolean has_boss) {
        this.player_mode = mode;
        this.game_mode = Game_Mode.CUSTOM;
        this.current_stage = 1;  // 自選模式只有一關
        this.game_over = false;
        this.running = true;
        this.paused = false;
        init_players();
        enemy_kill_stats.clear();
        game_map.load_default_map();
        level_manager.init_custom_level(speed, enemy_count, has_boss);
        spawn_players();
    }

    // 初始化玩家
    private void init_players() {
        players.clear();
        players.add(new Player(0, Constants.PLAYER_INITIAL_LIVES));
        if (player_mode == Player_Mode.DOUBLE) {
            players.add(new Player(1, Constants.PLAYER_INITIAL_LIVES));
        }
    }

    // 初始化關卡
    private void init_stage(int stage_number) {
        game_map.load_default_map();
        level_manager.init_normal_level(stage_number);
        bullets.clear();
        // 重置玩家關卡得分
        for (Player p : players) {
            p.reset_stage_score();
        }
        spawn_players();
    }

    // 在出生點生成玩家坦克
    private void spawn_players() {
        int cell = Constants.CELL_SIZE;
        // 玩家1出生在底部中間偏左
        Tank p1 = new Tank(9 * cell + 3, 15 * cell + 3, Direction.UP,
                Constants.PLAYER_SPEED, Constants.PLAYER_BULLET_SPEED,
                1, true);
        p1.set_player_id(0);
        players.get(0).set_tank(p1);

        if (player_mode == Player_Mode.DOUBLE && players.size() > 1) {
            // 玩家2出生在底部中間偏右
            Tank p2 = new Tank(7 * cell + 3, 15 * cell + 3, Direction.UP,
                    Constants.PLAYER_SPEED, Constants.PLAYER_BULLET_SPEED,
                    1, true);
            p2.set_player_id(1);
            players.get(1).set_tank(p2);
        }
    }

    // 每幀更新
    public Game_State update() {
        if (!running || game_over) return null;

        // 處理待處理的關卡推進（由EDT觸發）
        if (stage_advance_pending) {
            stage_advance_pending = false;
            current_stage++;
            if (game_mode == Game_Mode.NORMAL && current_stage <= 8) {
                init_stage(current_stage);
                this.paused = false;
            } else {
                end_game();
                return null;
            }
        }

        if (paused) return null;

        // 0. 更新玩家坦克冷卻
        for (Player p : players) {
            Tank pt = p.get_tank();
            if (pt != null && pt.is_alive()) {
                pt.decrement_cooldown();
            }
        }

        // 更新敵方坦克生成
        List<Tank> new_enemies = level_manager.update(game_map);

        // 更新AI行為（敵方移動和射擊）
        update_ai();

        // 更新子彈位置
        update_bullets();

        // 碰撞檢測
        check_collisions();

        // 檢查玩家死亡和復活
        check_player_deaths();

        // 檢查通關
        if (level_manager.is_stage_cleared()) {
            this.paused = true;
            int stage_score = players.get(0).get_stage_score();
            if (listener != null) listener.on_stage_cleared(current_stage, stage_score);
        }
        // 構建遊戲狀態快照
        return build_game_state();
    }

    // 更新AI行為
    private void update_ai() {
        for (Tank enemy : level_manager.get_active_enemies()) {
            if (!enemy.is_alive()) continue;
            enemy.decrement_cooldown();

            // 隨機改變方向
            if (random.nextInt(60) == 0) {
                Direction[] dirs = Direction.values();
                enemy.set_direction(dirs[random.nextInt(dirs.length)]);
            }

            // 嘗試移動
            enemy.set_moving(false);
            int old_x = enemy.get_x(), old_y = enemy.get_y();
            enemy.move_forward();
            if (!game_map.can_tank_move_to(enemy.get_x(), enemy.get_y(),
                    Constants.TANK_WIDTH, Constants.TANK_HEIGHT)) {
                // 碰撞，退回並換方向
                enemy.set_position(old_x, old_y);
                Direction[] dirs = Direction.values();
                enemy.set_direction(dirs[random.nextInt(dirs.length)]);
            }

            // 隨機射擊
            if (enemy.get_shoot_cooldown() <= 0 && random.nextInt(40) == 0) {
                fire_bullet(enemy);
                enemy.set_shoot_cooldown(40 + random.nextInt(40));
            }
        }
    }

    // 更新所有子彈
    private void update_bullets() {
        List<Bullet> to_remove = new ArrayList<>();
        for (Bullet b : bullets) {
            if (!b.is_active()) {
                to_remove.add(b);
                continue;
            }
            b.update();
            if (b.is_out_of_bounds(Constants.GAME_WIDTH, Constants.GAME_HEIGHT)) {
                b.set_active(false);
                to_remove.add(b);
            }
        }
        bullets.removeAll(to_remove);
    }

    // 碰撞檢測
    private void check_collisions() {
        // 子彈與障礙物碰撞
        for (Bullet b : bullets) {
            if (!b.is_active()) continue;
            Obstacle hit = game_map.check_bullet_collision(b.get_x(), b.get_y(), Bullet.WIDTH, Bullet.HEIGHT);
            if (hit != null) {
                if (hit.is_destructible()) {
                    // 磚牆被摧毀
                    game_map.remove_obstacle(hit.get_row(), hit.get_col());
                }
                if (!hit.is_bullet_passable()) {
                    // 非河流障礙物擋住子彈
                    b.set_active(false);
                }
            }
        }

        // 子彈與坦克碰撞
        for (Bullet b : bullets) {
            if (!b.is_active()) continue;
            java.awt.Rectangle bullet_rect = b.get_bounds();

            if (b.is_from_player()) {
                // 玩家子彈 vs 敵方坦克
                for (Tank enemy : level_manager.get_active_enemies()) {
                    if (!enemy.is_alive()) continue;
                    if (bullet_rect.intersects(enemy.get_bounds())) {
                        enemy.hit();
                        b.set_active(false);
                        if (!enemy.is_alive()) {
                            // 敵人被消滅
                            EnemyType type = enemy.get_enemy_type();
                            for (Player p : players) {
                                p.add_score(type.score);
                            }
                            level_manager.on_enemy_defeated();
                            if (listener != null) {
                                listener.on_enemy_killed(type, enemy.get_x(), enemy.get_y());
                            }
                        }
                        break;
                    }
                }
            } else {
                // 敵方子彈 vs 玩家坦克
                for (Player p : players) {
                    Tank pt = p.get_tank();
                    if (pt == null || !pt.is_alive()) continue;
                    if (bullet_rect.intersects(pt.get_bounds())) {
                        pt.set_alive(false);
                        b.set_active(false);
                        p.lose_life();
                        if (listener != null) {
                            listener.on_player_died(p.get_id(), p.get_lives());
                        }
                        break;
                    }
                }
            }
        }

        // 坦克之間碰撞（玩家 vs 敵人）
        for (Player p : players) {
            Tank pt = p.get_tank();
            if (pt == null || !pt.is_alive()) continue;
            for (Tank enemy : level_manager.get_active_enemies()) {
                if (!enemy.is_alive()) continue;
                if (pt.get_bounds().intersects(enemy.get_bounds())) {
                    // 雙方都摧毀
                    pt.set_alive(false);
                    enemy.set_alive(false);
                    p.lose_life();
                    level_manager.on_enemy_defeated();
                    if (listener != null) {
                        listener.on_player_died(p.get_id(), p.get_lives());
                    }
                    break;
                }
            }
        }
    }

    // 檢查玩家死亡並復活
    private void check_player_deaths() {
        for (Player p : players) {
            Tank pt = p.get_tank();
            if (pt == null) continue;
            if (!pt.is_alive() && p.is_alive()) {
                // 玩家坦克被摧毀但還有生命，復活
                int cell = Constants.CELL_SIZE;
                if (p.get_id() == 0) {
                    p.respawn_tank(9 * cell + 3, 15 * cell + 3);
                } else {
                    p.respawn_tank(7 * cell + 3, 15 * cell + 3);
                }
            }
        }

        // 檢查是否所有玩家都死亡
        boolean all_dead = true;
        for (Player p : players) {
            if (p.is_alive()) {
                all_dead = false;
                break;
            }
        }
        if (all_dead) {
            end_game();
        }
    }

    // 發射子彈
    public void fire_bullet(Tank tank) {
        if (tank.get_shoot_cooldown() > 0 || !tank.is_alive()) return;

        int cx = tank.get_x() + Constants.TANK_WIDTH / 2 - Bullet.WIDTH / 2;
        int cy = tank.get_y() + Constants.TANK_HEIGHT / 2 - Bullet.HEIGHT / 2;

        // 將子彈起點置於坦克前方
        switch (tank.get_direction()) {
            case UP:
                cy = tank.get_y() - Bullet.HEIGHT;
                break;
            case DOWN:
                cy = tank.get_y() + Constants.TANK_HEIGHT;
                break;
            case LEFT:
                cx = tank.get_x() - Bullet.WIDTH;
                break;
            case RIGHT:
                cx = tank.get_x() + Constants.TANK_WIDTH;
                break;
        }

        Bullet bullet = new Bullet(cx, cy, tank.get_direction(),
                tank.get_bullet_speed(), tank.get_player_id(), tank.is_player());
        bullets.add(bullet);
        tank.set_shoot_cooldown(20);  // 射擊冷卻
    }

    // 結束遊戲
    private void end_game() {
        this.running = false;
        this.game_over = true;
        Player p1 = players.isEmpty() ? null : players.get(0);
        int total_score = p1 != null ? p1.get_total_score() : 0;
        if (listener != null) listener.on_game_over(total_score);
    }

    // 由客戶端在通關彈窗關閉後調用，僅設置標誌位
    public void request_stage_advance() {
        stage_advance_pending = true;
    }

    // 構建遊戲狀態快照（用於網絡傳輸或渲染）
    private Game_State build_game_state() {
        Game_State state = new Game_State();
        state.current_stage = current_stage;
        state.game_mode = game_mode;
        state.player_mode = player_mode;
        state.game_over = game_over;
        state.enemies_remaining = level_manager.get_enemies_remaining();

        // 玩家狀態
        state.player_states = new ArrayList<>();
        for (Player p : players) {
            Game_State.PlayerState ps = new Game_State.PlayerState();
            ps.id = p.get_id();
            ps.lives = p.get_lives();
            ps.total_score = p.get_total_score();
            ps.stage_score = p.get_stage_score();
            Tank t = p.get_tank();
            if (t != null && t.is_alive()) {
                ps.tank_x = t.get_x();
                ps.tank_y = t.get_y();
                ps.tank_dir = t.get_direction();
                ps.tank_alive = true;
            } else {
                ps.tank_alive = false;
            }
            state.player_states.add(ps);
        }

        // 敵方坦克狀態
        state.enemy_states = new ArrayList<>();
        for (Tank e : level_manager.get_active_enemies()) {
            if (!e.is_alive()) continue;
            Game_State.TankState ts = new Game_State.TankState();
            ts.x = e.get_x();
            ts.y = e.get_y();
            ts.direction = e.get_direction();
            ts.type = e.get_enemy_type();
            ts.hp = e.get_hp();
            ts.max_hp = e.get_max_hp();
            state.enemy_states.add(ts);
        }

        // 子彈狀態
        state.bullet_states = new ArrayList<>();
        for (Bullet b : bullets) {
            if (!b.is_active()) continue;
            Game_State.BulletState bs = new Game_State.BulletState();
            bs.x = b.get_x();
            bs.y = b.get_y();
            bs.direction = b.get_direction();
            bs.from_player = b.is_from_player();
            state.bullet_states.add(bs);
        }

        // 障礙物狀態
        state.obstacles = game_map.get_all_obstacles();
        state.enemy_kill_stats = new HashMap<>(enemy_kill_stats);

        return state;
    }

    // 處理玩家移動輸入
    public void handle_player_move(int player_id, Direction direction) {
        if (!running || paused || game_over) return;
        Player player = get_player_by_id(player_id);
        if (player == null) return;
        Tank tank = player.get_tank();
        if (tank == null || !tank.is_alive()) return;

        tank.set_direction(direction);
        int old_x = tank.get_x(), old_y = tank.get_y();
        tank.move_forward();
        if (!game_map.can_tank_move_to(tank.get_x(), tank.get_y(),
                Constants.TANK_WIDTH, Constants.TANK_HEIGHT)) {
            tank.set_position(old_x, old_y);
        }
    }

    // 處理玩家射擊輸入
    public void handle_player_shoot(int player_id) {
        if (!running || paused || game_over) return;
        Player player = get_player_by_id(player_id);
        if (player == null) return;
        Tank tank = player.get_tank();
        if (tank == null || !tank.is_alive()) return;
        fire_bullet(tank);
    }

    // 根據ID獲取玩家
    private Player get_player_by_id(int id) {
        for (Player p : players) {
            if (p.get_id() == id) return p;
        }
        return null;
    }

    // 暫停/恢復
    public void toggle_pause() {
        this.paused = !this.paused;
    }

    // 獲取本關擊殺統計
    public Map<EnemyType, Integer> get_enemy_kill_stats() {
        return enemy_kill_stats;
    }

    // 停止遊戲
    public void stop() {
        this.running = false;
        this.game_over = true;
    }
}