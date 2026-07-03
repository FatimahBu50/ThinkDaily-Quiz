package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoleSelectionPage extends JFrame {

    public RoleSelectionPage() {

        setTitle("ThinkDaily - Select Your Role");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());   
        

        // LEFT PANEL (Logo) 
        JPanel leftPanel = new GradientPanel();
        leftPanel.setPreferredSize(new Dimension(480, 700));
        leftPanel.setLayout(new GridBagLayout()); 

        ImageIcon rawLogo = new ImageIcon(getClass().getResource("/logo/logo.png"));
        Image scaledLogoImg = rawLogo.getImage().getScaledInstance(260, 260, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogoImg));
        leftPanel.add(logoLabel);

        add(leftPanel, BorderLayout.WEST);  

        // RIGHT PANEL (Role Selection)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);

        // container to hold all title + buttons + footer
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //  TOP: Title + Subtitle 
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome!", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(70, 60, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Choose who want to enter ThinkDaily.", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(140, 130, 150));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(subtitle);
        titlePanel.add(Box.createVerticalStrut(30));

        contentPanel.add(titlePanel);

        // CENTER: Buttons side-by-side 
        JButton adminBtn = createRoleButton(
                loadIcon("/RolePhotos/Admins.png"),
                new Color(205, 225, 255),
                new Color(185, 215, 255)
        );

        JButton playerBtn = createRoleButton(
                loadIcon("/RolePhotos/Playerss.png"),
                new Color(255, 210, 225),
                new Color(255, 190, 215)
        );

        // fixed button size
        Dimension btnSize = new Dimension(160, 200);
        adminBtn.setPreferredSize(btnSize);
        adminBtn.setMaximumSize(btnSize);

        playerBtn.setPreferredSize(btnSize);
        playerBtn.setMaximumSize(btnSize);
        
        
        // actions to open login pages
                adminBtn.addActionListener(e -> {
                    new AdminLoginPage().setVisible(true);
                    dispose();
                });

                playerBtn.addActionListener(e -> {
                    new PlayerLoginPage().setVisible(true);
                    dispose();
                });
                
                
        // side-by-side panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 35, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.add(adminBtn);
        buttonsPanel.add(playerBtn);

        contentPanel.add(buttonsPanel);
        contentPanel.add(Box.createVerticalStrut(25));

                // FOOTER LINKS
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        linksPanel.setBackground(Color.WHITE);

        JButton policies = smallLink("Policies");
        JButton guidelines = smallLink("Guidelines");
        JButton help = smallLink("Help");
        policies.addActionListener(e ->
                        InfoDialog.show(this, "Policies", getPoliciesText())
                );
                guidelines.addActionListener(e ->
                        InfoDialog.show(this, "Guidelines", getGuidelinesText())
                );
                help.addActionListener(e ->
                        InfoDialog.show(this, "Help", getHelpText())
                );
        linksPanel.add(policies);
        linksPanel.add(guidelines);
        linksPanel.add(help);

        contentPanel.add(linksPanel);

        rightPanel.add(contentPanel); 

        add(rightPanel, BorderLayout.CENTER);}
    
    
    
        // HELPER: Load icons 
        private ImageIcon loadIcon(String path) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image scaled = icon.getImage().getScaledInstance(100, -1, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
        }

        // HELPER: Softer Rounded Role Buttons
        private JButton createRoleButton(ImageIcon icon, Color baseColor, Color hoverColor) {
        JButton btn = new JButton(icon) {
            private boolean hover = false;
            {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3)); 

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }

            
        @Override
        protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = hover ? hoverColor : baseColor;
        g2.setColor(fill);

        //Rounded pastel background behind icon
        int padding = 1; 
        int arc = 25;     // how round the corners are

        int bgX = padding;
        int bgY = padding;
        int bgW = getWidth() - padding * 2;
        int bgH = getHeight() - padding * 2;

         g2.fillRoundRect(bgX, bgY, bgW, bgH, arc, arc);

        g2.dispose();
        super.paintComponent(g);
        }
        };

        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setOpaque(false);

        return btn;
    }

    private JButton smallLink(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setForeground(new Color(150, 140, 150));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // GRADIENT PANEL (Left side) 
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            int w = getWidth();
            int h = getHeight();

            Color top = new Color(255, 235, 240);
            Color mid = new Color(255, 225, 245);
            Color bottom = new Color(230, 240, 255);

            GradientPaint gp1 = new GradientPaint(0, 0, top, w, h / 2, mid, false);
            g2.setPaint(gp1);
            g2.fillRect(0, 0, w, h);

            GradientPaint gp2 = new GradientPaint(0, h / 2, mid, w, h, bottom, false);
            g2.setPaint(gp2);
            g2.fillRect(0, h / 2, w, h);

            g2.dispose();
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoleSelectionPage().setVisible(true));
    }
}
