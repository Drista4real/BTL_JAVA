package classes;

import model.entity.Admission;
import model.entity.BENHNHAN;
import model.entity.BENHNHANBAOHIEMYTE;
import model.entity.User;
import model.gui.PatientManagementDAO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdmissionPanel extends JPanel {
    private JTextField admissionIdField;
    private JTextField patientIdField;
    private JTextField patientNameField;
    private JTextField admissionDateField;
    private JTextField doctorIdField;
    private JTextField roomIdField;
    private JTextField dischargeDateField;
    private JTextField insuranceNumberField;
    private JComboBox<String> insuranceTypeComboBox;
    private JTextField hospitalFeeField;
    private JTextArea notesArea;
    private JList<Admission> admissionList;
    private DefaultListModel<Admission> admissionListModel;
    private User currentUser;
    private JButton dischargeButton; // Added to access in saveAdmissionDetails

    public AdmissionPanel(User user) {
        this.currentUser = user;
        initComponents();
        loadAdmissions();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        // Panel bên trái - danh sách bệnh nhân nhập viện
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Danh sách bệnh nhân nhập viện"));

        admissionListModel = new DefaultListModel<>();
        admissionList = new JList<>(admissionListModel);
        admissionList.setCellRenderer(new AdmissionListCellRenderer());
        admissionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Tìm");
        searchButton.setBackground(new Color(41, 128, 185));
        searchButton.setForeground(Color.WHITE);
        searchPanel.add(new JLabel("Tìm kiếm:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton addButton = new JButton("Thêm mới");
        JButton deleteButton = new JButton("Xóa");
        JButton refreshButton = new JButton("Làm mới");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel leftTopPanel = new JPanel(new BorderLayout(0, 5));
        leftTopPanel.add(searchPanel, BorderLayout.NORTH);
        leftTopPanel.add(buttonPanel, BorderLayout.CENTER);

        leftPanel.add(leftTopPanel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(admissionList), BorderLayout.CENTER);

        // Panel bên phải - chi tiết thông tin nhập viện
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết thông tin nhập viện"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        admissionIdField = new JTextField(15);
        patientIdField = new JTextField(15);
        patientNameField = new JTextField(15);
        patientNameField.setEditable(false);
        admissionDateField = new JTextField(15);
        doctorIdField = new JTextField(15);
        roomIdField = new JTextField(15);
        dischargeDateField = new JTextField(15);
        insuranceNumberField = new JTextField(15);
        insuranceTypeComboBox = new JComboBox<>(new String[]{"Không có", "BHYT"});
        hospitalFeeField = new JTextField(15);
        hospitalFeeField.setEditable(false);
        notesArea = new JTextArea(5, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Mã nhập viện:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(admissionIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Mã bệnh nhân:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        JPanel patientPanel = new JPanel(new BorderLayout(5, 0));
        patientPanel.add(patientIdField, BorderLayout.CENTER);
        JButton selectPatientBtn = new JButton("...");
        selectPatientBtn.setMargin(new Insets(0, 5, 0, 5));
        patientPanel.add(selectPatientBtn, BorderLayout.EAST);
        formPanel.add(patientPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Tên bệnh nhân:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(patientNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Ngày nhập viện:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(admissionDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Mã bác sĩ:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        JPanel doctorPanel = new JPanel(new BorderLayout(5, 0));
        doctorPanel.add(doctorIdField, BorderLayout.CENTER);
        JButton selectDoctorBtn = new JButton("...");
        selectDoctorBtn.setMargin(new Insets(0, 5, 0, 5));
        doctorPanel.add(selectDoctorBtn, BorderLayout.EAST);
        formPanel.add(doctorPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Mã phòng:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(roomIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(new JLabel("Ngày xuất viện:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; formPanel.add(dischargeDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 7; formPanel.add(new JLabel("Loại bảo hiểm:"), gbc);
        gbc.gridx = 1; gbc.gridy = 7; formPanel.add(insuranceTypeComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 8; formPanel.add(new JLabel("Mã số bảo hiểm:"), gbc);
        gbc.gridx = 1; gbc.gridy = 8; formPanel.add(insuranceNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 9; formPanel.add(new JLabel("Phí viện:"), gbc);
        gbc.gridx = 1; gbc.gridy = 9; formPanel.add(hospitalFeeField, gbc);

        gbc.gridx = 0; gbc.gridy = 10; formPanel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridy = 10; gbc.gridheight = 3; formPanel.add(notesScrollPane, gbc);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu thông tin");
        saveButton.setBackground(new Color(46, 139, 87));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        dischargeButton = new JButton("Xuất viện");
        dischargeButton.setBackground(new Color(70, 130, 180));
        dischargeButton.setForeground(Color.WHITE);

        actionPanel.add(dischargeButton);
        actionPanel.add(saveButton);

        rightPanel.add(formPanel, BorderLayout.CENTER);
        rightPanel.add(actionPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện
        admissionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && admissionList.getSelectedValue() != null) {
                Admission selectedAdmission = admissionList.getSelectedValue();
                displayAdmissionDetails(selectedAdmission);
                dischargeButton.setEnabled(!selectedAdmission.isDischarged());
            }
        });

        saveButton.addActionListener(e -> {
            try {
                saveAdmissionDetails();
                loadAdmissions();
                JOptionPane.showMessageDialog(this, "Lưu thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dischargeButton.addActionListener(e -> {
            if (admissionList.getSelectedValue() != null) {
                try {
                    Admission selectedAdmission = admissionList.getSelectedValue();
                    if (selectedAdmission.isDischarged()) {
                        JOptionPane.showMessageDialog(this, "Bệnh nhân này đã xuất viện!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    dischargeDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    saveAdmissionDetails();
                    loadAdmissions();
                    JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái xuất viện!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dischargeButton.setEnabled(false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addButton.addActionListener(e -> {
            clearForm();
            admissionIdField.setText("ADM" + String.format("%03d", System.currentTimeMillis() % 1000));
            admissionDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            admissionList.clearSelection();
            patientIdField.requestFocus();
        });

        deleteButton.addActionListener(e -> {
            if (admissionList.getSelectedValue() != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa thông tin nhập viện này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    Admission selectedAdmission = admissionList.getSelectedValue();
                    boolean success = PatientManagementDAO.deleteAdmission(selectedAdmission.getAdmissionId());
                    if (success) {
                        loadAdmissions();
                        clearForm();
                        JOptionPane.showMessageDialog(this, "Đã xóa thông tin nhập viện!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Lỗi khi xóa thông tin nhập viện!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một hồ sơ nhập viện để xóa!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            admissionListModel.clear();
            List<Admission> admissions = PatientManagementDAO.getAllAdmissions();
            for (Admission admission : admissions) {
                BENHNHAN patient = PatientManagementDAO.getPatientById(admission.getPatientId());
                String patientName = patient != null ? patient.getHoten().toLowerCase() : "";
                if (admission.getAdmissionId().toLowerCase().contains(searchTerm) ||
                        admission.getPatientId().toLowerCase().contains(searchTerm) ||
                        admission.getDoctorId().toLowerCase().contains(searchTerm) ||
                        patientName.contains(searchTerm)) {
                    admissionListModel.addElement(admission);
                }
            }

            if (admissionListModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả nào phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            clearForm();
            admissionList.clearSelection();
            loadAdmissions();
        });

        selectPatientBtn.addActionListener(e -> {
            List<User> patients = PatientManagementDAO.getAllPatients();
            if (patients.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có bệnh nhân nào trong hệ thống!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] patientOptions = patients.stream()
                    .map(p -> p.getUserId() + " - " + p.getFullName())
                    .toArray(String[]::new);
            String selected = (String) JOptionPane.showInputDialog(this,
                    "Chọn bệnh nhân:",
                    "Danh sách bệnh nhân",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    patientOptions,
                    patientOptions[0]);

            if (selected != null) {
                String patientId = selected.split(" - ")[0];
                patientIdField.setText(patientId);
                updatePatientInfo(patientId);
            }
        });

        selectDoctorBtn.addActionListener(e -> {
            List<User> doctors = PatientManagementDAO.getAllDoctors();
            if (doctors.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có bác sĩ nào trong hệ thống!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] doctorOptions = doctors.stream()
                    .map(d -> d.getUserId() + " - " + d.getFullName())
                    .toArray(String[]::new);
            String selected = (String) JOptionPane.showInputDialog(this,
                    "Chọn bác sĩ phụ trách:",
                    "Danh sách bác sĩ",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    doctorOptions,
                    doctorOptions[0]);

            if (selected != null) {
                String doctorId = selected.split(" - ")[0];
                doctorIdField.setText(doctorId);
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400);
        mainContent.add(splitPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
        clearForm();
    }

    private void loadAdmissions() {
        admissionListModel.clear();
        List<Admission> admissions = PatientManagementDAO.getAllAdmissions();
        for (Admission admission : admissions) {
            admissionListModel.addElement(admission);
        }
    }

    private void displayAdmissionDetails(Admission admission) {
        admissionIdField.setText(admission.getAdmissionId());
        patientIdField.setText(admission.getPatientId());
        admissionDateField.setText(admission.getAdmissionDateString());
        doctorIdField.setText(admission.getDoctorId());
        roomIdField.setText(admission.getRoomId());
        dischargeDateField.setText(admission.getDischargeDateString());
        notesArea.setText(admission.getNotes());
        updatePatientInfo(admission.getPatientId());
    }

    private void updatePatientInfo(String patientId) {
        BENHNHAN patient = PatientManagementDAO.getPatientById(patientId);
        if (patient instanceof BENHNHANBAOHIEMYTE) {
            BENHNHANBAOHIEMYTE bhytPatient = (BENHNHANBAOHIEMYTE) patient;
            patientNameField.setText(bhytPatient.getHoten());
            insuranceNumberField.setText(bhytPatient.getMSBH());
            insuranceTypeComboBox.setSelectedItem(bhytPatient.getLoaiBH() == 'y' ? "BHYT" : "Không có");
            hospitalFeeField.setText(String.format("%,.0f VNĐ", bhytPatient.TinhhoadonVP()));
        } else {
            patientNameField.setText("");
            insuranceNumberField.setText("");
            insuranceTypeComboBox.setSelectedItem("Không có");
            hospitalFeeField.setText("");
        }
    }

    private void saveAdmissionDetails() {
        try {
            // Validate input
            if (admissionIdField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Mã nhập viện không được để trống");
            }
            if (patientIdField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Mã bệnh nhân không được để trống");
            }
            if (doctorIdField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Mã bác sĩ không được để trống");
            }
            if (roomIdField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Mã phòng không được để trống");
            }
            if (admissionDateField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Ngày nhập viện không được để trống");
            }

            // Xử lý ngày tháng
            LocalDate admissionDate;
            try {
                admissionDate = LocalDate.parse(admissionDateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Ngày nhập viện không hợp lệ (định dạng: dd/MM/yyyy)");
            }

            LocalDate dischargeDate = null;
            if (!dischargeDateField.getText().trim().isEmpty()) {
                try {
                    dischargeDate = LocalDate.parse(dischargeDateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    if (dischargeDate.isBefore(admissionDate)) {
                        throw new IllegalArgumentException("Ngày xuất viện không thể trước ngày nhập viện");
                    }
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Ngày xuất viện không hợp lệ (định dạng: dd/MM/yyyy)");
                }
            }

            // Tạo hoặc cập nhật đối tượng Admission
            Admission admission = new Admission(
                    admissionIdField.getText(),
                    patientIdField.getText(),
                    admissionDate,
                    doctorIdField.getText(),
                    roomIdField.getText(),
                    dischargeDate,
                    notesArea.getText()
            );

            // Lưu thông tin bảo hiểm
            String insuranceNumber = insuranceNumberField.getText().trim();
            String insuranceType = (String) insuranceTypeComboBox.getSelectedItem();
            if (!insuranceNumber.isEmpty() && insuranceType.equals("BHYT")) {
                PatientManagementDAO.saveInsurance(admission.getPatientId(), insuranceNumber);
            }

            // Lưu Admission vào cơ sở dữ liệu
            boolean success = PatientManagementDAO.saveAdmission(admission);
            if (!success) {
                throw new RuntimeException("Không thể lưu thông tin nhập viện");
            }

            // Cập nhật thông tin bệnh nhân
            BENHNHAN patient = PatientManagementDAO.getPatientById(admission.getPatientId());
            if (patient instanceof BENHNHANBAOHIEMYTE) {
                BENHNHANBAOHIEMYTE bhytPatient = (BENHNHANBAOHIEMYTE) patient;
                bhytPatient.setLoaiBH(insuranceType.equals("BHYT") ? 'y' : 'n');
                bhytPatient.setMSBH(insuranceNumber);
                bhytPatient.setAdmission(admission);
                bhytPatient.setPhongTYC(admission.getRoomId().equals("R002"));
                // Update patient in database if needed
                // Assuming Demo1 or another mechanism handles patient updates
            }

            // Cập nhật trạng thái nút "Xuất viện"
            dischargeButton.setEnabled(dischargeDate == null);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu thông tin: " + e.getMessage());
        }
    }

    private void clearForm() {
        admissionIdField.setText("");
        patientIdField.setText("");
        patientNameField.setText("");
        admissionDateField.setText("");
        doctorIdField.setText("");
        roomIdField.setText("");
        dischargeDateField.setText("");
        insuranceNumberField.setText("");
        insuranceTypeComboBox.setSelectedItem("Không có");
        hospitalFeeField.setText("");
        notesArea.setText("");
        dischargeButton.setEnabled(true);
    }

    private class AdmissionListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Admission) {
                Admission admission = (Admission) value;
                BENHNHAN patient = PatientManagementDAO.getPatientById(admission.getPatientId());
                String patientName = patient != null ? patient.getHoten() : admission.getPatientId();
                String displayText = String.format("%s - %s - %s",
                        admission.getAdmissionId(), patientName, admission.getAdmissionDateString());

                if (admission.isDischarged()) {
                    displayText += " (Đã xuất viện)";
                }

                Component c = super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);
                if (admission.isDischarged()) {
                    setForeground(isSelected ? Color.WHITE : new Color(100, 149, 237));
                }
                return c;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}