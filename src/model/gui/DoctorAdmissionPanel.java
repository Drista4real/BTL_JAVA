package model.gui;

import model.entity.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DoctorAdmissionPanel extends JPanel {
    private JTextField admissionIdField;
    private JTextField patientIdField;
    private JTextField patientNameField;
    private JTextField admissionDateField;
    private JTextField roomIdField;
    private JTextField dischargeDateField;
    private JTextField insuranceNumberField;
    private JComboBox<String> insuranceTypeComboBox;
    private JTextArea diagnosisArea;
    private JTextArea treatmentArea;
    private JTextArea notesArea;
    private JComboBox<String> statusComboBox;
    private JList<Admission> patientList;
    private DefaultListModel<Admission> patientListModel;
    private User currentDoctor;
    private BENHNHAN currentPatient;

    public DoctorAdmissionPanel(User doctor) {
        this.currentDoctor = doctor;
        initComponents();
        loadDoctorPatients();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        // Left Panel - Danh sách bệnh nhân
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Danh sách bệnh nhân được phân công"));

        patientListModel = new DefaultListModel<>();
        patientList = new JList<>(patientListModel);
        patientList.setCellRenderer(new PatientListCellRenderer());
        patientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Tìm");
        searchButton.setBackground(new Color(41, 128, 185));
        searchButton.setForeground(Color.WHITE);
        searchPanel.add(new JLabel("Tìm kiếm:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JComboBox<String> filterComboBox = new JComboBox<>(new String[]{
                "Tất cả bệnh nhân", "Đang điều trị", "Đã xuất viện"
        });
        filterPanel.add(new JLabel("Hiển thị:"));
        filterPanel.add(filterComboBox);

        JPanel leftTopPanel = new JPanel(new BorderLayout(0, 5));
        leftTopPanel.add(searchPanel, BorderLayout.NORTH);
        leftTopPanel.add(filterPanel, BorderLayout.CENTER);

        leftPanel.add(leftTopPanel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(patientList), BorderLayout.CENTER);

        // Right Panel - Chi tiết bệnh nhân
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết hồ sơ bệnh nhân"));

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel generalInfoPanel = createGeneralInfoPanel();
        tabbedPane.addTab("Thông tin chung", generalInfoPanel);
        JPanel treatmentPanel = createTreatmentPanel();
        tabbedPane.addTab("Chẩn đoán & Điều trị", treatmentPanel);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu thông tin");
        saveButton.setBackground(new Color(46, 139, 87));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton recommendDischargeButton = new JButton("Đề xuất xuất viện");
        recommendDischargeButton.setBackground(new Color(70, 130, 180));
        recommendDischargeButton.setForeground(Color.WHITE);

        actionPanel.add(recommendDischargeButton);
        actionPanel.add(saveButton);

        rightPanel.add(tabbedPane, BorderLayout.CENTER);
        rightPanel.add(actionPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện
        patientList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && patientList.getSelectedValue() != null) {
                Admission selectedAdmission = patientList.getSelectedValue();
                currentPatient = model.gui.PatientManagementDAO.getPatientById(selectedAdmission.getPatientId());
                if (currentPatient != null) {
                    displayPatientDetails(selectedAdmission, currentPatient);
                } else {
                    displayAdmissionOnly(selectedAdmission);
                }
                recommendDischargeButton.setEnabled(!selectedAdmission.isDischarged());
            }
        });

        saveButton.addActionListener(e -> {
            try {
                savePatientDetails();
                loadDoctorPatients();
                JOptionPane.showMessageDialog(this, "Lưu thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        recommendDischargeButton.addActionListener(e -> {
            if (patientList.getSelectedValue() != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc muốn đề xuất xuất viện cho bệnh nhân này?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    Admission selectedAdmission = patientList.getSelectedValue();
                    selectedAdmission.setDischargeDate(LocalDate.now());
                    dischargeDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                    boolean success = model.gui.PatientManagementDAO.saveAdmission(selectedAdmission);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái xuất viện thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        statusComboBox.setSelectedItem("Đã xuất viện");
                        recommendDischargeButton.setEnabled(false);
                        loadDoctorPatients();
                    } else {
                        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái xuất viện!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (!searchTerm.isEmpty()) {
                searchPatients(searchTerm);
            } else {
                loadDoctorPatients();
            }
        });

        filterComboBox.addActionListener(e -> {
            String filter = filterComboBox.getSelectedItem().toString();
            filterPatients(filter);
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(350);
        mainContent.add(splitPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createGeneralInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        admissionIdField = new JTextField(15);
        admissionIdField.setEditable(false);
        patientIdField = new JTextField(15);
        patientIdField.setEditable(false);
        patientNameField = new JTextField(15);
        patientNameField.setEditable(false);
        admissionDateField = new JTextField(15);
        admissionDateField.setEditable(false);
        roomIdField = new JTextField(15);
        dischargeDateField = new JTextField(15);
        dischargeDateField.setEditable(false);
        insuranceNumberField = new JTextField(15);
        insuranceTypeComboBox = new JComboBox<>(new String[]{"Không có", "BHYT"});
        statusComboBox = new JComboBox<>(new String[]{"Đang điều trị", "Đã xuất viện"});
        statusComboBox.setEnabled(false);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Mã nhập viện:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(admissionIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Mã bệnh nhân:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(patientIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Tên bệnh nhân:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(patientNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Ngày nhập viện:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(admissionDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Phòng hiện tại:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(roomIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Ngày xuất viện:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(dischargeDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(new JLabel("Loại bảo hiểm:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; formPanel.add(insuranceTypeComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 7; formPanel.add(new JLabel("Mã số bảo hiểm:"), gbc);
        gbc.gridx = 1; gbc.gridy = 7; formPanel.add(insuranceNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 8; formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.gridy = 8; formPanel.add(statusComboBox, gbc);

        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createTreatmentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        diagnosisArea = new JTextArea();
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        JScrollPane diagnosisScrollPane = new JScrollPane(diagnosisArea);
        diagnosisScrollPane.setPreferredSize(new Dimension(400, 120));

        treatmentArea = new JTextArea();
        treatmentArea.setLineWrap(true);
        treatmentArea.setWrapStyleWord(true);
        JScrollPane treatmentScrollPane = new JScrollPane(treatmentArea);
        treatmentScrollPane.setPreferredSize(new Dimension(400, 120));

        notesArea = new JTextArea();
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setPreferredSize(new Dimension(400, 120));

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Chẩn đoán:"), gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(diagnosisScrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Phương pháp điều trị:"), gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(treatmentScrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(notesScrollPane, gbc);

        return panel;
    }

    private void clearForm() {
        admissionIdField.setText("");
        patientIdField.setText("");
        patientNameField.setText("");
        admissionDateField.setText("");
        roomIdField.setText("");
        dischargeDateField.setText("");
        insuranceNumberField.setText("");
        insuranceTypeComboBox.setSelectedItem("Không có");
        diagnosisArea.setText("");
        treatmentArea.setText("");
        notesArea.setText("");
        statusComboBox.setSelectedItem("Đang điều trị");
    }

    private void loadDoctorPatients() {
        patientListModel.clear();
        if (currentDoctor != null) {
            List<Admission> patientAdmissions = model.gui.PatientManagementDAO.getAdmissionsByDoctor(currentDoctor.getUserId());
            if (patientAdmissions != null && !patientAdmissions.isEmpty()) {
                for (Admission admission : patientAdmissions) {
                    patientListModel.addElement(admission);
                }
                if (patientListModel.getSize() > 0) {
                    patientList.setSelectedIndex(0);
                    Admission selectedAdmission = patientListModel.getElementAt(0);
                    currentPatient = model.gui.PatientManagementDAO.getPatientById(selectedAdmission.getPatientId());
                    if (currentPatient != null) {
                        displayPatientDetails(selectedAdmission, currentPatient);
                    } else {
                        displayAdmissionOnly(selectedAdmission);
                    }
                }
            } else {
                clearForm();
            }
        }
    }

    private void searchPatients(String searchTerm) {
        patientListModel.clear();
        List<Admission> patients = model.gui.PatientManagementDAO.getAdmissionsByDoctor(currentDoctor.getUserId());
        if (patients != null && !patients.isEmpty()) {
            for (Admission admission : patients) {
                BENHNHAN patient = model.gui.PatientManagementDAO.getPatientById(admission.getPatientId());
                if (patient != null &&
                        (patient.getHoten().toLowerCase().contains(searchTerm) ||
                                patient.getMABN().toLowerCase().contains(searchTerm) ||
                                admission.getAdmissionId().toLowerCase().contains(searchTerm))) {
                    patientListModel.addElement(admission);
                }
            }
        }
    }

    private void filterPatients(String filter) {
        patientListModel.clear();
        List<Admission> patients = model.gui.PatientManagementDAO.getAdmissionsByDoctor(currentDoctor.getUserId());
        if (patients != null && !patients.isEmpty()) {
            for (Admission admission : patients) {
                if (filter.equals("Tất cả bệnh nhân") ||
                        (filter.equals("Đang điều trị") && !admission.isDischarged()) ||
                        (filter.equals("Đã xuất viện") && admission.isDischarged())) {
                    patientListModel.addElement(admission);
                }
            }
        }
    }

    private void displayPatientDetails(Admission admission, BENHNHAN patient) {
        if (admission == null) {
            clearForm();
            return;
        }

        admissionIdField.setText(admission.getAdmissionId());
        patientIdField.setText(admission.getPatientId());
        admissionDateField.setText(admission.getAdmissionDateString());
        roomIdField.setText(admission.getRoomId());
        dischargeDateField.setText(admission.getDischargeDateString());
        notesArea.setText(admission.getNotes());

        if (patient != null) {
            patientNameField.setText(patient.getHoten());

            if (patient instanceof BENHNHANBAOHIEMYTE) {
                BENHNHANBAOHIEMYTE insuredPatient = (BENHNHANBAOHIEMYTE) patient;
                insuranceNumberField.setText(insuredPatient.getMSBH());
                insuranceTypeComboBox.setSelectedItem(insuredPatient.getLoaiBH() == 'y' ? "BHYT" : "Không có");
            } else {
                insuranceNumberField.setText("");
                insuranceTypeComboBox.setSelectedItem("Không có");
            }

            String[] medicalInfo = model.gui.PatientManagementDAO.getPatientMedicalInfo(patient.getMABN());
            diagnosisArea.setText(medicalInfo[0] != null ? medicalInfo[0] : "");
            treatmentArea.setText(medicalInfo[1] != null ? medicalInfo[1] : "");

            updateStatusComboBox(admission);
        } else {
            patientNameField.setText("");
            insuranceNumberField.setText("");
            insuranceTypeComboBox.setSelectedItem("Không có");
            diagnosisArea.setText("");
            treatmentArea.setText("");
        }
    }

    private void updateStatusComboBox(Admission admission) {
        if (admission.isDischarged()) {
            statusComboBox.setSelectedItem("Đã xuất viện");
            setFieldsEditable(false);
        } else {
            statusComboBox.setSelectedItem("Đang điều trị");
            setFieldsEditable(true);
        }
    }

    private void setFieldsEditable(boolean editable) {
        admissionIdField.setEditable(false);
        patientIdField.setEditable(false);
        patientNameField.setEditable(false);
        admissionDateField.setEditable(false);
        dischargeDateField.setEditable(false);
        roomIdField.setEditable(editable);
        diagnosisArea.setEditable(editable);
        treatmentArea.setEditable(editable);
        notesArea.setEditable(editable);
        insuranceTypeComboBox.setEnabled(editable);
        insuranceNumberField.setEditable(editable);
    }

    private void displayAdmissionOnly(Admission admission) {
        admissionIdField.setText(admission.getAdmissionId());
        patientIdField.setText(admission.getPatientId());
        patientNameField.setText("Không tìm thấy thông tin");
        admissionDateField.setText(admission.getAdmissionDateString());
        roomIdField.setText(admission.getRoomId());
        dischargeDateField.setText(admission.getDischargeDateString());
        insuranceNumberField.setText("");
        insuranceTypeComboBox.setSelectedItem("Không có");
        statusComboBox.setSelectedItem(admission.isDischarged() ? "Đã xuất viện" : "Đang điều trị");

        String[] medicalInfo = model.gui.PatientManagementDAO.getPatientMedicalInfo(admission.getPatientId());
        diagnosisArea.setText(medicalInfo[0] != null ? medicalInfo[0] : "");
        treatmentArea.setText(medicalInfo[1] != null ? medicalInfo[1] : "");
        notesArea.setText(admission.getNotes() != null ? admission.getNotes() : "");
    }

    private void savePatientDetails() {
        if (patientList.getSelectedValue() == null) {
            throw new RuntimeException("Vui lòng chọn một bệnh nhân để lưu thông tin");
        }

        Admission selectedAdmission = patientList.getSelectedValue();

        // Kiểm tra dữ liệu đầu vào
        String diagnosis = diagnosisArea.getText().trim();
        String treatment = treatmentArea.getText().trim();
        String notes = notesArea.getText().trim();
        String insuranceNumber = insuranceNumberField.getText().trim();
        String insuranceType = (String) insuranceTypeComboBox.getSelectedItem();
        String roomId = roomIdField.getText().trim();

        if (diagnosis.isEmpty() || treatment.isEmpty()) {
            throw new RuntimeException("Chẩn đoán và phương pháp điều trị không được để trống");
        }
        if (roomId.isEmpty()) {
            throw new RuntimeException("Mã phòng không được để trống");
        }

        // Cập nhật thông tin Admission
        selectedAdmission.setRoomId(roomId);
        selectedAdmission.setNotes(notes);

        // Lưu thông tin nhập viện
        boolean admissionSuccess = model.gui.PatientManagementDAO.saveAdmission(selectedAdmission);
        if (!admissionSuccess) {
            throw new RuntimeException("Không thể lưu thông tin nhập viện");
        }

        // Lưu hồ sơ y tế
        boolean medicalSuccess = model.gui.PatientManagementDAO.saveMedicalRecord(
                selectedAdmission.getPatientId(),
                currentDoctor.getUserId(),
                diagnosis,
                treatment,
                notes
        );
        if (!medicalSuccess) {
            throw new RuntimeException("Không thể lưu thông tin y tế");
        }

        // Lưu thông tin bảo hiểm
        if (!insuranceNumber.isEmpty() && insuranceType.equals("BHYT")) {
            boolean insuranceSuccess = model.gui.PatientManagementDAO.saveInsurance(
                    selectedAdmission.getPatientId(),
                    insuranceNumber
            );
            if (!insuranceSuccess) {
                throw new RuntimeException("Không thể lưu thông tin bảo hiểm");
            }
        }

        // Cập nhật thông tin bệnh nhân
        if (currentPatient instanceof BENHNHANBAOHIEMYTE) {
            BENHNHANBAOHIEMYTE bhytPatient = (BENHNHANBAOHIEMYTE) currentPatient;
            bhytPatient.setLoaiBH(insuranceType.equals("BHYT") ? 'y' : 'n');
            bhytPatient.setMSBH(insuranceNumber);
            bhytPatient.setPhongTYC(roomId.equals("R002"));
            // Update patient in database if needed
            // Assuming Demo1 or another mechanism handles patient updates
        }
    }

    private class PatientListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Admission) {
                Admission admission = (Admission) value;
                BENHNHAN patient = model.gui.PatientManagementDAO.getPatientById(admission.getPatientId());

                StringBuilder displayText = new StringBuilder();
                displayText.append("<html>");

                if (patient != null) {
                    displayText.append("<b>").append(patient.getHoten()).append("</b>");
                } else {
                    displayText.append("<b>").append(admission.getPatientId()).append("</b>");
                }

                displayText.append(" - Phòng: ").append(admission.getRoomId()).append("<br/>");
                displayText.append("<font color='gray'>Nhập viện: ").append(admission.getAdmissionDateString()).append("</font>");

                if (admission.isDischarged()) {
                    displayText.append(" <font color='green'>[Đã xuất viện]</font>");
                }

                displayText.append("</html>");
                setText(displayText.toString());
            }

            return this;
        }
    }
}