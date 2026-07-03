package ThinkDaily;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserStats {

    private final int userId;
    private final String username;
    private final int totalPoints;
    private final int totalAnswered;
    private final int totalCorrect;

    public UserStats(int userId, String username,
                     int totalPoints, int totalAnswered, int totalCorrect) {
        this.userId = userId;
        this.username = username;
        this.totalPoints = totalPoints;
        this.totalAnswered = totalAnswered;
        this.totalCorrect = totalCorrect;
    }

    // ================== GENERATE ==================
    public static UserStats[] generate(Connection conn) {
        if (conn == null) return new UserStats[0];

        List<UserStats> list = new ArrayList<>();

        try {
            String sql = """
                SELECT
                    u.user_id,
                    a.username,
                    u.current_points_total AS total_points,
                    COUNT(ur.response_id) AS total_answered,
                    SUM(CASE WHEN ur.is_correct = 1 THEN 1 ELSE 0 END) AS total_correct
                FROM users u
                JOIN accounts a ON u.account_id = a.account_id
                LEFT JOIN user_responses ur ON u.user_id = ur.user_id
                WHERE a.role = 'PLAYER'
                GROUP BY u.user_id, a.username, u.current_points_total
                ORDER BY u.current_points_total DESC
            """;

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    list.add(new UserStats(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getInt("total_points"),
                            rs.getInt("total_answered"),
                            rs.getInt("total_correct")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("UserStats.generate error: " + e.getMessage());
        }

        return list.toArray(new UserStats[0]);
    }

    // ================== GETTERS ==================
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public int getTotalPoints() { return totalPoints; }
    public int getTotalAnswered() { return totalAnswered; }
    public int getTotalCorrect() { return totalCorrect; }
}

