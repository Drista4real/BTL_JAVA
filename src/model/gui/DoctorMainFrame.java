package model.gui;

import javax.swing.*;
import java.awt.*;
import model.entity.User;
import java.awt.image.BufferedImage;
import javax.swing.table.DefaultTableModel;
import model.entity.DataManager;
import model.entity.Appointment;
import model.entity.Role;

public class DoctorMainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton currentButton;
    private User currentUser;
    private JLabel userNameLabel;
    private JLabel avatarLabel;
    private JPanel navPanel;

    public DoctorMainFrame(User user) {
        setTitle("Hệ thống quản lý bệnh nhân - Bác sĩ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        this.currentUser = user;

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(new DoctorDashboardPanel(), "DASHBOARD");
        mainPanel.add(new DoctorPatientListPanel(), "PATIENT_LIST");
        mainPanel.add(new DoctorAppointmentListPanel(), "APPOINTMENT_LIST");

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
        navPanel.setBackground(new Color(41, 128, 185));
        navPanel.setPreferredSize(new Dimension(250, getHeight()));

        // Avatar panel
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
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarPanel.add(userNameLabel);
        navPanel.add(avatarPanel);
        navPanel.add(Box.createVerticalStrut(20));

        JButton btnDashboard = createNavButton("Trang chủ");
        JButton btnPatientList = createNavButton("Danh sách bệnh nhân");
        JButton btnAppointmentList = createNavButton("Danh sách lịch hẹn");
        JButton btnLogout = createNavButton("Đăng xuất");

        btnDashboard.addActionListener(e -> showDashboard());
        btnPatientList.addActionListener(e -> showPatientList());
        btnAppointmentList.addActionListener(e -> showAppointmentList());
        btnLogout.addActionListener(e -> logout());

        navPanel.add(btnDashboard);
        navPanel.add(Box.createVerticalStrut(1));
        navPanel.add(btnPatientList);
        navPanel.add(Box.createVerticalStrut(1));
        navPanel.add(btnAppointmentList);
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
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 25, 0, 0));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != currentButton) {
                    button.setBackground(new Color(52, 152, 219));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != currentButton) {
                    button.setBackground(new Color(41, 128, 185));
                }
            }
        });
        return button;
    }

    private void updateButtonSelection(JButton selectedButton) {
        if (currentButton != null) {
            currentButton.setBackground(new Color(41, 128, 185));
        }
        selectedButton.setBackground(new Color(52, 152, 219));
        currentButton = selectedButton;
    }

    private void showDashboard() {
        cardLayout.show(mainPanel, "DASHBOARD");
    }
    private void showPatientList() {
        cardLayout.show(mainPanel, "PATIENT_LIST");
    }
    private void showAppointmentList() {
        cardLayout.show(mainPanel, "APPOINTMENT_LIST");
    }
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            MainFrame mainFrame = new MainFrame();
            mainFrame.setContentPane(new LoginPanel(mainFrame, Role.DOCTOR));
            mainFrame.revalidate();
            mainFrame.pack();
            mainFrame.setSize(1200, 700);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        }
    }

}

class DoctorDashboardPanel extends JPanel {
    public DoctorDashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        statsPanel.setBackground(Color.WHITE);

        // Box: Tổng số bệnh nhân
        JPanel totalPatientBox = createStatBox("Tổng số bệnh nhân", "120");
        // Box: Bệnh nhân BHYT
        JPanel bhytBox = createStatBox("Bệnh nhân BHYT", "45");
        // Box: Lịch hẹn hôm nay
        JPanel todayApptBox = createStatBox("Lịch hẹn hôm nay", "8");

        statsPanel.add(totalPatientBox);
        statsPanel.add(bhytBox);
        statsPanel.add(todayApptBox);

        // Thông báo mới
        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BorderLayout());
        notificationPanel.setBackground(Color.WHITE);
        notificationPanel.setBorder(BorderFactory.createTitledBorder("Thông báo mới"));
        JTextArea notificationArea = new JTextArea("- Bệnh nhân Nguyễn Văn A vừa đặt lịch khám mới.\n- Có 2 bệnh nhân cần chú ý đặc biệt hôm nay.");
        notificationArea.setEditable(false);
        notificationArea.setBackground(new Color(245, 245, 245));
        notificationArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notificationPanel.add(new JScrollPane(notificationArea), BorderLayout.CENTER);
        notificationPanel.setPreferredSize(new Dimension(0, 120));

        add(statsPanel, BorderLayout.NORTH);
        add(notificationPanel, BorderLayout.CENTER);
    }

    private JPanel createStatBox(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(new Color(41, 128, 185));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(60, 60, 60));
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.add(titleLabel, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(180, 100));
        return panel;
    }
}

class DoctorPatientListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;

    public DoctorPatientListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Top panel: search + filter + CRUD buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);
        searchField = new JTextField(18);
        searchField.setToolTipText("Tìm kiếm theo tên hoặc mã bệnh nhân");
        JButton searchBtn = new JButton("Tìm kiếm");
        filterComboBox = new JComboBox<>(new String[]{"Tất cả", "BHYT", "Không bảo hiểm"});
        JButton addBtn = new JButton("Thêm");
        JButton editBtn = new JButton("Sửa");
        JButton deleteBtn = new JButton("Xóa");
        JButton noteBtn = new JButton("Ghi chú/Bệnh tình");
        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(new JLabel("Loại bảo hiểm:"));
        topPanel.add(filterComboBox);
        topPanel.add(addBtn);
        topPanel.add(editBtn);
        topPanel.add(deleteBtn);
        topPanel.add(noteBtn);

        // Table
        String[] columns = {"Tên đăng nhập", "Họ tên", "Email", "Số điện thoại", "Loại bảo hiểm"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(table);

        reloadTable();

        // Sửa ghi chú/bệnh tình
        noteBtn.addActionListener(e -> editNoteIllness());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void reloadTable() {
        tableModel.setRowCount(0);
        for (User u : DataManager.getInstance().getUsers()) {
            tableModel.addRow(new Object[]{u.getUsername(), u.getFullName(), u.getEmail(), u.getPhone(), u.getRole()});
        }
    }

    private void editNoteIllness() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn bệnh nhân để sửa ghi chú/bệnh tình!");
            return;
        }
        String username = (String) tableModel.getValueAt(row, 0);
        User user = DataManager.getInstance().getUsers().stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
        if (user == null) return;
        JTextArea noteArea = new JTextArea(user.getNote());
        JTextArea illnessArea = new JTextArea(user.getIllnessInfo());
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.add(new JLabel("Ghi chú của bác sĩ:"));
        panel.add(new JScrollPane(noteArea));
        panel.add(new JLabel("Thông tin bệnh tình:"));
        panel.add(new JScrollPane(illnessArea));
        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa ghi chú/bệnh tình", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            user.setNote(noteArea.getText());
            user.setIllnessInfo(illnessArea.getText());
            DataManager.getInstance().updateUser(user);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        }
    }
}

class DoctorAppointmentListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;

    public DoctorAppointmentListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Top panel: search + filter + CRUD buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);
        searchField = new JTextField(18);
        searchField.setToolTipText("Tìm kiếm theo tên bệnh nhân hoặc mã lịch hẹn");
        JButton searchBtn = new JButton("Tìm kiếm");
        filterComboBox = new JComboBox<>(new String[]{"Tất cả", "Chờ khám", "Đã khám", "Hủy"});
        JButton addBtn = new JButton("Thêm");
        JButton editBtn = new JButton("Sửa");
        JButton deleteBtn = new JButton("Xóa");
        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(new JLabel("Trạng thái:"));
        topPanel.add(filterComboBox);
        topPanel.add(addBtn);
        topPanel.add(editBtn);
        topPanel.add(deleteBtn);

        // Table
        String[] columns = {"Mã lịch hẹn", "Tên bệnh nhân", "Bác sĩ", "Thời gian", "Trạng thái", "Ghi chú"};
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

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void reloadTable() {
        tableModel.setRowCount(0);
        for (Appointment appt : DataManager.getInstance().getAppointments()) {
            tableModel.addRow(new Object[]{
                appt.getId(), appt.getPatient(), appt.getDoctor(), appt.getDate() + " " + appt.getTime(), appt.getStatus(), appt.getReason()
            });
        }
    }
}