package ThinkDaily;

import javax.swing.*;
import java.awt.*;

public class QuizResultPage extends JFrame {

    // Basic info about this session
    private final int userId;
    private final String username;
    private final int sessionPoints;
    private final int correctCount;
    private final int totalQuestions;
    private final long timeTakenMillis;

    public QuizResultPage(int userId,
                          String username,
                          int sessionPoints,
                          int correctCount,
                          int totalQuestions,
                          long timeTakenMillis) {

        this.userId = userId;
        this.username = (username != null && !username.isBlank()) ? username : "Player";
        this.sessionPoints = sessionPoints;
        this.correctCount = correctCount;
        this.totalQuestions = totalQuestions;
        this.timeTakenMillis = timeTakenMillis;

        setTitle("ThinkDaily - Result");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== CENTER GRADIENT BACKGROUND =====
        JPanel centerBg = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                        0, 0,
                        new Color(255, 245, 250),
                        w, h,
                        new Color(235, 242, 255)
                );
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
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(700, 500));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        centerBg.add(card, gbc);

        // ========= TOP PART: ICON + TEXT =========
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Try to load cute trophy icon. Put your image here or change the path.
        try {
            ImageIcon raw = new ImageIcon(
                    getClass().getResource("/Result/trophy.png")
            );
            Image scaled = raw.getImage().getScaledInstance(155, 190, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            iconLabel.setText("🏆");
            iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 80));
        }

        topPanel.add(iconLabel);
        topPanel.add(Box.createVerticalStrut(10));

        // Decide a cute message based on score
        double accuracy = (totalQuestions > 0)
                ? (100.0 * correctCount / totalQuestions)
                : 0.0;

        String mainMessage;
        if (accuracy == 100.0) {
            mainMessage = "You’re GOATed!";
        } else if (accuracy >= 80.0) {
            mainMessage = "Amazing work!";
        } else if (accuracy >= 50.0) {
            mainMessage = "Nice effort!";
        } else {
            mainMessage = "You showed up today";
        }

        JLabel mainLabel = new JLabel(mainMessage, SwingConstants.CENTER);
        mainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        mainLabel.setForeground(new Color(70, 60, 95));

        JLabel subLabel = new JLabel(
                "You answered " + correctCount + " out of " + totalQuestions + " correctly.",
                SwingConstants.CENTER
        );
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subLabel.setForeground(new Color(130, 120, 145));

        topPanel.add(mainLabel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(subLabel);
        topPanel.add(Box.createVerticalStrut(18));

        card.add(topPanel, BorderLayout.NORTH);

        // ========= MIDDLE: RESULT STAT BOXES =========
JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
statsRow.setOpaque(false);

// Each question = 10 points
int totalPossiblePoints = totalQuestions * 10;

// SCORE FIXES:

// percentage score (rounded without decimals)
int percentage = (totalQuestions > 0)
        ? (int) Math.round((correctCount * 100.0) / totalQuestions)
        : 0;

// Score message ONLY shown under trophy (already correct)
String scoreMessageTop = "You answered " + correctCount + " out of " + totalQuestions + " correctly.";
subLabel.setText(scoreMessageTop);

// Stat Cards:
statsRow.add(new InfoBox("⭐ Total Points", sessionPoints + " Points"));
statsRow.add(new InfoBox("🎯 Score", percentage + "%"));
statsRow.add(new InfoBox("⏱ Time", formatTime(timeTakenMillis)));

JPanel midWrapper = new JPanel();
midWrapper.setOpaque(false);
midWrapper.setLayout(new BoxLayout(midWrapper, BoxLayout.Y_AXIS));
midWrapper.add(Box.createVerticalStrut(10));
midWrapper.add(statsRow);

card.add(midWrapper, BorderLayout.CENTER);


        // ========= BOTTOM: BUTTONS =========
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottomPanel.setOpaque(false);

        JButton homeBtn = new JButton("Back to Home");
        homeBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        homeBtn.setBackground(new Color(255, 210, 225));
        homeBtn.setForeground(new Color(80, 60, 70));
        homeBtn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        homeBtn.setFocusPainted(false);
        homeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        homeBtn.addActionListener(e -> {
            new PlayerHomePage(userId).setVisible(true);
            dispose();
        });

        JButton againBtn = new JButton("Get More Points!");
        againBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        againBtn.setBackground(new Color(245, 232, 245));
        againBtn.setForeground(new Color(80, 60, 90));
        againBtn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        againBtn.setFocusPainted(false);
        againBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        againBtn.addActionListener(e -> {
            // You can pass real level & points later when DB is ready
            new ExtendedPlaySetupPage(userId, username, 0, 0, null).setVisible(true);
            dispose();
        });

        bottomPanel.add(homeBtn);
        bottomPanel.add(againBtn);

        card.add(bottomPanel, BorderLayout.SOUTH);
    }

    /** Small rounded stat card like “TOTAL XP / AMAZING / SPEEDY”. */
    private JPanel createStatCard(String title, String value, Color bg, Color border) {
        JPanel outer = new JPanel();
        outer.setOpaque(false);
        outer.setPreferredSize(new Dimension(180, 110));

        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(bg);
        card.setBorder(BorderFactory.createLineBorder(border, 2, true));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(new Color(90, 80, 100));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        valueLabel.setForeground(new Color(70, 60, 90));

        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(8));

        outer.setLayout(new GridBagLayout());
        outer.add(card);

        return outer;
    }

    /** mm:ss formatting. */
    private String formatTime(long millis) {
        long totalSec = millis / 1000;
        long min = totalSec / 60;
        long sec = totalSec % 60;
        return String.format("%d:%02d", min, sec);
    }

    // ===== Rounded pastel card (same style as other pages) =====
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
            g2.fillRoundRect(6, 8, w - 12, h - 12,
                    cornerRadius + 12, cornerRadius + 12);

            g2.setColor(new Color(252, 249, 254));
            g2.fillRoundRect(0, 0, w - 12, h - 12,
                    cornerRadius, cornerRadius);

            g2.setColor(new Color(245, 220, 230));
            g2.drawRoundRect(0, 0, w - 12, h - 12,
                    cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
private class InfoBox extends JPanel {
    private final String title;
    private final String value;

    public InfoBox(String title, String value) {
        this.title = title;
        this.value = value;
        setOpaque(false);
        setPreferredSize(new Dimension(170, 100));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        Color boxColor;
        Color borderColor;
        Color valueColor;

        // === Assign background colors by title ===
        if (title.contains("Total")) {
            boxColor = new Color(255, 255, 210);     // light yellow
            borderColor = new Color(250, 240, 170);
            valueColor = new Color(218, 165, 32);    // GOLD
        } else if (title.contains("Score")) {
            boxColor = new Color(215, 255, 215);     // light green
            borderColor = new Color(180, 235, 180);
            valueColor = new Color(0, 120, 0);       // DARK GREEN
        } else { 
            boxColor = new Color(210, 235, 255);     // light blue
            borderColor = new Color(170, 215, 240);
            valueColor = new Color(0, 70, 160);      // DARK BLUE
        }

        // === Background ===
        g2.setColor(boxColor);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);

        // === Border ===
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);

        // === Title ===
        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2.setColor(new Color(60, 55, 90));
        FontMetrics fmTitle = g2.getFontMetrics();
        int titleX = (getWidth() - fmTitle.stringWidth(title)) / 2;
        g2.drawString(title, titleX, 30);

        // === VALUE (centered) ===
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.setColor(valueColor);
        FontMetrics fmVal = g2.getFontMetrics();
        int valX = (getWidth() - fmVal.stringWidth(value)) / 2;
        g2.drawString(value, valX, 65);

        g2.dispose();
    }
  }
}