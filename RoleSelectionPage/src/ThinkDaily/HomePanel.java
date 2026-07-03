/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ThinkDaily;

import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class HomePanel extends javax.swing.JPanel {

    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;

    public HomePanel() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 32));
        jLabel1.setForeground(new java.awt.Color(226, 115, 150));
        jLabel1.setText("Welcome Back!");
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 24));
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("System is running smoothly. All modules are synced");
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // ===================== PERFECT CENTER LAYOUT ======================
        setLayout(new GridBagLayout());  // centers child panel automatically

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER; // absolute center

        // vertical label stack
        javax.swing.JPanel centerPanel = new javax.swing.JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        jLabel1.setAlignmentX(CENTER_ALIGNMENT);
        jLabel2.setAlignmentX(CENTER_ALIGNMENT);

        centerPanel.add(jLabel1);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(jLabel2);

        add(centerPanel, gbc);
        // ==================================================================
    }
}

