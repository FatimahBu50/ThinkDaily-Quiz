package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.MessageDigest;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ForgotPasswordDialog extends JDialog {

    public ForgotPasswordDialog(JFrame parent) {
        super(parent, "Recover Password", true); 
        setSize(450, 420);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            // only open a new login page if there isn't already one
            new PlayerLoginPage().setVisible(true);
        }
    });

        //  MAIN BACKGROUND
        JPanel mainPanel = new RoundedPanel(25);
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // TITLE
        JLabel title = new JLabel("Recover Your Password", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(80, 60, 90));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Answer the security question to reset your password.", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(140, 125, 150));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== Username =====
        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(300, 40));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        // ===== Security question =====
        String[] questions = {
                "Select a question",
                "What is your favorite color?",
                "What is your pet's name?",
                "What city were you born in?",
                "What is your favorite food?",
                "What is the name of your best friend?"
        };

        JComboBox<String> questionCombo = new JComboBox<>(questions);
        questionCombo.setMaximumSize(new Dimension(300, 48));
        questionCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        questionCombo.setBorder(BorderFactory.createTitledBorder("Security Question"));

        // Answer field
        JTextField answerField = new JTextField();
        answerField.setMaximumSize(new Dimension(300, 40));
        answerField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        answerField.setBorder(BorderFactory.createTitledBorder("Your Answer"));

        // BUTTONS
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        JButton submitBtn = new JButton("Submit");
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitBtn.setBackground(new Color(255, 204, 224));
        submitBtn.setForeground(new Color(80, 60, 70));
        submitBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color normal = new Color(255, 204, 224);
        Color hover = new Color(255, 185, 212);

        submitBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                submitBtn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                submitBtn.setBackground(normal);
            }
        });

        // ============= Submit logic (NOW with DB + reset panel) =============
        submitBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String question = (String) questionCombo.getSelectedItem();
            String answer   = answerField.getText().trim();

            // Basic validation
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(
                        ForgotPasswordDialog.this,
                        "Please enter your username",
                        "Missing username",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (questionCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(
                        ForgotPasswordDialog.this,
                        "Please select a security question",
                        "No question selected",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (answer.isEmpty()) {
                JOptionPane.showMessageDialog(
                        ForgotPasswordDialog.this,
                        "Please type your answer to the security question ",
                        "Missing answer",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // ===== Check against DB: accounts table =====
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(
                            ForgotPasswordDialog.this,
                            "Cannot connect to the database right now.\nPlease try again later.",
                            "Connection error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                String sql = """
                        SELECT account_id,
                               security_question1, security_ans1,
                               security_question2, security_ans2,
                               is_active
                        FROM accounts
                        WHERE username = ?
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            JOptionPane.showMessageDialog(
                                    ForgotPasswordDialog.this,
                                    "No account found with that username.",
                                    "Account not found",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }

                        if (rs.getInt("is_active") == 0) {
                            JOptionPane.showMessageDialog(
                                    ForgotPasswordDialog.this,
                                    "This account is not active.",
                                    "Inactive account",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }

                        int accountId = rs.getInt("account_id");
                        String dbQ1 = rs.getString("security_question1");
                        String dbA1 = rs.getString("security_ans1");
                        String dbQ2 = rs.getString("security_question2");
                        String dbA2 = rs.getString("security_ans2");

                        boolean correct = false;

                        // normalize
                        String userAnsNorm = answer.trim().toLowerCase();

                        if (question.equals(dbQ1)) {
                            if (dbA1 != null && userAnsNorm.equals(dbA1.trim().toLowerCase())) {
                                correct = true;
                            }
                        } else if (question.equals(dbQ2)) {
                            if (dbA2 != null && userAnsNorm.equals(dbA2.trim().toLowerCase())) {
                                correct = true;
                            }
                        } else {
                            JOptionPane.showMessageDialog(
                                    ForgotPasswordDialog.this,
                                    "Selected question does not match the one saved for this account.",
                                    "Wrong question",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }

                        if (!correct) {
                            JOptionPane.showMessageDialog(
                                    ForgotPasswordDialog.this,
                                    "Security answer is incorrect.",
                                    "Wrong answer",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }

                        // At this point: security check passed → show reset password panel
                        showResetPasswordDialog(accountId, username);

                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        ForgotPasswordDialog.this,
                        "An error occurred:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cancelBtn.setContentAreaFilled(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setForeground(new Color(150, 120, 160));
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cancelBtn.addActionListener(e -> {
            new PlayerLoginPage().setVisible(true);
            dispose();
        });

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        // ADD COMPONENTS
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(questionCombo);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(answerField);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(buttonPanel);

        // Wrap in background panel
        JPanel wrapper = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 240, 247));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        wrapper.setLayout(new GridBagLayout());
        wrapper.add(mainPanel);

        setContentPane(wrapper);
    }

    // ================= RESET PASSWORD PANEL (after security OK) =================
    private void showResetPasswordDialog(int accountId, String username) {

        JPasswordField newPass = new JPasswordField();
        JPasswordField confirmPass = new JPasswordField();

        Dimension fieldSize = new Dimension(230, 30);
        for (JPasswordField f : new JPasswordField[]{newPass, confirmPass}) {
            f.setPreferredSize(fieldSize);
            f.setMaximumSize(fieldSize);
            f.setMinimumSize(fieldSize);
            f.setFont(new Font("SansSerif", Font.PLAIN, 13));
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 210, 235), 1, true),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
        }

        // strength bar
        JProgressBar strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(true);
        strengthBar.setValue(0);
        strengthBar.setPreferredSize(new Dimension(230, 12));
        strengthBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        strengthBar.setBackground(new Color(248, 243, 252));
        strengthBar.setString("-");

        JLabel strengthLabel = new JLabel("Password strength");
        strengthLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        strengthLabel.setForeground(new Color(130, 115, 150));

        newPass.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String pw = new String(newPass.getPassword());
                int score = calculatePasswordStrength(pw);
                animateStrength(strengthBar, score);

                String text = getStrengthLabel(score);
                strengthBar.setString(text);
                if (score < 40) {
                    strengthBar.setForeground(new Color(255, 140, 140));
                } else if (score < 80) {
                    strengthBar.setForeground(new Color(255, 200, 140));
                } else {
                    strengthBar.setForeground(new Color(170, 215, 170));
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        JPanel newRow = createPasswordRow("New password", newPass);
        JPanel confirmRow = createPasswordRow("Confirm new password", confirmPass);

        JPanel strengthRow = new JPanel();
        strengthRow.setOpaque(false);
        strengthRow.setLayout(new BoxLayout(strengthRow, BoxLayout.Y_AXIS));
        strengthRow.add(strengthLabel);
        strengthRow.add(Box.createVerticalStrut(4));
        strengthRow.add(strengthBar);
        strengthRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(252, 248, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 205, 240), 1, true),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)
        ));

        panel.add(newRow);
        panel.add(Box.createVerticalStrut(6));
        panel.add(strengthRow);
        panel.add(Box.createVerticalStrut(8));
        panel.add(confirmRow);

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Set New Password for " + username,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                // user cancelled
                break;
            }

            String newPw = new String(newPass.getPassword()).trim();
            String confirmPw = new String(confirmPass.getPassword()).trim();

            if (newPw.isEmpty() || confirmPw.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please fill in all fields.",
                        "Missing information",
                        JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            if (!meetsStrongPolicy(newPw)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password must be at least 8 characters and include:\n" +
                                "- lowercase letters\n" +
                                "- UPPERCASE letters\n" +
                                "- numbers\n" +
                                "- symbols (e.g. !, @, #, ?)\n",
                        "Password too weak",
                        JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            if (!newPw.equals(confirmPw)) {
                JOptionPane.showMessageDialog(
                        this,
                        "New password and confirmation don’t match!",
                        "Mismatch",
                        JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            // ask gently if still weak-ish
            int strengthScore = calculatePasswordStrength(newPw);
            if (strengthScore < 60) {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Your password is valid but still a bit weak.\n" +
                                "Do you still want to use it?",
                        "Low strength",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (choice != JOptionPane.YES_OPTION) {
                    continue;
                }
            }

            // ===== save hashed password to DB =====
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Cannot connect to the database right now.\nPlease try again later.",
                            "Connection error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    break;
                }

                String newHash = hashPasswordSHA256(newPw);

                String updateSql = "UPDATE accounts SET password_hash = ? WHERE account_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, newHash);
                    ps.setInt(2, accountId);

                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        JOptionPane.showMessageDialog(
                                this,
                                createSuccessPanel(
                                        "Password reset successfully 🎉",
                                        "You can now log in with your new password."
                                ),
                                "Password Reset",
                                JOptionPane.PLAIN_MESSAGE
                        );
                        dispose(); // close ForgotPasswordDialog
                        new PlayerLoginPage().setVisible(true);
                        break;
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "Something went wrong. No changes were saved.",
                                "Update error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "An error occurred while updating your password:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // ===== helper: row with eye button =====
    private JPanel createPasswordRow(String labelText, JPasswordField field) {
        JLabel label = new JLabel(labelText + ":");
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(90, 75, 110));

        JButton eyeBtn = new JButton("👁");
        eyeBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        eyeBtn.setFocusable(false);
        eyeBtn.setContentAreaFilled(false);
        eyeBtn.setBorderPainted(false);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final char[] defaultEcho = {field.getEchoChar()};
        eyeBtn.addActionListener(e -> {
            if (field.getEchoChar() == 0) {
                field.setEchoChar(defaultEcho[0]);
            } else {
                defaultEcho[0] = field.getEchoChar();
                field.setEchoChar((char) 0);
            }
        });

        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));

        JPanel fieldRow = new JPanel(new BorderLayout(6, 0));
        fieldRow.setOpaque(false);
        fieldRow.add(field, BorderLayout.CENTER);
        fieldRow.add(eyeBtn, BorderLayout.EAST);

      	label.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(label);
        row.add(Box.createVerticalStrut(2));
        row.add(fieldRow);

        return row;
    }

    // ===== strength helpers =====
    private int calculatePasswordStrength(String pw) {
        if (pw == null) return 0;
        int score = 0;

        int len = pw.length();
        if (len >= 6) score += 20;
        if (len >= 8) score += 30;
        if (len >= 12) score += 20;

        if (pw.matches(".*[0-9].*")) score += 15;
        if (pw.matches(".*[a-z].*")) score += 15;
        if (pw.matches(".*[A-Z].*")) score += 20;
        if (pw.matches(".*[^a-zA-Z0-9].*")) score += 30;

        return Math.min(score, 100);
    }

    private boolean meetsStrongPolicy(String pw) {
        if (pw == null) return false;
        if (pw.length() < 8) return false;
        boolean hasLower = pw.matches(".*[a-z].*");
        boolean hasUpper = pw.matches(".*[A-Z].*");
        boolean hasDigit = pw.matches(".*[0-9].*");
        boolean hasSymbol = pw.matches(".*[^a-zA-Z0-9].*");
        return hasLower && hasUpper && hasDigit && hasSymbol;
    }

    private String getStrengthLabel(int score) {
        if (score == 0) return "-";
        else if (score < 40) return "Weak";
        else if (score < 80) return "Medium";
        else return "Strong";
    }

    private void animateStrength(JProgressBar bar, int target) {
        int start = bar.getValue();
        if (start == target) return;

        int step = (target > start) ? 1 : -1;

        javax.swing.Timer timer = new javax.swing.Timer(10, null);
        timer.addActionListener(e -> {
            int current = bar.getValue();
            if ((step > 0 && current >= target) || (step < 0 && current <= target)) {
                bar.setValue(target);
                timer.stop();
            } else {
                bar.setValue(current + step);
            }
        });
        timer.start();
    }

    // ===== SHA-256 hashing (same idea you used before) =====
    private String hashPasswordSHA256(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // ===== cute success panel (same style as before) =====
    private JPanel createSuccessPanel(String title, String message) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 28;

                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(6, 8, w - 12, h - 12, arc + 10, arc + 10);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 247, 255),
                        w, h, new Color(241, 222, 250)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w - 12, h - 12, arc, arc);

                g2.setColor(new Color(222, 188, 235));
                g2.drawRoundRect(0, 0, w - 12, h - 12, arc, arc);

                g2.setColor(new Color(245, 210, 230));
                g2.fillRoundRect(16, 42, w - 44, 3, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(82, 60, 125));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea msgArea = new JTextArea(message);
        msgArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        msgArea.setForeground(new Color(110, 86, 140));
        msgArea.setEditable(false);
        msgArea.setOpaque(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setFocusable(false);
        msgArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(msgArea);
        textPanel.add(Box.createVerticalStrut(4));

        card.add(textPanel, BorderLayout.CENTER);

        outer.add(card);
        return outer;
    }

    // Rounded Card Panel
    private static class RoundedPanel extends JPanel {
        private final int radius;
        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(252, 249, 254));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(245, 220, 230));
            g2.drawRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }
}

