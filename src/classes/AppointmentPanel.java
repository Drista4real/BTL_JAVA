package classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import model.entity.User;

public class AppointmentPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;

    // Fields cho phần quản lý nhập viện
    private JTextField admissionIdField;
    private JTextField patientIdField;
    private JTextField admissionDateField;
    private JTextField doctorIdField;
    private JTextField roomIdField;
    private JTextField dischargeDateField;
    private JTextArea notesArea;
    private JList<Admission> admissionList;
    private DefaultListModel<Admission> admissionListModel;

    public AppointmentPanel(User user) {
        setLayout(new BorderLayout(10, 10));

        // Tạo tabbed pane để chứa cả hai chức năng
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab đặt lịch khám
        JPanel appointmentTab = createAppointmentPanel();
        tabbedPane.addTab("Đặt lịch khám", appointmentTab);

        // Tab quản lý nhập viện
        JPanel admissionTab = createAdmissionPanel();
        tabbedPane.addTab("Quản lý nhập viện", admissionTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Đặt lịch khám"));
        panel.setBackground(Color.WHITE);

        // Form nhập thông tin lịch khám
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField dateField = new JTextField(10);
        JTextField timeField = new JTextField(8);
        JTextField doctorField = new JTextField(15);
        JTextField reasonField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Ngày (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; formPanel.add(dateField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Giờ (HH:mm):"), gbc);
        gbc.gridx = 1; formPanel.add(timeField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Bác sĩ:"), gbc);
        gbc.gridx = 1; formPanel.add(doctorField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Lý do:"), gbc);
        gbc.gridx = 1; formPanel.add(reasonField, gbc);

        JButton bookBtn = new JButton("Đặt lịch mới");
        bookBtn.setBackground(new Color(41, 128, 185));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(bookBtn, gbc);

        // Bảng lịch hẹn
        String[] columns = {"Ngày", "Giờ", "Bác sĩ", "Lý do"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(table);

        // Sự kiện đặt lịch
        bookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateField.getText().trim();
                String time = timeField.getText().trim();
                String doctor = doctorField.getText().trim();
                String reason = reasonField.getText().trim();
                if (date.isEmpty() || time.isEmpty() || doctor.isEmpty() || reason.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Vui lòng nhập đầy đủ thông tin!");
                    return;
                }
                tableModel.addRow(new Object[]{date, time, doctor, reason});
                dateField.setText("");
                timeField.setText("");
                doctorField.setText("");
                reasonField.setText("");
                JOptionPane.showMessageDialog(panel, "Đặt lịch thành công!");
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAdmissionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel mainContent = new JPanel(new BorderLayout());

        // Panel bên trái - danh sách bệnh nhân nhập viện
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Danh sách bệnh nhân nhập viện"));

        admissionListModel = new DefaultListModel<>();
        // Thêm dữ liệu mẫu
        LocalDate today = LocalDate.now();
        admissionListModel.addElement(new Admission("ADM001", "PT001", today, "DOC001", "R101", null, "Theo dõi"));
        admissionListModel.addElement(new Admission("ADM002", "PT002", today.minusDays(5), "DOC002", "R102", today, "Điều trị xong"));

        admissionList = new JList<>(admissionListModel);
        admissionList.setCellRenderer(new AdmissionListCellRenderer());
        admissionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(admissionList);

        // Thêm các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Thêm mới");
        JButton deleteButton = new JButton("Xóa");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Panel bên phải - chi tiết thông tin nhập viện
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết thông tin nhập viện"));

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Tạo các trường nhập liệu
        admissionIdField = new JTextField(15);
        patientIdField = new JTextField(15);
        admissionDateField = new JTextField(15);
        doctorIdField = new JTextField(15);
        roomIdField = new JTextField(15);
        dischargeDateField = new JTextField(15);
        notesArea = new JTextArea(5, 20);
        notesArea.setLineWrap(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);

        // Thêm các trường vào panel
        gbc.gridx = 0; gbc.gridy = 0; detailsPanel.add(new JLabel("Mã nhập viện:"), gbc);
        gbc.gridx = 1; detailsPanel.add(admissionIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; detailsPanel.add(new JLabel("Mã bệnh nhân:"), gbc);
        gbc.gridx = 1; detailsPanel.add(patientIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; detailsPanel.add(new JLabel("Ngày nhập viện:"), gbc);
        gbc.gridx = 1; detailsPanel.add(admissionDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; detailsPanel.add(new JLabel("Mã bác sĩ:"), gbc);
        gbc.gridx = 1; detailsPanel.add(doctorIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; detailsPanel.add(new JLabel("Mã phòng:"), gbc);
        gbc.gridx = 1; detailsPanel.add(roomIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; detailsPanel.add(new JLabel("Ngày xuất viện:"), gbc);
        gbc.gridx = 1; detailsPanel.add(dischargeDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; detailsPanel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridheight = 2; detailsPanel.add(notesScrollPane, gbc);

        rightPanel.add(detailsPanel, BorderLayout.CENTER);

        // Nút lưu thông tin
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu thông tin");
        savePanel.add(saveButton);
        rightPanel.add(savePanel, BorderLayout.SOUTH);

        // Xử lý sự kiện khi chọn một bệnh nhân trong danh sách
        admissionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && admissionList.getSelectedValue() != null) {
                Admission selectedAdmission = admissionList.getSelectedValue();
                displayAdmissionDetails(selectedAdmission);
            }
        });

        // Xử lý sự kiện khi nhấn nút lưu
        saveButton.addActionListener(e -> {
            try {
                saveAdmissionDetails();
                JOptionPane.showMessageDialog(panel,
                        "Lưu thông tin thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Xử lý sự kiện khi nhấn nút thêm mới
        addButton.addActionListener(e -> {
            clearForm();
            admissionIdField.setText("ADM" + String.format("%03d", admissionListModel.getSize() + 1));
            admissionDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        });

        // Xử lý sự kiện khi nhấn nút xóa
        deleteButton.addActionListener(e -> {
            if (admissionList.getSelectedValue() != null) {
                int confirm = JOptionPane.showConfirmDialog(panel,
                        "Bạn có chắc chắn muốn xóa thông tin nhập viện này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    admissionListModel.removeElement(admissionList.getSelectedValue());
                    clearForm();
                }
            }
        });

        // Phân chia không gian
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400);
        mainContent.add(splitPane, BorderLayout.CENTER);

        panel.add(mainContent, BorderLayout.CENTER);

        return panel;
    }

    // Các phương thức hỗ trợ cho quản lý nhập viện
    private void displayAdmissionDetails(Admission admission) {
        admissionIdField.setText(admission.getAdmissionId());
        patientIdField.setText(admission.getPatientId());
        admissionDateField.setText(admission.getAdmissionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        doctorIdField.setText(admission.getDoctorId());
        roomIdField.setText(admission.getRoomId());
        dischargeDateField.setText(admission.getDischargeDate() != null ?
                admission.getDischargeDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        notesArea.setText(admission.getNotes());
    }

    private void saveAdmissionDetails() {
        int selectedIndex = admissionList.getSelectedIndex();

        LocalDate admissionDate = LocalDate.parse(admissionDateField.getText(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        LocalDate dischargeDate = null;
        if (dischargeDateField.getText() != null && !dischargeDateField.getText().isEmpty()) {
            dischargeDate = LocalDate.parse(dischargeDateField.getText(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        // Tạo đối tượng Admission từ dữ liệu form
        Admission updatedAdmission = new Admission(
                admissionIdField.getText(),
                patientIdField.getText(),
                admissionDate,
                doctorIdField.getText(),
                roomIdField.getText(),
                dischargeDate,
                notesArea.getText()
        );

        if (selectedIndex >= 0) {
            // Cập nhật thông tin
            admissionListModel.set(selectedIndex, updatedAdmission);
        } else {
            // Thêm mới
            admissionListModel.addElement(updatedAdmission);
        }
    }

    private void clearForm() {
        admissionIdField.setText("");
        patientIdField.setText("");
        admissionDateField.setText("");
        doctorIdField.setText("");
        roomIdField.setText("");
        dischargeDateField.setText("");
        notesArea.setText("");
    }

    // Lớp hiển thị nội dung trong JList
    private class AdmissionListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Admission) {
                Admission admission = (Admission) value;
                return super.getListCellRendererComponent(
                        list,
                        String.format("%s - %s - %s",
                                admission.getAdmissionId(),
                                admission.getPatientId(),
                                admission.getAdmissionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                        index,
                        isSelected,
                        cellHasFocus);
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    // Lớp mô hình dữ liệu Admission
    public static class Admission {
        private String admissionId;
        private String patientId;
        private LocalDate admissionDate;
        private String doctorId;
        private String roomId;
        private LocalDate dischargeDate;
        private String notes;

        public Admission(String admissionId, String patientId, LocalDate admissionDate,
                         String doctorId, String roomId, LocalDate dischargeDate, String notes) {
            this.admissionId = admissionId;
            this.patientId = patientId;
            this.admissionDate = admissionDate;
            this.doctorId = doctorId;
            this.roomId = roomId;
            this.dischargeDate = dischargeDate;
            this.notes = notes;
        }

        public String getAdmissionId() { return admissionId; }
        public void setAdmissionId(String admissionId) { this.admissionId = admissionId; }

        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }

        public LocalDate getAdmissionDate() { return admissionDate; }
        public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }

        public String getDoctorId() { return doctorId; }
        public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

        public String getRoomId() { return roomId; }
        public void setRoomId(String roomId) { this.roomId = roomId; }

        public LocalDate getDischargeDate() { return dischargeDate; }
        public void setDischargeDate(LocalDate dischargeDate) { this.dischargeDate = dischargeDate; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}