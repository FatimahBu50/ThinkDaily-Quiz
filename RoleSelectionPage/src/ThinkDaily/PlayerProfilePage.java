package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiFunction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import ThinkDaily.ThinkDailyQueries;


public class PlayerProfilePage extends JFrame {
    private static final int POINTS_PER_LEVEL = 100;
    // Future DB fields (used to fill the UI)
    private final int userId;
    private String currentName;
    private int currentLevel;
    private int currentPoints;
    private String currentBio;
    private String currentIconPath;

    // username used in "Username" row (@handle) – read from accounts.username
    private String currentUsername;

    private static final String ICONS_FOLDER = "/icons/";

    private ImageIcon loadAvatarIcon(String fileName, int size) {
        if (fileName == null || fileName.isBlank()) return null;
        try {
            ImageIcon raw = new ImageIcon(getClass().getResource(ICONS_FOLDER + fileName));
            Image scaled = raw.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.err.println("Icon not found: " + ICONS_FOLDER + fileName);
            return null;
        }
    }
    

    //  UI components 
    private JLabel avatarLabel;
    private JLabel nameLabel;
    private JLabel usernameValueLabel;
    private JLabel bioDisplayLabel;
    private JProgressBar levelBar;

    public PlayerProfilePage(int userId, String name, int level, int points, String bio, String iconPath) {

        this.userId = userId;
        this.currentName = name;
        this.currentLevel = level;
        this.currentPoints = points;
        this.currentBio = bio;
        this.currentIconPath = iconPath != null ? iconPath : UserIconSession.getIcon(userId);
        
         ImageIcon avatarIcon = loadAvatarIcon(currentIconPath, 120);

        // ===== 1) Try to load latest info from DB =====
        loadUserFromDb();

        setTitle("ThinkDaily - My Profile");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // When profile closes, reopen homepage
                new PlayerHomePage(userId).setVisible(true);
            }
        });

        // TOP BAR: Back + Logo + Settings 
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 10, 20));

        // Back button (top-left)
        JButton backBtn = new JButton("⟵ Back");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setBackground(new Color(245, 232, 245));
        backBtn.setForeground(new Color(90, 70, 100));
        backBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            // when DB is ready, this will receive the same userId
            new PlayerHomePage(userId).setVisible(true);
            dispose();
        });
        topPanel.add(backBtn, BorderLayout.WEST);

        // Right side: settings gear + logo
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topRight.setOpaque(false);

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

        // ⚙ Settings Button
        JButton settingsBtn = new JButton("⚙");
        settingsBtn.setFont(new Font("SansSerif", Font.PLAIN, 30));
        settingsBtn.setContentAreaFilled(false);
        settingsBtn.setBorderPainted(false);
        settingsBtn.setFocusPainted(false);
        settingsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        settingsBtn.setToolTipText("Settings");

        // ===== Styled Menu =====
        JPopupMenu settingsMenu = new JPopupMenu();

        // Pastel menu styling
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

        // Menu Items
        JMenuItem updatePasswordItem = new StyledMenuItem("🔑 Update Password");
        JMenuItem helpItem = new StyledMenuItem("💡 Help");
        JMenuItem guidelinesItem = new StyledMenuItem("📘 Guidelines");
        JMenuItem aboutItem = new StyledMenuItem("✨ About ThinkDaily");
        JMenuItem logoutItem = new StyledMenuItem("🚪 Logout");

        // Add Menu Items 
        settingsMenu.add(updatePasswordItem);
        settingsMenu.addSeparator();
        settingsMenu.add(helpItem);
        settingsMenu.add(guidelinesItem);
        settingsMenu.add(aboutItem);
        settingsMenu.addSeparator();
        settingsMenu.add(logoutItem);

        // Show menu when clicking settings
        settingsBtn.addActionListener(e ->
                settingsMenu.show(settingsBtn, 0, settingsBtn.getHeight())
        );

        //  UI messages 
        updatePasswordItem.addActionListener(e ->
                showUpdatePasswordDialog());
        helpItem.addActionListener(e ->
                InfoDialog.show(this, "Help", getHelpText()));
        guidelinesItem.addActionListener(e ->
                InfoDialog.show(this, "Guidelines", getGuidelinesText()));
        aboutItem.addActionListener(e -> InfoDialog.show(this, "About ThinkDaily", getAboutText()));
        logoutItem.addActionListener(e -> {
            new RoleSelectionPage().setVisible(true);
            dispose();
        });

        topRight.add(settingsBtn);
        topRight.add(logoLabel);
        topPanel.add(topRight, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ========== CENTER BACKGROUND (soft gradient) ==========
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

        // Rounded card in the middle
        RoundedPanel card = new RoundedPanel(32);
        card.setOpaque(false);
        Dimension cardSize = new Dimension(600, 440);
        card.setPreferredSize(cardSize);
        card.setMinimumSize(cardSize);
        card.setMaximumSize(cardSize);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 25));

        centerBg.add(card, gbc);

        // "My Profile" title (center)
        JLabel titleLabel = new JLabel("My Profile", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(70, 60, 95));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(16));

        JPanel TitleInfoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        TitleInfoWrapper.setOpaque(false);
        TitleInfoWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        TitleInfoWrapper.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(TitleInfoWrapper);

        // Avatar (center)
        avatarLabel = new JLabel();
        

        if (avatarIcon != null) {
            avatarLabel.setIcon(avatarIcon);
        } else {
            avatarLabel.setText("🙂"); // Default emoji if no icon chosen
            avatarLabel.setFont(new Font("SansSerif", Font.PLAIN, 55));
        }
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(avatarLabel);
        card.add(Box.createVerticalStrut(4));

        JPanel iconInfoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconInfoWrapper.setOpaque(false);
        iconInfoWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconInfoWrapper.add(avatarLabel);

        card.add(Box.createVerticalStrut(5));
        card.add(iconInfoWrapper);

        // "Change icon" button
        JButton editIconBtn = new JButton("Change Icon");
        editIconBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        editIconBtn.setForeground(new Color(90, 70, 110));
        editIconBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editIconBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        editIconBtn.setContentAreaFilled(false);
        editIconBtn.setFocusPainted(false);
        editIconBtn.setBorderPainted(false);
        editIconBtn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        Dimension fixedSize = new Dimension(150, 35);
        editIconBtn.setPreferredSize(fixedSize);
        editIconBtn.setMaximumSize(fixedSize);
        editIconBtn.setMinimumSize(fixedSize);

        editIconBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                editIconBtn.setForeground(new Color(150, 110, 170));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                editIconBtn.setForeground(new Color(90, 70, 110));
            }
        });

        card.add(Box.createVerticalStrut(10));
        card.add(editIconBtn);
        card.add(Box.createVerticalStrut(15));

        JPanel editiconInfoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        editiconInfoWrapper.setOpaque(false);
        editiconInfoWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        editiconInfoWrapper.add(editIconBtn);

        card.add(Box.createVerticalStrut(5));
        card.add(editiconInfoWrapper);

        // Wrapper to push info to the *left* inside the card
        JPanel infoWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        infoWrapper.setOpaque(false);
        infoWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(infoWrapper);

        // Vertical info panel inside wrapper
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoWrapper.add(infoPanel);

        // Standard font for titles + values
        Font infoFont = new Font("SansSerif", Font.PLAIN, 13);
        Color titleColor = new Color(110, 100, 135);
        Color valueColor = new Color(70, 60, 95);

        // Helper to create row
        BiFunction<String, String, JPanel> createRow = (title, value) -> {
            JLabel t = new JLabel(title + ":");
            t.setFont(infoFont);
            t.setForeground(titleColor);

            JLabel v = new JLabel(value);
            v.setFont(infoFont);
            v.setForeground(valueColor);

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            row.setOpaque(false);
            row.add(t);
            row.add(v);
            return row;
        };

        // ------- 1) Name -------
        String shownName = (currentName != null && !currentName.isBlank()) ? currentName : "Player Name";
        JPanel nameRow = createRow.apply("Name", shownName);
        infoPanel.add(nameRow);
        this.nameLabel = (JLabel) nameRow.getComponent(1);
        infoPanel.add(Box.createVerticalStrut(6));

        // ------- 2) Username -------
        String shownUsername = (currentUsername != null && !currentUsername.isBlank())
                ? "@" + currentUsername
                : "@username";
        JPanel userRow = createRow.apply("Username", shownUsername);
        infoPanel.add(userRow);
        this.usernameValueLabel = (JLabel) userRow.getComponent(1); // FIX: keep reference
        infoPanel.add(Box.createVerticalStrut(6));

        // ------- 3) Bio -------
        String shownBio = (currentBio != null && !currentBio.isBlank())
                ? currentBio
                : "Tap \"Edit info\" to add a bio.";
        bioDisplayLabel = new JLabel("<html>" + shownBio + "</html>");
        JPanel bioRow = createRow.apply("Bio", "");
        bioRow.add(bioDisplayLabel);
        infoPanel.add(bioRow);
        infoPanel.add(Box.createVerticalStrut(8));

        // ------- 4) Level -------
        JPanel levelRow = createRow.apply("Level",
                currentLevel > 0 ? String.valueOf(currentLevel) : "-");
        infoPanel.add(levelRow);
        infoPanel.add(Box.createVerticalStrut(6));

        // ------- 5) Points -------
        JPanel pointsRow = createRow.apply("Points",
                currentPoints > 0 ? String.valueOf(currentPoints) : "-");
        infoPanel.add(pointsRow);
        infoPanel.add(Box.createVerticalStrut(10));

        // 6) Edit info button 
        JButton editInfoBtn = new JButton("Edit info");
        editInfoBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        editInfoBtn.setBackground(new Color(255, 190, 215));
        editInfoBtn.setForeground(new Color(80, 60, 90));
        editInfoBtn.setFocusPainted(false);
        editInfoBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        editInfoBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel editInfoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        editInfoWrapper.setOpaque(false);
        editInfoWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        editInfoWrapper.add(editInfoBtn);
        card.add(Box.createVerticalStrut(4));
        card.add(editInfoWrapper);

        // BUTTON ACTIONS 
        editIconBtn.addActionListener(e -> showIconChooserDialog());
        editInfoBtn.addActionListener(e -> showEditInfoDialog());
    }

    // ========== DB: load user's profile info ==========
    private void loadUserFromDb() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("DB connection is null in PlayerProfilePage.");
            return;
        }

        String sql =
                "SELECT u.user_name, u.bio, u.current_level, u.current_points_total, u.current_icon_path, a.username " +
                "FROM users u " +
                "JOIN accounts a ON u.account_id = a.account_id " +
                "WHERE u.user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbName = rs.getString("user_name");
                    String dbBio = rs.getString("bio");
                    int dbLevel = rs.getInt("current_level");
                    int dbPoints = rs.getInt("current_points_total");
                    String dbIconPath = rs.getString("current_icon_path");
                    String dbUsername = rs.getString("username");

                    if (dbName != null) currentName = dbName;
                    if (dbBio != null) currentBio = dbBio;
                    currentPoints = dbPoints;

                     recalcLevelFromPoints();

                    if (dbIconPath != null) currentIconPath = dbIconPath;
                    if (dbUsername != null) currentUsername = dbUsername;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error loading user profile from DB: " + ex.getMessage());
        }
    }
    // ========== LEVEL CALCULATION ==========
private void recalcLevelFromPoints() {
    currentLevel = Math.max(1, (currentPoints / POINTS_PER_LEVEL) + 1);
}


    // ========== DB: save name, bio & icon ==========
    private void saveProfileToDb() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("DB connection is null when saving profile.");
            return;
        }

        String sql =
                "UPDATE users SET user_name = ?, bio = ?, current_icon_path = ? " +
                "WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentName);
            ps.setString(2, currentBio);
            ps.setString(3, currentIconPath);
            ps.setInt(4, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error saving profile to DB: " + ex.getMessage());
        }
    }
private void updateUsernameInDb(String newUsername) {
    try (Connection conn = DBConnection.getConnection()) {
        if (conn == null) return;

        String sql = """
            UPDATE accounts a
            JOIN users u ON a.account_id = u.account_id
            SET a.username = ?
            WHERE u.user_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newUsername);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    } catch (SQLException ex) {
        System.out.println("Error updating username in DB: " + ex.getMessage());
    }
}
    //  Edit name & bio dialog
    private void showEditInfoDialog() {
        JTextField nameField = new JTextField(
                currentName != null ? currentName : ""
        );

        JTextField usernameField = new JTextField(
                currentUsername != null ? currentUsername : ""
        );

        JTextArea bioField = new JTextArea(4, 20);
        bioField.setText(currentBio != null ? currentBio : "");
        bioField.setLineWrap(true);
        bioField.setWrapStyleWord(true);

        JScrollPane bioScroll = new JScrollPane(bioField);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(new JLabel("Bio:"));
        panel.add(bioScroll);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit profile info",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

            // update name
            String newName = nameField.getText().trim();
            if (!newName.isEmpty()) {
                currentName = newName;
                nameLabel.setText(newName);
            }

            // update username (UI only; not saved to DB to avoid changing login)
            String newUsername = usernameField.getText().trim();
            if (!newUsername.isEmpty() && newUsername.startsWith("@")) {
                currentUsername = newUsername.substring(1);
            } else if (!newUsername.isEmpty()) {
                currentUsername = newUsername;
            }
            if (currentUsername != null && !currentUsername.isEmpty()) {
                usernameValueLabel.setText("@" + currentUsername);
                updateUsernameInDb(currentUsername); 
            }

            // update bio
            currentBio = bioField.getText();
            bioDisplayLabel.setText("<html>" + currentBio.replace("\n", "<br>") + "</html>");

            // Save to DB
            saveProfileToDb();

            showProfileUpdatedMessage();
        }
    }

    // Icon chooser dialog
    private void showIconChooserDialog() {
        JDialog dialog = new JDialog(this, "Choose Icon", true);
        dialog.setSize(440, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(2, 5, 10, 10));
        panel.setBackground(Color.WHITE);

        String[] icons = {
                "hijabi.png",
                "blue.png",
                "girl.png",
                "cat.png",
                "duck.png",
                "smily.png",
                "star.png",
                "bear.png",
                "leaderboard.png",
                "rappet.png",
                "pink.png",
                "heart.png"
        };

        for (String iconName : icons) {
            ImageIcon icon = loadAvatarIcon(iconName, 70);
            if (icon == null) continue;

            JButton btn = new JButton(icon);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> {
                currentIconPath = iconName;        // store key
                avatarLabel.setIcon(loadAvatarIcon(iconName, 110));
                avatarLabel.setText(null);         // remove 🙂
                saveProfileToDb();               // save icon to DB
                UserIconSession.setIcon(userId, iconName);
                dialog.dispose();


               showProfileUpdatedMessage();

            });

            panel.add(btn);
        }

        dialog.add(panel);
        dialog.setVisible(true);
    }

    

// ===== Update password dialog with DB + hashing =====
// ======= UPDATE PASSWORD DIALOG (WITH HASHED PASSWORDS) =======
private void showUpdatePasswordDialog() {
    JPasswordField oldPass = new JPasswordField();
    JPasswordField newPass = new JPasswordField();
    JPasswordField confirmPass = new JPasswordField();

    Dimension fieldSize = new Dimension(230, 30);

    for (JPasswordField f : new JPasswordField[]{oldPass, newPass, confirmPass}) {
        f.setPreferredSize(fieldSize);
        f.setMaximumSize(fieldSize);
        f.setMinimumSize(fieldSize);
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));

        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 210, 235), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 160, 230), 2, true),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 210, 235), 1, true),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));
            }
        });
    }

    // ===== strength bar =====
    JProgressBar strengthBar = new JProgressBar(0, 100);
    strengthBar.setStringPainted(true);
    strengthBar.setValue(0);
    strengthBar.setPreferredSize(new Dimension(230, 12));
    strengthBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    strengthBar.setBackground(new Color(248, 243, 252));

    JLabel strengthLabel = new JLabel("Strength: -");
    strengthLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
    strengthLabel.setForeground(new Color(130, 115, 150));

    newPass.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        private void update() {
            String pw = new String(newPass.getPassword());
            int targetScore = calculatePasswordStrength(pw);
            animateStrength(strengthBar, targetScore);

            String text = getStrengthLabel(targetScore);
            strengthBar.setString(text);
            if (targetScore < 40) {
                strengthBar.setForeground(new Color(255, 140, 140));
            } else if (targetScore < 80) {
                strengthBar.setForeground(new Color(255, 200, 140));
            } else {
                strengthBar.setForeground(new Color(170, 215, 170));
            }
        }

        public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
    });

    JPanel oldRow = createPasswordRow("Current password", oldPass);
    JPanel newRow = createPasswordRow("New password", newPass);
    JPanel confirmRow = createPasswordRow("Confirm new password", confirmPass);

    JPanel strengthRow = new JPanel();
    strengthRow.setOpaque(false);
    strengthRow.setLayout(new BoxLayout(strengthRow, BoxLayout.Y_AXIS));
    strengthRow.add(strengthLabel);
    strengthRow.add(Box.createVerticalStrut(4));
    strengthRow.add(strengthBar);
    strengthRow.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(252, 248, 255));
    panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 205, 240), 1, true),
            BorderFactory.createEmptyBorder(10, 8, 10, 8)
    ));

    panel.add(oldRow);
    panel.add(Box.createVerticalStrut(8));
    panel.add(newRow);
    panel.add(Box.createVerticalStrut(6));
    panel.add(strengthRow);
    panel.add(Box.createVerticalStrut(8));
    panel.add(confirmRow);

    // ===== MAIN LOOP =====
    while (true) {
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            break;
        }

        String oldPw = new String(oldPass.getPassword()).trim();
        String newPw = new String(newPass.getPassword()).trim();
        String confirmPw = new String(confirmPass.getPassword()).trim();

        if (oldPw.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields.",
                    "Missing information",
                    JOptionPane.WARNING_MESSAGE
            );
            continue;
        }

        // strong policy (simplified but strict)
        if (!meetsStrongPolicy(newPw)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Password must be at least 8 characters and include at least 3 of:\n" +
                            "- lowercase letters\n" +
                            "- UPPERCASE letters\n" +
                            "- numbers\n" +
                            "- symbols (e.g. !, @, #, ?)\n",
                    "Password too weak",
                    JOptionPane.WARNING_MESSAGE
            );
            continue;
        }

        if (!newPw.equals(confirmPw)) {
            JOptionPane.showMessageDialog(
                    this,
                    "New password and confirmation don’t match!",
                    "Mismatch",
                    JOptionPane.WARNING_MESSAGE
            );
            continue;
        }

        // --- DB PART: check old password (password_hash) and update ---
        // ====== CHECK OLD PASSWORD + UPDATE IN DB (using accounts table) ======
try (java.sql.Connection conn = DBConnection.getConnection()) {

    if (conn == null) {
        JOptionPane.showMessageDialog(
                this,
                "Cannot connect to the database right now.\nPlease try again later.",
                "Connection error",
                JOptionPane.ERROR_MESSAGE
        );
        break;
    }

    // 1) Get account_id + stored password_hash for this user
    Integer accountId = null;
    String storedHash = null;

    String checkSql = """
        SELECT a.account_id, a.password_hash
        FROM users u
        JOIN accounts a ON u.account_id = a.account_id
        WHERE u.user_id = ?
    """;

    try (java.sql.PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
        checkPs.setInt(1, userId);
        try (java.sql.ResultSet rs = checkPs.executeQuery()) {
            if (!rs.next()) {
                JOptionPane.showMessageDialog(
                        this,
                        "User not found. Please log in again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                break;
            }

            accountId = rs.getInt("account_id");
            storedHash = rs.getString("password_hash");
        }
    }

    // 2) Verify old password (handles both plain & SHA-256 hash)
    if (!verifyPassword(oldPw, storedHash)) {
        JOptionPane.showMessageDialog(
                this,
                "Current password is incorrect.",
                "Wrong password",
                JOptionPane.ERROR_MESSAGE
        );
        continue; // back to dialog
    }

    // 3) Hash new password and update accounts.password_hash
    String newHash = hashPassword(newPw);

    String updateSql = "UPDATE accounts SET password_hash = ? WHERE account_id = ?";
    try (java.sql.PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
        updatePs.setString(1, newHash);
        updatePs.setInt(2, accountId);

        int updated = updatePs.executeUpdate();
        if (updated > 0) {
            JPanel successPanel = createSuccessPanel(
                    "Password updated successfully 🎉",
                    "Your new password has been saved.\n" +
                    "Please make sure you remember it!"
            );

            JOptionPane.showMessageDialog(
                    this,
                    successPanel,
                    "Password Updated",
                    JOptionPane.PLAIN_MESSAGE
            );

            oldPass.setText("");
            newPass.setText("");
            confirmPass.setText("");
            break; // exit while(true)
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Something went wrong. No changes were saved.",
                    "Update error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

} catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(
            this,
            "An error occurred while updating your password:\n" + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
    );
}
    }
}
    private JPanel createPasswordRow(String labelText, JPasswordField field) {
        JLabel label = new JLabel(labelText + ":");
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(90, 75, 110));

        JButton eyeBtn = new JButton("👁");
        eyeBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
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

        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));

        JPanel fieldRow = new JPanel(new BorderLayout(6, 0));
        fieldRow.setOpaque(false);
        fieldRow.add(field, BorderLayout.CENTER);
        fieldRow.add(eyeBtn, BorderLayout.EAST);

        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(label);
        row.add(Box.createVerticalStrut(2));
        row.add(fieldRow);

        return row;
    }

   private int calculatePasswordStrength(String pw) {
    if (pw == null) return 0;
    int score = 0;

    int len = pw.length();
    boolean hasLower  = pw.matches(".*[a-z].*");
    boolean hasUpper  = pw.matches(".*[A-Z].*");
    boolean hasDigit  = pw.matches(".*[0-9].*");
    boolean hasSymbol = pw.matches(".*[^a-zA-Z0-9].*");

    if (len >= 8)  score += 30;
    if (len >= 12) score += 20;

    if (hasLower)  score += 15;
    if (hasUpper)  score += 15;
    if (hasDigit)  score += 10;
    if (hasSymbol) score += 10;

    int types = 0;
    if (hasLower) types++;
    if (hasUpper) types++;
    if (hasDigit) types++;
    if (hasSymbol) types++;
    if (len >= 14 && types >= 3) score += 10;

    return Math.min(score, 100);
}

private boolean meetsStrongPolicy(String pw) {
    if (pw == null) return false;
    if (pw.length() < 8) return false;

    boolean hasLower  = pw.matches(".*[a-z].*");
    boolean hasUpper  = pw.matches(".*[A-Z].*");
    boolean hasDigit  = pw.matches(".*[0-9].*");
    boolean hasSymbol = pw.matches(".*[^a-zA-Z0-9].*");

    int types = 0;
    if (hasLower) types++;
    if (hasUpper) types++;
    if (hasDigit) types++;
    if (hasSymbol) types++;

    return types >= 3; // at least 3 of 4 types
}

private String getStrengthLabel(int score) {
    if (score == 0)      return "-";
    else if (score < 40) return "Weak";
    else if (score < 80) return "Medium";
    else                 return "Strong";
}



    private void animateStrength(JProgressBar bar, int target) {
        int start = bar.getValue();
        if (start == target) return;

        int step = (target > start) ? 1 : -1;

        javax.swing.Timer timer = new javax.swing.Timer(10, null);
        timer.addActionListener(e -> {
            int current = bar.getValue();
            if ((step > 0 && current >= target) || (step < 0 && current <= target)) {
                bar.setValue(target);
                timer.stop();
            } else {
                bar.setValue(current + step);
            }
        });
        timer.start();
    }

   private JPanel createSuccessPanel(String title, String message) {
    // Outer container – keeps things centered and transparent
    JPanel outer = new JPanel(new GridBagLayout());
    outer.setOpaque(false);

    // Inner "card" with soft shadow + gradient
    JPanel card = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 28;

            // 🌫 Soft shadow (behind the card)
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(6, 8, w - 12, h - 12, arc + 10, arc + 10);

            // 🎀 Gradient background for the card
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(255, 247, 255),
                    w, h, new Color(241, 222, 250)
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w - 12, h - 12, arc, arc);

            // 💗 Soft border
            g2.setColor(new Color(222, 188, 235));
            g2.drawRoundRect(0, 0, w - 12, h - 12, arc, arc);

            // 🎗 subtle "ribbon" line near the top
            g2.setColor(new Color(245, 210, 230));
            g2.fillRoundRect(16, 42, w - 44, 3, 12, 12);

            g2.dispose();
            super.paintComponent(g);
        }
    };

    card.setOpaque(false);
    card.setLayout(new BorderLayout(10, 10));
    card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));


    // 🌸 Title
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
    titleLabel.setForeground(new Color(82, 60, 125));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Text
    JTextArea msgArea = new JTextArea(message);
    msgArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
    msgArea.setForeground(new Color(110, 86, 140));
    msgArea.setEditable(false);
    msgArea.setOpaque(false);
    msgArea.setLineWrap(true);
    msgArea.setWrapStyleWord(true);
    msgArea.setFocusable(false);
    msgArea.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Center stack (title + message)
    JPanel textPanel = new JPanel();
    textPanel.setOpaque(false);
    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
    textPanel.add(Box.createVerticalStrut(6));
    textPanel.add(titleLabel);
    textPanel.add(Box.createVerticalStrut(6));
    textPanel.add(msgArea);
    textPanel.add(Box.createVerticalStrut(4));

    
    card.add(textPanel, BorderLayout.CENTER);

    outer.add(card);
    return outer;
}

    
   // 🔐 Hash password using SHA-256 (matches your long hashes in accounts.password_hash)
private String hashPassword(String pw) throws java.security.NoSuchAlgorithmException {
    if (pw == null) return null;
    java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
    byte[] hashBytes = md.digest(pw.getBytes(java.nio.charset.StandardCharsets.UTF_8));

    StringBuilder sb = new StringBuilder();
    for (byte b : hashBytes) {
        sb.append(String.format("%02x", b));
    }
    return sb.toString();
}

// Detects if stored value looks like a SHA-256 hex hash (64 hex chars)
private boolean isProbablySha256(String s) {
    return s != null && s.matches("[0-9a-fA-F]{64}");
}

// Compare user-typed old password with stored value (hash or legacy plain)
private boolean verifyPassword(String rawPw, String stored) throws java.security.NoSuchAlgorithmException {
    if (stored == null) return false;

    if (isProbablySha256(stored)) {
        String rawHash = hashPassword(rawPw);
        return stored.equalsIgnoreCase(rawHash);
    } else {
        // old legacy accounts with plain text like '12345678'
        return stored.equals(rawPw);
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
private void showProfileUpdatedMessage() {
    JPanel successPanel = createSuccessPanel(
            "Updated successfully ✅",
            "Your profile information was saved.\n" +
            "Changes will appear everywhere in the app."
    );

    JOptionPane.showMessageDialog(
            this,
            successPanel,
            "Saved",
            JOptionPane.PLAIN_MESSAGE
    );
}
    // ========== Rounded pastel card ==========
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
}
