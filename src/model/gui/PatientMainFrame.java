package model.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import model.entity.User;
import model.entity.Appointment;
import model.entity.MedicalRecord;
import model.entity.Prescription;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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

    // Thêm các biến thành viên để lưu reference đến các bảng
    private JTable appointmentsTable;
    private JTable medicalRecordsTable;
    private JTextArea detailsArea;
    private JTable prescriptionsTable;
    private JTable detailsTable;
    private JTable invoicesTable;

    // Thêm các biến thành viên để lưu trữ số liệu tổng quan
    private int upcomingAppointmentCount = 0;
    private int currentPrescriptionCount = 0;
    private int medicalRecordCount = 0;
    private int unpaidBillCount = 0;

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

    // Phương thức createPatientDashboard đã được chỉnh sửa để sử dụng giá trị động
    private JPanel createPatientDashboard() {
        // Cập nhật các số liệu tổng quan từ database
        updateDashboardStats();

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Phần tiêu đề "Xin chào"
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Xin chào, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Thẻ thông tin tổng quan với màu xanh và giá trị động
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        statsPanel.add(createInfoCard("Lịch hẹn sắp tới", String.valueOf(upcomingAppointmentCount), new Color(0, 102, 204)));
        statsPanel.add(createInfoCard("Đơn thuốc hiện tại", String.valueOf(currentPrescriptionCount), new Color(0, 102, 204)));
        statsPanel.add(createInfoCard("Hồ sơ khám bệnh", String.valueOf(medicalRecordCount), new Color(0, 102, 204)));
        statsPanel.add(createInfoCard("Hóa đơn chờ thanh toán", String.valueOf(unpaidBillCount), new Color(0, 102, 204)));

        // Thêm khung thông tin cá nhân bên dưới thông tin tổng quan
        JPanel personalInfoBoxPanel = createPersonalInfoBox();

        // Tạo panel chứa các phần thông tin
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(personalInfoBoxPanel, BorderLayout.CENTER);

        // Đưa tất cả vào panel chính
        panel.add(createSectionHeader("Tổng quan"), BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(welcomePanel, BorderLayout.NORTH);
        centerPanel.add(contentPanel, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    // Phương thức mới để cập nhật số liệu tổng quan từ cơ sở dữ liệu
    private void updateDashboardStats() {
        try {
            // Lấy patientID từ currentUser
            String patientID = getPatientIDFromUserID(currentUser.getId());
            if (patientID == null) {
                // Nếu không tìm thấy thông tin bệnh nhân, sử dụng giá trị mặc định
                return;
            }

            Connection connection = getDBConnection();
            if (connection == null) {
                return; // Không thể kết nối, giữ nguyên giá trị mặc định
            }

            // 1. Đếm số lịch hẹn sắp tới
            String appointmentQuery = "SELECT COUNT(*) FROM Appointments WHERE PatientID = ? AND AppointmentDate >= CURRENT_DATE() AND Status != 'Huy'";
            try (PreparedStatement pstmt = connection.prepareStatement(appointmentQuery)) {
                pstmt.setString(1, patientID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    upcomingAppointmentCount = rs.getInt(1);
                }
            }

            // 2. Đếm số đơn thuốc hiện tại (trong 30 ngày gần đây)
            String prescriptionQuery = "SELECT COUNT(*) FROM Prescriptions WHERE PatientID = ? AND PrescriptionDate >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY)";
            try (PreparedStatement pstmt = connection.prepareStatement(prescriptionQuery)) {
                pstmt.setString(1, patientID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    currentPrescriptionCount = rs.getInt(1);
                }
            }

            // 3. Đếm tổng số hồ sơ khám bệnh
            String medicalRecordQuery = "SELECT COUNT(*) FROM MedicalRecords WHERE PatientID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(medicalRecordQuery)) {
                pstmt.setString(1, patientID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    medicalRecordCount = rs.getInt(1);
                }
            }

            // 4. Đếm số hóa đơn chưa thanh toán (giả sử có bảng Invoices với cột Status)
            // Nếu không có bảng Invoices, bạn có thể bỏ qua phần này hoặc đếm từ bảng khác
            // Đây là ví dụ, bạn cần điều chỉnh theo cấu trúc CSDL thực tế của bạn
            String unpaidBillQuery = "SELECT COUNT(*) FROM Prescriptions WHERE PatientID = ? AND PrescriptionDate >= DATE_SUB(CURRENT_DATE(), INTERVAL 90 DAY)";
            try (PreparedStatement pstmt = connection.prepareStatement(unpaidBillQuery)) {
                pstmt.setString(1, patientID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    unpaidBillCount = rs.getInt(1);
                }
            }

            connection.close();
        } catch (SQLException e) {
            // Xử lý lỗi, giữ lại giá trị mặc định
            System.err.println("Lỗi khi cập nhật số liệu tổng quan: " + e.getMessage());
        }
    }

    // Phương thức để lấy PatientID từ UserID
    private String getPatientIDFromUserID(String userID) {
        try {
            Connection connection = getDBConnection();
            if (connection == null) return null;

            String query = "SELECT PatientID FROM Patients WHERE UserID = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, userID);
            ResultSet rs = pstmt.executeQuery();

            String patientID = null;
            if (rs.next()) {
                patientID = rs.getString("PatientID");
            }

            rs.close();
            pstmt.close();
            connection.close();

            return patientID;
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy PatientID: " + e.getMessage());
            return null;
        }
    }

    // Phương thức để lấy kết nối đến CSDL
    private Connection getDBConnection() {
        String url = "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=UTF-8&useUnicode=true";
        String username = "root"; // Thay đổi nếu cần
        String password = "Pha2k5@";

        try {
            // Thử kết nối với một số cấu hình phổ biến
            try {
                // Cố gắng kết nối với user root và mật khẩu trống
                return DriverManager.getConnection("jdbc:mysql://localhost:3306/PatientManagement", "root", "Pha2k5@");
            } catch (SQLException rootErr) {
                try {
                    // Thử lại với user root và mật khẩu mặc định khác
                    return DriverManager.getConnection("jdbc:mysql://localhost:3306/PatientManagement", "root", "Pha2k5@");
                } catch (SQLException rootDefaultErr) {
                    // Nếu vẫn không được, sử dụng kết nối với SSL disabled và allowPublicKeyRetrieval=true
                    return DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/PatientManagement?useSSL=false&allowPublicKeyRetrieval=true",
                            "root", "Pha2k5@");
                }
            }
        } catch (SQLException e) {
            System.err.println("Không thể kết nối đến cơ sở dữ liệu: " + e.getMessage());
            return null;
        }
    }

    // Thêm phương thức để làm mới trang tổng quan khi cần
    public void refreshDashboard() {
        updateDashboardStats(); // Cập nhật số liệu

        // Tạo lại panel dashboard với dữ liệu mới
        JPanel newDashboardPanel = createPatientDashboard();

        // Cập nhật mainPanel với panel mới
        mainPanel.remove(dashboardPanel);
        dashboardPanel = newDashboardPanel;
        mainPanel.add(dashboardPanel, "dashboard");

        // Hiển thị lại panel dashboard
        cardLayout.show(mainPanel, "dashboard");
        // Cập nhật giao diện
        mainPanel.revalidate();
        mainPanel.repaint();
    }


    // Phương thức tạo khung thông tin cá nhân (không sử dụng SQL)
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

        // Thêm thông tin mẫu (thay vì từ SQL)
        // Giả sử chúng ta có dữ liệu mẫu
        String fullName = currentUser.getFullName();
        String patientID = "P00" + currentUser.getId();

        infoGrid.add(createInfoRow("Họ và tên:", fullName));
        infoGrid.add(createInfoRow("Mã bệnh nhân:", patientID));
        infoGrid.add(createInfoRow("Ngày sinh:", "01/01/1990"));
        infoGrid.add(createInfoRow("Giới tính:", "Nam"));
        infoGrid.add(createInfoRow("Số điện thoại:", currentUser.getPhone() != null ? currentUser.getPhone() : "Chưa cập nhật"));
        infoGrid.add(createInfoRow("Địa chỉ:", currentUser.getAddress() != null ? currentUser.getAddress() : "Chưa cập nhật"));
        infoGrid.add(createInfoRow("Chiều cao:", "170 cm"));
        infoGrid.add(createInfoRow("Cân nặng:", "65 kg"));
        infoGrid.add(createInfoRow("Nhóm máu:", "O+"));
        infoGrid.add(createInfoRow("Số BHYT:", "SH12345678"));

        panel.add(infoGrid, BorderLayout.CENTER);

        // Thêm nút chỉnh sửa thông tin
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

    // Cập nhật phương thức createInfoCard để sử dụng màu xanh
    private JPanel createInfoCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        // Sử dụng màu xanh cho tất cả các thẻ
        card.setBackground(new Color(0, 102, 204)); // Màu xanh đồng nhất
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

    // Cập nhật phương thức moHopThoaiChinhSuaThongTin để không sử dụng SQL
    private void moHopThoaiChinhSuaThongTin() {
        JDialog dialog = new JDialog(this, "Chỉnh sửa thông tin cá nhân", true);
        dialog.setSize(400, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Sử dụng thông tin mẫu thay vì từ SQL
        String fullName = currentUser.getFullName();
        String phone = currentUser.getPhone() != null ? currentUser.getPhone() : "";
        String address = currentUser.getAddress() != null ? currentUser.getAddress() : "";

        // Tạo các trường chỉnh sửa với dữ liệu mẫu
        JTextField nameField = new JTextField(fullName, 20);
        JTextField phoneField = new JTextField(phone, 20);
        JTextField addressField = new JTextField(address, 20);
        JTextField heightField = new JTextField("170", 20);
        JTextField weightField = new JTextField("65", 20);
        JTextField bloodTypeField = new JTextField("O+", 20);

        // Tạo panel chứa form với GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Thêm các trường vào form
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

        // Thêm panel form vào panel chính
        panel.add(formPanel);

        // Thêm các nút lưu và hủy
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu thay đổi");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> {
            // Cập nhật thông tin (ở đây chỉ cập nhật thông tin người dùng trong bộ nhớ)
            currentUser.setFullName(nameField.getText());
            currentUser.setPhone(phoneField.getText());
            currentUser.setAddress(addressField.getText());

            JOptionPane.showMessageDialog(dialog,
                    "Cập nhật thông tin thành công!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            // Refresh dashboard để hiển thị tên người dùng mới
            JLabel welcomeLabel = new JLabel("Xin chào, " + currentUser.getFullName());
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

            // Tạo lại các panel để cập nhật thông tin
            createPanels();
            cardLayout.show(mainPanel, "dashboard");
            setActiveButton(homeButton);
            refreshAppointmentsTable();
            refreshDashboard();
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }
    /**
     * Làm mới bảng lịch hẹn và cập nhật trang tổng quan
     */
    public void refreshAppointmentsTable() {
        try {
            // Cập nhật dữ liệu bảng lịch hẹn
            updateAppointmentsTable();

            // Cập nhật số liệu trên trang tổng quan
            updateDashboardStats();

            // Làm mới giao diện nếu đang ở tab tổng quan
            if (activeButton == homeButton) {
                refreshDashboard();
            }

            // Làm mới giao diện nếu đang ở tab lịch hẹn
            if (activeButton == appointmentButton) {
                if (appointmentsTable != null) {
                    appointmentsTable.revalidate();
                    appointmentsTable.repaint();
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi làm mới bảng lịch hẹn: " + e.getMessage());
        }
    }
    
    private ImageIcon loadIcon(String path) {
        try {
            java.net.URL iconUrl = getClass().getResource(path);
            if (iconUrl != null) {
                return new ImageIcon(iconUrl);
            }
        } catch (Exception e) {
            System.err.println("Không thể tải icon từ đường dẫn " + path + ": " + e.getMessage());
        }
        return null;
    }

    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Tạo tiêu đề phần lịch hẹn
        panel.add(createSectionHeader("Quản lý lịch hẹn"), BorderLayout.NORTH);

        // Tạo bảng lịch hẹn
        String[] columns = {"Mã lịch hẹn", "Ngày hẹn", "Bác sĩ", "Lý do khám", "Trạng thái"};
        DefaultTableModel appointmentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp
            }
        };

        appointmentsTable = new JTable(appointmentModel);
        setupTable(appointmentsTable);

        JScrollPane tableScrollPane = new JScrollPane(appointmentsTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Tạo panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Tạo nút đặt lịch hẹn mới
        JButton addButton = new JButton("Đặt lịch hẹn mới");
        ImageIcon addIcon = loadIcon("/images/add.png");
        if (addIcon != null) {
            addButton.setIcon(addIcon);
        }
        addButton.addActionListener(e -> moHopThoaiDatLichHen());

        // Tạo nút hủy lịch hẹn
        JButton cancelButton = new JButton("Hủy lịch hẹn");
        ImageIcon cancelIcon = loadIcon("/images/cancel.png");
        if (cancelIcon != null) {
            cancelButton.setIcon(cancelIcon);
        }
        cancelButton.addActionListener(e -> huyLichHenDaChon());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Cập nhật dữ liệu cho bảng
        updateAppointmentsTable();  // Sử dụng updateAppointmentsTable thay vì refreshAppointmentsTable

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

        medicalRecordsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(medicalRecordsTable);

        // Cài đặt thuộc tính cho bảng
        setupTable(medicalRecordsTable);

        // Đặt độ rộng cụ thể cho từng cột
        medicalRecordsTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // Mã hồ sơ
        medicalRecordsTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Ngày khám
        medicalRecordsTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Bác sĩ
        medicalRecordsTable.getColumnModel().getColumn(3).setPreferredWidth(200);  // Chẩn đoán
        medicalRecordsTable.getColumnModel().getColumn(4).setPreferredWidth(180);  // Điều trị

        // Thêm sự kiện cho bảng lịch sử khám bệnh
        medicalRecordsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiChiTietHoSoKham();
            }
        });

        // Panel hiển thị chi tiết
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết khám bệnh"));

        detailsArea = new JTextArea(10, 30);
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

        prescriptionsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(prescriptionsTable);

        // Cài đặt thuộc tính cho bảng
        setupTable(prescriptionsTable);

        // Đặt độ rộng cụ thể cho từng cột
        prescriptionsTable.getColumnModel().getColumn(0).setPreferredWidth(120);  // Mã đơn thuốc
        prescriptionsTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Ngày kê
        prescriptionsTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Bác sĩ
        prescriptionsTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Số loại thuốc
        prescriptionsTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Trạng thái

        // Thêm sự kiện cho bảng đơn thuốc
        prescriptionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiChiTietDonThuoc();
            }
        });

        // Panel chi tiết đơn thuốc
        JPanel prescriptionDetailsPanel = new JPanel(new BorderLayout());
        prescriptionDetailsPanel.setBackground(Color.WHITE);
        prescriptionDetailsPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn thuốc"));

        String[] detailColumns = {"Tên thuốc", "Liều lượng", "Hướng dẫn", "Số lượng", "Giá"};
        Object[][] detailData = {};

        detailsTable = new JTable(detailData, detailColumns);
        JScrollPane detailsScrollPane = new JScrollPane(detailsTable);

        // Cài đặt thuộc tính cho bảng chi tiết
        setupTable(detailsTable);

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
        editInfoButton.addActionListener(e -> moHopThoaiChinhSuaThongTin());
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
        Object[][] data = {
                {"INV-001", "05/04/2025", "Khám bệnh", "500,000 VNĐ", "0 VNĐ", "500,000 VNĐ", "Chưa thanh toán"},
                {"INV-002", "03/04/2025", "Thuốc", "350,000 VNĐ", "0 VNĐ", "350,000 VNĐ", "Chưa thanh toán"}
        };

        invoicesTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(invoicesTable);

        // Cài đặt thuộc tính cho bảng
        setupTable(invoicesTable);

        // Đặt độ rộng cụ thể cho từng cột
        invoicesTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // Mã hóa đơn
        invoicesTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Ngày tạo
        invoicesTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Loại
        invoicesTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Số tiền
        invoicesTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Đã thanh toán
        invoicesTable.getColumnModel().getColumn(5).setPreferredWidth(120);  // Còn lại
        invoicesTable.getColumnModel().getColumn(6).setPreferredWidth(120);  // Trạng thái

        // Panel nút điều khiển
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        JButton viewDetailsBtn = new JButton("Xem chi tiết");
        JButton payInvoiceBtn = new JButton("Thanh toán");

        // Thêm sự kiện cho các nút
        viewDetailsBtn.addActionListener(e -> xemChiTietHoaDon());
        payInvoiceBtn.addActionListener(e -> xuLyThanhToan());

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
        JPanel navPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Tạo gradient từ đậm sang nhạt (từ trên xuống dưới)
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(25, 79, 115), // Màu đậm phía trên
                        0, getHeight(), new Color(52, 152, 219) // Màu nhạt phía dưới
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setPreferredSize(new Dimension(250, getHeight()));

        // Add avatar and user information
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setOpaque(false); // Để hiện gradient từ panel cha
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

                // Vẽ hình tròn avatar
                g2d.setColor(new Color(255, 255, 255));
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
        avatarPanel.setMaximumSize(new Dimension(100, 100));
        avatarPanel.setMinimumSize(new Dimension(100, 100));
        avatarPanel.setOpaque(false);
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
        homeButton = createNavButton("Tổng quan", e -> {
            refreshDashboard(); // Gọi refreshDashboard() trước để cập nhật dữ liệu mới nhất
            cardLayout.show(mainPanel, "dashboard");
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

        navPanel.add(userPanel);
        navPanel.add(Box.createVerticalStrut(20));
        navPanel.add(homeButton);
        navPanel.add(appointmentButton);
        navPanel.add(medicalHistoryButton);
        navPanel.add(prescriptionButton);
        navPanel.add(paymentsButton);
        navPanel.add(Box.createVerticalGlue());
        navPanel.add(logoutButton);
        navPanel.add(Box.createVerticalStrut(20));

        // Đặt mặc định nút trang chủ là active
        setActiveButton(homeButton);


        return navPanel;
    }

    // Biến để lưu button đang được active
    private JButton activeButton = null;

    // Phương thức để đặt button active
    private void setActiveButton(JButton button) {
        // Reset button cũ về trạng thái bình thường
        if (activeButton != null && activeButton != logoutButton) {
            activeButton.setBackground(new Color(41, 128, 185, 0)); // Transparent để thấy gradient
        }

        // Đặt button mới là active
        if (button != logoutButton) {
            button.setBackground(new Color(52, 152, 219));
            activeButton = button;
        }
    }

    private JButton createNavButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 45));
        button.setMaximumSize(new Dimension(250, 45));
        button.setMinimumSize(new Dimension(250, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185, 0)); // Transparent để thấy gradient
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false); // Để thấy gradient
        button.setContentAreaFilled(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMargin(new Insets(0, 25, 0, 0));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != activeButton) { // Chỉ đổi màu khi không phải là active button
                    if (button != logoutButton) {
                        button.setBackground(new Color(52, 152, 219));
                    } else {
                        button.setBackground(new Color(231, 76, 60).brighter());
                    }
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != activeButton) { // Chỉ đổi màu khi không phải là active button
                    if (button != logoutButton) {
                        button.setBackground(new Color(41, 128, 185, 0)); // Transparent để thấy gradient
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

    // Phương thức mở hộp thoại đặt lịch hẹn mới
    private void moHopThoaiDatLichHen() {
        // Tạo hộp thoại cho lịch hẹn mới
        JDialog dialog = new JDialog(this, "Đặt lịch hẹn mới", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Thêm các trường nhập liệu
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
        JComboBox<String> doctorComboBox = new JComboBox<>(new String[]{"Nguyễn Văn An", "Lê Văn Cường", "Trần Thị Bình"});
        panel.add(doctorComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Lý do khám:"), gbc);

        gbc.gridx = 1;
        JTextArea reasonArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(reasonArea);
        panel.add(scrollPane, gbc);

        // Thêm nút xác nhận
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton confirmButton = new JButton("Xác nhận đặt lịch");
        confirmButton.addActionListener(confirmEvent -> {
            // Kiểm tra dữ liệu đầu vào
            if (dateField.getText().trim().isEmpty() || timeField.getText().trim().isEmpty() || reasonArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Vui lòng điền đầy đủ thông tin",
                        "Thiếu thông tin",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Lưu thông tin lịch hẹn vào cơ sở dữ liệu
            // ...

            // Tạo mã lịch hẹn mới
            String newId = "AP" + String.format("%03d", (int)(Math.random() * 999));

            // Cập nhật bảng lịch hẹn
            DefaultTableModel model = (DefaultTableModel) appointmentsTable.getModel();
            if (!(model instanceof DefaultTableModel)) {
                model = new DefaultTableModel();
                model.setColumnIdentifiers(new String[]{"Mã lịch hẹn", "Ngày", "Giờ", "Bác sĩ", "Lý do khám", "Trạng thái"});
                appointmentsTable.setModel(model);
            }
            model.addRow(new Object[]{
                    newId,
                    dateField.getText(),
                    timeField.getText(),
                    doctorComboBox.getSelectedItem(),
                    reasonArea.getText(),
                    "Chờ xác nhận"
            });

            JOptionPane.showMessageDialog(dialog,
                    "Đã đặt lịch hẹn thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        refreshAppointmentsTable();
        panel.add(confirmButton, gbc);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Phương thức hủy lịch hẹn đã chọn
    private void huyLichHenDaChon() {
        int row = appointmentsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch hẹn cần hủy!");
            return;
        }

        // Lấy mã lịch hẹn từ bảng
        String appointmentID = appointmentsTable.getValueAt(row, 0).toString();
        String status = appointmentsTable.getValueAt(row, 4).toString();

        // Kiểm tra trạng thái
        if (!"Chờ".equals(status)) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể hủy lịch hẹn có trạng thái 'Chờ'!");
            return;
        }

        // Hiển thị hộp thoại xác nhận
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy lịch hẹn này không?",
                "Xác nhận hủy lịch hẹn", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection connection = getDBConnection();
                if (connection != null) {
                    // Cập nhật trạng thái lịch hẹn trong CSDL
                    String query = "UPDATE Appointments SET Status = 'Đã hủy' WHERE AppointmentID = ?";
                    PreparedStatement pstmt = connection.prepareStatement(query);
                    pstmt.setString(1, appointmentID);

                    int result = pstmt.executeUpdate();
                    pstmt.close();
                    connection.close();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Hủy lịch hẹn thành công!");

                        // Cập nhật trạng thái trên UI
                        appointmentsTable.setValueAt("Đã hủy", row, 4);

                        // Làm mới dashboard
                        updateDashboardStats();
                        refreshDashboard();
                    } else {
                        JOptionPane.showMessageDialog(this, "Không thể hủy lịch hẹn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Cập nhật UI trong trường hợp không có kết nối CSDL (dùng cho demo)
                    JOptionPane.showMessageDialog(this, "Hủy lịch hẹn thành công! (Chế độ Offline)");
                    appointmentsTable.setValueAt("Đã hủy", row, 4);

                    // Làm mới dashboard
                    updateDashboardStats();
                    refreshDashboard();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi hủy lịch hẹn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Phương thức hiển thị chi tiết hồ sơ khám bệnh
    private void hienThiChiTietHoSoKham() {
        int selectedRow = medicalRecordsTable.getSelectedRow();
        if (selectedRow != -1) {
            String maHoSo = (String) medicalRecordsTable.getValueAt(selectedRow, 0);

            // Lấy thông tin chi tiết từ cơ sở dữ liệu
            // MedicalRecord hoSo = ... (truy vấn từ cơ sở dữ liệu)

            // Hiển thị thông tin chi tiết trong vùng chi tiết
            detailsArea.setText(
                    "Mã hồ sơ: " + maHoSo + "\n\n" +
                            "Ngày khám: 03/04/2025\n" +
                            "Bác sĩ: Nguyễn Văn An\n\n" +
                            "Chẩn đoán: Tăng huyết áp\n\n" +
                            "Triệu chứng: Đau đầu, hoa mắt, chóng mặt\n\n" +
                            "Phương pháp điều trị: Dùng thuốc hạ áp kết hợp chế độ ăn giảm muối\n\n" +
                            "Lời dặn: Tái khám sau 2 tuần, theo dõi huyết áp hàng ngày"
            );
        }
    }

    // Phương thức hiển thị chi tiết đơn thuốc
    private void hienThiChiTietDonThuoc() {
        int selectedRow = prescriptionsTable.getSelectedRow();
        if (selectedRow != -1) {
            String maDonThuoc = (String) prescriptionsTable.getValueAt(selectedRow, 0);

            // Lấy thông tin chi tiết từ cơ sở dữ liệu
            // Prescription donThuoc = ... (truy vấn từ cơ sở dữ liệu)

            // Dữ liệu mẫu cho bảng chi tiết đơn thuốc
            Object[][] detailData = {
                    {"Paracetamol", "500mg", "Uống 2 viên/ngày sau ăn", "20", "100,000 VNĐ"},
                    {"Amoxicillin", "250mg", "Uống 3 viên/ngày sau ăn", "30", "150,000 VNĐ"},
                    {"Vitamin C", "1000mg", "Uống 1 viên/ngày", "10", "80,000 VNĐ"}
            };

            // Cập nhật bảng chi tiết
            DefaultTableModel model = new DefaultTableModel(
                    detailData,
                    new String[]{"Tên thuốc", "Liều lượng", "Hướng dẫn", "Số lượng", "Giá"}
            );

            detailsTable.setModel(model);

            // Cài đặt thuộc tính cho bảng chi tiết sau khi cập nhật model
            setupTable(detailsTable);
        }
    }

    // Phương thức cài đặt thuộc tính cho bảng để cố định các cột
    private void setupTable(JTable table) {
        // Đặt độ rộng cố định cho các cột
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // Đặt renderer cho các cột (để canh giữa dữ liệu)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Áp dụng cho tất cả các cột
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Không cho phép di chuyển các cột
        table.getTableHeader().setReorderingAllowed(false);

        // Không cho phép thay đổi kích thước cột
        table.getTableHeader().setResizingAllowed(false);
    }

    // Phương thức xử lý xem chi tiết hóa đơn
    private void xemChiTietHoaDon() {
        int selectedRow = invoicesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn hóa đơn để xem chi tiết",
                    "Chưa chọn hóa đơn",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy thông tin hóa đơn từ dòng đã chọn
        String maHoaDon = (String) invoicesTable.getValueAt(selectedRow, 0);
        String ngayTao = (String) invoicesTable.getValueAt(selectedRow, 1);
        String loai = (String) invoicesTable.getValueAt(selectedRow, 2);
        String soTien = (String) invoicesTable.getValueAt(selectedRow, 3);
        String trangThai = (String) invoicesTable.getValueAt(selectedRow, 6);

        // Mở hộp thoại chi tiết hóa đơn
        JDialog dialog = new JDialog(this, "Chi tiết hóa đơn", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel thông tin chung
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        infoPanel.add(new JLabel("Mã hóa đơn:"));
        infoPanel.add(new JLabel(maHoaDon));

        infoPanel.add(new JLabel("Ngày tạo:"));
        infoPanel.add(new JLabel(ngayTao));

        infoPanel.add(new JLabel("Loại:"));
        infoPanel.add(new JLabel(loai));

        infoPanel.add(new JLabel("Tổng tiền:"));
        infoPanel.add(new JLabel(soTien));

        infoPanel.add(new JLabel("Trạng thái:"));
        infoPanel.add(new JLabel(trangThai));

        // Bảng chi tiết các mục trong hóa đơn
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết các khoản"));

        String[] columns = {"STT", "Mô tả", "Đơn giá", "Số lượng", "Thành tiền"};
        Object[][] data;

        // Dựa vào loại hóa đơn mà hiển thị dữ liệu khác nhau
        if ("Khám bệnh".equals(loai)) {
            data = new Object[][] {
                    {"1", "Phí khám bệnh", "200,000 VNĐ", "1", "200,000 VNĐ"},
                    {"2", "Xét nghiệm máu", "150,000 VNĐ", "1", "150,000 VNĐ"},
                    {"3", "Siêu âm", "150,000 VNĐ", "1", "150,000 VNĐ"}
            };
        } else if ("Thuốc".equals(loai)) {
            data = new Object[][] {
                    {"1", "Paracetamol 500mg", "100,000 VNĐ", "1", "100,000 VNĐ"},
                    {"2", "Amoxicillin 250mg", "150,000 VNĐ", "1", "150,000 VNĐ"},
                    {"3", "Vitamin C 1000mg", "100,000 VNĐ", "1", "100,000 VNĐ"}
            };
        } else {
            data = new Object[][] {};
        }

        JTable detailTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(detailTable);

        // Cài đặt độ rộng cố định cho các cột
        detailTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // STT
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Mô tả
        detailTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Đơn giá
        detailTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Số lượng
        detailTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Thành tiền

        // Áp dụng cài đặt bảng
        setupTable(detailTable);

        detailPanel.add(scrollPane, BorderLayout.CENTER);

        // Thêm nút in hóa đơn
        JButton printButton = new JButton("In hóa đơn");
        printButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog,
                    "Đã gửi hóa đơn đến máy in",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Panel chứa nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(printButton);

        // Thêm các panel vào dialog
        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(detailPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Phương thức xử lý thanh toán
    private void xuLyThanhToan() {
        int selectedRow = invoicesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn hóa đơn cần thanh toán",
                    "Chưa chọn hóa đơn",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy thông tin hóa đơn từ dòng đã chọn
        String maHoaDon = (String) invoicesTable.getValueAt(selectedRow, 0);
        String soTien = (String) invoicesTable.getValueAt(selectedRow, 3);

        // Mở hộp thoại thanh toán
        JDialog dialog = new JDialog(this, "Thanh toán hóa đơn", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hiển thị thông tin hóa đơn
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Mã hóa đơn:"), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel(maHoaDon), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Số tiền:"), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel(soTien), gbc);

        // Thêm phương thức thanh toán
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Phương thức thanh toán:"), gbc);

        gbc.gridx = 1;
        String[] paymentMethods = {"Tiền mặt", "Thẻ ngân hàng", "Ví điện tử"};
        JComboBox<String> methodComboBox = new JComboBox<>(paymentMethods);
        panel.add(methodComboBox, gbc);

        // Các thông tin thêm cho phương thức thanh toán
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

        // Xử lý thay đổi phương thức thanh toán
        methodComboBox.addActionListener(e -> {
            String selected = (String) methodComboBox.getSelectedItem();
            bankCardPanel.setVisible("Thẻ ngân hàng".equals(selected));
            eWalletPanel.setVisible("Ví điện tử".equals(selected));
            dialog.pack(); // Điều chỉnh kích thước hộp thoại
            dialog.setSize(400, dialog.getHeight()); // Giữ chiều rộng cố định
        });

        // Thêm nút xác nhận thanh toán
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton confirmButton = new JButton("Xác nhận thanh toán");
        confirmButton.addActionListener(confirmEvent -> {
            // Xử lý logic thanh toán
            // ...

            // Cập nhật trạng thái hóa đơn trên bảng
            invoicesTable.setValueAt("Đã thanh toán", selectedRow, 6);
            invoicesTable.setValueAt(soTien, selectedRow, 4);
            invoicesTable.setValueAt("0 VNĐ", selectedRow, 5);

            JOptionPane.showMessageDialog(dialog,
                    "Thanh toán thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);

            dialog.dispose();
        });

        panel.add(confirmButton, gbc);

        refreshDashboard();
        dialog.add(panel);
        dialog.setVisible(true);
    }
    /**
     * Cập nhật dữ liệu cho bảng lịch hẹn từ cơ sở dữ liệu
     */
    private void updateAppointmentsTable() {
        try {
            // Kiểm tra nếu bảng appointmentsTable chưa được khởi tạo
            if (appointmentsTable == null) {
                System.err.println("Lỗi: Bảng lịch hẹn chưa được khởi tạo");
                return;
            }

            // Lấy model của bảng
            DefaultTableModel model = (DefaultTableModel) appointmentsTable.getModel();

            // Xóa dữ liệu cũ
            model.setRowCount(0);

            // Lấy ID của bệnh nhân hiện tại
            String patientID = currentUser != null ? currentUser.getId() : null;
            if (patientID == null) {
                System.err.println("Không có thông tin bệnh nhân");
                return;
            }

            Connection connection = getDBConnection();
            if (connection != null) {
                // Lấy danh sách lịch hẹn từ CSDL
                String query = "SELECT a.AppointmentID, a.AppointmentDate, d.FullName AS DoctorName, " +
                        "a.Reason, a.Status FROM Appointments a " +
                        "LEFT JOIN UserAccounts d ON a.DoctorID = d.UserID " +
                        "WHERE a.PatientID = ? ORDER BY a.AppointmentDate DESC";

                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, patientID);
                ResultSet rs = pstmt.executeQuery();

                // Thêm dữ liệu vào model
                while (rs.next()) {
                    String appointmentID = rs.getString("AppointmentID");
                    String appointmentDate = rs.getString("AppointmentDate");
                    String doctorName = rs.getString("DoctorName");
                    String description = rs.getString("Description");
                    String status = rs.getString("Status");

                    model.addRow(new Object[]{appointmentID, appointmentDate, doctorName, description, status});
                }

                rs.close();
                pstmt.close();
                connection.close();
            } else {
                // Dữ liệu mẫu cho trường hợp không có kết nối CSDL (demo)
                model.addRow(new Object[]{"A1001", "2023-06-15 09:00", "Bác sĩ Nguyễn Văn A", "Khám định kỳ", "Chờ"});
                model.addRow(new Object[]{"A1002", "2023-06-10 14:30", "Bác sĩ Trần Thị B", "Đau đầu", "Hoàn thành"});
                model.addRow(new Object[]{"A1003", "2023-05-28 10:15", "Bác sĩ Lê Văn C", "Tiêm vắc-xin", "Hoàn thành"});

                System.out.println("Sử dụng dữ liệu mẫu cho bảng lịch hẹn do không có kết nối CSDL");
            }

            // Cập nhật số lịch hẹn sắp tới cho dashboard
            updateUpcomingAppointmentCount();

        } catch (ClassCastException e) {
            System.err.println("Lỗi khi chuyển đổi kiểu của bảng: " + e.getMessage());

            // Khắc phục vấn đề về model
            String[] columns = {"Mã lịch hẹn", "Ngày hẹn", "Bác sĩ", "Lý do khám", "Trạng thái"};
            DefaultTableModel newModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            appointmentsTable.setModel(newModel);

            // Thử lại việc cập nhật dữ liệu
            updateAppointmentsTable();
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật bảng lịch hẹn: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi cập nhật bảng lịch hẹn: " + e.getMessage());
        }
    }

    /**
     * Cập nhật số lượng lịch hẹn sắp tới cho dashboard
     */
    private void updateUpcomingAppointmentCount() {
        try {
            upcomingAppointmentCount = 0;

            // Đếm số lịch hẹn có trạng thái "Chờ"
            DefaultTableModel model = (DefaultTableModel) appointmentsTable.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String status = model.getValueAt(i, 4).toString();
                if ("Chờ".equals(status)) {
                    upcomingAppointmentCount++;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật số lịch hẹn sắp tới: " + e.getMessage());
        }
    }
    // Khởi chạy ứng dụng (phương thức main cho test)
    public static void main(String[] args) {
        // Tạo user mẫu để test
        User testUser = new User();
        testUser.setFullName("Nguyễn Văn Bệnh");
        testUser.setDateOfBirth("01/01/1990");
        testUser.setGender("Nam");
        testUser.setAddress("123 Đường Nguyễn Văn A, Quận 1, TP.HCM");
        testUser.setPhone("0987654321");
        testUser.setEmail("benhnhan@example.com");
        testUser.setHasInsurance(true);
        testUser.setInsuranceId("BH12345678");
        testUser.setInsuranceExpDate("31/12/2025");

        SwingUtilities.invokeLater(() -> {
            PatientMainFrame frame = new PatientMainFrame(testUser);
            frame.setVisible(true);
        });
    }
}
