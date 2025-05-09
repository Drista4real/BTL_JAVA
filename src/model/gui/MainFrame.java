package model.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import model.entity.User;
import model.entity.Role;
import model.gui.PatientRegisterPanel;

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

        showRoleSelectionPanel();
        instance = this;
    }

    public void showRoleSelectionPanel() {
        JPanel rolePanel = new JPanel(new GridBagLayout());
        rolePanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Vai trò của bạn là:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        rolePanel.add(titleLabel, gbc);

        JButton btnDoctor = new JButton("Bác sĩ");
        JButton btnPatient = new JButton("Bệnh nhân");
        styleButton(btnDoctor);
        styleButton(btnPatient);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        rolePanel.add(btnDoctor, gbc);
        gbc.gridx = 1;
        rolePanel.add(btnPatient, gbc);

        // Đặt kích thước panel nhỏ gọn
        rolePanel.setPreferredSize(new Dimension(350, 150));

        setContentPane(rolePanel);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();

        btnDoctor.addActionListener(e -> {
            setContentPane(new LoginPanel(this, Role.DOCTOR));
            setSize(1200, 700);
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        });
        btnPatient.addActionListener(e -> {
            setContentPane(new LoginPanel(this, Role.PATIENT));
            setSize(1200, 700);
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        });
    }

    private void styleButton(JButton button) {
        Color btnColor = new Color(41, 128, 185);
        button.setBackground(btnColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(btnColor.darker(), 1, true));
        button.setPreferredSize(new Dimension(120, 36));
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

        // Load icon if exists - sửa lại để xử lý an toàn
        URL iconUrl = getClass().getResource("/model/gui/icons/" + iconName);
        if (iconUrl != null) {
            try {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(img));
                button.setIconTextGap(15);
            } catch (Exception e) {
                System.out.println("Không thể tải icon: " + iconName);
            }
        } else {
            // Không tìm thấy icon, chỉ tăng thêm padding bên trái
            button.setMargin(new Insets(0, 35, 0, 0));
            System.out.println("Không tìm thấy icon: " + iconName);
        }

        // Add hover effect using MouseListener class
        button.addMouseListener(new ButtonMouseListener(button));

        return button;
    }

    private class ButtonMouseListener extends java.awt.event.MouseAdapter {
        private JButton button;

        public ButtonMouseListener(JButton button) {
            this.button = button;
        }

        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            if (button != currentButton) {
                button.setBackground(new Color(52, 152, 219));
            }
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            if (button != currentButton) {
                button.setBackground(new Color(41, 128, 185));
            }
        }
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
        if (user != null && userNameLabel != null) {
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

    // Thêm phương thức xử lý đăng nhập cho bệnh nhân
    public void handlePatientLogin(User user) {
        // Đóng frame hiện tại
        this.setVisible(false);
        this.dispose(); // Giải phóng tài nguyên của frame hiện tại
        
        // Mở frame cho bệnh nhân
        SwingUtilities.invokeLater(() -> {
            PatientMainFrame patientFrame = new PatientMainFrame(user);
            patientFrame.setVisible(true);
        });
    }
    // Thêm phương thức xử lý đăng nhập cho người dùng dựa trên vai trò
    public void handleUserLogin(User user) {
        // Đóng frame hiện tại
        this.dispose();

        // Mở giao diện tương ứng dựa trên vai trò
        if (user.getRole() == Role.DOCTOR) {
            DoctorMainFrame doctorFrame = new DoctorMainFrame(user);
            doctorFrame.setVisible(true);
        } else if (user.getRole() == Role.PATIENT) {
            PatientMainFrame patientFrame = new PatientMainFrame(user);
            patientFrame.setVisible(true);
        } else {
            // Trường hợp vai trò không xác định
            JOptionPane.showMessageDialog(null,
                    "Vai trò người dùng không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);

            // Quay lại màn hình đăng nhập
            MainFrame loginFrame = new MainFrame();
            loginFrame.setVisible(true);
        }
    }
    public void showLoginScreen() {
        if (navPanel != null) {
            navPanel.setVisible(false);
        }
        if (currentButton != null) {
            currentButton.setBackground(new Color(41, 128, 185));
            currentButton = null;
        }
        if (cardLayout != null && mainPanel != null) {
            cardLayout.show(mainPanel, "LOGIN");
        }
    }

    public void showMainContent() {
        if (navPanel != null) {
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
            
            if (cardLayout != null && mainPanel != null) {
                cardLayout.show(mainPanel, "DASHBOARD");
            }
        } else {
            // Khởi tạo thủ công nếu chưa được khởi tạo
            initMainUI();
            navPanel.setVisible(true);
            cardLayout.show(mainPanel, "DASHBOARD");
        }
    }

    // Thêm phương thức để khởi tạo giao diện chính nếu chưa được khởi tạo
    private void initMainUI() {
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        
        // Tạo các panel cần thiết
        dashboardPanel = new DashboardPanel();
        patientPanel = new PatientManagementPanel();
        searchPanel = new SearchPanel();
        filePanel = new FileManagementPanel();
        
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(patientPanel, "PATIENT");
        mainPanel.add(searchPanel, "SEARCH");
        mainPanel.add(filePanel, "FILES");
        
        // Khởi tạo navPanel
        navPanel = createNavPanel();
        
        // Cấu trúc giao diện
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(navPanel, BorderLayout.WEST);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        setContentPane(contentPane);
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
        JButton btnFiles = createNavButton("Quản lý tài liệu", "files.png");

        btnDashboard.addActionListener(e -> {
            updateButtonSelection(btnDashboard);
            cardLayout.show(mainPanel, "DASHBOARD");
        });
        
        btnPatient.addActionListener(e -> {
            updateButtonSelection(btnPatient);
            cardLayout.show(mainPanel, "PATIENT");
        });
        
        btnSearch.addActionListener(e -> {
            updateButtonSelection(btnSearch);
            cardLayout.show(mainPanel, "SEARCH");
        });
        
        btnFiles.addActionListener(e -> {
            updateButtonSelection(btnFiles);
            cardLayout.show(mainPanel, "FILES");
        });

        navPanel.add(btnDashboard);
        navPanel.add(btnPatient);
        navPanel.add(btnSearch);
        navPanel.add(btnFiles);
        
        // Thêm nút đăng xuất
        navPanel.add(Box.createVerticalGlue()); // Đẩy nút đăng xuất xuống cuối
        
        logoutButton = new JButton("Đăng xuất");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setMaximumSize(new Dimension(250, 45));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMargin(new Insets(10, 0, 10, 0));
        
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                currentUser = null;
                showRoleSelectionPanel();
            }
        });
        
        navPanel.add(logoutButton);
        navPanel.add(Box.createVerticalStrut(20)); // Thêm khoảng trống ở dưới

        return navPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}