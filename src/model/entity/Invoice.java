package model.entity;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Lớp quản lý thông tin và chức năng liên quan đến hóa đơn thanh toán.
 */
public class Invoice {
    // Các hằng số định nghĩa loại hóa đơn và trạng thái
    public enum InvoiceType {
        ROOM_CHARGE("Phí phòng bệnh"),
        MEDICATION("Phí thuốc"),
        PROCEDURE("Phí thủ thuật/phẫu thuật"),
        CONSULTATION("Phí tư vấn/khám bệnh"),
        SERVICE("Phí dịch vụ"),
        PACKAGE("Gói dịch vụ"),
        OTHER("Phí khác");

        private final String displayName;

        InvoiceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum InvoiceStatus {
        PENDING("Chờ thanh toán"),
        PARTIALLY_PAID("Thanh toán một phần"),
        PAID("Đã thanh toán"),
        CANCELLED("Đã hủy"),
        REFUNDED("Đã hoàn tiền");

        private final String displayName;

        InvoiceStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Thông tin cơ bản của hóa đơn
    private String invoiceId;
    private String invoiceNumber;
    private String patientId;
    private String patientName;
    private LocalDateTime createdDate;
    private LocalDateTime dueDate;
    private LocalDateTime paidDate;
    private InvoiceType invoiceType;
    private InvoiceStatus status;
    private String createdBy; // Người tạo hóa đơn
    private String approvedBy; // Người duyệt hóa đơn
    private String notes;

    // Thông tin thanh toán
    private double totalAmount;
    private double paidAmount;
    private double remainingAmount;
    private String paymentMethod;

    // Danh sách các mục trong hóa đơn
    private List<InvoiceItem> items;

    // Lịch sử thanh toán
    private List<PaymentRecord> paymentHistory;

    /**
     * Lớp đại diện cho một mục trong hóa đơn
     */
    public static class InvoiceItem {
        private String itemId;
        private String itemName;
        private String itemCode;
        private String description;
        private int quantity;
        private double unitPrice;
        private double totalPrice;
        private String category;

        public InvoiceItem(String itemName, String itemCode, String description,
                           int quantity, double unitPrice, String category) {
            this.itemId = UUID.randomUUID().toString();
            this.itemName = itemName;
            this.itemCode = itemCode;
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice;
            this.category = category;
        }

        // Getters and Setters
        public String getItemId() {
            return itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public String getItemCode() {
            return itemCode;
        }

        public String getDescription() {
            return description;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
            this.totalPrice = quantity * unitPrice;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        @Override
        public String toString() {
            return itemName + " (" + itemCode + "): " +
                    quantity + " x " + String.format("%,.0f", unitPrice) +
                    " = " + String.format("%,.0f", totalPrice) + " VND";
        }
    }

    /**
     * Lớp ghi lại lịch sử thanh toán
     */
    public static class PaymentRecord {
        private String paymentId;
        private LocalDateTime paymentDate;
        private double amount;
        private String paymentMethod;
        private String referenceNumber; // Số tham chiếu/giao dịch
        private String paidBy; // Người thanh toán
        private String receivedBy; // Người nhận thanh toán
        private String notes;

        public PaymentRecord(double amount, String paymentMethod, String paidBy, String receivedBy) {
            this.paymentId = UUID.randomUUID().toString();
            this.paymentDate = LocalDateTime.now();
            this.amount = amount;
            this.paymentMethod = paymentMethod;
            this.paidBy = paidBy;
            this.receivedBy = receivedBy;
            this.referenceNumber = "";
            this.notes = "";
        }

        // Getters and Setters
        public String getPaymentId() {
            return paymentId;
        }

        public LocalDateTime getPaymentDate() {
            return paymentDate;
        }

        public double getAmount() {
            return amount;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getReferenceNumber() {
            return referenceNumber;
        }

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }

        public String getPaidBy() {
            return paidBy;
        }

        public String getReceivedBy() {
            return receivedBy;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return "Thanh toán: " + String.format("%,.0f", amount) + " VND" +
                    " - Ngày: " + paymentDate.format(formatter) +
                    " - Phương thức: " + paymentMethod +
                    (referenceNumber != null && !referenceNumber.isEmpty() ?
                            " - Số tham chiếu: " + referenceNumber : "");
        }
    }

    // Constructor
    public Invoice(String invoiceNumber, String patientId, String patientName,
                   InvoiceType invoiceType, LocalDateTime dueDate, String createdBy) {
        this.invoiceId = UUID.randomUUID().toString();
        this.invoiceNumber = invoiceNumber;
        this.patientId = patientId;
        this.patientName = patientName;
        this.createdDate = LocalDateTime.now();
        this.dueDate = dueDate;
        this.paidDate = null;
        this.invoiceType = invoiceType;
        this.status = InvoiceStatus.PENDING;
        this.createdBy = createdBy;
        this.approvedBy = null;
        this.notes = "";

        this.totalAmount = 0.0;
        this.paidAmount = 0.0;
        this.remainingAmount = 0.0;
        this.paymentMethod = "";

        this.items = new ArrayList<>();
        this.paymentHistory = new ArrayList<>();
    }

    // Phương thức thêm mục vào hóa đơn
    public void addItem(InvoiceItem item) {
        items.add(item);
        recalculateTotalAmount();
    }

    // Phương thức xóa mục khỏi hóa đơn
    public void removeItem(String itemId) {
        items.removeIf(item -> item.getItemId().equals(itemId));
        recalculateTotalAmount();
    }

    // Phương thức tính lại tổng tiền
    private void recalculateTotalAmount() {
        this.totalAmount = items.stream()
                .mapToDouble(InvoiceItem::getTotalPrice)
                .sum();
        this.remainingAmount = totalAmount - paidAmount;
        updateStatus();
    }

    // Phương thức cập nhật trạng thái hóa đơn
    private void updateStatus() {
        if (status == InvoiceStatus.CANCELLED || status == InvoiceStatus.REFUNDED) {
            return; // Không thay đổi trạng thái nếu đã hủy hoặc hoàn tiền
        }

        if (paidAmount <= 0) {
            status = InvoiceStatus.PENDING;
        } else if (Math.abs(remainingAmount) < 0.01) {
            status = InvoiceStatus.PAID;
        } else {
            status = InvoiceStatus.PARTIALLY_PAID;
        }
    }

    // Phương thức ghi nhận thanh toán
    public void recordPayment(double amount, String paymentMethod, String paidBy, String receivedBy) {
        // Kiểm tra nếu hóa đơn đã bị hủy hoặc đã thanh toán đủ
        if (status == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Không thể thanh toán hóa đơn đã bị hủy");
        }

        if (status == InvoiceStatus.PAID) {
            throw new IllegalStateException("Hóa đơn đã được thanh toán đủ");
        }

        // Tạo bản ghi thanh toán
        PaymentRecord payment = new PaymentRecord(amount, paymentMethod, paidBy, receivedBy);
        paymentHistory.add(payment);

        // Cập nhật thông tin thanh toán
        this.paidAmount += amount;
        this.remainingAmount = totalAmount - paidAmount;
        this.paymentMethod = paymentMethod;

        // Nếu đã thanh toán đủ, cập nhật ngày thanh toán
        if (Math.abs(remainingAmount) < 0.01) {
            this.paidDate = LocalDateTime.now();
        }

        // Cập nhật trạng thái
        updateStatus();
    }

    // Phương thức hủy hóa đơn
    public void cancelInvoice(String reason) {
        if (status == InvoiceStatus.PAID) {
            throw new IllegalStateException("Không thể hủy hóa đơn đã thanh toán. Hãy hoàn tiền trước.");
        }

        this.status = InvoiceStatus.CANCELLED;
        this.notes = (this.notes.isEmpty() ? "" : this.notes + "\n") +
                "Hủy hóa đơn: " + reason;
    }

    // Phương thức hoàn tiền
    public void refundInvoice(String reason) {
        if (status != InvoiceStatus.PAID && status != InvoiceStatus.PARTIALLY_PAID) {
            throw new IllegalStateException("Chỉ có thể hoàn tiền cho hóa đơn đã thanh toán");
        }

        this.status = InvoiceStatus.REFUNDED;
        this.notes = (this.notes.isEmpty() ? "" : this.notes + "\n") +
                "Hoàn tiền: " + reason;
    }

    // Phương thức duyệt hóa đơn
    public void approveInvoice(String approver) {
        this.approvedBy = approver;
    }

    // Phương thức kiểm tra xem hóa đơn có quá hạn không
    public boolean isOverdue() {
        return status == InvoiceStatus.PENDING &&
                LocalDateTime.now().isAfter(dueDate);
    }

    // Getters and Setters
    public String getInvoiceId() {
        return invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getPaidDate() {
        return paidDate;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public List<InvoiceItem> getItems() {
        return new ArrayList<>(items); // Trả về bản sao để tránh sửa đổi trực tiếp
    }

    public List<PaymentRecord> getPaymentHistory() {
        return new ArrayList<>(paymentHistory); // Trả về bản sao để tránh sửa đổi trực tiếp
    }

    // Phương thức tìm kiếm mục trong hóa đơn theo ID
    public InvoiceItem findItemById(String itemId) {
        for (InvoiceItem item : items) {
            if (item.getItemId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("Hóa đơn: ").append(invoiceNumber)
                .append(" (").append(invoiceType.getDisplayName()).append(")\n")
                .append("Bệnh nhân: ").append(patientName)
                .append(" (ID: ").append(patientId).append(")\n")
                .append("Ngày tạo: ").append(createdDate.format(formatter))
                .append(" - Hạn thanh toán: ").append(dueDate.format(formatter)).append("\n")
                .append("Trạng thái: ").append(status.getDisplayName()).append("\n")
                .append("Tổng tiền: ").append(String.format("%,.0f", totalAmount)).append(" VND\n")
                .append("Đã thanh toán: ").append(String.format("%,.0f", paidAmount)).append(" VND\n")
                .append("Còn lại: ").append(String.format("%,.0f", remainingAmount)).append(" VND\n");

        return sb.toString();
    }

    // Phương thức lấy thông tin chi tiết về hóa đơn
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder(toString());

        sb.append("\nChi tiết các mục:\n");
        for (int i = 0; i < items.size(); i++) {
            sb.append(i + 1).append(". ").append(items.get(i).toString()).append("\n");
        }

        if (!paymentHistory.isEmpty()) {
            sb.append("\nLịch sử thanh toán:\n");
            for (int i = 0; i < paymentHistory.size(); i++) {
                sb.append(i + 1).append(". ").append(paymentHistory.get(i).toString()).append("\n");
            }
        }

        if (notes != null && !notes.isEmpty()) {
            sb.append("\nGhi chú: ").append(notes).append("\n");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(invoiceId, invoice.invoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId);
    }
}
