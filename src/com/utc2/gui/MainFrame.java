package com.utc2.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.RenderingHints;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton currentButton;
    private static MainFrame instance;
    private DashboardPanel dashboardPanel;
    
    public MainFrame() {
        setTitle("Hệ thống quản lý bệnh nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Tạo thanh menu
        JMenuBar menuBar = new JMenuBar();
        
        // Menu File
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Menu Trợ giúp
        JMenu helpMenu = new JMenu("Trợ giúp");
        JMenuItem aboutItem = new JMenuItem("Giới thiệu");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this, 
            "Hệ thống quản lý bệnh nhân\nVersion 1.0", 
            "Giới thiệu", 
            JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
        
        // Tạo panel chính với CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Tạo panel điều hướng với kiểu dáng tốt hơn
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(240, 240, 240));
        navPanel.setPreferredSize(new Dimension(250, getHeight()));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Thêm avatar
        JPanel avatarPanel = new JPanel();
        avatarPanel.setLayout(new BorderLayout());
        avatarPanel.setBackground(new Color(240, 240, 240));
        avatarPanel.setMaximumSize(new Dimension(200, 200));
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel avatarLabel = new JLabel();
        try {
            ImageIcon originalIcon = new ImageIcon("src/com/utc2/images/z6478241104532_86b43c55064d8b1bb9341aef36c66830.jpg");
            Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            
            // Tạo ảnh tròn
            BufferedImage circularImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = circularImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Vẽ hình tròn
            g2d.setColor(Color.WHITE);
            g2d.fillOval(0, 0, 150, 150);
            
            // Cắt ảnh thành hình tròn
            g2d.setClip(new Ellipse2D.Float(0, 0, 150, 150));
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();
            
            avatarLabel.setIcon(new ImageIcon(circularImage));
        } catch (Exception e) {
            // Tạo avatar mặc định hình tròn
            BufferedImage image = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Vẽ nền tròn
            g2d.setColor(new Color(200, 200, 200));
            g2d.fillOval(0, 0, 150, 150);
            
            // Vẽ chữ cái
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 60));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (150 - fm.stringWidth("A")) / 2;
            int y = ((150 - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString("A", x, y);
            
            g2d.dispose();
            avatarLabel.setIcon(new ImageIcon(image));
        }
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        
        // Thêm tên người dùng
        JLabel userNameLabel = new JLabel("Admin", SwingConstants.CENTER);
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        navPanel.add(avatarPanel);
        navPanel.add(userNameLabel);
        navPanel.add(Box.createVerticalStrut(20));
        
        // Tạo các nút với icon
        JButton btnDashboard = createNavButton("Trang chủ", "home.png");
        JButton btnPatient = createNavButton("Quản lý bệnh nhân", "patient.png");
        JButton btnSearch = createNavButton("Tìm kiếm", "search.png");
        JButton btnFile = createNavButton("Quản lý file", "file.png");
        
        // Thêm các sự kiện
        btnDashboard.addActionListener(e -> {
            cardLayout.show(mainPanel, "DASHBOARD");
            updateButtonSelection(btnDashboard);
        });
        btnPatient.addActionListener(e -> {
            cardLayout.show(mainPanel, "PATIENT");
            updateButtonSelection(btnPatient);
        });
        btnSearch.addActionListener(e -> {
            cardLayout.show(mainPanel, "SEARCH");
            updateButtonSelection(btnSearch);
        });
        btnFile.addActionListener(e -> {
            cardLayout.show(mainPanel, "FILE");
            updateButtonSelection(btnFile);
        });
        
        // Thêm các nút vào panel điều hướng
        navPanel.add(btnDashboard);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnPatient);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnSearch);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnFile);
        
        // Thêm các panel vào panel chính
        mainPanel.add(new DashboardPanel(), "DASHBOARD");
        mainPanel.add(new PatientManagementPanel(), "PATIENT");
        mainPanel.add(new SearchPanel(), "SEARCH");
        mainPanel.add(new FileManagementPanel(), "FILE");
        
        // Tạo panel nội dung chính
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        
        // Thêm các thành phần vào frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        // Thiết lập nút được chọn ban đầu
        updateButtonSelection(btnDashboard);
        instance = this;
        dashboardPanel = new DashboardPanel();
    }
    
    private JButton createNavButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 50));
        button.setPreferredSize(new Dimension(180, 50));
        button.setBackground(new Color(240, 240, 240));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
        
        // Thử tải icon
        try {
            URL iconUrl = getClass().getResource("/com/utc2/gui/icons/" + iconName);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                button.setIcon(icon);
            }
        } catch (Exception e) {
            System.out.println("Không thể tải icon: " + iconName);
        }
        
        // Thêm hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != currentButton) {
                    button.setBackground(new Color(220, 220, 220));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != currentButton) {
                    button.setBackground(new Color(240, 240, 240));
                }
            }
        });
        
        return button;
    }
    
    private void updateButtonSelection(JButton selectedButton) {
        if (currentButton != null) {
            currentButton.setBackground(new Color(240, 240, 240));
            currentButton.setForeground(Color.BLACK);
        }
        selectedButton.setBackground(new Color(0, 120, 215));
        selectedButton.setForeground(Color.WHITE);
        currentButton = selectedButton;
    }
    
    public static MainFrame getInstance() {
        return instance;
    }

    public DashboardPanel getDashboardPanel() {
        return dashboardPanel;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
} 