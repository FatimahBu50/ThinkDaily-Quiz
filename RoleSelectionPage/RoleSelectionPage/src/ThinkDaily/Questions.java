package ThinkDaily;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class Questions extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JButton refreshBtn, addBtn, searchBtn;
    private JTextField searchField;
    private ThinkDailyQueries queries;

    public Questions() {
        setBackground(new Color(253, 239, 244));
        initComponents();
        setupDatabaseConnection();
        loadQuestionsFromDB();
        setupButtonActions();
    }

    private void initComponents() {
        setLayout(null);

        // ===== LOGO =====
        JLabel logoLabel = new JLabel();
        logoLabel.setBounds(324, 10, 140, 140);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/logo.png"));
            Image scaled = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            logoLabel.setText("ThinkDaily");
            logoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        add(logoLabel);

        // ===== TITLE =====
        JLabel titleLabel = new JLabel("Questions Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(74, 74, 74));
        titleLabel.setBounds(0, 150, 788, 30);
        add(titleLabel);

        // ===== TOP BUTTONS =====
        JPanel topButtons = new JPanel();
        topButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topButtons.setBackground(new Color(253, 239, 244));
        topButtons.setBounds(30, 190, 730, 40);
        add(topButtons);

        refreshBtn = new JButton("Refresh");
        addBtn = new JButton("Add");
        searchField = new JTextField(15);
        searchBtn = new JButton("Search");

        styleButton(refreshBtn);
        styleButton(addBtn);
        styleButton(searchBtn);

        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setText("Search questions...");
        searchField.setForeground(Color.GRAY);

        topButtons.add(refreshBtn);
        topButtons.add(searchField);
        topButtons.add(searchBtn);
        topButtons.add(addBtn);

        // ===== TABLE SETUP =====
        String[] columns = {
            "No.", "Question", "Correct Answer", "Category", "Difficulty",
            "Option1", "Option2", "Option3", "Option4", "CorrectIndex",
            "QID"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // FULLY READ-ONLY
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setBackground(Color.decode("#F9EEF2"));
        table.setSelectionBackground(Color.decode("#F8DFEB"));
        table.setGridColor(new Color(220, 200, 210));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(74, 74, 74));

        // Alignment
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(left);
        table.getColumnModel().getColumn(2).setCellRenderer(left);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        // Hide internal columns
        hideColumn(5);
        hideColumn(6);
        hideColumn(7);
        hideColumn(8);
        hideColumn(9);
        hideColumn(10);

        // Scroll
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 230, 748, 400);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 200, 210)));
        add(scrollPane);

        // Search placeholder behavior
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search questions...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search questions...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void setupDatabaseConnection() {
        queries = new ThinkDailyQueries();
    }

    private void setupButtonActions() {
        refreshBtn.addActionListener(e -> {
            loadQuestionsFromDB();
            searchField.setText("Search questions...");
            searchField.setForeground(Color.GRAY);
        });

        searchBtn.addActionListener(e -> searchQuestions());
        searchField.addActionListener(e -> searchQuestions());

        addBtn.addActionListener(e -> openQuestionPopup());
    }

    private void loadQuestionsFromDB() {
        model.setRowCount(0);
        try {
            List<QuestionModel> questions = queries.getAllQuestions();
            int rowNum = 1;

            for (QuestionModel q : questions) {
                String correctAnswer = "";
                if (q.correctIndex >= 1 && q.correctIndex <= q.options.size()) {
                    correctAnswer = q.options.get(q.correctIndex - 1);
                }

                List<String> opts = new ArrayList<>(q.options);
                while (opts.size() < 4) opts.add("");

                model.addRow(new Object[]{
                    rowNum++, q.questionText, correctAnswer, q.categoryName,
                    q.difficulty, opts.get(0), opts.get(1), opts.get(2), opts.get(3),
                    q.correctIndex, q.questionId
                });
            }

            // Update row numbers
            for (int i = 0; i < model.getRowCount(); i++) {
                model.setValueAt(i + 1, i, 0);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Failed to load questions: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchQuestions() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty() || searchText.equals("search questions...")) {
            loadQuestionsFromDB();
            return;
        }

        try {
            List<QuestionModel> allQuestions = queries.getAllQuestions();
            model.setRowCount(0);
            int rowNum = 1;

            for (QuestionModel q : allQuestions) {
                if (q.questionText.toLowerCase().contains(searchText) ||
                    q.categoryName.toLowerCase().contains(searchText) ||
                    q.difficulty.toLowerCase().contains(searchText)) {

                    String correctAnswer = "";
                    if (q.correctIndex >= 1 && q.correctIndex <= q.options.size()) {
                        correctAnswer = q.options.get(q.correctIndex - 1);
                    }

                    List<String> opts = new ArrayList<>(q.options);
                    while (opts.size() < 4) opts.add("");

                    model.addRow(new Object[]{
                        rowNum++, q.questionText, correctAnswer,
                        q.categoryName, q.difficulty,
                        opts.get(0), opts.get(1), opts.get(2), opts.get(3),
                        q.correctIndex, q.questionId
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Search failed: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(230, 190, 200), 2));
        btn.setPreferredSize(new Dimension(90, 30));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    private void hideColumn(int index) {
        TableColumn col = table.getColumnModel().getColumn(index);
        col.setMinWidth(0);
        col.setMaxWidth(0);
        col.setPreferredWidth(0);
        col.setResizable(false);
    }

    // ADD ONLY popup (no edit)
    private void openQuestionPopup() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Add Question", true);

        dialog.setSize(500, 600);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(new Color(253, 239, 244));
        dialog.setLocationRelativeTo(this);

        int y = 30;

        // QUESTION
        JLabel qLabel = new JLabel("Question:*");
        qLabel.setBounds(30, y, 120, 30);
        dialog.add(qLabel);

        JTextArea txtQuestion = new JTextArea();
        txtQuestion.setLineWrap(true);
        txtQuestion.setWrapStyleWord(true);
        JScrollPane qScroll = new JScrollPane(txtQuestion);
        qScroll.setBounds(150, y, 320, 60);
        dialog.add(qScroll);
        y += 70;

        // CATEGORY
        JLabel cLabel = new JLabel("Category:*");
        cLabel.setBounds(30, y, 120, 30);
        dialog.add(cLabel);

        JTextField txtCategory = new JTextField();
        txtCategory.setBounds(150, y, 320, 30);
        dialog.add(txtCategory);
        y += 40;

        // DIFFICULTY
        JLabel dLabel = new JLabel("Difficulty:*");
        dLabel.setBounds(30, y, 120, 30);
        dialog.add(dLabel);

        JComboBox<String> difficultyCombo = new JComboBox<>(new String[]{"EASY", "MEDIUM", "HARD"});
        difficultyCombo.setBounds(150, y, 320, 30);
        dialog.add(difficultyCombo);
        y += 40;

        // OPTIONS
        JTextField[] optFields = new JTextField[4];
        JRadioButton[] radios = new JRadioButton[4];
        ButtonGroup bg = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            JLabel optL = new JLabel("Option " + (i + 1) + ":*");
            optL.setBounds(30, y, 100, 30);
            dialog.add(optL);

            optFields[i] = new JTextField();
            optFields[i].setBounds(150, y, 250, 30);
            dialog.add(optFields[i]);

            radios[i] = new JRadioButton("Correct");
            radios[i].setBounds(410, y, 80, 30);
            radios[i].setBackground(dialog.getBackground());
            bg.add(radios[i]);
            dialog.add(radios[i]);

            y += 40;
        }

        // BUTTONS
        JButton saveBtn = new JButton("Add");
        saveBtn.setBounds(150, y + 10, 100, 35);
        dialog.add(saveBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(270, y + 10, 100, 35);
        dialog.add(cancelBtn);

        cancelBtn.addActionListener(e -> dialog.dispose());

        // SAVE LOGIC
        saveBtn.addActionListener(e -> {
            try {
                String question = txtQuestion.getText().trim();
                String category = txtCategory.getText().trim();
                String difficulty = difficultyCombo.getSelectedItem().toString();

                if (question.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields.");
                    return;
                }

                List<String> options = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    options.add(optFields[i].getText().trim());
                }

                int correctIndex = -1;
                for (int i = 0; i < 4; i++) {
                    if (radios[i].isSelected()) correctIndex = i + 1;
                }

                if (correctIndex == -1) {
                    JOptionPane.showMessageDialog(dialog, "Select the correct answer.");
                    return;
                }

                int id = queries.addQuestion(question, category, options, correctIndex, difficulty);
                if (id > 0) {
                    loadQuestionsFromDB();
                    JOptionPane.showMessageDialog(dialog, "Question added!");
                    dialog.dispose();
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }
}
