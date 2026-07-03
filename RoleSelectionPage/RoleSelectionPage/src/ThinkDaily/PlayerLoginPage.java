package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PlayerLoginPage extends JFrame {

    public PlayerLoginPage() {
        setTitle("ThinkDaily - Player Login");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // LEFT PANEL (TEXT + FOOTER)
       JPanel leftPanel = new JPanel(new BorderLayout());
       leftPanel.setPreferredSize(new Dimension(480, 700));
       leftPanel.setBackground(Color.WHITE);
       leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

       // BACK BUTTON (Top Left) 
       JButton backBtn = new JButton("⟵ Back");
       backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
       backBtn.setContentAreaFilled(false);
       backBtn.setBorderPainted(false);
       backBtn.setFocusPainted(false);
       backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
       backBtn.setForeground(new Color(150, 130, 155));
       
       //  back to role select
       backBtn.addActionListener(e -> {
           new RoleSelectionPage().setVisible(true);
           dispose();
       });

       // hover effects
       backBtn.addMouseListener(new MouseAdapter() {
           public void mouseEntered(MouseEvent e) {
               backBtn.setForeground(new Color(110, 90, 120));
           }
           public void mouseExited(MouseEvent e) {
               backBtn.setForeground(new Color(150, 130, 155));
           }
       });

       // small container to align left 
       JPanel backContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
       backContainer.setOpaque(false);
       backContainer.add(backBtn);

       // add into the TOP of leftPanel
       leftPanel.add(backContainer, BorderLayout.NORTH);

       // CENTER CONTENT (logo + text centered)
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

       //TEXTS
       JLabel helloLabel = new JLabel("Hello, welcome!", SwingConstants.CENTER);
       helloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
       helloLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
       helloLabel.setForeground(new Color(100, 90, 120));
       centerContent.add(helloLabel);
       centerContent.add(Box.createVerticalStrut(12));

       JLabel line1 = new JLabel("Join us and enjoy your daily quiz journey.", SwingConstants.CENTER);
       line1.setAlignmentX(Component.CENTER_ALIGNMENT);
       line1.setFont(new Font("SansSerif", Font.PLAIN, 13));
       line1.setForeground(new Color(135, 125, 150));
       centerContent.add(line1);
       centerContent.add(Box.createVerticalStrut(5));

       JLabel line2 = new JLabel("We are here to make learning light and fun.", SwingConstants.CENTER);
       line2.setAlignmentX(Component.CENTER_ALIGNMENT);
       line2.setFont(new Font("SansSerif", Font.PLAIN, 13));
       line2.setForeground(new Color(135, 125, 150));
       centerContent.add(line2);

       centerContent.add(Box.createVerticalGlue());

       // Add centered content to the left side
       leftPanel.add(centerContent, BorderLayout.CENTER);

       // FOOTER (Policies, Guidelines, Help)
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

        //RIGHT PANEL  (login card)
        JPanel rightPanel = new GradientPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Rounded card for the form
        RoundedPanel formCard = new RoundedPanel(30);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);
        formCard.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        JLabel loginTitle = new JLabel("Player Login");
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        loginTitle.setForeground(new Color(247, 157, 177));

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(320, 40));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username / Email"));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(320, 40));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JButton LogInBtn = new JButton("Login");

        LogInBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        LogInBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        LogInBtn.setBackground(new Color(247, 157, 177));
        LogInBtn.setForeground(new Color(80, 60, 70));
        LogInBtn.setFocusPainted(false);
        LogInBtn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        LogInBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        LogInBtn.addActionListener(e -> {
          String username = usernameField.getText().trim();
          String password = new String(passwordField.getPassword());
          if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
          }
          ThinkDailyQueries queries = new ThinkDailyQueries();
          int userId = queries.authenticatePlayer(username, password);
          if (userId > 0) {
            PlayerHomePage homePage = new PlayerHomePage(userId);
            homePage.setVisible(true);
            dispose();
          } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials or account inactive.", "Login Failed", JOptionPane.ERROR_MESSAGE);
          }
        });

        // Hover effect for sign in button
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

        // "Don't have an account? Sign up" label
        JPanel signupPanel = new JPanel();
        signupPanel.setBackground(new Color(250, 248, 252));

        JLabel noAccountLabel = new JLabel("Don't have an account? ");
        noAccountLabel.setForeground(new Color(120, 110, 130));
        noAccountLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JButton signUpLink = new JButton("Sign up");
        signUpLink.setFont(new Font("SansSerif", Font.BOLD, 12));
        signUpLink.setForeground(new Color(150, 100, 180)); // cute lavender-pink
        signUpLink.setContentAreaFilled(false);
        signUpLink.setBorderPainted(false);
        signUpLink.setFocusPainted(false);
        signUpLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        //  action to signup button
        signUpLink.addActionListener(e -> {
            new SignUpPage().setVisible(true);
            dispose();
        });

        // combine text + link
        signupPanel.add(noAccountLabel);
        signupPanel.add(signUpLink);

        formCard.add(loginTitle);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(usernameField);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(passwordField);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(LogInBtn);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(signupPanel);

        // center formCard in the right panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(formCard, gbc);

        // Add both sides to frame
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
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

    // Rounded panel 
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
