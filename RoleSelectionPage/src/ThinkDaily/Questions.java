package ThinkDaily;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class Questions extends JPanel {

    // ========= Fields =========
    private JTable table;
    private DefaultTableModel model;
    private JButton refreshBtn, addBtn, searchBtn;
    private JTextField searchField;
    private ThinkDailyQueries queries;

    // ✅ Category must be selected (not typed)
    private JComboBox<String> categoryComboGlobal;

    public Questions() {
        setBackground(new Color(253, 239, 244));
        setPreferredSize(new Dimension(894, 750));
        setLayout(null);

        initComponents();
        setupDatabaseConnection();
        loadQuestionsFromDB();
        setupButtonActions();
    }

    // ========= UI SETUP =========
    private void initComponents() {
        // ===== LOGO =====
        JLabel logoLabel = new JLabel();
        logoLabel.setBounds(377, 10, 140, 140); // centered for width 894

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
        titleLabel.setBounds(0, 150, 894, 30);
        add(titleLabel);

        // ===== TOP BUTTONS (LEFT ALIGNED) =====
        JPanel topButtons = new JPanel();
        topButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topButtons.setBackground(new Color(253, 239, 244));
        topButtons.setBounds(30, 190, 830, 40);
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
        // ✅ Removed Option4 completely
        String[] columns = {
                "No.",            // 0
                "Question",       // 1
                "Correct Answer", // 2
                "Category",       // 3
                "Difficulty",     // 4
                "Option1",        // 5 (hidden)
                "Option2",        // 6 (hidden)
                "Option3",        // 7 (hidden)
                "CorrectIndex",   // 8 (hidden)
                "Edit",           // 9 (button)
                "Delete",         // 10 (button)
                "QID"             // 11 (hidden)
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Only Edit & Delete columns are editable (buttons)
                return col == 9 || col == 10;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 8 || columnIndex == 11) {
                    return Integer.class;
                }
                return Object.class;
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

        table.getColumnModel().getColumn(0).setCellRenderer(center); // No.
        table.getColumnModel().getColumn(3).setCellRenderer(center); // Category
        table.getColumnModel().getColumn(4).setCellRenderer(center); // Difficulty
        table.getColumnModel().getColumn(1).setCellRenderer(left);   // Question
        table.getColumnModel().getColumn(2).setCellRenderer(left);   // Correct Answer

        // Column widths (visible)
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(310);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(9).setPreferredWidth(70);
        table.getColumnModel().getColumn(10).setPreferredWidth(80);

        // Hide internal columns
        hideColumn(5);  // Option1
        hideColumn(6);  // Option2
        hideColumn(7);  // Option3
        hideColumn(8);  // CorrectIndex
        hideColumn(11); // QID

        // Button renderers & editors
        table.getColumnModel().getColumn(9).setCellRenderer(new ButtonRenderer("Edit"));
        table.getColumnModel().getColumn(9).setCellEditor(new EditButtonEditor(new JCheckBox()));

        table.getColumnModel().getColumn(10).setCellRenderer(new ButtonRenderer("Delete"));
        table.getColumnModel().getColumn(10).setCellEditor(new DeleteButtonEditor(new JCheckBox()));

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 240, 854, 460);
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

        addBtn.addActionListener(e -> openQuestionPopup(-1)); // -1 → Add mode
    }

    // ========= LOAD ALL QUESTIONS =========
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

                // ✅ Ensure 3 options only
                List<String> options = new ArrayList<>(q.options);
                while (options.size() < 3) options.add("");

                model.addRow(new Object[]{
                        rowNum++,              // 0 - No.
                        q.questionText,        // 1
                        correctAnswer,         // 2
                        q.categoryName,        // 3
                        q.difficulty,          // 4
                        options.get(0),        // 5 - Opt1 (hidden)
                        options.get(1),        // 6 - Opt2
                        options.get(2),        // 7 - Opt3
                        q.correctIndex,        // 8 - CorrectIndex
                        "Edit",                // 9
                        "Delete",              // 10
                        q.questionId           // 11 - QID
                });
            }

            // Fix row numbers in case of changes
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

    // ========= SEARCH =========
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
                if (q.questionText.toLowerCase().contains(searchText)
                        || q.categoryName.toLowerCase().contains(searchText)
                        || q.difficulty.toLowerCase().contains(searchText)) {

                    String correctAnswer = "";
                    if (q.correctIndex >= 1 && q.correctIndex <= q.options.size()) {
                        correctAnswer = q.options.get(q.correctIndex - 1);
                    }

                    List<String> options = new ArrayList<>(q.options);
                    while (options.size() < 3) options.add("");

                    model.addRow(new Object[]{
                            rowNum++,
                            q.questionText,
                            correctAnswer,
                            q.categoryName,
                            q.difficulty,
                            options.get(0),
                            options.get(1),
                            options.get(2),
                            q.correctIndex,
                            "Edit",
                            "Delete",
                            q.questionId
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Search failed: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
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

    // ========= ADD / EDIT POPUP =========
    /**
     * @param rowIndex -1 for ADD, otherwise EDIT by row.
     */
    private void openQuestionPopup(int rowIndex) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                rowIndex == -1 ? "Add Question" : "Edit Question",
                true);

        dialog.setSize(500, 560); // smaller because 3 options now
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

        // CATEGORY (SELECTED, NOT TYPED)
        JLabel cLabel = new JLabel("Category:*");
        cLabel.setBounds(30, y, 120, 30);
        dialog.add(cLabel);

        // ✅ You can adjust these to your real categories
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{
                "Math", "Science", "History", "Technology"
        });
        categoryCombo.setBounds(150, y, 320, 30);
        dialog.add(categoryCombo);
        y += 40;

        // DIFFICULTY
        JLabel dLabel = new JLabel("Difficulty:*");
        dLabel.setBounds(30, y, 120, 30);
        dialog.add(dLabel);

        JComboBox<String> difficultyCombo = new JComboBox<>(new String[]{"EASY", "MEDIUM", "HARD"});
        difficultyCombo.setBounds(150, y, 320, 30);
        dialog.add(difficultyCombo);
        y += 40;

        // OPTIONS (✅ ONLY 3)
        JTextField[] optFields = new JTextField[3];
        JRadioButton[] radios = new JRadioButton[3];
        ButtonGroup bg = new ButtonGroup();

        for (int i = 0; i < 3; i++) {
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

        // If EDIT mode, load existing data
        if (rowIndex >= 0) {
            txtQuestion.setText((String) model.getValueAt(rowIndex, 1));

            String currentCategory = String.valueOf(model.getValueAt(rowIndex, 3));
            categoryCombo.setSelectedItem(currentCategory);

            difficultyCombo.setSelectedItem(model.getValueAt(rowIndex, 4));

            optFields[0].setText((String) model.getValueAt(rowIndex, 5));
            optFields[1].setText((String) model.getValueAt(rowIndex, 6));
            optFields[2].setText((String) model.getValueAt(rowIndex, 7));

            int correctIndex = (Integer) model.getValueAt(rowIndex, 8);
            if (correctIndex >= 1 && correctIndex <= 3) {
                radios[correctIndex - 1].setSelected(true);
            }
        }

        // BUTTONS
        JButton saveBtn = new JButton(rowIndex == -1 ? "Add" : "Save");
        saveBtn.setBounds(150, y + 10, 100, 35);
        saveBtn.setBackground(new Color(255, 175, 204));
        saveBtn.setForeground(new Color(74, 74, 74));
        dialog.add(saveBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(270, y + 10, 100, 35);
        cancelBtn.setBackground(Color.LIGHT_GRAY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.add(cancelBtn);

        // SAVE LOGIC
        saveBtn.addActionListener(e -> {
            String question = txtQuestion.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            String difficulty = (String) difficultyCombo.getSelectedItem();

            List<String> options = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                String opt = optFields[i].getText().trim();
                if (opt.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "All 3 options must be filled!",
                            "Input Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                options.add(opt);
            }

            int correctIndex = -1;
            for (int i = 0; i < 3; i++) {
                if (radios[i].isSelected()) {
                    correctIndex = i + 1;
                    break;
                }
            }

            if (question.isEmpty() || category == null || category.isEmpty() || correctIndex == -1) {
                JOptionPane.showMessageDialog(dialog,
                        "All fields marked with * are required!",
                        "Input Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (rowIndex == -1) {
                    // ===== ADD NEW QUESTION =====
                    int questionId = queries.addQuestion(
                            question,
                            category,
                            options,
                            correctIndex,
                            difficulty
                    );

                    if (questionId > 0) {
                        String correctAnswer = options.get(correctIndex - 1);
                        model.addRow(new Object[]{
                                model.getRowCount() + 1,
                                question,
                                correctAnswer,
                                category,
                                difficulty,
                                options.get(0),
                                options.get(1),
                                options.get(2),
                                correctIndex,
                                "Edit",
                                "Delete",
                                questionId
                        });

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Question added successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                } else {
                    // ===== UPDATE EXISTING QUESTION =====
                    int questionId = (Integer) model.getValueAt(rowIndex, 11);

                    boolean success = queries.updateQuestion(
                            questionId,
                            question,
                            category,
                            options,
                            correctIndex,
                            difficulty,
                            true   // is_active
                    );

                    if (success) {
                        String correctAnswer = options.get(correctIndex - 1);

                        model.setValueAt(question,      rowIndex, 1);
                        model.setValueAt(correctAnswer, rowIndex, 2);
                        model.setValueAt(category,      rowIndex, 3);
                        model.setValueAt(difficulty,    rowIndex, 4);

                        for (int i = 0; i < 3; i++) {
                            model.setValueAt(options.get(i), rowIndex, 5 + i);
                        }

                        model.setValueAt(correctIndex, rowIndex, 8);

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Question updated successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                dialog,
                                "Failed to update question in database.",
                                "Update Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }

                dialog.dispose();
                loadQuestionsFromDB();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        dialog,
                        "Database error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        dialog.setVisible(true);
    }

    // ========= BUTTON RENDERER =========
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            super(text);
            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(230, 190, 200)));
            setFont(new Font("SansSerif", Font.PLAIN, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    // ========= EDIT BUTTON EDITOR =========
    class EditButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int editingRow;

        public EditButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Edit");
            button.setOpaque(true);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(new Color(230, 190, 200)));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addActionListener(e -> {
                fireEditingStopped();
                openQuestionPopup(editingRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            editingRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Edit";
        }
    }

    // ========= DELETE BUTTON EDITOR =========
    class DeleteButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int editingRow;

        public DeleteButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Delete");
            button.setOpaque(true);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(new Color(230, 190, 200)));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addActionListener(e -> {
                fireEditingStopped();

                int confirm = JOptionPane.showConfirmDialog(
                        Questions.this,
                        "Are you sure you want to delete this question?\nThis cannot be undone.",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm != JOptionPane.YES_OPTION) return;

                try {
                    int questionId = (Integer) model.getValueAt(editingRow, 11);
                    queries.deleteQuestion(questionId);
                    model.removeRow(editingRow);

                    // Fix row numbers
                    for (int i = 0; i < model.getRowCount(); i++) {
                        model.setValueAt(i + 1, i, 0);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            Questions.this,
                            "Failed to delete question: " + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            editingRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Delete";
        }
    }
}
