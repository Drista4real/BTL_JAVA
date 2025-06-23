package model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Invoice {
    public enum InvoiceType {
        OUTPATIENT("Ngoại trú"),
        INPATIENT("Nội trú"),
        PHARMACY("Nhà thuốc"),
        CONSULTATION("Tư vấn"),
        MEDICATION("Thuốc"),
        ROOM_CHARGE("Phí phòng"),
        PROCEDURE("Thủ thuật"),
        SERVICE("Dịch vụ"),
        OTHER("Khác");

        private final String displayName;


        InvoiceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum InvoiceStatus {
        UNPAID("Chưa thanh toán"),
        PARTIALLY_PAID("Thanh toán một phần"),
        PAID("Đã thanh toán"),
        CANCELLED("Đã hủy"),
        REFUNDED("Đã hoàn tiền"),
        PENDING("Đang xử lý");

        private final String displayName;

        InvoiceStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static class InvoiceItem {
        private String itemId;
        private String itemName;
        private String itemCode;
        private String description;
        private int quantity;
        private double unitPrice;
        private String category;

        public InvoiceItem(String itemName, String itemCode, String description, int quantity, double unitPrice, String category) {
            this.itemId = "ITEM" + UUID.randomUUID().toString().substring(0, 8);
            this.itemName = itemName;
            this.itemCode = itemCode;
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.category = category;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
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

        public double getUnitPrice() {
            return unitPrice;
        }

        public String getCategory() {
            return category;
        }
    }

    public static class PaymentRecord {
        private String paymentId;
        private double amount;
        private String paymentMethod;
        private LocalDateTime paymentDate;
        private String referenceNumber;
        private String paidBy;
        private String receivedBy;
        private String notes;

        public PaymentRecord(double amount, String paymentMethod, String paidBy, String receivedBy) {
            this.paymentId = "PAY" + UUID.randomUUID().toString().substring(0, 8);
            this.amount = amount;
            this.paymentMethod = paymentMethod;
            this.paymentDate = LocalDateTime.now();
            this.paidBy = paidBy;
            this.receivedBy = receivedBy;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(String paymentId) {
            this.paymentId = paymentId;
        }

        public double getAmount() {
            return amount;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public LocalDateTime getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(LocalDateTime paymentDate) {
            this.paymentDate = paymentDate;
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
    }

    private String invoiceId;
    private String invoiceNumber;
    private String patientId;
    private String patientName;
    private LocalDateTime createdDate;
    private LocalDateTime dueDate;
    private LocalDateTime paidDate;
    private InvoiceType invoiceType;
    private InvoiceStatus status;
    private String createdBy;
    private String approvedBy;
    private String notes;
    private double totalAmount;
    private double paidAmount;
    private double remainingAmount;
    private String paymentMethod;
    private final List<InvoiceItem> items;
    private final List<PaymentRecord> paymentHistory;

    public Invoice(String invoiceNumber, String patientID, String fullName, InvoiceType invoiceType, LocalDateTime dueDate, String createdBy) {
        this.invoiceId = "INV" + UUID.randomUUID().toString().substring(0, 8);
        this.invoiceNumber = this.invoiceNumber;
        this.patientId = patientId;
        this.patientName = patientName;
        this.createdDate = LocalDateTime.now();
        this.dueDate = this.dueDate;
        this.invoiceType = this.invoiceType;
        this.status = InvoiceStatus.UNPAID;
        this.createdBy = this.createdBy;
        this.totalAmount = 0.0;
        this.paidAmount = 0.0;
        this.remainingAmount = 0.0;
        this.items = new ArrayList<>();
        this.paymentHistory = new ArrayList<>();
    }

    public void addItem(InvoiceItem item) {
        if (item != null && item.getQuantity() > 0 && item.getUnitPrice() >= 0) {
            items.add(item);
            totalAmount += item.getQuantity() * item.getUnitPrice();
            remainingAmount = totalAmount - paidAmount;
            updateStatus();
        }
    }

    public void removeItem(InvoiceItem item) {
        if (items.remove(item)) {
            totalAmount -= item.getQuantity() * item.getUnitPrice();
            remainingAmount = totalAmount - paidAmount;
            updateStatus();
        }
    }

    public void recordPayment(double amount, String paymentMethod, String paidBy, String receivedBy) {
        if (status == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Không thể ghi nhận thanh toán cho hóa đơn đã hủy!");
        }
        if (status == InvoiceStatus.PAID) {
            throw new IllegalStateException("Hóa đơn đã được thanh toán đầy đủ!");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0!");
        }
        if (amount > remainingAmount) {
            throw new IllegalArgumentException("Số tiền thanh toán vượt quá số tiền còn lại!");
        }

        PaymentRecord payment = new PaymentRecord(amount, paymentMethod, paidBy, receivedBy);
        paymentHistory.add(payment);
        paidAmount += amount;
        remainingAmount = totalAmount - paidAmount;
        this.paymentMethod = paymentMethod;
        if (remainingAmount <= 0) {
            paidDate = LocalDateTime.now();
        }
        updateStatus();
    }

    public void cancelInvoice(String reason) {
        if (status != InvoiceStatus.CANCELLED) {
            status = InvoiceStatus.CANCELLED;
            notes = (notes == null ? "" : notes + "; ") + "Hủy: " + reason;
        }
    }

    public void refundInvoice(String reason) {
        if (status == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Hóa đơn đã bị hủy, không thể hoàn tiền!");
        }
        if (paidAmount <= 0) {
            throw new IllegalStateException("Hóa đơn chưa có thanh toán để hoàn tiền!");
        }

        double refundAmount = paidAmount;
        if (refundAmount > totalAmount) {
            throw new IllegalStateException("Số tiền hoàn lại vượt quá tổng số tiền hóa đơn!");
        }

        PaymentRecord refund = new PaymentRecord(-refundAmount, paymentMethod, patientId, createdBy);
        refund.setNotes("Hoàn tiền: " + reason);
        paymentHistory.add(refund);
        paidAmount = 0;
        remainingAmount = totalAmount;
        status = InvoiceStatus.REFUNDED;
        paidDate = null;
        notes = (notes == null ? "" : notes + "; ") + "Hoàn tiền: " + reason;
    }

    private void updateStatus() {
        if (status == InvoiceStatus.CANCELLED || status == InvoiceStatus.REFUNDED) {
            return;
        }
        if (paidAmount >= totalAmount && totalAmount > 0) {
            status = InvoiceStatus.PAID;
        } else if (paidAmount > 0) {
            status = InvoiceStatus.PARTIALLY_PAID;
        } else {
            status = InvoiceStatus.UNPAID;
        }
    }

    public boolean isOverdue() {
        return status != InvoiceStatus.PAID && status != InvoiceStatus.CANCELLED && dueDate.isBefore(LocalDateTime.now());
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
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

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        remainingAmount = totalAmount - paidAmount;
        updateStatus();
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
        remainingAmount = totalAmount - paidAmount;
        updateStatus();
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
        updateStatus();
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<InvoiceItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<PaymentRecord> getPaymentHistory() {
        return Collections.unmodifiableList(paymentHistory);
    }
}