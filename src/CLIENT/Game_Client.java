package CLIENT;

import COMMON.Constants;
import COMMON.Constants.*;
import ENGINE.Database_Manager;
import CLIENT.UI.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class Game_Client extends JFrame {
    private Game_Panel game_panel;              // 遊戲面板
    private Main_Menu_Bar menu_bar;             // 菜單欄
    private final Database_Manager db_manager;  // 數據庫管理器
    private Game_Mode current_game_mode;        // 當前遊戲模式
    private Player_Mode current_player_mode;    // 當前玩家模式

    // 自選模式參數緩存
    private int custom_speed = 1;
    private int custom_bullet_speed = 4;
    private int custom_enemy_count = 20;
    private boolean custom_boss = false;
    private int custom_stage = 1;
    private boolean custom_continue = true;

    public Game_Client() {
        super("坦克大戰");

        // 初始化數據庫
        db_manager = new Database_Manager(Constants.DB_NAME);
        db_manager.initialize();

        // 設置窗口
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // 創建菜單欄
        create_menu_bar();

        // 創建初始畫面
        JPanel init_panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());

                // 繪製右側面板背景
                g.setColor(new Color(40, 40, 40));
                g.fillRect(Constants.GAME_WIDTH, 0, Constants.PANEL_WIDTH, Constants.GAME_HEIGHT);

                // 繪製網格
                g.setColor(new Color(30, 30, 30));
                int cs = Constants.CELL_SIZE;
                for (int i = 0; i <= Constants.COLS; i++) {
                    g.drawLine(i * cs, 0, i * cs, Constants.GAME_HEIGHT);
                }
                for (int i = 0; i <= Constants.ROWS; i++) {
                    g.drawLine(0, i * cs, Constants.GAME_WIDTH, i * cs);
                }

                g.setFont(new Font("Arial", Font.BOLD, 48));
                FontMetrics fm = g.getFontMetrics();
                String text = "STAGE 1";
                g.setColor(new Color(0, 0, 0, 100));
                g.drawString(text,
                        (Constants.GAME_WIDTH - fm.stringWidth(text)) / 2 + 2,
                        Constants.GAME_HEIGHT / 2 + 2);
                g.setColor(new Color(255, 255, 255, 180));
                g.drawString(text,
                        (Constants.GAME_WIDTH - fm.stringWidth(text)) / 2,
                        Constants.GAME_HEIGHT / 2);
            }
        };

        init_panel.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        add(init_panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);

        // 顯示操作說明
        SwingUtilities.invokeLater(this::show_controls_help);
    }

    // 創建菜單欄
    private void create_menu_bar() {
        menu_bar = new Main_Menu_Bar();
        setJMenuBar(menu_bar);

        // 遊戲菜單 新遊戲
        menu_bar.on_new_game(e -> start_new_game());
        menu_bar.on_exit(e -> System.exit(0));

        // 設置菜單
        menu_bar.on_game_settings(e -> show_settings());
        menu_bar.on_game_record(e -> show_records());

        // 幫助菜單
        menu_bar.on_controls(e -> show_controls_help());
        menu_bar.on_about(e -> show_about());
    }

    // 通過設置對話框開始新遊戲
    private void start_new_game() {
        Settings_Dialog dialog = new Settings_Dialog(this);
        dialog.setVisible(true);

        if (!dialog.is_confirmed()) return;

        current_player_mode = dialog.is_single_player() ? Player_Mode.SINGLE : Player_Mode.DOUBLE;
        current_game_mode = dialog.is_normal_mode() ? Game_Mode.NORMAL : Game_Mode.CUSTOM;

        custom_speed = dialog.get_enemy_speed();
        custom_bullet_speed = dialog.get_bullet_speed();
        custom_enemy_count = dialog.get_enemy_count();
        custom_boss = dialog.has_boss();
        custom_stage = dialog.get_selected_stage();
        custom_continue = dialog.is_continue_game();

        launch_game();
    }

    // 啟動遊戲
    private void launch_game() {
        // 清除舊面板
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());

        // 創建遊戲面板
        game_panel = new Game_Panel(this);
        getContentPane().add(game_panel, BorderLayout.CENTER);
        revalidate();
        repaint();

        // 啟動遊戲
        if (current_game_mode == Game_Mode.NORMAL) {
            game_panel.normal_start(current_player_mode);
        } else {
            game_panel.custom_start(current_player_mode, custom_speed,
                    custom_enemy_count, custom_boss);
        }
    }

    // 保存分數
    public void save_score(String player_name, int score) {
        if (current_game_mode != Game_Mode.NORMAL) return;
        db_manager.insert_score(player_name, score);
    }

    // 獲取歷史最高分
    public int get_highest_score() {
        return db_manager.get_highest_score();
    }

    // 獲取排行榜
    public List<Map<String, Object>> get_rankings() {
        return db_manager.get_top10();
    }

    // 顯示設置對話框
    private void show_settings() {
        Settings_Dialog dialog = new Settings_Dialog(this);
        dialog.setVisible(true);
        if (dialog.is_confirmed()) {
            current_player_mode = dialog.is_single_player() ? Player_Mode.SINGLE : Player_Mode.DOUBLE;
            current_game_mode = dialog.is_normal_mode() ? Game_Mode.NORMAL : Game_Mode.CUSTOM;
            custom_speed = dialog.get_enemy_speed();
            custom_bullet_speed = dialog.get_bullet_speed();
            custom_enemy_count = dialog.get_enemy_count();
            custom_boss = dialog.has_boss();
            custom_stage = dialog.get_selected_stage();
            custom_continue = dialog.is_continue_game();

            if (game_panel != null) {
                game_panel.stop_game();
            }
            launch_game();
        }
    }

    // 顯示排行榜
    private void show_records() {
        List<Map<String, Object>> rankings = get_rankings();
        Record_Dialog dialog = new Record_Dialog(this, rankings);
        dialog.setVisible(true);
    }

    // 顯示操作說明
    private void show_controls_help() {
        JOptionPane.showMessageDialog(this,
                "請閱讀說明文檔",
                "操作說明",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // 顯示關於對話框
    private void show_about() {
        JOptionPane.showMessageDialog(this,
                """
                        坦克大戰｜Tank-Battle
                        
                        純 Java 實現的經典坦克大戰遊戲
                        
                        開發者: Vichien, Chen W.F, Zeng J.Y.
                        """,
                "關於",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // 重置到主菜單畫面
    public void reset_to_menu() {
        SwingUtilities.invokeLater(() -> {
            // 清除舊面板
            getContentPane().removeAll();
            getContentPane().setLayout(new BorderLayout());

            // 重新創建初始畫面
            JPanel init_panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(new Color(40, 40, 40));
                    g.fillRect(Constants.GAME_WIDTH, 0, Constants.PANEL_WIDTH, Constants.GAME_HEIGHT);
                    g.setColor(new Color(30, 30, 30));
                    int cs = Constants.CELL_SIZE;
                    for (int i = 0; i <= Constants.COLS; i++) {
                        g.drawLine(i * cs, 0, i * cs, Constants.GAME_HEIGHT);
                    }
                    for (int i = 0; i <= Constants.ROWS; i++) {
                        g.drawLine(0, i * cs, Constants.GAME_WIDTH, i * cs);
                    }
                    g.setFont(new Font("Arial", Font.BOLD, 48));
                    FontMetrics fm = g.getFontMetrics();
                    String text = "STAGE 1";
                    g.setColor(new Color(255, 255, 255, 180));
                    g.drawString(text, (Constants.GAME_WIDTH - fm.stringWidth(text)) / 2, Constants.GAME_HEIGHT / 2);
                }
            };

            init_panel.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
            getContentPane().add(init_panel, BorderLayout.CENTER);

            // 確保菜單欄可見
            if (menu_bar != null) {
                setJMenuBar(menu_bar);
            }
            revalidate();
            repaint();
            game_panel = null;
        });
    }

    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "false");
        // 設置系統外觀
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 在 EDT 中啟動 GUI
        SwingUtilities.invokeLater(() -> {
            Game_Client client = new Game_Client();
            client.setVisible(true);
        });
    }
}
