package ThinkDaily;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserIconSession {

    private static int currentUserId = -1;
    private static String iconPath = null;  

    /**
     * Set the icon for a user and save it permanently in the database
     * @param userId the ID of the user
     * @param fileName the path or filename of the selected icon
     */
    public static void setIcon(int userId, String fileName) {
        currentUserId = userId;
        iconPath = fileName;

        // Save in the database
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "UPDATE users SET current_icon_path = ? WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, fileName);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                }
            } else {
                System.out.println("Database connection is null. Icon not saved.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the icon for a user. If the icon is already loaded in memory, return it.
     * Otherwise, fetch it from the database.
     * @param userId the ID of the user
     * @return the icon path, or null if not set
     */
    public static String getIcon(int userId) {
        // If the requested user matches the cached one, return the cached path
        if (userId == currentUserId && iconPath != null) {
            return iconPath;
        }

        // Otherwise, fetch from database
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "SELECT current_icon_path FROM users WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            iconPath = rs.getString("current_icon_path");
                            currentUserId = userId;
                            return iconPath;
                        }
                    }
                }
            } else {
                System.out.println("Database connection is null. Cannot fetch icon.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

