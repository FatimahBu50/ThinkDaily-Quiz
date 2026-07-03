package ThinkDaily;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionStats {

    private final int questionId;
    private final String questionText;
    private final int timesAnswered;
    private final int timesCorrect;

    public QuestionStats(int questionId, String questionText, int timesAnswered, int timesCorrect) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.timesAnswered = timesAnswered;
        this.timesCorrect = timesCorrect;
    }

   public static QuestionStats[] generate(Connection conn) {
    if (conn == null) return new QuestionStats[0];

    List<QuestionStats> list = new ArrayList<>();

    try {
        String sql = """
            SELECT
                q.question_id,
                q.question_text,
                COUNT(ur.response_id) AS times_answered,
                SUM(CASE WHEN ur.is_correct = 1 THEN 1 ELSE 0 END) AS times_correct
            FROM questions q
            LEFT JOIN user_responses ur
                ON q.question_id = ur.question_id
            WHERE q.is_active = 1
            GROUP BY q.question_id, q.question_text
            ORDER BY times_answered DESC, times_correct DESC
        """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new QuestionStats(
                        rs.getInt("question_id"),
                        rs.getString("question_text"),
                        rs.getInt("times_answered"),
                        rs.getInt("times_correct")
                ));
            }
        }
    } catch (SQLException e) {
        System.out.println("QuestionStats.generate error: " + e.getMessage());
    }

    return list.toArray(new QuestionStats[0]);
}


    public int getQuestionId() { return questionId; }
    public String getQuestionText() { return questionText; }
    public int getTimesAnswered() { return timesAnswered; }
    public int getTimesCorrect() { return timesCorrect; }
}
