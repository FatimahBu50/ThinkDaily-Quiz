package ThinkDaily;

import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends JPanel {

    private Connection conn;

    // Stats cards data
    private Map<String, Integer> stats;

    // Category maps
    private Map<String, Integer> categoryQuestionCounts; // for PIE
    private Map<String, Integer> categoryResponseCounts; // for BAR

    // UI components we need to refresh
    private JButton reportButton;

    // Stat value labels (must be fields so we can update)
    private JLabel totalUsersValue;
    private JLabel totalQuestionsValue;
    private JLabel activeTodayValue;
    private JLabel totalCategoriesValue;

    // Chart panels (must be fields so we can update + repaint)
    private PieChartPanel piePanel;
    private BarChartPanel barPanel;

    public Dashboard() {

        setSize(1150, 750);
        setPreferredSize(new Dimension(1150, 750));
        setMinimumSize(new Dimension(1150, 750));
        setMaximumSize(new Dimension(1150, 750));

        setBackground(new Color(253, 239, 244));
        setLayout(null);

        setupDatabaseConnection();

        // load initial data
        stats = loadStatistics();
        categoryQuestionCounts = loadCategoryQuestionCounts();
        categoryResponseCounts = loadCategoryResponseCounts();

        // ---------- Logo ----------
        JLabel logoLabel = new JLabel();
        logoLabel.setBounds(330, 10, 120, 120);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/logo/logo.png"));
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            logoLabel.setText("ThinkDaily");
            logoLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
            logoLabel.setForeground(new Color(120, 90, 130));
        }
        add(logoLabel);

        // ---------- Dashboard Title ----------
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 23));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(300, 120, 220, 40);
        add(title);

        // ---------- REPORT BUTTON ----------
        reportButton = new JButton("Report");
        reportButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        reportButton.setBounds(30, 165, 120, 30);
        reportButton.setBackground(Color.WHITE);
        reportButton.setFocusPainted(false);
        reportButton.setBorder(BorderFactory.createLineBorder(new Color(226, 115, 150), 1, true));
        reportButton.addActionListener(e -> showReportDialog());
        add(reportButton);

        // ---------- STAT CARDS AREA ----------
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new GridLayout(1, 4, 15, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setBounds(30, 205, 720, 90);
        add(cardsPanel);

        // Create value labels ONCE so we can refresh them later
        totalUsersValue = new JLabel(String.valueOf(stats.getOrDefault("totalUsers", 0)));
        totalQuestionsValue = new JLabel(String.valueOf(stats.getOrDefault("totalQuestions", 0)));
        activeTodayValue = new JLabel(String.valueOf(stats.getOrDefault("activeToday", 0)));
        totalCategoriesValue = new JLabel(String.valueOf(stats.getOrDefault("totalCategories", 0)));

        cardsPanel.add(createStatCard("Total Users", totalUsersValue));
        cardsPanel.add(createStatCard("Total Questions", totalQuestionsValue));
        cardsPanel.add(createStatCard("Active Today", activeTodayValue));
        cardsPanel.add(createStatCard("Categories", totalCategoriesValue));

        // ---------- PIE CHART (Questions by Category) ----------
        piePanel = new PieChartPanel(categoryQuestionCounts);
        piePanel.setBounds(30, 315, 350, 230);
        add(piePanel);

        // ---------- BAR CHART (Most Answered Categories) ----------
        barPanel = new BarChartPanel(categoryResponseCounts);
        barPanel.setBounds(400, 315, 350, 230);
        add(barPanel);
    }

    private void setupDatabaseConnection() {
        try {
            conn = DBConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cannot connect to database. Dashboard data may not load.",
                        "Database Error",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    // ==========================================================
    // ✅ PUBLIC REFRESH METHOD (CALL THIS AFTER ADMIN ACTIONS)
    // ==========================================================
    public void refreshDashboard() {
        // Reload data from DB
        stats = loadStatistics();
        categoryQuestionCounts = loadCategoryQuestionCounts();
        categoryResponseCounts = loadCategoryResponseCounts();

        // Update cards
        totalUsersValue.setText(String.valueOf(stats.getOrDefault("totalUsers", 0)));
        totalQuestionsValue.setText(String.valueOf(stats.getOrDefault("totalQuestions", 0)));
        activeTodayValue.setText(String.valueOf(stats.getOrDefault("activeToday", 0)));
        totalCategoriesValue.setText(String.valueOf(stats.getOrDefault("totalCategories", 0)));

        // Update charts and repaint
        if (piePanel != null) {
            piePanel.setData(categoryQuestionCounts);
            piePanel.repaint();
        }
        if (barPanel != null) {
            barPanel.setData(categoryResponseCounts);
            barPanel.repaint();
        }

        // Refresh UI
        revalidate();
        repaint();
    }

    // ===================== LOAD OVERVIEW STATS =====================

    private Map<String, Integer> loadStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        if (conn == null) return stats;

        try {
            String sql1 = """
                SELECT COUNT(*) AS total
                FROM users u
                JOIN accounts a ON u.account_id = a.account_id
                WHERE a.role = 'PLAYER' AND a.is_active = 1
            """;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql1)) {
                if (rs.next()) stats.put("totalUsers", rs.getInt("total"));
            }

            String sql2 = "SELECT COUNT(*) as total FROM questions WHERE is_active = TRUE";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql2)) {
                if (rs.next()) stats.put("totalQuestions", rs.getInt("total"));
            }

            String sql3 = """
                SELECT COUNT(DISTINCT ur.user_id) as active 
                FROM user_responses ur
                WHERE DATE(ur.answered_at) = CURDATE()
            """;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql3)) {
                if (rs.next()) stats.put("activeToday", rs.getInt("active"));
            }

            String sql4 = "SELECT COUNT(*) as total FROM categories";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql4)) {
                if (rs.next()) stats.put("totalCategories", rs.getInt("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error loading statistics: " + e.getMessage());
        }

        return stats;
    }

    // ===================== CATEGORY MAPS =====================

    // For PIE: questions per category
    private Map<String, Integer> loadCategoryQuestionCounts() {
    Map<String, Integer> map = new HashMap<>();
    if (conn == null) return map;

    try {
        String sql = """
            SELECT c.category_name,
                   COUNT(q.question_id) AS question_count
            FROM categories c
            LEFT JOIN questions q
                ON c.category_id = q.category_id AND q.is_active = TRUE
            WHERE c.category_name <> 'Random'
            GROUP BY c.category_id, c.category_name
            ORDER BY question_count DESC
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                map.put(rs.getString("category_name"), rs.getInt("question_count"));
            }
        }

    } catch (SQLException e) {
        System.out.println("Error loading category question counts: " + e.getMessage());
    }

    return map;
}


    // For BAR: responses per category (Most Answered)
    private Map<String, Integer> loadCategoryResponseCounts() {
    Map<String, Integer> map = new HashMap<>();
    if (conn == null) return map;

    try {
        String sql = """
            SELECT c.category_name,
                   COUNT(ur.response_id) AS response_count
            FROM categories c
            LEFT JOIN questions q
                ON c.category_id = q.category_id AND q.is_active = TRUE
            LEFT JOIN user_responses ur
                ON q.question_id = ur.question_id
            WHERE c.category_name <> 'Random'
            GROUP BY c.category_id, c.category_name
            ORDER BY response_count DESC
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("category_name"), rs.getInt("response_count"));
            }
        }

    } catch (SQLException e) {
        System.out.println("Error loading category response counts: " + e.getMessage());
    }

    return map;
}


    // ===================== REPORT POPUP =====================

   private void showReportDialog() {
    if (conn == null) {
        JOptionPane.showMessageDialog(this,
                "No database connection.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Refresh dashboard before report
    refreshDashboard();

    ReportsService service = new ReportsService(conn);
    JComponent reportTabs = service.generateReport("DASHBOARD");

    // ✅ Create proper dialog (NOT JOptionPane)
    JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Dashboard Report",
            Dialog.ModalityType.APPLICATION_MODAL
    );

    // Wrap in scroll pane (important for big tables)
    JScrollPane scrollPane = new JScrollPane(reportTabs);
    scrollPane.setPreferredSize(new Dimension(1200, 700));

    dialog.setContentPane(scrollPane);
    dialog.setSize(800, 650);              // BIG
    dialog.setLocationRelativeTo(this);     // center
    dialog.setResizable(true);              // ✅ user can resize
    dialog.setVisible(true);
}


    // ===================== CARD MAKERS =====================

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 18;
                int margin = 2;

                g2.setColor(new Color(255, 248, 252));
                g2.fillRoundRect(margin, margin, getWidth() - 2 * margin, getHeight() - 2 * margin, arc, arc);

                g2.setColor(new Color(235, 210, 225));
                g2.drawRoundRect(margin, margin, getWidth() - 2 * margin, getHeight() - 2 * margin, arc, arc);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setHorizontalAlignment(SwingConstants.CENTER);
        t.setForeground(new Color(120, 100, 130));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(new Color(226, 115, 150));

        card.add(t, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // ===================== PIE CHART =====================

    static class PieChartPanel extends JPanel {
        private Map<String, Integer> categoryStats;

        private final Color[] colors = {
                new Color(166,196,255), new Color(171,217,178),
                new Color(252,212,132), new Color(250,167,160),
                new Color(203,180,255), new Color(255,209,220),
                new Color(178,235,242)
        };

        public PieChartPanel(Map<String, Integer> categoryStats) {
            this.categoryStats = categoryStats;
            setBackground(new Color(255, 248, 252));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        public void setData(Map<String, Integer> newData) {
            this.categoryStats = newData;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(255,246,251));
            g2.fillRoundRect(0, 0, w - 1, h - 1, 20, 20);
            g2.setColor(new Color(235,210,225));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.setColor(new Color(120,90,130));
            g2.drawString("Questions by Category", 15, 25);

            if (categoryStats == null || categoryStats.isEmpty()) {
                g2.drawString("No category data available", 100, 120);
                g2.dispose();
                return;
            }

            int size = 130;
            int x = 20;
            int y = 40;

            double total = categoryStats.values().stream().mapToInt(i -> i).sum();
            if (total <= 0) {
                g2.drawString("No category data available", 100, 120);
                g2.dispose();
                return;
            }

            String[] labels = categoryStats.keySet().toArray(new String[0]);
            int[] values = new int[labels.length];
            for (int i = 0; i < labels.length; i++) values[i] = categoryStats.get(labels[i]);

            int start = 0;
            for (int i = 0; i < values.length && i < 5; i++) {
                int arcAngle = (int) ((values[i] / total) * 360);
                g2.setColor(colors[i % colors.length]);
                g2.fillArc(x, y, size, size, start, arcAngle);
                start += arcAngle;
            }

            int legendX = 170;
            int legendY = 55;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

            for (int i = 0; i < Math.min(labels.length, 5); i++) {
                g2.setColor(colors[i]);
                g2.fillRoundRect(legendX, legendY + i * 20, 12, 12, 4, 4);
                g2.setColor(new Color(70, 70, 70));
                g2.drawString(labels[i] + " (" + values[i] + ")", legendX + 18, legendY + 10 + i * 20);
            }

            g2.dispose();
        }
    }

    // ===================== BAR CHART =====================

    static class BarChartPanel extends JPanel {
        private Map<String, Integer> categoryStats;

        private final Color[] colors = {
                new Color(166,196,255), new Color(171,217,178),
                new Color(252,212,132), new Color(250,167,160),
                new Color(203,180,255), new Color(255,209,220),
                new Color(178,235,242)
        };

        public BarChartPanel(Map<String, Integer> categoryStats) {
            this.categoryStats = categoryStats;
            setBackground(new Color(255, 248, 252));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        public void setData(Map<String, Integer> newData) {
            this.categoryStats = newData;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255,246,251));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            g2.setColor(new Color(235,210,225));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.setColor(new Color(120,90,130));
            g2.drawString("Most Answered Categories", 15, 25);

            if (categoryStats == null || categoryStats.isEmpty()) {
                g2.drawString("No response data available", 100, 120);
                g2.dispose();
                return;
            }

            String[] labels = categoryStats.keySet().toArray(new String[0]);
            int[] values = new int[labels.length];
            for (int i = 0; i < labels.length; i++) values[i] = categoryStats.get(labels[i]);

            // sort by value DESC
            for (int i = 0; i < values.length - 1; i++) {
                for (int j = 0; j < values.length - i - 1; j++) {
                    if (values[j] < values[j + 1]) {
                        int temp = values[j];
                        values[j] = values[j + 1];
                        values[j + 1] = temp;

                        String t = labels[j];
                        labels[j] = labels[j + 1];
                        labels[j + 1] = t;
                    }
                }
            }

            int maxCategories = Math.min(values.length, 5);
            int maxValue = 1;
            for (int i = 0; i < maxCategories; i++)
                maxValue = Math.max(maxValue, values[i]);

            int baseY = 190;
            int maxBarH = 120;
            int barW = 40;
            int gap = 30;
            int x = 30;

            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

            for (int i = 0; i < maxCategories; i++) {
                int hBar = (int) ((values[i] / (double) maxValue) * maxBarH);

                g2.setColor(colors[i]);
                g2.fillRoundRect(x, baseY - hBar, barW, hBar, 8, 8);

                g2.setColor(new Color(130,110,140));
                g2.drawRoundRect(x, baseY - hBar, barW, hBar, 8, 8);

                g2.setColor(new Color(80,80,80));
                g2.drawString(String.valueOf(values[i]), x + 10, baseY - hBar - 5);

                String label = labels[i].length() > 8 ? labels[i].substring(0, 7) + ".." : labels[i];
                g2.drawString(label, x + 5, baseY + 18);

                x += barW + gap;
            }

            g2.dispose();
        }
    }
}
