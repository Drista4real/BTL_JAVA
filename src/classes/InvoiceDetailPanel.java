package classes;

import model.entity.InvoiceDetail;
import model.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel quản lý chi tiết hóa đơn trong hệ thống y tế
 */
public class InvoiceDetailPanel extends JPanel {

    // Người dùng hiện tại
    private User currentUser;

    // Danh sách và đối tượng chi tiết hóa đơn đang chọn
    private List<InvoiceDetail> invoiceDetailList;
    private InvoiceDetail currentDetail;

    // Format thời gian
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // UI components
    private JTable detailsTable;
    private DefaultTableModel detailsTableModel;
    private JPanel infoPanel;
    private JTextArea detailInfoArea;

    // Field hiển thị chi tiết
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

    /**
     * Constructor
     * @param user Người dùng hiện tại
     */
    public InvoiceDetailPanel(User user) {
        this.currentUser = user;
        this.invoiceDetailList = new ArrayList<>();
        this.currentDetail = null;

        initializeUI();
    }

    /**
     * Constructor với danh sách chi tiết hóa đơn
     * @param user Người dùng hiện tại
     * @param detailList Danh sách chi tiết hóa đơn
     */
    public InvoiceDetailPanel(User user, List<InvoiceDetail> detailList) {
        this.currentUser = user;
        this.invoiceDetailList = new ArrayList<>(detailList);

        if (!detailList.isEmpty()) {
            this.currentDetail = detailList.get(0);
        } else {
            this.currentDetail = null;
        }

        initializeUI();
    }

    /**
     * Khởi tạo giao diện người dùng
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo Split Pane chính - chia danh sách và chi tiết
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerLocation(250);

        // Panel trên - danh sách các chi tiết
        JPanel topPanel = createDetailsListPanel();

        // Panel dưới - thông tin chi tiết
        JPanel bottomPanel = createDetailInfoPanel();

        mainSplitPane.setTopComponent(topPanel);
        mainSplitPane.setBottomComponent(bottomPanel);

        add(mainSplitPane, BorderLayout.CENTER);
    }

    /**
     * Tạo panel danh sách chi tiết hóa đơn
     */
    private JPanel createDetailsListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách chi tiết hóa đơn"));

        // Tạo bảng danh sách chi tiết
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

        // Xử lý sự kiện khi chọn dòng
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

        // Tạo panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Thêm mới");
        JButton editButton = new JButton("Chỉnh sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton cancelButton = new JButton("Hủy chi tiết");
        JButton restoreButton = new JButton("Khôi phục");

        // Thêm sự kiện cho nút Hủy chi tiết
        cancelButton.addActionListener(e -> {
            if (currentDetail != null && !currentDetail.isCancelled()) {
                String reason = JOptionPane.showInputDialog(this, "Nhập lý do hủy:", "Hủy chi tiết", JOptionPane.QUESTION_MESSAGE);
                if (reason != null && !reason.trim().isEmpty()) {
                    currentDetail.cancel(reason);
                    currentDetail.setUpdatedBy(currentUser.getUsername());
                    updateTableData();
                    updateDetailInfo();
                }
            }
        });

        // Thêm sự kiện cho nút Khôi phục
        restoreButton.addActionListener(e -> {
            if (currentDetail != null && currentDetail.isCancelled()) {
                currentDetail.restore();
                currentDetail.setUpdatedBy(currentUser.getUsername());
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

        // Cập nhật dữ liệu bảng
        updateTableData();

        return panel;
    }

    /**
     * Tạo panel thông tin chi tiết
     */
    private JPanel createDetailInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết"));

        // Tạo panel thông tin chi tiết
        infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Khởi tạo các label
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

        // Khởi tạo TextArea mô tả
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);

        // Thông tin cơ bản
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

        // Thông tin giá
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

        // Thông tin chỉ định
        gbc.gridx = 0; gbc.gridy = 5;
        infoPanel.add(new JLabel("Bác sĩ chỉ định:"), gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        infoPanel.add(prescribedByLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 5;
        infoPanel.add(new JLabel("Ngày chỉ định:"), gbc);

        gbc.gridx = 3; gbc.gridy = 5;
        infoPanel.add(prescribedDateLabel, gbc);

        // Thông tin thực hiện
        gbc.gridx = 0; gbc.gridy = 6;
        infoPanel.add(new JLabel("Người thực hiện:"), gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        infoPanel.add(performedByLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 6;
        infoPanel.add(new JLabel("Ngày thực hiện:"), gbc);

        gbc.gridx = 3; gbc.gridy = 6;
        infoPanel.add(performedDateLabel, gbc);

        // Thông tin hệ thống
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

        // Mô tả
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 1;
        infoPanel.add(new JLabel("Mô tả:"), gbc);

        gbc.gridx = 1; gbc.gridy = 9;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(descScrollPane, gbc);

        // Tạo panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton editButton = new JButton("Sửa thông tin");
        JButton recordPerformedButton = new JButton("Ghi nhận thực hiện");

        // Xử lý sự kiện ghi nhận thực hiện
        recordPerformedButton.addActionListener(e -> {
            if (currentDetail != null && !currentDetail.isCancelled() && currentDetail.getPerformedBy() == null) {
                currentDetail.setPerformer(currentUser.getUsername(), LocalDateTime.now());
                currentDetail.setUpdatedBy(currentUser.getUsername());
                updateDetailInfo();
            }
        });

        buttonPanel.add(recordPerformedButton);
        buttonPanel.add(editButton);

        // Thêm các panel vào panel chính
        panel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Cập nhật thông tin
        updateDetailInfo();

        return panel;
    }

    /**
     * Cập nhật dữ liệu bảng
     */
    private void updateTableData() {
        // Xóa dữ liệu hiện tại
        detailsTableModel.setRowCount(0);

        // Thêm dữ liệu từ danh sách
        for (InvoiceDetail detail : invoiceDetailList) {
            Object[] rowData = new Object[8];
            rowData[0] = detail.getServiceCode();
            rowData[1] = detail.getServiceName();
            rowData[2] = detail.getQuantity();
            rowData[3] = String.format("%,.0f", detail.getUnitPrice());
            rowData[4] = String.format("%,.0f", detail.getTotalPrice());

            if (detail.getDiscountAmount() > 0) {
                rowData[5] = String.format("%,.0f (%s%%)", detail.getDiscountAmount(), detail.getDiscountPercent());
            } else {
                rowData[5] = "0";
            }

            rowData[6] = String.format("%,.0f", detail.getFinalPrice());
            rowData[7] = detail.isCancelled() ? "Đã hủy" : "Hoạt động";

            detailsTableModel.addRow(rowData);
        }

        // Chọn dòng đầu tiên nếu có
        if (detailsTable.getRowCount() > 0) {
            detailsTable.setRowSelectionInterval(0, 0);
        }
    }

    /**
     * Cập nhật thông tin chi tiết
     */
    private void updateDetailInfo() {
        if (currentDetail == null) {
            // Xóa các thông tin
            serviceNameLabel.setText("");
            serviceCodeLabel.setText("");
            quantityLabel.setText("");
            unitPriceLabel.setText("");
            totalPriceLabel.setText("");
            discountLabel.setText("");
            finalPriceLabel.setText("");
            categoryLabel.setText("");
            unitLabel.setText("");
            statusLabel.setText("");
            prescribedByLabel.setText("");
            prescribedDateLabel.setText("");
            performedByLabel.setText("");
            performedDateLabel.setText("");
            createdInfoLabel.setText("");
            updatedInfoLabel.setText("");
            descriptionArea.setText("");
            return;
        }

        // Cập nhật các thông tin
        serviceNameLabel.setText(currentDetail.getServiceName());
        serviceCodeLabel.setText(currentDetail.getServiceCode());
        quantityLabel.setText(String.valueOf(currentDetail.getQuantity()));
        unitPriceLabel.setText(String.format("%,.0f VND", currentDetail.getUnitPrice()));
        totalPriceLabel.setText(String.format("%,.0f VND", currentDetail.getTotalPrice()));

        if (currentDetail.getDiscountAmount() > 0) {
            discountLabel.setText(String.format("%,.0f VND (%s%%)",
                    currentDetail.getDiscountAmount(), currentDetail.getDiscountPercent()));
        } else {
            discountLabel.setText("0 VND");
        }

        finalPriceLabel.setText(String.format("%,.0f VND", currentDetail.getFinalPrice()));
        categoryLabel.setText(currentDetail.getCategory());
        unitLabel.setText(currentDetail.getUnit());

        if (currentDetail.isCancelled()) {
            statusLabel.setText("Đã hủy - " + currentDetail.getCancelReason());
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("Hoạt động");
            statusLabel.setForeground(new Color(0, 128, 0));
        }

        prescribedByLabel.setText(currentDetail.getPrescribedBy() != null ? currentDetail.getPrescribedBy() : "");
        prescribedDateLabel.setText(currentDetail.getPrescribedDate() != null ?
                currentDetail.getPrescribedDate().format(DATE_TIME_FORMATTER) : "");

        performedByLabel.setText(currentDetail.getPerformedBy() != null ? currentDetail.getPerformedBy() : "Chưa thực hiện");
        performedDateLabel.setText(currentDetail.getPerformedDate() != null ?
                currentDetail.getPerformedDate().format(DATE_TIME_FORMATTER) : "");

        createdInfoLabel.setText(String.format("Tạo lúc %s bởi %s",
                currentDetail.getCreatedAt().format(DATE_TIME_FORMATTER),
                currentDetail.getCreatedBy() != null ? currentDetail.getCreatedBy() : ""));

        if (currentDetail.getUpdatedAt() != null) {
            updatedInfoLabel.setText(String.format("Cập nhật lúc %s bởi %s",
                    currentDetail.getUpdatedAt().format(DATE_TIME_FORMATTER),
                    currentDetail.getUpdatedBy() != null ? currentDetail.getUpdatedBy() : ""));
        } else {
            updatedInfoLabel.setText("Chưa cập nhật");
        }

        descriptionArea.setText(currentDetail.getDescription() != null ? currentDetail.getDescription() : "");
    }

    /**
     * Thêm chi tiết mới vào danh sách
     * @param detail Chi tiết hóa đơn mới
     */
    public void addInvoiceDetail(InvoiceDetail detail) {
        invoiceDetailList.add(detail);
        updateTableData();
    }

    /**
     * Tính tổng tiền của tất cả chi tiết
     * @param includeCancel Có tính cả các mục đã hủy hay không
     * @return Tổng tiền cuối cùng
     */
    public double calculateTotalAmount(boolean includeCancel) {
        double total = 0;
        for (InvoiceDetail detail : invoiceDetailList) {
            if (!detail.isCancelled() || includeCancel) {
                total += detail.getFinalPrice();
            }
        }
        return total;
    }

    /**
     * Lấy danh sách chi tiết hóa đơn
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> getInvoiceDetailList() {
        return invoiceDetailList;
    }

    /**
     * Lấy số lượng chi tiết hóa đơn
     * @param activeOnly Chỉ đếm các mục đang hoạt động
     * @return Số lượng chi tiết
     */
    public int getDetailCount(boolean activeOnly) {
        if (!activeOnly) {
            return invoiceDetailList.size();
        }

        int count = 0;
        for (InvoiceDetail detail : invoiceDetailList) {
            if (!detail.isCancelled()) {
                count++;
            }
        }
        return count;
    }
}
