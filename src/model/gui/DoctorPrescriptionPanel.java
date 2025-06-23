package model.gui;

import model.entity.Prescription;
import model.entity.PrescriptionDetail;
import model.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DoctorPrescriptionPanel extends JPanel {
    private User user;
    private JTable prescriptionTable;
    private DefaultTableModel prescriptionTableModel;
    private JTable detailTable;
    private DefaultTableModel detailTableModel;
    private JPanel detailPanel;
    private JLabel prescriptionInfoLabel;
    private List<Prescription> prescriptionList;

    public DoctorPrescriptionPanel(User user) {
        this.user = user;
        this.prescriptionList = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Đơn thuốc"));
        setBackground(Color.WHITE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.3);

        JPanel prescriptionListPanel = createPrescriptionListPanel();
        splitPane.setTopComponent(prescriptionListPanel);

        detailPanel = createPrescriptionDetailPanel();
        splitPane.setBottomComponent(detailPanel);

        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        loadPrescriptionsFromDB();
    }

    private JPanel createPrescriptionListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách đơn thuốc"));

        String[] columns = {"Mã đơn thuốc", "Ngày kê đơn", "Bác sĩ", "Số loại thuốc", "Tổng chi phí"};
        prescriptionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        prescriptionTable = new JTable(prescriptionTableModel);
        prescriptionTable.setRowHeight(28);
        prescriptionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        prescriptionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        prescriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        prescriptionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && prescriptionTable.getSelectedRow() >= 0) {
                displayPrescriptionDetails(prescriptionList.get(prescriptionTable.getSelectedRow()));
            }
        });

        JScrollPane scrollPane = new JScrollPane(prescriptionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPrescriptionDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn thuốc"));

        prescriptionInfoLabel = new JLabel("Chọn đơn thuốc để xem chi tiết");
        prescriptionInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        prescriptionInfoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(prescriptionInfoLabel, BorderLayout.NORTH);

        String[] detailColumns = {"Mã thuốc", "Liều lượng", "Hướng dẫn sử dụng", "Số lượng", "Đơn giá", "Thành tiền"};
        detailTableModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        detailTable = new JTable(detailTableModel);
        detailTable.setRowHeight(28);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane detailScrollPane = new JScrollPane(detailTable);
        panel.add(detailScrollPane, BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Tổng chi phí: 0 VNĐ");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalPanel.add(totalLabel);
        panel.add(totalPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> loadPrescriptionsFromDB());

        JButton printButton = new JButton("In đơn thuốc");
        printButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        printButton.addActionListener(e -> {
            if (prescriptionTable.getSelectedRow() >= 0) {
                printPrescription(prescriptionList.get(prescriptionTable.getSelectedRow()));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn một đơn thuốc để in",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton addButton = new JButton("Thêm đơn thuốc");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showAddPrescriptionDialog());

        panel.add(refreshButton);
        panel.add(addButton);
        panel.add(printButton);

        return panel;
    }

    private void showAddPrescriptionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm đơn thuốc", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel patientIdLabel = new JLabel("Mã bệnh nhân:");
        JTextField patientIdField = new JTextField(20);
        JLabel dateLabel = new JLabel("Ngày kê đơn (dd/MM/yyyy):");
        JTextField dateField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(patientIdLabel, gbc);
        gbc.gridx = 1;
        dialog.add(patientIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(dateLabel, gbc);
        gbc.gridx = 1;
        dialog.add(dateField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> {
            try {
                String patientId = patientIdField.getText().trim();
                String dateStr = dateField.getText().trim();

                // Kiểm tra định dạng PatientID
                if (!patientId.matches("P\\d{3}")) {
                    throw new IllegalArgumentException("Mã bệnh nhân phải có định dạng PXXX (X là số)");
                }

                // Kiểm tra PatientID có tồn tại trong bảng Patients
                try (Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "050705");
                     PreparedStatement stmt = conn.prepareStatement(
                             "SELECT PatientID FROM Patients WHERE PatientID = ?")) {
                    stmt.setString(1, patientId);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Mã bệnh nhân " + patientId + " không tồn tại");
                    }
                }

                // Kiểm tra và parse ngày kê đơn
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate date = LocalDate.parse(dateStr, formatter);

                // Kiểm tra DoctorID từ user.getUserId()
                String doctorId = user.getUserId();
                if (doctorId == null || doctorId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Không tìm thấy mã bác sĩ. Vui lòng kiểm tra đăng nhập hoặc liên hệ quản trị viên.");
                }

                // Tạo đối tượng Prescription
                Prescription prescription = new Prescription(patientId, doctorId, date);

                // Lưu vào cơ sở dữ liệu
                savePrescription(prescription);
                addPrescription(prescription);
                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }

    private void loadPrescriptionsFromDB() {
        prescriptionList.clear();
        prescriptionTableModel.setRowCount(0);
        detailTableModel.setRowCount(0);
        prescriptionInfoLabel.setText("Chọn đơn thuốc để xem chi tiết");

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false",
                "root", "050705")) {
            String query = "SELECT p.PrescriptionID, p.PatientID, p.DoctorID, p.PrescriptionDate " +
                    "FROM Prescriptions p WHERE p.DoctorID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Kiểm tra DoctorID trước khi truy vấn
                String doctorId = user.getUserId();
                if (doctorId == null || doctorId.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy mã bác sĩ. Vui lòng kiểm tra đăng nhập hoặc liên hệ quản trị viên.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                stmt.setString(1, doctorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Prescription prescription = new Prescription(
                                rs.getString("PrescriptionID"),
                                rs.getString("PatientID"),
                                rs.getString("DoctorID"),
                                rs.getDate("PrescriptionDate").toLocalDate(),
                                loadPrescriptionDetails(conn, rs.getString("PrescriptionID"))
                        );
                        prescriptionList.add(prescription);
                    }
                }
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Prescription prescription : prescriptionList) {
                Object[] rowData = {
                        prescription.getPrescriptionId(),
                        prescription.getPrescriptionDate().format(formatter),
                        prescription.getDoctorId(),
                        prescription.getDetails().size(),
                        formatCurrency(prescription.calculateTotalCost())
                };
                prescriptionTableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<PrescriptionDetail> loadPrescriptionDetails(Connection conn, String prescriptionId) throws SQLException {
        List<PrescriptionDetail> details = new ArrayList<>();
        String query = "SELECT DetailID, PrescriptionID, MedicationID, Dosage, Instructions, Price, Quantity " +
                "FROM PrescriptionDetails WHERE PrescriptionID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, prescriptionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    details.add(new PrescriptionDetail(
                            rs.getString("DetailID"),
                            rs.getString("PrescriptionID"),
                            rs.getString("MedicationID"),
                            rs.getString("Dosage"),
                            rs.getString("Instructions"),
                            rs.getDouble("Price"),
                            rs.getInt("Quantity")
                    ));
                }
            }
        }
        return details;
    }

    private void savePrescription(Prescription prescription) throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false",
                "root", "050705")) {
            String query = "INSERT INTO Prescriptions (PrescriptionID, PatientID, DoctorID, PrescriptionDate) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, prescription.getPrescriptionId());
                stmt.setString(2, prescription.getPatientId());
                stmt.setString(3, prescription.getDoctorId());
                stmt.setDate(4, Date.valueOf(prescription.getPrescriptionDate()));
                stmt.executeUpdate();
            }
        }
    }

    private void displayPrescriptionDetails(Prescription prescription) {
        if (prescription == null) return;

        detailTableModel.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        prescriptionInfoLabel.setText("<html>Mã đơn thuốc: <b>" + prescription.getPrescriptionId() +
                "</b> | Ngày kê: <b>" + prescription.getPrescriptionDate().format(formatter) +
                "</b> | Bác sĩ: <b>" + prescription.getDoctorId() +
                "</b> | Bệnh nhân: <b>" + prescription.getPatientId() + "</b></html>");

        double totalCost = 0;
        for (PrescriptionDetail detail : prescription.getDetails()) {
            Object[] rowData = {
                    detail.getMedicationId(),
                    detail.getDosage(),
                    detail.getInstructions(),
                    detail.getQuantity(),
                    formatCurrency(detail.getPrice()),
                    formatCurrency(detail.calculateCost())
            };
            detailTableModel.addRow(rowData);
            totalCost += detail.calculateCost();
        }

        ((JLabel)((JPanel)detailPanel.getComponent(2)).getComponent(0)).setText("Tổng chi phí: " + formatCurrency(totalCost));
    }

    private void printPrescription(Prescription prescription) {
        if (prescription == null) return;

        StringBuilder content = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        content.append("                           ĐƠN THUỐC\n\n");
        content.append("Mã đơn thuốc: ").append(prescription.getPrescriptionId()).append("\n");
        content.append("Ngày kê đơn: ").append(prescription.getPrescriptionDate().format(formatter)).append("\n");
        content.append("Bác sĩ: ").append(prescription.getDoctorId()).append("\n");
        content.append("Bệnh nhân: ").append(prescription.getPatientId()).append("\n\n");

        content.append("DANH SÁCH THUỐC:\n");
        content.append("----------------------------------------------------------------\n");
        content.append(String.format("%-4s %-20s %-15s %-15s %-15s\n",
                "STT", "Mã thuốc", "Liều lượng", "Số lượng", "Hướng dẫn"));
        content.append("----------------------------------------------------------------\n");

        int stt = 1;
        for (PrescriptionDetail detail : prescription.getDetails()) {
            content.append(String.format("%-4d %-20s %-15s %-15d %-15s\n",
                    stt++,
                    detail.getMedicationId(),
                    detail.getDosage(),
                    detail.getQuantity(),
                    detail.getInstructions()));
        }

        content.append("----------------------------------------------------------------\n");
        content.append("Tổng chi phí: ").append(formatCurrency(prescription.calculateTotalCost())).append("\n\n");
        content.append("Lưu ý: Vui lòng tuân thủ đúng liều lượng và cách dùng thuốc.\n");

        JTextArea textArea = new JTextArea(content.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JDialog printDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "In đơn thuốc", true);
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

    private String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }

    public void addPrescription(Prescription prescription) {
        if (prescription == null) return;

        prescriptionList.add(prescription);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Object[] rowData = {
                prescription.getPrescriptionId(),
                prescription.getPrescriptionDate().format(formatter),
                prescription.getDoctorId(),
                prescription.getDetails().size(),
                formatCurrency(prescription.calculateTotalCost())
        };
        prescriptionTableModel.addRow(rowData);
    }

    public void updatePrescriptions(List<Prescription> prescriptions) {
        prescriptionList.clear();
        prescriptionTableModel.setRowCount(0);

        if (prescriptions == null) return;

        prescriptionList.addAll(prescriptions);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Prescription prescription : prescriptionList) {
            Object[] rowData = {
                    prescription.getPrescriptionId(),
                    prescription.getPrescriptionDate().format(formatter),
                    prescription.getDoctorId(),
                    prescription.getDetails().size(),
                    formatCurrency(prescription.calculateTotalCost())
            };
            prescriptionTableModel.addRow(rowData);
        }
    }
}