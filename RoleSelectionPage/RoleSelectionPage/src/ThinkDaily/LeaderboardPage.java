package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardPage extends JFrame {

    private final int userId;
    private final String username;
    private final int level;
    private final int points;
    private String bio ;

    // UI
    private JPanel podiumPanel;
    private JPanel listPanel;
    private JScrollPane scrollPane;

    private JButton dailyBtn;
    private JButton allTimeBtn;

    private JLabel headerLevelLabel;
    private JLabel headerPointsLabel;

    // Lists (dummy data for design preview – DB team can replace later)
    private List<UserEntry> dailyList = new ArrayList<>();
    private List<UserEntry> allTimeList = new ArrayList<>();

    // ===========================================================
    //   CONSTRUCTOR
    // ===========================================================
    public LeaderboardPage(int userId, String username, int level, int points) {
        this.userId = userId;
        this.username = (username != null && !username.isEmpty()) ? username : "Player";
        this.level = level;
        this.points = points;

        setTitle("ThinkDaily - Leaderboard");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Just to show interface – replace with DB later
        createDummyData();

        buildTopBar();
        buildCenter();
        showDaily();
    }

    // ===========================================================
    //   DUMMY DATA (ONLY TO SEE THE UI)
    // ===========================================================
    private void createDummyData() {
        dailyList.clear();
        allTimeList.clear();

        // Users 1..9, all with 0 points (design only)
        for (int i = 1; i <= 9; i++) {
            dailyList.add(new UserEntry("User " + i, 0));
        }

        // All-time same as daily for now (DB team can change)
        allTimeList.addAll(dailyList);
    }

    // ===========================================================
    //   TOP BAR
    // ===========================================================
    private void buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // LEFT: user info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userPanel.setOpaque(false);

        JLabel userIcon = new JLabel("🙂");
        userIcon.setFont(new Font("SansSerif", Font.PLAIN, 26));
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
        JMenuItem aboutItem = new StyledMenuItem("About ThinkDaily ✨");
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

        JLabel usernameLabel = new JLabel(this.username);
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        usernameLabel.setForeground(new Color(80, 70, 95));

        JPanel userInfoBlock = new JPanel();
        userInfoBlock.setOpaque(false);
        userInfoBlock.setLayout(new BoxLayout(userInfoBlock, BoxLayout.Y_AXIS));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        nameRow.setOpaque(false);
        nameRow.add(userIcon);
        nameRow.add(usernameLabel);

        headerLevelLabel = new JLabel("Level: " + (level > 0 ? level : "-"));
        headerLevelLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        headerLevelLabel.setForeground(new Color(120, 110, 140));

        headerPointsLabel = new JLabel("Points: " + points);
        headerPointsLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        headerPointsLabel.setForeground(new Color(120, 110, 140));

        JProgressBar headerProgressBar = new JProgressBar(0, 100);
        headerProgressBar.setPreferredSize(new Dimension(160, 10));
        headerProgressBar.setMaximumSize(new Dimension(160, 10));
        headerProgressBar.setBorder(BorderFactory.createEmptyBorder());
        headerProgressBar.setForeground(new Color(255, 176, 203));
        headerProgressBar.setValue(0);

        userInfoBlock.add(nameRow);
        userInfoBlock.add(headerLevelLabel);
        userInfoBlock.add(headerPointsLabel);
        userInfoBlock.add(Box.createVerticalStrut(2));
        userInfoBlock.add(headerProgressBar);

        userPanel.add(userInfoBlock);
        topBar.add(userPanel, BorderLayout.WEST);

        // CENTER: title + mode buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Leaderboard", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(70, 60, 95));

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        modePanel.setOpaque(false);

        dailyBtn = createModeButton("Today");
        allTimeBtn = createModeButton("All-time");

        dailyBtn.addActionListener(e -> showDaily());
        allTimeBtn.addActionListener(e -> showAllTime());

        modePanel.add(dailyBtn);
        modePanel.add(allTimeBtn);

        centerPanel.add(title);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(modePanel);

        topBar.add(centerPanel, BorderLayout.CENTER);

        // RIGHT: logo
        JLabel logo;
        try {
            ImageIcon rawLogo = new ImageIcon(getClass().getResource("/logo/logo.png"));
            Image scaledLogo = rawLogo.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            logo = new JLabel(new ImageIcon(scaledLogo));
        } catch (Exception ex) {
            logo = new JLabel("ThinkDaily");
            logo.setFont(new Font("SansSerif", Font.BOLD, 16));
            logo.setForeground(new Color(80, 60, 90));
        }
        topBar.add(logo, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);
    }

    private JButton createModeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setBackground(new Color(245, 232, 245));
        btn.setForeground(new Color(80, 60, 80));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return btn;
    }

    // ===========================================================
    //   CENTER AREA
    // ===========================================================
    private void buildCenter() {
        JPanel centerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 245, 250),
                        w, h, new Color(235, 242, 255)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        RoundedPanel card = new RoundedPanel(30);
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Podium
        podiumPanel = new JPanel(new GridBagLayout());
        podiumPanel.setOpaque(false);
        podiumPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // List
        listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        // ===== Scroll Panel: Cute & Pastel ✨ =====
JScrollPane scrollPane = new JScrollPane(listPanel) {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
};
scrollPane.setOpaque(false);
scrollPane.getViewport().setOpaque(false);
scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

// Soft rounded border around the scroll area
scrollPane.setViewportBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(240, 225, 245), 2, true),
        BorderFactory.createEmptyBorder(12, 12, 12, 12)
));

// Spacing inside list
listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

// ===== Custom Pastel Scroll Bar =====
scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
    @Override
    protected void configureScrollBarColors() {
        thumbColor = new Color(245, 210, 230);
        trackColor = new Color(250, 240, 255);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        btn.setMinimumSize(new Dimension(0, 0));
        btn.setMaximumSize(new Dimension(0, 0));
        btn.setOpaque(false);
        btn.setBorder(null);
        return btn;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(245, 210, 230));
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
    }
});

// Add podium + the cute scroll area
card.add(podiumPanel, BorderLayout.NORTH);
card.add(scrollPane, BorderLayout.CENTER);


        // Back button
        JButton backBtn = new JButton("⟵ Back to Home");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setBackground(new Color(245, 232, 245));
        backBtn.setForeground(new Color(90, 70, 100));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener((ActionEvent e) -> {
            new PlayerHomePage(userId).setVisible(true);
            dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(backBtn);

        centerPanel.add(card, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    // ===========================================================
    //   MODES
    // ===========================================================
    private void showDaily() {
        styleSelected(dailyBtn, true);
        styleSelected(allTimeBtn, false);

        headerLevelLabel.setText("Level: " + (level > 0 ? level : "-"));
        headerPointsLabel.setText("Points: " + points);

        updatePodium(dailyList);
        updateList(dailyList, 10);
    }

    private void showAllTime() {
        styleSelected(dailyBtn, false);
        styleSelected(allTimeBtn, true);

        updatePodium(allTimeList);
        updateList(allTimeList, allTimeList.size());

        int allTimePoints = points;
        for (UserEntry u : allTimeList) {
            if (u.name.equalsIgnoreCase(username)) {
                allTimePoints = u.points;
                break;
            }
        }
        headerPointsLabel.setText("Points: " + allTimePoints);
    }

    private void styleSelected(JButton btn, boolean selected) {
        if (selected) {
            btn.setBackground(new Color(255, 210, 225));
            btn.setForeground(new Color(70, 50, 80));
        } else {
            btn.setBackground(new Color(245, 232, 245));
            btn.setForeground(new Color(80, 60, 80));
        }
    }

    // ===========================================================
    //   PODIUM + LIST
    // ===========================================================
    private void updatePodium(List<UserEntry> list) {
        podiumPanel.removeAll();

        // Ensure at least 3 entries so podium always shows
        while (list.size() < 3) {
            list.add(new UserEntry("User " + (list.size() + 1), 0));
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 30, 10, 30);

        // 3rd place (bronze) on the left
        gbc.gridx = 0;
        podiumPanel.add(createStarColumn("bronze.png", 90, list.get(2)), gbc);

        // 1st place (gold) in the center, bigger
        gbc.gridx = 1;
        podiumPanel.add(createStarColumn("gold.png", 130, list.get(0)), gbc);

        // 2nd place (silver) on the right
        gbc.gridx = 2;
        podiumPanel.add(createStarColumn("silver.png", 90, list.get(1)), gbc);

        podiumPanel.revalidate();
        podiumPanel.repaint();
    }

    private JPanel createStarColumn(String fileName, int starSize, UserEntry user) {
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        // Avatar placeholder
        JLabel avatar = new JLabel("🙂");
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatar.setFont(new Font("SansSerif", Font.PLAIN, 33));
        avatar.setForeground(new Color(70, 60, 90));

        // Star image
        JLabel starLabel;
        try {
            ImageIcon rawIcon = new ImageIcon(getClass().getResource("/podium/" + fileName));
            Image scaled = rawIcon.getImage().getScaledInstance(starSize, starSize, Image.SCALE_SMOOTH);
            starLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception ex) {
            // Fallback if image not found
            starLabel = new JLabel("★");
            starLabel.setFont(new Font("SansSerif", Font.BOLD, starSize / 2));
        }
        starLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel(user.name);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        name.setFont(new Font("SansSerif", Font.PLAIN, 13));
        name.setForeground(new Color(80, 70, 95));

        JLabel pts = new JLabel(user.points + " pts");
        pts.setAlignmentX(Component.CENTER_ALIGNMENT);
        pts.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pts.setForeground(new Color(130, 120, 145));

        col.add(avatar);
        col.add(Box.createVerticalStrut(6));
        col.add(starLabel);
        col.add(Box.createVerticalStrut(6));
        col.add(name);
        col.add(pts);

        return col;
    }

    private void updateList(List<UserEntry> list, int count) {
        listPanel.removeAll();

        for (int i = 3; i < Math.min(count, list.size()); i++) {
            UserEntry entry = list.get(i);
            boolean isCurrent = entry.name.equalsIgnoreCase(username);
            listPanel.add(createRow(i + 1, entry, isCurrent));
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createRow(int rank, UserEntry user, boolean highlight) {
        JPanel row = new JPanel(new BorderLayout());

        if (highlight) {
            row.setOpaque(true);
            row.setBackground(new Color(255, 230, 241));
        } else {
            row.setOpaque(false);
        }

        row.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);

        RankBadge badge = new RankBadge(rank);
        JLabel name = new JLabel(user.name);
        name.setFont(new Font("SansSerif", Font.PLAIN, 13));
        name.setForeground(new Color(100, 80, 115));

        left.add(badge);
        left.add(name);

        JLabel pts = new JLabel(user.points + " pts");
        pts.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pts.setForeground(new Color(130, 120, 145));

        row.add(left, BorderLayout.WEST);
        row.add(pts, BorderLayout.EAST);

        return row;
    }

    // ===========================================================
    //   RANK BADGE (PINK CIRCLE)
    // ===========================================================
    class RankBadge extends JPanel {
        private final String text;

        public RankBadge(int rank) {
            this.text = String.valueOf(rank);
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(26, 26);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h);

            g2.setColor(new Color(255, 210, 225));
            g2.fillOval(0, 0, size - 1, size - 1);

            g2.setColor(new Color(220, 170, 190));
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawOval(0, 0, size - 1, size - 1);

            g2.setColor(new Color(80, 60, 90));
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));

            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text);
            int th = fm.getAscent();
            int cx = (size - tw) / 2;
            int cy = (size - th) / 2 + th;

            g2.drawString(text, cx, cy);
            g2.dispose();
        }
    }

    // ===========================================================
    //   MODEL CLASS FOR ENTRIES
    // ===========================================================
    class UserEntry {
        String name;
        int points;

        public UserEntry(String name, int points) {
            this.name = name;
            this.points = points;
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

    // ===========================================================
    //   ROUNDED PANEL
    // ===========================================================
    private static class RoundedPanel extends JPanel {
        private final int radius;

        RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(6, 8, w - 12, h - 12, radius + 12, radius + 12);

            g2.setColor(new Color(252, 249, 254));
            g2.fillRoundRect(0, 0, w - 12, h - 12, radius, radius);

            g2.setColor(new Color(245, 220, 230));
            g2.drawRoundRect(0, 0, w - 12, h - 12, radius, radius);

            g2.dispose();
        }
    }

}