package model.gui;

import model.entity.Invoice;
import model.entity.Invoice.InvoiceItem;
import model.entity.Invoice.PaymentRecord;
import model.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DoctorPaymentPanel extends JPanel {
    private User currentUser;
    private List<InvoiceItem> invoiceItems;
    private List<PaymentRecord> paymentRecords;
    private PaymentRecord currentPayment;
    private Invoice currentInvoice; // Lưu hóa đơn hiện tại
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement?useUnicode=true&characterEncoding=UTF-8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "050705";

    private JTable paymentTable;
    private DefaultTableModel paymentTableModel;
    private JLabel paymentIdLabel;
    private JLabel paymentDateLabel;
    private JLabel amountLabel;
    private JLabel paymentMethodLabel;
    private JLabel referenceNumberLabel;
    private JLabel paidByLabel;
    private JLabel receivedByLabel;
    private JTextArea notesArea;

    public DoctorPaymentPanel(User user, Invoice invoice, List<InvoiceItem> items) {
        this.currentUser = user;
        this.invoiceItems = new ArrayList<>(items);
        this.currentInvoice = invoice;
        this.paymentRecords = new ArrayList<>();
        this.currentPayment = null;

        loadPaymentRecordsFromDatabase();
        initializeUI();
    }

    public void setInvoiceDetails(List<InvoiceItem> items, Invoice invoice) {
        this.invoiceItems = new ArrayList<>(items);
        this.currentInvoice = invoice;
        loadPaymentRecordsFromDatabase();
        updateTableData();
        updatePaymentInfo();
    }

    private void loadPaymentRecordsFromDatabase() {
        paymentRecords.clear();
        if (currentInvoice == null || currentInvoice.getInvoiceId() == null) {
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM PaymentRecords WHERE InvoiceID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, currentInvoice.getInvoiceId());
            rs = stmt.executeQuery();

            while (rs.next()) {
                PaymentRecord payment = new PaymentRecord(
                        rs.getDouble("Amount"),
                        rs.getString("PaymentMethod"),
                        rs.getString("PaidBy"),
                        rs.getString("ReceivedBy")
                );
                payment.setPaymentId(rs.getString("PaymentID"));
                payment.setPaymentDate(rs.getTimestamp("PaymentDate").toLocalDateTime());
                payment.setReferenceNumber(rs.getString("ReferenceNumber"));
                payment.setNotes(rs.getString("Notes"));
                paymentRecords.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải lịch sử thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (!paymentRecords.isEmpty()) {
            currentPayment = paymentRecords.get(0);
        } else {
            currentPayment = null;
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Khởi tạo các JLabel và JTextArea trước
        paymentIdLabel = new JLabel();
        paymentDateLabel = new JLabel();
        amountLabel = new JLabel();
        paymentMethodLabel = new JLabel();
        referenceNumberLabel = new JLabel();
        paidByLabel = new JLabel();
        receivedByLabel = new JLabel();
        notesArea = new JTextArea(3, 30);
        notesArea.setEditable(false);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        // Sau đó tạo các panel
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerLocation(200);

        JPanel topPanel = createPaymentListPanel();
        JPanel bottomPanel = createPaymentInfoPanel();

        mainSplitPane.setTopComponent(topPanel);
        mainSplitPane.setBottomComponent(bottomPanel);

        add(mainSplitPane, BorderLayout.CENTER);

        // Cập nhật thông tin thanh toán sau khi giao diện đã được khởi tạo
        updatePaymentInfo();
    }

    private JPanel createPaymentListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Lịch sử thanh toán"));

        String[] columnNames = {"Mã thanh toán", "Ngày thanh toán", "Số tiền", "Phương thức", "Người thanh toán", "Người nhận"};
        paymentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentTable = new JTable(paymentTableModel);
        paymentTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        paymentTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        paymentTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        paymentTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        paymentTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        paymentTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        paymentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = paymentTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < paymentRecords.size()) {
                    currentPayment = paymentRecords.get(selectedRow);
                    updatePaymentInfo();
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(paymentTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Ghi nhận thanh toán");
        JButton refundButton = new JButton("Hoàn tiền");

        addButton.addActionListener(e -> recordPayment());
        refundButton.addActionListener(e -> refundPayment());

        buttonPanel.add(addButton);
        buttonPanel.add(refundButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        updateTableData();

        return panel;
    }

    private void recordPayment() {
        if (currentInvoice == null || currentInvoice.getInvoiceId() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn trước khi ghi nhận thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (currentInvoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            JOptionPane.showMessageDialog(this, "Hóa đơn đã được thanh toán đầy đủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (currentInvoice.getStatus() == Invoice.InvoiceStatus.CANCELLED) {
            JOptionPane.showMessageDialog(this, "Hóa đơn đã bị hủy, không thể ghi nhận thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField amountField = new JTextField();
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng"});
        JTextField referenceNumberField = new JTextField();
        JTextField paidByField = new JTextField();
        JTextField notesField = new JTextField();

        panel.add(new JLabel("Số tiền:"));
        panel.add(amountField);
        panel.add(new JLabel("Phương thức thanh toán:"));
        panel.add(methodCombo);
        panel.add(new JLabel("Mã giao dịch:"));
        panel.add(referenceNumberField);
        panel.add(new JLabel("Người thanh toán:"));
        panel.add(paidByField);
        panel.add(new JLabel("Ghi chú:"));
        panel.add(notesField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Ghi nhận thanh toán", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                String paymentMethod = (String) methodCombo.getSelectedItem();
                String referenceNumber = referenceNumberField.getText().trim();
                String paidBy = paidByField.getText().trim();
                String notes = notesField.getText().trim();

                if (amount <= 0) {
                    throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0!");
                }
                if (amount > currentInvoice.getRemainingAmount()) {
                    throw new IllegalArgumentException("Số tiền thanh toán vượt quá số tiền còn lại (" + String.format("%,.0f", currentInvoice.getRemainingAmount()) + " VND)!");
                }
                if (paidBy.isEmpty()) {
                    throw new IllegalArgumentException("Người thanh toán không được để trống!");
                }

                PaymentRecord newPayment = new PaymentRecord(amount, paymentMethod, paidBy, currentUser.getUserId());
                newPayment.setReferenceNumber(referenceNumber);
                newPayment.setNotes(notes);
                newPayment.setPaymentId("PAY" + System.currentTimeMillis());
                savePaymentToDatabase(newPayment);
                currentInvoice.recordPayment(amount, paymentMethod, paidBy, currentUser.getUserId());
                updateInvoiceInDatabase(currentInvoice);
                paymentRecords.add(newPayment);
                updateTableData();
                updatePaymentInfo();
                JOptionPane.showMessageDialog(this, "Ghi nhận thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi ghi nhận thanh toán: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refundPayment() {
        if (currentPayment == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giao dịch để hoàn tiền!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (currentInvoice == null || currentInvoice.getInvoiceId() == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn liên quan!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (currentPayment.getAmount() <= 0) {
            JOptionPane.showMessageDialog(this, "Giao dịch này đã là giao dịch hoàn tiền!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hoàn tiền cho giao dịch " + currentPayment.getPaymentId() + "?", "Xác nhận hoàn tiền", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String reason = JOptionPane.showInputDialog(this, "Nhập lý do hoàn tiền:", "Hoàn tiền", JOptionPane.QUESTION_MESSAGE);
            if (reason != null && !reason.trim().isEmpty()) {
                try {
                    PaymentRecord refund = new PaymentRecord(-currentPayment.getAmount(), currentPayment.getPaymentMethod(), currentPayment.getPaidBy(), currentUser.getUserId());
                    refund.setReferenceNumber("REFUND-" + currentPayment.getPaymentId());
                    refund.setNotes("Hoàn tiền: " + reason);
                    refund.setPaymentId("PAY" + System.currentTimeMillis());
                    savePaymentToDatabase(refund);
                    currentInvoice.refundInvoice(reason);
                    updateInvoiceInDatabase(currentInvoice);
                    paymentRecords.add(refund);
                    updateTableData();
                    updatePaymentInfo();
                    JOptionPane.showMessageDialog(this, "Hoàn tiền thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi hoàn tiền: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lý do hoàn tiền không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void savePaymentToDatabase(PaymentRecord payment) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "INSERT INTO PaymentRecords (PaymentID, InvoiceID, PaymentDate, Amount, PaymentMethod, ReferenceNumber, PaidBy, ReceivedBy, Notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, payment.getPaymentId());
            stmt.setString(2, currentInvoice.getInvoiceId());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setDouble(4, payment.getAmount());
            stmt.setString(5, payment.getPaymentMethod());
            stmt.setString(6, payment.getReferenceNumber());
            stmt.setString(7, payment.getPaidBy());
            stmt.setString(8, payment.getReceivedBy());
            stmt.setString(9, payment.getNotes());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu giao dịch: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateInvoiceInDatabase(Invoice invoice) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "UPDATE Invoices SET Status = ?, PaidAmount = ?, RemainingAmount = ?, PaidDate = ?, Notes = ?, PaymentMethod = ? WHERE InvoiceID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, invoice.getStatus().name());
            stmt.setDouble(2, invoice.getPaidAmount());
            stmt.setDouble(3, invoice.getRemainingAmount());
            stmt.setTimestamp(4, invoice.getPaidDate() != null ? java.sql.Timestamp.valueOf(invoice.getPaidDate()) : null);
            stmt.setString(5, invoice.getNotes());
            stmt.setString(6, invoice.getPaymentMethod());
            stmt.setString(7, invoice.getInvoiceId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTableData() {
        // Xóa toàn bộ dữ liệu cũ trong bảng thanh toán
        paymentTableModel.setRowCount(0);

        // Thêm dữ liệu từ danh sách thanh toán vào bảng
        for (PaymentRecord payment : paymentRecords) {
            Object[] rowData = new Object[6];
            rowData[0] = payment.getPaymentId() != null ? payment.getPaymentId() : "";
            rowData[1] = payment.getPaymentDate() != null ? payment.getPaymentDate().format(DATE_TIME_FORMATTER) : "";
            rowData[2] = String.format("%,.0f", payment.getAmount());
            rowData[3] = payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "";
            rowData[4] = payment.getPaidBy() != null ? payment.getPaidBy() : "";
            rowData[5] = payment.getReceivedBy() != null ? payment.getReceivedBy() : "";
            paymentTableModel.addRow(rowData);
        }

        // Chọn dòng đầu tiên nếu có dữ liệu
        if (paymentTable.getRowCount() > 0) {
            paymentTable.setRowSelectionInterval(0, 0);
        }
    }

    private void updatePaymentInfo() {
        // Kiểm tra nếu các JLabel chưa được khởi tạo
        if (paymentIdLabel == null || paymentDateLabel == null || amountLabel == null ||
                paymentMethodLabel == null || referenceNumberLabel == null || paidByLabel == null ||
                receivedByLabel == null || notesArea == null) {
            return; // Thoát phương thức nếu các thành phần chưa được khởi tạo
        }

        if (currentPayment == null) {
            paymentIdLabel.setText("");
            paymentDateLabel.setText("");
            amountLabel.setText("");
            paymentMethodLabel.setText("");
            referenceNumberLabel.setText("");
            paidByLabel.setText("");
            receivedByLabel.setText("");
            notesArea.setText("");
            return;
        }

        paymentIdLabel.setText(currentPayment.getPaymentId() != null ? currentPayment.getPaymentId() : "");
        paymentDateLabel.setText(currentPayment.getPaymentDate() != null ? currentPayment.getPaymentDate().format(DATE_TIME_FORMATTER) : "");
        amountLabel.setText(String.format("%,.0f VND", currentPayment.getAmount()));
        paymentMethodLabel.setText(currentPayment.getPaymentMethod() != null ? currentPayment.getPaymentMethod() : "");
        referenceNumberLabel.setText(currentPayment.getReferenceNumber() != null ? currentPayment.getReferenceNumber() : "");
        paidByLabel.setText(currentPayment.getPaidBy() != null ? currentPayment.getPaidBy() : "");
        receivedByLabel.setText(currentPayment.getReceivedBy() != null ? currentPayment.getReceivedBy() : "");
        notesArea.setText(currentPayment.getNotes() != null ? currentPayment.getNotes() : "");
    }

    private JPanel createPaymentInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin thanh toán"));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        paymentIdLabel = new JLabel();
        paymentDateLabel = new JLabel();
        amountLabel = new JLabel();
        paymentMethodLabel = new JLabel();
        referenceNumberLabel = new JLabel();
        paidByLabel = new JLabel();
        receivedByLabel = new JLabel();

        notesArea = new JTextArea(3, 30);
        notesArea.setEditable(false);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);

        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Mã thanh toán:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        infoPanel.add(paymentIdLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Ngày thanh toán:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        infoPanel.add(paymentDateLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Số tiền:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        infoPanel.add(amountLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Phương thức thanh toán:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        infoPanel.add(paymentMethodLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        infoPanel.add(new JLabel("Mã giao dịch:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        infoPanel.add(referenceNumberLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        infoPanel.add(new JLabel("Người thanh toán:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        infoPanel.add(paidByLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        infoPanel.add(new JLabel("Người nhận:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2;
        infoPanel.add(receivedByLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        infoPanel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(notesScrollPane, gbc);

        panel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);

        return panel;
    }
}