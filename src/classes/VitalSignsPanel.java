package classes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.entity.User;
import model.entity.VitalSigns;

/**
 * Panel hiển thị và quản lý các dấu hiệu sinh tồn của bệnh nhân
 */
public class VitalSignsPanel extends JPanel {
    private User user;
    private JTable vitalSignsTable;
    private DefaultTableModel vitalSignsTableModel;
    private JPanel detailPanel;
    private JLabel patientInfoLabel;
    private List<VitalSigns> vitalSignsList;

    // Các thành phần hiển thị chi tiết
    private JTextField temperatureField;
    private JTextField systolicBPField;
    private JTextField diastolicBPField;
    private JTextField heartRateField;
    private JTextField spO2Field;
    private JTextField recordTimeField;

    public VitalSignsPanel(User user) {
        this.user = user;
        this.vitalSignsList = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Dấu hiệu sinh tồn"));
        setBackground(Color.WHITE);

        // Tạo panel chính với layout dạng split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.3);

        // Panel hiển thị danh sách dấu hiệu sinh tồn
        JPanel vitalSignsListPanel = createVitalSignsListPanel();
        splitPane.setTopComponent(vitalSignsListPanel);

        // Panel hiển thị chi tiết dấu hiệu sinh tồn
        detailPanel = createVitalSignsDetailPanel();
        splitPane.setBottomComponent(detailPanel);

        add(splitPane, BorderLayout.CENTER);

        // Panel các nút chức năng
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Tải dữ liệu mẫu
        loadSampleVitalSigns();
    }

    /**
     * Tạo panel hiển thị danh sách dấu hiệu sinh tồn
     */
    private JPanel createVitalSignsListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Lịch sử dấu hiệu sinh tồn"));

        // Tạo bảng hiển thị danh sách dấu hiệu sinh tồn
        String[] columns = {"ID", "Bệnh nhân", "Nhiệt độ (°C)", "Huyết áp (mmHg)", "Nhịp tim (bpm)", "SpO2 (%)", "Thời gian"};
        vitalSignsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vitalSignsTable = new JTable(vitalSignsTableModel);
        vitalSignsTable.setRowHeight(28);
        vitalSignsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        vitalSignsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        vitalSignsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Khi người dùng chọn một bản ghi, hiển thị chi tiết
        vitalSignsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && vitalSignsTable.getSelectedRow() >= 0) {
                displayVitalSignsDetails(vitalSignsList.get(vitalSignsTable.getSelectedRow()));
            }
        });

        JScrollPane scrollPane = new JScrollPane(vitalSignsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Thêm panel tìm kiếm ở phía trên
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Tìm theo ID bệnh nhân:");
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Tìm kiếm");

        searchButton.addActionListener(e -> {
            String patientId = searchField.getText().trim();
            if (!patientId.isEmpty()) {
                filterByPatientId(patientId);
            } else {
                updateVitalSignsTable(vitalSignsList);
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        return panel;
    }

    /**
     * Tạo panel hiển thị chi tiết dấu hiệu sinh tồn
     */
    private JPanel createVitalSignsDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết dấu hiệu sinh tồn"));

        // Label hiển thị thông tin bệnh nhân
        patientInfoLabel = new JLabel("Chọn bản ghi để xem chi tiết");
        patientInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        patientInfoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(patientInfoLabel, BorderLayout.NORTH);

        // Panel nhập liệu chi tiết
        JPanel detailInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tạo các trường nhập liệu
        JLabel temperatureLabel = new JLabel("Nhiệt độ (°C):");
        temperatureField = new JTextField(10);
        temperatureField.setEditable(false);

        JLabel bpLabel = new JLabel("Huyết áp (mmHg):");
        JPanel bpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        systolicBPField = new JTextField(5);
        systolicBPField.setEditable(false);
        diastolicBPField = new JTextField(5);
        diastolicBPField.setEditable(false);
        bpPanel.add(systolicBPField);
        bpPanel.add(new JLabel("/"));
        bpPanel.add(diastolicBPField);

        JLabel heartRateLabel = new JLabel("Nhịp tim (bpm):");
        heartRateField = new JTextField(10);
        heartRateField.setEditable(false);

        JLabel spO2Label = new JLabel("SpO2 (%):");
        spO2Field = new JTextField(10);
        spO2Field.setEditable(false);

        JLabel recordTimeLabel = new JLabel("Thời gian ghi nhận:");
        recordTimeField = new JTextField(20);
        recordTimeField.setEditable(false);

        // Thêm các thành phần vào panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailInputPanel.add(temperatureLabel, gbc);

        gbc.gridx = 1;
        detailInputPanel.add(temperatureField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        detailInputPanel.add(bpLabel, gbc);

        gbc.gridx = 1;
        detailInputPanel.add(bpPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        detailInputPanel.add(heartRateLabel, gbc);

        gbc.gridx = 1;
        detailInputPanel.add(heartRateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        detailInputPanel.add(spO2Label, gbc);

        gbc.gridx = 1;
        detailInputPanel.add(spO2Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        detailInputPanel.add(recordTimeLabel, gbc);

        gbc.gridx = 1;
        detailInputPanel.add(recordTimeField, gbc);

        // Thêm đánh giá tình trạng
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Đánh giá"));
        JTextArea statusArea = new JTextArea(3, 30);
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        JScrollPane statusScrollPane = new JScrollPane(statusArea);
        statusPanel.add(statusScrollPane);
        detailInputPanel.add(statusPanel, gbc);

        panel.add(new JScrollPane(detailInputPanel), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo panel chứa các nút chức năng
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> loadSampleVitalSigns());

        JButton printButton = new JButton("In báo cáo");
        printButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        printButton.addActionListener(e -> {
            if (vitalSignsTable.getSelectedRow() >= 0) {
                printVitalSignsReport(vitalSignsList.get(vitalSignsTable.getSelectedRow()));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn một bản ghi để in",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton addButton = new JButton("Thêm bản ghi");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddVitalSignsDialog());

        panel.add(refreshButton);
        panel.add(addButton);
        panel.add(printButton);

        return panel;
    }

    /**
     * Hiển thị dialog thêm dấu hiệu sinh tồn
     */
    private void showAddVitalSignsDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm dấu hiệu sinh tồn", true);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel patientIdLabel = new JLabel("ID Bệnh nhân:");
        JTextField patientIdField = new JTextField(15);

        JLabel temperatureLabel = new JLabel("Nhiệt độ (°C):");
        JTextField temperatureField = new JTextField(10);

        JLabel systolicLabel = new JLabel("Huyết áp tâm thu (mmHg):");
        JTextField systolicField = new JTextField(10);

        JLabel diastolicLabel = new JLabel("Huyết áp tâm trương (mmHg):");
        JTextField diastolicField = new JTextField(10);

        JLabel heartRateLabel = new JLabel("Nhịp tim (bpm):");
        JTextField heartRateField = new JTextField(10);

        JLabel spO2Label = new JLabel("SpO2 (%):");
        JTextField spO2Field = new JTextField(10);

        // Thêm các thành phần vào panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(patientIdLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(patientIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(temperatureLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(temperatureField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(systolicLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(systolicField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(diastolicLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(diastolicField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(heartRateLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(heartRateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(spO2Label, gbc);

        gbc.gridx = 1;
        inputPanel.add(spO2Field, gbc);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> {
            try {
                // Tạo ID mới theo định dạng VS-XXX
                String id = "VS-" + String.format("%03d", vitalSignsList.size() + 1);

                // Thu thập dữ liệu từ form
                String patientId = patientIdField.getText().trim();
                double temperature = Double.parseDouble(temperatureField.getText().trim());
                int systolicBP = Integer.parseInt(systolicField.getText().trim());
                int diastolicBP = Integer.parseInt(diastolicField.getText().trim());
                int heartRate = Integer.parseInt(heartRateField.getText().trim());
                int spO2 = Integer.parseInt(spO2Field.getText().trim());

                // Tạo đối tượng VitalSigns mới
                VitalSigns vitalSigns = new VitalSigns(
                        id, patientId, temperature, systolicBP,
                        diastolicBP, heartRate, spO2, LocalDateTime.now()
                );

                // Thêm vào danh sách và cập nhật bảng
                addVitalSigns(vitalSigns);
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi: Vui lòng nhập đúng định dạng số",
                        "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Hiển thị chi tiết của dấu hiệu sinh tồn được chọn
     */
    private void displayVitalSignsDetails(VitalSigns vitalSigns) {
        if (vitalSigns == null) return;

        // Cập nhật thông tin bệnh nhân
        patientInfoLabel.setText("<html>ID: <b>" + vitalSigns.getId() +
                "</b> | Bệnh nhân: <b>" + vitalSigns.getPatientId() +
                "</b> | Thời gian: <b>" + vitalSigns.getFormattedRecordTime() + "</b></html>");

        // Hiển thị chi tiết
        temperatureField.setText(String.format("%.1f", vitalSigns.getTemperature()));
        systolicBPField.setText(String.valueOf(vitalSigns.getSystolicBP()));
        diastolicBPField.setText(String.valueOf(vitalSigns.getDiastolicBP()));
        heartRateField.setText(String.valueOf(vitalSigns.getHeartRate()));
        spO2Field.setText(String.valueOf(vitalSigns.getSpO2()));
        recordTimeField.setText(vitalSigns.getFormattedRecordTime());

        // Đánh giá tình trạng - tương ứng với JTextArea trong panel
        JTextArea statusArea = (JTextArea)((JScrollPane)((JPanel)detailPanel.getComponent(1))
                .getComponent(5)).getViewport().getView();

        StringBuilder status = new StringBuilder();

        // Đánh giá nhiệt độ
        if (vitalSigns.getTemperature() > 37.5) {
            status.append("- Sốt\n");
        } else if (vitalSigns.getTemperature() < 36.0) {
            status.append("- Thân nhiệt thấp\n");
        }

        // Đánh giá huyết áp
        if (vitalSigns.getSystolicBP() >= 140 || vitalSigns.getDiastolicBP() >= 90) {
            status.append("- Tăng huyết áp\n");
        } else if (vitalSigns.getSystolicBP() <= 90 || vitalSigns.getDiastolicBP() <= 60) {
            status.append("- Huyết áp thấp\n");
        }

        // Đánh giá nhịp tim
        if (vitalSigns.getHeartRate() > 100) {
            status.append("- Nhịp tim nhanh\n");
        } else if (vitalSigns.getHeartRate() < 60) {
            status.append("- Nhịp tim chậm\n");
        }

        // Đánh giá SpO2
        if (vitalSigns.getSpO2() < 95) {
            status.append("- Thiếu oxy máu\n");
        }

        if (status.length() == 0) {
            status.append("Các dấu hiệu sinh tồn trong ngưỡng bình thường.");
        }

        statusArea.setText(status.toString());
    }

    /**
     * In báo cáo dấu hiệu sinh tồn
     */
    private void printVitalSignsReport(VitalSigns vitalSigns) {
        if (vitalSigns == null) return;

        // Tạo nội dung báo cáo để in
        StringBuilder content = new StringBuilder();

        content.append("                      BÁO CÁO DẤU HIỆU SINH TỒN\n\n");
        content.append("Mã bản ghi: ").append(vitalSigns.getId()).append("\n");
        content.append("Bệnh nhân: ").append(vitalSigns.getPatientId()).append("\n");
        content.append("Thời gian ghi nhận: ").append(vitalSigns.getFormattedRecordTime()).append("\n\n");

        content.append("CHỈ SỐ ĐO LƯỜNG:\n");
        content.append("--------------------------------\n");
        content.append("Nhiệt độ: ").append(String.format("%.1f°C", vitalSigns.getTemperature())).append("\n");
        content.append("Huyết áp: ").append(vitalSigns.getSystolicBP()).append("/")
                .append(vitalSigns.getDiastolicBP()).append(" mmHg\n");
        content.append("Nhịp tim: ").append(vitalSigns.getHeartRate()).append(" bpm\n");
        content.append("SpO2: ").append(vitalSigns.getSpO2()).append("%\n");
        content.append("--------------------------------\n\n");

        // Đánh giá tình trạng
        content.append("ĐÁNH GIÁ:\n");

        boolean hasIssue = false;

        // Đánh giá nhiệt độ
        if (vitalSigns.getTemperature() > 37.5) {
            content.append("- Sốt\n");
            hasIssue = true;
        } else if (vitalSigns.getTemperature() < 36.0) {
            content.append("- Thân nhiệt thấp\n");
            hasIssue = true;
        }

        // Đánh giá huyết áp
        if (vitalSigns.getSystolicBP() >= 140 || vitalSigns.getDiastolicBP() >= 90) {
            content.append("- Tăng huyết áp\n");
            hasIssue = true;
        } else if (vitalSigns.getSystolicBP() <= 90 || vitalSigns.getDiastolicBP() <= 60) {
            content.append("- Huyết áp thấp\n");
            hasIssue = true;
        }

        // Đánh giá nhịp tim
        if (vitalSigns.getHeartRate() > 100) {
            content.append("- Nhịp tim nhanh\n");
            hasIssue = true;
        } else if (vitalSigns.getHeartRate() < 60) {
            content.append("- Nhịp tim chậm\n");
            hasIssue = true;
        }

        // Đánh giá SpO2
        if (vitalSigns.getSpO2() < 95) {
            content.append("- Thiếu oxy máu\n");
            hasIssue = true;
        }

        if (!hasIssue) {
            content.append("Các dấu hiệu sinh tồn trong ngưỡng bình thường.\n");
        }

        content.append("\nGhi chú: _________________________________________________\n\n");
        content.append("Bác sĩ ký tên: _____________________ Ngày: ______________\n");

        // Hiển thị xem trước báo cáo
        JTextArea textArea = new JTextArea(content.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        // Dialog xem trước báo cáo
        JDialog printDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "In báo cáo", true);
        printDialog.setLayout(new BorderLayout());
        printDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printButton = new JButton("In");
        printButton.addActionListener(e -> {
            try {
                textArea.print();
                printDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(printDialog,
                        "Lỗi khi in: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> printDialog.dispose());

        buttonPanel.add(printButton);
        buttonPanel.add(cancelButton);
        printDialog.add(buttonPanel, BorderLayout.SOUTH);

        printDialog.pack();
        printDialog.setLocationRelativeTo(this);
        printDialog.setVisible(true);
    }

    /**
     * Lọc danh sách theo ID bệnh nhân
     */
    private void filterByPatientId(String patientId) {
        List<VitalSigns> filteredList = new ArrayList<>();

        for (VitalSigns vs : vitalSignsList) {
            if (vs.getPatientId().contains(patientId)) {
                filteredList.add(vs);
            }
        }

        updateVitalSignsTable(filteredList);
    }

    /**
     * Cập nhật bảng hiển thị với danh sách được cung cấp
     */
    private void updateVitalSignsTable(List<VitalSigns> list) {
        vitalSignsTableModel.setRowCount(0);

        for (VitalSigns vs : list) {
            Object[] rowData = {
                    vs.getId(),
                    vs.getPatientId(),
                    String.format("%.1f", vs.getTemperature()),
                    vs.getSystolicBP() + "/" + vs.getDiastolicBP(),
                    vs.getHeartRate(),
                    vs.getSpO2(),
                    vs.getFormattedRecordTime()
            };
            vitalSignsTableModel.addRow(rowData);
        }
    }

    /**
     * Tải dữ liệu mẫu dấu hiệu sinh tồn
     */
    private void loadSampleVitalSigns() {
        // Xóa dữ liệu cũ
        vitalSignsList.clear();
        vitalSignsTableModel.setRowCount(0);
        patientInfoLabel.setText("Chọn bản ghi để xem chi tiết");

        try {
            // Tạo dữ liệu mẫu
            LocalDateTime now = LocalDateTime.now();

            // Bản ghi 1 - Bình thường
            VitalSigns vs1 = new VitalSigns(
                    "VS-001", "PT-001", 36.8, 120, 80, 75, 98, now
            );

            // Bản ghi 2 - Sốt, tăng nhịp tim
            VitalSigns vs2 = new VitalSigns(
                    "VS-002", "PT-002", 38.5, 125, 85, 110, 96, now.minusHours(2)
            );

            // Bản ghi 3 - Huyết áp cao
            VitalSigns vs3 = new VitalSigns(
                    "VS-003", "PT-001", 36.9, 150, 95, 85, 97, now.minusDays(1)
            );

            // Bản ghi 4 - SpO2 thấp
            VitalSigns vs4 = new VitalSigns(
                    "VS-004", "PT-003", 37.2, 115, 75, 90, 92, now.minusDays(2)
            );

            // Thêm vào danh sách
            vitalSignsList.add(vs1);
            vitalSignsList.add(vs2);
            vitalSignsList.add(vs3);
            vitalSignsList.add(vs4);

            // Cập nhật bảng
            updateVitalSignsTable(vitalSignsList);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu mẫu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Thêm bản ghi dấu hiệu sinh tồn mới
     */
    public void addVitalSigns(VitalSigns vitalSigns) {
        if (vitalSigns == null) return;

        vitalSignsList.add(vitalSigns);

        // Cập nhật bảng
        Object[] rowData = {
                vitalSigns.getId(),
                vitalSigns.getPatientId(),
                String.format("%.1f", vitalSigns.getTemperature()),
                vitalSigns.getSystolicBP() + "/" + vitalSigns.getDiastolicBP(),
                vitalSigns.getHeartRate(),
                vitalSigns.getSpO2(),
                vitalSigns.getFormattedRecordTime()
        };
        vitalSignsTableModel.addRow(rowData);
    }

    /**
     * Cập nhật danh sách dấu hiệu sinh tồn
     */
    public void updateVitalSigns(List<VitalSigns> vitalSigns) {
        // Xóa dữ liệu cũ
        vitalSignsList.clear();
        vitalSignsTableModel.setRowCount(0);

        if (vitalSigns == null) return;

        // Thêm dữ liệu mới
        vitalSignsList.addAll(vitalSigns);

        // Cập nhật bảng
        updateVitalSignsTable(vitalSignsList);
    }
}
