package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class PlayerHomePage extends JFrame {

    // DB-related model fields 
    private final int userId;

    private String username;           // from Users table
    private int level;                 // from Users or Leaderboard
    private int points;                // from Users / Leaderboard
     private String bio;               // from Users table
    private int pointsToNextLevel;     // from your leveling rules
    private boolean dailyQuizCompleted; // from today's UserResponses
    private long millisUntilNextQuiz;   // time until next quiz unlock

    // UI components we need to update
    private JLabel usernameLabel;
    private JLabel levelLabel;
    private JLabel pointsLabel;
    private JProgressBar levelProgressBar;
    private JLabel dailyQuizStatusLabel;
    private JLabel countdownLabel;
    private JLabel motivationLabel;
    private JPanel categoriesListPanel;
    private JLabel welcomeTextLabel;
    private JLabel welcomeNameLabel;

    // categories from DB
    private List<String> allCategories = new ArrayList<>();
    
    private ImageIcon loadCategoryIcon(String path, int size) {
    try {
        ImageIcon raw = new ImageIcon(getClass().getResource(path));
        Image scaled = raw.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    } catch (Exception e) {
        System.err.println("Could not load: " + path);
        return null;
    }
}

        private JToggleButton createCategoryIconButton(String text, ImageIcon icon) {
            JToggleButton btn = new JToggleButton(text, icon) {
                private final int radius = 26;

                {
                    setHorizontalTextPosition(SwingConstants.CENTER);
                    setVerticalTextPosition(SwingConstants.BOTTOM);
                    setFocusPainted(false);
                    setContentAreaFilled(false);
                    setOpaque(false);
                    setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                    setFont(new Font("SansSerif", Font.PLAIN, 12));
                    setForeground(new Color(80, 60, 90));
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Color fill;
                    if (isSelected()) {
                        fill = new Color(255, 210, 225);
                    } else if (getModel().isRollover()) {
                        fill = new Color(248, 236, 252);
                    } else {
                        fill = new Color(245, 235, 250);
                    }

                    g2.setColor(fill);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                    g2.setColor(new Color(220, 200, 235));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            return btn;
        }


            public PlayerHomePage(int userId) {
                this.userId = userId;

                setTitle("ThinkDaily - Home");
                setSize(1150, 750);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
                getContentPane().setBackground(Color.WHITE);
                setLayout(new BorderLayout());

                //  TOP BAR
                JPanel topBar = new JPanel(new BorderLayout());
                topBar.setBackground(Color.WHITE);
                topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                // Left: User info + dropdown
                JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                userPanel.setOpaque(false);

                JLabel userIcon = new JLabel("🙂");
                userIcon.setFont(new Font("SansSerif", Font.PLAIN, 33));
                userIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

              // popup menu for user icon
        JPopupMenu userMenu = new JPopupMenu();

        // Menu styling (soft font + pastel hover)
        Font menuFont = new Font("SansSerif", Font.PLAIN, 13);
        Color hoverColor = new Color(255, 210, 225);

        class StyledMenuItem extends JMenuItem {
            public StyledMenuItem(String text) {
                super(text);
                setFont(menuFont);
                setBackground(Color.WHITE);
                setForeground(new Color(80, 60, 95));
                setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        setBackground(hoverColor);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        setBackground(Color.WHITE);
                    }
                });
            }
        }

        JMenuItem profileItem = new StyledMenuItem("My Profile");
        JMenuItem helpItem = new StyledMenuItem("Help 💡");
        JMenuItem guidelinesItem = new StyledMenuItem("Guidelines 📘");
        JMenuItem aboutItem = new StyledMenuItem("About ThinkDaily 📘");
        JMenuItem logoutItem = new StyledMenuItem("Logout 🚪");

        // Add in clean order with sections
        userMenu.add(profileItem);
        userMenu.addSeparator();
        userMenu.add(helpItem);
        userMenu.add(guidelinesItem);
        userMenu.add(aboutItem);
        userMenu.addSeparator();
        userMenu.add(logoutItem);
        

        profileItem.addActionListener(e -> {
            new PlayerProfilePage(userId,username,level,points, bio, null).setVisible(true);
            dispose();
        });
         helpItem.addActionListener(e ->
                InfoDialog.show(this, "Help", getHelpText()));
        guidelinesItem.addActionListener(e -> 
                InfoDialog.show(this, "Guidelines", getGuidelinesText()));
        aboutItem.addActionListener(e -> InfoDialog.show(this, "About ThinkDaily", getAboutText()));
        profileItem.addActionListener(e -> {
            // new PlayerProfilePage(userId, ... ) when DB is ready
            // dispose();
        });

        logoutItem.addActionListener(e -> {
            new RoleSelectionPage().setVisible(true);
            dispose();
        });

        userIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                userMenu.show(userIcon, 0, userIcon.getHeight());
            }
        });

        usernameLabel = new JLabel("User");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        usernameLabel.setForeground(new Color(80, 70, 95));

        JPanel userInfoBlock = new JPanel();
        userInfoBlock.setOpaque(false);
        userInfoBlock.setLayout(new BoxLayout(userInfoBlock, BoxLayout.Y_AXIS));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        nameRow.setOpaque(false);
        nameRow.add(userIcon);
        nameRow.add(usernameLabel);

        levelLabel = new JLabel("Level: -");
        levelLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        levelLabel.setForeground(new Color(120, 110, 140));

        pointsLabel = new JLabel("Points: -");
        pointsLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        pointsLabel.setForeground(new Color(120, 110, 140));

        levelProgressBar = new JProgressBar(0, 100);
        levelProgressBar.setPreferredSize(new Dimension(160, 10));
        levelProgressBar.setMaximumSize(new Dimension(160, 10));
        levelProgressBar.setBorder(BorderFactory.createEmptyBorder());
        levelProgressBar.setForeground(new Color(255, 176, 203)); // XP bar

        userInfoBlock.add(nameRow);
        userInfoBlock.add(levelLabel);
        userInfoBlock.add(pointsLabel);
        userInfoBlock.add(Box.createVerticalStrut(2));
        userInfoBlock.add(levelProgressBar);

        userPanel.add(userInfoBlock);
        topBar.add(userPanel, BorderLayout.WEST);

        // Center: nav bar buttons (Extended, Leaderboard)
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        navBar.setOpaque(false);

        JButton extendedPlayBtn = createNavPill("🎮 Extended");
        JButton leaderboardBtn = createNavPill("📊 Leaderboard");

        // OPEN EXTENDED PLAY PAGE
        extendedPlayBtn.addActionListener(e -> {
            String name = (username != null && !username.isEmpty()) ? username : "Player";
            new ExtendedPlaySetupPage(userId, name, level, points,"null").setVisible(true);
            dispose();
        });

        leaderboardBtn.addActionListener(e -> {
            String name = (username != null && !username.isEmpty()) ? username : "Player";
            new LeaderboardPage(userId, name, level, points).setVisible(true);
            dispose();
        });

        navBar.add(extendedPlayBtn);
        navBar.add(leaderboardBtn);

        topBar.add(navBar, BorderLayout.CENTER);

        // Right: small logo
        JLabel logoLabel = new JLabel(loadSmallLogoIcon());
        topBar.add(logoLabel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // CENTER AREA (card + categories)

        // soft gradient background
        JPanel centerBackground = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                Color c1 = new Color(255, 245, 250);
                Color c2 = new Color(235, 242, 255);
                GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        centerBackground.setOpaque(false);
        centerBackground.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(centerBackground, BorderLayout.CENTER);

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.gridx = 0;
        gbcCenter.gridy = 0;

        JPanel centerContent = new JPanel();
        centerContent.setOpaque(false);
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerBackground.add(centerContent, gbcCenter);
        
        // ===== Welcome row: "Welcome, <username>" =====
        JPanel welcomeRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        welcomeRow.setOpaque(false);
        welcomeRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        welcomeTextLabel = new JLabel("Welcome,");
        welcomeTextLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        welcomeTextLabel.setForeground(new Color(70, 60, 95));

        welcomeNameLabel = new JLabel("Player"); 
        welcomeNameLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        welcomeNameLabel.setForeground(new Color(120, 90, 145));

        welcomeRow.add(welcomeTextLabel);
        welcomeRow.add(welcomeNameLabel);

        centerContent.add(welcomeRow);

        // distance between welcome and card
        centerContent.add(Box.createVerticalStrut(15));


        //  main card: today's quiz
        RoundedPanel mainCard = new RoundedPanel(30);
        mainCard.setOpaque(false);
        mainCard.setPreferredSize(new Dimension(650, 280));
        mainCard.setLayout(new BorderLayout());
        mainCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel cardTop = new JPanel();
        cardTop.setOpaque(false);
        cardTop.setLayout(new BoxLayout(cardTop, BoxLayout.Y_AXIS));

        JLabel questionLabel = new JLabel("Are you ready for today’s challenge?", SwingConstants.CENTER);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        questionLabel.setForeground(new Color(70, 60, 95));

        dailyQuizStatusLabel = new JLabel("Daily quiz status: ...", SwingConstants.CENTER);
        dailyQuizStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dailyQuizStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dailyQuizStatusLabel.setForeground(new Color(135, 125, 155));

        // countdown label (only meaningful when quiz completed)
        countdownLabel = new JLabel("", SwingConstants.CENTER);
        countdownLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        countdownLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        countdownLabel.setForeground(new Color(150, 135, 165));

        JButton startDailyQuizBtn = new JButton("Start Today’s Quiz ▶");
        startDailyQuizBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startDailyQuizBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        startDailyQuizBtn.setBackground(new Color(255, 210, 225));
        startDailyQuizBtn.setForeground(new Color(80, 60, 70));
        startDailyQuizBtn.setFocusPainted(false);
        startDailyQuizBtn.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        startDailyQuizBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color startNormal = new Color(255, 210, 225);
        Color startHover = new Color(255, 190, 215);
        startDailyQuizBtn.addChangeListener(e -> {
            if (startDailyQuizBtn.getModel().isRollover()) {
                startDailyQuizBtn.setBackground(startHover);
            } else {
                startDailyQuizBtn.setBackground(startNormal);
            }
        });

        startDailyQuizBtn.addActionListener(e -> {
            new DailyQuizPage(userId).setVisible(true);
            dispose();
        });

        // motivational text under the button
        motivationLabel = new JLabel("", SwingConstants.CENTER);
        motivationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        motivationLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        motivationLabel.setForeground(new Color(140, 120, 155));

        // assemble cardTop
        cardTop.add(questionLabel);
        cardTop.add(Box.createVerticalStrut(6));
        cardTop.add(dailyQuizStatusLabel);
        cardTop.add(Box.createVerticalStrut(4));
        cardTop.add(countdownLabel);
        cardTop.add(Box.createVerticalStrut(8));
        cardTop.add(Box.createVerticalStrut(3));
        cardTop.add(Box.createVerticalStrut(12));
        cardTop.add(startDailyQuizBtn);
        cardTop.add(Box.createVerticalStrut(10));
        cardTop.add(motivationLabel);

        // About button
        JButton aboutBtn = new JButton("What is this? 💡");
        aboutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        aboutBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        aboutBtn.setContentAreaFilled(false);
        aboutBtn.setBorderPainted(false);
        aboutBtn.setFocusPainted(false);
        aboutBtn.setForeground(new Color(160, 140, 165));
        aboutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        aboutBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                """
                  About Today’s Quiz

                • You get 3 fun questions every day 💡
                • Answer once → earn points & level up 🏆

                You are one quiz away from becoming a genius! 🌟
                """,
                "About the Daily Quiz",
                JOptionPane.INFORMATION_MESSAGE
        ));

        cardTop.add(aboutBtn);
        cardTop.add(Box.createVerticalStrut(5));

        mainCard.add(cardTop, BorderLayout.CENTER);

        centerContent.add(mainCard);
        centerContent.add(Box.createVerticalStrut(18));

        //  CATEGORIES UNDER CARD
        JPanel categoriesOuter = new JPanel();
        categoriesOuter.setOpaque(false);
        categoriesOuter.setLayout(new BoxLayout(categoriesOuter, BoxLayout.Y_AXIS));
        categoriesOuter.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 20));
        

        JLabel catsLabel = new JLabel("Categories");
        catsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        catsLabel.setForeground(new Color(85, 75, 100));
        catsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        categoriesOuter.add(catsLabel);
        categoriesOuter.add(Box.createVerticalStrut(8));

        // panel that holds category icon-buttons
        categoriesListPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        categoriesListPanel.setOpaque(false);
        categoriesOuter.add(categoriesListPanel);

        centerContent.add(categoriesOuter);

        // Load data from DB (later) and update UI
        loadUserDataFromDatabase();
        loadCategoriesFromDatabase();
        updateUIFromModel();
    }

    //  Helpers

    private ImageIcon loadSmallLogoIcon() {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/logo/logo.png"));
            Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }

    private JButton createNavPill(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBackground(new Color(245, 232, 245));
        btn.setForeground(new Color(80, 60, 80));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ===== NEW: load category icon from /Catecories folder =====
    private ImageIcon loadCategoryIcon(String categoryName) {
    try {
        String fileName = "/Catecories/" + categoryName.toLowerCase() + ".png";
        ImageIcon raw = new ImageIcon(getClass().getResource(fileName));
        Image scaled = raw.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    } catch (Exception e) {
        System.err.println("Icon not found: " + categoryName);
        return null;
    }
}


    // ===== NEW: cute category button with icon =====
    private JButton createCategoryButton(String categoryName) {
        ImageIcon icon = loadCategoryIcon(categoryName);

        JButton btn = new JButton(categoryName, icon);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setBackground(new Color(250, 241, 252));
        btn.setForeground(new Color(100, 80, 115));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        btn.setPreferredSize(new Dimension(110, 100));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Click → go to ExtendedPlaySetupPage
        btn.addActionListener(e -> {
            String name = (username != null && !username.isEmpty()) ? username : "Player";
            new ExtendedPlaySetupPage(userId, name, level, points,categoryName).setVisible(true);
            dispose();
        });

        return btn;
    }

    // ===================== DB hooks (to implement later) =====================

    private void loadUserDataFromDatabase() {
       
    }

    private void loadCategoriesFromDatabase() {
        // TODO: Query Categories table and fill allCategories with category names.
        allCategories = new ArrayList<>();

        // TEMP: categories matching your icons' names
        allCategories.add("Random");
        allCategories.add("Math");
        allCategories.add("Science");
        allCategories.add("History");
        allCategories.add("Technology");

        updateCategoriesUI(allCategories);
    }

 private void updateCategoriesUI(List<String> categoryNames) {
    categoriesListPanel.removeAll();
    categoriesListPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 12));

    for (String cat : categoryNames) {
        ImageIcon icon = loadCategoryIcon(cat);

        JToggleButton btn = createCategoryIconButton(cat, icon);
        btn.setPreferredSize(new Dimension(110, 100));

        btn.addActionListener(e -> {
            String name = (username != null && !username.isEmpty()) ? username : "Player";
            new ExtendedPlaySetupPage(
                    userId,
                    name,
                    level,
                    points,
                    cat
            ).setVisible(true);
            dispose();
        });

        categoriesListPanel.add(btn);
    }

    categoriesListPanel.revalidate();
    categoriesListPanel.repaint();
}



    private void updateUIFromModel() {
        if (username != null) {
            usernameLabel.setText(username);
        }

        if (level > 0) {
            levelLabel.setText("Level: " + level);
        }

        if (points >= 0) {
            pointsLabel.setText("Points: " + points);
        }

        int progressPercent = 0;
        if (pointsToNextLevel > 0) {
            int currentLevelStartPoints = 0; // TODO: your leveling baseline
            int gainedThisLevel = points - currentLevelStartPoints;
            progressPercent = (int) (100.0 * gainedThisLevel / pointsToNextLevel);
            if (progressPercent < 0) progressPercent = 0;
            if (progressPercent > 100) progressPercent = 100;
        }
        levelProgressBar.setValue(progressPercent);

        if (dailyQuizCompleted) {
            dailyQuizStatusLabel.setText("Daily quiz status: Completed for today ✨");

            if (millisUntilNextQuiz > 0) {
                countdownLabel.setText(
                        "Next quiz unlocks in: " + formatDuration(millisUntilNextQuiz)
                );
            } else {
                countdownLabel.setText("");
            }

            motivationLabel.setText("Nice work today, see you tomorrow!");
        } else {
            dailyQuizStatusLabel.setText("Daily quiz status: Not completed yet.");
            countdownLabel.setText("");
            motivationLabel.setText("You’re one quiz away from leveling up!");
        }
    }

    private String formatDuration(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        if (hours <= 0 && minutes <= 0) {
            return "a few minutes";
        } else if (hours <= 0) {
            return minutes + " min";
        } else {
            return hours + "h " + minutes + "m";
        }
    }
// ========== Static texts for settings ==========
        private String getHelpText() {
            return """
                   Welcome to ThinkDaily! Here’s how to use the app:

                   • Daily Quiz
                     You get 3 questions every day. You can answer them once to earn points and level up.

                   • Categories
                     Choose a quiz category (Trivia, Riddles, Puzzles) or let the system pick randomly.

                   • Points & Levels
                     Correct answers give you points to increase your level.

                   • Extended Mode
                     After finishing the daily quiz, you can play extra questions for extra points.

                   • Profile
                     Change your name, bio, or profile icon anytime.
                   """;
        }

        private String getGuidelinesText() {
            return """
                   ThinkDaily Guidelines

                   1. Answer honestly.
                   2. One user per account.
                   3. Respect the daily quiz limit.
                   4. Use kind names and icons.
                   5. Play fairly – no cheating.
                   """;
        }

        private String getAboutText() {
            return """
                   About ThinkDaily

                   ThinkDaily is a small daily-quiz app designed
                   to make learning feel light, cozy, and fun.

                   • 3 new questions every day
                   • Cute levels and points
                   • Friendly competition on the leaderboard

                   Thank you for playing with us ✨
                   """;
        }

    // Rounded card panel
    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;

        public RoundedPanel(int cornerRadius) {
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // soft shadow
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(6, 8, w - 12, h - 12, cornerRadius + 12, cornerRadius + 12);

            // main pastel card
            g2.setColor(new Color(252, 249, 254));
            g2.fillRoundRect(0, 0, w - 12, h - 12, cornerRadius, cornerRadius);

            // border
            g2.setColor(new Color(245, 220, 230));
            g2.drawRoundRect(0, 0, w - 12, h - 12, cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}