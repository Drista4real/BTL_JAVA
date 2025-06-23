package model.gui;

import javax.swing.*;
import java.awt.*;
import model.entity.User;
import model.entity.Appointment;
import model.entity.DataManager;
import model.entity.Invoice;
import java.awt.image.BufferedImage;
import javax.swing.table.DefaultTableModel;
import model.entity.DataManager;
import model.entity.Appointment;

public class DoctorMainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton currentButton;
    private User currentUser;
    private JLabel userNameLabel;
    private JLabel avatarLabel;
    private JPanel navPanel;
    private DoctorAppointmentPanel appointmentPanel; // Thêm biến để lưu trữ panel
    private DoctorMedicalRecordPanel medicalRecordPanel; // Thêm biến để lưu trữ panel y tế
    private DoctorVitalSignsPanel vitalSignsPanel; // Thêm biến để lưu trữ panel dấu hiệu sinh tồn
    private DoctorPrescriptionPanel prescriptionPanel; // Thêm biến để lưu trữ panel đơn thuốc
    private DoctorPrescriptionDetailPanel prescriptionDetailPanel; // Thêm biến để lưu trữ panel chi tiết đơn thuốc
    private DoctorMedicationPanel medicationPanel; // Thêm biến để lưu trữ panel thuốc
    private DoctorHospitalRoomPanel hospitalRoomPanel; // Thêm biến để lưu trữ panel quản lý phòng bệnh
    private DoctorInvoicePanel invoicePanel; // Thêm biến để lưu trữ panel hóa đơn
    private DoctorInvoiceDetailPanel invoiceDetailPanel; // Thêm biến để lưu trữ panel chi tiết hóa đơn
    private DoctorPaymentPanel paymentPanel; // Thêm biến để lưu trữ panel thanh toán
    private String currentInvoiceId; // Thêm biến để lưu ID của hóa đơn hiện tại

    public DoctorMainFrame(User user) {
        this.currentUser = user;
        setTitle("Hệ thống quản lý bệnh nhân - Bác sĩ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Khởi tạo các panel
        JPanel dashboard = new DoctorDashboardPanel();
        JPanel patientList = new DoctorPatientListPanel();
        appointmentPanel = new DoctorAppointmentPanel(user); // Khởi tạo panel cuộc hẹn
        medicalRecordPanel = new DoctorMedicalRecordPanel(user); // Khởi tạo panel hồ sơ y tế
        vitalSignsPanel = new DoctorVitalSignsPanel(user); // Khởi tạo panel dấu hiệu sinh tồn
        prescriptionPanel = new DoctorPrescriptionPanel(user); // Khởi tạo panel đơn thuốc
        prescriptionDetailPanel = new DoctorPrescriptionDetailPanel(user); // Khởi tạo panel chi tiết đơn thuốc
        medicationPanel = new DoctorMedicationPanel(user); // Khởi tạo panel thuốc
        hospitalRoomPanel = new DoctorHospitalRoomPanel(user); // Khởi tạo panel quản lý phòng bệnh
        invoicePanel = new DoctorInvoicePanel(user); // Khởi tạo panel hóa đơn

        // Không thể khởi tạo invoiceDetailPanel và paymentPanel ở đây vì cần Invoice
        // Chúng sẽ được khởi tạo khi người dùng chọn một hóa đơn cụ thể

        // Thêm các panel vào cardLayout
        mainPanel.add(dashboard, "Dashboard");
        mainPanel.add(patientList, "PatientList");
        mainPanel.add(appointmentPanel, "AppointmentList");
        mainPanel.add(medicalRecordPanel, "MedicalRecord");
        mainPanel.add(vitalSignsPanel, "VitalSigns");
        mainPanel.add(prescriptionPanel, "Prescription");
        mainPanel.add(prescriptionDetailPanel, "PrescriptionDetail");
        mainPanel.add(medicationPanel, "Medication");
        mainPanel.add(hospitalRoomPanel, "HospitalRoom");
        mainPanel.add(invoicePanel, "Invoice");
        // InvoiceDetail và Payment sẽ được thêm vào khi cần

        JPanel navPanel = createNavPanel();

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(navPanel, BorderLayout.WEST);
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        add(contentPanel);

        showDashboard(); // Hiển thị dashboard mặc định
        // Giữ phần còn lại của constructor không thay đổi
    }

    private JPanel createNavPanel() {
        navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(41, 39, 40));
        navPanel.setPreferredSize(new Dimension(250, getHeight()));

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(new Color(41, 39, 40));
        logoPanel.setMaximumSize(new Dimension(250, 200));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel chứa thông tin người dùng
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(new Color(41, 39, 40));
        userPanel.setMaximumSize(new Dimension(250, 150));
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo avatar giả
        avatarLabel = new JLabel();
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        BufferedImage avatar = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = avatar.createGraphics();
        g.setColor(new Color(52, 152, 219));
        g.fillOval(0, 0, 80, 80);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString(Character.toString(currentUser.getFullName().charAt(0)), 30, 50);
        g.dispose();
        avatarLabel.setIcon(new ImageIcon(avatar));

        // Tên người dùng
        userNameLabel = new JLabel(currentUser.getFullName());
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Vai trò
        JLabel roleLabel = new JLabel(currentUser.getRole().getDisplayName());
        roleLabel.setForeground(Color.LIGHT_GRAY);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        userPanel.add(avatarLabel);
        userPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        userPanel.add(userNameLabel);
        userPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userPanel.add(roleLabel);

        logoPanel.add(userPanel);

        navPanel.add(logoPanel);
        navPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Các nút điều hướng
        JButton dashboardBtn = createNavButton("Trang chủ");
        dashboardBtn.addActionListener(e -> {
            updateButtonSelection(dashboardBtn);
            showDashboard();
        });
        currentButton = dashboardBtn; // Đặt nút hiện tại là dashboard
        updateButtonSelection(dashboardBtn); // Cập nhật trạng thái của nút

        JButton patientListBtn = createNavButton("Danh sách bệnh nhân");
        patientListBtn.addActionListener(e -> {
            updateButtonSelection(patientListBtn);
            showPatientList();
        });

        JButton appointmentBtn = createNavButton("Lịch hẹn");
        appointmentBtn.addActionListener(e -> {
            updateButtonSelection(appointmentBtn);
            showAppointmentList();
        });

        JButton medicalRecordBtn = createNavButton("Hồ sơ y tế");
        medicalRecordBtn.addActionListener(e -> {
            updateButtonSelection(medicalRecordBtn);
            showMedicalRecord();
        });

        JButton vitalSignsBtn = createNavButton("Dấu hiệu sinh tồn");
        vitalSignsBtn.addActionListener(e -> {
            updateButtonSelection(vitalSignsBtn);
            showVitalSigns();
        });

        JButton prescriptionBtn = createNavButton("Đơn thuốc");
        prescriptionBtn.addActionListener(e -> {
            updateButtonSelection(prescriptionBtn);
            showPrescription();
        });

        JButton prescriptionDetailBtn = createNavButton("Chi tiết đơn thuốc");
        prescriptionDetailBtn.addActionListener(e -> {
            updateButtonSelection(prescriptionDetailBtn);
            showPrescriptionDetail();
        });

        JButton medicationBtn = createNavButton("Quản lý thuốc");
        medicationBtn.addActionListener(e -> {
            updateButtonSelection(medicationBtn);
            showMedication();
        });

        JButton hospitalRoomBtn = createNavButton("Quản lý phòng bệnh");
        hospitalRoomBtn.addActionListener(e -> {
            updateButtonSelection(hospitalRoomBtn);
            showHospitalRoom();
        });

        JButton invoiceBtn = createNavButton("Hóa đơn");
        invoiceBtn.addActionListener(e -> {
            updateButtonSelection(invoiceBtn);
            showInvoice();
        });

        JButton logoutBtn = createNavButton("Đăng xuất");
        logoutBtn.addActionListener(e -> logout());

        navPanel.add(dashboardBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(patientListBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(appointmentBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(medicalRecordBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(vitalSignsBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(prescriptionBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(prescriptionDetailBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(medicationBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(hospitalRoomBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(invoiceBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Tạo một panel trống để đẩy nút đăng xuất xuống cuối
        navPanel.add(Box.createVerticalGlue());
        navPanel.add(logoutBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        return navPanel;
    }
    // Giữ các phương thức khác không thay đổi

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(250, 40));
        button.setBackground(new Color(41, 39, 40));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));

        return button;
    }

    private void updateButtonSelection(JButton selectedButton) {
        if (currentButton != null) {
            currentButton.setBackground(new Color(41, 39, 40));
        }
        selectedButton.setBackground(new Color(55, 55, 55));
        currentButton = selectedButton;
    }

    private void showDashboard() {
        cardLayout.show(mainPanel, "Dashboard");
    }
    private void showPatientList() {
        cardLayout.show(mainPanel, "PatientList");
    }

    private void showAppointmentList() {
        cardLayout.show(mainPanel, "AppointmentList");
    }

    private void showMedicalRecord() {
        cardLayout.show(mainPanel, "MedicalRecord");
    }

    private void showVitalSigns() {
        cardLayout.show(mainPanel, "VitalSigns");
    }
    private void showPrescription() {
        cardLayout.show(mainPanel, "Prescription");
    }

    private void showPrescriptionDetail() {
        cardLayout.show(mainPanel, "PrescriptionDetail");
    }

    private void showMedication() {
        cardLayout.show(mainPanel, "Medication");
    }

    private void showHospitalRoom() {
        cardLayout.show(mainPanel, "HospitalRoom");
    }
    private void showInvoice() {
        cardLayout.show(mainPanel, "Invoice");
    }

    // Phương thức để hiển thị chi tiết hóa đơn khi một hóa đơn được chọn
    public void showInvoiceDetail(Invoice invoice) {
        // Tạo panel chi tiết hóa đơn mới nếu chưa có hoặc nếu là hóa đơn khác
        if (invoiceDetailPanel == null || !invoice.getInvoiceId().equals(currentInvoiceId)) {
            currentInvoiceId = invoice.getInvoiceId();
            invoiceDetailPanel = new DoctorInvoiceDetailPanel(currentUser, invoice);
            // Xóa panel cũ nếu có
            try {
                mainPanel.remove(mainPanel.getComponent(mainPanel.getComponentCount() - 1));
            } catch (Exception ex) {
                // Không có gì để xóa, bỏ qua
            }
            mainPanel.add(invoiceDetailPanel, "InvoiceDetail");
        }
        cardLayout.show(mainPanel, "InvoiceDetail");
    }

    // Phương thức để hiển thị panel thanh toán khi người dùng muốn thanh toán
    public void showPayment(Invoice invoice) {
        // Tạo panel thanh toán mới nếu chưa có hoặc nếu là hóa đơn khác
        if (paymentPanel == null || !invoice.getInvoiceId().equals(currentInvoiceId)) {
            currentInvoiceId = invoice.getInvoiceId();
            // Lấy danh sách các item từ invoice
            java.util.List<Invoice.InvoiceItem> items = invoice.getItems();
            // Tạo DoctorPaymentPanel với 3 tham số
            paymentPanel = new DoctorPaymentPanel(currentUser, invoice, items);
            // Xóa panel cũ nếu có
            try {
                mainPanel.remove(mainPanel.getComponent(mainPanel.getComponentCount() - 1));
            } catch (Exception ex) {
                // Không có gì để xóa, bỏ qua
            }
            mainPanel.add(paymentPanel, "Payment");
        }
        cardLayout.show(mainPanel, "Payment");
    }
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất không?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            MainFrame loginFrame = new MainFrame();
            loginFrame.setVisible(true);
        }
    }


}

// Cập nhật các class con để sử dụng DB_URL, DB_USER, DB_PASSWORD từ DoctorMainFrame
class DoctorDashboardPanel extends JPanel {
    public DoctorDashboardPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Tổng quan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        statsPanel.add(createStatBox("Tổng số bệnh nhân", "24"));
        statsPanel.add(createStatBox("Cuộc hẹn hôm nay", "5"));
        statsPanel.add(createStatBox("Đang chờ khám", "3"));

        add(statsPanel, BorderLayout.CENTER);
    }

    private JPanel createStatBox(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setBackground(Color.WHITE);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(valueLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(titleLabel);

        return panel;
    }
    // Sửa để sử dụng DoctorMainFrame.DB_URL, DoctorMainFrame.DB_USER, DoctorMainFrame.DB_PASSWORD thay cho giá trị cứng
}

class DoctorPatientListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;

    public DoctorPatientListPanel() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Danh sách bệnh nhân");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Tạo panel tìm kiếm và lọc
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm bệnh nhân...");

        JPanel searchFieldPanel = new JPanel(new BorderLayout());
        searchFieldPanel.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Tìm kiếm");
        searchFieldPanel.add(searchButton, BorderLayout.EAST);

        searchPanel.add(searchFieldPanel, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Lọc theo:"));
        filterComboBox = new JComboBox<>(new String[]{"Tất cả", "Mới nhất", "Cũ nhất"});
        filterPanel.add(filterComboBox);

        searchPanel.add(filterPanel, BorderLayout.EAST);

        add(searchPanel, BorderLayout.NORTH);

        // Tạo bảng dữ liệu
        tableModel = new DefaultTableModel(
                new Object[][]{
                        {"BN001", "Nguyễn Văn A", "Nam", "01/01/1980", "0901234567", "Huyết áp cao"},
                        {"BN002", "Trần Thị B", "Nữ", "15/05/1992", "0912345678", "Tiểu đường"},
                        {"BN003", "Lê Văn C", "Nam", "20/11/1985", "0923456789", "Đau lưng mãn tính"},
                        {"BN004", "Phạm Thị D", "Nữ", "10/07/1975", "0934567890", "Viêm khớp"},
                        {"BN005", "Hoàng Văn E", "Nam", "05/03/1990", "0945678901", "Dị ứng"}
                },
                new String[]{
                        "Mã BN", "Họ và tên", "Giới tính", "Ngày sinh", "Số điện thoại", "Ghi chú"
                }
        );

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel chứa các nút thao tác
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewButton = new JButton("Xem chi tiết");
        JButton editButton = new JButton("Ghi chú bệnh án");
        JButton deleteButton = new JButton("Xóa");

        actionPanel.add(viewButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);

        add(actionPanel, BorderLayout.SOUTH);

        // Thêm các xử lý sự kiện
        editButton.addActionListener(e -> editNoteIllness());
    }

    private void reloadTable() {
        // Giả lập tải lại dữ liệu từ cơ sở dữ liệu
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"BN001", "Nguyễn Văn A", "Nam", "01/01/1980", "0901234567", "Huyết áp cao"});
        tableModel.addRow(new Object[]{"BN002", "Trần Thị B", "Nữ", "15/05/1992", "0912345678", "Tiểu đường"});
        tableModel.addRow(new Object[]{"BN003", "Lê Văn C", "Nam", "20/11/1985", "0923456789", "Đau lưng mãn tính"});
        tableModel.addRow(new Object[]{"BN004", "Phạm Thị D", "Nữ", "10/07/1975", "0934567890", "Viêm khớp"});
        tableModel.addRow(new Object[]{"BN005", "Hoàng Văn E", "Nam", "05/03/1990", "0945678901", "Dị ứng"});
    }

    private void editNoteIllness() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bệnh nhân.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String patientId = (String) table.getValueAt(selectedRow, 0);
        String patientName = (String) table.getValueAt(selectedRow, 1);
        String note = (String) table.getValueAt(selectedRow, 5);

        JTextArea noteArea = new JTextArea(note, 10, 30);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(noteArea);

        int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                "Ghi chú bệnh án cho " + patientName + " (" + patientId + ")",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            tableModel.setValueAt(noteArea.getText(), selectedRow, 5);
            JOptionPane.showMessageDialog(this, "Đã lưu ghi chú bệnh án.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

class DoctorAppointmentListPanel extends JPanel {
    // Sửa để sử dụng DoctorMainFrame.DB_URL, DoctorMainFrame.DB_USER, DoctorMainFrame.DB_PASSWORD thay cho giá trị cứng
}