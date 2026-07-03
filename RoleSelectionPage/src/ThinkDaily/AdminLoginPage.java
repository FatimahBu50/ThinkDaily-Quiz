package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// NEW imports for DB + hashing
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AdminLoginPage extends JFrame {

    // 2. Rounded panel with soft shadow effect
    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;

        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 0;
            int y = 0;
            int w = getWidth();
            int h = getHeight();

            // shadow
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(x + 4, y + 6, w - 8, h - 8, cornerRadius + 10, cornerRadius + 10);

            // main card
            g2.setColor(new Color(252, 249, 254));
            g2.fillRoundRect(x, y, w - 8, h - 8, cornerRadius, cornerRadius);

            // soft border
            g2.setColor(new Color(245, 220, 230));
            g2.drawRoundRect(x, y, w - 8, h - 8, cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public AdminLoginPage() {
        setTitle("ThinkDaily - Admin Login");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // LEFT PANEL (TEXT + FOOTER)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(480, 700));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // BACK BUTTON
        JButton backBtn = new JButton("⟵ Back");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setForeground(new Color(150, 130, 155));

        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backBtn.setForeground(new Color(110, 90, 120));
            }
            public void mouseExited(MouseEvent e) {
                backBtn.setForeground(new Color(150, 130, 155));
            }
        });

        backBtn.addActionListener(e -> {
            new RoleSelectionPage().setVisible(true);
            dispose();
        });

        JPanel backContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        backContainer.setOpaque(false);
        backContainer.add(backBtn);

        leftPanel.add(backContainer, BorderLayout.NORTH);

        // CENTER CONTENT (logo + text)
        JPanel centerContent = new JPanel();
        centerContent.setBackground(Color.WHITE);
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));

        centerContent.add(Box.createVerticalGlue());

        // LOGO
        ImageIcon rawLogo = new ImageIcon(getClass().getResource("/logo/logo.png"));
        Image scaledImg = rawLogo.getImage().getScaledInstance(190, 190, Image.SCALE_SMOOTH);
        ImageIcon scaledLogo = new ImageIcon(scaledImg);

        JLabel logoLabel = new JLabel(scaledLogo);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContent.add(logoLabel);
        centerContent.add(Box.createVerticalStrut(18));

        // TEXTS
        JLabel helloLabel = new JLabel("Hello, Admin!", SwingConstants.CENTER);
        helloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        helloLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        helloLabel.setForeground(new Color(100, 90, 120));
        centerContent.add(helloLabel);
        centerContent.add(Box.createVerticalStrut(12));

        JLabel line1 = new JLabel("Manage ThinkDaily and keep quizzes fair.", SwingConstants.CENTER);
        line1.setAlignmentX(Component.CENTER_ALIGNMENT);
        line1.setFont(new Font("SansSerif", Font.PLAIN, 13));
        line1.setForeground(new Color(135, 125, 150));
        centerContent.add(line1);
        centerContent.add(Box.createVerticalStrut(5));

        JLabel line2 = new JLabel("Review players, questions, and reports easily.", SwingConstants.CENTER);
        line2.setAlignmentX(Component.CENTER_ALIGNMENT);
        line2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        line2.setForeground(new Color(135, 125, 150));
        centerContent.add(line2);

        centerContent.add(Box.createVerticalGlue());
        leftPanel.add(centerContent, BorderLayout.CENTER);

        // FOOTER
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        footerPanel.setBackground(Color.WHITE);

        JButton policiesBtn = createSmallLink("Policies");
        JButton guidelinesBtn = createSmallLink("Guidelines");
        JButton helpBtn = createSmallLink("Help");

        policiesBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, getPoliciesText(), "Policies", JOptionPane.INFORMATION_MESSAGE));
        guidelinesBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, getGuidelinesText(), "Guidelines", JOptionPane.INFORMATION_MESSAGE));
        helpBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, getHelpText(), "Help", JOptionPane.INFORMATION_MESSAGE));

        footerPanel.add(policiesBtn);
        footerPanel.add(guidelinesBtn);
        footerPanel.add(helpBtn);
        leftPanel.add(footerPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        // RIGHT PANEL (form)
        JPanel rightPanel = new GradientPanel(); // assume you already have this class
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Rounded card for the form
        RoundedPanel formCard = new RoundedPanel(30);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);
        formCard.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        JLabel loginTitle = new JLabel("Admin Login");
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        loginTitle.setForeground(new Color(247, 157, 177));

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(320, 40));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createTitledBorder("Admin Username "));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(320, 40));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JButton LogInBtn = new JButton("Login");
        LogInBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        LogInBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        LogInBtn.setBackground(new Color(255, 204, 224));
        LogInBtn.setForeground(new Color(80, 60, 70));
        LogInBtn.setFocusPainted(false);
        LogInBtn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        LogInBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color normalColor = new Color(255, 204, 224);
        Color hoverColor = new Color(255, 189, 215);
        LogInBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                LogInBtn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                LogInBtn.setBackground(normalColor);
            }
        });

        // DATABASE LOGIN FUNCTIONALITY
        LogInBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both username and password.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (authenticateAdmin(user, pass)) {
                AdminDetails details = getAdminDetails(user);
                String welcomeName = (details != null && details.getFullName() != null && !details.getFullName().isEmpty())
                        ? details.getFullName() : user;

                JOptionPane.showMessageDialog(this,
                        "Welcome, " + welcomeName + "!",
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                new AdminDashboardFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid admin credentials or account not active.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        formCard.add(loginTitle);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(usernameField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(passwordField);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(LogInBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(formCard, gbc);

        add(rightPanel, BorderLayout.CENTER);
    }

    private JButton createSmallLink(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(new Color(150, 140, 150));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Text content methods
    private String getHelpText() {
        return """
                Welcome Admin!

                • Log in using your admin credentials.
                • Review and manage quiz questions.
                • Monitor players and leaderboard.
                • Need help? Contact: admin-support@thinkdaily.app
                """;
    }

    private String getGuidelinesText() {
        return """
                ThinkDaily Admin Guidelines

                1. Use admin powers responsibly.
                2. Ensure questions are fair and appropriate.
                3. Protect user privacy and data.
                4. Review reports and flags regularly.
                """;
    }

    private String getPoliciesText() {
        return """
                ThinkDaily Admin Policies

                • Admin accounts are granted by the system owner.
                • Some admin actions may be logged for security.
                • Misuse of admin access may result in removal.
                """;
    }

    // DATABASE LOGIC
    public static boolean authenticateAdmin(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;

            String sql = "SELECT password_hash, role, is_active FROM accounts WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("is_active");

                if (!"ADMIN".equals(role) || !isActive) return false;

                String inputHash = hashPassword(password);
                return inputHash != null && inputHash.equals(storedHash);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } 
            catch (SQLException e) { System.out.println("Error closing resources: " + e.getMessage()); }
        }

        return false;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Encryption algorithm error: " + e.getMessage());
            return null;
        }
    }

    public static AdminDetails getAdminDetails(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            if (conn == null) return null;

            String sql = """
                SELECT a.account_id, a.username, adm.admin_id, adm.full_name
                FROM accounts a
                JOIN admins adm ON a.account_id = adm.account_id
                WHERE a.username = ? AND a.role = 'ADMIN' AND a.is_active = true
                """;

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new AdminDetails(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getInt("admin_id"),
                        rs.getString("full_name")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching admin details: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } 
            catch (SQLException e) { System.out.println("Error closing resources: " + e.getMessage()); }
        }

        return null;
    }

    public static class AdminDetails {
        private int accountId;
        private String username;
        private int adminId;
        private String fullName;

        public AdminDetails(int accountId, String username, int adminId, String fullName) {
            this.accountId = accountId;
            this.username = username;
            this.adminId = adminId;
            this.fullName = fullName;
        }

        public int getAccountId() { return accountId; }
        public String getUsername() { return username; }
        public int getAdminId() { return adminId; }
        public String getFullName() { return fullName; }
    }
}
