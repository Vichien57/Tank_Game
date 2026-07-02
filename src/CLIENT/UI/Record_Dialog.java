package CLIENT.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;


public class Record_Dialog extends JDialog {
    public Record_Dialog(JFrame parent, List<Map<String, Object>> rankings) {
        super(parent, "遊戲排行榜 TOP 10", true);
        setSize(400, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        // 標題
        JLabel title_label = new JLabel("排行榜 TOP 10");
        title_label.setFont(new Font("Arial", Font.BOLD, 20));
        title_label.setForeground(Color.YELLOW);
        title_label.setHorizontalAlignment(SwingConstants.CENTER);
        title_label.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title_label, BorderLayout.NORTH);

        // 表格
        String[] columns = {"名次", "玩家", "總分", "日期"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        if (rankings != null) {
            for (Map<String, Object> record : rankings) {
                Object[] row = {
                        record.get("rank"),
                        record.get("name"),
                        record.get("score"),
                        record.get("date")
                };
                model.addRow(row);
            }
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setBackground(new Color(40, 40, 40));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.DARK_GRAY);
        table.getTableHeader().setBackground(new Color(50, 50, 50));
        table.getTableHeader().setForeground(Color.WHITE);

        // 設置列寬
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);

        JScrollPane scroll_pane = new JScrollPane(table);
        scroll_pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll_pane.getViewport().setBackground(new Color(30, 30, 30));
        add(scroll_pane, BorderLayout.CENTER);

        // 關閉按鈕
        JButton close_btn = new JButton("關閉");
        close_btn.setFont(new Font("Arial", Font.BOLD, 13));
        close_btn.setBackground(new Color(50, 50, 50));
        close_btn.setForeground(Color.WHITE);
        close_btn.setFocusPainted(false);
        close_btn.addActionListener(e -> dispose());
        JPanel btn_panel = new JPanel();
        btn_panel.setBackground(new Color(30, 30, 30));
        btn_panel.add(close_btn);
        add(btn_panel, BorderLayout.SOUTH);
    }
}