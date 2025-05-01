package model.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.RenderingHints;

import model.entity.User;
import model.entity.UserService;

import classes.InvoiceDetailPanel;
import classes.MedicationPanel;
import classes.PrescriptionDetailPanel;
import classes.PrescriptionPanel;
import classes.UserSearchPanel;
import classes.VitalSignsPanel;
import classes.AdmissionPanel;
import classes.AppointmentPanel;
import classes.HospitalRoomPanel;
import classes.MedicalRecordPanel;
import classes.PersonalInfoPanel;

/**
 * MainFrame - cửa sổ chính của ứng dụng quản lý y tế tích hợp nhiều panel
 */
public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton currentButton;
    private static MainFrame instance;

    private User currentUser;
    private UserService userService;

    private JLabel userNameLabel;
    private JLabel avatarLabel;
    private JPanel navPanel;

    // Các panel chức năng
    private DashboardPanel dashboardPanel;
    private InvoiceDetailPanel invoiceDetailPanel;
    private MedicationPanel medicationPanel;
    private PrescriptionDetailPanel prescriptionDetailPanel;
    private PrescriptionPanel prescriptionPanel;
    private UserSearchPanel userSearchPanel;
    private VitalSignsPanel vitalSignsPanel;
    private AdmissionPanel admissionPanel;
    private AppointmentPanel appointmentPanel;
    private HospitalRoomPanel hospitalRoomPanel;
    private MedicalRecordPanel medicalRecordPanel;
    private PersonalInfoPanel personalInfoPanel;

    public MainFrame() {
        setTitle("Hệ thống quản lý bệnh nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        userService = new UserService();
        initializeSampleUsers();

        currentUser = userService.getUserByUsername("admin");
        if (currentUser == null) {
            currentUser = new User("admin", "Admin User", "admin@hospital.com", "0900000000", null, null);
        }

        JMenuBar menuBar = new JMenuBar();

        JMenu systemMenu = new JMenu("Hệ thống");
        JMenuItem loginItem = new JMenuItem("Đăng nhập");
        loginItem.addActionListener(e -> showLoginDialog());
        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        logoutItem.addActionListener(e -> performLogout());
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> System.exit(0));
        systemMenu.add(loginItem);
        systemMenu.add(logoutItem);
        systemMenu.addSeparator();
        systemMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Trợ giúp");
        JMenuItem aboutItem = new JMenuItem("Giới thiệu");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(systemMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        dashboardPanel = new DashboardPanel();
        invoiceDetailPanel = new InvoiceDetailPanel(currentUser);
        medicationPanel = new MedicationPanel(currentUser);
        prescriptionDetailPanel = new PrescriptionDetailPanel(currentUser);
        prescriptionPanel = new PrescriptionPanel(currentUser);
        userSearchPanel = new UserSearchPanel(currentUser, userService);
        vitalSignsPanel = new VitalSignsPanel(currentUser);
        admissionPanel = new AdmissionPanel(currentUser);
        appointmentPanel = new AppointmentPanel(currentUser);
        hospitalRoomPanel = new HospitalRoomPanel(currentUser);
        medicalRecordPanel = new MedicalRecordPanel(currentUser);
        personalInfoPanel = new PersonalInfoPanel(currentUser);

        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(invoiceDetailPanel, "INVOICE_DETAIL");
        mainPanel.add(medicationPanel, "MEDICATION");
        mainPanel.add(prescriptionDetailPanel, "PRESCRIPTION_DETAIL");
        mainPanel.add(prescriptionPanel, "PRESCRIPTION");
        mainPanel.add(userSearchPanel, "USER_SEARCH");
        mainPanel.add(vitalSignsPanel, "VITAL_SIGNS");
        mainPanel.add(admissionPanel, "ADMISSION");
        mainPanel.add(appointmentPanel, "APPOINTMENT");
        mainPanel.add(hospitalRoomPanel, "HOSPITAL_ROOM");
        mainPanel.add(medicalRecordPanel, "MEDICAL_RECORD");
        mainPanel.add(personalInfoPanel, "PERSONAL_INFO");

        navPanel = createNavPanel();

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        showPanel("DASHBOARD");

        instance = this;
    }

    private void initializeSampleUsers() {
        try {
            userService.addUser("admin", "admin123", "Quản Trị Viên",
                    "admin@hospital.com", "0901234567", model.entity.Role.ADMIN, "Quản trị hệ thống");
            userService.addUser("bsnam", "doctor123", "Bác Sĩ Hoàng Nam",
                    "nam@hospital.com", "0912345678", model.entity.Role.DOCTOR, "Khoa Nội");
            userService.addUser("bslinh", "doctor123", "Bác Sĩ Thanh Linh",
                    "linh@hospital.com", "0923456789", model.entity.Role.DOCTOR, "Khoa Ngoại");
            userService.addUser("bnhoa", "patient123", "Nguyễn Thị Hoa",
                    "hoa@gmail.com", "0945678901", model.entity.Role.PATIENT, "Đau đầu, sốt");
        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo dữ liệu mẫu: " + e.getMessage());
        }
    }

    private JButton createNavButton(String text, String iconName, String panelName) {
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

        try {
            URL iconUrl = getClass().getResource("/model/gui/icons/" + iconName);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(img));
                button.setIconTextGap(15);
            }
        } catch (Exception e) {
            System.out.println("Không thể tải icon: " + iconName);
        }

        button.addActionListener(e -> {
            updateButtonSelection(button);
            showPanel(panelName);
        });

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

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(41, 128, 185));
        navPanel.setPreferredSize(new Dimension(250, getHeight()));

        JPanel avatarPanel = new JPanel();
        avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
        avatarPanel.setBackground(new Color(41, 128, 185));
        avatarPanel.setMaximumSize(new Dimension(250, 200));
        avatarPanel.setPreferredSize(new Dimension(250, 200));
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        String letter = currentUser != null && currentUser.getFullName() != null && !currentUser.getFullName().isEmpty()
                ? currentUser.getFullName().substring(0, 1).toUpperCase() : "P";
        g2d.drawString(letter, (120 - fm.stringWidth(letter)) / 2, ((120 - fm.getHeight()) / 2) + fm.getAscent());
        g2d.dispose();
        avatarLabel.setIcon(new ImageIcon(image));

        avatarPanel.add(Box.createVerticalStrut(30));
        avatarPanel.add(avatarLabel);
        avatarPanel.add(Box.createVerticalStrut(15));

        userNameLabel = new JLabel(currentUser != null ? currentUser.getFullName() : "User");
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarPanel.add(userNameLabel);

        navPanel.add(avatarPanel);
        navPanel.add(Box.createVerticalStrut(20));

        navPanel.add(createNavButton("Trang chủ", "home.png", "DASHBOARD"));
        navPanel.add(createNavButton("Quản lý chi tiết hóa đơn", "invoice.png", "INVOICE_DETAIL"));
        navPanel.add(createNavButton("Quản lý thuốc", "medication.png", "MEDICATION"));
        navPanel.add(createNavButton("Quản lý chi tiết đơn thuốc", "prescription_detail.png", "PRESCRIPTION_DETAIL"));
        navPanel.add(createNavButton("Quản lý đơn thuốc", "prescription.png", "PRESCRIPTION"));
        navPanel.add(createNavButton("Tìm kiếm người dùng", "search.png", "USER_SEARCH"));
        navPanel.add(createNavButton("Dấu hiệu sinh tồn", "vitals.png", "VITAL_SIGNS"));
        navPanel.add(createNavButton("Quản lý nhập viện", "admission.png", "ADMISSION"));
        navPanel.add(createNavButton("Quản lý cuộc hẹn", "appointment.png", "APPOINTMENT"));
        navPanel.add(createNavButton("Quản lý phòng bệnh", "hospital_room.png", "HOSPITAL_ROOM"));
        navPanel.add(createNavButton("Hồ sơ bệnh án", "medical_record.png", "MEDICAL_RECORD"));
        navPanel.add(createNavButton("Thông tin cá nhân", "personal_info.png", "PERSONAL_INFO"));

        return navPanel;
    }

    private void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public static MainFrame getInstance() {
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        userNameLabel.setText(user.getFullName());
        if (avatarLabel != null) {
            BufferedImage image = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval(0, 0, 120, 120);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 60));
            FontMetrics fm = g2d.getFontMetrics();
            String letter = user != null && user.getFullName() != null && !user.getFullName().isEmpty()
                    ? user.getFullName().substring(0, 1).toUpperCase() : "P";
            g2d.drawString(letter, (120 - fm.stringWidth(letter)) / 2, ((120 - fm.getHeight()) / 2) + fm.getAscent());
            g2d.dispose();
            avatarLabel.setIcon(new ImageIcon(image));
        }
    }

    private void showLoginDialog() {
        JDialog loginDialog = new JDialog(this, "Đăng nhập", true);
        loginDialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Tên đăng nhập:"));
        JTextField usernameField = new JTextField(15);
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Mật khẩu:"));
        JPasswordField passwordField = new JPasswordField(15);
        formPanel.add(passwordField);

        loginDialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            User user = userService.authenticate(username, password);
            if (user != null) {
                currentUser = user;
                setCurrentUser(user);
                showPanel("DASHBOARD");
                navPanel.setVisible(true);
                loginDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(loginDialog,
                        "Tên đăng nhập hoặc mật khẩu không đúng",
                        "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> loginDialog.dispose());

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        loginDialog.add(buttonPanel, BorderLayout.SOUTH);

        loginDialog.pack();
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setVisible(true);
    }

    private void performLogout() {
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Đăng xuất", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            currentUser = null;
            navPanel.setVisible(false);
            showLoginDialog();
        }
    }

    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "Giới thiệu", true);
        aboutDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ Y TẾ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionLabel = new JLabel("Phiên bản 1.0");
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("Phần mềm quản lý người dùng trong hệ thống y tế");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel copyrightLabel = new JLabel("© 2024 - Bản quyền thuộc về...");
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(versionLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(copyrightLabel);

        aboutDialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> aboutDialog.dispose());
        buttonPanel.add(closeButton);

        aboutDialog.add(buttonPanel, BorderLayout.SOUTH);

        aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
            frame.navPanel.setVisible(false); // Ẩn navPanel lúc đầu, yêu cầu đăng nhập
            frame.showLoginDialog();
        });
    }
}

