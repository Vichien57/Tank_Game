package CLIENT;

import COMMON.Constants.Direction;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;


// 鍵盤輸入處理器
public class Input_Handler extends KeyAdapter {
    private final Set<Integer> pressed_keys;  // 當前按下的按鍵集合
    private final Game_Panel game_panel;      // 遊戲面板回調

    public Input_Handler(Game_Panel game_panel) {
        this.game_panel = game_panel;
        this.pressed_keys = new HashSet<>();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressed_keys.add(e.getKeyCode());
        handle_input(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressed_keys.remove(e.getKeyCode());
    }

    // 處理按鍵輸入
    private void handle_input(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W:
                game_panel.on_player_move(0, Direction.UP);
                break;
            case KeyEvent.VK_S:
                game_panel.on_player_move(0, Direction.DOWN);
                break;
            case KeyEvent.VK_A:
                game_panel.on_player_move(0, Direction.LEFT);
                break;
            case KeyEvent.VK_D:
                game_panel.on_player_move(0, Direction.RIGHT);
                break;
            case KeyEvent.VK_J:
                game_panel.on_player_shoot(0);
                break;
        }

        if (game_panel.is_double_mode()) {
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    game_panel.on_player_move(1, Direction.UP);
                    break;
                case KeyEvent.VK_DOWN:
                    game_panel.on_player_move(1, Direction.DOWN);
                    break;
                case KeyEvent.VK_LEFT:
                    game_panel.on_player_move(1, Direction.LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
                    game_panel.on_player_move(1, Direction.RIGHT);
                    break;
                case KeyEvent.VK_ENTER:
                    game_panel.on_player_shoot(1);
                    break;
            }
        }

        // 全局快捷鍵
        if (keyCode == KeyEvent.VK_P) {
            game_panel.toggle_pause();
        }
    }

    // 檢查按鍵是否被按下
    public boolean is_key_pressed(int keyCode) {
        return pressed_keys.contains(keyCode);
    }

    // 清空按鍵狀態
    public void clear_keys() {
        pressed_keys.clear();
    }
}