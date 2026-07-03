package ThinkDaily;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ThinkDailyQueries {

    private Connection conn;

    public ThinkDailyQueries() {
        conn = DBConnection.getConnection(); // تأكد أن DBConnection يعمل
    }

    // ----------------- Add Admin -----------------
    public boolean addAdmin(String username, String password, String fullName) {
        if (conn == null) conn = DBConnection.getConnection();

        try {
            // 1. Insert into accounts table
            String insertAccount = "INSERT INTO accounts " +
                    "(username, password_hash, role, security_question1, security_ans1, security_question2, security_ans2, is_active) " +
                    "VALUES (?, ?, 'ADMIN', 'Default Q1', 'Answer1', 'Default Q2', 'Answer2', TRUE)";

            PreparedStatement stmt = conn.prepareStatement(insertAccount, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));

            int rows = stmt.executeUpdate();

            if (rows == 0) return false;

            // get generated account_id
            var rs = stmt.getGeneratedKeys();
            int accountId = 0;
            if (rs.next()) accountId = rs.getInt(1);

            // 2. Insert into admins table
            String insertAdmin = "INSERT INTO admins (account_id, full_name) VALUES (?, ?)";
            PreparedStatement stmt2 = conn.prepareStatement(insertAdmin);
            stmt2.setInt(1, accountId);
            stmt2.setString(2, fullName);

            stmt2.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ----------------- Password Hashing -----------------
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    // ----------------- Add Player (account + user) -----------------
    public boolean addPlayer(String username, String password, String userDisplayName,
                             String securityQuestion1, String securityAns1,
                             String securityQuestion2, String securityAns2) {
        if (conn == null) conn = DBConnection.getConnection();

        try {
            String insertAccount = "INSERT INTO accounts " +
                    "(username, password_hash, role, security_question1, security_ans1, security_question2, security_ans2, is_active) " +
                    "VALUES (?, ?, 'PLAYER', ?, ?, ?, ?, TRUE)";

            PreparedStatement stmt = conn.prepareStatement(insertAccount, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, securityQuestion1);
            stmt.setString(4, securityAns1);
            stmt.setString(5, securityQuestion2);
            stmt.setString(6, securityAns2);

            int rows = stmt.executeUpdate();
            if (rows == 0) return false;

            ResultSet rs = stmt.getGeneratedKeys();
            int accountId = 0;
            if (rs.next()) accountId = rs.getInt(1);

            String insertUser = "INSERT INTO users (account_id, user_name, bio, current_level, current_points_total, current_icon_path) VALUES (?, ?, NULL, 1, 0, NULL)";
            PreparedStatement stmt2 = conn.prepareStatement(insertUser);
            stmt2.setInt(1, accountId);
            stmt2.setString(2, userDisplayName != null && !userDisplayName.isEmpty() ? userDisplayName : username);
            stmt2.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ----------------- Authenticate Player -----------------
    // Returns user_id if successful, otherwise -1
    public int authenticatePlayer(String username, String password) {
        if (conn == null) conn = DBConnection.getConnection();
        try {
            String q = "SELECT account_id, password_hash, role, is_active FROM accounts WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(q);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) return -1;

            String storedHash = rs.getString("password_hash");
            String role = rs.getString("role");
            boolean active = rs.getBoolean("is_active");
            if (!active) return -1;
            if (!"PLAYER".equals(role)) return -1;
            if (!storedHash.equals(hashPassword(password))) return -1;

            int accountId = rs.getInt("account_id");
            String q2 = "SELECT user_id FROM users WHERE account_id = ? LIMIT 1";
            PreparedStatement stmt2 = conn.prepareStatement(q2);
            stmt2.setInt(1, accountId);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) return rs2.getInt("user_id");
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // ----------------- Close Connection -----------------
    public void close() {
        DBConnection.closeConnection();
    }

    // ----------------- Questions & Answers CRUD -----------------
    public int getOrCreateCategoryId(String categoryName) throws SQLException {
        if (conn == null) conn = DBConnection.getConnection();
        String q = "SELECT category_id FROM categories WHERE category_name = ?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setString(1, categoryName);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("category_id");

        String ins = "INSERT INTO categories (category_name) VALUES (?)";
        PreparedStatement ps2 = conn.prepareStatement(ins, PreparedStatement.RETURN_GENERATED_KEYS);
        ps2.setString(1, categoryName);
        int rows = ps2.executeUpdate();
        if (rows == 0) return -1;
        ResultSet gk = ps2.getGeneratedKeys();
        if (gk.next()) return gk.getInt(1);
        return -1;
    }

    public List<QuestionModel> getAllQuestions() throws SQLException {
        if (conn == null) conn = DBConnection.getConnection();
        List<QuestionModel> out = new ArrayList<>();
        String q = "SELECT q.question_id, q.question_text, c.category_name, q.difficulty, q.is_active " +
                "FROM questions q LEFT JOIN categories c ON q.category_id = c.category_id";
        PreparedStatement ps = conn.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int qid = rs.getInt("question_id");
            String text = rs.getString("question_text");
            String cat = rs.getString("category_name");
            String diff = rs.getString("difficulty");
            boolean active = rs.getBoolean("is_active");
            // fetch answers
            List<String> options = new ArrayList<>();
            int correctIndex = 1;
            String aQ = "SELECT answer_text, is_correct FROM answers WHERE question_id = ? ORDER BY answer_id";
            PreparedStatement psA = conn.prepareStatement(aQ);
            psA.setInt(1, qid);
            ResultSet rsa = psA.executeQuery();
            int idx = 0;
            while (rsa.next()) {
                idx++;
                options.add(rsa.getString("answer_text"));
                if (rsa.getBoolean("is_correct")) correctIndex = idx;
            }
            // ensure 3 options
            while (options.size() < 3) options.add("");

            out.add(new QuestionModel(qid, text, cat != null ? cat : "", options, correctIndex, diff != null ? diff : "MEDIUM", active));
        }
        return out;
    }

    public int addQuestion(String questionText, String categoryName, List<String> options, int correctIndex, String difficulty) throws SQLException {
        if (conn == null) conn = DBConnection.getConnection();
        int categoryId = getOrCreateCategoryId(categoryName);
        if (categoryId <= 0) throw new SQLException("Could not create/find category");

        String insQ = "INSERT INTO questions (category_id, question_text, difficulty, is_active, times_answered, times_answered_correctly) VALUES (?, ?, ?, TRUE, 0, 0)";
        PreparedStatement ps = conn.prepareStatement(insQ, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setInt(1, categoryId);
        ps.setString(2, questionText);
        ps.setString(3, difficulty != null ? difficulty : "MEDIUM");
        int rows = ps.executeUpdate();
        if (rows == 0) return -1;
        ResultSet gk = ps.getGeneratedKeys();
        int qid = -1;
        if (gk.next()) qid = gk.getInt(1);

        String insA = "INSERT INTO answers (question_id, answer_text, is_correct) VALUES (?, ?, ?)";
        for (int i = 0; i < options.size() && i < 3; i++) {
            PreparedStatement psa = conn.prepareStatement(insA);
            psa.setInt(1, qid);
            psa.setString(2, options.get(i));
            psa.setBoolean(3, (i + 1) == correctIndex);
            psa.executeUpdate();
        }
        return qid;
    }

    public boolean updateQuestion(int questionId, String questionText, String categoryName, List<String> options, int correctIndex, String difficulty, boolean isActive) throws SQLException {
        if (conn == null) conn = DBConnection.getConnection();
        int categoryId = getOrCreateCategoryId(categoryName);
        if (categoryId <= 0) throw new SQLException("Could not create/find category");

        String upQ = "UPDATE questions SET question_text = ?, category_id = ?, difficulty = ?, is_active = ? WHERE question_id = ?";
        PreparedStatement ps = conn.prepareStatement(upQ);
        ps.setString(1, questionText);
        ps.setInt(2, categoryId);
        ps.setString(3, difficulty != null ? difficulty : "MEDIUM");
        ps.setBoolean(4, isActive);
        ps.setInt(5, questionId);
        ps.executeUpdate();

        // replace answers: delete then insert
        String delA = "DELETE FROM answers WHERE question_id = ?";
        PreparedStatement psd = conn.prepareStatement(delA);
        psd.setInt(1, questionId);
        psd.executeUpdate();

        String insA = "INSERT INTO answers (question_id, answer_text, is_correct) VALUES (?, ?, ?)";
        for (int i = 0; i < options.size() && i < 3; i++) {
            PreparedStatement psa = conn.prepareStatement(insA);
            psa.setInt(1, questionId);
            psa.setString(2, options.get(i));
            psa.setBoolean(3, (i + 1) == correctIndex);
            psa.executeUpdate();
        }
        return true;
    }

    public boolean deleteQuestion(int questionId) throws SQLException {
        if (conn == null) conn = DBConnection.getConnection();
        String delA = "DELETE FROM answers WHERE question_id = ?";
        PreparedStatement psd = conn.prepareStatement(delA);
        psd.setInt(1, questionId);
        psd.executeUpdate();

        String delQ = "DELETE FROM questions WHERE question_id = ?";
        PreparedStatement psq = conn.prepareStatement(delQ);
        psq.setInt(1, questionId);
        psq.executeUpdate();
        return true;
    }

    // ----------------- Play: fetch questions for quiz -----------------
    public List<QuestionModel> getQuestionsForPlay(String categoryName, String difficulty, int limit) throws SQLException {
        if (conn == null) conn = DBConnection.getConnection();
        List<QuestionModel> out = new ArrayList<>();

        String q;
        if (categoryName == null || categoryName.isBlank() || "Random".equalsIgnoreCase(categoryName)) {
            q = "SELECT question_id, question_text, difficulty FROM questions WHERE is_active = TRUE" +
                    (difficulty != null && !difficulty.isBlank() ? " AND difficulty = ?" : "") +
                    " ORDER BY RAND() LIMIT ?";
        } else {
            q = "SELECT q.question_id, q.question_text, q.difficulty FROM questions q JOIN categories c ON q.category_id = c.category_id " +
                    "WHERE q.is_active = TRUE AND c.category_name = ?" +
                    (difficulty != null && !difficulty.isBlank() ? " AND q.difficulty = ?" : "") +
                    " ORDER BY RAND() LIMIT ?";
        }

        PreparedStatement ps = conn.prepareStatement(q);
        int idx = 1;
        if (categoryName == null || categoryName.isBlank() || "Random".equalsIgnoreCase(categoryName)) {
            if (difficulty != null && !difficulty.isBlank()) ps.setString(idx++, difficulty);
            ps.setInt(idx++, limit);
        } else {
            ps.setString(idx++, categoryName);
            if (difficulty != null && !difficulty.isBlank()) ps.setString(idx++, difficulty);
            ps.setInt(idx++, limit);
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int qid = rs.getInt("question_id");
            String text = rs.getString("question_text");
            String diff = rs.getString("difficulty");

            // fetch answers with their ids
            List<String> options = new ArrayList<>();
            List<Integer> answerIds = new ArrayList<>();
            int correctIndex = 1;
            String aQ = "SELECT answer_id, answer_text, is_correct FROM answers WHERE question_id = ? ORDER BY answer_id";
            PreparedStatement psA = conn.prepareStatement(aQ);
            psA.setInt(1, qid);
            ResultSet rsa = psA.executeQuery();
            int aidx = 0;
            while (rsa.next()) {
                aidx++;
                answerIds.add(rsa.getInt("answer_id"));
                options.add(rsa.getString("answer_text"));
                if (rsa.getBoolean("is_correct")) correctIndex = aidx;
            }
            while (options.size() < 3) { options.add(""); answerIds.add(-1); }

            QuestionModel qm = new QuestionModel(qid, text, "", options, correctIndex, diff != null ? diff : "MEDIUM", true);
            qm.answerIds = answerIds;
            out.add(qm);
        }
        return out;
    }

    // ----------------- Extended session + responses -----------------
    public int createExtendedSession(int userId, String categoryName, String difficulty, int requestedCount, int durationSeconds) throws SQLException {
        if (conn == null) conn = DBConnection.getConnection();
        Integer categoryId = null;
        if (categoryName != null && !categoryName.isBlank() && !"Random".equalsIgnoreCase(categoryName)) {
            String q = "SELECT category_id FROM categories WHERE category_name = ?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, categoryName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) categoryId = rs.getInt("category_id");
        }

        String ins = "INSERT INTO extended_sessions (user_id, category_id, difficulty, requested_question_count, duration, total_points_earned, was_completed) VALUES (?, ?, ?, ?, ?, 0, FALSE)";
        PreparedStatement ps2 = conn.prepareStatement(ins, PreparedStatement.RETURN_GENERATED_KEYS);
        ps2.setInt(1, userId);
        if (categoryId != null) ps2.setInt(2, categoryId); else ps2.setNull(2, java.sql.Types.INTEGER);
        ps2.setString(3, difficulty != null ? difficulty : "MEDIUM");
        ps2.setInt(4, requestedCount);
        ps2.setInt(5, durationSeconds);
        int rows = ps2.executeUpdate();
        if (rows == 0) return -1;
        ResultSet gk = ps2.getGeneratedKeys();
        if (gk.next()) return gk.getInt(1);
        return -1;
    }

    public boolean updateExtendedSession(int sessionId, int durationSeconds, int totalPoints, boolean wasCompleted) throws SQLException {
        if (conn == null) conn = DBConnection.getConnection();
        String up = "UPDATE extended_sessions SET duration = ?, total_points_earned = ?, was_completed = ? WHERE session_id = ?";
        PreparedStatement ps = conn.prepareStatement(up);
        ps.setInt(1, durationSeconds);
        ps.setInt(2, totalPoints);
        ps.setBoolean(3, wasCompleted);
        ps.setInt(4, sessionId);
        ps.executeUpdate();
        return true;
    }

    public boolean saveUserResponse(int userId, int questionId, int selectedAnswerId, boolean isCorrect, int pointsAwarded, java.sql.Timestamp answeredAt, String mode, Integer sessionId) throws SQLException {
        if (conn == null) conn = DBConnection.getConnection();
        String ins = "INSERT INTO user_responses (user_id, question_id, selected_answer_id, is_correct, points_awarded, answered_at, mode, session_id, quiz_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(ins);
        ps.setInt(1, userId);
        ps.setInt(2, questionId);
        ps.setInt(3, selectedAnswerId);
        ps.setBoolean(4, isCorrect);
        ps.setInt(5, pointsAwarded);
        ps.setTimestamp(6, answeredAt);
        ps.setString(7, mode);
        if (sessionId != null) ps.setInt(8, sessionId); else ps.setNull(8, java.sql.Types.INTEGER);
        ps.setDate(9, new java.sql.Date(System.currentTimeMillis()));
        ps.executeUpdate();
        return true;
    }

    public boolean addPointsToUser(int userId, int points) throws SQLException {
        if (conn == null) conn = DBConnection.getConnection();
        String up = "UPDATE users SET current_points_total = current_points_total + ? WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(up);
        ps.setInt(1, points);
        ps.setInt(2, userId);
        ps.executeUpdate();
        return true;
    }
}
