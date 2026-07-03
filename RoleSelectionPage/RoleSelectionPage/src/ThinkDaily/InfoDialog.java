package ThinkDaily;

import javax.swing.*;
import java.awt.*;

public class InfoDialog extends JDialog {

    public InfoDialog(JFrame owner, String title, String content) {
        super(owner, title, true); 

        setSize(420, 320);
        setLocationRelativeTo(owner);

        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 60, 70));

        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(10, 12, 10, 12));
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textArea.setForeground(new Color(80, 70, 80));
        textArea.setBackground(Color.WHITE);

        // Cute pastel scroll pane 💗
JScrollPane scrollPane = new JScrollPane(textArea);
scrollPane.setOpaque(false);
scrollPane.getViewport().setOpaque(false);
scrollPane.setBorder(BorderFactory.createEmptyBorder()); // remove sharp border

// Pink rounded edge around content
scrollPane.setViewportBorder(BorderFactory.createLineBorder(new Color(240, 200, 215), 2));

// Custom pink scrollbar (zipper)
scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = new Color(255, 170, 195);  // pink slider
        this.trackColor = new Color(253, 240, 246);  // soft background
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton(); // remove arrows
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton(); // remove arrows
    }

    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        btn.setVisible(false);
        return btn;
    }
});

// thinner cute scrollbar
scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, Integer.MAX_VALUE));
scrollPane.getVerticalScrollBar().setUnitIncrement(15); // smoother scroll

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(255, 210, 220));
        closeBtn.setForeground(new Color(70, 60, 60));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);
        bottom.add(closeBtn);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    // Helper static method
    public static void show(JFrame owner, String title, String content) {
        InfoDialog dialog = new InfoDialog(owner, title, content);
        dialog.setVisible(true);
    }
}
