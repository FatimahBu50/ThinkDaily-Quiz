package ThinkDaily;

import java.util.List;

public class QuestionModel {
    public int questionId;
    public String questionText;
    public String categoryName;
    public List<String> options; // option texts
    public List<Integer> answerIds; // parallel list of answer ids from DB
    public int correctIndex; // 1-based index
    public String difficulty;
    public boolean isActive;

    public QuestionModel(int questionId, String questionText, String categoryName, List<String> options, int correctIndex, String difficulty, boolean isActive) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.categoryName = categoryName;
        this.options = options;
        this.correctIndex = correctIndex;
        this.difficulty = difficulty;
        this.isActive = isActive;
    }
}
