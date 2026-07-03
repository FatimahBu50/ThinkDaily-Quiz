package ThinkDaily;

import java.sql.*;
import java.time.LocalDateTime;

public class DailyQuizService {

    // check if user finished today's daily quiz
    public static boolean hasCompletedToday(int userId) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String sql = """
            SELECT is_completed
            FROM user_daily_stats
            WHERE user_id = ? AND quiz_date = CURDATE()
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_completed");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in hasCompletedToday: " + e.getMessage());
        }
        return false;
    }

    // mark today's quiz as completed
    public static void markCompletedToday(int userId, int categoryId, int pointsEarnedToday) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        int hours = 24;

        String cfgSql = "SELECT daily_countdown_hours FROM quiz_config ORDER BY config_id LIMIT 1";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(cfgSql)) {
            if (rs.next()) {
                hours = rs.getInt("daily_countdown_hours");
            }
        } catch (SQLException e) {
            System.out.println("Error reading quiz_config: " + e.getMessage());
        }

        LocalDateTime nextQuiz = LocalDateTime.now().plusHours(hours);
        Timestamp nextTs = Timestamp.valueOf(nextQuiz);

        try {
            String checkSql = """
                SELECT COUNT(*) AS cnt
                FROM user_daily_stats
                WHERE user_id = ? AND quiz_date = CURDATE()
            """;
            int count = 0;
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) count = rs.getInt("cnt");
                }
            }

            if (count > 0) {
                String updateSql = """
                    UPDATE user_daily_stats
                    SET is_completed = 1,
                        daily_category_id = ?,
                        points_earned_today = ?,
                        next_quiz_available_at = ?
                    WHERE user_id = ? AND quiz_date = CURDATE()
                """;
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, categoryId);
                    ps.setInt(2, pointsEarnedToday);
                    ps.setTimestamp(3, nextTs);
                    ps.setInt(4, userId);
                    ps.executeUpdate();
                }
            } else {
                String insertSql = """
                    INSERT INTO user_daily_stats
                    (user_id, quiz_date, daily_category_id, is_completed,
                     points_earned_today, next_quiz_available_at)
                    VALUES (?, CURDATE(), ?, 1, ?, ?)
                """;
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setInt(1, userId);
                    ps.setInt(2, categoryId);
                    ps.setInt(3, pointsEarnedToday);
                    ps.setTimestamp(4, nextTs);
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.out.println("Error in markCompletedToday: " + e.getMessage());
        }
    }
}
