package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class SignUpPage extends JFrame {

    public SignUpPage() {

        setTitle("ThinkDaily - Sign Up");
        setSize(1150, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        //  LEFT PANEL (logo + welcome text + footer)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(480, 700));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // BACK BUTTON (Top Left)
        JButton backBtn2 = new JButton("⟵ Back");
        backBtn2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn2.setContentAreaFilled(false);
        backBtn2.setBorderPainted(false);
        backBtn2.setFocusPainted(false);
        backBtn2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn2.setForeground(new Color(150, 130, 155));

        // hover effects
        backBtn2.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backBtn2.setForeground(new Color(110, 90, 120));
            }
            public void mouseExited(MouseEvent e) {
                backBtn2.setForeground(new Color(150, 130, 155));
            }
        });

        // back to role selection
        backBtn2.addActionListener(e -> {
            new PlayerLoginPage().setVisible(true);
            dispose();
        });

        // small container to align left
        JPanel backContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        backContainer.setOpaque(false);
        backContainer.add(backBtn2);

        // add into the TOP of leftPanel
        leftPanel.add(backContainer, BorderLayout.NORTH);

        //  CENTER CONTENT (logo + text perfectly centered)
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
        centerContent.add(Box.createVerticalStrut(20));

        // TEXTS (centered)
        JLabel helloLabel = new JLabel("Hello, welcome!", SwingConstants.CENTER);
        helloLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        helloLabel.setForeground(new Color(100, 90, 120));
        helloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel line1 = new JLabel("Join us and enjoy your daily quiz journey.", SwingConstants.CENTER);
        line1.setFont(new Font("SansSerif", Font.PLAIN, 13));
        line1.setForeground(new Color(135, 125, 150));
        line1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel line2 = new JLabel("We are here to make learning light and fun.", SwingConstants.CENTER);
        line2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        line2.setForeground(new Color(135, 125, 150));
        line2.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerContent.add(helloLabel);
        centerContent.add(Box.createVerticalStrut(10));
        centerContent.add(line1);
        centerContent.add(Box.createVerticalStrut(5));
        centerContent.add(line2);

        centerContent.add(Box.createVerticalGlue());

        leftPanel.add(centerContent, BorderLayout.CENTER);

        // FOOTER LINKS (inside left panel)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        footerPanel.setBackground(Color.WHITE);

        JButton policiesBtn = createSmallLink("Policies");
        JButton guidelinesBtn = createSmallLink("Guidelines");
        JButton helpBtn = createSmallLink("Help");

        policiesBtn.addActionListener(e ->
                InfoDialog.show(this, "Policies", getPoliciesText())
        );
        guidelinesBtn.addActionListener(e ->
                InfoDialog.show(this, "Guidelines", getGuidelinesText())
        );
        helpBtn.addActionListener(e ->
                InfoDialog.show(this, "Help", getHelpText())
        );

        footerPanel.add(policiesBtn);
        footerPanel.add(guidelinesBtn);
        footerPanel.add(helpBtn);

        leftPanel.add(footerPanel, BorderLayout.SOUTH);

        // Add Left Panel to frame
        add(leftPanel, BorderLayout.WEST);

        // RIGHT PANEL (gradient background + sign up card)
        JPanel rightPanel = new GradientPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        RoundedPanel formCard = new RoundedPanel(30);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);
        formCard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        formCard.setPreferredSize(new Dimension(500, 700)); // adjust height if needed


        JLabel signupTitle = new JLabel("Sign Up");
        signupTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        signupTitle.setForeground(new Color(70, 60, 90));

        // =========================
        // Form of Sign up
        // =========================

        // Username
        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // ✅ responsive
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Gender
        String[] genders = {
                "Select gender",
                "Female",
                "Male",
                "Prefer not to say"
        };
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        genderCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); // ✅ responsive
        genderCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        genderCombo.setBorder(BorderFactory.createTitledBorder("Gender"));
        genderCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Date of Birth (instead of manual age)
        JSpinner dobSpinner = new JSpinner(new SpinnerDateModel());
        dobSpinner.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // show as day / month / year
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dobSpinner, "dd/MM/yyyy");
        dobSpinner.setEditor(dateEditor);

        // wrap in a panel with a titled border (same style as text fields)
        JPanel dobPanel = new JPanel(new BorderLayout());
        dobPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // ✅ responsive
        dobPanel.setBorder(BorderFactory.createTitledBorder("Date of Birth"));
        dobPanel.add(dobSpinner, BorderLayout.CENTER);
        dobPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password + Confirm (we keep fields as-is but wrap for eye)
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // ✅ Eye + field in same row (beside it)
        JPanel passwordRow = createPasswordWithEyeRow("Password", passwordField);
        JPanel confirmRow  = createPasswordWithEyeRow("Confirm Password", confirmPasswordField);

        // ✅ Strength bar (visible)
        JLabel strengthLabel = new JLabel("Password strength:");
        strengthLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        strengthLabel.setForeground(new Color(120, 110, 140));
        strengthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar strengthBar = new JProgressBar(0, 100);
        strengthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));
        strengthBar.setStringPainted(true);
        strengthBar.setValue(0);
        strengthBar.setString("-");
        strengthBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        strengthBar.setBackground(new Color(248, 243, 252));
        strengthBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String pw = new String(passwordField.getPassword());
                int score = calculatePasswordStrength(pw);
                strengthBar.setValue(score);

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

        // ===== Security Questions =====
        JLabel securityLabel = new JLabel("Security Questions (for password recovery):");
        securityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        securityLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        securityLabel.setForeground(new Color(120, 110, 140));

        String[] questions = {
                "Select a question",
                "What is your favorite color?",
                "What is your pet's name?",
                "What city were you born in?",
                "What is your favorite food?",
                "What is the name of your best friend?"
        };

        JComboBox<String> q1Combo = new JComboBox<>(questions);
        q1Combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // ✅ responsive
        q1Combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        q1Combo.setBorder(BorderFactory.createTitledBorder("Question 1"));
        q1Combo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField a1Field = new JTextField();
        a1Field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // ✅ responsive
        a1Field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        a1Field.setBorder(BorderFactory.createTitledBorder("Answer 1"));
        a1Field.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<String> q2Combo = new JComboBox<>(questions);
        q2Combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // ✅ responsive
        q2Combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        q2Combo.setBorder(BorderFactory.createTitledBorder("Question 2"));
        q2Combo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField a2Field = new JTextField();
        a2Field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // ✅ responsive
        a2Field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        a2Field.setBorder(BorderFactory.createTitledBorder("Answer 2"));
        a2Field.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== Buttons =====
        JButton createBtn = new JButton("Create Account");
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        createBtn.setBackground(new Color(255, 204, 224));
        createBtn.setForeground(new Color(80, 60, 70));
        createBtn.setFocusPainted(false);
        createBtn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color normalColor = new Color(255, 204, 224);
        Color hoverColor = new Color(255, 189, 215);

        createBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                createBtn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                createBtn.setBackground(normalColor);
            }
        });

        // Action: create account in DB
        createBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmPasswordField.getPassword());
            String q1 = (String) q1Combo.getSelectedItem();
            String a1 = a1Field.getText().trim();
            String q2 = (String) q2Combo.getSelectedItem();
            String a2 = a2Field.getText().trim();

            // ✅ Username rules: no spaces + minimum letters
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a username.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (username.contains(" ")) {
                JOptionPane.showMessageDialog(this, "Username must not contain spaces.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int MIN_USERNAME_LEN = 6; // ✅ you can change here
            if (username.length() < MIN_USERNAME_LEN) {
                JOptionPane.showMessageDialog(this,
                        "Username is too short. Please use at least " + MIN_USERNAME_LEN + " letters.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Password validations
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a password.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ✅ same policy as update password (strong)
            if (!meetsStrongPolicy(password)) {
                JOptionPane.showMessageDialog(this,
                        "Password must be at least 8 characters and include:\n" +
                                "- lowercase letters\n" +
                                "- UPPERCASE letters\n" +
                                "- numbers\n" +
                                "- symbols (e.g. !, @, #, ?)\n",
                        "Password too weak",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (q1 == null || q1.startsWith("Select") || a1.isEmpty() || q2 == null || q2.startsWith("Select") || a2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select and answer both security questions.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ThinkDailyQueries queries = new ThinkDailyQueries();
            boolean ok = queries.addPlayer(username, password, username, q1, a1, q2, a2);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Account created successfully. You may now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                new PlayerLoginPage().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Account creation failed. Try a different username.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backBtn = new JButton("Back to Login");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(new Color(140, 110, 150));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        backBtn.addActionListener(e -> {
            new PlayerLoginPage().setVisible(true);
            dispose();
        });

        // Add everything to form card
        formCard.add(signupTitle);
        formCard.add(Box.createVerticalStrut(18));
        formCard.add(usernameField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(genderCombo);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(dobPanel);
        formCard.add(Box.createVerticalStrut(10));

        // ✅ password row + bar + confirm row
        formCard.add(passwordRow);
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(strengthLabel);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(strengthBar);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(confirmRow);

        formCard.add(Box.createVerticalStrut(15));
        formCard.add(securityLabel);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(q1Combo);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(a1Field);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(q2Combo);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(a2Field);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(createBtn);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(backBtn);

        // Center formCard on right gradient panel
        GridBagConstraints gbc = new GridBagConstraints();
gbc.gridx = 0;
gbc.gridy = 0;
gbc.anchor = GridBagConstraints.CENTER; // ✅ center card
gbc.weightx = 0;                        // ✅ no stretching
gbc.weighty = 0;

rightPanel.add(formCard, gbc);


        add(rightPanel, BorderLayout.CENTER);
    }

    // ✅ Password field + eye beside it (same row)
    private JPanel createPasswordWithEyeRow(String title, JPasswordField field) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Keep your title style like other fields
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(120, 110, 140));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); // ✅ responsive height
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Field with titled border
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // ✅ responsive
        field.setBorder(BorderFactory.createTitledBorder(title));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton eyeBtn = new JButton("👁");
        eyeBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
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

        row.add(field, BorderLayout.CENTER);
        row.add(eyeBtn, BorderLayout.EAST);

        wrapper.add(row);
        return wrapper;
    }

    // Gradient background panel (right side)
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            Color c1 = new Color(255, 240, 246); // soft pink
            Color c2 = new Color(232, 242, 255); // soft blue
            GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }

    // Rounded card panel
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

    private int calculateAge(java.util.Date dob) {
        if (dob == null) return 0;

        LocalDate birthDate = dob.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate today = LocalDate.now();

        return Period.between(birthDate, today).getYears();
    }

    // ===== strength helpers (same idea as your update password) =====
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

    //Text content methods
    private String getHelpText() {
        return """
               Welcome to ThinkDaily! Here’s how to use the app:

               • Daily Quiz
                 You get 3 questions every day. You can answer them once to earn points and level up.

               • Categories
                 Choose a quiz category (Trivia, Riddles, Puzzles) or let the system pick randomly.

               • Points & Levels
                 Correct answers give you points. Collect points to increase your level.

               • Extended Mode
                 After finishing the daily quiz, you can play extra questions to level up.

               • Leaderboard
                 Check your ranking against other players (Daily, Weekly, All-Time).

               • Profile
                 Change your name, bio, or profile icon anytime.

               • Need help?
                 Contact support at: support@thinkdaily.app
               """;
    }

    private String getGuidelinesText() {
        return """
               ThinkDaily Guidelines

               To keep ThinkDaily fair and enjoyable for everyone:

               1. Answer honestly
                  Avoid using external help or searching for answers during the quiz.

               2. One user per account
                  Do not share your account with other people.

               3. Respect the daily limit
                  The daily quiz can only be taken once per day for points.

               4. Be kind
                  Use appropriate names and profile pictures.

               5. Play fairly
                  Any cheating or abusing the system may lead to account restriction.

               6. Report issues
                  If you find a bug or wrong question, please report it to the admin or support.
               """;
    }

    private String getPoliciesText() {
        return """
               ThinkDaily Policies

               • Data Usage
                 We store only basic information:
                 your name, encrypted password, level, points, and quiz history.

               • Privacy
                 Your information is not shared with third parties.
                 Admins can only view what is required to manage the system.

               • Security
                 Passwords are securely stored and cannot be seen by anyone.

               • Account Management
                 You may deactivate your account at any time from your profile settings.

               • Admin Rights
                 Admins can remove users, delete inappropriate accounts, and manage questions.

               • Content Accuracy
                 Quiz questions are curated by admins, but mistakes can happen.
               """;
    }
}

