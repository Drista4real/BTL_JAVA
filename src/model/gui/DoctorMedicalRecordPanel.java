package model.gui;

import model.entity.MedicalRecordService;
import model.entity.MedicalRecord;
import model.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class DoctorMedicalRecordPanel extends JPanel {
    private MedicalRecord medicalRecord;
    private DefaultTableModel tableModel;
    private JTable entryTable;
    private final User currentUser;
    private final MedicalRecordService medicalRecordService;
    private JTextField patientIdField;
    private JPanel headerPanel;
    private JPanel tablePanel;

    public DoctorMedicalRecordPanel(User user) {
        this.currentUser = user;
        this.medicalRecordService = new MedicalRecordService(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo panel nhập patientId
        JPanel inputPanel = createInputPanel();
        headerPanel = new JPanel(); // Khởi tạo trống, sẽ cập nhật sau
        tablePanel = new JPanel(); // Khởi tạo trống, sẽ cập nhật sau
        JPanel buttonPanel = createButtonPanel();

        add(inputPanel, BorderLayout.NORTH);
        add(headerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JLabel patientIdLabel = new JLabel("Mã bệnh nhân:");
        patientIdField = new JTextField(10);
        JButton loadButton = new JButton("Tải hồ sơ");

        loadButton.addActionListener(e -> loadMedicalRecord());

        panel.add(patientIdLabel);
        panel.add(patientIdField);
        panel.add(loadButton);

        return panel;
    }

    private void loadMedicalRecord() {
        String patientId = patientIdField.getText().trim();
        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã bệnh nhân!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        medicalRecord = medicalRecordService.loadMedicalRecordByPatientId(patientId);
        if (medicalRecord == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hồ sơ y tế cho bệnh nhân: " + patientId, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cập nhật giao diện
        remove(headerPanel);
        remove(tablePanel);
        headerPanel = createHeaderPanel();
        tablePanel = createTablePanel();
        add(headerPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel patientIdLabel = new JLabel("Mã bệnh nhân: " + medicalRecord.getPatientId());
        JLabel bloodTypeLabel = new JLabel("Nhóm máu: " + (medicalRecord.getBloodType() != null ? medicalRecord.getBloodType() : "Chưa có dữ liệu"));
        JLabel heightLabel = new JLabel("Chiều cao: " + (medicalRecord.getHeight() != null ? medicalRecord.getHeight() + " cm" : "Chưa có dữ liệu"));
        JLabel weightLabel = new JLabel("Cân nặng: " + (medicalRecord.getWeight() != null ? medicalRecord.getWeight() + " kg" : "Chưa có dữ liệu"));
        JLabel allergiesLabel = new JLabel("Dị ứng: " + medicalRecord.getAllergies());
        JLabel chronicConditionsLabel = new JLabel("Bệnh mãn tính: " + medicalRecord.getChronicConditions());
        JLabel familyMedicalHistoryLabel = new JLabel("Tiền sử gia đình: " + medicalRecord.getFamilyMedicalHistory());
        JLabel notesLabel = new JLabel("Ghi chú: " + medicalRecord.getAdditionalNotes());

        gbc.gridx = 0; gbc.gridy = 0; panel.add(patientIdLabel, gbc);
        gbc.gridy = 1; panel.add(bloodTypeLabel, gbc);
        gbc.gridy = 2; panel.add(heightLabel, gbc);
        gbc.gridy = 3; panel.add(weightLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(allergiesLabel, gbc);
        gbc.gridy = 1; panel.add(chronicConditionsLabel, gbc);
        gbc.gridy = 2; panel.add(familyMedicalHistoryLabel, gbc);
        gbc.gridy = 3; panel.add(notesLabel, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lịch sử khám bệnh"));

        String[] columns = {"Ngày", "Bác sĩ", "Triệu chứng", "Chẩn đoán", "Điều trị", "Thuốc", "Kết quả xét nghiệm", "Hướng dẫn tái khám", "Ghi chú", "Lời khuyên"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        entryTable = new JTable(tableModel);
        entryTable.setRowHeight(28);
        entryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        entryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Đổ dữ liệu từ MedicalRecord vào bảng
        if (medicalRecord != null) {
            for (MedicalRecord.MedicalEntry entry : medicalRecord.getMedicalHistory()) {
                tableModel.addRow(new Object[]{
                        entry.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        entry.getDoctorId(),
                        entry.getSymptoms(),
                        entry.getDiagnosis(),
                        entry.getTreatmentPlan(),
                        entry.getMedications(),
                        entry.getLabResults(),
                        entry.getFollowUpInstructions(),
                        entry.getNotes(),
                        entry.getLifestyleRecommendations()
                });
            }
        }

        JScrollPane scrollPane = new JScrollPane(entryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addEntryBtn = new JButton("Thêm bản ghi");
        addEntryBtn.addActionListener(e -> showAddEntryDialog());
        panel.add(addEntryBtn);
        return panel;
    }

    private void showAddEntryDialog() {
        if (medicalRecord == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tải hồ sơ y tế của bệnh nhân trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm bản ghi y tế", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Các trường nhập liệu
        JLabel symptomsLabel = new JLabel("Triệu chứng:");
        JTextArea symptomsArea = new JTextArea(3, 20);
        JScrollPane symptomsScroll = new JScrollPane(symptomsArea);

        JLabel diagnosisLabel = new JLabel("Chẩn đoán:");
        JTextArea diagnosisArea = new JTextArea(3, 20);
        JScrollPane diagnosisScroll = new JScrollPane(diagnosisArea);

        JLabel treatmentLabel = new JLabel("Điều trị:");
        JTextArea treatmentArea = new JTextArea(3, 20);
        JScrollPane treatmentScroll = new JScrollPane(treatmentArea);

        JLabel medicationsLabel = new JLabel("Thuốc:");
        JTextArea medicationsArea = new JTextArea(3, 20);
        JScrollPane medicationsScroll = new JScrollPane(medicationsArea);

        JLabel labResultsLabel = new JLabel("Kết quả xét nghiệm:");
        JTextArea labResultsArea = new JTextArea(3, 20);
        JScrollPane labResultsScroll = new JScrollPane(labResultsArea);

        JLabel followUpLabel = new JLabel("Hướng dẫn tái khám:");
        JTextArea followUpArea = new JTextArea(3, 20);
        JScrollPane followUpScroll = new JScrollPane(followUpArea);

        JLabel notesLabel = new JLabel("Ghi chú:");
        JTextArea notesArea = new JTextArea(3, 20);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        JLabel lifestyleLabel = new JLabel("Lời khuyên:");
        JTextArea lifestyleArea = new JTextArea(3, 20);
        JScrollPane lifestyleScroll = new JScrollPane(lifestyleArea);

        JButton saveBtn = new JButton("Lưu");
        JButton cancelBtn = new JButton("Hủy");

        // Đặt layout
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(symptomsLabel, gbc);
        gbc.gridx = 1; dialog.add(symptomsScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(diagnosisLabel, gbc);
        gbc.gridx = 1; dialog.add(diagnosisScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 2; dialog.add(treatmentLabel, gbc);
        gbc.gridx = 1; dialog.add(treatmentScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 3; dialog.add(medicationsLabel, gbc);
        gbc.gridx = 1; dialog.add(medicationsScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 4; dialog.add(labResultsLabel, gbc);
        gbc.gridx = 1; dialog.add(labResultsScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 5; dialog.add(followUpLabel, gbc);
        gbc.gridx = 1; dialog.add(followUpScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 6; dialog.add(notesLabel, gbc);
        gbc.gridx = 1; dialog.add(notesScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 7; dialog.add(lifestyleLabel, gbc);
        gbc.gridx = 1; dialog.add(lifestyleScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 8; dialog.add(saveBtn, gbc);
        gbc.gridx = 1; dialog.add(cancelBtn, gbc);

        // Xử lý sự kiện
        saveBtn.addActionListener(e -> {
            MedicalRecord.MedicalEntry entry = new MedicalRecord.MedicalEntry(
                    UUID.randomUUID().toString(),
                    LocalDateTime.now(),
                    currentUser.getUserId(),
                    symptomsArea.getText(),
                    diagnosisArea.getText(),
                    treatmentArea.getText(),
                    medicationsArea.getText(),
                    labResultsArea.getText(),
                    followUpArea.getText(),
                    notesArea.getText(),
                    lifestyleArea.getText()
            );
            try {
                medicalRecordService.saveMedicalEntry(entry, medicalRecord.getPatientId());
                medicalRecord.addMedicalEntry(entry);
                tableModel.addRow(new Object[]{
                        entry.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        entry.getDoctorId(),
                        entry.getSymptoms(),
                        entry.getDiagnosis(),
                        entry.getTreatmentPlan(),
                        entry.getMedications(),
                        entry.getLabResults(),
                        entry.getFollowUpInstructions(),
                        entry.getNotes(),
                        entry.getLifestyleRecommendations()
                });
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Đã thêm bản ghi y tế!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu bản ghi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}