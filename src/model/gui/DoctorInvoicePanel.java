package model.gui;

import model.entity.Invoice;
import model.entity.Invoice.InvoiceItem;
import model.entity.Invoice.InvoiceStatus;
import model.entity.Invoice.InvoiceType;
import model.entity.Invoice.PaymentRecord;
import model.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DoctorInvoicePanel extends JPanel {
    private User currentUser;
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private DoctorInvoiceDetailPanel invoiceDetailPanel;
    private DoctorPaymentPanel paymentPanel;
    private Invoice currentInvoice;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement?useUnicode=true&characterEncoding=UTF-8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Pha2k5@";

    public DoctorInvoicePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initializeUI();
        loadInvoicesFromDatabase();
    }

    private void initializeUI() {
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(400);

        JPanel leftPanel = createInvoiceListPanel();
        JPanel rightPanel = createInvoiceDetailPanel();

        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);

        add(mainSplitPane, BorderLayout.CENTER);
    }

    private JPanel createInvoiceListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));

        // Initialize table
        String[] columns = {"Mã hóa đơn", "Mã BN", "Tên bệnh nhân", "Ngày lập", "Loại", "Tổng tiền", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoiceTable = new JTable(tableModel);
        setupTable(invoiceTable);
        JScrollPane tableScrollPane = new JScrollPane(invoiceTable);

        // Table selection listener
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && invoiceTable.getSelectedRow() != -1) {
                int selectedRow = invoiceTable.getSelectedRow();
                String invoiceId = (String) tableModel.getValueAt(selectedRow, 0);
                currentInvoice = new Invoice(
                        invoiceId,
                        (String) tableModel.getValueAt(selectedRow, 1),
                        (String) tableModel.getValueAt(selectedRow, 2),
                        InvoiceType.valueOf((String) tableModel.getValueAt(selectedRow, 4)),
                        LocalDateTime.parse("01/01/2025 00:00", DATE_TIME_FORMATTER), // Placeholder due to missing DueDate
                        currentUser.getUserId()
                );
                loadInvoiceItems(currentInvoice);
                loadPaymentRecords(currentInvoice);
                invoiceDetailPanel.setInvoiceDetails(currentInvoice.getItems());
                paymentPanel.setInvoiceDetails(currentInvoice.getItems(), currentInvoice);
            }
        });

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            tableModel.setRowCount(0);
            loadInvoicesFromDatabase(keyword);
        });
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton newButton = new JButton("Tạo mới");
        JButton deleteButton = new JButton("Xóa");
        newButton.addActionListener(e -> createNewInvoice());
        deleteButton.addActionListener(e -> deleteInvoice());
        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupTable(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
    }

    private void loadInvoicesFromDatabase() {
        loadInvoicesFromDatabase("");
    }

    private void loadInvoicesFromDatabase(String keyword) {
        tableModel.setRowCount(0);
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT i.InvoiceNumber, i.PatientID, p.FullName AS PatientName, i.CreatedDate, i.InvoiceType, " +
                    "i.TotalAmount, i.Status " +
                    "FROM Invoices i " +
                    "JOIN Patients p ON i.PatientID = p.PatientID " +
                    "JOIN Appointments a ON i.PatientID = a.PatientID " +
                    "JOIN UserAccounts u ON a.DoctorID = u.UserID " +
                    "WHERE u.UserID = ? AND i.Status != 'DELETED' " +
                    (keyword.isEmpty() ? "" : "AND (LOWER(i.InvoiceNumber) LIKE ? OR LOWER(p.FullName) LIKE ? OR LOWER(i.PatientID) LIKE ?)") +
                    " ORDER BY i.CreatedDate DESC";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, currentUser.getUserId());
            if (!keyword.isEmpty()) {
                String likePattern = "%" + keyword + "%";
                pstmt.setString(2, likePattern);
                pstmt.setString(3, likePattern);
                pstmt.setString(4, likePattern);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("InvoiceNumber"),
                        rs.getString("PatientID"),
                        rs.getString("PatientName"),
                        rs.getString("CreatedDate"),
                        rs.getString("InvoiceType"),
                        String.format("%,.0f VNĐ", rs.getDouble("TotalAmount")),
                        rs.getString("Status")
                });
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadInvoiceItems(Invoice invoice) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT ServiceName, ServiceCode, Description, Quantity, UnitPrice, Category FROM InvoiceDetails WHERE InvoiceID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, invoice.getInvoiceId());
            ResultSet rs = stmt.executeQuery();

            invoice.getItems().clear();
            while (rs.next()) {
                InvoiceItem item = new InvoiceItem(
                        rs.getString("ServiceName"),
                        rs.getString("ServiceCode"),
                        rs.getString("Description"),
                        rs.getInt("Quantity"),
                        rs.getDouble("UnitPrice"),
                        rs.getString("Category")
                );
                invoice.addItem(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPaymentRecords(Invoice invoice) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM PaymentRecords WHERE InvoiceID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, invoice.getInvoiceId());
            ResultSet rs = stmt.executeQuery();

            invoice.getPaymentHistory().clear();
            while (rs.next()) {
                double amount = rs.getDouble("Amount");
                String paymentMethod = rs.getString("PaymentMethod");
                String paidBy = rs.getString("PaidBy");
                String receivedBy = rs.getString("ReceivedBy");
                invoice.recordPayment(amount, paymentMethod, paidBy, receivedBy);
                PaymentRecord lastPayment = invoice.getPaymentHistory().get(invoice.getPaymentHistory().size() - 1);
                lastPayment.setPaymentId(rs.getString("PaymentID"));
                lastPayment.setPaymentDate(rs.getTimestamp("PaymentDate").toLocalDateTime());
                lastPayment.setReferenceNumber(rs.getString("ReferenceNumber"));
                lastPayment.setNotes(rs.getString("Notes"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalStateException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải bản ghi thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createInvoiceDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JTabbedPane tabbedPane = new JTabbedPane();
        invoiceDetailPanel = new DoctorInvoiceDetailPanel(currentUser, currentInvoice);
        paymentPanel = new DoctorPaymentPanel(currentUser, currentInvoice, currentInvoice != null ? currentInvoice.getItems() : new ArrayList<>());
        tabbedPane.addTab("Chi tiết hóa đơn", invoiceDetailPanel);
        tabbedPane.addTab("Thanh toán", paymentPanel);
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private void createNewInvoice() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField invoiceNumberField = new JTextField();
        JTextField patientIdField = new JTextField();
        JTextField patientNameField = new JTextField();
        JComboBox<InvoiceType> typeCombo = new JComboBox<>(InvoiceType.values());
        JTextField dueDateField = new JTextField(DATE_TIME_FORMATTER.format(LocalDateTime.now().plusDays(7)));

        panel.add(new JLabel("Số hóa đơn:"));
        panel.add(invoiceNumberField);
        panel.add(new JLabel("Mã bệnh nhân:"));
        panel.add(patientIdField);
        panel.add(new JLabel("Tên bệnh nhân:"));
        panel.add(patientNameField);
        panel.add(new JLabel("Loại hóa đơn:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Hạn thanh toán (dd/MM/yyyy HH:mm):"));
        panel.add(dueDateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Tạo hóa đơn mới", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String invoiceNumber = invoiceNumberField.getText().trim();
                String patientId = patientIdField.getText().trim();
                String patientName = patientNameField.getText().trim();
                InvoiceType invoiceType = (InvoiceType) typeCombo.getSelectedItem();
                LocalDateTime dueDate = LocalDateTime.parse(dueDateField.getText().trim(), DATE_TIME_FORMATTER);

                if (invoiceNumber.isEmpty() || patientId.isEmpty() || patientName.isEmpty()) {
                    throw new IllegalArgumentException("Số hóa đơn, mã bệnh nhân và tên bệnh nhân không được để trống!");
                }

                if (isInvoiceNumberExists(invoiceNumber)) {
                    JOptionPane.showMessageDialog(this, "Số hóa đơn đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Invoice newInvoice = new Invoice(invoiceNumber, patientId, patientName, invoiceType, dueDate, currentUser.getUserId());
                saveInvoiceToDatabase(newInvoice);
                loadInvoicesFromDatabase();
                JOptionPane.showMessageDialog(this, "Tạo hóa đơn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean isInvoiceNumberExists(String invoiceNumber) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(*) FROM Invoices WHERE InvoiceNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, invoiceNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveInvoiceToDatabase(Invoice invoice) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO Invoices (InvoiceID, InvoiceNumber, PatientID, CreatedDate, DueDate, InvoiceType, Status, CreatedBy, TotalAmount, PaidAmount, RemainingAmount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, invoice.getInvoiceId());
            stmt.setString(2, invoice.getInvoiceNumber());
            stmt.setString(3, invoice.getPatientId());
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(invoice.getCreatedDate()));
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(invoice.getDueDate()));
            stmt.setString(6, invoice.getInvoiceType().name());
            stmt.setString(7, invoice.getStatus().name());
            stmt.setString(8, invoice.getCreatedBy());
            stmt.setDouble(9, invoice.getTotalAmount());
            stmt.setDouble(10, invoice.getPaidAmount());
            stmt.setDouble(11, invoice.getRemainingAmount());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String invoiceId = (String) tableModel.getValueAt(selectedRow, 0);
        if (hasRelatedDetailsOrPayments(invoiceId)) {
            JOptionPane.showMessageDialog(this, "Không thể xóa hóa đơn vì đã có chi tiết hoặc bản ghi thanh toán liên quan!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa hóa đơn " + invoiceId + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "DELETE FROM Invoices WHERE InvoiceNumber = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, invoiceId);
                stmt.executeUpdate();
                loadInvoicesFromDatabase();
                invoiceDetailPanel.setInvoiceDetails(new ArrayList<>());
                paymentPanel.setInvoiceDetails(new ArrayList<>(), null);
                currentInvoice = null;
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean hasRelatedDetailsOrPayments(String invoiceId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(*) FROM InvoiceDetails WHERE InvoiceID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
            query = "SELECT COUNT(*) FROM PaymentRecords WHERE InvoiceID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, invoiceId);
            rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}