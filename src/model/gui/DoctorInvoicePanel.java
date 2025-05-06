package model.gui;

import model.entity.Invoice;
import model.entity.Invoice.InvoiceItem;
import model.entity.Invoice.InvoiceStatus;
import model.entity.Invoice.InvoiceType;
import model.entity.Invoice.PaymentRecord;
import model.entity.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DoctorInvoicePanel extends JPanel {
    private User currentUser;
    private List<Invoice> invoiceList;
    private Invoice currentInvoice;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement?useUnicode=true&characterEncoding=UTF-8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Pha2k5@";

    private JList<Invoice> invoiceJList;
    private DefaultListModel<Invoice> invoiceListModel;
    private DoctorInvoiceDetailPanel invoiceDetailPanel;
    private DoctorPaymentPanel paymentPanel;
    private JTable invoiceTable;

    public DoctorInvoicePanel(User user) {
        this.currentUser = user;
        this.invoiceList = new ArrayList<>();
        loadInvoicesFromDatabase();
        initializeUI();
    }

    private void loadInvoicesFromDatabase() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // In ra các bảng trong cơ sở dữ liệu để debug
            java.sql.DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            System.out.println("Danh sách các bảng trong cơ sở dữ liệu:");
            while (tables.next()) {
                System.out.println(tables.getString("TABLE_NAME"));
            }

            // Kiểm tra xem bảng Invoices có tồn tại không, nếu không thì tạo bảng
            boolean tableExists = false;
            tables = metaData.getTables(null, null, "Invoices", null);
            if (tables.next()) {
                tableExists = true;
            }

            if (!tableExists) {
                // Tạo bảng Invoices nếu chưa tồn tại
                System.out.println("Bảng Invoices không tồn tại, đang tạo bảng...");
                java.sql.Statement stmt = conn.createStatement();
                String sql = "CREATE TABLE Invoices ("
                        + "InvoiceID VARCHAR(50) PRIMARY KEY, "
                        + "PatientID VARCHAR(50), "
                        + "DoctorID VARCHAR(50), "
                        + "InvoiceDate DATE, "
                        + "DueDate DATE, "
                        + "TotalAmount DECIMAL(10,2), "
                        + "Status ENUM('Chua thanh toan', 'Da thanh toan'), "
                        + "Notes TEXT, "
                        + "FOREIGN KEY (PatientID) REFERENCES Patients(PatientID), "
                        + "FOREIGN KEY (DoctorID) REFERENCES UserAccounts(UserID)"
                        + ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
                stmt.executeUpdate(sql);

                // Tạo bảng InvoiceItems để lưu chi tiết hóa đơn
                sql = "CREATE TABLE InvoiceItems ("
                        + "ItemID VARCHAR(50) PRIMARY KEY, "
                        + "InvoiceID VARCHAR(50), "
                        + "Description VARCHAR(255), "
                        + "Quantity INT, "
                        + "UnitPrice DECIMAL(10,2), "
                        + "TotalPrice DECIMAL(10,2), "
                        + "FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID)"
                        + ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
                stmt.executeUpdate(sql);
                System.out.println("Đã tạo bảng Invoices và InvoiceItems");

                // Thêm dữ liệu mẫu
                sql = "INSERT INTO Invoices (InvoiceID, PatientID, DoctorID, InvoiceDate, DueDate, TotalAmount, Status) VALUES "
                        + "('INV001', 'P001', 'U001', '2025-04-01', '2025-04-15', 1500000, 'Chua thanh toan'), "
                        + "('INV002', 'P002', 'U002', '2025-04-02', '2025-04-16', 2000000, 'Chua thanh toan'), "
                        + "('INV003', 'P003', 'U003', '2025-04-03', '2025-04-17', 3000000, 'Da thanh toan')";
                stmt.executeUpdate(sql);

                sql = "INSERT INTO InvoiceItems (ItemID, InvoiceID, Description, Quantity, UnitPrice, TotalPrice) VALUES "
                        + "('ITEM001', 'INV001', 'Khám tổng quát', 1, 500000, 500000), "
                        + "('ITEM002', 'INV001', 'Xét nghiệm máu', 1, 1000000, 1000000), "
                        + "('ITEM003', 'INV002', 'Chụp X-quang', 1, 800000, 800000), "
                        + "('ITEM004', 'INV002', 'Thuốc', 1, 1200000, 1200000), "
                        + "('ITEM005', 'INV003', 'Phẫu thuật', 1, 3000000, 3000000)";
                stmt.executeUpdate(sql);

                System.out.println("Đã thêm dữ liệu mẫu vào bảng Invoices");
            }

            // Truy vấn dữ liệu từ bảng Invoices
            String query = "SELECT i.*, p.FullName AS PatientName, u.FullName AS DoctorName " +
                    "FROM Invoices i " +
                    "LEFT JOIN Patients p ON i.PatientID = p.PatientID " +
                    "LEFT JOIN UserAccounts u ON i.DoctorID = u.UserID " +
                    "ORDER BY i.InvoiceDate DESC";

            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            // Xóa dữ liệu cũ trong bảng
            if (this.invoiceTable == null) {
                this.invoiceTable = new JTable();
                this.invoiceTable.setModel(new DefaultTableModel(
                    new Object[][] {},
                    new String[] {"Mã hóa đơn", "Mã BN", "Tên bệnh nhân", "Bác sĩ", "Ngày lập", "Ngày hạn", "Tổng tiền", "Trạng thái"}
                ));
            }
            DefaultTableModel tableModel = (DefaultTableModel) this.invoiceTable.getModel();
            tableModel.setRowCount(0);

            // Đọc dữ liệu và thêm vào bảng
            while (rs.next()) {
                String invoiceId = rs.getString("InvoiceID");
                String patientId = rs.getString("PatientID");
                String patientName = rs.getString("PatientName");
                String doctorName = rs.getString("DoctorName");
                String invoiceDate = rs.getDate("InvoiceDate").toString();
                String dueDate = rs.getDate("DueDate").toString();
                String totalAmount = String.format("%,.0f VNĐ", rs.getDouble("TotalAmount"));
                String status = rs.getString("Status");

                tableModel.addRow(new Object[]{invoiceId, patientId, patientName, doctorName, invoiceDate, dueDate, totalAmount, status});
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
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM InvoiceDetails WHERE InvoiceID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, invoice.getInvoiceId());
            rs = stmt.executeQuery();

            while (rs.next()) {
                InvoiceItem item = new InvoiceItem(
                        rs.getString("ServiceName"),
                        rs.getString("ServiceCode"),
                        rs.getString("Description"),
                        rs.getInt("Quantity"),
                        rs.getDouble("UnitPrice"),
                        rs.getString("Category")
                );
                item.setItemId(rs.getString("DetailID"));
                invoice.addItem(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadPaymentRecords(Invoice invoice) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM PaymentRecords WHERE InvoiceID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, invoice.getInvoiceId());
            rs = stmt.executeQuery();

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
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(300);

        JPanel leftPanel = createInvoiceListPanel();
        JPanel rightPanel = createInvoiceDetailPanel();

        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);

        add(mainSplitPane, BorderLayout.CENTER);
    }

    private JPanel createInvoiceListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));

        invoiceListModel = new DefaultListModel<>();
        for (Invoice invoice : invoiceList) {
            invoiceListModel.addElement(invoice);
        }

        invoiceJList = new JList<>(invoiceListModel);
        invoiceJList.setCellRenderer(new InvoiceListCellRenderer());
        invoiceJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        invoiceJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && invoiceJList.getSelectedValue() != null) {
                    currentInvoice = invoiceJList.getSelectedValue();
                    invoiceDetailPanel.setInvoiceDetails(currentInvoice.getItems());
                    paymentPanel.setInvoiceDetails(currentInvoice.getItems(), currentInvoice);
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(invoiceJList);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Tìm kiếm");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText().trim();
                invoiceListModel.clear();
                if (keyword.isEmpty()) {
                    for (Invoice invoice : invoiceList) {
                        invoiceListModel.addElement(invoice);
                    }
                } else {
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
        panel.add(listScrollPane, BorderLayout.CENTER);

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
                invoiceList.add(newInvoice);
                invoiceListModel.addElement(newInvoice);
                JOptionPane.showMessageDialog(this, "Tạo hóa đơn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean isInvoiceNumberExists(String invoiceNumber) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT COUNT(*) FROM Invoices WHERE InvoiceNumber = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, invoiceNumber);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void saveInvoiceToDatabase(Invoice invoice) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "INSERT INTO Invoices (InvoiceID, InvoiceNumber, PatientID, CreatedDate, DueDate, InvoiceType, Status, CreatedBy, TotalAmount, PaidAmount, RemainingAmount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
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
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteInvoice() {
        if (currentInvoice == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (hasRelatedDetailsOrPayments(currentInvoice.getInvoiceId())) {
            JOptionPane.showMessageDialog(this, "Không thể xóa hóa đơn vì đã có chi tiết hoặc bản ghi thanh toán liên quan!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa hóa đơn " + currentInvoice.getInvoiceNumber() + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String query = "DELETE FROM Invoices WHERE InvoiceID = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, currentInvoice.getInvoiceId());
                stmt.executeUpdate();

                invoiceList.remove(currentInvoice);
                invoiceListModel.removeElement(currentInvoice);
                currentInvoice = invoiceList.isEmpty() ? null : invoiceList.get(0);
                if (currentInvoice != null) {
                    invoiceDetailPanel.setInvoiceDetails(currentInvoice.getItems());
                    paymentPanel.setInvoiceDetails(currentInvoice.getItems(), currentInvoice);
                } else {
                    invoiceDetailPanel.setInvoiceDetails(new ArrayList<>());
                    paymentPanel.setInvoiceDetails(new ArrayList<>(), null);
                }
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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

    private boolean hasRelatedDetailsOrPayments(String invoiceId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT COUNT(*) FROM InvoiceDetails WHERE InvoiceID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, invoiceId);
            rs = stmt.executeQuery();
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
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private JPanel createInvoiceDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JTabbedPane tabbedPane = new JTabbedPane();

        invoiceDetailPanel = new DoctorInvoiceDetailPanel(currentUser, currentInvoice != null ? currentInvoice : null);
        paymentPanel = new DoctorPaymentPanel(currentUser, currentInvoice != null ? currentInvoice : null, currentInvoice != null ? currentInvoice.getItems() : new ArrayList<>());

        tabbedPane.addTab("Chi tiết hóa đơn", invoiceDetailPanel);
        tabbedPane.addTab("Thanh toán", paymentPanel);

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private class InvoiceListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Invoice) {
                Invoice invoice = (Invoice) value;
                String invoiceNumber = invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : "(Không có số)";
                String patientName = invoice.getPatientName() != null ? invoice.getPatientName() : "(Không có tên)";
                String statusText = invoice.getStatus() != null ? invoice.getStatus().getDisplayName() : "Không xác định";

                String displayText = String.format("%s - %s - %s", invoiceNumber, patientName, statusText);

                Component c = super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);

                if (!isSelected) {
                    if (invoice.getStatus() == InvoiceStatus.PAID) {
                        c.setForeground(new Color(0, 128, 0));
                    } else if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
                        c.setForeground(Color.GRAY);
                    } else if (invoice.getStatus() == InvoiceStatus.PARTIALLY_PAID) {
                        c.setForeground(new Color(0, 0, 205));
                    } else if (invoice.isOverdue()) {
                        c.setForeground(new Color(205, 0, 0));
                    }
                }

                return c;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}