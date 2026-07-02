package CLIENT.UI;

import javax.swing.*;
import java.awt.*;


public class Game_Over_Dialog extends JDialog {
    private boolean restart_requested = false;
    private boolean return_to_menu = false;

    public Game_Over_Dialog(JFrame parent, int total_score, boolean is_new_high, boolean is_normal_mode) {
        super(parent, "遊戲結束", true);

        setSize(380, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title_label = new JLabel("GAME OVER");
        title_label.setFont(new Font("Arial", Font.BOLD, 28));
        title_label.setForeground(Color.RED);
        title_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title_label);
        panel.add(Box.createVerticalStrut(15));

        JLabel score_label = new JLabel("總得分: " + total_score);
        score_label.setFont(new Font("Arial", Font.PLAIN, 20));
        score_label.setForeground(Color.WHITE);
        score_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(score_label);
        panel.add(Box.createVerticalStrut(10));

        if (is_new_high && is_normal_mode) {
            JLabel high_label = new JLabel("新紀錄");
            high_label.setFont(new Font("Arial", Font.BOLD, 16));
            high_label.setForeground(Color.ORANGE);
            high_label.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(high_label);
            panel.add(Box.createVerticalStrut(10));
        }

        JPanel btn_panel = new JPanel();
        btn_panel.setBackground(new Color(30, 30, 30));
        btn_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton restart_btn = new JButton("重新開始");
        restart_btn.setFont(new Font("Arial", Font.BOLD, 13));
        restart_btn.setBackground(new Color(50, 150, 50));
        restart_btn.setForeground(Color.WHITE);
        restart_btn.setFocusPainted(false);
        restart_btn.addActionListener(e -> {
            restart_requested = true;
            dispose();
        });
        btn_panel.add(restart_btn);

        JButton menu_btn = new JButton("返回菜單");
        menu_btn.setFont(new Font("Arial", Font.BOLD, 13));
        menu_btn.setBackground(new Color(50, 50, 50));
        menu_btn.setForeground(Color.WHITE);
        menu_btn.setFocusPainted(false);
        menu_btn.addActionListener(e -> {
            return_to_menu = true;
            dispose();
        });
        btn_panel.add(menu_btn);

        panel.add(btn_panel);
        add(panel, BorderLayout.CENTER);
    }

    public boolean is_restart_requested() {
        return restart_requested;
    }

    public boolean is_return_to_menu() {
        return return_to_menu;
    }
}