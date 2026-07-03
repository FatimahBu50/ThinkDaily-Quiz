package ThinkDaily;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class AdminDashboardFrame extends javax.swing.JFrame {

    public AdminDashboardFrame() {
        initComponents();
        contentPanel.setLayout(new BorderLayout());
        showPanel(new HomePanel());
        setSize(1200, 750);
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
    }
    
    public void showPanel(JPanel panel) {
        contentPanel.removeAll();
        panel.setSize(contentPanel.getWidth(), contentPanel.getHeight());
        panel.setPreferredSize(contentPanel.getSize());
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(253, 239, 244));

        jPanel1.setBackground(new java.awt.Color(248, 201, 218));
        jPanel1.setAutoscrolls(true);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 26));
        jLabel1.setForeground(new java.awt.Color(74, 74, 74));
        jLabel1.setText("ThinkDaily");

        jButton1.setBackground(new java.awt.Color(249, 238, 242));
        jButton1.setFont(new java.awt.Font("SansSerif", 1, 14));
        jButton1.setForeground(new java.awt.Color(74, 74, 74));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ThinkDaily/icons8-dashboard-32.png")));
        jButton1.setText("Dashboard");
        jButton1.setBorder(null);
        jButton1.setOpaque(true);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(249, 238, 242));
        jButton2.setFont(new java.awt.Font("SansSerif", 1, 14));
        jButton2.setForeground(new java.awt.Color(74, 74, 74));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ThinkDaily/icons8-quiz-31.png")));
        jButton2.setText("Questions");
        jButton2.setBorder(null);
        jButton2.setMaximumSize(new java.awt.Dimension(89, 23));
        jButton2.setMinimumSize(new java.awt.Dimension(89, 23));
        jButton2.setOpaque(true);
        // Note: ActionListener for jButton2 is already added in constructor

        jButton3.setBackground(new java.awt.Color(249, 238, 242));
        jButton3.setFont(new java.awt.Font("SansSerif", 1, 14));
        jButton3.setForeground(new java.awt.Color(74, 74, 74));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ThinkDaily/icons8-admin-settings-male-32.png")));
        jButton3.setText("Management");
        jButton3.setBorder(null);
        jButton3.setMaximumSize(new java.awt.Dimension(89, 23));
        jButton3.setMinimumSize(new java.awt.Dimension(89, 23));
        jButton3.setOpaque(true);
        jButton3.setPreferredSize(new java.awt.Dimension(57, 16));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(249, 238, 242));
        jButton4.setFont(new java.awt.Font("SansSerif", 1, 14));
        jButton4.setForeground(new java.awt.Color(74, 74, 74));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ThinkDaily/icons8-leaderboard-31.png")));
        jButton4.setBorder(null);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton4.setLabel("leaderboard \nMonitor");
        jButton4.setMaximumSize(new java.awt.Dimension(89, 23));
        jButton4.setMinimumSize(new java.awt.Dimension(89, 23));
        jButton4.setOpaque(true);
        jButton4.setPreferredSize(new java.awt.Dimension(57, 16));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(249, 238, 242));
        jButton5.setFont(new java.awt.Font("SansSerif", 1, 14));
        jButton5.setForeground(new java.awt.Color(74, 74, 74));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ThinkDaily/icons8-log-out-32.png")));
        jButton5.setText("Log Out");
        jButton5.setBorder(null);
        jButton5.setOpaque(true);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.CENTER, jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(93, Short.MAX_VALUE))
        );

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1150, Short.MAX_VALUE)
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 750, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel(new Dashboard());
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        
        showPanel(new Questions());
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel(new Managment());
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel(new Leaderboard_Moniter());
    }

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Log Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            this.setVisible(false);
            new AdminLoginPage().setVisible(true); 
        }
    }
    
    // Variables declaration
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
}