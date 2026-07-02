package ENGINE;

import java.sql.*;
import java.util.*;

// 數據庫管理器
public class Database_Manager {
    // 數據庫連接URL
    private final String db_url;
    private Connection connection;

    public Database_Manager(String dbPath) {
        this.db_url = "jdbc:sqlite:" + dbPath;
    }

    // 初始化數據庫連接並創建表
    public void initialize() {
        try {
            // 加載SQLite JDBC驅動
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(db_url);
            create_table();
            System.out.println("數據庫初始化成功: " + db_url);
        } catch (Exception e) {
            System.err.println("數據庫初始化失敗: " + e.getMessage());
        }
    }

    // 創建排行榜表（如果不存在）
    private void create_table() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS rankings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player_name TEXT NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "play_date TEXT DEFAULT (datetime('now','localtime'))" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    // 檢查分數是否能進入排行榜TOP10
    public boolean can_enter_ranking(int score) {
        String sql = "SELECT COUNT(*) FROM rankings";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count < 10) return true;  // 不滿10人直接入榜
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 檢查是否高於第十名
        sql = "SELECT MIN(score) FROM (SELECT score FROM rankings ORDER BY score DESC LIMIT 10)";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int minTop10 = rs.getInt(1);
                return score > minTop10;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    // 插入新的排行榜記錄
    public boolean insert_score(String player_name, int score) {
        if (!can_enter_ranking(score)) {
            return false;
        }

        String sql = "INSERT INTO rankings (player_name, score) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, player_name);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();

            // 只保留TOP10，刪除多餘記錄
            prune_rankings();
            System.out.println("分數已插入: " + player_name + " - " + score);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // 刪除超出TOP10的記錄
    private void prune_rankings() throws SQLException {
        String sql = "DELETE FROM rankings WHERE id NOT IN " +
                "(SELECT id FROM rankings ORDER BY score DESC LIMIT 10)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    // 獲取排行榜 TOP10
    public List<Map<String, Object>> get_top10() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT player_name, score, play_date FROM rankings ORDER BY score DESC LIMIT 10";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            int rank = 1;
            while (rs.next()) {
                Map<String, Object> record = new LinkedHashMap<>();
                record.put("rank", rank++);
                record.put("name", rs.getString("player_name"));
                record.put("score", rs.getInt("score"));
                record.put("date", rs.getString("play_date"));
                list.add(record);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // 獲取歷史最高分
    public int get_highest_score() {
        String sql = "SELECT MAX(score) FROM rankings";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}