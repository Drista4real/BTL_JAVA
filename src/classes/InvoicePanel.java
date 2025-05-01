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
import model.entity.Invoice;
import model.entity.Invoice.InvoiceItem;
import model.entity.Invoice.InvoiceStatus;
import model.entity.Invoice.InvoiceType;
import model.entity.Invoice.PaymentRecord;
import model.entity.User;

/**
 * Panel quản lý hóa đơn trong hệ thống y tế
 */
public class InvoicePanel extends JPanel {

    // Người dùng hiện tại
    private User currentUser;

    // Danh sách hóa đơn và hóa đơn hiện tại
    private List<Invoice> invoiceList;
    private Invoice currentInvoice;

    // Format thời gian
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // UI components
    private JList<Invoice> invoiceJList;
    private DefaultListModel<Invoice> invoiceListModel;
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;
    private JTable paymentsTable;
    private DefaultTableModel paymentsTableModel;

    // Form fields
    private JTextField invoiceNumberField;
    private JTextField patientIdField;
    private JTextField patientNameField;
    private JTextField createdDateField;
    private JTextField dueDateField;
    private JTextField createdByField;
    private JTextField approvedByField;
    private JComboBox<String> invoiceTypeComboBox;
    private JComboBox<String> statusComboBox;
    private JTextField totalAmountField;
    private JTextField paidAmountField;
    private JTextField remainingAmountField;
    private JTextArea notesArea;

    /**
     * Constructor
     * @param user Người dùng hiện tại
     */
    public InvoicePanel(User user) {
        this.currentUser = user;
        this.invoiceList = new ArrayList<>();

        // Thêm dữ liệu mẫu
        createSampleData();

        initializeUI();
    }

    /**
     * Constructor với danh sách hóa đơn
     * @param user Người dùng hiện tại
     * @param invoiceList Danh sách hóa đơn
     */
    public InvoicePanel(User user, List<Invoice> invoiceList) {
        this.currentUser = user;
        this.invoiceList = new ArrayList<>(invoiceList);

        if (!invoiceList.isEmpty()) {
            this.currentInvoice = invoiceList.get(0);
        }

        initializeUI();
    }

    /**
     * Tạo dữ liệu mẫu cho panel
     */
    private void createSampleData() {
        // Tạo hóa đơn mẫu 1 - Hóa đơn chưa thanh toán
        Invoice invoice1 = new Invoice(
                "INV20240001",
                "PT001",
                "Nguyễn Văn A",
                InvoiceType.MEDICATION,
                LocalDateTime.now().plusDays(7),
                "Staff001"
        );

        invoice1.addItem(new InvoiceItem("Paracetamol", "MED001", "Thuốc hạ sốt", 2, 15000, "Thuốc"));
        invoice1.addItem(new InvoiceItem("Vitamin C", "MED002", "Bổ sung vitamin", 1, 25000, "Thuốc"));
        invoice1.setNotes("Thuốc cho bệnh nhân điều trị ngoại trú");

        // Tạo hóa đơn mẫu 2 - Hóa đơn đã thanh toán một phần
        Invoice invoice2 = new Invoice(
                "INV20240002",
                "PT002",
                "Trần Thị B",
                InvoiceType.PROCEDURE,
                LocalDateTime.now().plusDays(14),
                "Staff001"
        );

        invoice2.addItem(new InvoiceItem("Xét nghiệm máu", "PROC001", "Xét nghiệm chỉ số máu cơ bản", 1, 250000, "Xét nghiệm"));
        invoice2.addItem(new InvoiceItem("Siêu âm ổ bụng", "PROC002", "Siêu âm kiểm tra tổng quát", 1, 350000, "Siêu âm"));
        invoice2.recordPayment(300000, "Tiền mặt", "PT002", "Staff001");
        invoice2.setNotes("Yêu cầu thanh toán phần còn lại trong vòng 14 ngày");

        // Tạo hóa đơn mẫu 3 - Hóa đơn đã thanh toán
        Invoice invoice3 = new Invoice(
                "INV20240003",
                "PT003",
                "Lê Văn C",
                InvoiceType.ROOM_CHARGE,
                LocalDateTime.now().minusDays(7),
                "Staff002"
        );

        invoice3.addItem(new InvoiceItem("Phòng đơn", "ROOM001", "Phòng đơn tiêu chuẩn (3 ngày)", 3, 500000, "Phòng"));
        invoice3.addItem(new InvoiceItem("Suất ăn", "MEAL001", "Suất ăn tiêu chuẩn (3 ngày x 3 bữa)", 9, 50000, "Ăn uống"));
        invoice3.recordPayment(1950000, "Chuyển khoản", "PT003", "Staff002");
        invoice3.approveInvoice("Admin001");

        // Thêm vào danh sách
        invoiceList.add(invoice1);
        invoiceList.add(invoice2);
        invoiceList.add(invoice3);

        if (!invoiceList.isEmpty()) {
            currentInvoice = invoiceList.get(0);
        }
    }

    /**
     * Khởi tạo giao diện người dùng
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo Split Pane chính - chia danh sách và chi tiết
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(300);

        // Panel bên trái - danh sách hóa đơn
        JPanel leftPanel = createInvoiceListPanel();

        // Panel bên phải - chi tiết hóa đơn
        JPanel rightPanel = createInvoiceDetailPanel();

        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);

        add(mainSplitPane, BorderLayout.CENTER);
    }

    /**
     * Tạo panel danh sách hóa đơn
     */
    private JPanel createInvoiceListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));

        // Tạo model và list
        invoiceListModel = new DefaultListModel<>();
        for (Invoice invoice : invoiceList) {
            invoiceListModel.addElement(invoice);
        }

        invoiceJList = new JList<>(invoiceListModel);
        invoiceJList.setCellRenderer(new InvoiceListCellRenderer());
        invoiceJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Xử lý sự kiện khi chọn hóa đơn
        invoiceJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && invoiceJList.getSelectedValue() != null) {
                    currentInvoice = invoiceJList.getSelectedValue();
                    updateInvoiceDetails();
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(invoiceJList);

        // Tạo panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Tìm kiếm");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText().trim();
                if (keyword.isEmpty()) {
                    // Hiển thị tất cả
                    invoiceListModel.clear();
                    for (Invoice invoice : invoiceList) {
                        invoiceListModel.addElement(invoice);
                    }
                } else {
                    // Tìm kiếm theo từ khóa
                    invoiceListModel.clear();

                    keyword = keyword.toLowerCase();
                    for (Invoice invoice : invoiceList) {
                        if (invoice.getInvoiceNumber().toLowerCase().contains(keyword) ||
                                invoice.getPatientId().toLowerCase().contains(keyword) ||
                                invoice.getPatientName().toLowerCase().contains(keyword)) {

                            invoiceListModel.addElement(invoice);
                        }
                    }
                }
            }
        });

        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Tạo panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton newButton = new JButton("Tạo mới");
        JButton deleteButton = new JButton("Xóa");

        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);

        // Kết hợp các panel con
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(listScrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo panel chi tiết hóa đơn
     */
    private JPanel createInvoiceDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Tạo tab pane cho chi tiết hóa đơn
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab thông tin cơ bản
        JPanel basicInfoPanel = createBasicInfoPanel();
        tabbedPane.addTab("Thông tin cơ bản", basicInfoPanel);

        // Tab danh sách mục
        JPanel itemsPanel = createItemsPanel();
        tabbedPane.addTab("Danh sách mục", itemsPanel);

        // Tab lịch sử thanh toán
        JPanel paymentsPanel = createPaymentsPanel();
        tabbedPane.addTab("Lịch sử thanh toán", paymentsPanel);

        panel.add(tabbedPane, BorderLayout.CENTER);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Lưu thông tin");
        JButton printButton = new JButton("In hóa đơn");
        JButton payButton = new JButton("Thanh toán");

        buttonPanel.add(saveButton);
        buttonPanel.add(payButton);
        buttonPanel.add(printButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo panel thông tin cơ bản của hóa đơn
     */
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Panel thông tin
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tạo các trường nhập liệu
        invoiceNumberField = new JTextField(15);
        patientIdField = new JTextField(15);
        patientNameField = new JTextField(15);
        createdDateField = new JTextField(15);
        dueDateField = new JTextField(15);
        createdByField = new JTextField(15);
        approvedByField = new JTextField(15);

        // Tạo combobox
        String[] invoiceTypes = new String[Invoice.InvoiceType.values().length];
        for (int i = 0; i < Invoice.InvoiceType.values().length; i++) {
            invoiceTypes[i] = Invoice.InvoiceType.values()[i].getDisplayName();
        }
        invoiceTypeComboBox = new JComboBox<>(invoiceTypes);

        String[] statusTypes = new String[Invoice.InvoiceStatus.values().length];
        for (int i = 0; i < Invoice.InvoiceStatus.values().length; i++) {
            statusTypes[i] = Invoice.InvoiceStatus.values()[i].getDisplayName();
        }
        statusComboBox = new JComboBox<>(statusTypes);

        // Tạo các trường số tiền
        totalAmountField = new JTextField(15);
        paidAmountField = new JTextField(15);
        remainingAmountField = new JTextField(15);

        // Tạo trường ghi chú
        notesArea = new JTextArea(5, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);

        // Set editability
        invoiceNumberField.setEditable(false);
        createdDateField.setEditable(false);
        createdByField.setEditable(false);
        approvedByField.setEditable(false);
        totalAmountField.setEditable(false);
        paidAmountField.setEditable(false);
        remainingAmountField.setEditable(false);
        statusComboBox.setEnabled(false);

        // Thêm các thành phần vào panel
        // Cột 1
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Số hóa đơn:"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Mã bệnh nhân:"), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Tên bệnh nhân:"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Ngày tạo:"), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        infoPanel.add(new JLabel("Hạn thanh toán:"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        infoPanel.add(new JLabel("Loại hóa đơn:"), gbc);

        // Cột 2
        gbc.gridx = 1; gbc.gridy = 0;
        infoPanel.add(invoiceNumberField, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        infoPanel.add(patientIdField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        infoPanel.add(patientNameField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        infoPanel.add(createdDateField, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        infoPanel.add(dueDateField, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        infoPanel.add(invoiceTypeComboBox, gbc);

        // Cột 3
        gbc.gridx = 2; gbc.gridy = 0;
        infoPanel.add(new JLabel("Trạng thái:"), gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        infoPanel.add(new JLabel("Tổng tiền:"), gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        infoPanel.add(new JLabel("Đã thanh toán:"), gbc);

        gbc.gridx = 2; gbc.gridy = 3;
        infoPanel.add(new JLabel("Còn lại:"), gbc);

        gbc.gridx = 2; gbc.gridy = 4;
        infoPanel.add(new JLabel("Người tạo:"), gbc);

        gbc.gridx = 2; gbc.gridy = 5;
        infoPanel.add(new JLabel("Người duyệt:"), gbc);

        // Cột 4
        gbc.gridx = 3; gbc.gridy = 0;
        infoPanel.add(statusComboBox, gbc);

        gbc.gridx = 3; gbc.gridy = 1;
        infoPanel.add(totalAmountField, gbc);

        gbc.gridx = 3; gbc.gridy = 2;
        infoPanel.add(paidAmountField, gbc);

        gbc.gridx = 3; gbc.gridy = 3;
        infoPanel.add(remainingAmountField, gbc);

        gbc.gridx = 3; gbc.gridy = 4;
        infoPanel.add(createdByField, gbc);

        gbc.gridx = 3; gbc.gridy = 5;
        infoPanel.add(approvedByField, gbc);

        // Ghi chú
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 1;
        infoPanel.add(new JLabel("Ghi chú:"), gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        gbc.gridwidth = 3;
        infoPanel.add(notesScrollPane, gbc);

        panel.add(infoPanel, BorderLayout.NORTH);

        // Cập nhật thông tin hiển thị
        updateInvoiceDetails();

        return panel;
    }

    /**
     * Tạo panel danh sách mục trong hóa đơn
     */
    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Tạo bảng danh sách mục
        String[] columnNames = {"Mã", "Tên mục", "Mô tả", "Số lượng", "Đơn giá", "Thành tiền", "Loại"};
        itemsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemsTable = new JTable(itemsTableModel);
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(6).setPreferredWidth(80);

        JScrollPane tableScrollPane = new JScrollPane(itemsTable);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemButton = new JButton("Thêm mục");
        JButton editItemButton = new JButton("Sửa");
        JButton deleteItemButton = new JButton("Xóa");

        buttonPanel.add(addItemButton);
        buttonPanel.add(editItemButton);
        buttonPanel.add(deleteItemButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Cập nhật danh sách mục
        updateItemsTable();

        return panel;
    }

    /**
     * Tạo panel lịch sử thanh toán
     */
    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Tạo bảng lịch sử thanh toán
        String[] columnNames = {"Ngày thanh toán", "Số tiền", "Phương thức", "Người thanh toán", "Người nhận", "Ghi chú"};
        paymentsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentsTable = new JTable(paymentsTableModel);
        paymentsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        paymentsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        paymentsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        paymentsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        paymentsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        paymentsTable.getColumnModel().getColumn(5).setPreferredWidth(200);

        JScrollPane tableScrollPane = new JScrollPane(paymentsTable);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addPaymentButton = new JButton("Thêm thanh toán mới");

        buttonPanel.add(addPaymentButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Cập nhật bảng lịch sử thanh toán
        updatePaymentsTable();

        return panel;
    }

    /**
     * Cập nhật hiển thị thông tin hóa đơn
     */
    private void updateInvoiceDetails() {
        if (currentInvoice == null) {
            // Xóa trắng các trường
            invoiceNumberField.setText("");
            patientIdField.setText("");
            patientNameField.setText("");
            createdDateField.setText("");
            dueDateField.setText("");
            invoiceTypeComboBox.setSelectedIndex(0);
            statusComboBox.setSelectedIndex(0);
            totalAmountField.setText("");
            paidAmountField.setText("");
            remainingAmountField.setText("");
            createdByField.setText("");
            approvedByField.setText("");
            notesArea.setText("");

            // Xóa bảng
            itemsTableModel.setRowCount(0);
            paymentsTableModel.setRowCount(0);

            return;
        }

        // Cập nhật thông tin cơ bản
        invoiceNumberField.setText(currentInvoice.getInvoiceNumber());
        patientIdField.setText(currentInvoice.getPatientId());
        patientNameField.setText(currentInvoice.getPatientName());
        createdDateField.setText(currentInvoice.getCreatedDate().format(DATE_TIME_FORMATTER));
        dueDateField.setText(currentInvoice.getDueDate().format(DATE_TIME_FORMATTER));

        // Cập nhật combobox
        for (int i = 0; i < Invoice.InvoiceType.values().length; i++) {
            if (Invoice.InvoiceType.values()[i] == currentInvoice.getInvoiceType()) {
                invoiceTypeComboBox.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < Invoice.InvoiceStatus.values().length; i++) {
            if (Invoice.InvoiceStatus.values()[i] == currentInvoice.getStatus()) {
                statusComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Cập nhật thông tin tài chính
        totalAmountField.setText(String.format("%,.0f", currentInvoice.getTotalAmount()));
        paidAmountField.setText(String.format("%,.0f", currentInvoice.getPaidAmount()));
        remainingAmountField.setText(String.format("%,.0f", currentInvoice.getRemainingAmount()));

        // Cập nhật thông tin người tạo/duyệt
        createdByField.setText(currentInvoice.getCreatedBy());
        approvedByField.setText(currentInvoice.getApprovedBy() != null ? currentInvoice.getApprovedBy() : "");

        // Cập nhật ghi chú
        notesArea.setText(currentInvoice.getNotes());

        // Cập nhật bảng
        updateItemsTable();
        updatePaymentsTable();
    }

    /**
     * Cập nhật bảng danh sách mục
     */
    private void updateItemsTable() {
        // Xóa dữ liệu cũ
        itemsTableModel.setRowCount(0);

        if (currentInvoice == null) return;

        // Thêm dữ liệu mới
        for (InvoiceItem item : currentInvoice.getItems()) {
            Object[] rowData = new Object[7];
            rowData[0] = item.getItemCode();
            rowData[1] = item.getItemName();
            rowData[2] = item.getDescription();
            rowData[3] = item.getQuantity();
            rowData[4] = String.format("%,.0f", item.getUnitPrice());
            rowData[5] = String.format("%,.0f", item.getTotalPrice());
            rowData[6] = item.getCategory();

            itemsTableModel.addRow(rowData);
        }
    }

    /**
     * Cập nhật bảng lịch sử thanh toán
     */
    private void updatePaymentsTable() {
        // Xóa dữ liệu cũ
        paymentsTableModel.setRowCount(0);

        if (currentInvoice == null) return;

        // Thêm dữ liệu mới
        for (PaymentRecord payment : currentInvoice.getPaymentHistory()) {
            Object[] rowData = new Object[6];
            rowData[0] = payment.getPaymentDate().format(DATE_TIME_FORMATTER);
            rowData[1] = String.format("%,.0f", payment.getAmount());
            rowData[2] = payment.getPaymentMethod();
            rowData[3] = payment.getPaidBy();
            rowData[4] = payment.getReceivedBy();
            rowData[5] = payment.getNotes();

            paymentsTableModel.addRow(rowData);
        }
    }

    /**
     * Lớp renderer tùy chỉnh cho danh sách hóa đơn
     */
    private class InvoiceListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Invoice) {
                Invoice invoice = (Invoice) value;
                String displayText = String.format("%s - %s - %s",
                        invoice.getInvoiceNumber(),
                        invoice.getPatientName(),
                        invoice.getStatus().getDisplayName());

                Component c = super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);

                // Đổi màu tùy theo trạng thái
                if (!isSelected) {
                    if (invoice.getStatus() == InvoiceStatus.PAID) {
                        c.setForeground(new Color(0, 128, 0)); // Màu xanh lá - đã thanh toán
                    } else if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
                        c.setForeground(Color.GRAY); // Màu xám - đã hủy
                    } else if (invoice.getStatus() == InvoiceStatus.PARTIALLY_PAID) {
                        c.setForeground(new Color(0, 0, 205)); // Màu xanh dương - thanh toán một phần
                    } else if (invoice.isOverdue()) {
                        c.setForeground(new Color(205, 0, 0)); // Màu đỏ - quá hạn
                    }
                }

                return c;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }    /**
     * Cắt chuỗi nếu quá dài
     * @param str Chuỗi cần cắt
     * @param maxLength Độ dài tối đa
     * @return Chuỗi đã cắt
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}

