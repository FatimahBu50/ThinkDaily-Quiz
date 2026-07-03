package ThinkDaily;

import java.util.List;

public class QuestionModel {
    public int questionId;
    public String questionText;
    public String categoryName;
    public int categoryId;          
    public List<String> options;
    public List<Integer> answerIds;
    public int correctIndex;
    public String difficulty;
    public boolean active;

    public QuestionModel(
        int questionId,
        String questionText,
        String categoryName,
        List<String> options,
        int correctIndex,
        String difficulty,
        boolean active
    ) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.categoryName = categoryName;
        this.options = options;
        this.correctIndex = correctIndex;
        this.difficulty = difficulty;
        this.active = active;
    }
}
