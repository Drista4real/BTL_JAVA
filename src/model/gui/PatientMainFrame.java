package model.gui;

import javax.swing.*;
import java.awt.*;
import model.entity.User;
import java.awt.image.BufferedImage;
import javax.swing.table.DefaultTableModel;
import model.utils.ExceptionUtils;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import model.entity.Role;

public class PatientMainFrame extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Pha2k5@";

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton currentButton;
    private User currentUser;
    private JLabel userNameLabel;
    private JLabel avatarLabel;
    private JPanel navPanel;

    // Thêm các panel để tham chiếu trực tiếp
    private PersonalInfoPanel personalInfoPanel;
    private MedicalRecordPanel medicalRecordPanel;
    private PrescriptionPanel prescriptionPanel;
    private PaymentPanel paymentPanel;
    private PatientAppointmentPanel appointmentPanel;

    public PatientMainFrame(User user) {
        setTitle("Hệ thống quản lý bệnh nhân - Bệnh nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        this.currentUser = user;

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Khởi tạo các panel
        personalInfoPanel = new PersonalInfoPanel(currentUser);

        // Gọi loadUserData trước để cập nhật thông tin user, đặc biệt là patientId
        loadUserData();

        // Sau đó mới khởi tạo các panel cần patientId
        medicalRecordPanel = new MedicalRecordPanel(currentUser);
        prescriptionPanel = new PrescriptionPanel(currentUser);
        paymentPanel = new PaymentPanel(currentUser);
        appointmentPanel = new PatientAppointmentPanel(currentUser.getUsername());

        mainPanel.add(personalInfoPanel, "PERSONAL_INFO");
        mainPanel.add(medicalRecordPanel, "MEDICAL_RECORD");
        mainPanel.add(prescriptionPanel, "PRESCRIPTION");
        mainPanel.add(paymentPanel, "PAYMENT");
        mainPanel.add(appointmentPanel, "APPOINTMENT");

        navPanel = createNavPanel();
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // Hiển thị tab mặc định
        showPersonalInfo();
    }

    /**
     * Tải dữ liệu người dùng từ cơ sở dữ liệu
     */
    private void loadUserData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Lấy thông tin PatientID từ cơ sở dữ liệu
            String sql = "SELECT p.* FROM Patients p " +
                    "JOIN UserAccounts u ON p.UserID = u.UserID " +
                    "WHERE u.UserName = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, currentUser.getUsername());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String patientId = rs.getString("PatientID");
                        currentUser.setPatientId(patientId);

                        // Cập nhật thông tin bổ sung
                        currentUser.setDateOfBirth(rs.getString("DateOfBirth"));
                        currentUser.setGender(rs.getString("Gender"));
                        currentUser.setAddress(rs.getString("Address"));

                        // Cập nhật thông tin BHYT từ bảng Insurance
                        checkInsuranceStatus(conn, patientId);
                    }
                }
            }

            // Cập nhật giao diện nếu panel đã được khởi tạo
            if (personalInfoPanel != null) {
                personalInfoPanel.updateInfo(currentUser);
            }

        } catch (SQLException e) {
            ExceptionUtils.handleSQLException(mainPanel, e);
        }
    }

    /**
     * Kiểm tra và cập nhật thông tin BHYT
     */
    private void checkInsuranceStatus(Connection conn, String patientId) throws SQLException {
        String sql = "SELECT * FROM Insurance WHERE PatientID = ? AND Status = 'Hoat Dong'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    currentUser.setHasInsurance(true);
                    currentUser.setInsuranceId(rs.getString("InsuranceID"));
                    currentUser.setInsuranceExpDate(rs.getString("ExpirationDate"));
                } else {
                    currentUser.setHasInsurance(false);
                }
            }
        }
    }

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(180, 205, 230)); // Màu xanh nhạt
        navPanel.setPreferredSize(new Dimension(250, getHeight()));

        // Avatar panel
        JPanel avatarPanel = new JPanel();
        avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
        avatarPanel.setBackground(new Color(180, 205, 230));
        avatarPanel.setMaximumSize(new Dimension(250, 200));
        avatarPanel.setPreferredSize(new Dimension(250, 200));
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        avatarLabel = new JLabel();
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setPreferredSize(new Dimension(120, 120));
        avatarLabel.setMaximumSize(new Dimension(120, 120));

        // Tạo avatar mặc định với chữ cái đầu
        String firstLetter = "P";
        if (currentUser != null && currentUser.getFullName() != null && !currentUser.getFullName().isEmpty()) {
            firstLetter = currentUser.getFullName().substring(0, 1).toUpperCase();
        }

        BufferedImage image = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(0, 0, 120, 120);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 60));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(firstLetter, (120 - fm.stringWidth(firstLetter)) / 2, ((120 - fm.getHeight()) / 2) + fm.getAscent());
        g2d.dispose();

        avatarLabel.setIcon(new ImageIcon(image));
        avatarPanel.add(Box.createVerticalStrut(30));
        avatarPanel.add(avatarLabel);
        avatarPanel.add(Box.createVerticalStrut(15));

        userNameLabel = new JLabel(currentUser != null ? currentUser.getFullName() : "");
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userNameLabel.setForeground(new Color(41, 128, 185));
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarPanel.add(userNameLabel);
        navPanel.add(avatarPanel);
        navPanel.add(Box.createVerticalStrut(20));

        // Các nút chức năng
        JButton btnPersonalInfo = createNavButton("Xem thông tin cá nhân");
        JButton btnMedicalRecord = createNavButton("Xem bệnh án");
        JButton btnPrescription = createNavButton("Xem đơn thuốc");
        JButton btnPayment = createNavButton("Thanh toán");
        JButton btnAppointment = createNavButton("Đặt lịch khám");
        JButton btnLogout = createNavButton("Đăng xuất");

        btnPersonalInfo.addActionListener(e -> {
            showPersonalInfo();
            updateButtonSelection(btnPersonalInfo);
        });
        btnMedicalRecord.addActionListener(e -> {
            showMedicalRecord();
            updateButtonSelection(btnMedicalRecord);
        });
        btnPrescription.addActionListener(e -> {
            showPrescription();
            updateButtonSelection(btnPrescription);
        });
        btnPayment.addActionListener(e -> {
            showPayment();
            updateButtonSelection(btnPayment);
        });
        btnAppointment.addActionListener(e -> {
            showAppointment();
            updateButtonSelection(btnAppointment);
        });
        btnLogout.addActionListener(e -> logout());

        navPanel.add(btnPersonalInfo);
        navPanel.add(Box.createVerticalStrut(1));
        navPanel.add(btnMedicalRecord);
        navPanel.add(Box.createVerticalStrut(1));
        navPanel.add(btnPrescription);
        navPanel.add(Box.createVerticalStrut(1));
        navPanel.add(btnPayment);
        navPanel.add(Box.createVerticalStrut(1));
        navPanel.add(btnAppointment);
        navPanel.add(Box.createVerticalStrut(1));
        navPanel.add(btnLogout);
        navPanel.add(Box.createVerticalGlue());

        // Đặt nút thông tin cá nhân làm nút mặc định được chọn
        currentButton = btnPersonalInfo;
        btnPersonalInfo.setBackground(new Color(200, 220, 240));

        return navPanel;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 45));
        button.setMaximumSize(new Dimension(250, 45));
        button.setMinimumSize(new Dimension(250, 45));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(new Color(41, 128, 185));
        button.setBackground(new Color(180, 205, 230));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 25, 0, 0));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != currentButton) {
                    button.setBackground(new Color(200, 220, 240));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != currentButton) {
                    button.setBackground(new Color(180, 205, 230));
                }
            }
        });

        return button;
    }

    private void updateButtonSelection(JButton selectedButton) {
        if (currentButton != null) {
            currentButton.setBackground(new Color(180, 205, 230));
        }
        selectedButton.setBackground(new Color(200, 220, 240));
        currentButton = selectedButton;
    }

    private void showPersonalInfo() {
        cardLayout.show(mainPanel, "PERSONAL_INFO");
    }

    private void showMedicalRecord() {
        // Trước khi hiển thị, kiểm tra nếu patientId null thì load lại dữ liệu
        if (currentUser.getPatientId() == null) {
            loadUserData();
        }

        // Cập nhật dữ liệu mới nhất
        medicalRecordPanel.refreshData();
        cardLayout.show(mainPanel, "MEDICAL_RECORD");
    }

    private void showPrescription() {
        // Trước khi hiển thị, kiểm tra nếu patientId null thì load lại dữ liệu
        if (currentUser.getPatientId() == null) {
            loadUserData();
        }

        // Cập nhật dữ liệu mới nhất
        prescriptionPanel.refreshData();
        cardLayout.show(mainPanel, "PRESCRIPTION");
    }

    private void showPayment() {
        // Trước khi hiển thị, kiểm tra nếu patientId null thì load lại dữ liệu
        if (currentUser.getPatientId() == null) {
            loadUserData();
        }

        // Cập nhật dữ liệu mới nhất
        paymentPanel.refreshData();
        cardLayout.show(mainPanel, "PAYMENT");
    }

    private void showAppointment() {
        // Cập nhật dữ liệu mới nhất
        appointmentPanel.reloadTable();
        cardLayout.show(mainPanel, "APPOINTMENT");
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            MainFrame mainFrame = new MainFrame();
            mainFrame.setContentPane(new LoginPanel(mainFrame, Role.PATIENT));
            mainFrame.revalidate();
            mainFrame.pack();
            mainFrame.setSize(1200, 700);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        }
    }

    // Getter để các panel khác có thể truy cập thông tin người dùng
    public User getCurrentUser() {
        return currentUser;
    }
}

/**
 * Panel hiển thị thông tin cá nhân
 */
class PersonalInfoPanel extends JPanel {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Pha2k5@";

    private JLabel lblFullName;
    private JLabel lblPhone;
    private JLabel lblDOB;
    private JLabel lblGender;
    private JLabel lblAddress;
    private JLabel lblCCCD;
    private JLabel lblInsurance;
    private JLabel lblInsuranceId;
    private JLabel lblInsuranceExp;
    private JButton btnEdit;

    public PersonalInfoPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createEmptyBorder(20, 30, 20, 30),
                javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)
        ));

        // Panel chứa tiêu đề và nút chỉnh sửa
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Thông tin cá nhân");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));

        btnEdit = new JButton("Chỉnh sửa");
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(41, 128, 185));
        btnEdit.setFocusPainted(false);
        btnEdit.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(btnEdit, BorderLayout.EAST);

        // Panel chứa thông tin
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 15);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Thêm các label
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Họ tên:");
        nameLabel.setFont(labelFont);
        infoPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        lblFullName = new JLabel(user != null ? user.getFullName() : "");
        lblFullName.setFont(valueFont);
        infoPanel.add(lblFullName, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneLabel.setFont(labelFont);
        infoPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        lblPhone = new JLabel(user != null ? user.getPhone() : "");
        lblPhone.setFont(valueFont);
        infoPanel.add(lblPhone, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel dobLabel = new JLabel("Ngày sinh:");
        dobLabel.setFont(labelFont);
        infoPanel.add(dobLabel, gbc);

        gbc.gridx = 1;
        lblDOB = new JLabel(user != null ? user.getDateOfBirth() : "");
        lblDOB.setFont(valueFont);
        infoPanel.add(lblDOB, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel genderLabel = new JLabel("Giới tính:");
        genderLabel.setFont(labelFont);
        infoPanel.add(genderLabel, gbc);

        gbc.gridx = 1;
        lblGender = new JLabel(user != null ? user.getGender() : "");
        lblGender.setFont(valueFont);
        infoPanel.add(lblGender, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel addressLabel = new JLabel("Nơi cư trú:");
        addressLabel.setFont(labelFont);
        infoPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        lblAddress = new JLabel(user != null ? user.getAddress() : "");
        lblAddress.setFont(valueFont);
        infoPanel.add(lblAddress, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel cccdLabel = new JLabel("Số CCCD:");
        cccdLabel.setFont(labelFont);
        infoPanel.add(cccdLabel, gbc);

        gbc.gridx = 1;
        lblCCCD = new JLabel(user != null ? user.getCccd() : "");
        lblCCCD.setFont(valueFont);
        infoPanel.add(lblCCCD, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel insuranceLabel = new JLabel("Có giấy BHYT không:");
        insuranceLabel.setFont(labelFont);
        infoPanel.add(insuranceLabel, gbc);

        gbc.gridx = 1;
        lblInsurance = new JLabel(user != null && user.isHasInsurance() ? "Có" : "Không");
        lblInsurance.setFont(valueFont);
        infoPanel.add(lblInsurance, gbc);

        // Hiển thị thông tin BHYT nếu có
        if (user != null && user.isHasInsurance()) {
            gbc.gridx = 0; gbc.gridy++;
            JLabel insuranceIdLabel = new JLabel("Mã BHYT:");
            insuranceIdLabel.setFont(labelFont);
            infoPanel.add(insuranceIdLabel, gbc);

            gbc.gridx = 1;
            lblInsuranceId = new JLabel(user.getInsuranceId() != null ? user.getInsuranceId() : "");
            lblInsuranceId.setFont(valueFont);
            infoPanel.add(lblInsuranceId, gbc);

            gbc.gridx = 0; gbc.gridy++;
            JLabel insuranceExpLabel = new JLabel("Ngày hết hạn:");
            insuranceExpLabel.setFont(labelFont);
            infoPanel.add(insuranceExpLabel, gbc);

            gbc.gridx = 1;
            lblInsuranceExp = new JLabel(user.getInsuranceExpDate() != null ? user.getInsuranceExpDate() : "");
            lblInsuranceExp.setFont(valueFont);
            infoPanel.add(lblInsuranceExp, gbc);
        }

        // Thêm chức năng chỉnh sửa thông tin
        btnEdit.addActionListener(e -> {
            showEditDialog(user);
        });

        // Thêm các panel vào panel chính
        add(topPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
    }

    /**
     * Cập nhật thông tin hiển thị khi có thay đổi
     */
    public void updateInfo(User user) {
        if (user == null) return;

        lblFullName.setText(user.getFullName());
        lblPhone.setText(user.getPhone());
        lblDOB.setText(user.getDateOfBirth());
        lblGender.setText(user.getGender());
        lblAddress.setText(user.getAddress());
        lblCCCD.setText(user.getCccd());
        lblInsurance.setText(user.isHasInsurance() ? "Có" : "Không");

        // Cập nhật thông tin BHYT
        if (user.isHasInsurance()) {
            if (lblInsuranceId != null) lblInsuranceId.setText(user.getInsuranceId());
            if (lblInsuranceExp != null) lblInsuranceExp.setText(user.getInsuranceExpDate());
        }
    }

    /**
     * Hiển thị dialog chỉnh sửa thông tin
     */
    private void showEditDialog(User user) {
        if (user == null) return;

        JTextField phoneField = new JTextField(user.getPhone(), 20);
        JTextField addressField = new JTextField(user.getAddress(), 20);
        JTextField cccdField = new JTextField(user.getCccd(), 20);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Số CCCD:"), gbc);
        gbc.gridx = 1;
        panel.add(cccdField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Chỉnh sửa thông tin",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String cccd = cccdField.getText().trim();

            // Kiểm tra dữ liệu nhập vào
            if (!ExceptionUtils.validatePhone(this, phone)) {
                return;
            }

            // Cập nhật vào cơ sở dữ liệu
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // Cập nhật bảng UserAccounts
                String sql1 = "UPDATE UserAccounts SET PhoneNumber = ? WHERE UserName = ?";
                try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
                    stmt1.setString(1, phone);
                    stmt1.setString(2, user.getUsername());
                    stmt1.executeUpdate();
                }

                // Cập nhật bảng Patients
                String sql2 = "UPDATE Patients p JOIN UserAccounts u ON p.UserID = u.UserID " +
                        "SET p.PhoneNumber = ?, p.Address = ? " +
                        "WHERE u.UserName = ?";
                try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                    stmt2.setString(1, phone);
                    stmt2.setString(2, address);
                    stmt2.setString(3, user.getUsername());
                    stmt2.executeUpdate();
                }

                // Cập nhật đối tượng User
                user.setPhone(phone);
                user.setAddress(address);
                user.setCccd(cccd);

                // Cập nhật giao diện
                updateInfo(user);

                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");

            } catch (SQLException e) {
                ExceptionUtils.handleSQLException(this, e);
            }
        }
    }
}

/**
 * Panel hiển thị bệnh án
 */
class MedicalRecordPanel extends JPanel {
    // Thêm các biến kết nối database 
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Pha2k5@";

    private User user;
    private JTextArea contentArea;

    public MedicalRecordPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)
        ));

        // Panel chứa tiêu đề
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Bệnh án");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Khu vực hiển thị bệnh án
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        // Tải dữ liệu ban đầu
        refreshData();

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Tải lại dữ liệu bệnh án
     */
    public void refreshData() {
        if (user == null || user.getPatientId() == null) {
            contentArea.setText("Không tìm thấy thông tin bệnh án.");
            return;
        }

        String recordText = getMedicalRecordFromDB(user.getPatientId());
        contentArea.setText(recordText);
    }

    private String getMedicalRecordFromDB(String patientId) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT r.*, u.FullName as DoctorName " +
                    "FROM MedicalRecords r " +
                    "JOIN UserAccounts u ON r.DoctorID = u.UserID " +
                    "WHERE r.PatientID = ? " +
                    "ORDER BY r.RecordDate DESC";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean hasRecords = false;

                    while (rs.next()) {
                        hasRecords = true;
                        sb.append("=== BỆNH ÁN ===\n\n");
                        sb.append("Mã hồ sơ: ").append(rs.getString("RecordID")).append("\n");
                        sb.append("Ngày tạo: ").append(rs.getDate("RecordDate")).append("\n");
                        sb.append("Bác sĩ phụ trách: ").append(rs.getString("DoctorName")).append("\n\n");
                        sb.append("Chẩn đoán: ").append(rs.getString("Diagnosis")).append("\n\n");
                        sb.append("Điều trị: ").append(rs.getString("Treatment")).append("\n\n");
                        sb.append("Ghi chú: ").append(rs.getString("Notes")).append("\n");
                        sb.append("-----------------------------------\n\n");
                    }

                    if (!hasRecords) {
                        sb.append("Chưa có bệnh án.");
                    }
                }
            }
        } catch (SQLException e) {
            return "Lỗi khi truy vấn dữ liệu: " + e.getMessage();
        }
        return sb.toString();
    }
}

/**
 * Panel hiển thị đơn thuốc
 */
class PrescriptionPanel extends JPanel {
    // Thêm các biến kết nối database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Pha2k5@";

    private User user;
    private JTextArea contentArea;

    public PrescriptionPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)
        ));

        // Panel chứa tiêu đề
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Đơn thuốc");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Khu vực hiển thị đơn thuốc
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        // Tải dữ liệu ban đầu
        refreshData();

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Tải lại dữ liệu đơn thuốc
     */
    public void refreshData() {
        if (user == null || user.getPatientId() == null) {
            contentArea.setText("Không tìm thấy thông tin đơn thuốc.");
            return;
        }

        String prescriptionsText = getPrescriptionsFromDB(user.getPatientId());
        contentArea.setText(prescriptionsText);
    }

    private String getPrescriptionsFromDB(String patientId) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Ví dụ giả định có bảng Prescriptions
            // Thực tế nếu không có bảng này, có thể tạo hoặc sửa câu truy vấn thích hợp
            String sql = "SELECT r.RecordID, r.RecordDate, r.Treatment, u.FullName as DoctorName " +
                    "FROM MedicalRecords r " +
                    "JOIN UserAccounts u ON r.DoctorID = u.UserID " +
                    "WHERE r.PatientID = ? AND r.Treatment IS NOT NULL AND r.Treatment != '' " +
                    "ORDER BY r.RecordDate DESC";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean hasRecords = false;

                    while (rs.next()) {
                        hasRecords = true;
                        sb.append("=== ĐƠN THUỐC ===\n\n");
                        sb.append("Mã đơn: ").append(rs.getString("RecordID")).append("\n");
                        sb.append("Ngày kê: ").append(rs.getDate("RecordDate")).append("\n");
                        sb.append("Bác sĩ kê đơn: ").append(rs.getString("DoctorName")).append("\n\n");
                        sb.append("Thuốc và hướng dẫn sử dụng:\n").append(rs.getString("Treatment")).append("\n");
                        sb.append("-----------------------------------\n\n");
                    }

                    if (!hasRecords) {
                        sb.append("Chưa có đơn thuốc.");
                    }
                }
            }
        } catch (SQLException e) {
            return "Lỗi khi truy vấn dữ liệu: " + e.getMessage();
        }
        return sb.toString();
    }
}

/**
 * Panel thanh toán
 */
class PaymentPanel extends JPanel {
    // Thêm các biến kết nối database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Pha2k5@";

    private User user;
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private JButton payButton;

    public PaymentPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)
        ));

        // Panel chứa tiêu đề
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Thanh toán");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Tạo bảng hiển thị hóa đơn
        String[] columns = {"Mã hóa đơn", "Ngày lập", "Loại", "Số tiền", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentTable = new JTable(tableModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentTable.setRowHeight(25);
        paymentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        paymentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Panel chứa nút thanh toán
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);

        payButton = new JButton("Thanh toán");
        payButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payButton.setForeground(Color.WHITE);
        payButton.setBackground(new Color(41, 128, 185));
        payButton.setFocusPainted(false);
        payButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        payButton.addActionListener(e -> {
            int selectedRow = paymentTable.getSelectedRow();
            if (selectedRow >= 0) {
                String status = (String) tableModel.getValueAt(selectedRow, 4);
                if (!"Đã thanh toán".equals(status)) {
                    paySelectedInvoice(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(this, "Hóa đơn này đã được thanh toán!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để thanh toán!");
            }
        });

        bottomPanel.add(payButton);

        // Tải dữ liệu ban đầu
        refreshData();

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Tải lại dữ liệu thanh toán
     */
    public void refreshData() {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);

        if (user == null || user.getPatientId() == null) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Giả định có bảng Invoices để lưu hóa đơn
            // Nếu không có, có thể sử dụng các bảng hiện có để mô phỏng
            String sql = "SELECT a.AppointmentID as InvoiceID, a.AppointmentDate as InvoiceDate, " +
                    "'Phí khám bệnh' as InvoiceType, '300000' as Amount, " +
                    "a.Status as PaymentStatus, '' as Notes " +
                    "FROM Appointments a " +
                    "WHERE a.PatientID = ? " +
                    "ORDER BY a.AppointmentDate DESC";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getPatientId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String invoiceId = rs.getString("InvoiceID");
                        String date = rs.getDate("InvoiceDate").toString();
                        String type = rs.getString("InvoiceType");
                        String amount = rs.getString("Amount");

                        // Chuyển đổi trạng thái từ trạng thái cuộc hẹn sang trạng thái thanh toán
                        String status = rs.getString("PaymentStatus");
                        if ("Da xac nhan".equals(status)) {
                            status = "Chưa thanh toán";
                        } else if ("Cho xac nhan".equals(status)) {
                            status = "Đang xử lý";
                        } else if ("Huy".equals(status)) {
                            status = "Đã hủy";
                        }

                        String notes = rs.getString("Notes");

                        tableModel.addRow(new Object[]{invoiceId, date, type, amount, status, notes});
                    }
                }
            }
        } catch (SQLException e) {
            ExceptionUtils.handleSQLException(this, e);
        }
    }

    /**
     * Thanh toán hóa đơn đã chọn
     */
    private void paySelectedInvoice(int row) {
        String invoiceId = (String) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn thanh toán hóa đơn này?",
                "Xác nhận thanh toán",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // Cập nhật trạng thái cuộc hẹn thành "Đã thanh toán"
                String sql = "UPDATE Appointments SET Status = 'Da xac nhan', Notes = CONCAT(IFNULL(Notes, ''), ' - Đã thanh toán') " +
                        "WHERE AppointmentID = ?";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, invoiceId);
                    int result = stmt.executeUpdate();

                    if (result > 0) {
                        // Cập nhật bảng
                        tableModel.setValueAt("Đã thanh toán", row, 4);
                        JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Không thể thanh toán. Vui lòng thử lại!");
                    }
                }
            } catch (SQLException e) {
                ExceptionUtils.handleSQLException(this, e);
            }
        }
    }
}

/**
 * Panel đặt lịch khám
 */
class PatientAppointmentPanel extends JPanel {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Pha2k5@";

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton bookBtn;
    private JButton cancelBtn;
    private String currentUsername;
    private String patientId;

    public PatientAppointmentPanel(String patientUsername) {
        this.currentUsername = patientUsername;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)
        ));

        // Panel chứa tiêu đề
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Lịch hẹn khám bệnh");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Tạo bảng lịch hẹn
        String[] columns = {"Mã lịch hẹn", "Bác sĩ", "Ngày giờ", "Lý do khám", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        bookBtn = new JButton("Đặt lịch mới");
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setBackground(new Color(41, 128, 185));
        bookBtn.setFocusPainted(false);
        bookBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        cancelBtn = new JButton("Hủy lịch");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(new Color(231, 76, 60));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        buttonPanel.add(bookBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cancelBtn);

        // Xử lý sự kiện
        bookBtn.addActionListener(e -> showBookDialog());

        cancelBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String status = (String) tableModel.getValueAt(selectedRow, 4);
                if (!"Đã hủy".equals(status)) {
                    cancelSelectedAppointment(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(this, "Lịch hẹn này đã bị hủy!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch hẹn để hủy!");
            }
        });

        // Lấy patientId từ username
        fetchPatientId();

        // Tải dữ liệu ban đầu
        reloadTable();

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Lấy PatientID từ username
     */
    private void fetchPatientId() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT p.PatientID FROM Patients p " +
                    "JOIN UserAccounts u ON p.UserID = u.UserID " +
                    "WHERE u.UserName = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, currentUsername);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        this.patientId = rs.getString("PatientID");
                    }
                }
            }
        } catch (SQLException e) {
            ExceptionUtils.handleSQLException(this, e);
        }
    }

    /**
     * Tải lại dữ liệu bảng
     */
    public void reloadTable() {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);

        if (patientId == null) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT a.*, u.FullName as DoctorName " +
                    "FROM Appointments a " +
                    "JOIN UserAccounts u ON a.DoctorID = u.UserID " +
                    "WHERE a.PatientID = ? " +
                    "ORDER BY a.AppointmentDate DESC";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String appointmentId = rs.getString("AppointmentID");
                        String doctorName = rs.getString("DoctorName");
                        String appointmentDate = rs.getTimestamp("AppointmentDate").toString();
                        String reason = rs.getString("Reason");
                        String status = rs.getString("Status");

                        // Chuyển đổi trạng thái để hiển thị
                        if ("Da xac nhan".equals(status)) {
                            status = "Đã xác nhận";
                        } else if ("Cho xac nhan".equals(status)) {
                            status = "Chờ xác nhận";
                        } else if ("Huy".equals(status)) {
                            status = "Đã hủy";
                        }

                        tableModel.addRow(new Object[]{appointmentId, doctorName, appointmentDate, reason, status});
                    }
                }
            }
        } catch (SQLException e) {
            ExceptionUtils.handleSQLException(this, e);
        }
    }

    /**
     * Hiển thị dialog đặt lịch
     */
    private void showBookDialog() {
        if (patientId == null) {
            JOptionPane.showMessageDialog(this, "Không thể xác định thông tin bệnh nhân!");
            return;
        }

        // Tạo panel nhập thông tin
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ComboBox chọn bác sĩ
        JComboBox<String> doctorCombo = new JComboBox<>();
        JComboBox<String> doctorIdCombo = new JComboBox<>(); // Hidden combo để lưu ID

        // Spinner chọn ngày giờ
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm"));

        // TextArea nhập lý do
        JTextArea reasonArea = new JTextArea(3, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);

        // Thêm các thành phần vào panel
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Bác sĩ:"), gbc);
        gbc.gridx = 1;
        panel.add(doctorCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ngày giờ:"), gbc);
        gbc.gridx = 1;
        panel.add(dateSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Lý do khám:"), gbc);
        gbc.gridx = 1;
        panel.add(reasonScroll, gbc);

        // Lấy danh sách bác sĩ
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT UserID, FullName FROM UserAccounts WHERE Role = 'Bac si'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        doctorCombo.addItem(rs.getString("FullName"));
                        doctorIdCombo.addItem(rs.getString("UserID"));
                    }
                }
            }
        } catch (SQLException e) {
            ExceptionUtils.handleSQLException(this, e);
            return;
        }

        // Hiển thị dialog
        int result = JOptionPane.showConfirmDialog(this, panel, "Đặt lịch khám",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Lấy giá trị đã chọn
            int selectedIndex = doctorCombo.getSelectedIndex();
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ!");
                return;
            }

            String doctorId = doctorIdCombo.getItemAt(selectedIndex);
            java.util.Date appointmentDate = (java.util.Date) dateSpinner.getValue();
            String reason = reasonArea.getText().trim();

            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do khám!");
                return;
            }

            // Thêm lịch hẹn mới
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String appointmentId = "AP" + System.currentTimeMillis();

                String sql = "INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Reason, Status) " +
                        "VALUES (?, ?, ?, ?, ?, 'Cho xac nhan')";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, appointmentId);
                    stmt.setString(2, patientId);
                    stmt.setString(3, doctorId);
                    stmt.setTimestamp(4, new java.sql.Timestamp(appointmentDate.getTime()));
                    stmt.setString(5, reason);

                    int rows = stmt.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Đặt lịch thành công! Vui lòng chờ xác nhận.");
                        reloadTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Đặt lịch thất bại. Vui lòng thử lại!");
                    }
                }
            } catch (SQLException e) {
                ExceptionUtils.handleSQLException(this, e);
            }
        }
    }

    /**
     * Hủy lịch hẹn đã chọn
     */
    private void cancelSelectedAppointment(int row) {
        String appointmentId = (String) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn hủy lịch hẹn này?",
                "Xác nhận hủy lịch",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE Appointments SET Status = 'Huy' WHERE AppointmentID = ?";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, appointmentId);
                    int result = stmt.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Hủy lịch thành công!");
                        reloadTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Không thể hủy lịch. Vui lòng thử lại!");
                    }
                }
            } catch (SQLException e) {
                ExceptionUtils.handleSQLException(this, e);
            }
        }
    }
}