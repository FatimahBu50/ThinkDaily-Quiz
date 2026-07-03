package ThinkDaily;

import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends JPanel {

    private Connection conn;
    
    public Dashboard() {

        setSize(1150, 750);
        setPreferredSize(new Dimension(1150, 750));
        setMinimumSize(new Dimension(1150, 750));
        setMaximumSize(new Dimension(1150,750));

        setBackground(new Color(253, 239, 244));
        setLayout(null);
        
        setupDatabaseConnection();
        
        Map<String, Integer> stats = loadStatistics();
        Map<String, Integer> categoryStats = loadCategoryStatistics();

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
        title.setBounds(300, 135, 220, 40);
        add(title);
        
        // ---------- STAT CARDS AREA ----------
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new GridLayout(1, 4, 15, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setBounds(30, 170, 720, 90);
        add(cardsPanel);

        cardsPanel.add(createStatCard("Total Users", String.valueOf(stats.getOrDefault("totalUsers", 0))));
        cardsPanel.add(createStatCard("Total Questions", String.valueOf(stats.getOrDefault("totalQuestions", 0))));
        cardsPanel.add(createStatCard("Active Today", String.valueOf(stats.getOrDefault("activeToday", 0))));
        cardsPanel.add(createStatCard("Categories", String.valueOf(stats.getOrDefault("totalCategories", 0))));

        // ---------- PIE CHART ----------
        JPanel piePanel = new PieChartPanel(categoryStats);
        piePanel.setBounds(30, 270, 350, 230);
        add(piePanel);

        // ---------- BAR CHART ----------
        JPanel barPanel = new BarChartPanel(categoryStats);
        barPanel.setBounds(400, 270, 350, 230);
        add(barPanel);

        // ❌ REMOVED LOWER 4 BOXES — NOTHING BELOW THIS POINT
    }
    
    private void setupDatabaseConnection() {
        try {
            conn = DBConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                JOptionPane.showMessageDialog(this,
                    "Cannot connect to database. Dashboard data may not load.",
                    "Database Error",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }
    
    private Map<String, Integer> loadStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        if (conn == null) return stats;
        
        try {
            String sql1 = "SELECT COUNT(*) as total FROM users";
            Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery(sql1);
            if (rs1.next()) stats.put("totalUsers", rs1.getInt("total"));
            rs1.close();
            stmt1.close();
            
            String sql2 = "SELECT COUNT(*) as total FROM questions WHERE is_active = TRUE";
            Statement stmt2 = conn.createStatement();
            ResultSet rs2 = stmt2.executeQuery(sql2);
            if (rs2.next()) stats.put("totalQuestions", rs2.getInt("total"));
            rs2.close();
            stmt2.close();

            String sql3 = """
                SELECT COUNT(DISTINCT u.user_id) as active 
                FROM users u
                JOIN user_responses ur ON u.user_id = ur.user_id
                WHERE DATE(ur.answered_at) = CURDATE()
            """;
            Statement stmt3 = conn.createStatement();
            ResultSet rs3 = stmt3.executeQuery(sql3);
            if (rs3.next()) stats.put("activeToday", rs3.getInt("active"));
            rs3.close();
            stmt3.close();
            
            String sql4 = "SELECT COUNT(*) as total FROM categories";
            Statement stmt4 = conn.createStatement();
            ResultSet rs4 = stmt4.executeQuery(sql4);
            if (rs4.next()) stats.put("totalCategories", rs4.getInt("total"));
            rs4.close();
            stmt4.close();

        } catch (SQLException e) {
            System.out.println("Error loading statistics: " + e.getMessage());
        }
        
        return stats;
    }
    
    private Map<String, Integer> loadCategoryStatistics() {
        Map<String, Integer> categoryStats = new HashMap<>();
        
        if (conn == null) return categoryStats;
        
        try {
            String sql = """
                SELECT 
                    c.category_name,
                    COUNT(q.question_id) as question_count,
                    COUNT(ur.response_id) as response_count
                FROM categories c
                LEFT JOIN questions q ON c.category_id = q.category_id AND q.is_active = TRUE
                LEFT JOIN user_responses ur ON q.question_id = ur.question_id
                GROUP BY c.category_id, c.category_name
                ORDER BY response_count DESC
            """;
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                categoryStats.put(
                    rs.getString("category_name"),
                    rs.getInt("question_count")
                );
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.out.println("Error loading category statistics: " + e.getMessage());
        }
        
        return categoryStats;
    }

    // CARD MAKERS -----------------------------------

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 18;
                int margin = 2;

                g2.setColor(new Color(255, 248, 252));
                g2.fillRoundRect(margin, margin, getWidth()-2*margin, getHeight()-2*margin, arc, arc);

                g2.setColor(new Color(235, 210, 225));
                g2.drawRoundRect(margin, margin, getWidth()-2*margin, getHeight()-2*margin, arc, arc);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setHorizontalAlignment(SwingConstants.CENTER);
        t.setForeground(new Color(120, 100, 130));

        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 26));
        v.setHorizontalAlignment(SwingConstants.CENTER);
        v.setForeground(new Color(226, 115, 150));

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);

        return card;
    }

    // PIE CHART PANEL ------------------------------------

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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(255,246,251));
            g2.fillRoundRect(0, 0, w-1, h-1, 20, 20);
            g2.setColor(new Color(235,210,225));
            g2.drawRoundRect(0, 0, w-1, h-1, 20, 20);

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.setColor(new Color(120,90,130));
            g2.drawString("Questions by Category", 15, 25);

            if (categoryStats.isEmpty()) {
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
                int arcAngle = (int)((values[i] / total) * 360);
                g2.setColor(colors[i % colors.length]);
                g2.fillArc(x, y, size, size, start, arcAngle);
                start += arcAngle;
            }

            int legendX = 170;
            int legendY = 55;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

            for (int i = 0; i < Math.min(labels.length, 5); i++) {
                g2.setColor(colors[i]);
                g2.fillRoundRect(legendX, legendY + i*20, 12, 12, 4, 4);
                g2.setColor(new Color(70,70,70));
                g2.drawString(labels[i] + " (" + values[i] + ")", legendX + 18, legendY + 10 + i*20);
            }

            g2.dispose();
        }
    }

    // BAR CHART PANEL ------------------------------------

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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255,246,251));
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            g2.setColor(new Color(235,210,225));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.setColor(new Color(120,90,130));
            g2.drawString("Most Answered Categories", 15, 25);

            if (categoryStats.isEmpty()) {
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
                int hBar = (int)((values[i] / (double)maxValue) * maxBarH);

                g2.setColor(colors[i]);
                g2.fillRoundRect(x, baseY - hBar, barW, hBar, 8, 8);

                g2.setColor(new Color(130,110,140));
                g2.drawRoundRect(x, baseY - hBar, barW, hBar, 8, 8);

                g2.setColor(new Color(80,80,80));
                g2.drawString(String.valueOf(values[i]), x+10, baseY - hBar - 5);

                String label = labels[i].length() > 8 ? labels[i].substring(0,7)+".." : labels[i];
                g2.drawString(label, x+5, baseY + 18);

                x += barW + gap;
            }

            g2.dispose();
        }
    }
}

