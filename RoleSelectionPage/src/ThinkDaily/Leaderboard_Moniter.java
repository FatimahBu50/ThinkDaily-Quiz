package ThinkDaily;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard_Moniter extends javax.swing.JPanel {

    private Connection conn;
    private DefaultTableModel tableModel;
    
    // ================= CONSTRUCTOR =================
    public Leaderboard_Moniter() {
        initComponents();
        setSize(1000, 750);
        setPreferredSize(new Dimension (1000, 750));
        setMinimumSize(new Dimension (1000, 750));
        setMaximumSize(new Dimension (1000, 750));
        setupDatabaseConnection();
        resizeLogoIcon();
        initializeTable();
        loadLeaderboardData("Today", "All Categories");
        setupButtonActions();
    }

    // ================= SETUP DATABASE =================
    private void setupDatabaseConnection() {
        try {
            conn = DBConnection.getConnection();
            
            if (conn == null || conn.isClosed()) {
                JOptionPane.showMessageDialog(this,
                    "Cannot connect to database. Leaderboard data may not load.",
                    "Database Error",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    // ================= RESIZE LOGO =================
    private void resizeLogoIcon() {
        try {
            java.net.URL logoURL = getClass().getResource("/logo.png");
            if (logoURL != null) {
                ImageIcon originalIcon = new ImageIcon(logoURL);
                Image img = originalIcon.getImage();
                Image scaled = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                jLabel4.setIcon(new ImageIcon(scaled));
            } else {
                System.out.println("Logo not found, using text instead");
                jLabel4.setText("ThinkDaily");
                jLabel4.setFont(new Font("SansSerif", Font.BOLD, 20));
                jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
            }
        } catch (Exception e) {
            System.out.println("Error loading logo: " + e.getMessage());
        }
    }

    // ================= INITIALIZE TABLE =================
    private void initializeTable() {
        tableModel = (DefaultTableModel) jTable1.getModel();
        
        // Center / left alignment for columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        
        jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Rank
        jTable1.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);   // Username
        jTable1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Points
        jTable1.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Level
        jTable1.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Response Time
        
        // Set column widths
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);  // Rank
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(180); // Username
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);  // Points
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(60);  // Level
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(120); // Response time
        
        // Add refresh icon to button (optional)
        try {
            ImageIcon refreshIcon = new ImageIcon(getClass().getResource("/ThinkDaily/icons8-refresh-24.png"));
            if (refreshIcon != null) {
                jButton1.setIcon(refreshIcon);
            }
        } catch (Exception e) {
            System.out.println("Refresh icon not found");
        }
    }

    // ================= SETUP BUTTON ACTIONS =================
    private void setupButtonActions() {
        // Refresh button
        jButton1.addActionListener(e -> {
            String timePeriod = (String) jComboBox1.getSelectedItem();
            String category = (String) jComboBox2.getSelectedItem();
            loadLeaderboardData(timePeriod, category);
        });
        
        // Time period combo box
        jComboBox1.addActionListener(e -> {
            String timePeriod = (String) jComboBox1.getSelectedItem();
            String category = (String) jComboBox2.getSelectedItem();
            loadLeaderboardData(timePeriod, category);
        });
        
        // Category combo box
        jComboBox2.addActionListener(e -> {
            String timePeriod = (String) jComboBox1.getSelectedItem();
            String category = (String) jComboBox2.getSelectedItem();
            loadLeaderboardData(timePeriod, category);
        });
    }

    // ================= LOAD LEADERBOARD DATA =================
    private void loadLeaderboardData(String timePeriod, String category) {
        if (conn == null) {
            System.out.println("Connection is null, cannot load data");
            return;
        }
        
        tableModel.setRowCount(0);
        
        try {
            List<LeaderboardEntry> leaderboardData;
            
            if ("Today".equals(timePeriod)) {
                leaderboardData = getDailyLeaderboard(category);
            } else {
                // "All Time"
                leaderboardData = getAllTimeLeaderboard(category);
            }
            
            if (leaderboardData.isEmpty()) {
                System.out.println("No data returned from database");
                JOptionPane.showMessageDialog(this,
                    "No leaderboard data available.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            int rank = 1;
            for (LeaderboardEntry entry : leaderboardData) {
                String responseTime = entry.responseTime > 0 ? 
                    String.format("%.1f sec", entry.responseTime) : "N/A";
                
                tableModel.addRow(new Object[] {
                    rank++,
                    entry.username,
                    entry.points,
                    entry.level,
                    responseTime
                });
            }
            
        } catch (SQLException e) {
            System.out.println("SQL Error loading leaderboard: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading leaderboard data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.out.println("General Error loading leaderboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================= GET DAILY LEADERBOARD =================
    private List<LeaderboardEntry> getDailyLeaderboard(String category) throws SQLException {
        List<LeaderboardEntry> entries = new ArrayList<>();
        
        // NOTE: category parameter is not used in SQL yet.
        // If you want to filter by category later, we can edit this.
        String sql = """
    SELECT 
        a.username AS username,
        COALESCE(SUM(ur.points_awarded), 0) AS daily_points,
        (FLOOR(u.current_points_total / 100) + 1) AS level,
        COALESCE(AVG(TIMESTAMPDIFF(SECOND, ur.answered_at, NOW())), 0) AS response_time
    FROM users u
    JOIN accounts a ON u.account_id = a.account_id
    LEFT JOIN user_responses ur ON u.user_id = ur.user_id 
        AND DATE(ur.answered_at) = CURDATE()
        AND ur.is_correct = TRUE
    WHERE a.role = 'PLAYER' 
        AND a.is_active = TRUE
    GROUP BY u.user_id, a.username, u.current_points_total
    ORDER BY daily_points DESC, response_time ASC
    LIMIT 10
""";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                LeaderboardEntry entry = new LeaderboardEntry();
                entry.username = rs.getString("username");
                entry.points = rs.getInt("daily_points");
                entry.level = rs.getInt("level");
                entry.responseTime = rs.getDouble("response_time");
                entries.add(entry);
            }
            
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        
        return entries;
    }

    // ================= GET ALL-TIME LEADERBOARD =================
    private List<LeaderboardEntry> getAllTimeLeaderboard(String category) throws SQLException {
        List<LeaderboardEntry> entries = new ArrayList<>();
        
        // NOTE: category parameter is not used in SQL yet.
        String sql = """
    SELECT 
        a.username AS username,
        u.current_points_total AS points,
        (FLOOR(u.current_points_total / 100) + 1) AS level,
        COALESCE(AVG(TIMESTAMPDIFF(SECOND, ur.answered_at, NOW())), 0) AS response_time
    FROM users u
    JOIN accounts a ON u.account_id = a.account_id
    LEFT JOIN user_responses ur ON u.user_id = ur.user_id 
        AND ur.is_correct = TRUE
    WHERE a.role = 'PLAYER' 
        AND a.is_active = TRUE
    GROUP BY u.user_id, a.username, u.current_points_total
    ORDER BY u.current_points_total DESC, response_time ASC
    LIMIT 10
""";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                LeaderboardEntry entry = new LeaderboardEntry();
                entry.username = rs.getString("username");
                entry.points = rs.getInt("points");
                entry.level = rs.getInt("level");
                entry.responseTime = rs.getDouble("response_time");
                entries.add(entry);
            }
            
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        
        return entries;
    }

    // ================= LEADERBOARD ENTRY CLASS =================
    private class LeaderboardEntry {
        String username;
        int points;
        int level;
        double responseTime;
    }

    // =============== initComponents (NetBeans) ===============

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(253, 239, 244));
        setPreferredSize(new java.awt.Dimension(1150, 750));

        jLabel1.setFont(new java.awt.Font("SansSerif", Font.BOLD, 24)); 
        jLabel1.setForeground(new java.awt.Color(74, 74, 74));
        jLabel1.setText("Leaderboard Monitor");

        jButton1.setBackground(new java.awt.Color(255, 175, 204));
        jButton1.setFont(new java.awt.Font("SansSerif", Font.BOLD, 12)); 
        jButton1.setForeground(new java.awt.Color(74, 74, 74));
        jButton1.setText("Refresh Data");
        jButton1.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // ===== TIME PERIOD: ONLY TODAY + ALL TIME =====
        jComboBox1.setBackground(new java.awt.Color(255, 245, 250));
        jComboBox1.setFont(new java.awt.Font("SansSerif", Font.PLAIN, 12));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[] { "Today", "All Time" }
        ));

        // ===== CATEGORIES: SAME AS HOME PAGE / DB =====
        jComboBox2.setBackground(new java.awt.Color(255, 245, 250));
        jComboBox2.setFont(new java.awt.Font("SansSerif", Font.PLAIN, 12));
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[] { 
                "All Categories",
                "Math",
                "Science",
                "Random",
                "History",
                "Technology"
            }
        ));

        jLabel2.setFont(new java.awt.Font("SansSerif", Font.BOLD, 14)); 
        jLabel2.setForeground(new java.awt.Color(74, 74, 74));
        jLabel2.setText("Time Period:");

        jLabel3.setFont(new java.awt.Font("SansSerif", Font.BOLD, 14)); 
        jLabel3.setForeground(new java.awt.Color(74, 74, 74));
        jLabel3.setText("Category:");

        jScrollPane1.setPreferredSize(new java.awt.Dimension(440, 180));

        jTable1.setBackground(new java.awt.Color(255, 245, 250));
        jTable1.setFont(new java.awt.Font("SansSerif", Font.PLAIN, 12)); 
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Rank", "Username", "Points", "Level", "Avg. Response Time"
            }
        ) {
            Class[] types = new Class [] {
                Integer.class, String.class, Integer.class, Integer.class, String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };
            
            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
            
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        jTable1.setGridColor(new java.awt.Color(200, 180, 190));
        jTable1.setSelectionBackground(new java.awt.Color(255, 219, 229));
        jTable1.setShowGrid(true);
        jTable1.getTableHeader().setBackground(new Color(255, 245, 250));
        jTable1.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(50);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(1).setMinWidth(150);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(180);
            jTable1.getColumnModel().getColumn(2).setMinWidth(60);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(3).setMinWidth(50);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(4).setMinWidth(100);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
        }

        jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel4.setPreferredSize(new java.awt.Dimension(120, 120));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);

        // ========== HORIZONTAL GROUP ========== 
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)

                // Logo
                .addGroup(layout.createSequentialGroup()
                    .addGap(280)
                    .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)
                )

                // Title
                .addGroup(layout.createSequentialGroup()
                    .addGap(230)
                    .addComponent(jLabel1)
                    .addGap(0, 0, Short.MAX_VALUE)
                )

                // Filters labels
                .addGroup(layout.createSequentialGroup()
                    .addGap(40)
                    .addComponent(jLabel2)
                    .addGap(50)
                    .addComponent(jLabel3)
                )

                // Filters + Button
                .addGroup(layout.createSequentialGroup()
                    .addGap(40)
                    .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                    .addGap(40)
                    .addComponent(jComboBox2, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addGap(40)
                    .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                )

                // Table
                .addGroup(layout.createSequentialGroup()
                    .addGap(40)
                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 700, GroupLayout.PREFERRED_SIZE)
                )
        );

        // ========== VERTICAL GROUP ========== 
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addGap(30)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(jLabel1)
                    .addGap(30)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3))
                    .addGap(5)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(30)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(30))
        );
    }

    // =============== Variables ===============

    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
}



