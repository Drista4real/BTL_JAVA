package com.utc2.gui;

import javax.swing.*;
import java.awt.*;
import com.utc2.entity.User;
import java.awt.image.BufferedImage;
import javax.swing.table.DefaultTableModel;
import com.utc2.entity.DataManager;
import com.utc2.entity.Appointment;

public class PatientMainFrame extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public PatientMainFrame(User user) {
        this.currentUser = user;
        setTitle("Hệ thống quản lý bệnh nhân - Bệnh nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // Sidebar màu xanh nhạt hơn
        Color sidebarColor = new Color(93, 173, 226); // Xanh nhạt
        Color sidebarButtonColor = new Color(93, 173, 226); // Xanh nhạt hơn nữa
        Color sidebarButtonHover = new Color(100, 200, 230);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Avatar
        JLabel avatarLabel = new JLabel();
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setPreferredSize(new Dimension(120, 120));
        avatarLabel.setMaximumSize(new Dimension(120, 120));
        avatarLabel.setIcon(createAvatarIcon(user.getFullName()));
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(avatarLabel);

        // Tên
        JLabel nameLabel = new JLabel(user.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(nameLabel);
        sidebar.add(Box.createVerticalStrut(30));

        // Menu buttons
        String[] menu = {"Thông tin cá nhân", "Bệnh án", "Đơn thuốc", "Thanh toán", "Đặt lịch khám", "Đăng xuất"};
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(sidebarColor);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        JButton[] menuButtons = new JButton[menu.length];
        for (int i = 0; i < menu.length; i++) {
            menuButtons[i] = new JButton(menu[i]);
            menuButtons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            menuButtons[i].setMaximumSize(new Dimension(250, 48));
            menuButtons[i].setMinimumSize(new Dimension(250, 48));
            menuButtons[i].setPreferredSize(new Dimension(250, 48));
            menuButtons[i].setFont(new Font("Segoe UI", Font.BOLD, 16));
            menuButtons[i].setBackground(sidebarButtonColor);
            menuButtons[i].setForeground(Color.WHITE);
            menuButtons[i].setFocusPainted(false);
            menuButtons[i].setBorderPainted(false);
            menuButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            int idx = i;
            menuButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    menuButtons[idx].setBackground(sidebarButtonHover);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    menuButtons[idx].setBackground(sidebarButtonColor);
                }
            });
            menuPanel.add(menuButtons[i]);
            menuPanel.add(Box.createVerticalStrut(10));
        }
        sidebar.add(menuPanel);
        sidebar.add(Box.createVerticalGlue());

        // Nội dung chính (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(new PersonalInfoPanel(user), "Thông tin cá nhân");
        contentPanel.add(new MedicalRecordPanel(user), "Bệnh án");
        contentPanel.add(new PrescriptionPanel(user), "Đơn thuốc");
        contentPanel.add(new PaymentPanel(user), "Thanh toán");
        contentPanel.add(new AppointmentPanel(user), "Đặt lịch khám");

        // Sự kiện chuyển tab
        for (int i = 0; i < menu.length - 1; i++) {
            int idx = i;
            menuButtons[i].addActionListener(e -> cardLayout.show(contentPanel, menu[idx]));
        }
        // Đăng xuất
        menuButtons[menu.length - 1].addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                this.dispose();
                // Hiện màn hình đăng nhập nếu cần
            }
        });

        // Layout tổng
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "Thông tin cá nhân");
    }

    // Tạo avatar hình tròn với chữ cái đầu
    private Icon createAvatarIcon(String name) {
        int size = 120;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(0, 0, size, size);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 60));
        FontMetrics fm = g2d.getFontMetrics();
        String letter = name.substring(0, 1).toUpperCase();
        g2d.drawString(letter, (size - fm.stringWidth(letter)) / 2, ((size - fm.getHeight()) / 2) + fm.getAscent());
        g2d.dispose();
        return new ImageIcon(image);
    }
}

class PatientDashboardPanel extends JPanel {
    public PatientDashboardPanel(String username) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        com.utc2.entity.User user = com.utc2.entity.DataManager.getInstance().getUsers().stream()
            .filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
        String fullName = user != null ? user.getFullName() : "";
        String dob = ""; // Nếu có ngày sinh thì lấy ở đây
        String phone = user != null ? user.getPhone() : "";
        String note = user != null ? user.getNote() : "";
        String illness = user != null ? user.getIllnessInfo() : "";

        // Thông tin cá nhân
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin cá nhân"));
        infoPanel.add(new JLabel("Họ tên:"));
        infoPanel.add(new JLabel(fullName));
        infoPanel.add(new JLabel("Ngày sinh:"));
        infoPanel.add(new JLabel(dob));
        infoPanel.add(new JLabel("Số điện thoại:"));
        infoPanel.add(new JLabel(phone));

        // Ghi chú của bác sĩ
        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBackground(Color.WHITE);
        notePanel.setBorder(BorderFactory.createTitledBorder("Ghi chú của bác sĩ"));
        JTextArea noteArea = new JTextArea(note);
        noteArea.setEditable(false);
        noteArea.setBackground(new Color(245, 245, 245));
        noteArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notePanel.add(new JScrollPane(noteArea), BorderLayout.CENTER);
        notePanel.setPreferredSize(new Dimension(0, 80));

        // Thông tin bệnh tình
        JPanel illnessPanel = new JPanel(new BorderLayout());
        illnessPanel.setBackground(Color.WHITE);
        illnessPanel.setBorder(BorderFactory.createTitledBorder("Thông tin bệnh tình"));
        JTextArea illnessArea = new JTextArea(illness);
        illnessArea.setEditable(false);
        illnessArea.setBackground(new Color(245, 245, 245));
        illnessArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        illnessPanel.add(new JScrollPane(illnessArea), BorderLayout.CENTER);
        illnessPanel.setPreferredSize(new Dimension(0, 80));

        // Nút đặt lịch hẹn và thanh toán
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        actionPanel.setBackground(Color.WHITE);
        JButton bookBtn = new JButton("Đặt lịch hẹn");
        JButton payBtn = new JButton("Thanh toán");
        actionPanel.add(bookBtn);
        actionPanel.add(payBtn);

        // Sự kiện mẫu cho nút
        bookBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng đặt lịch hẹn!"));
        payBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng thanh toán!"));

        // Sắp xếp layout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(infoPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(notePanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(illnessPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(actionPanel);

        add(centerPanel, BorderLayout.CENTER);
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