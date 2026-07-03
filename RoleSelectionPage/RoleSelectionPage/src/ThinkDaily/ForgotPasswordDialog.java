//package ThinkDaily;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
//public class ForgotPasswordDialog extends JDialog {
//
//    public ForgotPasswordDialog(JFrame parent) {
//        super(parent, "Recover Password", true); // modal dialog
//        setSize(450, 420);
//        setLocationRelativeTo(parent);
//        setResizable(false);
//
//        //  MAIN BACKGROUND
//        JPanel mainPanel = new RoundedPanel(25);
//        mainPanel.setOpaque(false);
//        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
//        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//
//        // TITLE
//        JLabel title = new JLabel("Recover Your Password", SwingConstants.CENTER);
//        title.setFont(new Font("SansSerif", Font.BOLD, 20));
//        title.setForeground(new Color(80, 60, 90));
//        title.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JLabel subtitle = new JLabel("Answer the security question to reset your password.", SwingConstants.CENTER);
//        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
//        subtitle.setForeground(new Color(140, 125, 150));
//        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        // FIELDS
//        // Username
//        JTextField usernameField = new JTextField();
//        usernameField.setMaximumSize(new Dimension(300, 40));
//        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 13));
//        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
//
//        // Security question (demo dropdown for now)
//        String[] questions = {
//                "Select a question",
//                "What is your favorite color?",
//                "What is your pet's name?",
//                "What city were you born in?",
//                "What is your favorite food?",
//                "What is the name of your best friend?"
//        };
//
//        JComboBox<String> questionCombo = new JComboBox<>(questions);
//        questionCombo.setMaximumSize(new Dimension(300, 48));
//        questionCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
//        questionCombo.setBorder(BorderFactory.createTitledBorder("Security Question"));
//
//        // Answer field
//        JTextField answerField = new JTextField();
//        answerField.setMaximumSize(new Dimension(300, 40));
//        answerField.setFont(new Font("SansSerif", Font.PLAIN, 13));
//        answerField.setBorder(BorderFactory.createTitledBorder("Your Answer"));
//
//        // BUTTONS
//        JPanel buttonPanel = new JPanel(new FlowLayout());
//        buttonPanel.setOpaque(false);
//
//        JButton submitBtn = new JButton("Submit");
//        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
//        submitBtn.setBackground(new Color(255, 204, 224));
//        submitBtn.setForeground(new Color(80, 60, 70));
//        submitBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
//        submitBtn.setFocusPainted(false);
//        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//
//        Color normal = new Color(255, 204, 224);
//        Color hover = new Color(255, 185, 212);
//
//        submitBtn.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                submitBtn.setBackground(hover);
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                submitBtn.setBackground(normal);
//            }
//        });
//
//        // ============= Submit logic (UI only, no DB) =============
//        submitBtn.addActionListener(e -> {
//            String username = usernameField.getText().trim();
//            String question = (String) questionCombo.getSelectedItem();
//            String answer   = answerField.getText().trim();
//
//            // Basic validation
//            if (username.isEmpty()) {
//                JOptionPane.showMessageDialog(
//                        ForgotPasswordDialog.this,
//                        "Please enter your username",
//                        "Missing username",
//                        JOptionPane.WARNING_MESSAGE
//                );
//                return;
//            }
//
//            if (questionCombo.getSelectedIndex() == 0) {
//                JOptionPane.showMessageDialog(
//                        ForgotPasswordDialog.this,
//                        "Please select a security question",
//                        "No question selected",
//                        JOptionPane.WARNING_MESSAGE
//                );
//                return;
//            }
//
//            if (answer.isEmpty()) {
//                JOptionPane.showMessageDialog(
//                        ForgotPasswordDialog.this,
//                        "Please type your answer to the security question ",
//                        "Missing answer",
//                        JOptionPane.WARNING_MESSAGE
//                );
//                return;
//            }
//
//            // For now: just a placeholder, no DB yet.
//            JOptionPane.showMessageDialog(
//                    ForgotPasswordDialog.this,
//                    "Your information has been submitted.\n" +
//                    "Password recovery will be enabled once the database is connected ",
//                    "Feature not active yet",
//                    JOptionPane.INFORMATION_MESSAGE
//            );
//
//            // Close dialog after "success"
//            ForgotPasswordDialog.this.dispose();
//        });
//
//        JButton cancelBtn = new JButton("Cancel");
//        cancelBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
//        cancelBtn.setContentAreaFilled(false);
//        cancelBtn.setBorderPainted(false);
//        cancelBtn.setForeground(new Color(150, 120, 160));
//        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//
//        // Just close this dialog; login window is already there
//        cancelBtn.addActionListener(e -> {
//             new PlayerLoginPage().setVisible(true);
//            dispose();
//        });
//
//
//        buttonPanel.add(submitBtn);
//        buttonPanel.add(cancelBtn);
//
//        // ADD COMPONENTS
//        mainPanel.add(title);
//        mainPanel.add(Box.createVerticalStrut(5));
//        mainPanel.add(subtitle);
//        mainPanel.add(Box.createVerticalStrut(20));
//        mainPanel.add(usernameField);
//        mainPanel.add(Box.createVerticalStrut(12));
//        mainPanel.add(questionCombo);
//        mainPanel.add(Box.createVerticalStrut(12));
//        mainPanel.add(answerField);
//        mainPanel.add(Box.createVerticalStrut(25));
//        mainPanel.add(buttonPanel);
//
//        // Wrap in background panel
//        JPanel wrapper = new JPanel() {
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                Graphics2D g2 = (Graphics2D) g;
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                g2.setColor(new Color(255, 240, 247));
//                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
//            }
//        };
//        wrapper.setLayout(new GridBagLayout());
//        wrapper.add(mainPanel);
//
//        setContentPane(wrapper);
//    }
//
//    // Rounded Card Panel
//    private static class RoundedPanel extends JPanel {
//        private final int radius;
//        public RoundedPanel(int radius) {
//            this.radius = radius;
//            setOpaque(false);
//        }
//        @Override
//        protected void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            Graphics2D g2 = (Graphics2D) g.create();
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2.setColor(new Color(252, 249, 254));
//            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
//            g2.setColor(new Color(245, 220, 230));
//            g2.drawRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
//            g2.dispose();
//        }
//    }
//}
package ThinkDaily;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ForgotPasswordDialog extends JDialog {

    public ForgotPasswordDialog(JFrame parent) {
        super(parent, "Recover Password", true); // modal dialog
        setSize(450, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        //  MAIN BACKGROUND
        JPanel mainPanel = new RoundedPanel(25);
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // TITLE
        JLabel title = new JLabel("Recover Your Password", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(80, 60, 90));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Answer the security question to reset your password.", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(140, 125, 150));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // FIELDS
        // Username
        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(300, 40));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        // Security question (demo dropdown for now)
        String[] questions = {
                "Select a question",
                "What is your favorite color?",
                "What is your pet's name?",
                "What city were you born in?",
                "What is your favorite food?",
                "What is the name of your best friend?"
        };

        JComboBox<String> questionCombo = new JComboBox<>(questions);
        questionCombo.setMaximumSize(new Dimension(300, 48));
        questionCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        questionCombo.setBorder(BorderFactory.createTitledBorder("Security Question"));

        // Answer field
        JTextField answerField = new JTextField();
        answerField.setMaximumSize(new Dimension(300, 40));
        answerField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        answerField.setBorder(BorderFactory.createTitledBorder("Your Answer"));

        // BUTTONS
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        JButton submitBtn = new JButton("Submit");
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitBtn.setBackground(new Color(255, 204, 224));
        submitBtn.setForeground(new Color(80, 60, 70));
        submitBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color normal = new Color(255, 204, 224);
        Color hover = new Color(255, 185, 212);

        submitBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                submitBtn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                submitBtn.setBackground(normal);
            }
        });

        // ============= Submit logic (UI only, no DB) =============
        submitBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String question = (String) questionCombo.getSelectedItem();
            String answer   = answerField.getText().trim();

            // Basic validation
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(
                        ForgotPasswordDialog.this,
                        "Please enter your username",
                        "Missing username",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (questionCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(
                        ForgotPasswordDialog.this,
                        "Please select a security question",
                        "No question selected",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (answer.isEmpty()) {
                JOptionPane.showMessageDialog(
                        ForgotPasswordDialog.this,
                        "Please type your answer to the security question ",
                        "Missing answer",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // For now: just a placeholder, no DB yet.
            JOptionPane.showMessageDialog(
                    ForgotPasswordDialog.this,
                    "Your information has been submitted.\n" +
                    "Password recovery will be enabled once the database is connected ",
                    "Feature not active yet",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Close dialog after "success"
            ForgotPasswordDialog.this.dispose();
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cancelBtn.setContentAreaFilled(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setForeground(new Color(150, 120, 160));
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Just close this dialog; login window is already there
        cancelBtn.addActionListener(e -> {
             new PlayerLoginPage().setVisible(true);
            dispose();
        });


        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        // ADD COMPONENTS
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(questionCombo);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(answerField);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(buttonPanel);

        // Wrap in background panel
        JPanel wrapper = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 240, 247));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        wrapper.setLayout(new GridBagLayout());
        wrapper.add(mainPanel);

        setContentPane(wrapper);
    }

    // Rounded Card Panel
    private static class RoundedPanel extends JPanel {
        private final int radius;
        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(252, 249, 254));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(245, 220, 230));
            g2.drawRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }
}
