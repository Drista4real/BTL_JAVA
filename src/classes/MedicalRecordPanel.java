package classes;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import model.entity.MedicalRecord;
import model.entity.MedicalRecord.MedicalEntry;
import model.entity.User;
import model.entity.Role;

/**
 * Panel hiển thị và quản lý hồ sơ bệnh án
 */
public class
MedicalRecordPanel extends JPanel {
    private User currentUser;
    private MedicalRecord medicalRecord;

    // Các thành phần giao diện
    private JTextField patientIdField;
    private JTextField bloodTypeField;
    private JTextField heightField;
    private JTextField weightField;
    private JTextField bmiField;
    private JTextArea allergiesArea;
    private JTextArea chronicConditionsArea;
    private JTextArea familyHistoryArea;
    private JTextArea additionalNotesArea;
    private JList<MedicalEntry> entryList;
    private DefaultListModel<MedicalEntry> entryListModel;

    // Các thành phần chi tiết lần khám
    private JTextField dateField;
    private JTextField doctorField;
    private JTextArea symptomsArea;
    private JTextArea diagnosisArea;
    private JTextArea treatmentPlanArea;
    private JTextArea medicationsArea;
    private JTextArea labResultsArea;
    private JTextArea followUpInstructionsArea;
    private JTextArea notesArea;

    // Chế độ người dùng (true = bác sĩ, false = bệnh nhân)
    private boolean doctorMode;

    /**
     * Constructor cho MedicalRecordPanel với thông tin người dùng hiện tại
     * @param user Người dùng đang đăng nhập (bác sĩ hoặc bệnh nhân)
     */
    public MedicalRecordPanel(User user) {
        this.currentUser = user;
        // Xác định chế độ người dùng dựa trên vai trò (Role)
        this.doctorMode = isDoctor(user);

        // Tạo dữ liệu mẫu - trong ứng dụng thực tế sẽ tải từ cơ sở dữ liệu
        createSampleMedicalRecord(user.getUsername());

        initializeUI();
    }

    /**
     * Constructor nhận trực tiếp đối tượng MedicalRecord
     * @param user Người dùng đang đăng nhập
     * @param medicalRecord Hồ sơ bệnh án cần hiển thị
     */
    public MedicalRecordPanel(User user, MedicalRecord medicalRecord) {
        this.currentUser = user;
        this.doctorMode = isDoctor(user);
        this.medicalRecord = medicalRecord;

        initializeUI();
    }

    /**
     * Kiểm tra xem người dùng có phải là bác sĩ không
     * @param user Người dùng cần kiểm tra
     * @return true nếu là bác sĩ, false nếu không phải
     */
    private boolean isDoctor(User user) {
        if (user == null || user.getRole() == null) return false;

        // Kiểm tra xem role có phải là DOCTOR không
        return Role.DOCTOR.equals(user.getRole());
    }

    /**
     * Tạo dữ liệu mẫu cho hồ sơ bệnh án
     * @param patientId ID của bệnh nhân
     */
    private void createSampleMedicalRecord(String patientId) {
        // Tạo hồ sơ bệnh án mới
        medicalRecord = new MedicalRecord(
                UUID.randomUUID().toString(),
                patientId,
                LocalDate.now().minusYears(1),
                "O+",
                170.0,
                65.0,
                "Dị ứng với penicilin, hải sản",
                "Cao huyết áp",
                "Bố mắc tiểu đường, mẹ bị huyết áp cao",
                "Bệnh nhân cần tư vấn chế độ ăn hợp lý"
        );

        // Thêm vài lần khám mẫu
        MedicalEntry entry1 = new MedicalEntry(
                UUID.randomUUID().toString(),
                LocalDateTime.now().minusMonths(6),
                "DOCTOR001",
                "Đau đầu, sốt nhẹ, ho khan",
                "Viêm đường hô hấp trên",
                "Nghỉ ngơi, uống nhiều nước, dùng thuốc theo chỉ định",
                "Paracetamol 500mg (3 lần/ngày), Loratadine 10mg (1 lần/ngày)",
                "Không có",
                "Tái khám sau 7 ngày nếu không thuyên giảm",
                "Tránh thức khuya và ăn đồ lạnh"
        );

        MedicalEntry entry2 = new MedicalEntry(
                UUID.randomUUID().toString(),
                LocalDateTime.now().minusMonths(2),
                "DOCTOR002",
                "Đau bụng, buồn nôn, tiêu chảy",
                "Viêm dạ dày cấp",
                "Ăn nhẹ, tránh thức ăn cay nóng, dùng thuốc theo chỉ định",
                "Omeprazole 20mg (2 lần/ngày), Domperidone 10mg (3 lần/ngày)",
                "Xét nghiệm máu: WBC tăng nhẹ",
                "Tái khám sau 10 ngày",
                "Kiêng bia rượu 1 tháng"
        );

        medicalRecord.addMedicalEntry(entry1);
        medicalRecord.addMedicalEntry(entry2);
    }

    /**
     * Khởi tạo giao diện người dùng
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel chính sử dụng JTabbedPane để chia thông tin theo tab
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab thông tin cơ bản
        JPanel basicInfoPanel = createBasicInfoPanel();
        tabbedPane.addTab("Thông tin cơ bản", basicInfoPanel);

        // Tab lịch sử khám bệnh
        JPanel historyPanel = createMedicalHistoryPanel();
        tabbedPane.addTab("Lịch sử khám bệnh", historyPanel);

        // Thêm vào panel chính
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Tạo panel chứa thông tin cơ bản của bệnh nhân
     * @return JPanel chứa thông tin cơ bản
     */
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel bên trên chứa thông tin cá nhân
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin cá nhân"));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Tạo các trường thông tin
        patientIdField = new JTextField(15);
        patientIdField.setText(medicalRecord.getPatientId());
        patientIdField.setEditable(false);

        bloodTypeField = new JTextField(5);
        bloodTypeField.setText(medicalRecord.getBloodType());
        bloodTypeField.setEditable(doctorMode);

        heightField = new JTextField(5);
        heightField.setText(medicalRecord.getHeight() != null ? medicalRecord.getHeight().toString() : "");
        heightField.setEditable(doctorMode);

        weightField = new JTextField(5);
        weightField.setText(medicalRecord.getWeight() != null ? medicalRecord.getWeight().toString() : "");
        weightField.setEditable(doctorMode);

        bmiField = new JTextField(5);
        bmiField.setText(medicalRecord.getBMI() != null ? String.format("%.2f", medicalRecord.getBMI()) : "");
        bmiField.setEditable(false);

        // Thêm các trường vào panel
        gbc.gridx = 0; gbc.gridy = 0; infoPanel.add(new JLabel("Mã bệnh nhân:"), gbc);
        gbc.gridx = 1; infoPanel.add(patientIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; infoPanel.add(new JLabel("Nhóm máu:"), gbc);
        gbc.gridx = 1; infoPanel.add(bloodTypeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; infoPanel.add(new JLabel("Chiều cao (cm):"), gbc);
        gbc.gridx = 1; infoPanel.add(heightField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; infoPanel.add(new JLabel("Cân nặng (kg):"), gbc);
        gbc.gridx = 1; infoPanel.add(weightField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; infoPanel.add(new JLabel("BMI:"), gbc);
        gbc.gridx = 1; infoPanel.add(bmiField, gbc);

        topPanel.add(infoPanel, BorderLayout.CENTER);

        // Panel bên dưới chứa tiền sử bệnh
        JPanel bottomPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Tiền sử bệnh"));

        // Khu vực dị ứng
        JPanel allergiesPanel = new JPanel(new BorderLayout());
        allergiesPanel.setBorder(BorderFactory.createTitledBorder("Dị ứng"));
        allergiesArea = new JTextArea(5, 20);
        allergiesArea.setText(medicalRecord.getAllergies());
        allergiesArea.setEditable(doctorMode);
        allergiesArea.setLineWrap(true);
        allergiesArea.setWrapStyleWord(true);
        allergiesPanel.add(new JScrollPane(allergiesArea), BorderLayout.CENTER);

        // Khu vực bệnh mãn tính
        JPanel chronicPanel = new JPanel(new BorderLayout());
        chronicPanel.setBorder(BorderFactory.createTitledBorder("Bệnh mãn tính"));
        chronicConditionsArea = new JTextArea(5, 20);
        chronicConditionsArea.setText(medicalRecord.getChronicConditions());
        chronicConditionsArea.setEditable(doctorMode);
        chronicConditionsArea.setLineWrap(true);
        chronicConditionsArea.setWrapStyleWord(true);
        chronicPanel.add(new JScrollPane(chronicConditionsArea), BorderLayout.CENTER);

        // Khu vực tiền sử gia đình
        JPanel familyPanel = new JPanel(new BorderLayout());
        familyPanel.setBorder(BorderFactory.createTitledBorder("Tiền sử gia đình"));
        familyHistoryArea = new JTextArea(5, 20);
        familyHistoryArea.setText(medicalRecord.getFamilyMedicalHistory());
        familyHistoryArea.setEditable(doctorMode);
        familyHistoryArea.setLineWrap(true);
        familyHistoryArea.setWrapStyleWord(true);
        familyPanel.add(new JScrollPane(familyHistoryArea), BorderLayout.CENTER);

        // Khu vực ghi chú bổ sung
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createTitledBorder("Ghi chú bổ sung"));
        additionalNotesArea = new JTextArea(5, 20);
        additionalNotesArea.setText(medicalRecord.getAdditionalNotes());
        additionalNotesArea.setEditable(doctorMode);
        additionalNotesArea.setLineWrap(true);
        additionalNotesArea.setWrapStyleWord(true);
        notesPanel.add(new JScrollPane(additionalNotesArea), BorderLayout.CENTER);

        // Thêm các panel vào panel chính
        bottomPanel.add(allergiesPanel);
        bottomPanel.add(chronicPanel);
        bottomPanel.add(familyPanel);
        bottomPanel.add(notesPanel);

        // Nút lưu thông tin (chỉ hiển thị cho bác sĩ)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (doctorMode) {
            JButton saveButton = new JButton("Lưu thông tin");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveBasicInfo();
                }
            });
            buttonPanel.add(saveButton);
        }

        // Thêm tất cả vào panel chính
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo panel chứa lịch sử khám bệnh
     * @return JPanel chứa lịch sử khám bệnh
     */
    private JPanel createMedicalHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel bên trái hiển thị danh sách các lần khám
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Danh sách lần khám"));

        // Tạo model và list cho danh sách lần khám
        entryListModel = new DefaultListModel<>();
        List<MedicalEntry> entries = medicalRecord.getMedicalHistory();
        for (MedicalEntry entry : entries) {
            entryListModel.addElement(entry);
        }

        entryList = new JList<>(entryListModel);
        entryList.setCellRenderer(new MedicalEntryCellRenderer());
        entryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Xử lý sự kiện khi chọn một lần khám
        entryList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && entryList.getSelectedValue() != null) {
                    displayEntryDetails(entryList.getSelectedValue());
                }
            }
        });

        // Panel nút chức năng cho danh sách
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Chỉ bác sĩ mới có thể thêm lần khám mới
        if (doctorMode) {
            JButton addButton = new JButton("Thêm lần khám mới");
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    createNewEntry();
                }
            });
            buttonPanel.add(addButton);
        }

        leftPanel.add(new JScrollPane(entryList), BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Panel bên phải hiển thị chi tiết lần khám
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết lần khám"));

        // Panel thông tin chung
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        dateField = new JTextField();
        dateField.setEditable(false);
        doctorField = new JTextField();
        doctorField.setEditable(false);

        infoPanel.add(new JLabel("Ngày khám:"));
        infoPanel.add(dateField);
        infoPanel.add(new JLabel("Bác sĩ:"));
        infoPanel.add(doctorField);

        // Panel nội dung khám
        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        // Khởi tạo các trường thông tin
        symptomsArea = new JTextArea(4, 20);
        symptomsArea.setLineWrap(true);
        symptomsArea.setWrapStyleWord(true);
        symptomsArea.setEditable(doctorMode);

        diagnosisArea = new JTextArea(4, 20);
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        diagnosisArea.setEditable(doctorMode);

        treatmentPlanArea = new JTextArea(4, 20);
        treatmentPlanArea.setLineWrap(true);
        treatmentPlanArea.setWrapStyleWord(true);
        treatmentPlanArea.setEditable(doctorMode);

        medicationsArea = new JTextArea(4, 20);
        medicationsArea.setLineWrap(true);
        medicationsArea.setWrapStyleWord(true);
        medicationsArea.setEditable(doctorMode);

        labResultsArea = new JTextArea(4, 20);
        labResultsArea.setLineWrap(true);
        labResultsArea.setWrapStyleWord(true);
        labResultsArea.setEditable(doctorMode);

        followUpInstructionsArea = new JTextArea(4, 20);
        followUpInstructionsArea.setLineWrap(true);
        followUpInstructionsArea.setWrapStyleWord(true);
        followUpInstructionsArea.setEditable(doctorMode);

        // Thêm vào panel nội dung
        JPanel symptomsPanel = new JPanel(new BorderLayout());
        symptomsPanel.setBorder(BorderFactory.createTitledBorder("Triệu chứng"));
        symptomsPanel.add(new JScrollPane(symptomsArea), BorderLayout.CENTER);

        JPanel diagnosisPanel = new JPanel(new BorderLayout());
        diagnosisPanel.setBorder(BorderFactory.createTitledBorder("Chuẩn đoán"));
        diagnosisPanel.add(new JScrollPane(diagnosisArea), BorderLayout.CENTER);

        JPanel treatmentPanel = new JPanel(new BorderLayout());
        treatmentPanel.setBorder(BorderFactory.createTitledBorder("Kế hoạch điều trị"));
        treatmentPanel.add(new JScrollPane(treatmentPlanArea), BorderLayout.CENTER);

        JPanel medicationsPanel = new JPanel(new BorderLayout());
        medicationsPanel.setBorder(BorderFactory.createTitledBorder("Thuốc kê đơn"));
        medicationsPanel.add(new JScrollPane(medicationsArea), BorderLayout.CENTER);

        JPanel labResultsPanel = new JPanel(new BorderLayout());
        labResultsPanel.setBorder(BorderFactory.createTitledBorder("Kết quả xét nghiệm"));
        labResultsPanel.add(new JScrollPane(labResultsArea), BorderLayout.CENTER);

        JPanel followUpPanel = new JPanel(new BorderLayout());
        followUpPanel.setBorder(BorderFactory.createTitledBorder("Hướng dẫn tái khám"));
        followUpPanel.add(new JScrollPane(followUpInstructionsArea), BorderLayout.CENTER);

        contentPanel.add(symptomsPanel);
        contentPanel.add(diagnosisPanel);
        contentPanel.add(treatmentPanel);
        contentPanel.add(medicationsPanel);
        contentPanel.add(labResultsPanel);
        contentPanel.add(followUpPanel);

        // Panel ghi chú
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createTitledBorder("Ghi chú"));
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setEditable(doctorMode);
        notesPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        // Nút lưu thông tin lần khám (chỉ dành cho bác sĩ)
        JPanel entryButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (doctorMode) {
            JButton saveEntryButton = new JButton("Lưu thông tin lần khám");
            saveEntryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveEntryDetails();
                }
            });
            entryButtonPanel.add(saveEntryButton);
        }

        // Thêm các thành phần vào panel bên phải
        JPanel rightContentPanel = new JPanel(new BorderLayout(5, 5));
        rightContentPanel.add(infoPanel, BorderLayout.NORTH);
        rightContentPanel.add(contentPanel, BorderLayout.CENTER);
        rightContentPanel.add(notesPanel, BorderLayout.SOUTH);

        rightPanel.add(rightContentPanel, BorderLayout.CENTER);
        rightPanel.add(entryButtonPanel, BorderLayout.SOUTH);

        // Tạo JSplitPane để chia màn hình
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Lưu thông tin cơ bản của bệnh nhân
     */
    private void saveBasicInfo() {
        try {
            // Lấy thông tin từ các trường
            String bloodType = bloodTypeField.getText().trim();
            Double height = null;
            Double weight = null;

            try {
                if (!heightField.getText().trim().isEmpty()) {
                    height = Double.parseDouble(heightField.getText().trim());
                }
                if (!weightField.getText().trim().isEmpty()) {
                    weight = Double.parseDouble(weightField.getText().trim());
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Chiều cao và cân nặng phải là số",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String allergies = allergiesArea.getText().trim();
            String chronicConditions = chronicConditionsArea.getText().trim();
            String familyHistory = familyHistoryArea.getText().trim();
            String additionalNotes = additionalNotesArea.getText().trim();

            // Cập nhật thông tin vào hồ sơ
            medicalRecord.updateBasicInfo(bloodType, height, weight);
            medicalRecord.updateMedicalHistory(allergies, chronicConditions, familyHistory);
            medicalRecord.setAdditionalNotes(additionalNotes);

            // Cập nhật lại BMI
            bmiField.setText(medicalRecord.getBMI() != null ? String.format("%.2f", medicalRecord.getBMI()) : "");

            JOptionPane.showMessageDialog(this,
                    "Đã lưu thông tin cơ bản của bệnh nhân",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lưu thông tin: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tạo lần khám mới
     */
    private void createNewEntry() {
        // Tạo lần khám mới với thông tin cơ bản
        MedicalEntry newEntry = medicalRecord.createNewMedicalEntry(
                currentUser.getUsername(), // ID của bác sĩ hiện tại
                "", // Triệu chứng
                "", // Chuẩn đoán
                "", // Kế hoạch điều trị
                ""  // Thuốc kê đơn
        );

        // Thêm vào model và chọn
        entryListModel.addElement(newEntry);
        entryList.setSelectedIndex(entryListModel.size() - 1);

        // Hiển thị thông tin
        displayEntryDetails(newEntry);

        // Focus vào trường triệu chứng
        symptomsArea.requestFocus();
    }

    /**
     * Hiển thị chi tiết một lần khám
     * @param entry Đối tượng MedicalEntry cần hiển thị
     */
    private void displayEntryDetails(MedicalEntry entry) {
        if (entry == null) return;

        // Định dạng thời gian
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateField.setText(entry.getDate().format(formatter));
        doctorField.setText(entry.getDoctorId());

        // Hiển thị thông tin chi tiết
        symptomsArea.setText(entry.getSymptoms());
        diagnosisArea.setText(entry.getDiagnosis());
        treatmentPlanArea.setText(entry.getTreatmentPlan());
        medicationsArea.setText(entry.getMedications());
        labResultsArea.setText(entry.getLabResults());
        followUpInstructionsArea.setText(entry.getFollowUpInstructions());
        notesArea.setText(entry.getNotes());
    }

    /**
     * Lưu thông tin của lần khám được chọn
     */
    private void saveEntryDetails() {
        MedicalEntry selectedEntry = entryList.getSelectedValue();
        if (selectedEntry == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một lần khám",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // Cập nhật thông tin
            selectedEntry.setSymptoms(symptomsArea.getText().trim());
            selectedEntry.setDiagnosis(diagnosisArea.getText().trim());
            selectedEntry.setTreatmentPlan(treatmentPlanArea.getText().trim());
            selectedEntry.setMedications(medicationsArea.getText().trim());
            selectedEntry.setLabResults(labResultsArea.getText().trim());
            selectedEntry.setFollowUpInstructions(followUpInstructionsArea.getText().trim());
            selectedEntry.setNotes(notesArea.getText().trim());

            // Cập nhật lại hiển thị
            entryList.repaint();

            JOptionPane.showMessageDialog(this,
                    "Đã lưu thông tin lần khám",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lưu thông tin: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lớp tùy chỉnh hiển thị các phần tử trong JList
     */
    private class MedicalEntryCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof MedicalEntry) {
                MedicalEntry entry = (MedicalEntry) value;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String displayText = entry.getDate().format(formatter);

                if (entry.getDiagnosis() != null && !entry.getDiagnosis().isEmpty()) {
                    displayText += " - " + entry.getDiagnosis();
                } else {
                    displayText += " - Chưa chẩn đoán";
                }

                return super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}