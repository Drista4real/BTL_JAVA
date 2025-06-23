package model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class InvoiceDetail {
    private String detailId;
    private String invoiceId;
    private String serviceId;
    private String serviceName;
    private String serviceCode;
    private String description;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private double discountPercent;
    private double discountAmount;
    private double finalPrice;
    private String category;
    private String unit;
    private boolean isCancelled;
    private String cancelReason;
    private String prescribedBy;
    private LocalDateTime prescribedDate;
    private String performedBy;
    private LocalDateTime performedDate;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public InvoiceDetail(String invoiceId, String serviceId, String serviceName, String serviceCode, int quantity, double unitPrice, String category) {
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            throw new IllegalArgumentException("InvoiceID không được để trống!");
        }
        this.detailId = "DET" + UUID.randomUUID().toString().substring(0, 8);
        this.invoiceId = invoiceId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceCode = serviceCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.isCancelled = false;
        calculatePrices();
    }

    private void calculatePrices() {
        this.totalPrice = quantity * unitPrice;
        this.discountAmount = totalPrice * (discountPercent / 100);
        this.finalPrice = totalPrice - discountAmount;
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0!");
        }
        this.quantity = newQuantity;
        calculatePrices();
        this.updatedAt = LocalDateTime.now();
    }

    public void applyDiscount(double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Tỷ lệ giảm giá phải từ 0 đến 100!");
        }
        this.discountPercent = discountPercent;
        calculatePrices();
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        if (!isCancelled) {
            this.isCancelled = true;
            this.cancelReason = reason;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void restore() {
        if (isCancelled) {
            this.isCancelled = false;
            this.cancelReason = null;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void setPerformer(String performedBy, LocalDateTime performedDate) {
        this.performedBy = performedBy;
        this.performedDate = performedDate;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
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

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
        this.updatedAt = LocalDateTime.now();
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
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Đơn giá không được âm!");
        }
        this.unitPrice = unitPrice;
        calculatePrices();
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

    public boolean isCancelled() {
        return isCancelled;
    }

    public String getCancelReason() {
        return cancelReason;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }
}