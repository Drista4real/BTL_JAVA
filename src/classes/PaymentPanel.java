package classes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import model.entity.User;
import model.entity.InvoiceDetail;

public class PaymentPanel extends JPanel {
    private User currentUser;
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private ArrayList<InvoiceDetail> invoiceDetails;

    public PaymentPanel(User user) {
        this.currentUser = user;
        this.invoiceDetails = new ArrayList<>();

        // Thiết lập giao diện
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Thanh toán"));
        setBackground(Color.WHITE);

        // Tạo bảng hiển thị chi tiết hóa đơn
        createInvoiceTable();

        // Panel chứa tổng tiền và nút thanh toán
        JPanel southPanel = createPaymentControlPanel();
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Tạo bảng hiển thị chi tiết hóa đơn
     */
    private void createInvoiceTable() {
        // Định nghĩa các cột cho bảng
        String[] columns = {"Mã hóa đơn", "Mã dịch vụ", "Tên dịch vụ", "Số lượng", "Đơn giá", "Thành tiền", "Giảm giá", "Thanh toán", "Trạng thái"};

        // Khởi tạo model bảng với các cột đã định nghĩa
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Không cho phép chỉnh sửa trực tiếp trên bảng
                return false;
            }
        };

        // Tạo JTable với model đã thiết lập
        invoiceTable = new JTable(tableModel);
        invoiceTable.setRowHeight(28);
        invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        invoiceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Đặt bảng vào JScrollPane để hỗ trợ cuộn
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        add(scrollPane, BorderLayout.CENTER);

        // Thêm listener để hiển thị thông tin chi tiết khi click vào một dòng
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && invoiceTable.getSelectedRow() >= 0 &&
                    invoiceTable.getSelectedRow() < invoiceDetails.size()) {
                showInvoiceDetailInfo(invoiceDetails.get(invoiceTable.getSelectedRow()));
            }
        });
    }

    /**
     * Cập nhật dữ liệu bảng từ danh sách chi tiết hóa đơn
     */
    private void updateTableData() {
        // Xóa tất cả dữ liệu hiện tại
        tableModel.setRowCount(0);

        // Thêm dữ liệu từ danh sách vào bảng
        for (InvoiceDetail detail : invoiceDetails) {
            String status = detail.isCancelled() ? "Đã hủy" : "Đang xử lý";

            Object[] rowData = {
                    detail.getInvoiceId(),
                    detail.getServiceCode(),
                    detail.getServiceName(),
                    detail.getQuantity() + " " + detail.getUnit(),
                    String.format("%,.0f", detail.getUnitPrice()),
                    String.format("%,.0f", detail.getTotalPrice()),
                    String.format("%.1f%%", detail.getDiscountPercent()),
                    String.format("%,.0f", detail.getFinalPrice()),
                    status
            };

            tableModel.addRow(rowData);
        }
    }

    /**
     * Hiển thị thông tin chi tiết trong dialog
     */
    private void showInvoiceDetailInfo(InvoiceDetail detail) {
        String detailedInfo = detail.getDetailedInfo();

        // Thêm thông tin người kê đơn nếu có
        if (detail.getPrescribedBy() != null && !detail.getPrescribedBy().isEmpty()) {
            String prescribedDate = "";
            if (detail.getPrescribedDate() != null) {
                prescribedDate = " - " + detail.getPrescribedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            detailedInfo += "\nBác sĩ kê đơn: " + detail.getPrescribedBy() + prescribedDate;
        }

        JOptionPane.showMessageDialog(
                this,
                detailedInfo,
                "Chi tiết dịch vụ: " + detail.getServiceName(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Tạo panel điều khiển thanh toán ở phía dưới
     */
    private JPanel createPaymentControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);

        // Tạo panel hiển thị tổng tiền
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setBackground(Color.WHITE);

        double totalAmount = calculateTotalAmount();
        JLabel totalLabel = new JLabel("Tổng tiền thanh toán: " + String.format("%,.0f VNĐ", totalAmount));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(new Color(65, 105, 225));
        totalPanel.add(totalLabel);

        // Tạo panel chứa các nút điều khiển
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        // Nút thêm dịch vụ
        JButton addServiceBtn = new JButton("Thêm dịch vụ");
        addServiceBtn.setBackground(new Color(70, 130, 180));
        addServiceBtn.setForeground(Color.WHITE);
        addServiceBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addServiceBtn.addActionListener(e -> addNewService());

        // Nút thanh toán
        JButton payBtn = new JButton("Thanh toán hóa đơn");
        payBtn.setBackground(new Color(41, 128, 185));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payBtn.addActionListener(e -> processPayment());

        // Nút in hóa đơn
        JButton printBtn = new JButton("In hóa đơn");
        printBtn.setBackground(new Color(46, 139, 87));
        printBtn.setForeground(Color.WHITE);
        printBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        printBtn.addActionListener(e -> printInvoice());

        // Thêm các nút vào panel
        buttonPanel.add(addServiceBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(payBtn);

        // Thêm các panel vào panel chính
        panel.add(totalPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Tính tổng số tiền cần thanh toán
     */
    private double calculateTotalAmount() {
        double total = 0;
        for (InvoiceDetail detail : invoiceDetails) {
            if (!detail.isCancelled()) {
                total += detail.getFinalPrice();
            }
        }
        return total;
    }

    /**
     * Xử lý thêm dịch vụ mới
     */
    private void addNewService() {
        // Tạo panel nhập thông tin
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField serviceNameField = new JTextField(20);
        JTextField serviceCodeField = new JTextField(10);
        JTextField quantityField = new JTextField("1");
        JTextField unitPriceField = new JTextField();
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{
                "Khám bệnh", "Xét nghiệm", "Thuốc", "Thủ thuật", "Chẩn đoán hình ảnh", "Khác"
        });

        panel.add(new JLabel("Tên dịch vụ:"));
        panel.add(serviceNameField);
        panel.add(new JLabel("Mã dịch vụ:"));
        panel.add(serviceCodeField);
        panel.add(new JLabel("Số lượng:"));
        panel.add(quantityField);
        panel.add(new JLabel("Đơn giá:"));
        panel.add(unitPriceField);
        panel.add(new JLabel("Loại dịch vụ:"));
        panel.add(categoryCombo);

        // Hiển thị dialog
        int result = JOptionPane.showConfirmDialog(
                this, panel, "Thêm dịch vụ mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        // Xử lý khi nhấn OK
        if (result == JOptionPane.OK_OPTION) {
            try {
                String serviceName = serviceNameField.getText().trim();
                String serviceCode = serviceCodeField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
                String category = (String) categoryCombo.getSelectedItem();

                if (serviceName.isEmpty() || serviceCode.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Vui lòng nhập đầy đủ thông tin!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tạo chi tiết hóa đơn mới
                InvoiceDetail newDetail = new InvoiceDetail(
                        "HD" + String.format("%03d", invoiceDetails.size() + 1), // Mã hóa đơn tự sinh
                        "SV" + String.format("%03d", invoiceDetails.size() + 1), // Mã dịch vụ tự sinh
                        serviceName,
                        serviceCode,
                        quantity,
                        unitPrice,
                        category
                );

                // Thêm vào danh sách và cập nhật bảng
                invoiceDetails.add(newDetail);
                updateTableData();

                // Cập nhật tổng tiền
                double totalAmount = calculateTotalAmount();
                ((JLabel)((JPanel)((BorderLayout)this.getLayout()).getLayoutComponent(BorderLayout.SOUTH))
                        .getComponent(0)).setText("Tổng tiền thanh toán: " + String.format("%,.0f VNĐ", totalAmount));

                JOptionPane.showMessageDialog(this,
                        "Đã thêm dịch vụ thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Giá trị không hợp lệ. Vui lòng kiểm tra lại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xử lý thanh toán
     */
    private void processPayment() {
        if (invoiceDetails.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không có dịch vụ nào để thanh toán!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Xác nhận thanh toán tổng số tiền " + String.format("%,.0f VNĐ", calculateTotalAmount()) + "?",
                "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Thanh toán thành công!\nĐã in hóa đơn.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            // Xử lý nghiệp vụ thanh toán ở đây - trong trường hợp thực tế
            // Có thể cập nhật trạng thái đã thanh toán trong database
        }
    }

    /**
     * Xử lý in hóa đơn
     */
    private void printInvoice() {
        if (invoiceDetails.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không có dịch vụ nào để in hóa đơn!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Tạo nội dung hóa đơn
        StringBuilder invoice = new StringBuilder();
        invoice.append("HÓA ĐƠN THANH TOÁN\n");
        invoice.append("----------------------------------\n");

        // Giả sử tất cả các chi tiết thuộc cùng một hóa đơn
        String invoiceId = invoiceDetails.get(0).getInvoiceId();
        invoice.append("Mã hóa đơn: ").append(invoiceId).append("\n");
        invoice.append("Ngày tạo: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        invoice.append("----------------------------------\n");
        invoice.append("CHI TIẾT DỊCH VỤ:\n\n");

        for (InvoiceDetail detail : invoiceDetails) {
            if (!detail.isCancelled()) {
                invoice.append(detail.getServiceName()).append(" (").append(detail.getServiceCode()).append(")\n");
                invoice.append("   Số lượng: ").append(detail.getQuantity()).append(" ").append(detail.getUnit()).append("\n");
                invoice.append("   Đơn giá: ").append(String.format("%,.0f VNĐ", detail.getUnitPrice())).append("\n");
                invoice.append("   Thành tiền: ").append(String.format("%,.0f VNĐ", detail.getTotalPrice())).append("\n");

                if (detail.getDiscountPercent() > 0) {
                    invoice.append("   Giảm giá: ").append(String.format("%.1f%%", detail.getDiscountPercent())).append(" - ")
                            .append(String.format("%,.0f VNĐ", detail.getDiscountAmount())).append("\n");
                }

                invoice.append("   Thanh toán: ").append(String.format("%,.0f VNĐ", detail.getFinalPrice())).append("\n\n");
            }
        }

        invoice.append("----------------------------------\n");
        invoice.append("TỔNG THANH TOÁN: ").append(String.format("%,.0f VNĐ", calculateTotalAmount())).append("\n");
        invoice.append("----------------------------------\n");
        invoice.append("Cảm ơn quý khách đã sử dụng dịch vụ!");

        // Hiển thị preview hóa đơn
        JTextArea textArea = new JTextArea(invoice.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 500));

        JOptionPane.showMessageDialog(this, scrollPane, "Xem trước hóa đơn", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Thêm hoặc cập nhật chi tiết hóa đơn
     * @param detail Chi tiết hóa đơn cần thêm hoặc cập nhật
     */
    public void addOrUpdateInvoiceDetail(InvoiceDetail detail) {
        // Kiểm tra xem đã tồn tại chi tiết với cùng ID chưa
        boolean found = false;
        for (int i = 0; i < invoiceDetails.size(); i++) {
            if (invoiceDetails.get(i).getDetailId().equals(detail.getDetailId())) {
                invoiceDetails.set(i, detail);
                found = true;
                break;
            }
        }

        if (!found) {
            invoiceDetails.add(detail);
        }

        updateTableData();
    }

    /**
     * Xóa chi tiết hóa đơn
     * @param detailId ID của chi tiết cần xóa
     * @return true nếu xóa thành công, false nếu không tìm thấy
     */
    public boolean removeInvoiceDetail(String detailId) {
        for (int i = 0; i < invoiceDetails.size(); i++) {
            if (invoiceDetails.get(i).getDetailId().equals(detailId)) {
                invoiceDetails.remove(i);
                updateTableData();
                return true;
            }
        }
        return false;
    }

    /**
     * Lấy danh sách chi tiết hóa đơn hiện tại
     * @return Danh sách chi tiết hóa đơn
     */
    public ArrayList<InvoiceDetail> getInvoiceDetails() {
        return new ArrayList<>(invoiceDetails);
    }
}