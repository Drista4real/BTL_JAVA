package classes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.entity.User;
import model.entity.Prescription;
import model.entity.PrescriptionDetail;

public class PrescriptionPanel extends JPanel {
    private User user;
    private JTable prescriptionTable;
    private DefaultTableModel prescriptionTableModel;
    private JTable detailTable;
    private DefaultTableModel detailTableModel;
    private JPanel detailPanel;
    private JLabel prescriptionInfoLabel;
    private List<Prescription> prescriptionList;

    public PrescriptionPanel(User user) {
        this.user = user;
        this.prescriptionList = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Đơn thuốc"));
        setBackground(Color.WHITE);

        // Tạo panel chính với layout dạng split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.3);

        // Panel hiển thị danh sách đơn thuốc
        JPanel prescriptionListPanel = createPrescriptionListPanel();
        splitPane.setTopComponent(prescriptionListPanel);

        // Panel hiển thị chi tiết đơn thuốc
        detailPanel = createPrescriptionDetailPanel();
        splitPane.setBottomComponent(detailPanel);

        add(splitPane, BorderLayout.CENTER);

        // Panel các nút chức năng
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Tải dữ liệu mẫu
        loadSamplePrescriptions();
    }

    /**
     * Tạo panel hiển thị danh sách đơn thuốc
     */
    private JPanel createPrescriptionListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách đơn thuốc"));

        // Tạo bảng hiển thị danh sách đơn thuốc
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

        // Khi người dùng chọn một đơn thuốc, hiển thị chi tiết
        prescriptionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && prescriptionTable.getSelectedRow() >= 0) {
                displayPrescriptionDetails(prescriptionList.get(prescriptionTable.getSelectedRow()));
            }
        });

        JScrollPane scrollPane = new JScrollPane(prescriptionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo panel hiển thị chi tiết đơn thuốc
     */
    private JPanel createPrescriptionDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn thuốc"));

        // Label hiển thị thông tin đơn thuốc được chọn
        prescriptionInfoLabel = new JLabel("Chọn đơn thuốc để xem chi tiết");
        prescriptionInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        prescriptionInfoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(prescriptionInfoLabel, BorderLayout.NORTH);

        // Bảng hiển thị chi tiết đơn thuốc
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

        // Panel hiển thị tổng chi phí
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Tổng chi phí: 0 VNĐ");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalPanel.add(totalLabel);
        panel.add(totalPanel, BorderLayout.SOUTH);

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
        refreshButton.addActionListener(e -> loadSamplePrescriptions());

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

    /**
     * Hiển thị dialog thêm đơn thuốc
     */
    private void showAddPrescriptionDialog() {
        // Triển khai chức năng thêm đơn thuốc
        JOptionPane.showMessageDialog(this,
                "Chức năng thêm đơn thuốc đang được phát triển",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Hiển thị chi tiết của đơn thuốc được chọn
     */
    private void displayPrescriptionDetails(Prescription prescription) {
        if (prescription == null) return;

        // Xóa dữ liệu cũ trong bảng chi tiết
        detailTableModel.setRowCount(0);

        // Cập nhật thông tin đơn thuốc
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        prescriptionInfoLabel.setText("<html>Mã đơn thuốc: <b>" + prescription.getPrescriptionId() +
                "</b> | Ngày kê: <b>" + prescription.getPrescriptionDate().format(formatter) +
                "</b> | Bác sĩ: <b>" + prescription.getDoctorId() +
                "</b> | Bệnh nhân: <b>" + prescription.getPatientId() + "</b></html>");

        // Hiển thị chi tiết đơn thuốc
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

        // Cập nhật tổng chi phí
        ((JLabel)((JPanel)detailPanel.getComponent(2)).getComponent(0)).setText("Tổng chi phí: " + formatCurrency(totalCost));
    }

    /**
     * In đơn thuốc
     */
    private void printPrescription(Prescription prescription) {
        if (prescription == null) return;

        // Tạo nội dung đơn thuốc để in
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
        content.append("       Để đảm bảo hiệu quả điều trị, không tự ý thay đổi liều lượng\n");
        content.append("       hoặc ngừng thuốc khi chưa có chỉ định của bác sĩ.\n");

        // Hiển thị xem trước đơn thuốc
        JTextArea textArea = new JTextArea(content.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        // Dialog xem trước đơn thuốc
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

    /**
     * Định dạng số tiền
     */
    private String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }

    /**
     * Tải dữ liệu mẫu đơn thuốc
     */
    private void loadSamplePrescriptions() {
        // Xóa dữ liệu cũ
        prescriptionList.clear();
        prescriptionTableModel.setRowCount(0);
        detailTableModel.setRowCount(0);
        prescriptionInfoLabel.setText("Chọn đơn thuốc để xem chi tiết");

        try {
            // Tạo dữ liệu mẫu
            // Đơn thuốc 1
            Prescription prescription1 = new Prescription("PRE-001", "PT-001", "DR-001", LocalDate.now(), new ArrayList<>());

            // Thêm chi tiết đơn thuốc - sử dụng PrescriptionDetail thực tế
            PrescriptionDetail detail1 = new PrescriptionDetail(
                    prescription1.getPrescriptionId(),
                    "MED-001",
                    "500mg",
                    "Uống 1 viên mỗi 6 giờ",
                    2000,
                    20
            );

            PrescriptionDetail detail2 = new PrescriptionDetail(
                    prescription1.getPrescriptionId(),
                    "MED-002",
                    "250mg",
                    "Uống 1 viên sau ăn, ngày 3 lần",
                    3000,
                    30
            );

            // Thêm chi tiết vào đơn thuốc - phương thức có thể thay đổi tùy vào cách triển khai của Prescription
            prescription1.getDetails().add(detail1);
            prescription1.getDetails().add(detail2);

            // Đơn thuốc 2
            Prescription prescription2 = new Prescription("PRE-002", "PT-001", "DR-002", LocalDate.now().minusDays(7), new ArrayList<>());

            PrescriptionDetail detail3 = new PrescriptionDetail(
                    prescription2.getPrescriptionId(),
                    "MED-003",
                    "1000mg",
                    "Uống 1 viên mỗi ngày",
                    5000,
                    10
            );

            PrescriptionDetail detail4 = new PrescriptionDetail(
                    prescription2.getPrescriptionId(),
                    "MED-004",
                    "20mg",
                    "Uống 1 viên trước ăn sáng",
                    4000,
                    14
            );

            // Thêm chi tiết vào đơn thuốc
            prescription2.getDetails().add(detail3);
            prescription2.getDetails().add(detail4);

            // Thêm vào danh sách
            prescriptionList.add(prescription1);
            prescriptionList.add(prescription2);

            // Cập nhật bảng
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu mẫu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Thêm đơn thuốc mới
     */
    public void addPrescription(Prescription prescription) {
        if (prescription == null) return;

        prescriptionList.add(prescription);

        // Cập nhật bảng
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

    /**
     * Cập nhật danh sách đơn thuốc
     */
    public void updatePrescriptions(List<Prescription> prescriptions) {
        // Xóa dữ liệu cũ
        prescriptionList.clear();
        prescriptionTableModel.setRowCount(0);

        if (prescriptions == null) return;

        // Thêm dữ liệu mới
        prescriptionList.addAll(prescriptions);

        // Cập nhật bảng
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