package model.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import model.entity.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientMainFrame extends JFrame {
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Panels
    private JPanel dashboardPanel;
    private JPanel appointmentPanel;
    private JPanel medicalHistoryPanel;
    private JPanel prescriptionPanel;
    private JPanel personalInfoPanel;
    private JPanel paymentsPanel;

    // Buttons
    private JButton homeButton;
    private JButton appointmentButton;
    private JButton medicalHistoryButton;
    private JButton prescriptionButton;
    private JButton personalInfoButton;
    private JButton paymentsButton;
    private JButton logoutButton;

    // Tables and details
    private JTable appointmentsTable;
    private JTable medicalRecordsTable;
    private JTextArea detailsArea;
    private JTable prescriptionsTable;
    private JTable detailsTable;
    private JTable invoicesTable;

    // Dashboard stats
    private int upcomingAppointmentCount = 0;
    private int currentPrescriptionCount = 0;
    private int medicalRecordCount = 0;
    private int unpaidBillCount = 0;

    private JButton activeButton;

    public PatientMainFrame(User user) {
        this.currentUser = user;
        setTitle("Hệ thống quản lý bệnh nhân - Giao diện bệnh nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        JPanel navPanel = createNavigationPanel();
        contentPane.add(navPanel, BorderLayout.WEST);

        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        createPanels();

        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(appointmentPanel, "APPOINTMENTS");
        mainPanel.add(medicalHistoryPanel, "MEDICAL_HISTORY");
        mainPanel.add(prescriptionPanel, "PRESCRIPTIONS");
        mainPanel.add(personalInfoPanel, "PERSONAL_INFO");
        mainPanel.add(paymentsPanel, "PAYMENTS");

        contentPane.add(mainPanel, BorderLayout.CENTER);

        setActiveButton(homeButton);
        cardLayout.show(mainPanel, "DASHBOARD");
    }

    private void createPanels() {
        dashboardPanel = createPatientDashboard();
        appointmentPanel = createAppointmentPanel();
        medicalHistoryPanel = createMedicalHistoryPanel();
        prescriptionPanel = createPrescriptionPanel();
        personalInfoPanel = createPersonalInfoPanel();
        paymentsPanel = createPaymentsPanel();
    }

    private JPanel createPatientDashboard() {
        updateDashboardStats();

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Xin chào, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        statsPanel.add(createInfoCard("Lịch hẹn sắp tới", String.valueOf(upcomingAppointmentCount), new Color(0, 102, 204)));
        statsPanel.add(createInfoCard("Đơn thuốc hiện tại", String.valueOf(currentPrescriptionCount), new Color(0, 102, 204)));
        statsPanel.add(createInfoCard("Hồ sơ khám bệnh", String.valueOf(medicalRecordCount), new Color(0, 102, 204)));
        statsPanel.add(createInfoCard("Hóa đơn chờ thanh toán", String.valueOf(unpaidBillCount), new Color(0, 102, 204)));

        JPanel personalInfoBoxPanel = createPersonalInfoBox();

        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(personalInfoBoxPanel, BorderLayout.CENTER);

        panel.add(createSectionHeader("Tổng quan"), BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(welcomePanel, BorderLayout.NORTH);
        centerPanel.add(contentPanel, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private void updateDashboardStats() {
        try {
            Connection connection = getDBConnection();
            if (connection == null) return;

            String patientID = currentUser.getPatientId();

            // Upcoming appointments
            String appointmentQuery = "SELECT COUNT(*) FROM Appointments WHERE PatientID = ? AND AppointmentDate >= CURRENT_DATE AND Status != 'Huy'";
            try (PreparedStatement pstmt = connection.prepareStatement(appointmentQuery)) {
                pstmt.setString(1, patientID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) upcomingAppointmentCount = rs.getInt(1);
            }

            // Current prescriptions
            String prescriptionQuery = "SELECT COUNT(*) FROM Prescriptions WHERE PatientID = ? AND PrescriptionDate >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)";
            try (PreparedStatement pstmt = connection.prepareStatement(prescriptionQuery)) {
                pstmt.setString(1, patientID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) currentPrescriptionCount = rs.getInt(1);
            }

            // Medical records
            String medicalRecordQuery = "SELECT COUNT(*) FROM MedicalRecords WHERE PatientID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(medicalRecordQuery)) {
                pstmt.setString(1, patientID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) medicalRecordCount = rs.getInt(1);
            }

            // Unpaid bills
            String unpaidBillQuery = "SELECT COUNT(*) FROM Invoices WHERE PatientID = ? AND Status IN ('PENDING', 'PARTIALLY_PAID')";
            try (PreparedStatement pstmt = connection.prepareStatement(unpaidBillQuery)) {
                pstmt.setString(1, patientID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) unpaidBillCount = rs.getInt(1);
            }

            connection.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật số liệu tổng quan: " + e.getMessage());
        }
    }

    private Connection getDBConnection() {
        String url = "jdbc:mysql://localhost:3306/PatientManagement?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
        String username = "root";
        String password = "Pha2k5@"; // Replace with your MySQL password

        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("Không thể kết nối đến cơ sở dữ liệu: " + e.getMessage());
            return null;
        }
    }

    private JPanel createPersonalInfoBox() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0, 102, 204), 2, true),
                        "Thông tin cá nhân"
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel infoGrid = new JPanel(new GridLayout(0, 2, 15, 10));

        infoGrid.add(createInfoRow("Họ và tên:", currentUser.getFullName()));
        infoGrid.add(createInfoRow("Mã bệnh nhân:", currentUser.getPatientId()));
        infoGrid.add(createInfoRow("Ngày sinh:", currentUser.getDateOfBirth()));
        infoGrid.add(createInfoRow("Giới tính:", currentUser.getGender()));
        infoGrid.add(createInfoRow("Số điện thoại:", currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Chưa cập nhật"));
        infoGrid.add(createInfoRow("Địa chỉ:", currentUser.getAddress() != null ? currentUser.getAddress() : "Chưa cập nhật"));
        infoGrid.add(createInfoRow("Chiều cao:", currentUser.getHeight() > 0 ? currentUser.getHeight() + " cm" : "Chưa cập nhật"));
        infoGrid.add(createInfoRow("Cân nặng:", currentUser.getWeight() > 0 ? currentUser.getWeight() + " kg" : "Chưa cập nhật"));
        infoGrid.add(createInfoRow("Nhóm máu:", currentUser.getBloodType() != null ? currentUser.getBloodType() : "Chưa cập nhật"));
        infoGrid.add(createInfoRow("Số BHYT:", currentUser.isHasInsurance() ? currentUser.getInsuranceId() : "Không có"));

        panel.add(infoGrid, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Chỉnh sửa thông tin");
        editButton.addActionListener(e -> moHopThoaiChinhSuaThongTin());
        buttonPanel.add(editButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(labelComponent, BorderLayout.WEST);
        panel.add(valueComponent, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInfoCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void moHopThoaiChinhSuaThongTin() {
        JDialog dialog = new JDialog(this, "Chỉnh sửa thông tin cá nhân", true);
        dialog.setSize(400, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField(currentUser.getFullName(), 20);
        JTextField phoneField = new JTextField(currentUser.getPhoneNumber(), 20);
        JTextField addressField = new JTextField(currentUser.getAddress(), 20);
        JTextField heightField = new JTextField(String.valueOf(currentUser.getHeight()), 20);
        JTextField weightField = new JTextField(String.valueOf(currentUser.getWeight()), 20);
        JTextField bloodTypeField = new JTextField(currentUser.getBloodType(), 20);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Họ và tên:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Chiều cao (cm):"), gbc);
        gbc.gridx = 1;
        formPanel.add(heightField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Cân nặng (kg):"), gbc);
        gbc.gridx = 1;
        formPanel.add(weightField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Nhóm máu:"), gbc);
        gbc.gridx = 1;
        formPanel.add(bloodTypeField, gbc);

        panel.add(formPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu thay đổi");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> {
            try {
                // Update local user object
                currentUser.setFullName(nameField.getText());
                currentUser.setPhoneNumber(phoneField.getText());
                currentUser.setAddress(addressField.getText());
                currentUser.setHeight(Double.parseDouble(heightField.getText()));
                currentUser.setWeight(Double.parseDouble(weightField.getText()));
                currentUser.setBloodType(bloodTypeField.getText());

                // Update database
                Connection connection = getDBConnection();
                if (connection != null) {
                    String updateQuery = "UPDATE Patients SET FullName = ?, PhoneNumber = ?, Address = ?, Height = ?, Weight = ?, BloodType = ? WHERE PatientID = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                        pstmt.setString(1, currentUser.getFullName());
                        pstmt.setString(2, currentUser.getPhoneNumber());
                        pstmt.setString(3, currentUser.getAddress());
                        pstmt.setDouble(4, currentUser.getHeight());
                        pstmt.setDouble(5, currentUser.getWeight());
                        pstmt.setString(6, currentUser.getBloodType());
                        pstmt.setString(7, currentUser.getPatientId());
                        pstmt.executeUpdate();
                    }

                    String updateUserQuery = "UPDATE UserAccounts SET FullName = ?, PhoneNumber = ? WHERE UserID = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(updateUserQuery)) {
                        pstmt.setString(1, currentUser.getFullName());
                        pstmt.setString(2, currentUser.getPhoneNumber());
                        pstmt.setString(3, currentUser.getUserId());
                        pstmt.executeUpdate();
                    }

                    connection.close();
                }

                JOptionPane.showMessageDialog(dialog, "Cập nhật thông tin thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                refreshDashboard();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Chiều cao và cân nặng phải là số!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public void refreshDashboard() {
        updateDashboardStats();
        JPanel newDashboardPanel = createPatientDashboard();
        mainPanel.remove(dashboardPanel);
        dashboardPanel = newDashboardPanel;
        mainPanel.add(dashboardPanel, "DASHBOARD");
        cardLayout.show(mainPanel, "DASHBOARD");
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        panel.add(createSectionHeader("Quản lý lịch hẹn"), BorderLayout.NORTH);

        String[] columns = {"Mã lịch hẹn", "Ngày hẹn", "Bác sĩ", "Lý do khám", "Trạng thái"};
        DefaultTableModel appointmentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentsTable = new JTable(appointmentModel);
        setupTable(appointmentsTable);
        JScrollPane tableScrollPane = new JScrollPane(appointmentsTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("Đặt lịch hẹn mới");
        addButton.addActionListener(e -> moHopThoaiDatLichHen());
        JButton cancelButton = new JButton("Hủy lịch hẹn");
        cancelButton.addActionListener(e -> huyLichHenDaChon());
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        updateAppointmentsTable();
        return panel;
    }

    private JPanel createMedicalHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(createSectionHeader("Lịch sử khám bệnh"), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"Mã hồ sơ", "Ngày khám", "Bác sĩ", "Chẩn đoán", "Điều trị"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicalRecordsTable = new JTable(model);
        setupTable(medicalRecordsTable);
        JScrollPane scrollPane = new JScrollPane(medicalRecordsTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết khám bệnh"));
        detailsArea = new JTextArea(10, 30);
        detailsArea.setEditable(false);
        detailsArea.setText("Chọn một hồ sơ từ bảng để xem chi tiết");
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsPanel.add(detailsScrollPane);
        contentPanel.add(detailsPanel, BorderLayout.SOUTH);

        medicalRecordsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiChiTietHoSoKham();
            }
        });

        updateMedicalRecordsTable();

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPrescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(createSectionHeader("Đơn thuốc và kê toa"), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"Mã đơn thuốc", "Ngày kê", "Bác sĩ", "Số loại thuốc", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        prescriptionsTable = new JTable(model);
        setupTable(prescriptionsTable);
        JScrollPane scrollPane = new JScrollPane(prescriptionsTable);
        contentPanel.add(scrollPane, BorderLayout.NORTH);

        JPanel prescriptionDetailsPanel = new JPanel(new BorderLayout());
        prescriptionDetailsPanel.setBackground(Color.WHITE);
        prescriptionDetailsPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn thuốc"));
        String[] detailColumns = {"Tên thuốc", "Liều lượng", "Hướng dẫn", "Số lượng", "Giá"};
        DefaultTableModel detailModel = new DefaultTableModel(detailColumns, 0);
        detailsTable = new JTable(detailModel);
        setupTable(detailsTable);
        JScrollPane detailsScrollPane = new JScrollPane(detailsTable);
        prescriptionDetailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        contentPanel.add(prescriptionDetailsPanel, BorderLayout.CENTER);

        prescriptionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiChiTietDonThuoc();
            }
        });

        updatePrescriptionsTable();

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(createSectionHeader("Thông tin cá nhân"), BorderLayout.NORTH);

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

        addFormField(contentPanel, gbc, "Họ và tên:", new JLabel(currentUser.getFullName()));
        gbc.gridy++;
        addFormField(contentPanel, gbc, "Ngày sinh:", new JLabel(currentUser.getDateOfBirth()));
        gbc.gridy++;
        addFormField(contentPanel, gbc, "Giới tính:", new JLabel(currentUser.getGender()));
        gbc.gridy++;
        addFormField(contentPanel, gbc, "Địa chỉ:", new JLabel(currentUser.getAddress()));
        gbc.gridy++;
        addFormField(contentPanel, gbc, "Số điện thoại:", new JLabel(currentUser.getPhoneNumber()));
        gbc.gridy++;
        addFormField(contentPanel, gbc, "Email:", new JLabel(currentUser.getEmail()));
        gbc.gridy++;

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

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        JButton editInfoButton = new JButton("Chỉnh sửa thông tin");
        editInfoButton.addActionListener(e -> moHopThoaiChinhSuaThongTin());
        contentPanel.add(editInfoButton, gbc);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(createSectionHeader("Quản lý thanh toán và hóa đơn"), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"Mã hóa đơn", "Ngày tạo", "Loại", "Số tiền", "Đã thanh toán", "Còn lại", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        invoicesTable = new JTable(model);
        setupTable(invoicesTable);
        JScrollPane scrollPane = new JScrollPane(invoicesTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewDetailsBtn = new JButton("Xem chi tiết");
        JButton payInvoiceBtn = new JButton("Thanh toán");
        viewDetailsBtn.addActionListener(e -> xemChiTietHoaDon());
        payInvoiceBtn.addActionListener(e -> xuLyThanhToan());
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(payInvoiceBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        updateInvoicesTable();

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
        JPanel navPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(25, 79, 115),
                        0, getHeight(), new Color(52, 152, 219)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setPreferredSize(new Dimension(250, getHeight()));

        // User panel for avatar and user info
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setOpaque(false);
        // Set maximum width to fit content (avatar is 100px, labels may be wider)
        userPanel.setMaximumSize(new Dimension(250, 200));
        userPanel.setPreferredSize(new Dimension(250, 200));
        userPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Center the avatar panel within a container
        JPanel avatarContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarContainer.setOpaque(false);

        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight()) - 10;
                g2d.setColor(Color.WHITE);
                g2d.fillOval(5, 5, size, size);
                g2d.setColor(new Color(41, 128, 185));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
                String firstLetter = String.valueOf(currentUser.getFullName().charAt(0)).toUpperCase();
                FontMetrics metrics = g2d.getFontMetrics();
                int letterX = 5 + (size - metrics.stringWidth(firstLetter)) / 2;
                int letterY = 5 + ((size - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(firstLetter, letterX, letterY);
                g2d.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(100, 100));
        avatarPanel.setOpaque(false);
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        avatarContainer.add(avatarPanel);

        JLabel userLabel = new JLabel(currentUser.getFullName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Bệnh nhân");
        roleLabel.setForeground(Color.LIGHT_GRAY);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userPanel.add(Box.createVerticalStrut(20));
        userPanel.add(avatarContainer);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(userLabel);
        userPanel.add(Box.createVerticalStrut(5));
        userPanel.add(roleLabel);

        // Navigation buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        homeButton = createNavButton("Tổng quan", e -> {
            refreshDashboard();
            cardLayout.show(mainPanel, "DASHBOARD");
            setActiveButton(homeButton);
        });
        appointmentButton = createNavButton("Lịch hẹn khám bệnh", e -> {
            setActiveButton(appointmentButton);
            cardLayout.show(mainPanel, "APPOINTMENTS");
        });
        medicalHistoryButton = createNavButton("Lịch sử khám bệnh", e -> {
            setActiveButton(medicalHistoryButton);
            cardLayout.show(mainPanel, "MEDICAL_HISTORY");
        });
        prescriptionButton = createNavButton("Đơn thuốc", e -> {
            setActiveButton(prescriptionButton);
            cardLayout.show(mainPanel, "PRESCRIPTIONS");
        });
        paymentsButton = createNavButton("Thanh toán & Hóa đơn", e -> {
            setActiveButton(paymentsButton);
            cardLayout.show(mainPanel, "PAYMENTS");
        });
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

        // Add buttons to buttonsPanel with padding
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(homeButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(appointmentButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(medicalHistoryButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(prescriptionButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(paymentsButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(logoutButton);
        buttonsPanel.add(Box.createVerticalStrut(20));

        // Add components to navPanel
        navPanel.add(Box.createVerticalStrut(20));
        navPanel.add(userPanel);
        navPanel.add(Box.createVerticalStrut(20));
        navPanel.add(buttonsPanel);

        setActiveButton(homeButton);
        return navPanel;
    }

    private void setActiveButton(JButton button) {
        if (activeButton != null) {
            activeButton.setBackground(new Color(52, 73, 94));
            activeButton.setForeground(Color.WHITE);
        }
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        activeButton = button;
    }

    private JButton createNavButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(230, 45));
        button.setPreferredSize(new Dimension(230, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185, 0));
        button.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    if (button != logoutButton) {
                        button.setBackground(new Color(52, 152, 219));
                    } else {
                        button.setBackground(new Color(231, 76, 60).brighter());
                    }
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    if (button != logoutButton) {
                        button.setBackground(new Color(41, 128, 185, 0));
                    } else {
                        button.setBackground(new Color(231, 76, 60));
                    }
                }
            }
        });

        if (listener != null) {
            button.addActionListener(listener);
        }
        return button;
    }

    private void moHopThoaiDatLichHen() {
        JDialog dialog = new JDialog(this, "Đặt lịch hẹn mới", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Ngày khám:"), gbc);
        gbc.gridx = 1;
        JTextField dateField = new JTextField(10);
        panel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Giờ khám:"), gbc);
        gbc.gridx = 1;
        JTextField timeField = new JTextField(10);
        panel.add(timeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Bác sĩ:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> doctorComboBox = new JComboBox<>();
        loadDoctors(doctorComboBox);
        panel.add(doctorComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Lý do khám:"), gbc);
        gbc.gridx = 1;
        JTextArea reasonArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(reasonArea);
        panel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton confirmButton = new JButton("Xác nhận đặt lịch");
        confirmButton.addActionListener(e -> {
            if (dateField.getText().trim().isEmpty() || timeField.getText().trim().isEmpty() || reasonArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date appointmentDate = sdf.parse(dateField.getText() + " " + timeField.getText());
                String doctorName = (String) doctorComboBox.getSelectedItem();
                String reason = reasonArea.getText();

                Connection connection = getDBConnection();
                if (connection != null) {
                    String doctorIdQuery = "SELECT UserID FROM UserAccounts WHERE FullName = ? AND Role = 'Bac si'";
                    String doctorId;
                    try (PreparedStatement pstmt = connection.prepareStatement(doctorIdQuery)) {
                        pstmt.setString(1, doctorName);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            doctorId = rs.getString("UserID");
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Bác sĩ không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    String insertQuery = "INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Reason, Status) VALUES (?, ?, ?, ?, ?, ?)";
                    String newId = "AP" + String.format("%03d", System.currentTimeMillis() % 1000);
                    try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                        pstmt.setString(1, newId);
                        pstmt.setString(2, currentUser.getPatientId());
                        pstmt.setString(3, doctorId);
                        pstmt.setTimestamp(4, new java.sql.Timestamp(appointmentDate.getTime()));
                        pstmt.setString(5, reason);
                        pstmt.setString(6, "Cho xac nhan");
                        pstmt.executeUpdate();
                    }

                    connection.close();
                    JOptionPane.showMessageDialog(dialog, "Đã đặt lịch hẹn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    updateAppointmentsTable();
                    refreshDashboard();
                    dialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi đặt lịch: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(confirmButton, gbc);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void loadDoctors(JComboBox<String> doctorComboBox) {
        try {
            Connection connection = getDBConnection();
            if (connection != null) {
                String query = "SELECT FullName FROM UserAccounts WHERE Role = 'Bac si'";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        doctorComboBox.addItem(rs.getString("FullName"));
                    }
                }
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách bác sĩ: " + e.getMessage());
        }
    }

    private void huyLichHenDaChon() {
        int row = appointmentsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch hẹn cần hủy!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appointmentID = appointmentsTable.getValueAt(row, 0).toString();
        String status = appointmentsTable.getValueAt(row, 4).toString();

        if (!"Cho xac nhan".equals(status)) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể hủy lịch hẹn có trạng thái 'Cho xac nhan'!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy lịch hẹn này không?", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection connection = getDBConnection();
                if (connection != null) {
                    String query = "UPDATE Appointments SET Status = 'Huy' WHERE AppointmentID = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                        pstmt.setString(1, appointmentID);
                        int result = pstmt.executeUpdate();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(this, "Hủy lịch hẹn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            updateAppointmentsTable();
                            refreshDashboard();
                        } else {
                            JOptionPane.showMessageDialog(this, "Không thể hủy lịch hẹn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    connection.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi hủy lịch hẹn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hienThiChiTietHoSoKham() {
        int selectedRow = medicalRecordsTable.getSelectedRow();
        if (selectedRow != -1) {
            String recordID = (String) medicalRecordsTable.getValueAt(selectedRow, 0);

            try {
                Connection connection = getDBConnection();
                if (connection != null) {
                    String query = "SELECT mr.RecordID, mr.RecordDate, ua.FullName AS DoctorName, mr.Diagnosis, mr.Treatment, mr.Notes, mr.LifestyleRecommendations " +
                            "FROM MedicalRecords mr JOIN UserAccounts ua ON mr.DoctorID = ua.UserID WHERE mr.RecordID = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                        pstmt.setString(1, recordID);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            detailsArea.setText(
                                    "Mã hồ sơ: " + rs.getString("RecordID") + "\n\n" +
                                            "Ngày khám: " + rs.getString("RecordDate") + "\n" +
                                            "Bác sĩ: " + rs.getString("DoctorName") + "\n\n" +
                                            "Chẩn đoán: " + rs.getString("Diagnosis") + "\n\n" +
                                            "Điều trị: " + (rs.getString("Treatment") != null ? rs.getString("Treatment") : "Không có") + "\n\n" +
                                            "Ghi chú: " + (rs.getString("Notes") != null ? rs.getString("Notes") : "Không có") + "\n\n" +
                                            "Lời khuyên: " + (rs.getString("LifestyleRecommendations") != null ? rs.getString("LifestyleRecommendations") : "Không có")
                            );
                        }
                    }
                    connection.close();
                }
            } catch (SQLException e) {
                detailsArea.setText("Lỗi khi tải chi tiết: " + e.getMessage());
            }
        }
    }

    private void hienThiChiTietDonThuoc() {
        int selectedRow = prescriptionsTable.getSelectedRow();
        if (selectedRow != -1) {
            String prescriptionID = (String) prescriptionsTable.getValueAt(selectedRow, 0);

            DefaultTableModel model = (DefaultTableModel) detailsTable.getModel();
            model.setRowCount(0);

            try {
                Connection connection = getDBConnection();
                if (connection != null) {
                    String query = "SELECT pd.Dosage, pd.Instructions, pd.Quantity, pd.Price, m.Name " +
                            "FROM PrescriptionDetails pd JOIN Medications m ON pd.MedicationID = m.MedicationID " +
                            "WHERE pd.PrescriptionID = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                        pstmt.setString(1, prescriptionID);
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next()) {
                            model.addRow(new Object[]{
                                    rs.getString("Name"),
                                    rs.getString("Dosage"),
                                    rs.getString("Instructions"),
                                    rs.getInt("Quantity"),
                                    String.format("%,.0f VNĐ", rs.getDouble("Price") * rs.getInt("Quantity"))
                            });
                        }
                    }
                    connection.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết đơn thuốc: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setupTable(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
    }

    private void xemChiTietHoaDon() {
        int selectedRow = invoicesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xem chi tiết", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String invoiceID = (String) invoicesTable.getValueAt(selectedRow, 0);

        JDialog dialog = new JDialog(this, "Chi tiết hóa đơn", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        try {
            Connection connection = getDBConnection();
            if (connection != null) {
                String query = "SELECT InvoiceNumber, CreatedDate, InvoiceType, TotalAmount, PaidAmount, RemainingAmount, Status FROM Invoices WHERE InvoiceID = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setString(1, invoiceID);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        infoPanel.add(new JLabel("Mã hóa đơn:"));
                        infoPanel.add(new JLabel(rs.getString("InvoiceNumber")));
                        infoPanel.add(new JLabel("Ngày tạo:"));
                        infoPanel.add(new JLabel(rs.getString("CreatedDate")));
                        infoPanel.add(new JLabel("Loại:"));
                        infoPanel.add(new JLabel(rs.getString("InvoiceType")));
                        infoPanel.add(new JLabel("Tổng tiền:"));
                        infoPanel.add(new JLabel(String.format("%,.0f VNĐ", rs.getDouble("TotalAmount"))));
                        infoPanel.add(new JLabel("Trạng thái:"));
                        infoPanel.add(new JLabel(rs.getString("Status")));
                    }
                }

                JPanel detailPanel = new JPanel(new BorderLayout());
                detailPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết các khoản"));
                String[] columns = {"STT", "Mô tả", "Đơn giá", "Số lượng", "Thành tiền"};
                DefaultTableModel model = new DefaultTableModel(columns, 0);
                JTable detailTable = new JTable(model);
                setupTable(detailTable);
                JScrollPane scrollPane = new JScrollPane(detailTable);
                detailPanel.add(scrollPane, BorderLayout.CENTER);

                String detailQuery = "SELECT ServiceName, UnitPrice, Quantity, TotalPrice FROM InvoiceDetails WHERE InvoiceID = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(detailQuery)) {
                    pstmt.setString(1, invoiceID);
                    ResultSet rs = pstmt.executeQuery();
                    int stt = 1;
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                stt++,
                                rs.getString("ServiceName"),
                                String.format("%,.0f VNĐ", rs.getDouble("UnitPrice")),
                                rs.getInt("Quantity"),
                                String.format("%,.0f VNĐ", rs.getDouble("TotalPrice"))
                        });
                    }
                }

                JButton printButton = new JButton("In hóa đơn");
                printButton.addActionListener(e -> JOptionPane.showMessageDialog(dialog, "Đã gửi hóa đơn đến máy in", "Thông báo", JOptionPane.INFORMATION_MESSAGE));
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.add(printButton);

                panel.add(infoPanel, BorderLayout.NORTH);
                panel.add(detailPanel, BorderLayout.CENTER);
                panel.add(buttonPanel, BorderLayout.SOUTH);

                connection.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void xuLyThanhToan() {
        int selectedRow = invoicesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần thanh toán", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String invoiceID = (String) invoicesTable.getValueAt(selectedRow, 0);
        String status = (String) invoicesTable.getValueAt(selectedRow, 6);

        if ("PAID".equals(status)) {
            JOptionPane.showMessageDialog(this, "Hóa đơn đã được thanh toán!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Thanh toán hóa đơn", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Mã hóa đơn:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel((String) invoicesTable.getValueAt(selectedRow, 0)), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Số tiền:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel((String) invoicesTable.getValueAt(selectedRow, 5)), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Phương thức thanh toán:"), gbc);
        gbc.gridx = 1;
        String[] paymentMethods = {"Tiền mặt", "Thẻ ngân hàng", "Ví điện tử"};
        JComboBox<String> methodComboBox = new JComboBox<>(paymentMethods);
        panel.add(methodComboBox, gbc);

        JPanel bankCardPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        bankCardPanel.add(new JLabel("Số thẻ:"));
        bankCardPanel.add(new JTextField(16));
        bankCardPanel.add(new JLabel("Ngày hết hạn:"));
        bankCardPanel.add(new JTextField(8));
        bankCardPanel.setVisible(false);

        JPanel eWalletPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        eWalletPanel.add(new JLabel("Số điện thoại:"));
        eWalletPanel.add(new JTextField(11));
        eWalletPanel.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(bankCardPanel, gbc);
        gbc.gridy = 4;
        panel.add(eWalletPanel, gbc);

        methodComboBox.addActionListener(e -> {
            String selected = (String) methodComboBox.getSelectedItem();
            bankCardPanel.setVisible("Thẻ ngân hàng".equals(selected));
            eWalletPanel.setVisible("Ví điện tử".equals(selected));
            dialog.pack();
            dialog.setSize(400, dialog.getHeight());
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton confirmButton = new JButton("Xác nhận thanh toán");
        confirmButton.addActionListener(e -> {
            try {
                Connection connection = getDBConnection();
                if (connection != null) {
                    String query = "UPDATE Invoices SET Status = 'PAID', PaidAmount = TotalAmount, RemainingAmount = 0, PaidDate = NOW() WHERE InvoiceID = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                        pstmt.setString(1, invoiceID);
                        pstmt.executeUpdate();
                    }

                    String insertPayment = "INSERT INTO PaymentRecords (PaymentID, InvoiceID, PaymentDate, Amount, PaymentMethod, PaidBy, ReceivedBy) VALUES (?, ?, NOW(), ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(insertPayment)) {
                        pstmt.setString(1, "PAY" + System.currentTimeMillis());
                        pstmt.setString(2, invoiceID);
                        pstmt.setDouble(3, Double.parseDouble(((String) invoicesTable.getValueAt(selectedRow, 5)).replace(" VNĐ", "").replace(",", "")));
                        pstmt.setString(4, (String) methodComboBox.getSelectedItem());
                        pstmt.setString(5, currentUser.getPatientId());
                        pstmt.setString(6, currentUser.getUserId());
                        pstmt.executeUpdate();
                    }

                    connection.close();
                    invoicesTable.setValueAt("PAID", selectedRow, 6);
                    invoicesTable.setValueAt(invoicesTable.getValueAt(selectedRow, 3), selectedRow, 4);
                    invoicesTable.setValueAt("0 VNĐ", selectedRow, 5);
                    JOptionPane.showMessageDialog(dialog, "Thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    updateInvoicesTable();
                    refreshDashboard();
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thanh toán: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(confirmButton, gbc);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateAppointmentsTable() {
        DefaultTableModel model = (DefaultTableModel) appointmentsTable.getModel();
        model.setRowCount(0);

        try {
            Connection connection = getDBConnection();
            if (connection != null) {
                String query = "SELECT a.AppointmentID, a.AppointmentDate, ua.FullName AS DoctorName, a.Reason, a.Status " +
                        "FROM Appointments a JOIN UserAccounts ua ON a.DoctorID = ua.UserID " +
                        "WHERE a.PatientID = ? ORDER BY a.AppointmentDate DESC";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setString(1, currentUser.getPatientId());
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getString("AppointmentID"),
                                rs.getString("AppointmentDate"),
                                rs.getString("DoctorName"),
                                rs.getString("Reason"),
                                rs.getString("Status")
                        });
                    }
                }
                connection.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải lịch hẹn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMedicalRecordsTable() {
        DefaultTableModel model = (DefaultTableModel) medicalRecordsTable.getModel();
        model.setRowCount(0);

        try {
            Connection connection = getDBConnection();
            if (connection != null) {
                String query = "SELECT mr.RecordID, mr.RecordDate, ua.FullName AS DoctorName, mr.Diagnosis, mr.Treatment " +
                        "FROM MedicalRecords mr JOIN UserAccounts ua ON mr.DoctorID = ua.UserID " +
                        "WHERE mr.PatientID = ? ORDER BY mr.RecordDate DESC";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setString(1, currentUser.getPatientId());
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getString("RecordID"),
                                rs.getString("RecordDate"),
                                rs.getString("DoctorName"),
                                rs.getString("Diagnosis"),
                                rs.getString("Treatment")
                        });
                    }
                }
                connection.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải hồ sơ khám bệnh: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePrescriptionsTable() {
        DefaultTableModel model = (DefaultTableModel) prescriptionsTable.getModel();
        model.setRowCount(0);

        try {
            Connection connection = getDBConnection();
            if (connection != null) {
                String query = "SELECT p.PrescriptionID, p.PrescriptionDate, ua.FullName AS DoctorName, " +
                        "(SELECT COUNT(*) FROM PrescriptionDetails pd WHERE pd.PrescriptionID = p.PrescriptionID) AS DrugCount " +
                        "FROM Prescriptions p JOIN UserAccounts ua ON p.DoctorID = ua.UserID " +
                        "WHERE p.PatientID = ? ORDER BY p.PrescriptionDate DESC";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setString(1, currentUser.getPatientId());
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getString("PrescriptionID"),
                                rs.getString("PrescriptionDate"),
                                rs.getString("DoctorName"),
                                rs.getInt("DrugCount"),
                                "Đang xử lý"
                        });
                    }
                }
                connection.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải đơn thuốc: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateInvoicesTable() {
        DefaultTableModel model = (DefaultTableModel) invoicesTable.getModel();
        model.setRowCount(0);

        try {
            Connection connection = getDBConnection();
            if (connection != null) {
                String query = "SELECT InvoiceID, InvoiceNumber, CreatedDate, InvoiceType, TotalAmount, PaidAmount, RemainingAmount, Status " +
                        "FROM Invoices WHERE PatientID = ? ORDER BY CreatedDate DESC";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setString(1, currentUser.getPatientId());
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getString("InvoiceID"),
                                rs.getString("CreatedDate"),
                                rs.getString("InvoiceType"),
                                String.format("%,.0f VNĐ", rs.getDouble("TotalAmount")),
                                String.format("%,.0f VNĐ", rs.getDouble("PaidAmount")),
                                String.format("%,.0f VNĐ", rs.getDouble("RemainingAmount")),
                                rs.getString("Status")
                        });
                    }
                }
                connection.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}