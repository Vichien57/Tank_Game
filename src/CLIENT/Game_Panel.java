package CLIENT;

import CLIENT.UI.Game_Over_Dialog;
import CLIENT.UI.High_Score_Dialog;
import CLIENT.UI.Stage_Clear_Dialog;
import COMMON.Constants;
import COMMON.Constants.*;
import ENGINE.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

// 遊戲主面板
public class Game_Panel extends JPanel implements Runnable {
    private final Game_Client client;           // 父窗口引用
    private Game_Engine engine;                 // 遊戲引擎
    private final Game_Renderer renderer;       // 遊戲渲染器
    private final Input_Handler input_handler;  // 輸入處理器
    private Game_State current_state;           // 遊戲狀態
    private Thread game_thread;                 // 遊戲循環線程
    private volatile boolean running;           // 是否運行中
    private Game_Mode game_mode;                // 遊戲模式
    private Player_Mode player_mode;            // 玩家模式
    private int fps;                            // 渲染幀率
    private int frame_count;

    public Game_Panel(Game_Client client) {
        this.client = client;
        this.renderer = new Game_Renderer(this);
        this.input_handler = new Input_Handler(this);
        this.running = false;
        this.fps = 0;
        this.frame_count = 0;

        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(input_handler);
    }

    // 啟動正常模式遊戲（單機）
    public void normal_start(Player_Mode player_mode) {
        this.player_mode = player_mode;
        this.game_mode = Game_Mode.NORMAL;
        this.engine = new Game_Engine();

        setup_engine_events();
        engine.normal_start(player_mode);
        start_game_loop();
    }

    // 啟動自選模式遊戲（單機）
    public void custom_start(Player_Mode player_mode, int speed, int count, boolean boss) {
        this.player_mode = player_mode;
        this.game_mode = Game_Mode.CUSTOM;
        this.engine = new Game_Engine();
        setup_engine_events();
        engine.start_custom_game(player_mode, speed, count, boss);
        start_game_loop();
    }

    // 設定引擎事件監聽
    private void setup_engine_events() {
        engine.set_event_listener(new Game_Engine.GameEventListener() {
            @Override
            public void on_stage_cleared(int stage, int score) {
                SwingUtilities.invokeLater(() -> show_stage_clear(stage, score));
            }

            @Override
            public void on_game_over(int total_score) {
                SwingUtilities.invokeLater(() -> show_game_over(total_score));
            }

            @Override
            public void on_player_died(int player_id, int remaining_lives) {

            }

            @Override
            public void on_new_high_score(int total_score) {
                SwingUtilities.invokeLater(() -> show_high_score_input(total_score));
            }

            @Override
            public void on_enemy_killed(EnemyType type, int x, int y) {

            }
        });
    }

    // 啟動遊戲循環
    private void start_game_loop() {
        running = true;
        requestFocusInWindow();
        game_thread = new Thread(this, "GameRenderLoop");
        game_thread.start();
    }

    // 遊戲循環
    public void run() {
        // 使用主動渲染
        JFrame frame = client;
        frame.createBufferStrategy(2);
        BufferStrategy bs = frame.getBufferStrategy();

        long last_tick = System.nanoTime();
        double ns_per_tick = 1_000_000_000.0 / 60.0;
        double delta = 0;

        long last_fps_time = System.currentTimeMillis();

        while (running) {
            long now = System.nanoTime();
            delta += (now - last_tick) / ns_per_tick;
            last_tick = now;

            while (delta >= 1) {  // 更新邏輯
                update();
                delta--;
            }

            // 渲染
            render(bs);
            frame_count++;

            // FPS 計算
            long current_time = System.currentTimeMillis();
            if (current_time - last_fps_time >= 1000) {
                fps = frame_count;
                frame_count = 0;
                last_fps_time = current_time;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    // 遊戲邏輯更新
    private void update() {
        if (engine != null) {
            current_state = engine.update();

            // 處理持續按鍵的移動
            if (current_state != null && !current_state.game_over) {
                handle_continuous_input();
            }
        }
    }

    // 處理持續按住移動鍵的輸入
    private void handle_continuous_input() {
        if (input_handler.is_key_pressed(java.awt.event.KeyEvent.VK_W))
            engine.handle_player_move(0, Direction.UP);
        if (input_handler.is_key_pressed(java.awt.event.KeyEvent.VK_S))
            engine.handle_player_move(0, Direction.DOWN);
        if (input_handler.is_key_pressed(java.awt.event.KeyEvent.VK_A))
            engine.handle_player_move(0, Direction.LEFT);
        if (input_handler.is_key_pressed(java.awt.event.KeyEvent.VK_D))
            engine.handle_player_move(0, Direction.RIGHT);

        if (input_handler.is_key_pressed(java.awt.event.KeyEvent.VK_J))
            engine.handle_player_shoot(0);

        if (player_mode == Player_Mode.DOUBLE) {
            if (input_handler.is_key_pressed(java.awt.event.KeyEvent.VK_UP))
                engine.handle_player_move(1, Direction.UP);
            if (input_handler.is_key_pressed(java.awt.event.KeyEvent.VK_DOWN))
                engine.handle_player_move(1, Direction.DOWN);
            if (input_handler.is_key_pressed(java.awt.event.KeyEvent.VK_LEFT))
                engine.handle_player_move(1, Direction.LEFT);
            if (input_handler.is_key_pressed(java.awt.event.KeyEvent.VK_RIGHT))
                engine.handle_player_move(1, Direction.RIGHT);

            if (input_handler.is_key_pressed(java.awt.event.KeyEvent.VK_ENTER))  // 連續射擊
                engine.handle_player_shoot(1);
        }
    }

    // 渲染畫面
    private void render(BufferStrategy bs) {
        if (bs == null) return;
        do {
            do {
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                try {
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    renderer.render(g, current_state);

                    // 繪製 FPS
                    g.setColor(Color.GRAY);
                    g.setFont(new Font("Arial", Font.PLAIN, 10));
                    g.drawString("FPS: " + fps, Constants.GAME_WIDTH + 10, Constants.GAME_HEIGHT - 10);

                    // 暫停提示
                    if (engine != null && engine.is_paused()) {
                        g.setColor(new Color(0, 0, 0, 150));
                        g.fillRect(0, 0, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Arial", Font.BOLD, 36));
                        String pause_text = "PAUSED";
                        FontMetrics fm = g.getFontMetrics();
                        g.drawString(pause_text,
                                (Constants.GAME_WIDTH - fm.stringWidth(pause_text)) / 2,
                                Constants.GAME_HEIGHT / 2);
                    }
                } finally {
                    g.dispose();
                }
            } while (bs.contentsRestored());
            bs.show();
        } while (bs.contentsLost());
    }

    public void on_player_move(int player_id, Direction direction) {
        if (engine != null) {
            engine.handle_player_move(player_id, direction);
        }
    }

    public void on_player_shoot(int player_id) {
        if (engine != null) {
            engine.handle_player_shoot(player_id);
        }
    }

    public void toggle_pause() {
        if (engine != null) {
            engine.toggle_pause();
        }
    }

    public boolean is_double_mode() {
        return player_mode == Player_Mode.DOUBLE;
    }

    private void show_stage_clear(int stage, int score) {
        Game_State.PlayerState ps = current_state != null && !current_state.player_states.isEmpty()
                ? current_state.player_states.get(0) : null;
        int total_score = ps != null ? ps.total_score : score;
        boolean is_last = (game_mode == Game_Mode.NORMAL && stage >= 8)
                || game_mode == Game_Mode.CUSTOM;

        // 獲取擊殺統計
        java.util.Map<COMMON.Constants.EnemyType, Integer> kill_stats = null;
        if (current_state != null) {
            kill_stats = current_state.enemy_kill_stats;
        }
        if (kill_stats == null && engine != null) {
            kill_stats = engine.get_enemy_kill_stats();
        }

        Stage_Clear_Dialog dialog = new Stage_Clear_Dialog(
                client, stage, score, total_score, is_last, kill_stats);
        dialog.setVisible(true);
        if (engine != null) {
            engine.request_stage_advance();
        }
    }

    private void show_game_over(int total_score) {
        // 先暫停遊戲循環
        running = false;
        // 檢查是否為新紀錄（僅正常模式）
        boolean is_new_high = false;
        if (game_mode == Game_Mode.NORMAL && client != null) {
            int highest = client.get_highest_score();
            is_new_high = total_score > highest;
        }

        Game_Over_Dialog dialog = new Game_Over_Dialog(
                client, total_score, is_new_high, game_mode == Game_Mode.NORMAL);
        dialog.setVisible(true);
        if (engine != null) {
            engine.request_stage_advance();
        }

        // 如果是新紀錄 彈出輸入姓名對話框
        if (is_new_high && game_mode == Game_Mode.NORMAL) {
            show_high_score_input(total_score);
        }

        if (dialog.is_restart_requested()) {
            restart_game();
        } else if (dialog.is_return_to_menu()) {
            return_to_menu();
        }
    }

    private void show_high_score_input(int score) {
        High_Score_Dialog dialog = new High_Score_Dialog(client, score);
        dialog.setVisible(true);
        if (engine != null) {
            engine.request_stage_advance();
        }
        String name = dialog.get_player_name();
        if (name != null && client != null) {
            client.save_score(name, score);
        }
    }

    // 重新開始遊戲
    private void restart_game() {
        stop_game();
        if (game_mode == Game_Mode.NORMAL) {
            normal_start(player_mode);
        } else {
            // 自選模式重新開始使用相同參數
            Level_Manager lm = engine != null ? engine.get_level_manager() : null;
            int speed = lm != null ? lm.get_current_level().get_enemy_speed() : 1;
            int count = lm != null ? lm.get_current_level().get_enemy_count() : 20;
            boolean boss = lm != null ? lm.get_current_level().has_boss() : false;
            custom_start(player_mode, speed, count, boss);
        }
    }

    // 返回主菜單
    private void return_to_menu() {
        stop_game();
        if (client != null) {
            client.reset_to_menu();
        }
    }

    // 停止遊戲循環
    public void stop_game() {
        running = false;
        if (engine != null) {
            engine.stop();
        }
        if (game_thread != null) {
            try {
                game_thread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        input_handler.clear_keys();
        current_state = null;
    }
}