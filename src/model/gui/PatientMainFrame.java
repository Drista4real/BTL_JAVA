package model.gui;

import javax.swing.*;
import java.awt.*;
import model.entity.User;
import java.awt.image.BufferedImage;
import javax.swing.table.DefaultTableModel;
import model.entity.DataManager;
import model.entity.Appointment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;

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

    public PatientMainFrame(User user) {
        setTitle("Hệ thống quản lý bệnh nhân - Bệnh nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        this.currentUser = user;

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(new PersonalInfoPanel(currentUser), "PERSONAL_INFO");
        mainPanel.add(new MedicalRecordPanel(currentUser), "MEDICAL_RECORD");
        mainPanel.add(new PrescriptionPanel(currentUser), "PRESCRIPTION");
        mainPanel.add(new PaymentPanel(currentUser), "PAYMENT");
        mainPanel.add(new PatientAppointmentPanel(currentUser.getUsername()), "APPOINTMENT");

        navPanel = createNavPanel();
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        showPersonalInfo();
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
        BufferedImage image = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(0, 0, 120, 120);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 60));
        FontMetrics fm = g2d.getFontMetrics();
        String letter = currentUser.getFullName().substring(0, 1).toUpperCase();
        g2d.drawString(letter, (120 - fm.stringWidth(letter)) / 2, ((120 - fm.getHeight()) / 2) + fm.getAscent());
        g2d.dispose();
        avatarLabel.setIcon(new ImageIcon(image));
        avatarPanel.add(Box.createVerticalStrut(30));
        avatarPanel.add(avatarLabel);
        avatarPanel.add(Box.createVerticalStrut(15));
        userNameLabel = new JLabel(currentUser.getFullName());
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

        btnPersonalInfo.addActionListener(e -> showPersonalInfo());
        btnMedicalRecord.addActionListener(e -> showMedicalRecord());
        btnPrescription.addActionListener(e -> showPrescription());
        btnPayment.addActionListener(e -> showPayment());
        btnAppointment.addActionListener(e -> showAppointment());
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
        cardLayout.show(mainPanel, "MEDICAL_RECORD");
    }
    private void showPrescription() {
        cardLayout.show(mainPanel, "PRESCRIPTION");
    }
    private void showPayment() {
        cardLayout.show(mainPanel, "PAYMENT");
    }
    private void showAppointment() {
        cardLayout.show(mainPanel, "APPOINTMENT");
    }
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            JFrame loginFrame = new JFrame();
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setSize(1200, 700);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setContentPane(new LoginPanel(null));
            loginFrame.setVisible(true);
        }
    }
}

class PersonalInfoPanel extends JPanel {
    public PersonalInfoPanel(User user) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createEmptyBorder(20, 30, 20, 30),
            javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)
        ));
        Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 15);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Họ tên:"), gbc); gbc.gridx = 1;
        add(new JLabel(user.getFullName()), gbc);
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Số điện thoại:"), gbc); gbc.gridx = 1;
        add(new JLabel(user.getPhone()), gbc);
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Ngày sinh:"), gbc); gbc.gridx = 1;
        add(new JLabel(user.getDateOfBirth()), gbc);
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Giới tính:"), gbc); gbc.gridx = 1;
        add(new JLabel(user.getGender()), gbc);
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Nơi cư trú:"), gbc); gbc.gridx = 1;
        add(new JLabel(user.getAddress()), gbc);
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Số CCCD:"), gbc); gbc.gridx = 1;
        add(new JLabel(user.getCccd()), gbc);
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Có giấy BHYT không:"), gbc); gbc.gridx = 1;
        add(new JLabel(user.isHasInsurance() ? "Có" : "Không"), gbc);
        // Nếu muốn cho phép cập nhật, thêm nút ở đây
    }
}

class MedicalRecordPanel extends JPanel {
    public MedicalRecordPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Bệnh án"));
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        String recordText = getMedicalRecordFromDB(user.getUsername());
        area.setText(recordText);
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    private String getMedicalRecordFromDB(String patientId) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM MedicalRecords WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                sb.append("Mã hồ sơ: ").append(rs.getString("RecordID")).append("\n");
                sb.append("Ngày tạo: ").append(rs.getDate("RecordDate")).append("\n");
                sb.append("Chẩn đoán: ").append(rs.getString("Diagnosis")).append("\n");
                sb.append("Điều trị: ").append(rs.getString("Treatment")).append("\n");
                sb.append("Ghi chú: ").append(rs.getString("Notes")).append("\n");
            } else {
                sb.append("Chưa có bệnh án.");
            }
        } catch (Exception e) {
            sb.append("Lỗi khi lấy bệnh án: ").append(e.getMessage());
        }
        return sb.toString();
    }
}

class PrescriptionPanel extends JPanel {
    public PrescriptionPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Đơn thuốc"));
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        String prescriptions = getPrescriptionsFromDB(user.getUsername());
        area.setText(prescriptions);
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    private String getPrescriptionsFromDB(String patientId) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM Prescriptions WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append("Đơn thuốc #").append(count).append("\n");
                sb.append("Mã đơn: ").append(rs.getString("PrescriptionID")).append("\n");
                sb.append("Ngày kê: ").append(rs.getDate("PrescriptionDate")).append("\n");
                sb.append("Bác sĩ: ").append(rs.getString("DoctorID")).append("\n");
                // Có thể truy vấn PrescriptionDetail để lấy chi tiết thuốc
                sb.append("----------------------\n");
            }
            if (count == 0) sb.append("Chưa có đơn thuốc.");
        } catch (Exception e) {
            sb.append("Lỗi khi lấy đơn thuốc: ").append(e.getMessage());
        }
        return sb.toString();
    }
}

class PaymentPanel extends JPanel {
    public PaymentPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Thanh toán"));
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        String invoices = getInvoicesFromDB(user.getUsername());
        area.setText(invoices);
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    private String getInvoicesFromDB(String patientId) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM Invoice WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append("Hóa đơn #").append(count).append("\n");
                sb.append("Mã hóa đơn: ").append(rs.getString("InvoiceID")).append("\n");
                sb.append("Ngày tạo: ").append(rs.getDate("CreatedDate")).append("\n");
                sb.append("Tổng tiền: ").append(rs.getDouble("TotalAmount")).append("\n");
                sb.append("Trạng thái: ").append(rs.getString("Status")).append("\n");
                sb.append("----------------------\n");
            }
            if (count == 0) sb.append("Chưa có hóa đơn.");
        } catch (Exception e) {
            sb.append("Lỗi khi lấy hóa đơn: ").append(e.getMessage());
        }
        return sb.toString();
    }
}

class PatientAppointmentPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton bookBtn;
    private JButton payBtn;
    private String currentPatient;

    public PatientAppointmentPanel(String patientUsername) {
        this.currentPatient = patientUsername;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Top panel: nút đặt lịch và thanh toán
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        topPanel.setBackground(Color.WHITE);
        bookBtn = new JButton("Đặt lịch mới");
        payBtn = new JButton("Thanh toán");
        topPanel.add(bookBtn);
        topPanel.add(payBtn);

        // Table
        String[] columns = {"Mã lịch hẹn", "Ngày", "Giờ", "Bác sĩ", "Lý do", "Trạng thái", "Thanh toán"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(table);

        // Load data
        reloadTable();

        // Sự kiện đặt lịch mới
        bookBtn.addActionListener(e -> showBookDialog());
        // Sự kiện thanh toán
        payBtn.addActionListener(e -> paySelectedAppointment());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void reloadTable() {
        tableModel.setRowCount(0);
        for (Appointment appt : DataManager.getInstance().getAppointments()) {
            if (appt.getPatient().equals(currentPatient)) {
                tableModel.addRow(new Object[]{
                    appt.getId(), appt.getDate(), appt.getTime(), appt.getDoctor(), appt.getReason(), appt.getStatus(), appt.getPaymentStatus()
                });
            }
        }
    }

    private void showBookDialog() {
        JTextField dateField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField doctorField = new JTextField();
        JTextField reasonField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        panel.add(new JLabel("Ngày (dd/MM/yyyy):"));
        panel.add(dateField);
        panel.add(new JLabel("Giờ (HH:mm):"));
        panel.add(timeField);
        panel.add(new JLabel("Bác sĩ:"));
        panel.add(doctorField);
        panel.add(new JLabel("Lý do:"));
        panel.add(reasonField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Đặt lịch mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            String doctor = doctorField.getText().trim();
            String reason = reasonField.getText().trim();
            if (date.isEmpty() || time.isEmpty() || doctor.isEmpty() || reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            String id = "LH" + (100 + DataManager.getInstance().getAppointments().size() + 1);
            Appointment appt = new Appointment(id, date, time, doctor, currentPatient, reason, "Chờ khám", "Chưa thanh toán");
            DataManager.getInstance().addAppointment(appt);
            reloadTable();
            JOptionPane.showMessageDialog(this, "Đặt lịch thành công!");
        }
    }

    private void paySelectedAppointment() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch hẹn để thanh toán!");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        for (Appointment appt : DataManager.getInstance().getAppointments()) {
            if (appt.getId().equals(id)) {
                if ("Đã thanh toán".equals(appt.getPaymentStatus())) {
                    JOptionPane.showMessageDialog(this, "Lịch hẹn này đã được thanh toán!");
                    return;
                }
                appt.setPaymentStatus("Đã thanh toán");
                DataManager.getInstance().updateAppointment(appt);
                reloadTable();
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                return;
            }
        }
    }
} 