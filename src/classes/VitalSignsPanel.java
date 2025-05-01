package classes;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.entity.VitalSigns;
import model.entity.User;
import model.entity.Role;

/**
 * Panel hiển thị và quản lý thông tin dấu hiệu sinh tồn của bệnh nhân
 */
public class VitalSignsPanel extends JPanel {
    private User currentUser;
    private String currentPatientId;
    private List<VitalSigns> vitalSignsList;

    // Các thành phần giao diện
    private JTextField patientIdField;
    private JTextField temperatureField;
    private JTextField systolicBPField;
    private JTextField diastolicBPField;
    private JTextField heartRateField;
    private JTextField spO2Field;

    // Bảng hiển thị danh sách dấu hiệu sinh tồn
    private JTable vitalSignsTable;
    private DefaultTableModel vitalSignsTableModel;

    // Biểu đồ hiển thị (giả lập, có thể mở rộng sau)
    private JPanel chartPanel;

    // Chế độ người dùng (true = bác sĩ/y tá, false = bệnh nhân)
    private boolean medicalStaffMode;

    /**
     * Constructor với thông tin người dùng và mã bệnh nhân
     * @param user Người dùng đang đăng nhập
     * @param patientId Mã bệnh nhân cần theo dõi
     */
    public VitalSignsPanel(User user, String patientId) {
        this.currentUser = user;
        this.currentPatientId = patientId;
        this.medicalStaffMode = isMedicalStaff(user);
        this.vitalSignsList = new ArrayList<>();

        initializeUI();
    }

    /**
     * Constructor với thông tin người dùng, mã bệnh nhân và danh sách dấu hiệu sinh tồn
     * @param user Người dùng đang đăng nhập
     * @param patientId Mã bệnh nhân cần theo dõi
     * @param vitalSignsList Danh sách dấu hiệu sinh tồn
     */
    public VitalSignsPanel(User user, String patientId, List<VitalSigns> vitalSignsList) {
        this.currentUser = user;
        this.currentPatientId = patientId;
        this.medicalStaffMode = isMedicalStaff(user);
        this.vitalSignsList = new ArrayList<>(vitalSignsList);

        initializeUI();
    }

    /**
     * Kiểm tra xem người dùng có phải là nhân viên y tế không
     * @param user Người dùng cần kiểm tra
     * @return true nếu là bác sĩ hoặc vai trò phù hợp, false nếu không phải
     */
    private boolean isMedicalStaff(User user) {
        if (user == null || user.getRole() == null) return false;

        // Kiểm tra vai trò - chỉ xét DOCTOR vì có thể không có vai trò NURSE
        return Role.DOCTOR.equals(user.getRole());

        // Nếu sau này hệ thống có thêm vai trò y tá, có thể mở rộng như sau:
        // return Role.DOCTOR.equals(user.getRole()) || "NURSE".equals(user.getRole().toString());
    }
    /**
     * Thiết lập danh sách dấu hiệu sinh tồn mới
     * @param vitalSignsList Danh sách mới
     */
    public void setVitalSignsList(List<VitalSigns> vitalSignsList) {
        this.vitalSignsList = new ArrayList<>(vitalSignsList);
        updateVitalSignsTable();
        updateChartPanel();
    }

    /**
     * Thêm dấu hiệu sinh tồn vào danh sách
     * @param vitalSigns Dấu hiệu sinh tồn cần thêm
     */
    public void addVitalSigns(VitalSigns vitalSigns) {
        if (vitalSigns != null) {
            vitalSignsList.add(vitalSigns);
            updateVitalSignsTable();
            updateChartPanel();
        }
    }

    /**
     * Thiết lập mã bệnh nhân mới
     * @param patientId Mã bệnh nhân mới
     */
    public void setPatientId(String patientId) {
        this.currentPatientId = patientId;
        patientIdField.setText(patientId);
    }

    /**
     * Khởi tạo giao diện người dùng
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel chính sử dụng JTabbedPane để chia thông tin theo tab
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab nhập dữ liệu mới
        JPanel inputPanel = createInputPanel();
        tabbedPane.addTab("Nhập dữ liệu", inputPanel);

        // Tab lịch sử
        JPanel historyPanel = createHistoryPanel();
        tabbedPane.addTab("Lịch sử", historyPanel);

        // Tab biểu đồ
        chartPanel = createChartPanel();
        tabbedPane.addTab("Biểu đồ", new JScrollPane(chartPanel));

        // Thêm vào panel chính
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Tạo panel nhập dữ liệu mới
     * @return Panel nhập dữ liệu
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel chứa trường thông tin
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Dấu hiệu sinh tồn"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Tạo các trường thông tin
        patientIdField = new JTextField(15);
        patientIdField.setText(currentPatientId);
        patientIdField.setEditable(false); // ID bệnh nhân không được thay đổi trực tiếp

        temperatureField = new JTextField(10);
        systolicBPField = new JTextField(10);
        diastolicBPField = new JTextField(10);
        heartRateField = new JTextField(10);
        spO2Field = new JTextField(10);

        // Thiết lập quyền chỉnh sửa dựa vào vai trò
        boolean canEdit = medicalStaffMode;
        temperatureField.setEditable(canEdit);
        systolicBPField.setEditable(canEdit);
        diastolicBPField.setEditable(canEdit);
        heartRateField.setEditable(canEdit);
        spO2Field.setEditable(canEdit);

        // Thêm các trường vào panel
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Mã bệnh nhân:"), gbc);
        gbc.gridx = 1; formPanel.add(patientIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Nhiệt độ (°C):"), gbc);
        gbc.gridx = 1; formPanel.add(temperatureField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Binh thường: 36.1-37.2°C"), gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Huyết áp tâm thu (mmHg):"), gbc);
        gbc.gridx = 1; formPanel.add(systolicBPField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Bình thường: 90-120 mmHg"), gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Huyết áp tâm trương (mmHg):"), gbc);
        gbc.gridx = 1; formPanel.add(diastolicBPField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Bình thường: 60-80 mmHg"), gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Nhịp tim (bpm):"), gbc);
        gbc.gridx = 1; formPanel.add(heartRateField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Bình thường: 60-100 bpm"), gbc);

        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("SpO2 (%):"), gbc);
        gbc.gridx = 1; formPanel.add(spO2Field, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Bình thường: ≥ 95%"), gbc);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        if (canEdit) {
            JButton saveButton = new JButton("Lưu");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveVitalSigns();
                }
            });

            JButton clearButton = new JButton("Xóa trắng");
            clearButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearInputFields();
                }
            });

            buttonPanel.add(saveButton);
            buttonPanel.add(clearButton);
        }

        // Panel quan sát
        JPanel recentPanel = new JPanel(new BorderLayout(5, 5));
        recentPanel.setBorder(BorderFactory.createTitledBorder("Dấu hiệu sinh tồn gần đây"));

        JTextArea recentDataArea = new JTextArea(5, 30);
        recentDataArea.setEditable(false);

        // Hiển thị bản ghi mới nhất nếu có
        if (!vitalSignsList.isEmpty()) {
            VitalSigns latest = vitalSignsList.get(vitalSignsList.size() - 1);
            recentDataArea.setText(createDetailedVitalSignsText(latest));
        } else {
            recentDataArea.setText("Chưa có dữ liệu");
        }

        recentPanel.add(new JScrollPane(recentDataArea), BorderLayout.CENTER);

        // Thêm các panel con vào panel chính
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(recentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo panel hiển thị lịch sử
     * @return Panel lịch sử
     */
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Tạo bảng hiển thị lịch sử dấu hiệu sinh tồn
        String[] columnNames = {"Thời gian", "Nhiệt độ (°C)", "Huyết áp (mmHg)", "Nhịp tim (bpm)", "SpO2 (%)"};
        vitalSignsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };

        vitalSignsTable = new JTable(vitalSignsTableModel);
        vitalSignsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        vitalSignsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        vitalSignsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        vitalSignsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        vitalSignsTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        updateVitalSignsTable();

        // Panel hiển thị chi tiết bản ghi được chọn
        JPanel detailPanel = new JPanel(new BorderLayout(5, 5));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết"));

        JTextArea detailArea = new JTextArea(5, 30);
        detailArea.setEditable(false);

        // Hiển thị chi tiết khi chọn một bản ghi
        vitalSignsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = vitalSignsTable.getSelectedRow();
                    if (selectedRow >= 0 && selectedRow < vitalSignsList.size()) {
                        VitalSigns selected = vitalSignsList.get(selectedRow);
                        detailArea.setText(createDetailedVitalSignsText(selected));
                    }
                }
            }
        });

        detailPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);

        // Panel chức năng
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        if (medicalStaffMode) {
            JButton deleteButton = new JButton("Xóa bản ghi");
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteSelectedRecord();
                }
            });

            actionPanel.add(deleteButton);
        }

        // Panel bộ lọc
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc"));

        JLabel fromLabel = new JLabel("Từ ngày:");
        JTextField fromDateField = new JTextField(10);
        JLabel toLabel = new JLabel("Đến ngày:");
        JTextField toDateField = new JTextField(10);

        JButton filterButton = new JButton("Lọc");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Xử lý lọc dữ liệu theo ngày
                // (Phần này có thể triển khai sau)
            }
        });

        filterPanel.add(fromLabel);
        filterPanel.add(fromDateField);
        filterPanel.add(toLabel);
        filterPanel.add(toDateField);
        filterPanel.add(filterButton);

        // Thêm vào panel chính
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(filterPanel, BorderLayout.CENTER);

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(vitalSignsTable), BorderLayout.CENTER);
        panel.add(detailPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo panel biểu đồ
     * @return Panel biểu đồ
     */
    private JPanel createChartPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Trong thực tế, đây sẽ là nơi hiển thị biểu đồ thực sự
        // Ví dụ sử dụng JFreeChart hoặc các thư viện biểu đồ khác

        JLabel chartLabel = new JLabel("Biểu đồ dấu hiệu sinh tồn");
        chartLabel.setFont(new Font("Arial", Font.BOLD, 16));
        chartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel temperatureChartPlaceholder = new JPanel();
        temperatureChartPlaceholder.setBorder(BorderFactory.createTitledBorder("Biểu đồ nhiệt độ"));
        temperatureChartPlaceholder.setPreferredSize(new Dimension(800, 200));
        temperatureChartPlaceholder.setMinimumSize(new Dimension(600, 200));

        JPanel bpChartPlaceholder = new JPanel();
        bpChartPlaceholder.setBorder(BorderFactory.createTitledBorder("Biểu đồ huyết áp"));
        bpChartPlaceholder.setPreferredSize(new Dimension(800, 200));
        bpChartPlaceholder.setMinimumSize(new Dimension(600, 200));

        JPanel heartRateChartPlaceholder = new JPanel();
        heartRateChartPlaceholder.setBorder(BorderFactory.createTitledBorder("Biểu đồ nhịp tim"));
        heartRateChartPlaceholder.setPreferredSize(new Dimension(800, 200));
        heartRateChartPlaceholder.setMinimumSize(new Dimension(600, 200));

        JPanel spO2ChartPlaceholder = new JPanel();
        spO2ChartPlaceholder.setBorder(BorderFactory.createTitledBorder("Biểu đồ SpO2"));
        spO2ChartPlaceholder.setPreferredSize(new Dimension(800, 200));
        spO2ChartPlaceholder.setMinimumSize(new Dimension(600, 200));

        panel.add(chartLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(temperatureChartPlaceholder);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(bpChartPlaceholder);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(heartRateChartPlaceholder);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(spO2ChartPlaceholder);

        return panel;
    }

    /**
     * Cập nhật dữ liệu bảng dấu hiệu sinh tồn
     */
    private void updateVitalSignsTable() {
        // Xóa dữ liệu cũ
        vitalSignsTableModel.setRowCount(0);

        if (vitalSignsList.isEmpty()) return;

        // Thêm dữ liệu mới
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for (VitalSigns vs : vitalSignsList) {
            Object[] rowData = new Object[5];
            rowData[0] = vs.getRecordTime().format(formatter);
            rowData[1] = String.format("%.1f", vs.getTemperature());
            rowData[2] = vs.getSystolicBP() + "/" + vs.getDiastolicBP();
            rowData[3] = vs.getHeartRate();
            rowData[4] = vs.getSpO2();

            vitalSignsTableModel.addRow(rowData);
        }
    }

    /**
     * Cập nhật panel biểu đồ
     */
    private void updateChartPanel() {
        // Trong thực tế, đây sẽ là nơi cập nhật dữ liệu cho các biểu đồ
        // Trong ví dụ này, chúng ta chỉ giả lập việc này
    }

    /**
     * Lưu dấu hiệu sinh tồn mới
     */
    private void saveVitalSigns() {
        if (currentPatientId == null || currentPatientId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không có thông tin mã bệnh nhân",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Lấy dữ liệu từ các trường
            double temperature = Double.parseDouble(temperatureField.getText().trim());
            int systolicBP = Integer.parseInt(systolicBPField.getText().trim());
            int diastolicBP = Integer.parseInt(diastolicBPField.getText().trim());
            int heartRate = Integer.parseInt(heartRateField.getText().trim());
            int spO2 = Integer.parseInt(spO2Field.getText().trim());

            // Tạo đối tượng VitalSigns mới
            VitalSigns newVitalSigns = new VitalSigns(
                    UUID.randomUUID().toString(),
                    currentPatientId,
                    temperature,
                    systolicBP,
                    diastolicBP,
                    heartRate,
                    spO2,
                    LocalDateTime.now()
            );

            // Thêm vào danh sách
            vitalSignsList.add(newVitalSigns);

            // Cập nhật giao diện
            updateVitalSignsTable();
            updateChartPanel();

            // Xóa trắng các trường nhập
            clearInputFields();

            JOptionPane.showMessageDialog(this,
                    "Đã lưu dấu hiệu sinh tồn mới",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Dữ liệu nhập không hợp lệ. Vui lòng kiểm tra lại các giá trị số.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi dữ liệu: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xóa trắng các trường nhập liệu
     */
    private void clearInputFields() {
        temperatureField.setText("");
        systolicBPField.setText("");
        diastolicBPField.setText("");
        heartRateField.setText("");
        spO2Field.setText("");
    }

    /**
     * Xóa bản ghi được chọn
     */
    private void deleteSelectedRecord() {
        int selectedRow = vitalSignsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một bản ghi để xóa",
                    "Chưa chọn bản ghi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Xác nhận xóa
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa bản ghi này không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            // Xóa bản ghi
            vitalSignsList.remove(selectedRow);

            // Cập nhật giao diện
            updateVitalSignsTable();
            updateChartPanel();
        }
    }

    /**
     * Tạo văn bản mô tả chi tiết dấu hiệu sinh tồn
     * @param vs Đối tượng dấu hiệu sinh tồn
     * @return Chuỗi văn bản mô tả chi tiết
     */
    private String createDetailedVitalSignsText(VitalSigns vs) {
        if (vs == null) return "Không có dữ liệu";

        StringBuilder sb = new StringBuilder();

        sb.append("Thời gian ghi nhận: ").append(vs.getFormattedRecordTime()).append("\n");
        sb.append("Mã bệnh nhân: ").append(vs.getPatientId()).append("\n\n");

        sb.append("Nhiệt độ: ").append(String.format("%.1f°C", vs.getTemperature()));
        if (vs.getTemperature() < 36.1) {
            sb.append(" (Thấp)");
        } else if (vs.getTemperature() > 37.2) {
            sb.append(" (Cao)");
        } else {
            sb.append(" (Bình thường)");
        }
        sb.append("\n");

        sb.append("Huyết áp: ").append(vs.getSystolicBP()).append("/").append(vs.getDiastolicBP()).append(" mmHg");
        if (vs.getSystolicBP() < 90 || vs.getDiastolicBP() < 60) {
            sb.append(" (Thấp)");
        } else if (vs.getSystolicBP() > 120 || vs.getDiastolicBP() > 80) {
            sb.append(" (Cao)");
        } else {
            sb.append(" (Bình thường)");
        }
        sb.append("\n");

        sb.append("Nhịp tim: ").append(vs.getHeartRate()).append(" bpm");
        if (vs.getHeartRate() < 60) {
            sb.append(" (Chậm)");
        } else if (vs.getHeartRate() > 100) {
            sb.append(" (Nhanh)");
        } else {
            sb.append(" (Bình thường)");
        }
        sb.append("\n");

        sb.append("SpO2: ").append(vs.getSpO2()).append("%");
        if (vs.getSpO2() < 95) {
            sb.append(" (Thấp)");
        } else {
            sb.append(" (Bình thường)");
        }
        sb.append("\n");

        return sb.toString();
    }

    /**
     * Phương thức main để kiểm thử panel
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Theo dõi dấu hiệu sinh tồn");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Tạo người dùng bác sĩ giả lập
                User doctorUser = new User("doctor1", "password", "Bác sĩ Nguyễn Văn A", "doctor@hospital.com", "0123456789", Role.DOCTOR);

                // Tạo một số dữ liệu mẫu
                List<VitalSigns> sampleData = new ArrayList<>();
                try {
                    sampleData.add(new VitalSigns("VS001", "BN001", 36.5, 120, 80, 72, 98, LocalDateTime.now().minusHours(5)));
                    sampleData.add(new VitalSigns("VS002", "BN001", 37.1, 125, 85, 78, 97, LocalDateTime.now().minusHours(4)));
                    sampleData.add(new VitalSigns("VS003", "BN001", 37.8, 130, 90, 85, 96, LocalDateTime.now().minusHours(3)));
                    sampleData.add(new VitalSigns("VS004", "BN001", 37.5, 128, 88, 80, 95, LocalDateTime.now().minusHours(2)));
                    sampleData.add(new VitalSigns("VS005", "BN001", 37.2, 122, 82, 75, 97, LocalDateTime.now().minusHours(1)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Tạo panel theo dõi dấu hiệu sinh tồn
                VitalSignsPanel panel = new VitalSignsPanel(doctorUser, "BN001", sampleData);

                frame.getContentPane().add(panel);
                frame.setSize(900, 700);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
