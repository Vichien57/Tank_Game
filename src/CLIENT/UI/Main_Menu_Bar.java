package CLIENT.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


// 頂部菜單欄
public class Main_Menu_Bar extends JMenuBar {
    private JMenu game_menu;
    private JMenu settings_menu;
    private JMenu help_menu;

    private JMenuItem new_game_item;
    private JMenuItem exit_item;
    private JMenuItem game_settings_item;
    private JMenuItem game_record_item;
    private JMenuItem controls_item;
    private JMenuItem about_item;

    public Main_Menu_Bar() {
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createEmptyBorder());

        // 遊戲菜單
        game_menu = create_menu("遊戲");
        new_game_item = create_menu_item("新遊戲");
        exit_item = create_menu_item("退出");
        game_menu.add(new_game_item);
        game_menu.addSeparator();
        game_menu.add(exit_item);
        add(game_menu);

        // 設置菜單
        settings_menu = create_menu("設置");
        game_settings_item = create_menu_item("遊戲設置");
        game_record_item = create_menu_item("遊戲紀錄");
        settings_menu.add(game_settings_item);
        settings_menu.add(game_record_item);
        add(settings_menu);

        // 幫助菜單
        help_menu = create_menu("幫助");
        controls_item = create_menu_item("操作說明");
        about_item = create_menu_item("關於");
        help_menu.add(controls_item);
        help_menu.add(about_item);
        add(help_menu);
    }

    private JMenu create_menu(String title) {
        JMenu menu = new JMenu(title);
        menu.setFont(new Font("Arial", Font.BOLD, 13));
        menu.setForeground(Color.WHITE);
        menu.setBackground(new Color(40, 40, 40));
        return menu;
    }

    private JMenuItem create_menu_item(String title) {
        JMenuItem item = new JMenuItem(title);
        item.setFont(new Font("Arial", Font.PLAIN, 13));
        item.setBackground(new Color(50, 50, 50));
        item.setForeground(Color.WHITE);
        return item;
    }

    // 事件綁定
    public void on_new_game(ActionListener listener) {
        new_game_item.addActionListener(listener);
    }

    public void on_exit(ActionListener listener) {
        exit_item.addActionListener(listener);
    }

    public void on_game_settings(ActionListener listener) {
        game_settings_item.addActionListener(listener);
    }

    public void on_game_record(ActionListener listener) {
        game_record_item.addActionListener(listener);
    }

    public void on_controls(ActionListener listener) {
        controls_item.addActionListener(listener);
    }

    public void on_about(ActionListener listener) {
        about_item.addActionListener(listener);
    }
}