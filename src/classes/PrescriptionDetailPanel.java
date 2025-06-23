package classes;


import model.entity.PrescriptionDetail;
import model.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel quản lý danh sách chi tiết đơn thuốc trong hệ thống y tế
 */
public class PrescriptionDetailPanel extends JPanel {

    private User currentUser;

    private List<PrescriptionDetail> prescriptionDetailList;
    private PrescriptionDetail currentDetail;

    private JTable detailTable;
    private DefaultTableModel detailTableModel;

    // Các label hiển thị chi tiết đơn thuốc
    private JLabel detailIdLabel;
    private JLabel prescriptionIdLabel;
    private JLabel medicationIdLabel;
    private JLabel dosageLabel;
    private JTextArea instructionsArea;
    private JLabel priceLabel;
    private JLabel quantityLabel;
    private JLabel totalCostLabel;

    /**
     * Constructor
     * @param user Người dùng hiện tại
     */
    public PrescriptionDetailPanel(User user) {
        this.currentUser = user;
        this.prescriptionDetailList = new ArrayList<>();
        this.currentDetail = null;

        initializeUI();
    }

    /**
     * Constructor với danh sách chi tiết đơn thuốc
     * @param user Người dùng hiện tại
     * @param details Danh sách chi tiết đơn thuốc
     */
    public PrescriptionDetailPanel(User user, List<PrescriptionDetail> details) {
        this.currentUser = user;
        this.prescriptionDetailList = new ArrayList<>(details);
        if (!details.isEmpty()) {
            this.currentDetail = details.get(0);
        }
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(250);

        JPanel topPanel = createDetailListPanel();
        JPanel bottomPanel = createDetailInfoPanel();

        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createDetailListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách chi tiết đơn thuốc"));

        String[] columnNames = {"ID chi tiết", "ID đơn thuốc", "ID thuốc", "Liều lượng", "Số lượng", "Giá", "Tổng tiền"};
        detailTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        detailTable = new JTable(detailTableModel);
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        detailTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        detailTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        detailTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        detailTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        detailTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        detailTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = detailTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < prescriptionDetailList.size()) {
                    currentDetail = prescriptionDetailList.get(selectedRow);
                    updateDetailInfo();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(detailTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Thêm mới");
        JButton editButton = new JButton("Chỉnh sửa");
        JButton deleteButton = new JButton("Xóa");

        addButton.addActionListener(e -> addDetail());
        editButton.addActionListener(e -> editDetail());
        deleteButton.addActionListener(e -> deleteDetail());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        updateTableData();

        return panel;
    }

    private JPanel createDetailInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết đơn thuốc"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5,5,5,5);

        detailIdLabel = new JLabel();
        prescriptionIdLabel = new JLabel();
        medicationIdLabel = new JLabel();
        dosageLabel = new JLabel();
        instructionsArea = new JTextArea(4, 30);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setEditable(false);
        JScrollPane instructionsScrollPane = new JScrollPane(instructionsArea);

        priceLabel = new JLabel();
        quantityLabel = new JLabel();
        totalCostLabel = new JLabel();

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID chi tiết:"), gbc);
        gbc.gridx = 1;
        panel.add(detailIdLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("ID đơn thuốc:"), gbc);
        gbc.gridx = 1;
        panel.add(prescriptionIdLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("ID thuốc:"), gbc);
        gbc.gridx = 1;
        panel.add(medicationIdLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Liều lượng:"), gbc);
        gbc.gridx = 1;
        panel.add(dosageLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Hướng dẫn:"), gbc);
        gbc.gridx = 1;
        panel.add(instructionsScrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Giá (VND):"), gbc);
        gbc.gridx = 1;
        panel.add(priceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        panel.add(quantityLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Tổng chi phí (VND):"), gbc);
        gbc.gridx = 1;
        panel.add(totalCostLabel, gbc);

        return panel;
    }

    private void updateTableData() {
        detailTableModel.setRowCount(0);
        for (PrescriptionDetail detail : prescriptionDetailList) {
            Object[] row = new Object[] {
                    detail.getDetailId(),
                    detail.getPrescriptionId(),
                    detail.getMedicationId(),
                    detail.getDosage(),
                    detail.getQuantity(),
                    String.format("%,.0f", detail.getPrice()),
                    String.format("%,.0f", detail.calculateCost())
            };
            detailTableModel.addRow(row);
        }
        if (detailTable.getRowCount() > 0) {
            detailTable.setRowSelectionInterval(0, 0);
        }
    }

    private void updateDetailInfo() {
        if (currentDetail == null) {
            detailIdLabel.setText("");
            prescriptionIdLabel.setText("");
            medicationIdLabel.setText("");
            dosageLabel.setText("");
            instructionsArea.setText("");
            priceLabel.setText("");
            quantityLabel.setText("");
            totalCostLabel.setText("");
            return;
        }

        detailIdLabel.setText(currentDetail.getDetailId());
        prescriptionIdLabel.setText(currentDetail.getPrescriptionId());
        medicationIdLabel.setText(currentDetail.getMedicationId());
        dosageLabel.setText(currentDetail.getDosage());
        instructionsArea.setText(currentDetail.getInstructions() != null ? currentDetail.getInstructions() : "");
        priceLabel.setText(String.format("%,.0f", currentDetail.getPrice()));
        quantityLabel.setText(String.valueOf(currentDetail.getQuantity()));
        totalCostLabel.setText(String.format("%,.0f", currentDetail.calculateCost()));
    }

    private void addDetail() {
        PrescriptionDetailFormDialog dialog = new PrescriptionDetailFormDialog(null);
        dialog.setVisible(true);
        PrescriptionDetail newDetail = dialog.getPrescriptionDetail();
        if (newDetail != null) {
            prescriptionDetailList.add(newDetail);
            updateTableData();
            detailTable.setRowSelectionInterval(prescriptionDetailList.size() - 1, prescriptionDetailList.size() - 1);
        }
    }

    private void editDetail() {
        if (currentDetail == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chi tiết đơn thuốc để chỉnh sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        PrescriptionDetailFormDialog dialog = new PrescriptionDetailFormDialog(currentDetail);
        dialog.setVisible(true);
        PrescriptionDetail updatedDetail = dialog.getPrescriptionDetail();
        if (updatedDetail != null) {
            int index = prescriptionDetailList.indexOf(currentDetail);
            if (index >= 0) {
                prescriptionDetailList.set(index, updatedDetail);
                currentDetail = updatedDetail;
                updateTableData();
                detailTable.setRowSelectionInterval(index, index);
            }
        }
    }

    private void deleteDetail() {
        if (currentDetail == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chi tiết đơn thuốc để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa chi tiết đơn thuốc này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            prescriptionDetailList.remove(currentDetail);
            currentDetail = null;
            updateTableData();
            updateDetailInfo();
        }
    }

    /**
     * Dialog nhập liệu chi tiết đơn thuốc (thêm/sửa)
     */
    private static class PrescriptionDetailFormDialog extends JDialog {
        private JTextField prescriptionIdField;
        private JTextField medicationIdField;
        private JTextField dosageField;
        private JTextArea instructionsArea;
        private JTextField priceField;
        private JTextField quantityField;

        private PrescriptionDetail prescriptionDetail;
        private boolean saved = false;

        public PrescriptionDetailFormDialog(PrescriptionDetail detail) {
            setTitle(detail == null ? "Thêm chi tiết đơn thuốc" : "Chỉnh sửa chi tiết đơn thuốc");
            setModal(true);
            setSize(400, 450);
            setLocationRelativeTo(null);
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.anchor = GridBagConstraints.WEST;

            JLabel prescriptionIdLabel = new JLabel("ID đơn thuốc:");
            prescriptionIdField = new JTextField(25);

            JLabel medicationIdLabel = new JLabel("ID thuốc:");
            medicationIdField = new JTextField(25);

            JLabel dosageLabel = new JLabel("Liều lượng:");
            dosageField = new JTextField(25);

            JLabel instructionsLabel = new JLabel("Hướng dẫn:");
            instructionsArea = new JTextArea(4, 25);
            instructionsArea.setLineWrap(true);
            instructionsArea.setWrapStyleWord(true);
            JScrollPane instructionsScrollPane = new JScrollPane(instructionsArea);

            JLabel priceLabel = new JLabel("Giá (VND):");
            priceField = new JTextField(25);

            JLabel quantityLabel = new JLabel("Số lượng:");
            quantityField = new JTextField(25);

            gbc.gridx = 0; gbc.gridy = 0;
            add(prescriptionIdLabel, gbc);
            gbc.gridx = 1;
            add(prescriptionIdField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            add(medicationIdLabel, gbc);
            gbc.gridx = 1;
            add(medicationIdField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            add(dosageLabel, gbc);
            gbc.gridx = 1;
            add(dosageField, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            add(instructionsLabel, gbc);
            gbc.gridx = 1;
            add(instructionsScrollPane, gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            add(priceLabel, gbc);
            gbc.gridx = 1;
            add(priceField, gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            add(quantityLabel, gbc);
            gbc.gridx = 1;
            add(quantityField, gbc);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Lưu");
            JButton cancelButton = new JButton("Hủy");

            saveButton.addActionListener(e -> {
                if (validateInput()) {
                    saved = true;
                    if (detail == null) {
                        prescriptionDetail = new PrescriptionDetail(
                                prescriptionIdField.getText().trim(),
                                medicationIdField.getText().trim(),
                                dosageField.getText().trim(),
                                instructionsArea.getText().trim(),
                                Double.parseDouble(priceField.getText().trim()),
                                Integer.parseInt(quantityField.getText().trim())
                        );
                    } else {
                        prescriptionDetail = new PrescriptionDetail(
                                detail.getDetailId(),
                                prescriptionIdField.getText().trim(),
                                medicationIdField.getText().trim(),
                                dosageField.getText().trim(),
                                instructionsArea.getText().trim(),
                                Double.parseDouble(priceField.getText().trim()),
                                Integer.parseInt(quantityField.getText().trim())
                        );
                    }
                    dispose();
                }
            });

            cancelButton.addActionListener(e -> {
                saved = false;
                prescriptionDetail = null;
                dispose();
            });

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            gbc.gridx = 0; gbc.gridy = 6;
            gbc.gridwidth = 2;
            add(buttonPanel, gbc);

            if (detail != null) {
                prescriptionIdField.setText(detail.getPrescriptionId());
                medicationIdField.setText(detail.getMedicationId());
                dosageField.setText(detail.getDosage());
                instructionsArea.setText(detail.getInstructions());
                priceField.setText(String.valueOf(detail.getPrice()));
                quantityField.setText(String.valueOf(detail.getQuantity()));
            }
        }

        private boolean validateInput() {
            if (prescriptionIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID đơn thuốc không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (medicationIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID thuốc không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (dosageField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Liều lượng không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                if (price < 0) {
                    JOptionPane.showMessageDialog(this, "Giá phải là số không âm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Giá không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }

        public PrescriptionDetail getPrescriptionDetail() {
            return saved ? prescriptionDetail : null;
        }
    }
}
