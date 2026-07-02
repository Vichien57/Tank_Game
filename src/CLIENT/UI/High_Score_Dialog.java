package CLIENT.UI;

import javax.swing.*;
import java.awt.*;


public class High_Score_Dialog extends JDialog {
    private String player_name = null;

    public High_Score_Dialog(JFrame parent, int score) {
        super(parent, "新紀錄", true);

        setSize(350, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title_label = new JLabel("恭喜 新紀錄 " + score + " 分");
        title_label.setFont(new Font("Arial", Font.BOLD, 16));
        title_label.setForeground(Color.ORANGE);
        title_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title_label);
        panel.add(Box.createVerticalStrut(15));

        JLabel prompt_label = new JLabel("請輸入你的名字");
        prompt_label.setFont(new Font("Arial", Font.PLAIN, 14));
        prompt_label.setForeground(Color.WHITE);
        prompt_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(prompt_label);
        panel.add(Box.createVerticalStrut(10));

        JTextField name_field = new JTextField(15);
        name_field.setFont(new Font("Arial", Font.PLAIN, 14));
        name_field.setMaximumSize(new Dimension(200, 30));
        name_field.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(name_field);
        panel.add(Box.createVerticalStrut(15));

        JButton btn = new JButton("確認");
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(new Color(50, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> {
            String name = name_field.getText().trim();
            if (!name.isEmpty()) {
                player_name = name;
                dispose();
            }
        });
        panel.add(btn);

        add(panel, BorderLayout.CENTER);

        // 監聽回車鍵
        name_field.addActionListener(e -> {
            String name = name_field.getText().trim();
            if (!name.isEmpty()) {
                player_name = name;
                dispose();
            }
        });
    }

    public String get_player_name() {
        return player_name;
    }
}