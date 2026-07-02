package CLIENT;

import COMMON.Constants;
import COMMON.Constants.*;
import MODEL.*;
import ENGINE.Game_State;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;


// 遊戲渲染器
public class Game_Renderer {
    private static final Color COLOR_PLAYER1 = new Color(255, 255, 0);             // 黃色
    private static final Color COLOR_PLAYER2 = new Color(0, 255, 0);               // 綠色
    private static final Color COLOR_ENEMY_NORMAL = new Color(135, 206, 235);      // 淺藍
    private static final Color COLOR_ENEMY_FAST_BULLET = new Color(255, 182, 193); // 粉色
    private static final Color COLOR_ENEMY_FAST_MOVE = new Color(255, 99, 71);     // 紅色
    private static final Color COLOR_ENEMY_HEAVY = new Color(144, 238, 144);       // 綠色
    private static final Color COLOR_ENEMY_BOSS = new Color(70, 130, 180);         // 深藍
    private static final Color COLOR_BRICK = new Color(205, 133, 63);              // 磚色
    private static final Color COLOR_STEEL = new Color(169, 169, 169);             // 灰色
    private static final Color COLOR_RIVER = new Color(30, 144, 255);              // 藍色
    private static final Color COLOR_BULLET_PLAYER = Color.WHITE;
    private static final Color COLOR_BULLET_ENEMY = Color.ORANGE;
    private static final Color COLOR_BG = Color.BLACK;
    private static final Color COLOR_PANEL_BG = new Color(40, 40, 40);
    private static final Color COLOR_TEXT = Color.WHITE;
    private static final Color COLOR_HP_BAR = Color.RED;

    public Game_Renderer(Game_Panel panel) {

    }

    // 繪製完整遊戲畫面
    public void render(Graphics2D g, Game_State state) {
        int gw = Constants.GAME_WIDTH;
        int gh = Constants.GAME_HEIGHT;

        // 繪製遊戲區域背景
        g.setColor(COLOR_BG);
        g.fillRect(0, 0, gw, gh);

        // 繪製網格線
        g.setColor(new Color(30, 30, 30));
        int cs = Constants.CELL_SIZE;
        for (int i = 0; i <= Constants.COLS; i++) {
            g.drawLine(i * cs, 0, i * cs, gh);
        }
        for (int i = 0; i <= Constants.ROWS; i++) {
            g.drawLine(0, i * cs, gw, i * cs);
        }

        // 繪製障礙物
        if (state != null && state.obstacles != null) {
            render_obstacles(g, state.obstacles);
        }

        // 繪製敵方坦克
        if (state != null && state.enemy_states != null) {
            render_enemy_tanks(g, state.enemy_states);
        }

        // 繪製玩家坦克
        if (state != null && state.player_states != null) {
            render_player_tanks(g, state.player_states);
        }

        // 繪製子彈
        if (state != null && state.bullet_states != null) {
            render_bullets(g, state.bullet_states);
        }

        // 繪製關卡標題
        if (state != null) {
            render_stage_title(g, state.current_stage);
        }

        // 繪製右側信息面板
        render_info_panel(g, state);
    }

    // 繪製障礙物
    private void render_obstacles(Graphics2D g, List<Obstacle> obstacles) {
        for (Obstacle obs : obstacles) {
            int x = obs.get_x();
            int y = obs.get_y();
            int s = obs.get_size();

            switch (obs.get_type()) {
                case BRICK:
                    g.setColor(COLOR_BRICK);
                    g.fillRect(x, y, s, s);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, s, s);
                    // 繪製磚紋
                    g.drawLine(x, y + s / 2, x + s, y + s / 2);
                    g.drawLine(x + s / 2, y, x + s / 2, y + s / 2);
                    break;
                case STEEL:
                    g.setColor(COLOR_STEEL);
                    g.fillRect(x, y, s, s);
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect(x, y, s, s);
                    // 鐵牆花紋
                    g.fillRect(x + 5, y + 5, s - 10, s - 10);
                    break;
                case RIVER:
                    g.setColor(COLOR_RIVER);
                    g.fillRect(x, y, s, s);
                    // 河流波紋
                    g.setColor(new Color(100, 180, 255));
                    for (int wy = y + 5; wy < y + s; wy += 8) {
                        g.drawLine(x + 2, wy, x + s - 2, wy);
                    }
                    break;
            }
        }
    }

    // 繪製敵方坦克
    private void render_enemy_tanks(Graphics2D g, List<Game_State.TankState> enemies) {
        for (Game_State.TankState ts : enemies) {
            Color color = get_enemy_color(ts.type);
            render_tank(g, ts.x, ts.y, ts.direction, color, false, ts.hp, ts.max_hp);
        }
    }

    // 繪製玩家坦克
    private void render_player_tanks(Graphics2D g, List<Game_State.PlayerState> players) {
        for (Game_State.PlayerState ps : players) {
            if (!ps.tank_alive) continue;
            Color color = (ps.id == 0) ? COLOR_PLAYER1 : COLOR_PLAYER2;
            render_tank(g, ps.tank_x, ps.tank_y, ps.tank_dir, color, true, 1, 1);
        }
    }

    // 繪製單個坦克
    private void render_tank(Graphics2D g, int x, int y, Direction dir, Color color, boolean is_player, int hp, int max_hp) {
        int tw = Constants.TANK_WIDTH;
        int th = Constants.TANK_HEIGHT;

        // 計算旋轉角度
        double angle = switch (dir) {
            case UP -> 0;
            case DOWN -> Math.PI;
            case LEFT -> -Math.PI / 2;
            case RIGHT -> Math.PI / 2;
        };

        // 保存變換
        AffineTransform old = g.getTransform();
        g.translate(x + tw / 2.0, y + th / 2.0);
        g.rotate(angle);

        // 繪製車身
        g.setColor(color);
        g.fillRect(-tw / 2, -th / 2, tw, th);

        // 繪製履帶
        g.setColor(color.darker());
        g.fillRect(-tw / 2, -th / 2, 5, th);
        g.fillRect(tw / 2 - 5, -th / 2, 5, th);

        // 繪製炮管
        g.setColor(color.brighter());
        g.fillRect(-3, -th / 2 - 8, 6, 12);

        // 繪製車身中心
        g.setColor(color.brighter().brighter());
        g.fillOval(-4, -4, 8, 8);

        // 繪製邊框
        g.setColor(Color.BLACK);
        g.drawRect(-tw / 2, -th / 2, tw, th);

        // 血條
        if (!is_player && max_hp > 1) {
            int bar_height = 4;
            int bar_y = th / 2 + 3;

            g.setColor(Color.DARK_GRAY);
            g.fillRect(-tw / 2, bar_y, tw, bar_height);
            g.setColor(COLOR_HP_BAR);
            int fill_width = (int) (tw * ((double) hp / max_hp));
            g.fillRect(-tw / 2, bar_y, fill_width, bar_height);
        }

        // 恢復變換
        g.setTransform(old);
    }

    // 繪製子彈
    private void render_bullets(Graphics2D g, List<Game_State.BulletState> bullets) {
        for (Game_State.BulletState bs : bullets) {
            g.setColor(bs.from_player ? COLOR_BULLET_PLAYER : COLOR_BULLET_ENEMY);
            g.fillOval(bs.x - 2, bs.y - 2, Bullet.WIDTH + 4, Bullet.HEIGHT + 4);
            g.setColor(bs.from_player ? Color.YELLOW : Color.RED);
            g.fillOval(bs.x, bs.y, Bullet.WIDTH, Bullet.HEIGHT);
        }
    }

    // 繪製關卡標題
    private void render_stage_title(Graphics2D g, int stage) {
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String text = "STAGE " + stage;
        FontMetrics fm = g.getFontMetrics();
        int text_width = fm.stringWidth(text);

        // 文字陰影
        g.setColor(new Color(0, 0, 0, 100));
        g.drawString(text,
                (Constants.GAME_WIDTH - text_width) / 2 + 2,
                Constants.GAME_HEIGHT / 2 + 2);

        // 文字本體
        g.setColor(new Color(255, 255, 255, 180));
        g.drawString(text,
                (Constants.GAME_WIDTH - text_width) / 2,
                Constants.GAME_HEIGHT / 2);
    }

    // 繪製右側信息面板
    private void render_info_panel(Graphics2D g, Game_State state) {
        int px = Constants.GAME_WIDTH;
        int pw = Constants.PANEL_WIDTH;

        // 面板背景
        g.setColor(COLOR_PANEL_BG);
        g.fillRect(px, 0, pw, Constants.GAME_HEIGHT);

        // 分隔線
        g.setColor(Color.GRAY);
        g.drawLine(px, 0, px, Constants.GAME_HEIGHT);

        int y = 20;
        g.setColor(COLOR_TEXT);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        // 關卡信息
        if (state != null) {
            String stage_text = "STAGE: " + state.current_stage;
            g.drawString(stage_text, px + 10, y);
            y += 25;

            // 剩餘敵人
            String enemy_text = "ENEMIES: " + state.enemies_remaining;
            g.drawString(enemy_text, px + 10, y);
            y += 30;

            // 玩家信息
            if (state.player_states != null) {
                for (Game_State.PlayerState ps : state.player_states) {
                    g.setColor(COLOR_TEXT);
                    String p_label = (ps.id == 0) ? "P1" : "P2";
                    g.drawString(p_label, px + 10, y);
                    y += 20;

                    // 生命值
                    g.setColor(Color.RED);
                    for (int i = 0; i < ps.lives; i++) {
                        g.fillRect(px + 15 + i * 18, y - 12, 12, 12);
                    }
                    y += 5;

                    // 分數
                    g.setColor(Color.YELLOW);
                    g.drawString("Score: " + ps.stage_score, px + 10, y);
                    y += 20;

                    // 總分
                    g.drawString("Total: " + ps.total_score, px + 10, y);
                    y += 35;
                }
            }
        }

        // 操作提示
        y = Constants.GAME_HEIGHT - 120;
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        g.drawString("P1: WASD + J", px + 10, y);
        y += 18;
        g.drawString("P2: Arrows + Enter", px + 10, y);
        y += 25;
        g.drawString("P: Pause", px + 10, y);
    }

    // 獲取敵方坦克顏色
    private Color get_enemy_color(EnemyType type) {
        return switch (type) {
            case NORMAL -> COLOR_ENEMY_NORMAL;
            case FAST_BULLET -> COLOR_ENEMY_FAST_BULLET;
            case FAST_MOVE -> COLOR_ENEMY_FAST_MOVE;
            case HEAVY -> COLOR_ENEMY_HEAVY;
            case BOSS -> COLOR_ENEMY_BOSS;
        };
    }
}