package classes;


import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import model.entity.Admission;
import model.entity.User;

/**
 * AdmissionPanel - Panel quản lý thông tin nhập viện của bệnh nhân
 * Cho phép xem, thêm, sửa và xóa thông tin nhập viện
 */
public class AdmissionPanel extends JPanel {
    private JTextField admissionIdField;
    private JTextField patientIdField;
    private JTextField admissionDateField;
    private JTextField doctorIdField;
    private JTextField roomIdField;
    private JTextField dischargeDateField;
    private JTextArea notesArea;
    private JList<Admission> admissionList;
    private DefaultListModel<Admission> admissionListModel;
    private User currentUser;

    /**
     * Constructor AdmissionPanel với tham số user
     * @param user Người dùng hiện tại đang đăng nhập
     */
    public AdmissionPanel(User user) {
        this.currentUser = user;
        initComponents();
    }

    /**
     * Khởi tạo các thành phần giao diện
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel chính
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        // === Panel bên trái - danh sách bệnh nhân nhập viện ===
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Danh sách bệnh nhân nhập viện"));

        // Khởi tạo model và danh sách
        admissionListModel = new DefaultListModel<>();

        // Tạo JList với renderer tùy chỉnh
        admissionList = new JList<>(admissionListModel);
        admissionList.setCellRenderer(new AdmissionListCellRenderer());
        admissionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Tìm");
        searchPanel.add(new JLabel("Tìm kiếm:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton addButton = new JButton("Thêm mới");
        JButton deleteButton = new JButton("Xóa");
        JButton refreshButton = new JButton("Làm mới");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Thêm các component vào panel bên trái
        JPanel leftTopPanel = new JPanel(new BorderLayout(0, 5));
        leftTopPanel.add(searchPanel, BorderLayout.NORTH);
        leftTopPanel.add(buttonPanel, BorderLayout.CENTER);

        leftPanel.add(leftTopPanel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(admissionList), BorderLayout.CENTER);

        // === Panel bên phải - Chi tiết thông tin nhập viện ===
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết thông tin nhập viện"));

        // Form nhập liệu
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Khởi tạo các trường nhập liệu
        admissionIdField = new JTextField(15);
        patientIdField = new JTextField(15);
        admissionDateField = new JTextField(15);
        doctorIdField = new JTextField(15);
        roomIdField = new JTextField(15);
        dischargeDateField = new JTextField(15);
        notesArea = new JTextArea(5, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);

        // Thêm các trường vào panel
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

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Ngày nhập viện:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(admissionDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Mã bác sĩ:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        JPanel doctorPanel = new JPanel(new BorderLayout(5, 0));
        doctorPanel.add(doctorIdField, BorderLayout.CENTER);
        JButton selectDoctorBtn = new JButton("...");
        selectDoctorBtn.setMargin(new Insets(0, 5, 0, 5));
        doctorPanel.add(selectDoctorBtn, BorderLayout.EAST);
        formPanel.add(doctorPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Mã phòng:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(roomIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Ngày xuất viện:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(dischargeDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridheight = 3; formPanel.add(notesScrollPane, gbc);

        // Panel nút lưu
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu thông tin");
        saveButton.setBackground(new Color(46, 139, 87));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton dischargeButton = new JButton("Xuất viện");
        dischargeButton.setBackground(new Color(70, 130, 180));
        dischargeButton.setForeground(Color.WHITE);

        actionPanel.add(dischargeButton);
        actionPanel.add(saveButton);

        // Thêm các panel vào panel bên phải
        rightPanel.add(formPanel, BorderLayout.CENTER);
        rightPanel.add(actionPanel, BorderLayout.SOUTH);

        // === Xử lý các sự kiện ===

        // Sự kiện khi chọn một bệnh nhân trong danh sách
        admissionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && admissionList.getSelectedValue() != null) {
                Admission selectedAdmission = admissionList.getSelectedValue();
                displayAdmissionDetails(selectedAdmission);

                // Cập nhật trạng thái nút xuất viện
                dischargeButton.setEnabled(selectedAdmission.getDischargeDate() == null);
            }
        });

        // Sự kiện khi nhấn nút lưu
        saveButton.addActionListener(e -> {
            try {
                saveAdmissionDetails();
                JOptionPane.showMessageDialog(this,
                        "Lưu thông tin thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Sự kiện nút xuất viện
        dischargeButton.addActionListener(e -> {
            if (admissionList.getSelectedValue() != null) {
                try {
                    Admission selectedAdmission = admissionList.getSelectedValue();
                    if (selectedAdmission.getDischargeDate() != null) {
                        JOptionPane.showMessageDialog(this,
                                "Bệnh nhân này đã xuất viện!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // Cập nhật ngày xuất viện là ngày hiện tại
                    dischargeDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                    // Tự động lưu
                    saveAdmissionDetails();
                    JOptionPane.showMessageDialog(this,
                            "Đã cập nhật trạng thái xuất viện!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Cập nhật trạng thái nút
                    dischargeButton.setEnabled(false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Lỗi: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Sự kiện khi nhấn nút thêm mới
        addButton.addActionListener(e -> {
            clearForm();
            admissionIdField.setText("ADM" + String.format("%03d", admissionListModel.getSize() + 1));
            admissionDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            // Bỏ chọn phần tử đang chọn
            admissionList.clearSelection();

            // Focus vào trường mã bệnh nhân
            patientIdField.requestFocus();
        });

        // Sự kiện khi nhấn nút xóa
        deleteButton.addActionListener(e -> {
            if (admissionList.getSelectedValue() != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa thông tin nhập viện này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    admissionListModel.removeElement(admissionList.getSelectedValue());
                    clearForm();
                    JOptionPane.showMessageDialog(this,
                            "Đã xóa thông tin nhập viện!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn một hồ sơ nhập viện để xóa!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Sự kiện tìm kiếm
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (searchTerm.isEmpty()) {
                // Thông báo nếu không có từ khóa tìm kiếm
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập từ khóa tìm kiếm!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Tìm kiếm trong danh sách hiện tại
            boolean found = false;
            for (int i = 0; i < admissionListModel.getSize(); i++) {
                Admission admission = admissionListModel.getElementAt(i);
                // Tìm kiếm theo mã, id bệnh nhân, hoặc bác sĩ
                if (admission.getAdmissionId().toLowerCase().contains(searchTerm) ||
                        admission.getPatientId().toLowerCase().contains(searchTerm) ||
                        admission.getDoctorId().toLowerCase().contains(searchTerm)) {
                    admissionList.setSelectedIndex(i);
                    admissionList.ensureIndexIsVisible(i);
                    found = true;
                    break;
                }
            }

            // Thông báo kết quả
            if (!found) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy kết quả nào phù hợp!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Sự kiện làm mới
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            clearForm();
            admissionList.clearSelection();
        });

        // Sự kiện chọn bệnh nhân
        selectPatientBtn.addActionListener(e -> {
            String patientId = JOptionPane.showInputDialog(this,
                    "Nhập mã bệnh nhân cần tìm:",
                    "Tìm bệnh nhân",
                    JOptionPane.QUESTION_MESSAGE);

            if (patientId != null && !patientId.trim().isEmpty()) {
                patientIdField.setText(patientId);
            }
        });

        // Sự kiện chọn bác sĩ
        selectDoctorBtn.addActionListener(e -> {
            String[] doctors = {"DOC001", "DOC002", "DOC003", "DOC004"};

            String selectedDoctor = (String) JOptionPane.showInputDialog(this,
                    "Chọn bác sĩ phụ trách:",
                    "Danh sách bác sĩ",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    doctors,
                    doctors[0]);

            if (selectedDoctor != null) {
                doctorIdField.setText(selectedDoctor);
            }
        });

        // Tạo SplitPane để chia không gian
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400);
        mainContent.add(splitPane, BorderLayout.CENTER);

        // Thêm tất cả vào panel chính
        add(mainContent, BorderLayout.CENTER);

        // Thiết lập trạng thái ban đầu
        clearForm();
    }

    /**
     * Hiển thị chi tiết thông tin nhập viện lên form
     * @param admission Đối tượng Admission cần hiển thị
     */
    private void displayAdmissionDetails(Admission admission) {
        admissionIdField.setText(admission.getAdmissionId());
        patientIdField.setText(admission.getPatientId());
        admissionDateField.setText(admission.getAdmissionDateString());
        doctorIdField.setText(admission.getDoctorId());
        roomIdField.setText(admission.getRoomId());
        dischargeDateField.setText(admission.getDischargeDateString());
        notesArea.setText(admission.getNotes());
    }

    /**
     * Lưu thông tin nhập viện từ form
     * @throws Exception Nếu có lỗi trong quá trình lưu
     */
    private void saveAdmissionDetails() throws Exception {
        // Kiểm tra các trường bắt buộc
        if (admissionIdField.getText().trim().isEmpty() ||
                patientIdField.getText().trim().isEmpty() ||
                admissionDateField.getText().trim().isEmpty() ||
                doctorIdField.getText().trim().isEmpty() ||
                roomIdField.getText().trim().isEmpty()) {
            throw new Exception("Vui lòng nhập đầy đủ thông tin các trường bắt buộc!");
        }

        int selectedIndex = admissionList.getSelectedIndex();

        try {
            // Tạo đối tượng Admission từ dữ liệu form
            Admission updatedAdmission = new Admission(
                    admissionIdField.getText().trim(),
                    patientIdField.getText().trim(),
                    admissionDateField.getText().trim(),
                    doctorIdField.getText().trim(),
                    roomIdField.getText().trim(),
                    dischargeDateField.getText().trim().isEmpty() ? null : dischargeDateField.getText().trim(),
                    notesArea.getText().trim()
            );

            if (selectedIndex >= 0) {
                // Cập nhật thông tin
                admissionListModel.set(selectedIndex, updatedAdmission);
            } else {
                // Thêm mới
                admissionListModel.addElement(updatedAdmission);
                // Chọn phần tử vừa thêm
                admissionList.setSelectedIndex(admissionListModel.getSize() - 1);
            }
        } catch (Exception e) {
            throw new Exception("Lỗi định dạng dữ liệu: " + e.getMessage());
        }
    }

    /**
     * Xóa dữ liệu trên form
     */
    private void clearForm() {
        admissionIdField.setText("");
        patientIdField.setText("");
        admissionDateField.setText("");
        doctorIdField.setText("");
        roomIdField.setText("");
        dischargeDateField.setText("");
        notesArea.setText("");
    }

    /**
     * Lớp tùy chỉnh hiển thị các phần tử trong JList
     */
    private class AdmissionListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Admission) {
                Admission admission = (Admission) value;
                String displayText = String.format("%s - %s - %s",
                        admission.getAdmissionId(),
                        admission.getPatientId(),
                        admission.getAdmissionDateString());

                // Thêm thông tin xuất viện nếu có
                if (admission.getDischargeDate() != null) {
                    displayText += " (Đã xuất viện)";
                }

                Component c = super.getListCellRendererComponent(
                        list, displayText, index, isSelected, cellHasFocus);

                // Đổi màu cho các bệnh nhân đã xuất viện
                if (admission.getDischargeDate() != null) {
                    setForeground(isSelected ? Color.WHITE : new Color(100, 149, 237)); // Màu xanh dương nhạt
                }

                return c;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
