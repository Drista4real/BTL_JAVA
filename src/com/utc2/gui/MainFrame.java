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
import com.utc2.entity.User;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton currentButton;
    private static MainFrame instance;
    private DashboardPanel dashboardPanel;
    private User currentUser;
    private JLabel userNameLabel;
    private JLabel avatarLabel;
    private JButton logoutButton;
    private JPanel navPanel;
    private PatientManagementPanel patientPanel;
    private SearchPanel searchPanel;
    private FileManagementPanel filePanel;
    
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
        
        // Tạo các panel một lần và lưu lại
        dashboardPanel = new DashboardPanel();
        patientPanel = new PatientManagementPanel();
        searchPanel = new SearchPanel();
        filePanel = new FileManagementPanel();
        
        // Thêm các panel vào panel chính
        mainPanel.add(new LoginPanel(this), "LOGIN");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(patientPanel, "PATIENT");
        mainPanel.add(searchPanel, "SEARCH");
        mainPanel.add(filePanel, "FILE");
        
        // Tạo navPanel một lần và lưu lại
        navPanel = createNavPanel();
        
        // Tạo panel nội dung chính
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        
        // Thêm các thành phần vào frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        // Hiển thị màn hình đăng nhập ban đầu
        showLoginScreen();
        instance = this;
    }
    
    private JButton createNavButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 45));
        button.setMaximumSize(new Dimension(250, 45));
        button.setMinimumSize(new Dimension(250, 45));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 25, 0, 0));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Load icon if exists
        try {
            URL iconUrl = getClass().getResource("/com/utc2/gui/icons/" + iconName);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(img));
                button.setIconTextGap(15);
            }
        } catch (Exception e) {
            System.out.println("Không thể tải icon: " + iconName);
        }
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != currentButton) {
                    button.setBackground(new Color(52, 152, 219));
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != currentButton) {
                    button.setBackground(new Color(41, 128, 185));
                }
            }
        });
        
        return button;
    }
    
    private void updateButtonSelection(JButton selectedButton) {
        if (currentButton != null) {
            currentButton.setBackground(new Color(41, 128, 185));
        }
        
        selectedButton.setBackground(new Color(52, 152, 219));
        currentButton = selectedButton;
    }
    
    public static MainFrame getInstance() {
        return instance;
    }

    public DashboardPanel getDashboardPanel() {
        return dashboardPanel;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            userNameLabel.setText(user.getFullName());
            // Cập nhật chữ cái đầu cho avatar mặc định
            if (avatarLabel != null && avatarLabel.getIcon() == null) {
                String firstLetter = String.valueOf(user.getFullName().charAt(0)).toUpperCase();
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
                int x = (150 - fm.stringWidth(firstLetter)) / 2;
                int y = ((150 - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(firstLetter, x, y);
                
                g2d.dispose();
                avatarLabel.setIcon(new ImageIcon(image));
            }
        }
    }

    public void showLoginScreen() {
        navPanel.setVisible(false);
        if (currentButton != null) {
            currentButton.setBackground(new Color(41, 128, 185));
            currentButton = null;
        }
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showMainContent() {
        navPanel.setVisible(true);
        // Select home button by default
        Component[] components = navPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().equals("Trang chủ")) {
                    updateButtonSelection(button);
                    break;
                }
            }
        }
        cardLayout.show(mainPanel, "DASHBOARD");
    }

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(41, 128, 185));
        navPanel.setPreferredSize(new Dimension(250, getHeight()));
        
        // Avatar panel
        JPanel avatarPanel = new JPanel();
        avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
        avatarPanel.setBackground(new Color(41, 128, 185));
        avatarPanel.setMaximumSize(new Dimension(250, 200));
        avatarPanel.setPreferredSize(new Dimension(250, 200));
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create default avatar
        avatarLabel = new JLabel();
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setPreferredSize(new Dimension(120, 120));
        avatarLabel.setMaximumSize(new Dimension(120, 120));
        
        BufferedImage image = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(0, 0, 120, 120);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 60));
        FontMetrics fm = g2d.getFontMetrics();
        String letter = "P";
        g2d.drawString(letter, (120 - fm.stringWidth(letter)) / 2, ((120 - fm.getHeight()) / 2) + fm.getAscent());
        g2d.dispose();
        avatarLabel.setIcon(new ImageIcon(image));
        
        // Add padding above avatar
        avatarPanel.add(Box.createVerticalStrut(30));
        avatarPanel.add(avatarLabel);
        avatarPanel.add(Box.createVerticalStrut(15));

        // User name panel
        userNameLabel = new JLabel("pha");
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarPanel.add(userNameLabel);

        navPanel.add(avatarPanel);
        navPanel.add(Box.createVerticalStrut(20));

        // Navigation buttons
        JButton btnDashboard = createNavButton("Trang chủ", "home.png");
        JButton btnPatient = createNavButton("Quản lý bệnh nhân", "patient.png");
        JButton btnSearch = createNavButton("Tìm kiếm", "search.png");
        JButton btnFile = createNavButton("Quản lý file", "file.png");
        JButton btnLogout = createNavButton("Đăng xuất", "logout.png");

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
        
        btnLogout.addActionListener(e -> logout());

        // Add buttons with minimal spacing
        navPanel.add(btnDashboard);
        navPanel.add(Box.createVerticalStrut(1));
        navPanel.add(btnPatient);
        navPanel.add(Box.createVerticalStrut(1));
        navPanel.add(btnSearch);
        navPanel.add(Box.createVerticalStrut(1));
        navPanel.add(btnFile);
        navPanel.add(Box.createVerticalStrut(1));
        navPanel.add(btnLogout);
        
        // Add bottom spacing
        navPanel.add(Box.createVerticalGlue());

        return navPanel;
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            currentUser = null;
            showLoginScreen();
        }
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