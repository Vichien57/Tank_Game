package MODEL;

import COMMON.Constants;


// 關卡配置數據類
public class Level_Data {
    private final int stage_number;  // 關卡編號（1-8）
    private final int enemy_speed;   // 敵方坦克移動速度
    private final int enemy_count;   // 敵方坦克總數
    private final boolean has_boss;  // 是否有BOSS

    public Level_Data(int stage_number, int enemy_speed, int enemy_count, boolean has_boss) {
        this.stage_number = stage_number;
        this.enemy_speed = enemy_speed;
        this.enemy_count = enemy_count;
        this.has_boss = has_boss;
    }

    public int get_stage_number() {
        return stage_number;
    }

    public int get_enemy_speed() {
        return enemy_speed;
    }

    public int get_enemy_count() {
        return enemy_count;
    }

    public boolean has_boss() {
        return has_boss;
    }

    // 獲取標準8關配置
    public static Level_Data[] get_standard_levels() {
        return new Level_Data[]{
                new Level_Data(1, Constants.ENEMY_SPEED_SLOW, 20, false),
                new Level_Data(2, Constants.ENEMY_SPEED_SLOW, 20, false),
                new Level_Data(3, Constants.ENEMY_SPEED_SLOW, 20, true),
                new Level_Data(4, Constants.ENEMY_SPEED_MEDIUM, 25, false),
                new Level_Data(5, Constants.ENEMY_SPEED_MEDIUM, 25, false),
                new Level_Data(6, Constants.ENEMY_SPEED_MEDIUM, 25, true),
                new Level_Data(7, Constants.ENEMY_SPEED_FAST, 30, false),
                new Level_Data(8, Constants.ENEMY_SPEED_FAST, 30, true),
        };
    }
}