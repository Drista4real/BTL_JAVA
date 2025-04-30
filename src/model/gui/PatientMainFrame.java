package model.gui;

import javax.swing.*;
import java.awt.*;
import model.entity.User;
import java.awt.image.BufferedImage;
import javax.swing.table.DefaultTableModel;
import model.entity.DataManager;
import model.entity.Appointment;

public class PatientMainFrame extends JFrame {
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
        JLabel l1 = new JLabel("Họ tên:"); l1.setFont(labelFont); add(l1, gbc); gbc.gridx = 1;
        JLabel v1 = new JLabel(user.getFullName()); v1.setFont(valueFont); add(v1, gbc);
        gbc.gridx = 0; gbc.gridy++;
        JLabel l2 = new JLabel("Số điện thoại:"); l2.setFont(labelFont); add(l2, gbc); gbc.gridx = 1;
        JLabel v2 = new JLabel(user.getPhone()); v2.setFont(valueFont); add(v2, gbc);
        gbc.gridx = 0; gbc.gridy++;
        JLabel l4 = new JLabel("Ngày sinh:"); l4.setFont(labelFont); add(l4, gbc); gbc.gridx = 1;
        JLabel v4 = new JLabel(user.getDateOfBirth()); v4.setFont(valueFont); add(v4, gbc);
        gbc.gridx = 0; gbc.gridy++;
        JLabel l5 = new JLabel("Giới tính:"); l5.setFont(labelFont); add(l5, gbc); gbc.gridx = 1;
        JLabel v5 = new JLabel(user.getGender()); v5.setFont(valueFont); add(v5, gbc);
        gbc.gridx = 0; gbc.gridy++;
        JLabel l6 = new JLabel("Nơi cư trú:"); l6.setFont(labelFont); add(l6, gbc); gbc.gridx = 1;
        JLabel v6 = new JLabel(user.getAddress()); v6.setFont(valueFont); add(v6, gbc);
        gbc.gridx = 0; gbc.gridy++;
        JLabel l7 = new JLabel("Số CCCD:"); l7.setFont(labelFont); add(l7, gbc); gbc.gridx = 1;
        JLabel v7 = new JLabel(user.getCccd()); v7.setFont(valueFont); add(v7, gbc);
        gbc.gridx = 0; gbc.gridy++;
        JLabel l8 = new JLabel("Có giấy BHYT không:"); l8.setFont(labelFont); add(l8, gbc); gbc.gridx = 1;
        JLabel v8 = new JLabel(user.isHasInsurance() ? "Có" : "Không"); v8.setFont(valueFont); add(v8, gbc);
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        add(Box.createVerticalGlue(), gbc);
    }
}

class MedicalRecordPanel extends JPanel {
    public MedicalRecordPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Bệnh án"));
        JTextArea area = new JTextArea("Chức năng xem bệnh án đang phát triển.");
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        add(new JScrollPane(area), BorderLayout.CENTER);
    }
}

class PrescriptionPanel extends JPanel {
    public PrescriptionPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Đơn thuốc"));
        JTextArea area = new JTextArea("Chức năng xem đơn thuốc đang phát triển.");
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        add(new JScrollPane(area), BorderLayout.CENTER);
    }
}

class PaymentPanel extends JPanel {
    public PaymentPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Thanh toán"));
        JTextArea area = new JTextArea("Chức năng thanh toán đang phát triển.");
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        add(new JScrollPane(area), BorderLayout.CENTER);
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