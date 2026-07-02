package CLIENT.UI;

import javax.swing.*;
import java.awt.*;


public class Settings_Dialog extends JDialog {
    private boolean confirmed = false;
    private boolean single_player = true;
    private boolean normal_mode = true;
    private int enemy_speed = 1;
    private int bullet_speed = 4;         // 子彈速度
    private int enemy_count = 20;         // 敵坦數量(10-50)
    private boolean has_boss = false;
    private int selected_stage = 1;        // 自選關卡(1-8)
    private boolean continue_game = true;  // 是否繼續遊戲

    public Settings_Dialog(JFrame parent) {
        super(parent, "遊戲設置", true);
        setSize(420, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 標題
        JLabel title = new JLabel("遊戲設置");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.YELLOW);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        // 玩家模式
        JLabel player_label = new JLabel("玩家模式");
        player_label.setFont(new Font("Arial", Font.BOLD, 14));
        player_label.setForeground(Color.WHITE);
        player_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(player_label);

        JPanel player_panel = new JPanel(new FlowLayout());
        player_panel.setBackground(new Color(30, 30, 30));
        ButtonGroup player_group = new ButtonGroup();
        JRadioButton single_btn = new JRadioButton("單人模式");
        JRadioButton double_btn = new JRadioButton("雙人合作");
        single_btn.setSelected(true);
        style_radio_button(single_btn);
        style_radio_button(double_btn);
        player_group.add(single_btn);
        player_group.add(double_btn);
        player_panel.add(single_btn);
        player_panel.add(double_btn);
        panel.add(player_panel);
        panel.add(Box.createVerticalStrut(15));

        single_btn.addActionListener(e -> single_player = true);
        double_btn.addActionListener(e -> single_player = false);

        // 遊戲模式
        JLabel mode_label = new JLabel("遊戲模式");
        mode_label.setFont(new Font("Arial", Font.BOLD, 14));
        mode_label.setForeground(Color.WHITE);
        mode_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(mode_label);

        JPanel mode_panel = new JPanel(new FlowLayout());
        mode_panel.setBackground(new Color(30, 30, 30));
        ButtonGroup mode_group = new ButtonGroup();
        JRadioButton normal_btn = new JRadioButton("正常模式");
        JRadioButton custom_btn = new JRadioButton("自選模式");
        normal_btn.setSelected(true);
        style_radio_button(normal_btn);
        style_radio_button(custom_btn);
        mode_group.add(normal_btn);
        mode_group.add(custom_btn);
        mode_panel.add(normal_btn);
        mode_panel.add(custom_btn);
        panel.add(mode_panel);
        panel.add(Box.createVerticalStrut(15));

        // 自定義參數面板
        JPanel custom_panel = new JPanel();
        custom_panel.setBackground(new Color(30, 30, 30));
        custom_panel.setLayout(new GridLayout(6, 2, 10, 5));
        custom_panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "自定義參數",
                0, 0, new Font("Arial", Font.BOLD, 12), Color.WHITE));

        // 關卡選擇（自選模式可選關卡）
        custom_panel.add(createLabel("選擇關卡"));
        JSpinner stage_spinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        custom_panel.add(stage_spinner);

        // 坦克速度
        custom_panel.add(createLabel("坦克速度"));
        String[] speeds = {"慢速", "中速", "快速"};
        JComboBox<String> speed_combo = new JComboBox<>(speeds);
        custom_panel.add(speed_combo);

        // 子彈速度
        custom_panel.add(createLabel("子彈速度"));
        String[] bullets = {"普通", "快速"};
        JComboBox<String> bullet_combo = new JComboBox<>(bullets);
        custom_panel.add(bullet_combo);

        // 敵坦數量
        custom_panel.add(createLabel("敵坦數量(10-50)"));
        JSpinner count_spinner = new JSpinner(new SpinnerNumberModel(20, 10, 50, 1));
        custom_panel.add(count_spinner);

        // BOSS
        custom_panel.add(createLabel("開啟BOSS"));
        JCheckBox boss_check = new JCheckBox();
        boss_check.setBackground(new Color(30, 30, 30));
        custom_panel.add(boss_check);

        // 是否繼續遊戲
        custom_panel.add(createLabel("繼續遊戲"));
        JCheckBox continue_check = new JCheckBox();
        continue_check.setSelected(true);
        continue_check.setBackground(new Color(30, 30, 30));
        custom_panel.add(continue_check);

        // 初始禁用自定義參數
        set_custom_enabled(custom_panel, false);

        normal_btn.addActionListener(e -> {
            normal_mode = true;
            set_custom_enabled(custom_panel, false);
        });
        custom_btn.addActionListener(e -> {
            normal_mode = false;
            set_custom_enabled(custom_panel, true);
        });

        panel.add(custom_panel);
        panel.add(Box.createVerticalStrut(15));

        // 按鈕
        JPanel btn_panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btn_panel.setBackground(new Color(30, 30, 30));

        JButton confirm_btn = new JButton("開始遊戲");
        confirm_btn.setFont(new Font("Arial", Font.BOLD, 14));
        confirm_btn.setBackground(new Color(50, 150, 50));
        confirm_btn.setForeground(Color.WHITE);
        confirm_btn.setFocusPainted(false);
        confirm_btn.addActionListener(e -> {
            confirmed = true;
            selected_stage = (Integer) stage_spinner.getValue();
            enemy_speed = speed_combo.getSelectedIndex() + 1;
            bullet_speed = bullet_combo.getSelectedIndex() == 0 ? 4 : 8;
            enemy_count = (Integer) count_spinner.getValue();
            has_boss = boss_check.isSelected();
            continue_game = continue_check.isSelected();
            dispose();
        });
        btn_panel.add(confirm_btn);

        JButton cancel_btn = new JButton("取消");
        cancel_btn.setFont(new Font("Arial", Font.BOLD, 14));
        cancel_btn.setBackground(new Color(50, 50, 50));
        cancel_btn.setForeground(Color.WHITE);
        cancel_btn.setFocusPainted(false);
        cancel_btn.addActionListener(e -> dispose());
        btn_panel.add(cancel_btn);

        panel.add(btn_panel);
        add(panel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.WHITE);
        return label;
    }

    private void style_radio_button(JRadioButton btn) {
        btn.setBackground(new Color(30, 30, 30));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setFocusPainted(false);
    }

    private void set_custom_enabled(JPanel panel, boolean enabled) {
        for (Component c : panel.getComponents()) {
            c.setEnabled(enabled);
        }
    }

    public boolean is_confirmed() {
        return confirmed;
    }

    public boolean is_single_player() {
        return single_player;
    }

    public boolean is_normal_mode() {
        return normal_mode;
    }

    public int get_enemy_speed() {
        return enemy_speed;
    }

    public int get_bullet_speed() {
        return bullet_speed;
    }

    public int get_enemy_count() {
        return enemy_count;
    }

    public boolean has_boss() {
        return has_boss;
    }

    public int get_selected_stage() {
        return selected_stage;
    }

    public boolean is_continue_game() {
        return continue_game;
    }
}