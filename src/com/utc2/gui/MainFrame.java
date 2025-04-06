package com.utc2.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton currentButton;
    private static MainFrame instance;
    private DashboardPanel dashboardPanel;
    
    public MainFrame() {
        setTitle("Hệ thống quản lý bệnh nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
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
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        navPanel.setPreferredSize(new Dimension(200, 0));
        
        // Tạo các nút với icon
        JButton btnDashboard = createNavButton("Trang chủ", "dashboard.png");
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
        navPanel.add(Box.createVerticalStrut(20));
        navPanel.add(btnDashboard);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnPatient);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnSearch);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnFile);
        navPanel.add(Box.createVerticalGlue());
        
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