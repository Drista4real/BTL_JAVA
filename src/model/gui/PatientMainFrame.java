package model.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import model.entity.User;
import model.entity.Appointment;
import model.entity.MedicalRecord;
import model.entity.Prescription;
import java.util.ArrayList;
import java.util.List;

public class PatientMainFrame extends JFrame {
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Thêm các panel chức năng
    private JPanel dashboardPanel;
    private JPanel appointmentPanel;
    private JPanel medicalHistoryPanel;
    private JPanel prescriptionPanel;
    private JPanel personalInfoPanel;
    private JPanel paymentsPanel;
    
    // Thêm các button chức năng
    private JButton homeButton;
    private JButton appointmentButton;
    private JButton medicalHistoryButton;
    private JButton prescriptionButton;
    private JButton personalInfoButton;
    private JButton paymentsButton;
    private JButton logoutButton;
    
    public PatientMainFrame(User user) {
        this.currentUser = user;
        
        setTitle("Hệ thống quản lý bệnh nhân - Giao diện bệnh nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        // Initialize the main panel with card layout
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        
        // Create all panels
        createPanels();
        
        // Add panels to card layout
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(appointmentPanel, "APPOINTMENTS");
        mainPanel.add(medicalHistoryPanel, "MEDICAL_HISTORY");
        mainPanel.add(prescriptionPanel, "PRESCRIPTIONS");
        mainPanel.add(personalInfoPanel, "PERSONAL_INFO");
        mainPanel.add(paymentsPanel, "PAYMENTS");
        
        // Create navigation panel
        JPanel navPanel = createNavigationPanel();
        
        // Set up the main layout
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(navPanel, BorderLayout.WEST);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        setContentPane(contentPane);
    }
    
    private void createPanels() {
        // Tạo các panel chức năng
        dashboardPanel = createPatientDashboard();
        appointmentPanel = createAppointmentPanel();
        medicalHistoryPanel = createMedicalHistoryPanel();
        prescriptionPanel = createPrescriptionPanel();
        personalInfoPanel = createPersonalInfoPanel();
        paymentsPanel = createPaymentsPanel();
    }
    
    private JPanel createPatientDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Welcome header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(panel.getWidth(), 100));
        
        JLabel welcomeLabel = new JLabel("Chào mừng, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel);
        
        // Dashboard content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        JLabel infoLabel = new JLabel("Thông tin tổng quan");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(infoLabel);
        
        // Thêm các widget thông tin
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setMaximumSize(new Dimension(800, 200));
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        statsPanel.add(createInfoCard("Lịch hẹn sắp tới", "2", new Color(52, 152, 219)));
        statsPanel.add(createInfoCard("Đơn thuốc hiện tại", "1", new Color(46, 204, 113)));
        statsPanel.add(createInfoCard("Kết quả xét nghiệm mới", "3", new Color(155, 89, 182)));
        statsPanel.add(createInfoCard("Hóa đơn chưa thanh toán", "0", new Color(230, 126, 34)));
        
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(statsPanel);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel headerPanel = createSectionHeader("Quản lý lịch hẹn khám bệnh");
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tạo bảng lịch hẹn
        String[] columns = {"Mã lịch hẹn", "Ngày", "Giờ", "Bác sĩ", "Lý do khám", "Trạng thái"};
        Object[][] data = {
            {"AP001", "30/04/2025", "09:00", "Nguyễn Văn An", "Khám định kỳ", "Đã xác nhận"},
            {"AP003", "30/04/2025", "11:00", "Lê Văn Cường", "Khám tim mạch", "Đã xác nhận"}
        };
        
        JTable appointmentsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        
        // Panel nút điều khiển
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton newAppointmentBtn = new JButton("Đặt lịch hẹn mới");
        JButton cancelAppointmentBtn = new JButton("Hủy lịch hẹn");
        
        buttonPanel.add(newAppointmentBtn);
        buttonPanel.add(cancelAppointmentBtn);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMedicalHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel headerPanel = createSectionHeader("Lịch sử khám bệnh");
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tạo bảng lịch sử khám bệnh
        String[] columns = {"Mã hồ sơ", "Ngày khám", "Bác sĩ", "Chẩn đoán", "Điều trị"};
        Object[][] data = {
            {"MR001", "03/04/2025", "Nguyễn Văn An", "Tăng huyết áp", "Thuốc hạ áp"}
        };
        
        JTable medicalRecordsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(medicalRecordsTable);
        
        // Panel hiển thị chi tiết
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết khám bệnh"));
        
        JTextArea detailsArea = new JTextArea(10, 30);
        detailsArea.setEditable(false);
        detailsArea.setText("Chọn một hồ sơ từ bảng để xem chi tiết");
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        
        detailsPanel.add(detailsScrollPane);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(detailsPanel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPrescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel headerPanel = createSectionHeader("Đơn thuốc và kê toa");
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tạo bảng đơn thuốc
        String[] columns = {"Mã đơn thuốc", "Ngày kê", "Bác sĩ", "Số loại thuốc", "Trạng thái"};
        Object[][] data = {
            {"PRE-001", "03/04/2025", "Nguyễn Văn An", "3", "Đang xử lý"}
        };
        
        JTable prescriptionsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(prescriptionsTable);
        
        // Panel chi tiết đơn thuốc
        JPanel prescriptionDetailsPanel = new JPanel(new BorderLayout());
        prescriptionDetailsPanel.setBackground(Color.WHITE);
        prescriptionDetailsPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn thuốc"));
        
        String[] detailColumns = {"Tên thuốc", "Liều lượng", "Hướng dẫn", "Số lượng", "Giá"};
        Object[][] detailData = {};
        
        JTable detailsTable = new JTable(detailData, detailColumns);
        JScrollPane detailsScrollPane = new JScrollPane(detailsTable);
        
        prescriptionDetailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(scrollPane, BorderLayout.NORTH);
        contentPanel.add(prescriptionDetailsPanel, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel headerPanel = createSectionHeader("Thông tin cá nhân");
        
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Thông tin cá nhân
        addFormField(contentPanel, gbc, "Họ và tên:", new JLabel(currentUser.getFullName()));
        gbc.gridy++;
        addFormField(contentPanel, gbc, "Ngày sinh:", new JLabel(currentUser.getDateOfBirth()));
        gbc.gridy++;
        addFormField(contentPanel, gbc, "Giới tính:", new JLabel(currentUser.getGender()));
        gbc.gridy++;
        addFormField(contentPanel, gbc, "Địa chỉ:", new JLabel(currentUser.getAddress()));
        gbc.gridy++;
        addFormField(contentPanel, gbc, "Số điện thoại:", new JLabel(currentUser.getPhone()));
        gbc.gridy++;
        addFormField(contentPanel, gbc, "Email:", new JLabel(currentUser.getEmail()));
        gbc.gridy++;
        
        // Thông tin bảo hiểm
        gbc.gridy++;
        JLabel insuranceHeaderLabel = new JLabel("Thông tin bảo hiểm y tế");
        insuranceHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contentPanel.add(insuranceHeaderLabel, gbc);
        
        gbc.gridy++;
        String hasInsurance = currentUser.isHasInsurance() ? "Có" : "Không";
        addFormField(contentPanel, gbc, "Có BHYT:", new JLabel(hasInsurance));
        
        if (currentUser.isHasInsurance()) {
            gbc.gridy++;
            addFormField(contentPanel, gbc, "Mã BHYT:", new JLabel(currentUser.getInsuranceId()));
            gbc.gridy++;
            addFormField(contentPanel, gbc, "Ngày hết hạn:", new JLabel(currentUser.getInsuranceExpDate()));
        }
        
        // Buttons
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JButton editInfoButton = new JButton("Chỉnh sửa thông tin");
        contentPanel.add(editInfoButton, gbc);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel headerPanel = createSectionHeader("Quản lý thanh toán và hóa đơn");
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tạo bảng hóa đơn
        String[] columns = {"Mã hóa đơn", "Ngày tạo", "Loại", "Số tiền", "Đã thanh toán", "Còn lại", "Trạng thái"};
        Object[][] data = {};
        
        JTable invoicesTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(invoicesTable);
        
        // Panel nút điều khiển
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton viewDetailsBtn = new JButton("Xem chi tiết");
        JButton payInvoiceBtn = new JButton("Thanh toán");
        
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(payInvoiceBtn);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSectionHeader(String title) {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        return headerPanel;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }
    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(41, 128, 185));
        navPanel.setPreferredSize(new Dimension(250, getHeight()));
        
        // Add avatar and user information
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(new Color(41, 128, 185));
        userPanel.setMaximumSize(new Dimension(250, 200));
        userPanel.setPreferredSize(new Dimension(250, 200));
        userPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Tạo avatar (hình tròn với chữ cái đầu)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = Math.min(getWidth(), getHeight()) - 10;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillOval(x, y, size, size);
                
                g2d.setColor(new Color(41, 128, 185));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
                
                String firstLetter = String.valueOf(currentUser.getFullName().charAt(0)).toUpperCase();
                FontMetrics metrics = g2d.getFontMetrics();
                int letterX = x + (size - metrics.stringWidth(firstLetter)) / 2;
                int letterY = y + ((size - metrics.getHeight()) / 2) + metrics.getAscent();
                
                g2d.drawString(firstLetter, letterX, letterY);
                g2d.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(80, 80));
        avatarPanel.setMaximumSize(new Dimension(80, 80));
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel userLabel = new JLabel(currentUser.getFullName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel roleLabel = new JLabel("Bệnh nhân");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        userPanel.add(Box.createVerticalStrut(20));
        userPanel.add(avatarPanel);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(userLabel);
        userPanel.add(Box.createVerticalStrut(5));
        userPanel.add(roleLabel);
        
        // Navigation buttons
        homeButton = createNavButton("Trang chủ", e -> cardLayout.show(mainPanel, "DASHBOARD"));
        appointmentButton = createNavButton("Lịch hẹn khám bệnh", e -> cardLayout.show(mainPanel, "APPOINTMENTS"));
        medicalHistoryButton = createNavButton("Lịch sử khám bệnh", e -> cardLayout.show(mainPanel, "MEDICAL_HISTORY"));
        prescriptionButton = createNavButton("Đơn thuốc", e -> cardLayout.show(mainPanel, "PRESCRIPTIONS"));
        personalInfoButton = createNavButton("Thông tin cá nhân", e -> cardLayout.show(mainPanel, "PERSONAL_INFO"));
        paymentsButton = createNavButton("Thanh toán & Hóa đơn", e -> cardLayout.show(mainPanel, "PAYMENTS"));
        
        logoutButton = createNavButton("Đăng xuất", e -> {
            int option = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
            );
            
            if (option == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                });
            }
        });
        
        logoutButton.setBackground(new Color(231, 76, 60));
        
        navPanel.add(userPanel);
        navPanel.add(Box.createVerticalStrut(20));
        navPanel.add(homeButton);
        navPanel.add(appointmentButton);
        navPanel.add(medicalHistoryButton);
        navPanel.add(prescriptionButton);
        navPanel.add(personalInfoButton);
        navPanel.add(paymentsButton);
        navPanel.add(Box.createVerticalGlue());
        navPanel.add(logoutButton);
        navPanel.add(Box.createVerticalStrut(20));
        
        return navPanel;
    }
    
    private JButton createNavButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 45));
        button.setMaximumSize(new Dimension(250, 45));
        button.setMinimumSize(new Dimension(250, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMargin(new Insets(0, 25, 0, 0));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != logoutButton) {
                    button.setBackground(new Color(52, 152, 219));
                } else {
                    button.setBackground(new Color(231, 76, 60).brighter());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != logoutButton) {
                    button.setBackground(new Color(41, 128, 185));
                } else {
                    button.setBackground(new Color(231, 76, 60));
                }
            }
        });
        
        if (listener != null) {
            button.addActionListener(listener);
        }
        
        return button;
    }
}