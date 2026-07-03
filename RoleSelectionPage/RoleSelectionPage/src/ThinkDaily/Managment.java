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
    private JButton jButtonAdd;     // + Add new user
    private JButton jButtonSearch;  // Search
    private JLabel jLabelSearch;
    private JLabel jLabelTitle;
    private JLabel jLabelLogo;
    private JScrollPane jScrollPaneTable;
    private JTable jTableUsers;
    private JTextField jTextFieldSearch;

    // DB + model
    private Connection conn;
    private DefaultTableModel tableModel;
    private List<UserData> userList;

    // Class to hold user data
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
        initComponents();          // Build UI
        initializeTable();         // Setup table model + styles
        setupDatabaseConnection();
        loadUsersFromDatabase();
        setupSearchFunctionality();
        setupButtonActions();
    }

    private void initComponents() {
        // ===== OUTER PANEL (fills contentPanel) =====
        setBackground(new Color(253, 239, 244));
        setPreferredSize(new Dimension(894, 750));
        setMinimumSize(new Dimension(894, 750));
        setMaximumSize(new Dimension(894, 750));
        setLayout(new GridBagLayout()); // Center inner panel

        // ===== Create components =====
        jLabelLogo = new JLabel();
        jLabelTitle = new JLabel();
        jLabelSearch = new JLabel();
        jTextFieldSearch = new JTextField();
        jButtonSearch = new JButton();
        jButtonAdd = new JButton();
        jScrollPaneTable = new JScrollPane();
        jTableUsers = new JTable();

        // Logo
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/admin_panels/logo.png"));
            jLabelLogo.setIcon(icon);
        } catch (Exception e) {
            jLabelLogo.setText("ThinkDaily");
            jLabelLogo.setFont(new Font("SansSerif", Font.BOLD, 24));
            jLabelLogo.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Title
        jLabelTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        jLabelTitle.setForeground(new Color(74, 74, 74));
        jLabelTitle.setText("Management");

        // Search label
        jLabelSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
        jLabelSearch.setForeground(new Color(74, 74, 74));
        jLabelSearch.setText("Search User:");

        // Search field
        jTextFieldSearch.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
        jTextFieldSearch.setForeground(new Color(153, 153, 153));
        jTextFieldSearch.setText("Search");

        // Search button
        jButtonSearch.setBackground(new Color(255, 175, 204));
        jButtonSearch.setFont(new Font("SansSerif", Font.BOLD, 12));
        jButtonSearch.setForeground(new Color(74, 74, 74));
        jButtonSearch.setText("Search");

        // Add new user button
        jButtonAdd.setBackground(new Color(255, 175, 204));
        jButtonAdd.setFont(new Font("SansSerif", Font.BOLD, 12));
        jButtonAdd.setForeground(new Color(74, 74, 74));
        jButtonAdd.setText("+ Add new user");

        // Table in scroll pane
        jScrollPaneTable.setViewportView(jTableUsers);
        jScrollPaneTable.setPreferredSize(new Dimension(600, 230));

        // ===== INNER PANEL (actual content, centered inside outer) =====
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setPreferredSize(new Dimension(700, 500));

        GroupLayout layout = new GroupLayout(inner);
        inner.setLayout(layout);

        // ---------- HORIZONTAL ----------
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(jLabelLogo)
                .addComponent(jLabelTitle)
                .addComponent(jLabelSearch)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jTextFieldSearch, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
                    .addGap(15)
                    .addComponent(jButtonSearch)
                    .addGap(15)
                    .addComponent(jButtonAdd, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                )
                .addComponent(jScrollPaneTable, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
        );

        // ---------- VERTICAL ----------
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(25)
                .addComponent(jLabelLogo)
                .addGap(10)
                .addComponent(jLabelTitle)
                .addGap(40)
                .addComponent(jLabelSearch)
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSearch)
                    .addComponent(jButtonAdd)
                )
                .addGap(30)
                .addComponent(jScrollPaneTable, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
                .addGap(40)
        );

        // Add inner panel to outer (CENTER using GridBag)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(inner, gbc);
    }

    private void initializeTable() {
        // Simple view-only table
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

        jTableUsers.setModel(tableModel);

        jTableUsers.setBackground(new Color(255, 245, 250));
        jTableUsers.setGridColor(new Color(153, 153, 153));
        jTableUsers.setSelectionBackground(new Color(255, 219, 229));
        jTableUsers.setShowGrid(true);

        // Center cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jTableUsers.getColumnCount(); i++) {
            jTableUsers.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Column widths
        jTableUsers.getColumnModel().getColumn(0).setPreferredWidth(60);   // User ID
        jTableUsers.getColumnModel().getColumn(1).setPreferredWidth(110);  // Username
        jTableUsers.getColumnModel().getColumn(2).setPreferredWidth(140);  // Display Name
        jTableUsers.getColumnModel().getColumn(3).setPreferredWidth(60);   // Level
        jTableUsers.getColumnModel().getColumn(4).setPreferredWidth(80);   // Points
        jTableUsers.getColumnModel().getColumn(5).setPreferredWidth(80);   // Status

        userList = new ArrayList<>();
    }

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

                Object[] row = {
                    userId,
                    username,
                    displayName,
                    level,
                    points,
                    status
                };
                tableModel.addRow(row);
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
        jTextFieldSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { searchUsers(); }
            @Override public void removeUpdate(DocumentEvent e) { searchUsers(); }
            @Override public void changedUpdate(DocumentEvent e) { searchUsers(); }
        });

        jButtonSearch.addActionListener(e -> searchUsers());

        // Placeholder behavior
        jTextFieldSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (jTextFieldSearch.getText().equals("Search")) {
                    jTextFieldSearch.setText("");
                    jTextFieldSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (jTextFieldSearch.getText().isEmpty()) {
                    jTextFieldSearch.setText("Search");
                    jTextFieldSearch.setForeground(new Color(153, 153, 153));
                }
            }
        });
    }

    private void searchUsers() {
        String searchText = jTextFieldSearch.getText().trim().toLowerCase();

        if (searchText.equals("search") || searchText.isEmpty()) {
            loadUsersFromDatabase();
            return;
        }

        tableModel.setRowCount(0);

        for (UserData user : userList) {
            if (String.valueOf(user.userId).contains(searchText) ||
                user.username.toLowerCase().contains(searchText) ||
                (user.displayName != null && user.displayName.toLowerCase().contains(searchText)) ||
                user.status.toLowerCase().contains(searchText)) {

                Object[] row = {
                    user.userId,
                    user.username,
                    user.displayName,
                    user.currentLevel,
                    user.currentPoints,
                    user.status
                };
                tableModel.addRow(row);
            }
        }
    }

    private void setupButtonActions() {
        // Only Add New User now
        jButtonAdd.addActionListener(e -> showAddUserDialog());
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Username
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:*"), gbc);
        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:*"), gbc);
        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Confirm Password:*"), gbc);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Display Name
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Display Name:*"), gbc);
        JTextField displayNameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(displayNameField, gbc);

        // Security Questions
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Security Q1:*"), gbc);
        JTextField securityQ1Field = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(securityQ1Field, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Answer 1:*"), gbc);
        JTextField answer1Field = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(answer1Field, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Security Q2:*"), gbc);
        JTextField securityQ2Field = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(securityQ2Field, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Answer 2:*"), gbc);
        JTextField answer2Field = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(answer2Field, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save User");
        JButton cancelButton = new JButton("Cancel");

        saveButton.setBackground(new Color(255, 175, 204));
        saveButton.setForeground(new Color(74, 74, 74));
        cancelButton.setBackground(Color.LIGHT_GRAY);

        saveButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (saveNewUser(usernameField.getText(),
                          password,
                          displayNameField.getText(),
                          securityQ1Field.getText(),
                          answer1Field.getText(),
                          securityQ2Field.getText(),
                          answer2Field.getText(),
                          confirmPassword)) {
                dialog.dispose();
                loadUsersFromDatabase();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        JLabel infoLabel = new JLabel("* All fields are required");
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
        infoLabel.setForeground(Color.GRAY);
        buttonPanel.add(infoLabel);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private boolean saveNewUser(String username, String password, String displayName,
                               String securityQ1, String answer1,
                               String securityQ2, String answer2, String confirmPassword) {

        // Validation
        if (username.isEmpty() || password.isEmpty() || displayName.isEmpty() ||
            securityQ1.isEmpty() || answer1.isEmpty() || securityQ2.isEmpty() || answer2.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "All fields are required.",
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
}


