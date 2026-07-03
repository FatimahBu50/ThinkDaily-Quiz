package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExtendedPlaySetupPage extends JFrame {

    private final int userId;
    private final String username;
    private final int userLevel;
    private final int userPoints;

    // category name -> ID (for DB later)
    private final Map<String, Integer> categoryMap = new LinkedHashMap<>();

    // UI state
    private String selectedCategoryLabel;
    private int questionCount = 5;       // default
    private final int MIN_QUESTIONS = 3;
    private final int MAX_QUESTIONS = 20;
    private JLabel questionCountLabel;

    private static final String CATEGORY_ICON_FOLDER = "/Catecories/";

  
    public ExtendedPlaySetupPage(int userId,
                                 String username,
                                 int userLevel,
                                 int userPoints,
                                 String initialCategoryName) {

        this.userId = userId;
        this.username = (username != null && !username.isEmpty()) ? username : "Player";
        this.userLevel = userLevel;
        this.userPoints = userPoints;
        this.selectedCategoryLabel = initialCategoryName; // may be null

        setTitle("ThinkDaily - Extended Play");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ====== TOP BAR ======
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.setBackground(Color.WHITE);

        // Left: user info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userPanel.setOpaque(false);

        JLabel userIcon = new JLabel("🙂");
        userIcon.setFont(new Font("SansSerif", Font.PLAIN, 33));
        userIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // popup menu for user icon
        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem profileItem = new JMenuItem("Profile");
        JMenuItem logoutItem = new JMenuItem("Logout");
        userMenu.add(profileItem);
        userMenu.add(logoutItem);

        profileItem.addActionListener(e -> {
             new PlayerProfilePage(userId, username, userLevel, userPoints, "", null).setVisible(true);
             dispose();
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

        JLabel nameLabel = new JLabel(this.username);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        nameLabel.setForeground(new Color(80, 70, 95));

        JPanel userInfoBlock = new JPanel();
        userInfoBlock.setOpaque(false);
        userInfoBlock.setLayout(new BoxLayout(userInfoBlock, BoxLayout.Y_AXIS));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        nameRow.setOpaque(false);
        nameRow.add(userIcon);
        nameRow.add(nameLabel);

        JLabel levelLabel = new JLabel("Level: " + (userLevel > 0 ? userLevel : "-"));
        levelLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        levelLabel.setForeground(new Color(120, 110, 140));

        JLabel pointsLabel = new JLabel("Points: " + userPoints);
        pointsLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        pointsLabel.setForeground(new Color(120, 110, 140));

        // small XP bar (decorative)
        JProgressBar headerProgressBar = new JProgressBar(0, 100);
        headerProgressBar.setPreferredSize(new Dimension(160, 10));
        headerProgressBar.setMaximumSize(new Dimension(160, 10));
        headerProgressBar.setBorder(BorderFactory.createEmptyBorder());
        headerProgressBar.setForeground(new Color(255, 176, 203));
        headerProgressBar.setValue(0);

        userInfoBlock.add(nameRow);
        userInfoBlock.add(levelLabel);
        userInfoBlock.add(pointsLabel);
        userInfoBlock.add(Box.createVerticalStrut(2));
        userInfoBlock.add(headerProgressBar);

        userPanel.add(userInfoBlock);
        topPanel.add(userPanel, BorderLayout.WEST);

        // Right: small logo
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
        topPanel.add(logoLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ====== CENTER BACKGROUND ======
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

        // ====== CENTER CARD ======
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

        // ---- Card Center ----
        JPanel cardCenter = new JPanel();
        cardCenter.setOpaque(false);
        cardCenter.setLayout(new BoxLayout(cardCenter, BoxLayout.Y_AXIS));

        // Category icon buttons row (same idea as Home Page)
        JPanel categoriesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 5));
        categoriesPanel.setOpaque(false);

        // Define categories (edit icon file names to match your src/Catecories/)
        String[][] categoryDefs = {
                //  {displayName, iconFileName, idString}
                {"Random",    "random.png",    null},
                {"Math",      "math.png",      "1"},
                {"Science",   "science.png",   "2"},
                {"History",   "history.png",   "3"},
                {"Technology","technology.png","4"}
        };

        ButtonGroup catGroup = new ButtonGroup();
        String initialLower = (selectedCategoryLabel != null)
                ? selectedCategoryLabel.trim().toLowerCase()
                : null;

        for (String[] def : categoryDefs) {
            String catName = def[0];
            String iconFile = def[1];
            String idStr    = def[2];

            Integer catId = null;
            if (idStr != null) {
                try { catId = Integer.parseInt(idStr); } catch (NumberFormatException ignored) {}
            }
            categoryMap.put(catName, catId); // for DB use later

            ImageIcon icon = loadCategoryIcon(iconFile, 60);
            JToggleButton btn = createCategoryIconButton(catName, icon);

            // select initial category
            boolean shouldSelect = false;
            if (initialLower != null && catName.toLowerCase().equals(initialLower)) {
                shouldSelect = true;
            } else if (initialLower == null && "Random".equals(catName)) {
                // default when coming from Extended button (no category from Home)
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

        // Number of questions area
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

        JLabel hintLabel = new JLabel(
                "💡 shorter quizzes are great for quick focus sessions",
                SwingConstants.CENTER
        );
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

    // change question count via - / +
    private void changeQuestionCount(int delta) {
        int newValue = questionCount + delta;
        if (newValue < MIN_QUESTIONS || newValue > MAX_QUESTIONS) {
            return;
        }
        questionCount = newValue;
        questionCountLabel.setText(String.valueOf(questionCount));
    }

    // Start Extended Quiz → open question page
    private void handleStartExtended(ActionEvent e) {
        String label = (selectedCategoryLabel != null) ? selectedCategoryLabel : "Random";
        ExtendedQuizPage page = new ExtendedQuizPage(userId, username, label, questionCount);
        page.setVisible(true);
        dispose();
    }

    // About Extended Mode dialog
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
        JOptionPane.showMessageDialog(
                this,
                msg,
                "About Extended Mode",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ===== Rounded card panel =====
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

    //  category icon buttons (icon + label, toggle)
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

    //  buttons
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
}