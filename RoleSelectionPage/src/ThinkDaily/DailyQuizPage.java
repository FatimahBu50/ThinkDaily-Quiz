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
 import java.util.Collections;
import java.util.Map;
import java.sql.SQLException;

public class DailyQuizPage extends JFrame {

    private final int userId;
    private List<Question> questions = new ArrayList<Question>();
    private int currentIndex = 0;
    private final Map<Integer, Integer> selectedAnswerByQuestionId = new HashMap<Integer, Integer>();
    private ThinkDailyQueries queries;

    // ➕ for user_daily_stats.daily_category_id (e.g. 1 = Math)
    private int dailyCategoryId = 1;

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

    public DailyQuizPage(int userId) {
        this.userId = userId;

        setTitle("ThinkDaily - Today’s Quiz");
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
        top.setBorder(new EmptyBorder(6, 18, 6, 18));

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftTop.setOpaque(false);

        questionCounterLabel = new JLabel("Question 1 / 3");
        questionCounterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        questionCounterLabel.setForeground(new Color(80, 70, 95));

        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        timerLabel.setForeground(new Color(80, 70, 95));

        leftTop.add(questionCounterLabel);
        leftTop.add(timerLabel);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);

        categoryLabel = new JLabel("Category: -");
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

        // ================= QUESTION + ANSWERS CENTER =================
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

        // ===== ANSWERS PANEL =====
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

        JButton homeBtn = new JButton("⟵ Back");
        homeBtn.setBackground(new Color(245, 230, 245));
        homeBtn.setForeground(new Color(90, 70, 100));
        homeBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        homeBtn.setFocusPainted(false);
        homeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        homeBtn.addActionListener(e -> {
            quizTimer.stop();
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

        bottom.add(homeBtn, BorderLayout.WEST);
        bottom.add(nextButton, BorderLayout.EAST);

        card.add(bottom, BorderLayout.SOUTH);

        // ================= TIMER =================
        quizTimer = new javax.swing.Timer(1000, e -> {
            elapsedSeconds++;
            timerLabel.setText(String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60));
        });
        quizTimer.start();

        // ================= LOAD QUESTIONS FROM DB =================
    

queries = new ThinkDailyQueries();
try {
    java.util.List<QuestionModel> qms = queries.getQuestionsForPlay(null, null, 3);

    // ⭐ Make sure they are random and max 3
    if (qms != null && !qms.isEmpty()) {
        java.util.Collections.shuffle(qms);          // random order
        if (qms.size() > 3) {
            qms = new ArrayList<>(qms.subList(0, 3)); // keep only first 3
        }
    }

    questions.clear();
    for (QuestionModel qm : qms) {
        Question q = new Question(qm.questionId, qm.questionText, qm.categoryName, qm.difficulty);
        q.answers.clear();
        if (qm.answerIds != null) {
            for (int i = 0; i < qm.options.size(); i++) {
                int aid = (i < qm.answerIds.size()) ? qm.answerIds.get(i) : -1;
                q.answers.add(new Answer(aid, qm.options.get(i)));
            }
            if (qm.correctIndex > 0 && qm.correctIndex <= qm.answerIds.size()) {
                q.correctAnswerId = qm.answerIds.get(qm.correctIndex - 1);
            }
        }
        questions.add(q);
    }
} catch (SQLException ex) {
    ex.printStackTrace();
    // fallback to demo
    loadDailyQuiz();
}

showQuestion(0);
    }

    // ⭐ Answer Button Component ⭐
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
                selectedButton.setBorder(BorderFactory.createLineBorder(new Color(230, 180, 200), 2, true));
            }

            selected = true;
            selectedButton = this;

            setBackground(new Color(240, 200, 220));
            setBorder(BorderFactory.createLineBorder(new Color(200, 90, 150), 3, true));
        }
    }

    // ==============================================================
// SHOW QUESTION
// ==============================================================
    private void showQuestion(int index) {
        Question q = questions.get(index);

        questionCounterLabel.setText("Question " + (index + 1) + "/" + questions.size());
        categoryLabel.setText("Category: " + q.categoryName);
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
            // ======= QUIZ FINISHED =======
            quizTimer.stop();

            int totalQuestions = questions.size();
            int correctCount = calculateCorrectCount();
            int sessionPoints = 0;

            if (queries != null) {
                for (Question qq : questions) {
                    Integer selectedAns = selectedAnswerByQuestionId.get(qq.questionId);
                    int sel = (selectedAns != null) ? selectedAns : -1;
                    boolean isCorrect = (sel != -1 && sel == qq.correctAnswerId);
                    int pts = isCorrect ? 10 : 0;
                    sessionPoints += pts;
                    try {
                        queries.saveUserResponse(
                                userId,
                                qq.questionId,
                                sel,
                                isCorrect,
                                pts,
                                new java.sql.Timestamp(System.currentTimeMillis()),
                                "DAILY",
                                null
                        );
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }

                try {
                    queries.addPointsToUser(userId, sessionPoints);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // ⭐ NEW: mark daily quiz as completed in DB
                try {
                    DailyQuizService.markCompletedToday(userId, dailyCategoryId, sessionPoints);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                sessionPoints = correctCount * 10;
            }

            long timeMillis = elapsedSeconds * 1000L;
            String name = "Player";

            new QuizResultPage(
                    userId,
                    name,
                    sessionPoints,
                    correctCount,
                    totalQuestions,
                    timeMillis
            ).setVisible(true);

            dispose();
        }
    }

    // DEMO DATA
    private void loadDailyQuiz() {
        Question q1 = new Question(1, "What does CPU stand for?", "Technology", "Easy");
        q1.answers.add(new Answer(101, "Computer Personal Unit"));
        q1.answers.add(new Answer(102, "Central Processing Unit"));
        q1.answers.add(new Answer(103, "Central Power Utility"));
        q1.correctAnswerId = 102;

        questions.add(q1);

        Question q2 = new Question(2, "Which planet is the Red Planet?", "Science", "Medium");
        q2.answers.add(new Answer(201, "Mars"));
        q2.answers.add(new Answer(202, "Jupiter"));
        q2.answers.add(new Answer(203, "Venus"));
        q2.correctAnswerId = 201;

        questions.add(q2);

        Question q3 = new Question(3, "How many minutes in an hour?", "Math", "Easy");
        q3.answers.add(new Answer(301, "45"));
        q3.answers.add(new Answer(302, "30"));
        q3.answers.add(new Answer(303, "60"));
        q3.correctAnswerId = 303;

        questions.add(q3);
    }

    // MODEL CLASSES
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

    // ROUNDED PANEL
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
