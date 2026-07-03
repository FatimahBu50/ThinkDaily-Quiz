package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExtendedPlaySetupPage extends JFrame {

    private final int userId;

    // user info (DB truth)
    private String username;
    private int points;      // source of truth
    private int effectiveLevel;
    private String bio;

    // leveling rule (ONE constant)
    private static final int POINTS_PER_LEVEL = 100; // Use a constant for level calculation

    // category name -> ID (optional for DB)
    private final Map<String, Integer> categoryMap = new LinkedHashMap<>();

    // UI state
    private String selectedCategoryLabel;
    private int questionCount = 5;      // default
    private final int MIN_QUESTIONS = 3;
    private final int MAX_QUESTIONS = 15;
    private JLabel questionCountLabel;

    private static final String CATEGORY_ICON_FOLDER = "/Catecories/";
    private static final String USER_ICON_FOLDER = "/icons/";

    // header labels (FIELDS) - Initialized later in the constructor
    private JLabel usernameLabel;
    private JLabel levelLabel;
    private JLabel pointsLabel;
    private JProgressBar levelProgressBar;

    public ExtendedPlaySetupPage(int userId,
                                 String username,
                                 int userLevel, // Retained for compatibility but DB points are the truth
                                 int userPoints,
                                 String initialCategoryName) {

        this.userId = userId;

        // fallback values
        this.username = (username != null && !username.isEmpty()) ? username : "User";
        this.points   = Math.max(0, userPoints);
        this.selectedCategoryLabel = initialCategoryName;
        this.bio = null;

        // 1) load from DB (truth)
        loadUserDataFromDatabase();

        setTitle("ThinkDaily - Extended Play");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ================= TOP BAR (UI COMPONENT INITIALIZATION) =================
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userPanel.setOpaque(false);

        // ===== Avatar from UserIconSession OR 🙂 =====
        String iconFile = UserIconSession.getIcon(userId);
        ImageIcon avatarIcon = loadAvatarIcon(iconFile, 36);

        JLabel userIcon;
        if (avatarIcon != null && avatarIcon.getIconWidth() > 0) {
            userIcon = new JLabel(avatarIcon);
        } else {
            userIcon = new JLabel("🙂");
            userIcon.setFont(new Font("SansSerif", Font.PLAIN, 33));
        }
        userIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // popup menu setup (profile, help, etc. functionality retained)
        JPopupMenu userMenu = new JPopupMenu();
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

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent evt) {
                        setBackground(hoverColor);
                    }

                    @Override
                    public void mouseExited(MouseEvent evt) {
                        setBackground(Color.WHITE);
                    }
                });
            }
        }

        JMenuItem profileItem    = new StyledMenuItem("My Profile");
        JMenuItem helpItem       = new StyledMenuItem("Help 💡");
        JMenuItem guidelinesItem = new StyledMenuItem("Guidelines 📘");
        JMenuItem aboutItem      = new StyledMenuItem("About ThinkDaily ✨");
        JMenuItem logoutItem     = new StyledMenuItem("Logout 🚪");

        userMenu.add(profileItem);
        userMenu.addSeparator();
        userMenu.add(helpItem);
        userMenu.add(guidelinesItem);
        userMenu.add(aboutItem);
        userMenu.addSeparator();
        userMenu.add(logoutItem);

        profileItem.addActionListener(e -> {
            new PlayerProfilePage(
                    userId,
                    this.username,
                    this.effectiveLevel,
                    this.points,
                    (this.bio != null ? this.bio : ""),
                    UserIconSession.getIcon(userId)
            ).setVisible(true);
            dispose();
        });

        helpItem.addActionListener(e -> InfoDialog.show(this, "Help", getHelpText()));
        guidelinesItem.addActionListener(e -> InfoDialog.show(this, "Guidelines", getGuidelinesText()));
        aboutItem.addActionListener(e -> InfoDialog.show(this, "About ThinkDaily", getAboutText()));
        logoutItem.addActionListener(e -> {
            new RoleSelectionPage().setVisible(true);
            dispose();
        });

        userIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                userMenu.show(userIcon, 0, userIcon.getHeight());
            }
        });

        // ** FIX: Initialize header fields here **
        this.usernameLabel = new JLabel();
        this.usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        this.usernameLabel.setForeground(new Color(80, 70, 95));

        this.levelLabel = new JLabel();
        this.levelLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        this.levelLabel.setForeground(new Color(120, 110, 140));

        this.pointsLabel = new JLabel();
        this.pointsLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        this.pointsLabel.setForeground(new Color(120, 110, 140));

        this.levelProgressBar = new JProgressBar(0, 100);
        this.levelProgressBar.setPreferredSize(new Dimension(160, 10));
        this.levelProgressBar.setMaximumSize(new Dimension(160, 10));
        this.levelProgressBar.setBorder(BorderFactory.createEmptyBorder());
        this.levelProgressBar.setForeground(new Color(255, 176, 203));
        // ** END FIX **

        JPanel userInfoBlock = new JPanel();
        userInfoBlock.setOpaque(false);
        userInfoBlock.setLayout(new BoxLayout(userInfoBlock, BoxLayout.Y_AXIS));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        nameRow.setOpaque(false);
        nameRow.add(userIcon);
        nameRow.add(this.usernameLabel);

        userInfoBlock.add(nameRow);
        userInfoBlock.add(this.levelLabel);
        userInfoBlock.add(this.pointsLabel);
        userInfoBlock.add(Box.createVerticalStrut(2));
        userInfoBlock.add(this.levelProgressBar);

        userPanel.add(userInfoBlock);
        topBar.add(userPanel, BorderLayout.WEST);

        // Right: logo
        JLabel logoLabel;
        try {
            ImageIcon rawLogo = new ImageIcon(getClass().getResource("/logo/logo.png"));
            Image scaledLogo = rawLogo.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaledLogo));
        } catch (Exception ex) {
            logoLabel = new JLabel("ThinkDaily");
            logoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            logoLabel.setForeground(new Color(80, 60, 90));
        }
        topBar.add(logoLabel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // 3) Update UI values using the correct, initialized fields
        refreshLevelUI();

        // ================== CENTER BACKGROUND + CARD ==================
        JPanel centerBg = new JPanel(new GridBagLayout()) {
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
        centerBg.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        add(centerBg, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        RoundedPanel card = new RoundedPanel(30);
        card.setPreferredSize(new Dimension(650, 400));
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        centerBg.add(card, gbc);

        // ---- Card Top ----
        JPanel cardTop = new JPanel();
        cardTop.setOpaque(false);
        cardTop.setLayout(new BoxLayout(cardTop, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Extended Play ✨", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(70, 60, 95));

        JLabel subtitleLabel = new JLabel("Choose category to play", SwingConstants.CENTER);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(130, 120, 150));

        cardTop.add(titleLabel);
        cardTop.add(Box.createVerticalStrut(6));
        cardTop.add(subtitleLabel);
        cardTop.add(Box.createVerticalStrut(18));

        card.add(cardTop, BorderLayout.NORTH);

        // ---- Card Center (Category/Count Selection) ----
        JPanel cardCenter = new JPanel();
        cardCenter.setOpaque(false);
        cardCenter.setLayout(new BoxLayout(cardCenter, BoxLayout.Y_AXIS));

        JPanel categoriesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 5));
        categoriesPanel.setOpaque(false);

        String[][] categoryDefs = {
                {"Random",     "random.png",     null},
                {"Math",       "math.png",       "1"},
                {"Science",    "science.png",    "2"},
                {"History",    "history.png",    "3"},
                {"Technology", "technology.png", "4"}
        };

        ButtonGroup catGroup = new ButtonGroup();
        String initialLower = (selectedCategoryLabel != null)
                ? selectedCategoryLabel.trim().toLowerCase()
                : null;

        for (String[] def : categoryDefs) {
            String catName = def[0];
            String iconFileCat = def[1];
            String idStr = def[2];

            Integer catId = null;
            if (idStr != null) {
                try { catId = Integer.parseInt(idStr); } catch (NumberFormatException ignored) {}
            }
            categoryMap.put(catName, catId);

            ImageIcon icon = loadCategoryIcon(iconFileCat, 60);
            JToggleButton btn = createCategoryIconButton(catName, icon);

            boolean shouldSelect = false;
            // Select based on initial category name or default to Random if none provided
            if (initialLower != null && catName.toLowerCase().equals(initialLower)) {
                shouldSelect = true;
            } else if (initialLower == null && "Random".equals(catName)) {
                shouldSelect = true;
            }

            if (shouldSelect) {
                btn.setSelected(true);
                selectedCategoryLabel = catName;
            }

            btn.addActionListener(ev -> selectedCategoryLabel = catName);

            catGroup.add(btn);
            categoriesPanel.add(btn);
        }

        JPanel numberPanelWrapper = new JPanel();
        numberPanelWrapper.setOpaque(false);
        numberPanelWrapper.setLayout(new BoxLayout(numberPanelWrapper, BoxLayout.Y_AXIS));

        JLabel numLabel = new JLabel("Number of questions");
        numLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        numLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        numLabel.setForeground(new Color(110, 100, 135));

        numberPanelWrapper.add(numLabel);
        numberPanelWrapper.add(Box.createVerticalStrut(8));

        JPanel numberPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        numberPanel.setOpaque(false);

        JButton minusBtn = new RoundIconButton("-");
        minusBtn.addActionListener(ev -> changeQuestionCount(-1));

        questionCountLabel = new JLabel(String.valueOf(questionCount), SwingConstants.CENTER);
        questionCountLabel.setPreferredSize(new Dimension(70, 40));
        questionCountLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        questionCountLabel.setOpaque(true);
        questionCountLabel.setBackground(new Color(252, 249, 254));
        questionCountLabel.setForeground(new Color(70, 60, 95));
        questionCountLabel.setBorder(BorderFactory.createLineBorder(new Color(230, 210, 235), 1));

        JButton plusBtn = new RoundIconButton("+");
        plusBtn.addActionListener(ev -> changeQuestionCount(1));

        numberPanel.add(minusBtn);
        numberPanel.add(questionCountLabel);
        numberPanel.add(plusBtn);

        numberPanelWrapper.add(numberPanel);

        JLabel hintLabel = new JLabel("💡 shorter quizzes are great for quick focus sessions", SwingConstants.CENTER);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setForeground(new Color(150, 135, 165));

        cardCenter.add(categoriesPanel);
        cardCenter.add(Box.createVerticalStrut(20));
        cardCenter.add(numberPanelWrapper);
        cardCenter.add(Box.createVerticalStrut(10));
        cardCenter.add(hintLabel);

        card.add(cardCenter, BorderLayout.CENTER);

        // ---- Card Bottom ----
        JPanel cardBottom = new JPanel(new BorderLayout());
        cardBottom.setOpaque(false);

        JButton backBtn = new JButton("⟵ Back");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setBackground(new Color(245, 232, 245));
        backBtn.setForeground(new Color(90, 70, 100));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            new PlayerHomePage(userId).setVisible(true);
            dispose();
        });
        cardBottom.add(backBtn, BorderLayout.WEST);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setOpaque(false);

        JButton aboutBtn = new JButton("About Extended? ");
        aboutBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        aboutBtn.setContentAreaFilled(false);
        aboutBtn.setBorderPainted(false);
        aboutBtn.setFocusPainted(false);
        aboutBtn.setForeground(new Color(150, 130, 170));
        aboutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aboutBtn.addActionListener(this::showAboutExtendedMode);

        JButton startBtn = new JButton("Start ▶");
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        startBtn.setBackground(new Color(255, 210, 225));
        startBtn.setForeground(new Color(80, 60, 70));
        startBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startBtn.setFocusPainted(false);
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(this::handleStartExtended);

        rightButtons.add(aboutBtn);
        rightButtons.add(startBtn);

        cardBottom.add(rightButtons, BorderLayout.EAST);

        card.add(cardBottom, BorderLayout.SOUTH);
    }

    // ================== LEVELING LOGIC ==================
    // This replaces the external LevelUtils dependency for level calculation
    private void refreshLevelUI() {
        // Calculate effective level (Level 1 starts at 0 points)
        // e.g., 0-99 points = Level 1, 100-199 points = Level 2, etc.
        this.effectiveLevel = Math.max(1, (this.points / POINTS_PER_LEVEL) + 1);

        // Calculate points into the current level
        int pointsInto = this.points % POINTS_PER_LEVEL;
        int percent = (int) (100.0 * pointsInto / POINTS_PER_LEVEL);

        // Update UI elements (with null checks for safety, though they are initialized)
        if (usernameLabel != null) usernameLabel.setText(this.username != null ? this.username : "User");
        if (levelLabel != null) levelLabel.setText("Level: " + effectiveLevel);
        if (pointsLabel != null) pointsLabel.setText("Points: " + points);

        if (levelProgressBar != null) {
            levelProgressBar.setValue(percent);
            levelProgressBar.setToolTipText(pointsInto + " / " + POINTS_PER_LEVEL + " points to next level");
        }
    }

    // ================== DB: Load user info ==================
    private void loadUserDataFromDatabase() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Assume DBConnection.getConnection() works
            conn = DBConnection.getConnection();
            if (conn == null || conn.isClosed()) return;

            String sql = """
                SELECT 
                    u.user_name,
                    u.current_points_total,
                    u.bio
                FROM users u
                JOIN accounts a ON u.account_id = a.account_id
                WHERE u.user_id = ? AND a.role = 'PLAYER'
            """;

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String dbName   = rs.getString("user_name");
                int    dbPoints = rs.getInt("current_points_total");
                String dbBio    = rs.getString("bio");

                if (dbName != null && !dbName.isEmpty()) this.username = dbName;
                if (dbPoints >= 0) this.points = dbPoints;
                this.bio = dbBio;
            }
        } catch (SQLException ex) {
            System.out.println("Error loading user for ExtendedPlaySetupPage: " + ex.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }
    }

    // ================== Icon Loaders ==================
    private ImageIcon loadAvatarIcon(String iconPath, int size) {
        if (iconPath == null || iconPath.isBlank()) return null;

        try {
            ImageIcon raw;
            // Check if iconPath is already a full path (e.g., from DB) or needs the prefix
            if (iconPath.startsWith("/")) raw = new ImageIcon(getClass().getResource(iconPath));
            else raw = new ImageIcon(getClass().getResource(USER_ICON_FOLDER + iconPath));

            Image scaled = raw.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            System.out.println("Avatar load failed: " + iconPath);
            return null;
        }
    }

    private ImageIcon loadCategoryIcon(String fileName, int size) {
        if (fileName == null || fileName.isBlank()) return null;
        try {
            ImageIcon raw = new ImageIcon(getClass().getResource(CATEGORY_ICON_FOLDER + fileName));
            Image scaled = raw.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.err.println("Could not load category icon: " + CATEGORY_ICON_FOLDER + fileName);
            return null;
        }
    }

    // ================== Logic/Components ==================
    private void changeQuestionCount(int delta) {
        int newValue = questionCount + delta;
        if (newValue < MIN_QUESTIONS || newValue > MAX_QUESTIONS) return;
        questionCount = newValue;
        questionCountLabel.setText(String.valueOf(questionCount));
    }

    private void handleStartExtended(ActionEvent e) {
        String label = (selectedCategoryLabel != null) ? selectedCategoryLabel : "Random";
        // Assume ExtendedQuizPage exists and handles category logic
        ExtendedQuizPage page = new ExtendedQuizPage(userId, username, label, questionCount);
        page.setVisible(true);
        dispose();
    }

    private void showAboutExtendedMode(ActionEvent e) {
        String msg = """
            Extended Play Mode ✨

            • After finishing your daily quiz, you can keep playing.
            • You choose how many questions you want.
            • You can pick a specific category or let us choose randomly.
            • Every correct answer gives you extra points.
            • These points still count for your level and leaderboard.

            A cozy way to practice more whenever you feel like it 💡
        """;
        JOptionPane.showMessageDialog(this, msg, "About Extended Mode", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ... (RoundedPanel, JToggleButton, RoundIconButton helper classes remain the same)

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

            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(6, 8, w - 12, h - 12, cornerRadius + 12, cornerRadius + 12);

            g2.setColor(new Color(252, 249, 254));
            g2.fillRoundRect(0, 0, w - 12, h - 12, cornerRadius, cornerRadius);

            g2.setColor(new Color(245, 220, 230));
            g2.drawRoundRect(0, 0, w - 12, h - 12, cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JToggleButton createCategoryIconButton(String text, ImageIcon icon) {
        return new JToggleButton(text, icon) {
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
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color fill;
                if (isSelected()) fill = new Color(255, 210, 225);
                else if (getModel().isRollover()) fill = new Color(248, 236, 252);
                else fill = new Color(245, 235, 250);

                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.setColor(new Color(220, 200, 235));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    private static class RoundIconButton extends JButton {
        public RoundIconButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("SansSerif", Font.BOLD, 16));
            setPreferredSize(new Dimension(40, 40));
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setForeground(new Color(80, 60, 90));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = getModel().isPressed()
                    ? new Color(240, 220, 240)
                    : new Color(245, 232, 245);

            g2.setColor(fill);
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(new Color(210, 190, 220));
            g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ===== static texts for menu (methods retained) =====
    private String getHelpText() {
        return """
            Welcome to ThinkDaily! Here’s how to use the app:

            • Daily Quiz
              You get 3 questions every day. You can answer them once to earn points and level up.

            • Categories
              Choose a quiz category or let the system pick randomly.

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
}