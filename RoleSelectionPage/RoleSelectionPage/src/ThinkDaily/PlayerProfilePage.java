package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiFunction;

public class PlayerProfilePage extends JFrame {

    //Future DB fields (used only to fill the UI for now) 
    private final int userId;
    private String currentName;
    private int currentLevel;
    private int currentPoints;
    private String currentBio;
    private String currentIconPath;
    
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
    private String currentUsername;
    private JLabel usernameValueLabel;
    private JLabel bioDisplayLabel;
    private JProgressBar levelBar;


    public PlayerProfilePage(int userId,String name,int level,int points,String bio, String iconPath){

        this.userId         = userId;
        this.currentName    = name;
        this.currentLevel   = level;
        this.currentPoints  = points;
        this.currentBio     = bio;
        this.currentIconPath = iconPath;
        

        setTitle("ThinkDaily - My Profile");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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
        JMenuItem helpItem           = new StyledMenuItem("💡 Help");
        JMenuItem guidelinesItem     = new StyledMenuItem("📘 Guidelines");
        JMenuItem aboutItem          = new StyledMenuItem("✨ About ThinkDaily");
        JMenuItem logoutItem         = new StyledMenuItem("🚪 Logout");

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



        //  INSIDE CARD :

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
        ImageIcon avatarIcon = loadAvatarIcon(currentIconPath, 110);

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

        
        // "Change icon" button – cute & stable
        JButton editIconBtn = new JButton("Change Icon");
        editIconBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        editIconBtn.setForeground(new Color(90, 70, 110));
        editIconBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editIconBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        editIconBtn.setContentAreaFilled(false);
        editIconBtn.setFocusPainted(false);
        editIconBtn.setBorderPainted(false);
        editIconBtn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        // Set fixed size 
        Dimension fixedSize = new Dimension(150, 35);
        editIconBtn.setPreferredSize(fixedSize);
        editIconBtn.setMaximumSize(fixedSize);
        editIconBtn.setMinimumSize(fixedSize);

        // Soft hover effect 
        editIconBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                editIconBtn.setForeground(new Color(150, 110, 170)); // hover tint
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
        // ===== Vertical info panel inside wrapper =====
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
        this.nameLabel = (JLabel) nameRow.getComponent(1); // keep reference
        infoPanel.add(Box.createVerticalStrut(6));

        // ------- 2) Username (DB later) -------
        String shownUsername = "@username"; // TODO database later
        JPanel userRow = createRow.apply("Username", shownUsername);
        infoPanel.add(userRow);
        infoPanel.add(Box.createVerticalStrut(6));

        // ------- 3) Bio (single line, cute default) -------
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
                currentLevel > 0 ? String.valueOf(currentLevel) : "-"
        );
        infoPanel.add(levelRow);
        infoPanel.add(Box.createVerticalStrut(6));

        // ------- 5) Points -------
        JPanel pointsRow = createRow.apply("Points",
                currentPoints > 0 ? String.valueOf(currentPoints) : "-"
        );
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

        //  BUTTON ACTIONS 
        editIconBtn.addActionListener(e -> 
                showIconChooserDialog());
        editInfoBtn.addActionListener(e -> 
                showEditInfoDialog());
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

            // update username
            String newUsername = usernameField.getText().trim();
            if (!newUsername.isEmpty() && newUsername.startsWith("@")) {
                currentUsername = newUsername.substring(1); // remove @ for storing
            } else if (!newUsername.isEmpty()) {
                currentUsername = newUsername; // accept without @
            }
            usernameValueLabel.setText("@" + currentUsername);

            // update bio
            currentBio = bioField.getText();
            bioDisplayLabel.setText("<html>" + currentBio.replace("\n", "<br>") + "</html>");

            // later: save to DB here
        }
    }

        // ========== Icon chooser dialog (grid of clickable icons) ==========
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
                currentIconPath = iconName; // store key only
                avatarLabel.setIcon(loadAvatarIcon(iconName, 110));
                avatarLabel.setText(null); // remove 🙂 when set
                dialog.dispose();
                // SAVE TO DB LATER HERE
            });

            panel.add(btn);
        }

        dialog.add(panel);
        dialog.setVisible(true);
    }



    // Update password dialog UI + strength bar + rules
    private void showUpdatePasswordDialog() {
        // password fields (keep them outside the loop so values stay)
        JPasswordField oldPass     = new JPasswordField();
        JPasswordField newPass     = new JPasswordField();
        JPasswordField confirmPass = new JPasswordField();

        Dimension fieldSize = new Dimension(230, 30);

        for (JPasswordField f : new JPasswordField[]{oldPass, newPass, confirmPass}) {
            f.setPreferredSize(fieldSize);
            f.setMaximumSize(fieldSize);
            f.setMinimumSize(fieldSize);
            f.setFont(new Font("SansSerif", Font.PLAIN, 13));

            // soft pastel border (default)
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 210, 235), 1, true),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));

        // focus glow effect 
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

        // strength bar + label (for new password)
        JProgressBar strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(true);
        strengthBar.setValue(0);
        strengthBar.setPreferredSize(new Dimension(230, 12));
        strengthBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        strengthBar.setBackground(new Color(248, 243, 252));

        JLabel strengthLabel = new JLabel("Strength: -");
        strengthLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        strengthLabel.setForeground(new Color(130, 115, 150));

        // live strength update when typing new password (with animation C)
        newPass.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String pw = new String(newPass.getPassword());
                int targetScore = calculatePasswordStrength(pw);
                animateStrength(strengthBar, targetScore); // smooth animation

                String text = getStrengthLabel(targetScore);
                strengthBar.setString(text);
                if (targetScore < 40) {
                    strengthBar.setForeground(new Color(255, 140, 140));   // weak
                } else if (targetScore < 80) {
                    strengthBar.setForeground(new Color(255, 200, 140));  // medium
                } else {
                    strengthBar.setForeground(new Color(170, 215, 170));  // strong
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        // wrap each field with an "eye" button
        JPanel oldRow     = createPasswordRow("Current password", oldPass);
        JPanel newRow     = createPasswordRow("New password", newPass);
        JPanel confirmRow = createPasswordRow("Confirm new password", confirmPass);

        // panel for strength indicator
        JPanel strengthRow = new JPanel();
        strengthRow.setOpaque(false);
        strengthRow.setLayout(new BoxLayout(strengthRow, BoxLayout.Y_AXIS));
        strengthRow.add(strengthLabel);
        strengthRow.add(Box.createVerticalStrut(4));
        strengthRow.add(strengthBar);
        strengthRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // main panel (pastel background + rounded feel) (B)
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(252, 248, 255)); // soft lavender
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

    //  stay in dialog, not profile page 
    while (true) {
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            // user pressed Cancel or closed the dialog
            break;
        }

        String oldPw = new String(oldPass.getPassword()).trim();
        String newPw = new String(newPass.getPassword()).trim();
        String confirmPw = new String(confirmPass.getPassword()).trim();

        // 1) empty fields
        if (oldPw.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields.",
                    "Missing information",
                    JOptionPane.WARNING_MESSAGE
            );
            continue;
        }

        // 2) strong policy (F)
        if (!meetsStrongPolicy(newPw)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Password must be at least 8 characters and include:\n" +
                    "- lowercase letters\n" +
                    "- UPPERCASE letters\n" +
                    "- numbers\n" +
                    "- symbols (e.g. !, @, #, ?)\n",
                    "Password too weak",
                    JOptionPane.WARNING_MESSAGE
            );
            continue;
        }

        // 3) passwords must match
        if (!newPw.equals(confirmPw)) {
            JOptionPane.showMessageDialog(
                    this,
                    "New password and confirmation don’t match!",
                    "Mismatch",
                    JOptionPane.WARNING_MESSAGE
            );
            continue;
        }

        // 4) additional strength check (optional on top of policy)
        if (calculatePasswordStrength(newPw) < 60) {
            JOptionPane.showMessageDialog(
                    this,
                    "Your password is valid but still a bit weak.\n" +
                    "Try making it longer for extra security 💡",
                    "Low strength",
                    JOptionPane.WARNING_MESSAGE
            );
            
        }

        
        JPanel successPanel = createSuccessPanel(
                "Password updated (UI side) 🎉",
                "Database update will be enabled once it is connected."
        );

        JOptionPane.showMessageDialog(
                this,
                successPanel,
                "Password Updated",
                JOptionPane.PLAIN_MESSAGE
        );

        // fields cleanup after success
        oldPass.setText("");
        newPass.setText("");
        confirmPass.setText("");

        break; 
    }
}

        //  helper to create a row: label + password field + eye button.

        private JPanel createPasswordRow(String labelText, JPasswordField field) {
            JLabel label = new JLabel(labelText + ":");
            label.setFont(new Font("SansSerif", Font.PLAIN, 13));
            label.setForeground(new Color(90, 75, 110));

            // eye button
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

        //password strength scoring.
        private int calculatePasswordStrength(String pw) {
            if (pw == null) return 0;
            int score = 0;

            int len = pw.length();
            if (len >= 6)  score += 20;
            if (len >= 8)  score += 30;
            if (len >= 12) score += 20;

            if (pw.matches(".[0-9]."))        score += 15; // has digit
            if (pw.matches(".[a-z]."))        score += 15; // has lowercase
            if (pw.matches(".[A-Z]."))        score += 20; // has uppercase
            if (pw.matches(".[^a-zA-Z0-9].")) score += 30; // has symbol

            return Math.min(score, 100);
        }

        //Strong policy check 
        private boolean meetsStrongPolicy(String pw) {
            if (pw == null) return false;
            if (pw.length() < 8) return false;
            boolean hasLower = pw.matches(".[a-z].");
            boolean hasUpper = pw.matches(".[A-Z].");
            boolean hasDigit = pw.matches(".[0-9].");
            boolean hasSymbol = pw.matches(".[^a-zA-Z0-9].");
            return hasLower && hasUpper && hasDigit && hasSymbol;
        }

        //Strength label based on score.

        private String getStrengthLabel(int score) {
            if (score == 0)          return "-";
            else if (score < 40)     return "Weak";
            else if (score < 80)     return "Medium";
            else                     return "Strong";
        }

        // Smooth animation of progress bar value .

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

        //Cute success panel .

        private JPanel createSuccessPanel(String title, String message) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout(10, 10));
            panel.setBackground(new Color(250, 245, 255));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

            JLabel emoji = new JLabel("🏆");
            emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
            emoji.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            titleLabel.setForeground(new Color(90, 70, 120));

            JTextArea msgArea = new JTextArea(message);
            msgArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
            msgArea.setForeground(new Color(110, 90, 130));
            msgArea.setEditable(false);
            msgArea.setOpaque(false);
            msgArea.setLineWrap(true);
            msgArea.setWrapStyleWord(true);

            JPanel textPanel = new JPanel();
            textPanel.setOpaque(false);
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.add(titleLabel);
            textPanel.add(Box.createVerticalStrut(4));
            textPanel.add(msgArea);

            panel.add(emoji, BorderLayout.WEST);
            panel.add(textPanel, BorderLayout.CENTER);

            return panel;
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