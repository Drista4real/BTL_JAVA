package model.gui;

import model.entity.Invoice;
import model.entity.InvoiceDetail;
import model.entity.User;
import model.entity.Invoice.InvoiceItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DoctorInvoiceDetailPanel extends JPanel {
    private User currentUser;
    private List<InvoiceDetail> invoiceDetailList;
    private InvoiceDetail currentDetail;
    private String currentInvoiceId; // Lưu InvoiceID của hóa đơn hiện tại
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement?useUnicode=true&characterEncoding=UTF-8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "050705";

    private JTable detailsTable;
    private DefaultTableModel detailsTableModel;
    private JLabel serviceNameLabel;
    private JLabel serviceCodeLabel;
    private JLabel quantityLabel;
    private JLabel unitPriceLabel;
    private JLabel totalPriceLabel;
    private JLabel discountLabel;
    private JLabel finalPriceLabel;
    private JLabel categoryLabel;
    private JLabel unitLabel;
    private JLabel statusLabel;
    private JLabel prescribedByLabel;
    private JLabel prescribedDateLabel;
    private JLabel performedByLabel;
    private JLabel performedDateLabel;
    private JLabel createdInfoLabel;
    private JLabel updatedInfoLabel;
    private JTextArea descriptionArea;

    public DoctorInvoiceDetailPanel(User user, Invoice invoice) {
        this.currentUser = user;
        this.invoiceDetailList = new ArrayList<>();

        if (invoice != null) {
            this.currentInvoiceId = invoice.getInvoiceId();
        }

        // Khởi tạo UI (bao gồm cả detailsTableModel) trước khi gọi các phương thức cập nhật
        initializeUI();

        // Sau đó mới thiết lập dữ liệu
        if (invoice != null && invoice.getItems() != null) {
            setInvoiceDetails(invoice.getItems());
        }
    }

    public void setInvoiceDetails(List<InvoiceItem> items) {
        this.invoiceDetailList.clear();
        if (items != null && currentInvoiceId != null) {
            for (InvoiceItem item : items) {
                InvoiceDetail detail = new InvoiceDetail(
                        currentInvoiceId, // Gán InvoiceID
                        item.getItemId(),
                        item.getItemName(),
                        item.getItemCode(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getCategory()
                );
                this.invoiceDetailList.add(detail);
            }
        }
        this.currentDetail = invoiceDetailList.isEmpty() ? null : invoiceDetailList.get(0);
        updateTableData();
        updateDetailInfo();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerLocation(250);

        JPanel topPanel = createDetailsListPanel();
        JPanel bottomPanel = createDetailInfoPanel();

        mainSplitPane.setTopComponent(topPanel);
        mainSplitPane.setBottomComponent(bottomPanel);

        add(mainSplitPane, BorderLayout.CENTER);
    }

    private JPanel createDetailsListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách chi tiết hóa đơn"));

        String[] columnNames = {"Mã", "Tên dịch vụ/sản phẩm", "SL", "Đơn giá", "Tổng tiền", "Giảm giá", "Thành tiền", "Trạng thái"};
        detailsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        detailsTable = new JTable(detailsTableModel);
        detailsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        detailsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        detailsTable.getColumnModel().getColumn(2).setPreferredWidth(40);
        detailsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        detailsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        detailsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        detailsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        detailsTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        detailsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = detailsTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < invoiceDetailList.size()) {
                    currentDetail = invoiceDetailList.get(selectedRow);
                    updateDetailInfo();
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(detailsTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Thêm mới");
        JButton editButton = new JButton("Chỉnh sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton cancelButton = new JButton("Hủy chi tiết");
        JButton restoreButton = new JButton("Khôi phục");

        addButton.addActionListener(e -> addNewDetail());
        editButton.addActionListener(e -> editDetail());
        deleteButton.addActionListener(e -> deleteDetail());
        cancelButton.addActionListener(e -> {
            if (currentDetail != null && !currentDetail.isCancelled()) {
                String reason = JOptionPane.showInputDialog(this, "Nhập lý do hủy:", "Hủy chi tiết", JOptionPane.QUESTION_MESSAGE);
                if (reason != null && !reason.trim().isEmpty()) {
                    currentDetail.cancel(reason);
                    currentDetail.setUpdatedBy(currentUser.getUserId());
                    updateDetailInDatabase(currentDetail);
                    updateTableData();
                    updateDetailInfo();
                }
            }
        });
        restoreButton.addActionListener(e -> {
            if (currentDetail != null && currentDetail.isCancelled()) {
                currentDetail.restore();
                currentDetail.setUpdatedBy(currentUser.getUserId());
                updateDetailInDatabase(currentDetail);
                updateTableData();
                updateDetailInfo();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(restoreButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        updateTableData();

        return panel;
    }

    private void addNewDetail() {
        if (currentInvoiceId == null || currentInvoiceId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn trước khi thêm chi tiết!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField serviceNameField = new JTextField();
        JTextField serviceCodeField = new JTextField();
        JTextField quantityField = new JTextField("1");
        JTextField unitPriceField = new JTextField();
        JTextField categoryField = new JTextField();

        panel.add(new JLabel("Tên dịch vụ:"));
        panel.add(serviceNameField);
        panel.add(new JLabel("Mã dịch vụ:"));
        panel.add(serviceCodeField);
        panel.add(new JLabel("Số lượng:"));
        panel.add(quantityField);
        panel.add(new JLabel("Đơn giá:"));
        panel.add(unitPriceField);
        panel.add(new JLabel("Loại dịch vụ:"));
        panel.add(categoryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm chi tiết mới", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String serviceName = serviceNameField.getText().trim();
                String serviceCode = serviceCodeField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
                String category = categoryField.getText().trim();

                if (serviceName.isEmpty() || serviceCode.isEmpty() || category.isEmpty()) {
                    throw new IllegalArgumentException("Tên dịch vụ, mã dịch vụ và loại dịch vụ không được để trống!");
                }
                if (quantity <= 0) {
                    throw new IllegalArgumentException("Số lượng phải lớn hơn 0!");
                }
                if (unitPrice < 0) {
                    throw new IllegalArgumentException("Đơn giá không được âm!");
                }

                InvoiceDetail newDetail = new InvoiceDetail(
                        currentInvoiceId,
                        "SV" + System.currentTimeMillis(),
                        serviceName,
                        serviceCode,
                        quantity,
                        unitPrice,
                        category
                );
                newDetail.setCreatedBy(currentUser.getUserId());
                saveDetailToDatabase(newDetail);
                invoiceDetailList.add(newDetail);
                updateTableData();
                JOptionPane.showMessageDialog(this, "Thêm chi tiết thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm chi tiết: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editDetail() {
        if (currentDetail == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chi tiết để chỉnh sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField serviceNameField = new JTextField(currentDetail.getServiceName());
        JTextField serviceCodeField = new JTextField(currentDetail.getServiceCode());
        JTextField quantityField = new JTextField(String.valueOf(currentDetail.getQuantity()));
        JTextField unitPriceField = new JTextField(String.valueOf(currentDetail.getUnitPrice()));
        JTextField categoryField = new JTextField(currentDetail.getCategory());

        panel.add(new JLabel("Tên dịch vụ:"));
        panel.add(serviceNameField);
        panel.add(new JLabel("Mã dịch vụ:"));
        panel.add(serviceCodeField);
        panel.add(new JLabel("Số lượng:"));
        panel.add(quantityField);
        panel.add(new JLabel("Đơn giá:"));
        panel.add(unitPriceField);
        panel.add(new JLabel("Loại dịch vụ:"));
        panel.add(categoryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Chỉnh sửa chi tiết", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String serviceName = serviceNameField.getText().trim();
                String serviceCode = serviceCodeField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
                String category = categoryField.getText().trim();

                if (serviceName.isEmpty() || serviceCode.isEmpty() || category.isEmpty()) {
                    throw new IllegalArgumentException("Tên dịch vụ, mã dịch vụ và loại dịch vụ không được để trống!");
                }
                if (quantity <= 0) {
                    throw new IllegalArgumentException("Số lượng phải lớn hơn 0!");
                }
                if (unitPrice < 0) {
                    throw new IllegalArgumentException("Đơn giá không được âm!");
                }

                currentDetail.setServiceName(serviceName);
                currentDetail.setServiceCode(serviceCode);
                currentDetail.updateQuantity(quantity);
                currentDetail.setUnitPrice(unitPrice);
                currentDetail.setCategory(category);
                currentDetail.setUpdatedBy(currentUser.getUserId());
                updateDetailInDatabase(currentDetail);
                updateTableData();
                updateDetailInfo();
                JOptionPane.showMessageDialog(this, "Chỉnh sửa chi tiết thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi chỉnh sửa chi tiết: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteDetail() {
        if (currentDetail == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chi tiết để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa chi tiết này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String query = "DELETE FROM InvoiceDetails WHERE DetailID = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, currentDetail.getDetailId());
                stmt.executeUpdate();

                invoiceDetailList.remove(currentDetail);
                currentDetail = invoiceDetailList.isEmpty() ? null : invoiceDetailList.get(0);
                updateTableData();
                updateDetailInfo();
                JOptionPane.showMessageDialog(this, "Xóa chi tiết thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa chi tiết: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveDetailToDatabase(InvoiceDetail detail) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "INSERT INTO InvoiceDetails (DetailID, InvoiceID, ServiceID, ServiceName, ServiceCode, Quantity, UnitPrice, TotalPrice, DiscountPercent, DiscountAmount, FinalPrice, Category, Unit, CreatedAt, CreatedBy) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, detail.getDetailId());
            stmt.setString(2, detail.getInvoiceId());
            stmt.setString(3, detail.getServiceId());
            stmt.setString(4, detail.getServiceName());
            stmt.setString(5, detail.getServiceCode());
            stmt.setInt(6, detail.getQuantity());
            stmt.setDouble(7, detail.getUnitPrice());
            stmt.setDouble(8, detail.getTotalPrice());
            stmt.setDouble(9, detail.getDiscountPercent());
            stmt.setDouble(10, detail.getDiscountAmount());
            stmt.setDouble(11, detail.getFinalPrice());
            stmt.setString(12, detail.getCategory());
            stmt.setString(13, detail.getUnit());
            stmt.setTimestamp(14, java.sql.Timestamp.valueOf(detail.getCreatedAt()));
            stmt.setString(15, detail.getCreatedBy());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu chi tiết: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDetailInDatabase(InvoiceDetail detail) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "UPDATE InvoiceDetails SET ServiceName = ?, ServiceCode = ?, Quantity = ?, UnitPrice = ?, TotalPrice = ?, DiscountPercent = ?, DiscountAmount = ?, FinalPrice = ?, Category = ?, Unit = ?, UpdatedAt = ?, UpdatedBy = ?, IsCancelled = ?, CancelReason = ? WHERE DetailID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, detail.getServiceName());
            stmt.setString(2, detail.getServiceCode());
            stmt.setInt(3, detail.getQuantity());
            stmt.setDouble(4, detail.getUnitPrice());
            stmt.setDouble(5, detail.getTotalPrice());
            stmt.setDouble(6, detail.getDiscountPercent());
            stmt.setDouble(7, detail.getDiscountAmount());
            stmt.setDouble(8, detail.getFinalPrice());
            stmt.setString(9, detail.getCategory());
            stmt.setString(10, detail.getUnit());
            stmt.setTimestamp(11, detail.getUpdatedAt() != null ? java.sql.Timestamp.valueOf(detail.getUpdatedAt()) : null);
            stmt.setString(12, detail.getUpdatedBy());
            stmt.setBoolean(13, detail.isCancelled());
            stmt.setString(14, detail.getCancelReason());
            stmt.setString(15, detail.getDetailId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật chi tiết: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel createDetailInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết"));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        serviceNameLabel = new JLabel();
        serviceCodeLabel = new JLabel();
        quantityLabel = new JLabel();
        unitPriceLabel = new JLabel();
        totalPriceLabel = new JLabel();
        discountLabel = new JLabel();
        finalPriceLabel = new JLabel();
        categoryLabel = new JLabel();
        unitLabel = new JLabel();
        statusLabel = new JLabel();
        prescribedByLabel = new JLabel();
        prescribedDateLabel = new JLabel();
        performedByLabel = new JLabel();
        performedDateLabel = new JLabel();
        createdInfoLabel = new JLabel();
        updatedInfoLabel = new JLabel();

        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);

        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Tên dịch vụ/sản phẩm:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        infoPanel.add(serviceNameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Mã sản phẩm:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        infoPanel.add(serviceCodeLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        infoPanel.add(quantityLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Đơn vị:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        infoPanel.add(unitLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        infoPanel.add(new JLabel("Phân loại:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        infoPanel.add(categoryLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        infoPanel.add(new JLabel("Đơn giá:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        infoPanel.add(unitPriceLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        infoPanel.add(new JLabel("Tổng tiền:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        infoPanel.add(totalPriceLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        infoPanel.add(new JLabel("Giảm giá:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2;
        infoPanel.add(discountLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 3;
        infoPanel.add(new JLabel("Thành tiền:"), gbc);
        gbc.gridx = 3; gbc.gridy = 3;
        infoPanel.add(finalPriceLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 4;
        infoPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; gbc.gridy = 4;
        infoPanel.add(statusLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        infoPanel.add(new JLabel("Bác sĩ chỉ định:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        infoPanel.add(prescribedByLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 5;
        infoPanel.add(new JLabel("Ngày chỉ định:"), gbc);
        gbc.gridx = 3; gbc.gridy = 5;
        infoPanel.add(prescribedDateLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        infoPanel.add(new JLabel("Người thực hiện:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        infoPanel.add(performedByLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 6;
        infoPanel.add(new JLabel("Ngày thực hiện:"), gbc);
        gbc.gridx = 3; gbc.gridy = 6;
        infoPanel.add(performedDateLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        infoPanel.add(new JLabel("Thông tin tạo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        gbc.gridwidth = 3;
        infoPanel.add(createdInfoLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 1;
        infoPanel.add(new JLabel("Thông tin cập nhật:"), gbc);
        gbc.gridx = 1; gbc.gridy = 8;
        gbc.gridwidth = 3;
        infoPanel.add(updatedInfoLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 1;
        infoPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.gridy = 9;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(descScrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Sửa thông tin");
        JButton recordPerformedButton = new JButton("Ghi nhận thực hiện");

        editButton.addActionListener(e -> editDetail());
        recordPerformedButton.addActionListener(e -> {
            if (currentDetail != null && !currentDetail.isCancelled() && currentDetail.getPerformedBy() == null) {
                currentDetail.setPerformer(currentUser.getUserId(), LocalDateTime.now());
                currentDetail.setUpdatedBy(currentUser.getUserId());
                updateDetailInDatabase(currentDetail);
                updateDetailInfo();
            }
        });

        buttonPanel.add(recordPerformedButton);
        buttonPanel.add(editButton);

        panel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        updateDetailInfo();

        return panel;
    }

    private void updateTableData() {
        detailsTableModel.setRowCount(0);
        for (InvoiceDetail detail : invoiceDetailList) {
            Object[] rowData = new Object[8];
            rowData[0] = detail.getServiceCode() != null ? detail.getServiceCode() : "";
            rowData[1] = detail.getServiceName() != null ? detail.getServiceName() : "";
            rowData[2] = detail.getQuantity();
            rowData[3] = String.format("%,.0f", detail.getUnitPrice());
            rowData[4] = String.format("%,.0f", detail.getTotalPrice());
            rowData[5] = detail.getDiscountAmount() > 0 ? String.format("%,.0f (%s%%)", detail.getDiscountAmount(), detail.getDiscountPercent()) : "0";
            rowData[6] = String.format("%,.0f", detail.getFinalPrice());
            rowData[7] = detail.isCancelled() ? "Đã hủy" : "Hoạt động";
            detailsTableModel.addRow(rowData);
        }
        if (detailsTable.getRowCount() > 0) {
            detailsTable.setRowSelectionInterval(0, 0);
        }
    }

    private void updateDetailInfo() {
        if (currentDetail == null) {
            return;
        }

        serviceNameLabel.setText(currentDetail.getServiceName() != null ? currentDetail.getServiceName() : "");
        serviceCodeLabel.setText(currentDetail.getServiceCode() != null ? currentDetail.getServiceCode() : "");
        quantityLabel.setText(String.valueOf(currentDetail.getQuantity()));
        unitPriceLabel.setText(String.format("%,.0f VND", currentDetail.getUnitPrice()));
        totalPriceLabel.setText(String.format("%,.0f VND", currentDetail.getTotalPrice()));
        discountLabel.setText(currentDetail.getDiscountAmount() > 0 ?
                String.format("%,.0f VND (%s%%)", currentDetail.getDiscountAmount(), currentDetail.getDiscountPercent()) : "0 VND");
        finalPriceLabel.setText(String.format("%,.0f VND", currentDetail.getFinalPrice()));
        categoryLabel.setText(currentDetail.getCategory() != null ? currentDetail.getCategory() : "");
        unitLabel.setText(currentDetail.getUnit() != null ? currentDetail.getUnit() : "");

        if (currentDetail.isCancelled()) {
            statusLabel.setText("Đã hủy - " + (currentDetail.getCancelReason() != null ? currentDetail.getCancelReason() : ""));
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("Hoạt động");
            statusLabel.setForeground(new Color(0, 128, 0));
        }

        prescribedByLabel.setText(currentDetail.getPrescribedBy() != null ? currentDetail.getPrescribedBy() : "");
        prescribedDateLabel.setText(currentDetail.getPrescribedDate() != null ?
                currentDetail.getPrescribedDate().format(DATE_TIME_FORMATTER) : "");
        performedByLabel.setText(currentDetail.getPerformedBy() != null ? currentDetail.getPerformedBy() : "");
        performedDateLabel.setText(currentDetail.getPerformedDate() != null ?
                currentDetail.getPerformedDate().format(DATE_TIME_FORMATTER) : "");
        createdInfoLabel.setText(currentDetail.getCreatedBy() != null ?
                String.format("Tạo bởi: %s, lúc: %s", currentDetail.getCreatedBy(), currentDetail.getCreatedAt().format(DATE_TIME_FORMATTER)) : "");
        updatedInfoLabel.setText(currentDetail.getUpdatedAt() != null && currentDetail.getUpdatedBy() != null ?
                String.format("Cập nhật bởi: %s, lúc: %s", currentDetail.getUpdatedBy(), currentDetail.getUpdatedAt().format(DATE_TIME_FORMATTER)) : "");
        descriptionArea.setText(currentDetail.getDescription() != null ? currentDetail.getDescription() : "");
    }
}