package ThinkDaily;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import ThinkDaily.ThinkDailyQueries;
import ThinkDaily.QuestionModel;

public class ExtendedQuizPage extends JFrame {

    private final int userId;
    private final String username;
    private final String categoryName;
    /** Max questions user requested (capped at 20) */
    private final int requestedQuestionCount;

    // questions for this extended session
    private List<Question> questions = new ArrayList<Question>();
    private int currentIndex = 0;
    private final Map<Integer, Integer> selectedAnswerByQuestionId =
            new HashMap<Integer, Integer>();
    private ThinkDailyQueries queries;
    private Integer sessionId = null;

    // UI
    private JLabel questionCounterLabel, categoryLabel, difficultyLabel, timerLabel;
    private JTextArea questionTextArea;
    private JPanel answersPanel;
    private JButton nextButton;

    // Timer
    private javax.swing.Timer quizTimer;
    private int elapsedSeconds = 0;

    // Track selected
    private AnswerButton selectedButton = null;

    public ExtendedQuizPage(int userId,
                            String username,
                            String categoryName,
                            int requestedQuestionCount) {

        this.userId = userId;
        this.username = (username != null && !username.isEmpty()) ? username : "Player";
        this.categoryName = (categoryName != null && !categoryName.isEmpty())
                ? categoryName
                : "Random";
        // cap to 20
        this.requestedQuestionCount = Math.min(requestedQuestionCount <= 0 ? 20 : requestedQuestionCount, 20);

        setTitle("ThinkDaily - Extended Quiz");
        setSize(900, 630);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== BACKGROUND GRADIENT =====
        JPanel bg = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 240, 247),
                        getWidth(), getHeight(), new Color(235, 220, 255)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bg.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(bg);

        RoundedPanel card = new RoundedPanel(30);
        card.setPreferredSize(new Dimension(700, 470));
        card.setLayout(new BorderLayout(0, 15));
        card.setOpaque(false);

        bg.add(card);

        // ================= TOP BAR =================
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(6, 18, 6, 18)); // give space from corners

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftTop.setOpaque(false);

        questionCounterLabel = new JLabel("Question 1 / 20");
        questionCounterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        questionCounterLabel.setForeground(new Color(80, 70, 95));

        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        timerLabel.setForeground(new Color(80, 70, 95));

        leftTop.add(questionCounterLabel);
        leftTop.add(timerLabel);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);

        categoryLabel = new JLabel("Category: " + this.categoryName);
        difficultyLabel = new JLabel("Difficulty: -");
        categoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        difficultyLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        categoryLabel.setForeground(new Color(120, 110, 140));
        difficultyLabel.setForeground(new Color(120, 110, 140));

        rightTop.add(categoryLabel);
        rightTop.add(difficultyLabel);

        top.add(leftTop, BorderLayout.WEST);
        top.add(rightTop, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);

        // ================= CENTER: QUESTION + ANSWERS =================
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(30, 40, 20, 40));

        questionTextArea = new JTextArea();
        questionTextArea.setFont(new Font("SansSerif", Font.BOLD, 20));
        questionTextArea.setForeground(new Color(70, 60, 95));
        questionTextArea.setWrapStyleWord(true);
        questionTextArea.setLineWrap(true);
        questionTextArea.setEditable(false);
        questionTextArea.setOpaque(false);
        questionTextArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        center.add(questionTextArea);
        center.add(Box.createVerticalStrut(20));

        answersPanel = new JPanel();
        answersPanel.setOpaque(false);
        answersPanel.setLayout(new BoxLayout(answersPanel, BoxLayout.Y_AXIS));
        answersPanel.setAlignmentX(CENTER_ALIGNMENT);

        center.add(answersPanel);

        card.add(center, BorderLayout.CENTER);

        // ================= BOTTOM BUTTONS =================
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(0, 18, 6, 18));

        JButton backButton = new JButton("⟵ Back");
        backButton.setBackground(new Color(245, 230, 245));
        backButton.setForeground(new Color(90, 70, 100));
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            quizTimer.stop();
            // back to setup, or just home for now
            new PlayerHomePage(userId).setVisible(true);
            dispose();
        });

        nextButton = new JButton("Next ➜");
        nextButton.setBackground(new Color(255, 200, 220));
        nextButton.setForeground(new Color(80, 60, 80));
        nextButton.setBorder(BorderFactory.createEmptyBorder(10, 26, 10, 26));
        nextButton.setFocusPainted(false);
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextButton.addActionListener(this::handleNext);

        bottom.add(backButton, BorderLayout.WEST);
        bottom.add(nextButton, BorderLayout.EAST);

        card.add(bottom, BorderLayout.SOUTH);

        // ================= TIMER =================
        quizTimer = new javax.swing.Timer(1000, e -> {
            elapsedSeconds++;
            timerLabel.setText(String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60));
        });
        quizTimer.start();

        // ================= CREATE SESSION + LOAD QUESTIONS =================
        queries = new ThinkDailyQueries();
        try {
            sessionId = queries.createExtendedSession(userId, this.categoryName, null, this.requestedQuestionCount, 0);
            java.util.List<QuestionModel> qms = queries.getQuestionsForPlay(this.categoryName, null, this.requestedQuestionCount);
            // convert QuestionModel to local Question objects
            questions.clear();
            for (QuestionModel qm : qms) {
                Question q = new Question(qm.questionId, qm.questionText, qm.categoryName, qm.difficulty);
                q.answers.clear();
                for (int i = 0; i < qm.options.size(); i++) {
                    int aid = (qm.answerIds != null && i < qm.answerIds.size()) ? qm.answerIds.get(i) : -1;
                    q.answers.add(new Answer(aid, qm.options.get(i)));
                }
                // set correctAnswerId to the DB answer id if available
                if (qm.answerIds != null && qm.correctIndex > 0 && qm.correctIndex <= qm.answerIds.size()) {
                    q.correctAnswerId = qm.answerIds.get(qm.correctIndex - 1);
                }
                questions.add(q);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (questions.isEmpty()) {
            showNoQuestionsUI();
        } else {
            showQuestion(0);
        }
    }

    // ⭐ Answer Button Component (same style as DailyQuizPage) ⭐
    private class AnswerButton extends JPanel {
        public final Answer answer;
        private boolean selected = false;

        JLabel text;

        public AnswerButton(Answer ans) {
            this.answer = ans;

            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(420, 48));
            setMaximumSize(new Dimension(420, 48));
            setAlignmentX(CENTER_ALIGNMENT);

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBackground(new Color(255, 240, 250));
            setBorder(BorderFactory.createLineBorder(new Color(230, 180, 200), 2, true));

            text = new JLabel(ans.answerText, SwingConstants.CENTER);
            text.setFont(new Font("SansSerif", Font.PLAIN, 15));
            text.setForeground(new Color(80, 60, 95));
            text.setVerticalAlignment(SwingConstants.CENTER);
            add(text, BorderLayout.CENTER);

            setBorder(new EmptyBorder(6, 15, 6, 15));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    selectThis();
                }

                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (!selected)
                        setBackground(new Color(255, 225, 240));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (!selected)
                        setBackground(new Color(255, 240, 250));
                }
            });
        }

        void selectThis() {
            if (selectedButton != null) {
                selectedButton.selected = false;
                selectedButton.setBackground(new Color(255, 240, 250));
                selectedButton.setBorder(
                        BorderFactory.createLineBorder(new Color(230, 180, 200), 2, true));
            }

            selected = true;
            selectedButton = this;

            setBackground(new Color(240, 200, 220));
            setBorder(BorderFactory.createLineBorder(new Color(200, 90, 150), 3, true));
        }
    }

    // ================= SHOW QUESTION =================
    private void showQuestion(int index) {
    Question q = questions.get(index);

    questionCounterLabel.setText("Question " + (index + 1) + "/" + questions.size());

    // Always show the chosen category from the setup/home page
    if ("Random".equalsIgnoreCase(this.categoryName)) {
        categoryLabel.setText("Category: Random (mixed)");
    } else {
        categoryLabel.setText("Category: " + this.categoryName);
    }

    difficultyLabel.setText("Difficulty: " + q.difficulty);
    questionTextArea.setText(q.questionText);

    answersPanel.removeAll();
    selectedButton = null;

    for (Answer a : q.answers) {
        AnswerButton btn = new AnswerButton(a);
        answersPanel.add(btn);
        answersPanel.add(Box.createVerticalStrut(10));
    }

    nextButton.setText(index == questions.size() - 1 ? "Submit " : "Next ➜");

    answersPanel.revalidate();
    answersPanel.repaint();
}


    private void handleNext(ActionEvent e) {
        if (selectedButton == null) {
            JOptionPane.showMessageDialog(this, "Please choose an answer 💡");
            return;
        }

        Question q = questions.get(currentIndex);
        selectedAnswerByQuestionId.put(q.questionId, selectedButton.answer.answerId);

        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            showQuestion(currentIndex);
       } else {
    quizTimer.stop();

    int totalQuestions = questions.size();
    int correctCount = calculateCorrectCount();    // ✅ real correct
    int sessionPoints = 0;

    // Save each response to DB
    if (queries != null) {
        for (Question qq : questions) {
            Integer selectedAns = selectedAnswerByQuestionId.get(qq.questionId);
            int sel = (selectedAns != null) ? selectedAns : -1;
            boolean isCorrect = (sel != -1 && sel == qq.correctAnswerId);
            int pts = isCorrect ? 10 : 0;
            sessionPoints += pts;
            try {
                queries.saveUserResponse(userId, qq.questionId, sel, isCorrect, pts, new java.sql.Timestamp(System.currentTimeMillis()), "EXTENDED", sessionId);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        // award total points to user
        try {
            queries.addPointsToUser(userId, sessionPoints);
            if (sessionId != null) {
                queries.updateExtendedSession(sessionId, elapsedSeconds, sessionPoints, true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    } else {
        // fallback to simple calculation
        sessionPoints = correctCount * 10;
    }

    long timeMillis = elapsedSeconds * 1000L;

    new QuizResultPage(
            userId,
            username,
            sessionPoints,
            correctCount,
            totalQuestions,
            timeMillis
    ).setVisible(true);

    dispose();
}


    }

    private void showNoQuestionsUI() {
        questionCounterLabel.setText("No questions");
        difficultyLabel.setText("Difficulty: -");
        questionTextArea.setText(
                "No questions are available for this extended session.\n" +
                "Please try again later or pick another category."
        );
        answersPanel.removeAll();
        answersPanel.revalidate();
        answersPanel.repaint();
        nextButton.setEnabled(false);
    }

    // ================= LOAD QUESTIONS (DEMO – LIMIT 20) =================
    // ================= LOAD QUESTIONS (DEMO – LIMIT 20) =================
private void loadExtendedQuiz() {
    // TODO: replace this demo list with real DB query by category
    List<Question> all = new ArrayList<Question>();

    // ===== MATH (Extended-only demo) =====
    all.add(makeQuestion(1, "What is 9 × 7?", "Math", "Easy",
            "63", "56", "72", "49"));

    all.add(makeQuestion(2, "What is the square root of 81?", "Math", "Easy",
            "9", "8", "7", "6"));

    all.add(makeQuestion(3, "Which of these numbers is a prime number?", "Math", "Medium",
            "13", "21", "33", "39"));

    all.add(makeQuestion(4, "What is 25% of 80?", "Math", "Medium",
            "20", "15", "25", "18"));

    // ===== SCIENCE (Extended-only demo) =====
    all.add(makeQuestion(5, "What is the chemical symbol for water?", "Science", "Easy",
            "H₂O", "O₂", "CO₂", "NaCl"));

    all.add(makeQuestion(6, "Which part of the plant absorbs water from the soil?", "Science", "Easy",
            "Roots", "Leaves", "Stem", "Flowers"));

    all.add(makeQuestion(7, "What force pulls objects toward the center of the Earth?", "Science", "Medium",
            "Gravity", "Magnetism", "Friction", "Electricity"));

    all.add(makeQuestion(8, "Which organ in the human body pumps blood?", "Science", "Easy",
            "Heart", "Lungs", "Brain", "Liver"));

    // ===== HISTORY (Extended-only demo) =====
    all.add(makeQuestion(9, "Who was the first president of the United States?", "History", "Easy",
            "George Washington", "Abraham Lincoln", "John Adams", "Thomas Jefferson"));

    all.add(makeQuestion(10, "In which year did World War II end?", "History", "Medium",
            "1945", "1939", "1918", "1960"));

    all.add(makeQuestion(11, "The pyramids of Giza are located in which country?", "History", "Easy",
            "Egypt", "Greece", "Mexico", "India"));

    // ===== TECHNOLOGY (Extended-only demo) =====
    all.add(makeQuestion(12, "What does HTML stand for?", "Technology", "Medium",
            "HyperText Markup Language",
            "HighText Machine Language",
            "Hyperlink and Text Management Language",
            "Home Tool Markup Language"));

    all.add(makeQuestion(13, "Which device is used to store data permanently?", "Technology", "Easy",
            "Hard disk", "RAM", "Cache", "Register"));

    all.add(makeQuestion(14, "Which company created the Android operating system?", "Technology", "Medium",
            "Google", "Microsoft", "Apple", "IBM"));

    // ===== GEOGRAPHY / GENERAL (for Random mix only if you want) =====
    all.add(makeQuestion(15, "Which continent is Brazil located in?", "Geography", "Easy",
            "South America", "Europe", "Asia", "Africa"));

    all.add(makeQuestion(16, "Which ocean is the largest?", "Geography", "Medium",
            "Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean"));

    // apply category filter only if not Random (very simple demo filter)
    List<Question> filtered = new ArrayList<Question>();
    if ("Random".equalsIgnoreCase(categoryName)) {
        filtered.addAll(all);
    } else {
        for (Question q : all) {
            if (q.categoryName.equalsIgnoreCase(categoryName)) {
                filtered.add(q);
            }
        }
        // no fallback to "all" → if category has 0 questions, show "no questions" message
    }

    // limit to requested count (max 20)
    int limit = Math.min(requestedQuestionCount, filtered.size());
    questions = new ArrayList<Question>(filtered.subList(0, limit));
}

   
    // helper to build a question quickly
private Question makeQuestion(int id, String text, String cat, String diff,
                              String a1, String a2, String a3, String a4) {
    Question q = new Question(id, text, cat, diff);

    int correctId = id * 10 + 1;      // first answer id
    q.answers.add(new Answer(correctId, a1));
    q.answers.add(new Answer(id * 10 + 2, a2));
    q.answers.add(new Answer(id * 10 + 3, a3));
    q.answers.add(new Answer(id * 10 + 4, a4));

    q.correctAnswerId = correctId;    // mark correct answer

    return q;
}


    // ================= MODEL CLASSES =================
    private static class Question {
        int questionId;
        String questionText, categoryName, difficulty;
        List<Answer> answers = new ArrayList<Answer>();
        int correctAnswerId;

        Question(int id, String t, String c, String d) {
            questionId = id;
            questionText = t;
            categoryName = c;
            difficulty = d;
        }
    }

    private static class Answer {
        int answerId;
        String answerText;

        Answer(int id, String t) {
            answerId = id;
            answerText = t;
        }
    }
private int calculateCorrectCount() {
    int correct = 0;
    for (Question q : questions) {
        Integer selectedId = selectedAnswerByQuestionId.get(q.questionId);
        if (selectedId != null && selectedId == q.correctAnswerId) {
            correct++;
        }
    }
    return correct;
}

    // ================= ROUNDED PANEL =================
    private static class RoundedPanel extends JPanel {
        int radius;

        RoundedPanel(int r) {
            radius = r;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 250, 255));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.setColor(new Color(245, 200, 230));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}