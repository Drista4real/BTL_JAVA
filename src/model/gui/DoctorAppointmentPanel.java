package model.gui;

import model.entity.Admission;
import model.entity.Appointment;
import model.entity.MedicalRecord;
import model.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Appointment> appointments;
    private JTextField admissionIdField;
    private JTextField patientIdField;
    private JTextField admissionDateField;
    private JTextField doctorIdField;
    private JTextField roomIdField;
    private JTextField dischargeDateField;
    private JTextArea notesArea;
    private JList<Admission> admissionList;
    private DefaultListModel<Admission> admissionListModel;
    private MedicalRecord medicalRecord; // Để tạo MedicalEntry khi hoàn thành lịch hẹn
    private final User currentUser;

    public DoctorAppointmentPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        appointments = new ArrayList<>();
        // Giả định medicalRecord sẽ được tải từ cơ sở dữ liệu hoặc khởi tạo tạm thời
        // Trong thực tế, cần tải MedicalRecord dựa trên patientId
        medicalRecord = new MedicalRecord(user.getUsername()); // Cần thay bằng tải thực tế

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel appointmentTab = createAppointmentPanel();
        tabbedPane.addTab("Đặt lịch khám", appointmentTab);
        JPanel admissionTab = createAdmissionPanel();
        tabbedPane.addTab("Quản lý nhập viện", admissionTab);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Đặt lịch khám"));
        panel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField(10);
        JTextField dateField = new JTextField(10);
        JTextField timeField = new JTextField(8);
        JTextField doctorField = new JTextField(15);
        JTextField patientField = new JTextField(15);
        JTextField reasonField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Mã cuộc hẹn:"), gbc);
        gbc.gridx = 1; formPanel.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Ngày (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; formPanel.add(dateField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Giờ (HH:mm):"), gbc);
        gbc.gridx = 1; formPanel.add(timeField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Bác sĩ:"), gbc);
        gbc.gridx = 1; formPanel.add(doctorField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Bệnh nhân:"), gbc);
        gbc.gridx = 1; formPanel.add(patientField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Lý do:"), gbc);
        gbc.gridx = 1; formPanel.add(reasonField, gbc);

        JButton bookBtn = new JButton("Đặt lịch mới");
        bookBtn.setBackground(new Color(41, 128, 185));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton completeBtn = new JButton("Hoàn thành");
        completeBtn.setBackground(new Color(46, 204, 113));
        completeBtn.setForeground(Color.WHITE);
        completeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton viewRecordBtn = new JButton("Xem hồ sơ bệnh án");
        viewRecordBtn.setBackground(new Color(241, 196, 15));
        viewRecordBtn.setForeground(Color.WHITE);
        viewRecordBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1; formPanel.add(bookBtn, gbc);
        gbc.gridx = 1; formPanel.add(completeBtn, gbc);
        gbc.gridx = 2; formPanel.add(viewRecordBtn, gbc);

        String[] columns = {"Mã", "Ngày", "Giờ", "Bác sĩ", "Bệnh nhân", "Lý do", "Trạng thái", "Thanh toán"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(table);

        bookBtn.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String dateStr = dateField.getText().trim();
                String timeStr = timeField.getText().trim();
                String doctor = doctorField.getText().trim();
                String patient = patientField.getText().trim();
                String reason = reasonField.getText().trim();

                if (id.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty() || doctor.isEmpty() || patient.isEmpty() || reason.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Vui lòng nhập đầy đủ thông tin!");
                    return;
                }

                Appointment appointment = new Appointment();
                appointment.setId(id);
                appointment.setDate(dateStr);
                appointment.setTime(timeStr);
                appointment.setDoctor(doctor);
                appointment.setPatient(patient);
                appointment.setReason(reason);
                appointment.setStatus(Appointment.AppointmentStatus.PENDING);
                appointment.setPaymentStatus(Appointment.PaymentStatus.UNPAID);

                appointments.add(appointment);
                tableModel.addRow(new Object[]{
                        appointment.getId(),
                        appointment.getDateString(),
                        appointment.getTimeString(),
                        appointment.getDoctor(),
                        appointment.getPatient(),
                        appointment.getReason(),
                        appointment.getStatusDisplay(),
                        appointment.getPaymentStatusDisplay()
                });

                idField.setText("");
                dateField.setText("");
                timeField.setText("");
                doctorField.setText("");
                patientField.setText("");
                reasonField.setText("");
                JOptionPane.showMessageDialog(panel, "Đặt lịch thành công!");
            } catch (IllegalArgumentException | DateTimeParseException ex) {
                JOptionPane.showMessageDialog(panel, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        completeBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn một lịch hẹn để hoàn thành!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Appointment appointment = appointments.get(selectedRow);
            if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
                JOptionPane.showMessageDialog(panel, "Chỉ có thể hoàn thành lịch hẹn đang chờ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tải MedicalRecord dựa trên patientId (giả định tạm thời)
            MedicalRecord patientRecord = new MedicalRecord(appointment.getPatient()); // Cần thay bằng tải từ cơ sở dữ liệu
            MedicalRecord.MedicalEntry entry = patientRecord.createNewMedicalEntry(
                    appointment.getDoctor(),
                    appointment.getReason(),
                    "",
                    "",
                    ""
            );
            appointment.setEntryId(entry.getEntryId());

            appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
            tableModel.setValueAt(appointment.getStatusDisplay(), selectedRow, 6);
            JOptionPane.showMessageDialog(panel, "Hoàn thành lịch hẹn và tạo lần khám mới!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });

        viewRecordBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn một lịch hẹn để xem hồ sơ bệnh án!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Appointment appointment = appointments.get(selectedRow);
            MedicalRecord patientRecord = new MedicalRecord(appointment.getPatient()); // Cần thay bằng tải từ cơ sở dữ liệu
            JFrame recordFrame = new JFrame("Hồ sơ bệnh án - " + appointment.getPatient());
            recordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            recordFrame.setSize(800, 600);
            recordFrame.add(new DoctorMedicalRecordPanel(currentUser));
            recordFrame.setVisible(true);
        });

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAdmissionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel mainContent = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Danh sách bệnh nhân nhập viện"));

        admissionListModel = new DefaultListModel<>();
        LocalDate today = LocalDate.now();
        MedicalRecord patientRecord1 = new MedicalRecord("PT001");
        MedicalRecord patientRecord2 = new MedicalRecord("PT002");
        Admission admission1 = new Admission("ADM001", "PT001", today, "DOC001", "R101", "Theo dõi");
        admission1.setRecordId(patientRecord1.getRecordId());
        Admission admission2 = new Admission("ADM002", "PT002", today.minusDays(5), "DOC002", "R102", today, "Điều trị xong");
        admission2.setRecordId(patientRecord2.getRecordId());
        admissionListModel.addElement(admission1);
        admissionListModel.addElement(admission2);

        admissionList = new JList<>(admissionListModel);
        admissionList.setCellRenderer(new AdmissionListCellRenderer());
        admissionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        admissionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Admission selected = admissionList.getSelectedValue();
                if (selected != null) {
                    admissionIdField.setText(selected.getAdmissionId());
                    patientIdField.setText(selected.getPatientId());
                    admissionDateField.setText(selected.getAdmissionDateString());
                    doctorIdField.setText(selected.getDoctorId());
                    roomIdField.setText(selected.getRoomId());
                    dischargeDateField.setText(selected.getDischargeDateString());
                    notesArea.setText(selected.getNotes());
                }
            }
        });
        JScrollPane listScrollPane = new JScrollPane(admissionList);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Thêm mới");
        addBtn.setBackground(new Color(41, 128, 185));
        addBtn.setForeground(Color.WHITE);
        JButton updateBtn = new JButton("Cập nhật");
        updateBtn.setBackground(new Color(46, 204, 113));
        updateBtn.setForeground(Color.WHITE);
        JButton dischargeBtn = new JButton("Xuất viện");
        dischargeBtn.setBackground(new Color(231, 76, 60));
        dischargeBtn.setForeground(Color.WHITE);
        JButton viewRecordBtn = new JButton("Xem hồ sơ bệnh án");
        viewRecordBtn.setBackground(new Color(241, 196, 15));
        viewRecordBtn.setForeground(Color.WHITE);

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(dischargeBtn);
        buttonPanel.add(viewRecordBtn);

        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Thông tin nhập viện"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        admissionIdField = new JTextField(10);
        patientIdField = new JTextField(10);
        admissionDateField = new JTextField(10);
        doctorIdField = new JTextField(10);
        roomIdField = new JTextField(10);
        dischargeDateField = new JTextField(10);
        notesArea = new JTextArea(5, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Mã nhập viện:"), gbc);
        gbc.gridx = 1; formPanel.add(admissionIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Mã bệnh nhân:"), gbc);
        gbc.gridx = 1; formPanel.add(patientIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Ngày nhập viện (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; formPanel.add(admissionDateField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Mã bác sĩ:"), gbc);
        gbc.gridx = 1; formPanel.add(doctorIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Mã phòng:"), gbc);
        gbc.gridx = 1; formPanel.add(roomIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Ngày xuất viện (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; formPanel.add(dischargeDateField, gbc);
        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; formPanel.add(new JScrollPane(notesArea), gbc);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            try {
                String admissionId = admissionIdField.getText().trim();
                String patientId = patientIdField.getText().trim();
                String admissionDateStr = admissionDateField.getText().trim();
                String doctorId = doctorIdField.getText().trim();
                String roomId = roomIdField.getText().trim();
                String dischargeDateStr = dischargeDateField.getText().trim();
                String notes = notesArea.getText().trim();

                if (admissionId.isEmpty() || patientId.isEmpty() || admissionDateStr.isEmpty() || doctorId.isEmpty() || roomId.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Vui lòng nhập đầy đủ thông tin bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                MedicalRecord patientRecord = new MedicalRecord(patientId); // Cần thay bằng tải từ cơ sở dữ liệu
                Admission admission;
                if (dischargeDateStr.isEmpty()) {
                    admission = new Admission(admissionId, patientId, admissionDateStr, doctorId, roomId, notes);
                } else {
                    admission = new Admission(admissionId, patientId, admissionDateStr, doctorId, roomId, dischargeDateStr, notes);
                }
                admission.setRecordId(patientRecord.getRecordId());

                admissionListModel.addElement(admission);
                clearAdmissionFields();
                JOptionPane.showMessageDialog(panel, "Thêm thông tin nhập viện thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | DateTimeParseException ex) {
                JOptionPane.showMessageDialog(panel, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateBtn.addActionListener(e -> {
            Admission selected = admissionList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn một bệnh nhân để cập nhật!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                String admissionId = admissionIdField.getText().trim();
                String patientId = patientIdField.getText().trim();
                String admissionDateStr = admissionDateField.getText().trim();
                String doctorId = doctorIdField.getText().trim();
                String roomId = roomIdField.getText().trim();
                String dischargeDateStr = dischargeDateField.getText().trim();
                String notes = notesArea.getText().trim();

                if (admissionId.isEmpty() || patientId.isEmpty() || admissionDateStr.isEmpty() || doctorId.isEmpty() || roomId.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Vui lòng nhập đầy đủ thông tin bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                selected.setAdmissionId(admissionId);
                selected.setPatientId(patientId);
                selected.setAdmissionDate(admissionDateStr);
                selected.setDoctorId(doctorId);
                selected.setRoomId(roomId);
                selected.setDischargeDate(dischargeDateStr);
                selected.setNotes(notes);

                admissionList.repaint();
                JOptionPane.showMessageDialog(panel, "Cập nhật thông tin nhập viện thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | DateTimeParseException ex) {
                JOptionPane.showMessageDialog(panel, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dischargeBtn.addActionListener(e -> {
            Admission selected = admissionList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn một bệnh nhân để xuất viện!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (selected.isDischarged()) {
                JOptionPane.showMessageDialog(panel, "Bệnh nhân đã được xuất viện!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String dischargeDateStr = dischargeDateField.getText().trim();
                if (dischargeDateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Vui lòng nhập ngày xuất viện!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                selected.setDischargeDate(dischargeDateStr);
                admissionList.repaint();
                JOptionPane.showMessageDialog(panel, "Xuất viện thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | DateTimeParseException ex) {
                JOptionPane.showMessageDialog(panel, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewRecordBtn.addActionListener(e -> {
            Admission selected = admissionList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn một bệnh nhân để xem hồ sơ bệnh án!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            MedicalRecord patientRecord = new MedicalRecord(selected.getPatientId()); // Cần thay bằng tải từ cơ sở dữ liệu
            JFrame recordFrame = new JFrame("Hồ sơ bệnh án - " + selected.getPatientId());
            recordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            recordFrame.setSize(800, 600);
            recordFrame.add(new DoctorMedicalRecordPanel(currentUser));
            recordFrame.setVisible(true);
        });

        mainContent.add(leftPanel, BorderLayout.WEST);
        mainContent.add(rightPanel, BorderLayout.CENTER);

        panel.add(mainContent, BorderLayout.CENTER);

        return panel;
    }

    private void clearAdmissionFields() {
        admissionIdField.setText("");
        patientIdField.setText("");
        admissionDateField.setText("");
        doctorIdField.setText("");
        roomIdField.setText("");
        dischargeDateField.setText("");
        notesArea.setText("");
    }

    private class AdmissionListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Admission) {
                Admission admission = (Admission) value;
                String displayText = String.format("%s - %s (Phòng: %s, %s)",
                        admission.getAdmissionId(),
                        admission.getPatientId(),
                        admission.getRoomId(),
                        admission.isDischarged() ? "Đã xuất viện" : "Đang điều trị");
                setText(displayText);
            }
            return this;
        }
    }
}