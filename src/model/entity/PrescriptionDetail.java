package model.entity;

import java.util.Objects;
import java.util.UUID;

/**
 * Lớp Chi tiết đơn thuốc, biểu diễn thông tin chi tiết về thuốc được kê trong đơn thuốc.
 * Lớp này tạo mối quan hệ nhiều-nhiều giữa Đơn thuốc (Prescription) và Thuốc (Medication).
 */
public class PrescriptionDetail {
    // Thuộc tính
    private String detailId;          // ID của chi tiết đơn thuốc
    private String prescriptionId;    // Liên kết đến đơn thuốc
    private String medicationId;      // Liên kết đến thông tin thuốc
    private String dosage;            // Liều lượng của thuốc
    private String instructions;      // Hướng dẫn sử dụng thuốc
    private double price;             // Giá của thuốc
    private int quantity;             // Số lượng thuốc

    /**
     * Constructor mặc định
     */
    public PrescriptionDetail() {
        this.detailId = UUID.randomUUID().toString();
    }

    /**
     * Constructor với các thông tin cơ bản
     *
     * @param prescriptionId ID của đơn thuốc
     * @param medicationId ID của thuốc
     * @param dosage Liều lượng của thuốc
     * @param instructions Hướng dẫn sử dụng thuốc
     */
    public PrescriptionDetail(String prescriptionId, String medicationId, String dosage, String instructions) {
        this.detailId = UUID.randomUUID().toString();
        this.prescriptionId = prescriptionId;
        this.medicationId = medicationId;
        this.dosage = dosage;
        this.instructions = instructions;
        this.quantity = 1;
    }

    /**
     * Constructor đầy đủ tham số
     *
     * @param prescriptionId ID của đơn thuốc
     * @param medicationId ID của thuốc
     * @param dosage Liều lượng của thuốc
     * @param instructions Hướng dẫn sử dụng thuốc
     * @param price Giá của thuốc
     * @param quantity Số lượng thuốc
     */
    public PrescriptionDetail(String prescriptionId, String medicationId, String dosage,
                              String instructions, double price, int quantity) {
        this.detailId = UUID.randomUUID().toString();
        this.prescriptionId = prescriptionId;
        this.medicationId = medicationId;
        this.dosage = dosage;
        this.instructions = instructions;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Constructor đầy đủ tham số bao gồm ID
     *
     * @param detailId ID của chi tiết đơn thuốc
     * @param prescriptionId ID của đơn thuốc
     * @param medicationId ID của thuốc
     * @param dosage Liều lượng của thuốc
     * @param instructions Hướng dẫn sử dụng thuốc
     * @param price Giá của thuốc
     * @param quantity Số lượng thuốc
     */
    public PrescriptionDetail(String detailId, String prescriptionId, String medicationId,
                              String dosage, String instructions, double price, int quantity) {
        this.detailId = detailId;
        this.prescriptionId = prescriptionId;
        this.medicationId = medicationId;
        this.dosage = dosage;
        this.instructions = instructions;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Tính tổng chi phí của thuốc trong chi tiết đơn thuốc
     *
     * @return Tổng chi phí (giá * số lượng)
     */
    public double calculateCost() {
        return price * quantity;
    }

    // Getters và Setters

    /**
     * Lấy ID của chi tiết đơn thuốc
     *
     * @return ID của chi tiết đơn thuốc
     */
    public String getDetailId() {
        return detailId;
    }

    /**
     * Thiết lập ID của chi tiết đơn thuốc
     *
     * @param detailId ID của chi tiết đơn thuốc
     */
    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    /**
     * Lấy ID của đơn thuốc
     *
     * @return ID của đơn thuốc
     */
    public String getPrescriptionId() {
        return prescriptionId;
    }

    /**
     * Thiết lập ID của đơn thuốc
     *
     * @param prescriptionId ID của đơn thuốc
     */
    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    /**
     * Lấy ID của thuốc
     *
     * @return ID của thuốc
     */
    public String getMedicationId() {
        return medicationId;
    }

    /**
     * Thiết lập ID của thuốc
     *
     * @param medicationId ID của thuốc
     */
    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    /**
     * Lấy liều lượng thuốc
     *
     * @return Liều lượng thuốc
     */
    public String getDosage() {
        return dosage;
    }

    /**
     * Thiết lập liều lượng thuốc
     *
     * @param dosage Liều lượng thuốc
     */
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    /**
     * Lấy hướng dẫn sử dụng thuốc
     *
     * @return Hướng dẫn sử dụng thuốc
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Thiết lập hướng dẫn sử dụng thuốc
     *
     * @param instructions Hướng dẫn sử dụng thuốc
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * Lấy giá thuốc
     *
     * @return Giá thuốc
     */
    public double getPrice() {
        return price;
    }

    /**
     * Thiết lập giá thuốc
     *
     * @param price Giá thuốc
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Lấy số lượng thuốc
     *
     * @return Số lượng thuốc
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Thiết lập số lượng thuốc
     *
     * @param quantity Số lượng thuốc
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Chi tiết đơn thuốc [ID: " + detailId +
                ", Thuốc: " + medicationId +
                ", Liều lượng: " + dosage +
                ", Hướng dẫn: " + instructions +
                ", Số lượng: " + quantity +
                ", Giá: " + String.format("%,.0f", price) + " VND]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrescriptionDetail that = (PrescriptionDetail) o;
        return Objects.equals(detailId, that.detailId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(detailId);
    }
}