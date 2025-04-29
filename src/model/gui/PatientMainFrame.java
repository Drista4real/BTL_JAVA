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
        mainPanel.add(new PatientDashboardPanel(currentUser.getUsername()), "DASHBOARD");
        mainPanel.add(new PatientAppointmentPanel(currentUser.getUsername()), "APPOINTMENT");

        navPanel = createNavPanel();
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        showDashboard();
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

        JButton btnDashboard = createNavButton("Trang chủ");
        JButton btnAppointment = createNavButton("Đặt lịch hẹn");
        JButton btnLogout = createNavButton("Đăng xuất");

        btnDashboard.addActionListener(e -> showDashboard());
        btnAppointment.addActionListener(e -> showAppointment());
        btnLogout.addActionListener(e -> logout());

        navPanel.add(btnDashboard);
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

    private void showDashboard() {
        cardLayout.show(mainPanel, "DASHBOARD");
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

class PatientDashboardPanel extends JPanel {
    public PatientDashboardPanel(String username) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        User user = DataManager.getInstance().getUsers().stream()
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