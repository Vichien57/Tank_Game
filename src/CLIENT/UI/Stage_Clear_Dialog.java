package CLIENT.UI;

import COMMON.Constants.EnemyType;

import javax.swing.*;
import java.awt.*;
import java.util.Map;


public class Stage_Clear_Dialog extends JDialog {
    public Stage_Clear_Dialog(JFrame parent, int stage, int score, int total_score, boolean is_last, Map<EnemyType, Integer> kill_stats) {
        super(parent, "關卡通過", true);
        setSize(380, 380);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 標題
        JLabel title_label = new JLabel("STAGE " + stage + " CLEAR");
        title_label.setFont(new Font("Arial", Font.BOLD, 22));
        title_label.setForeground(Color.YELLOW);
        title_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title_label);
        panel.add(Box.createVerticalStrut(15));

        // 分數明細
        int total_kills = 0;
        if (kill_stats != null && !kill_stats.isEmpty()) {
            EnemyType[] display_order = {
                    EnemyType.NORMAL, EnemyType.FAST_BULLET,
                    EnemyType.FAST_MOVE, EnemyType.HEAVY, EnemyType.BOSS
            };
            for (EnemyType type : display_order) {
                int count = kill_stats.getOrDefault(type, 0);
                if (count > 0) {
                    int sub_total = count * type.score;
                    JLabel line = new JLabel(count + " × " + type.score + " = " + sub_total);
                    line.setFont(new Font("Monospaced", Font.PLAIN, 15));
                    line.setForeground(Color.WHITE);
                    line.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panel.add(line);
                    total_kills += count;
                }
            }
        }

        // 如果沒有明細 顯示簡單信息
        if (total_kills == 0) {
            JLabel simple_label = new JLabel("本關得分: " + score);
            simple_label.setFont(new Font("Arial", Font.PLAIN, 18));
            simple_label.setForeground(Color.WHITE);
            simple_label.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(simple_label);
        }

        panel.add(Box.createVerticalStrut(5));

        // 分隔線
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(300, 2));
        sep.setForeground(Color.GRAY);
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(sep);
        panel.add(Box.createVerticalStrut(8));

        // 總結
        JLabel enemy_label = new JLabel("enemy: 擊敗敵坦 " + total_kills + " -- total: " + score);
        enemy_label.setFont(new Font("Monospaced", Font.BOLD, 14));
        enemy_label.setForeground(Color.ORANGE);
        enemy_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(enemy_label);
        panel.add(Box.createVerticalStrut(5));

        JLabel total_label = new JLabel("累計總分: " + total_score);
        total_label.setFont(new Font("Arial", Font.PLAIN, 14));
        total_label.setForeground(Color.LIGHT_GRAY);
        total_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(total_label);
        panel.add(Box.createVerticalStrut(20));

        JButton btn = new JButton(is_last ? "查看結果" : "下一關");
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(new Color(50, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> dispose());
        panel.add(btn);

        add(panel, BorderLayout.CENTER);
    }
}