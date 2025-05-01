package model.entity;


import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


public class InvoiceDetail {
    // Thông tin cơ bản
    private String detailId;
    private String invoiceId;
    private String serviceId;
    private String serviceName;
    private String serviceCode;
    private String description;

    // Thông tin về số lượng và giá
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private double discountPercent;
    private double discountAmount;
    private double finalPrice; // Giá sau khi đã giảm giá

    // Thông tin phân loại
    private String category;
    private String unit; // Đơn vị tính (viên, lọ, lần...)

    // Thông tin đối với dịch vụ y tế
    private String prescribedBy; // Bác sĩ chỉ định
    private LocalDateTime prescribedDate; // Ngày chỉ định
    private String performedBy; // Người thực hiện
    private LocalDateTime performedDate; // Ngày thực hiện

    // Thông tin quản lý
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private boolean isCancelled;
    private String cancelReason;

    /**
     * Constructor với các thông tin cơ bản cần thiết
     */
    public InvoiceDetail(String invoiceId, String serviceId, String serviceName, String serviceCode,
                         int quantity, double unitPrice, String category) {
        this.detailId = UUID.randomUUID().toString();
        this.invoiceId = invoiceId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceCode = serviceCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.category = category;
        this.discountPercent = 0.0;
        this.discountAmount = 0.0;

        // Tính toán giá trị
        this.totalPrice = quantity * unitPrice;
        this.finalPrice = this.totalPrice;

        // Thiết lập thời gian tạo
        this.createdAt = LocalDateTime.now();
        this.isCancelled = false;
        this.unit = "lần"; // Đơn vị mặc định
    }

    /**
     * Constructor đầy đủ với nhiều thông tin hơn
     */
    public InvoiceDetail(String invoiceId, String serviceId, String serviceName, String serviceCode,
                         String description, int quantity, double unitPrice, String category,
                         String unit, double discountPercent, String prescribedBy,
                         LocalDateTime prescribedDate, String createdBy) {
        this(invoiceId, serviceId, serviceName, serviceCode, quantity, unitPrice, category);
        this.description = description;
        this.unit = unit;
        this.prescribedBy = prescribedBy;
        this.prescribedDate = prescribedDate;
        this.createdBy = createdBy;

        // Áp dụng giảm giá
        this.applyDiscount(discountPercent);
    }

    /**
     * Áp dụng giảm giá theo phần trăm
     */
    public void applyDiscount(double discountPercent) {
        this.discountPercent = discountPercent;
        this.discountAmount = (this.totalPrice * discountPercent) / 100;
        this.finalPrice = this.totalPrice - this.discountAmount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Áp dụng giảm giá theo số tiền cụ thể
     */
    public void applyDiscountAmount(double amount) {
        if (amount > this.totalPrice) {
            throw new IllegalArgumentException("Số tiền giảm giá không thể lớn hơn tổng tiền");
        }
        this.discountAmount = amount;
        this.discountPercent = (amount * 100) / this.totalPrice;
        this.finalPrice = this.totalPrice - this.discountAmount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cập nhật số lượng và tính lại giá
     */
    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
        this.totalPrice = this.quantity * this.unitPrice;

        // Tính lại giảm giá
        if (this.discountPercent > 0) {
            this.discountAmount = (this.totalPrice * this.discountPercent) / 100;
        }
        this.finalPrice = this.totalPrice - this.discountAmount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Hủy chi tiết hóa đơn
     */
    public void cancel(String reason) {
        this.isCancelled = true;
        this.cancelReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Khôi phục chi tiết hóa đơn đã hủy
     */
    public void restore() {
        this.isCancelled = false;
        this.cancelReason = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cập nhật thông tin người thực hiện dịch vụ
     */
    public void setPerformer(String performedBy, LocalDateTime performedDate) {
        this.performedBy = performedBy;
        this.performedDate = performedDate;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters và Setters

    public String getDetailId() {
        return detailId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
        this.updatedAt = LocalDateTime.now();
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.totalPrice = this.quantity * this.unitPrice;

        // Tính lại giảm giá
        if (this.discountPercent > 0) {
            this.discountAmount = (this.totalPrice * this.discountPercent) / 100;
        }
        this.finalPrice = this.totalPrice - this.discountAmount;
        this.updatedAt = LocalDateTime.now();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPrescribedBy() {
        return prescribedBy;
    }

    public void setPrescribedBy(String prescribedBy) {
        this.prescribedBy = prescribedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getPrescribedDate() {
        return prescribedDate;
    }

    public void setPrescribedDate(LocalDateTime prescribedDate) {
        this.prescribedDate = prescribedDate;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public LocalDateTime getPerformedDate() {
        return performedDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(serviceName).append(" (").append(serviceCode).append("): ")
                .append(quantity).append(" ").append(unit)
                .append(" x ").append(String.format("%,.0f", unitPrice))
                .append(" = ").append(String.format("%,.0f", totalPrice)).append(" VND");

        if (discountAmount > 0) {
            sb.append(" - Giảm giá: ").append(String.format("%,.0f", discountAmount))
                    .append(" VND (").append(String.format("%.1f", discountPercent)).append("%)");
            sb.append(" - Thành tiền: ").append(String.format("%,.0f", finalPrice)).append(" VND");
        }

        if (isCancelled) {
            sb.append(" [ĐÃ HỦY]");
        }

        return sb.toString();
    }

    /**
     * Lấy thông tin chi tiết dạng text
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder(toString());

        if (description != null && !description.isEmpty()) {
            sb.append("\n  Mô tả: ").append(description);
        }

        if (prescribedBy != null && !prescribedBy.isEmpty()) {
            sb.append("\n  Bác sĩ chỉ định: ").append(prescribedBy);
            if (prescribedDate != null) {
                sb.append(" (").append(prescribedDate.toLocalDate()).append(")");
            }
        }

        if (performedBy != null && !performedBy.isEmpty()) {
            sb.append("\n  Người thực hiện: ").append(performedBy);
            if (performedDate != null) {
                sb.append(" (").append(performedDate.toLocalDate()).append(")");
            }
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceDetail that = (InvoiceDetail) o;
        return Objects.equals(detailId, that.detailId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(detailId);
    }
}
