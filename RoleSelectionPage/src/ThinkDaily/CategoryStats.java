package ThinkDaily;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryStats {

    private final int categoryId;
    private final String categoryName;
    private final int questionCount;
    private final double questionPercent;

    public CategoryStats(int categoryId, String categoryName,
                         int questionCount, double questionPercent) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.questionCount = questionCount;
        this.questionPercent = questionPercent;
    }

    public static CategoryStats[] generate(Connection conn) {
        if (conn == null) return new CategoryStats[0];

        List<Row> raw = new ArrayList<>();
        int totalQuestionsAll = 0;

        try {
            String sql = """
                SELECT 
                    c.category_id,
                    c.category_name,
                    COUNT(q.question_id) AS question_count
                FROM categories c
                LEFT JOIN questions q 
                    ON c.category_id = q.category_id 
                    AND q.is_active = TRUE
                GROUP BY c.category_id, c.category_name
                ORDER BY c.category_name ASC
            """;

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    int id = rs.getInt("category_id");
                    String name = rs.getString("category_name");
                    int qCount = rs.getInt("question_count");

                    raw.add(new Row(id, name, qCount));
                    totalQuestionsAll += qCount;
                }
            }

        } catch (SQLException e) {
            System.out.println("CategoryStats.generate error: " + e.getMessage());
        }

        CategoryStats[] out = new CategoryStats[raw.size()];
        for (int i = 0; i < raw.size(); i++) {
            Row r = raw.get(i);

            double qPct = totalQuestionsAll > 0 ? (r.qCount * 100.0 / totalQuestionsAll) : 0.0;
            out[i] = new CategoryStats(r.id, r.name, r.qCount, qPct);
        }
        return out;
    }

    private static class Row {
        final int id; final String name; final int qCount;
        Row(int id, String name, int qCount) {
            this.id = id; this.name = name; this.qCount = qCount;
        }
    }

    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public int getQuestionCount() { return questionCount; }
    public double getQuestionPercent() { return questionPercent; }
}
