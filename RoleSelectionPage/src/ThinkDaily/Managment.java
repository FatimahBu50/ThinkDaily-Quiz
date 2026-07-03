package ThinkDaily;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class Managment extends JPanel {

    // UI components
    private JButton jButton1; // + Add new user
    private JButton jButton2; // Delete (top)
    private JButton jButton3; // Search
    private JLabel jLabel1;   // "Search User:"
    private JLabel jLabel2;   // "Management"
    private JLabel jLabel3;   // logo
    private JScrollPane jScrollPane3;
    private JTable jTable2;
    private JTextField jTextField1;

    // DB + model
    private Connection conn;
    private DefaultTableModel tableModel;
    private List<UserData> userList;

    // holder class
    private class UserData {
        int userId;
        String username;
        String displayName;
        int currentLevel;
        int currentPoints;
        String status;

        UserData(int userId, String username, String displayName,
                 int currentLevel, int currentPoints, String status) {
            this.userId = userId;
            this.username = username;
            this.displayName = displayName;
            this.currentLevel = currentLevel;
            this.currentPoints = currentPoints;
            this.status = status;
        }
    }

    public Managment() {
        initComponents();          // 🎨 design
        initializeTable();         // table model + styles
        setupDatabaseConnection();
        loadUsersFromDatabase();
        setupSearchFunctionality();
        setupButtonActions();
    }

    private void initComponents() {
        // ============ BASIC PANEL STYLE ============
        setBackground(new Color(253, 239, 244));
        setPreferredSize(new Dimension(894, 750));
        setLayout(null); // same style as Questions panel

        // ===== LOGO =====
        jLabel3 = new JLabel();
        jLabel3.setBounds(377, 10, 140, 140); // centered for width 894

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/admin_panels/logo.png"));
            Image scaled = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            jLabel3.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            jLabel3.setText("ThinkDaily");
            jLabel3.setFont(new Font("SansSerif", Font.BOLD, 18));
            jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        }
        add(jLabel3);

        // ===== TITLE =====
        jLabel2 = new JLabel("Management", SwingConstants.CENTER);
        jLabel2.setFont(new Font("SansSerif", Font.BOLD, 24));
        jLabel2.setForeground(new Color(74, 74, 74));
        jLabel2.setBounds(0, 150, 894, 30);
        add(jLabel2);

        // ===== SEARCH LABEL =====
        jLabel1 = new JLabel("Search User:");
        jLabel1.setFont(new Font("SansSerif", Font.BOLD, 14));
        jLabel1.setForeground(new Color(74, 74, 74));
        jLabel1.setBounds(30, 185, 200, 25); // small label above the row
        add(jLabel1);

        // ===== TOP BUTTONS ROW =====
        JPanel topButtons = new JPanel();
        topButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topButtons.setBackground(new Color(253, 239, 244));
        topButtons.setBounds(30, 210, 830, 40);
        add(topButtons);

        jTextField1 = new JTextField(15);
        jTextField1.setFont(new Font("SansSerif", Font.PLAIN, 12));
        jTextField1.setForeground(new Color(153, 153, 153));
        jTextField1.setText("Search");

        jButton3 = new JButton("Search");
        jButton2 = new JButton("Delete");
        jButton1 = new JButton("+ Add new user");

        styleTopButton(jButton3);
        styleTopButton(jButton2);
        styleTopButton(jButton1);

        topButtons.add(jTextField1);
        topButtons.add(jButton3);
        topButtons.add(jButton2);
        topButtons.add(jButton1);

        // ===== TABLE + SCROLLPANE =====
        jTable2 = new JTable();
        jScrollPane3 = new JScrollPane(jTable2);
        jScrollPane3.setBounds(20, 260, 854, 420);
        jScrollPane3.getViewport().setBackground(Color.WHITE);
        jScrollPane3.setBorder(BorderFactory.createLineBorder(new Color(220, 200, 210)));
        add(jScrollPane3);
    }

    private void styleTopButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(230, 190, 200), 2));
        btn.setPreferredSize(new Dimension(110, 30));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    // ========== TABLE INITIALIZATION (same logic as before) ==========
    private void initializeTable() {
        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "User ID", "Username", "Display Name", "Level", "Points", "Status"
                }
        ) {
            Class[] types = new Class[]{
                    Integer.class, String.class, String.class,
                    Integer.class, Integer.class, String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        jTable2.setModel(tableModel);

        jTable2.setBackground(new Color(255, 245, 250));
        jTable2.setGridColor(new Color(153, 153, 153));
        jTable2.setSelectionBackground(new Color(255, 219, 229));
        jTable2.setShowGrid(true);
        jTable2.setRowHeight(28);

        // center all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < jTable2.getColumnCount(); i++) {
            jTable2.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // column widths
        jTable2.getColumnModel().getColumn(0).setPreferredWidth(60);
        jTable2.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(2).setPreferredWidth(140);
        jTable2.getColumnModel().getColumn(3).setPreferredWidth(60);
        jTable2.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTable2.getColumnModel().getColumn(5).setPreferredWidth(80);

        userList = new ArrayList<>();
    }

    // ========== DB + LOGIC (unchanged) ==========

    private void setupDatabaseConnection() {
        try {
            conn = DBConnection.getConnection();
            
            if (conn == null || conn.isClosed()) {
                JOptionPane.showMessageDialog(this,
                        "Cannot connect to database. Some features may not work.",
                        "Database Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    private void loadUsersFromDatabase() {
        if (conn == null) return;

        try {
             // ✅ 1) FIX: update levels based on points
        String syncLevels = """
            UPDATE users u
            JOIN accounts a ON a.account_id = u.account_id
            SET u.current_level = (u.current_points_total DIV 100) + 1
            WHERE a.role = 'PLAYER'
        """;
        Statement syncStmt = conn.createStatement();
        syncStmt.executeUpdate(syncLevels);
        syncStmt.close();

            String sql = """
                    SELECT 
                        u.user_id,
                        a.username,
                        u.user_name as display_name,
                        u.current_level,
                        u.current_points_total,
                        CASE 
                            WHEN a.is_active = TRUE THEN 'Active'
                            ELSE 'Inactive'
                        END as status
                    FROM users u
                    JOIN accounts a ON u.account_id = a.account_id
                    WHERE a.role = 'PLAYER'
                    ORDER BY u.user_id
                    """;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            tableModel.setRowCount(0);
            userList.clear();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String displayName = rs.getString("display_name");
                int level = rs.getInt("current_level");
                int points = rs.getInt("current_points_total");
                String status = rs.getString("status");

                userList.add(new UserData(userId, username, displayName, level, points, status));

                tableModel.addRow(new Object[]{
                        userId,
                        username,
                        displayName,
                        level,
                        points,
                        status
                });
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error loading user data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupSearchFunctionality() {
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { searchUsers(); }

            @Override
            public void removeUpdate(DocumentEvent e) { searchUsers(); }

            @Override
            public void changedUpdate(DocumentEvent e) { searchUsers(); }
        });

        jButton3.addActionListener(e -> searchUsers());

        jTextField1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (jTextField1.getText().equals("Search")) {
                    jTextField1.setText("");
                    jTextField1.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (jTextField1.getText().isEmpty()) {
                    jTextField1.setText("Search");
                    jTextField1.setForeground(new Color(153, 153, 153));
                }
            }
        });
    }

    private void searchUsers() {
        String searchText = jTextField1.getText().trim().toLowerCase();

        if (searchText.equals("search") || searchText.isEmpty()) {
            loadUsersFromDatabase();
            return;
        }

        tableModel.setRowCount(0);

        for (UserData user : userList) {
            if (String.valueOf(user.userId).contains(searchText)
                    || user.username.toLowerCase().contains(searchText)
                    || (user.displayName != null && user.displayName.toLowerCase().contains(searchText))
                    || user.status.toLowerCase().contains(searchText)) {

                tableModel.addRow(new Object[]{
                        user.userId,
                        user.username,
                        user.displayName,
                        user.currentLevel,
                        user.currentPoints,
                        user.status
                });
            }
        }
    }

    private void setupButtonActions() {
        jButton1.addActionListener(e -> showAddUserDialog());
        jButton2.addActionListener(e -> deleteSelectedUser());
    }


    private void showAddUserDialog() {
    JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Add New User", true);
    dialog.setSize(420, 520);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(new Color(253, 239, 244));
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;

    Font labelFont = new Font("SansSerif", Font.PLAIN, 12);

    // ===== COMMON SECURITY QUESTIONS =====
    String[] securityQuestions = {
            "Select a question",
            "What is your favorite color?",
            "What is your pet's name?",
            "What city were you born in?",
            "What is your favorite food?",
            "What is the name of your best friend?"
    };

    // Username
    gbc.gridy = 0;
    JLabel usernameLbl = new JLabel("Username:*");
    usernameLbl.setFont(labelFont);
    formPanel.add(usernameLbl, gbc);
    JTextField usernameField = new JTextField(20);
    gbc.gridx = 1;
    formPanel.add(usernameField, gbc);

    // Password
    gbc.gridx = 0; gbc.gridy = 1;
    JLabel passLbl = new JLabel("Password:*");
    passLbl.setFont(labelFont);
    formPanel.add(passLbl, gbc);
    JPasswordField passwordField = new JPasswordField(20);
    gbc.gridx = 1;
    formPanel.add(passwordField, gbc);

    // Confirm Password
    gbc.gridx = 0; gbc.gridy = 2;
    JLabel confirmLbl = new JLabel("Confirm Password:*");
    confirmLbl.setFont(labelFont);
    formPanel.add(confirmLbl, gbc);
    JPasswordField confirmPasswordField = new JPasswordField(20);
    gbc.gridx = 1;
    formPanel.add(confirmPasswordField, gbc);

    // Display Name
    gbc.gridx = 0; gbc.gridy = 3;
    JLabel displayLbl = new JLabel("Display Name:*");
    displayLbl.setFont(labelFont);
    formPanel.add(displayLbl, gbc);
    JTextField displayNameField = new JTextField(20);
    gbc.gridx = 1;
    formPanel.add(displayNameField, gbc);

    // ===== Security Question 1 (COMBO) =====
    gbc.gridx = 0; gbc.gridy = 4;
    JLabel q1Lbl = new JLabel("Security Q1:*");
    q1Lbl.setFont(labelFont);
    formPanel.add(q1Lbl, gbc);
    JComboBox<String> q1Combo = new JComboBox<>(securityQuestions);
    gbc.gridx = 1;
    formPanel.add(q1Combo, gbc);

    // Answer 1
    gbc.gridx = 0; gbc.gridy = 5;
    JLabel a1Lbl = new JLabel("Answer 1:*");
    a1Lbl.setFont(labelFont);
    formPanel.add(a1Lbl, gbc);
    JTextField answer1Field = new JTextField(20);
    gbc.gridx = 1;
    formPanel.add(answer1Field, gbc);

    // ===== Security Question 2 (COMBO) =====
    gbc.gridx = 0; gbc.gridy = 6;
    JLabel q2Lbl = new JLabel("Security Q2:*");
    q2Lbl.setFont(labelFont);
    formPanel.add(q2Lbl, gbc);
    JComboBox<String> q2Combo = new JComboBox<>(securityQuestions);
    gbc.gridx = 1;
    formPanel.add(q2Combo, gbc);

    // Answer 2
    gbc.gridx = 0; gbc.gridy = 7;
    JLabel a2Lbl = new JLabel("Answer 2:*");
    a2Lbl.setFont(labelFont);
    formPanel.add(a2Lbl, gbc);
    JTextField answer2Field = new JTextField(20);
    gbc.gridx = 1;
    formPanel.add(answer2Field, gbc);

    // ===== BUTTONS =====
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(new Color(253, 239, 244));

    JButton saveButton = new JButton("Save User");
    JButton cancelButton = new JButton("Cancel");

    saveButton.setBackground(new Color(255, 175, 204));
    saveButton.setForeground(new Color(74, 74, 74));
    saveButton.setFocusPainted(false);
    cancelButton.setBackground(new Color(230, 230, 230));
    cancelButton.setFocusPainted(false);

    saveButton.addActionListener(e -> {
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // pass selected questions (as String) instead of text fields
        String q1 = (String) q1Combo.getSelectedItem();
        String q2 = (String) q2Combo.getSelectedItem();

        if (saveNewUser(
                usernameField.getText().trim(),
                password,
                displayNameField.getText().trim(),
                q1,
                answer1Field.getText().trim(),
                q2,
                answer2Field.getText().trim(),
                confirmPassword
        )) {
            dialog.dispose();
            loadUsersFromDatabase();
        }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    JLabel infoLabel = new JLabel("* All fields are required");
    infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
    infoLabel.setForeground(Color.GRAY);

    buttonPanel.add(infoLabel);
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
}

    
    private boolean saveNewUser(String username, String password, String displayName,
                           String securityQ1, String answer1,
                           String securityQ2, String answer2, String confirmPassword) {

    // Required fields
    if (username.isEmpty() || password.isEmpty() || displayName.isEmpty() ||
        answer1.isEmpty() || answer2.isEmpty() ||
        securityQ1 == null || securityQ2 == null) {

        JOptionPane.showMessageDialog(this,
                "All fields are required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        return false;
    }

    // Must choose real questions (not the placeholder)
    if ("Select a question".equals(securityQ1) || "Select a question".equals(securityQ2)) {
        JOptionPane.showMessageDialog(this,
                "Please choose both security questions from the list.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        return false;
    }

    if (!password.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        return false;
    }

    if (password.length() < 6) {
        JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        return false;
    }

    if (securityQ1.equals(securityQ2)) {
        JOptionPane.showMessageDialog(this,
                "Security questions must be different.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        return false;
    }

    // --- rest of your DB insert logic stays the same ---
    try {
        String hashedPassword = AdminLoginPage.hashPassword(password);

        String accountSql = """
            INSERT INTO accounts (username, password_hash, role,
                                  security_question1, security_ans1,
                                  security_question2, security_ans2, is_active)
            VALUES (?, ?, 'PLAYER', ?, ?, ?, ?, TRUE)
            """;

        PreparedStatement accountStmt = conn.prepareStatement(accountSql, Statement.RETURN_GENERATED_KEYS);
        accountStmt.setString(1, username);
        accountStmt.setString(2, hashedPassword);
        accountStmt.setString(3, securityQ1);
        accountStmt.setString(4, answer1);
        accountStmt.setString(5, securityQ2);
        accountStmt.setString(6, answer2);

        int accountRows = accountStmt.executeUpdate();

        if (accountRows > 0) {
            ResultSet rs = accountStmt.getGeneratedKeys();
            if (rs.next()) {
                int accountId = rs.getInt(1);

                String userSql = """
                    INSERT INTO users (account_id, user_name, bio,
                                       current_level, current_points_total)
                    VALUES (?, ?, ?, 1, 0)
                    """;

                PreparedStatement userStmt = conn.prepareStatement(userSql);
                userStmt.setInt(1, accountId);
                userStmt.setString(2, displayName);
                userStmt.setString(3, "New user");

                userStmt.executeUpdate();
                userStmt.close();

                JOptionPane.showMessageDialog(this,
                        "User added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                accountStmt.close();
                return true;
            }
        }

        accountStmt.close();

    } catch (SQLException e) {
        System.out.println("Error saving user: " + e.getMessage());

        if (e.getMessage().contains("Duplicate entry")) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists. Please choose another.",
                    "Duplicate Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error saving user: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    return false;
}

    
    private void deleteSelectedUser() {
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user:\n" +
            "ID: " + userId + " - " + username + "?\n\n" +
            "This action cannot be undone.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            deleteUserFromDatabase(userId);
        }
    }
    
    private void deleteUserFromDatabase(int userId) {
        try {
            // First get the account_id
            String getAccountSql = "SELECT account_id FROM users WHERE user_id = ?";
            PreparedStatement getStmt = conn.prepareStatement(getAccountSql);
            getStmt.setInt(1, userId);
            ResultSet rs = getStmt.executeQuery();
            
            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                
                // Delete from accounts (this will cascade to users if FK has ON DELETE CASCADE)
                String deleteSql = "DELETE FROM accounts WHERE account_id = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                deleteStmt.setInt(1, accountId);
                
                int rowsAffected = deleteStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                        "User deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the table
                    loadUsersFromDatabase();
                }
                
                deleteStmt.close();
            }
            
            getStmt.close();
            rs.close();
            
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error deleting user: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
