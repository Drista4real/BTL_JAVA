package model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Lớp Đơn thuốc, biểu diễn thông tin về đơn thuốc trong hệ thống quản lý y tế.
 * Mỗi đơn thuốc được kê bởi một bác sĩ cho một bệnh nhân cụ thể và chứa danh sách các chi tiết đơn thuốc.
 */
public class Prescription {
    // Thuộc tính
    private String prescriptionId;            // Mã đơn thuốc (định dạng PRE-XXX)
    private String patientId;                 // Mã bệnh nhân (khóa ngoại)
    private String doctorId;                  // Mã bác sĩ (khóa ngoại)
    private LocalDate prescriptionDate;       // Ngày kê đơn
    private List<PrescriptionDetail> details; // Danh sách chi tiết đơn thuốc

    /**
     * Constructor mặc định
     */
    public Prescription() {
        this.details = new ArrayList<>();
    }

    /**
     * Constructor có tham số, tự động sinh mã đơn thuốc
     *
     * @param patientId        Mã bệnh nhân
     * @param doctorId         Mã bác sĩ
     * @param prescriptionDate Ngày kê đơn
     */
    public Prescription(String patientId, String doctorId, LocalDate prescriptionDate) {
        this.prescriptionId = generatePrescriptionId();
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.prescriptionDate = prescriptionDate;
        this.details = new ArrayList<>();
    }

    /**
     * Constructor đầy đủ tham số
     *
     * @param prescriptionId   Mã đơn thuốc
     * @param patientId        Mã bệnh nhân
     * @param doctorId         Mã bác sĩ
     * @param prescriptionDate Ngày kê đơn
     * @param details          Danh sách chi tiết đơn thuốc
     */
    public Prescription(String prescriptionId, String patientId, String doctorId, LocalDate prescriptionDate,
                        List<PrescriptionDetail> details) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.prescriptionDate = prescriptionDate;
        this.details = details != null ? details : new ArrayList<>();
    }

    /**
     * Tạo mã đơn thuốc tự động với định dạng PRE-XXX
     * Phương thức này truy vấn database để lấy ID lớn nhất hiện có và tăng thêm 1
     *
     * @return Mã đơn thuốc mới
     */
    private String generatePrescriptionId() {
        String newId = "PRE-001"; // Giá trị mặc định nếu không có đơn thuốc nào tồn tại

        try (Connection conn = /* Lấy kết nối CSDL từ lớp quản lý kết nối */null;
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT prescriptionId FROM prescriptions ORDER BY CAST(SUBSTRING(prescriptionId, 5) AS INT) DESC LIMIT 1")) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String lastId = rs.getString("prescriptionId");
                int numericPart = Integer.parseInt(lastId.substring(4)) + 1;
                newId = String.format("PRE-%03d", numericPart);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Trong trường hợp lỗi, vẫn sử dụng giá trị mặc định
        }

        return newId;
    }

    /**
     * Thêm chi tiết đơn thuốc vào danh sách
     *
     * @param detail Chi tiết đơn thuốc cần thêm
     */
    public void addPrescriptionDetail(PrescriptionDetail detail) {
        if (detail != null) {
            // Đảm bảo chi tiết đơn thuốc có cùng mã đơn thuốc
            detail.setPrescriptionId(this.prescriptionId);
            this.details.add(detail);
        }
    }

    /**
     * Tính tổng chi phí của đơn thuốc
     *
     * @return Tổng chi phí
     */
    public double calculateTotalCost() {
        return details.stream()
                .mapToDouble(PrescriptionDetail::calculateCost)
                .sum();
    }

    // Getters và Setters

    /**
     * Lấy mã đơn thuốc
     *
     * @return Mã đơn thuốc
     */
    public String getPrescriptionId() {
        return prescriptionId;
    }

    /**
     * Thiết lập mã đơn thuốc
     *
     * @param prescriptionId Mã đơn thuốc
     */
    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    /**
     * Lấy mã bệnh nhân
     *
     * @return Mã bệnh nhân
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Thiết lập mã bệnh nhân
     *
     * @param patientId Mã bệnh nhân
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * Lấy mã bác sĩ
     *
     * @return Mã bác sĩ
     */
    public String getDoctorId() {
        return doctorId;
    }

    /**
     * Thiết lập mã bác sĩ
     *
     * @param doctorId Mã bác sĩ
     */
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * Lấy ngày kê đơn
     *
     * @return Ngày kê đơn
     */
    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    /**
     * Thiết lập ngày kê đơn
     *
     * @param prescriptionDate Ngày kê đơn
     */
    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    /**
     * Lấy danh sách chi tiết đơn thuốc
     *
     * @return Danh sách chi tiết đơn thuốc
     */
    public List<PrescriptionDetail> getDetails() {
        return details;
    }

    /**
     * Thiết lập danh sách chi tiết đơn thuốc
     *
     * @param details Danh sách chi tiết đơn thuốc
     */
    public void setDetails(List<PrescriptionDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Đơn thuốc [Mã: " + prescriptionId +
                ", Bệnh nhân: " + patientId +
                ", Bác sĩ: " + doctorId +
                ", Ngày kê: " + prescriptionDate +
                ", Số loại thuốc: " + (details != null ? details.size() : 0) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prescription that = (Prescription) o;
        return Objects.equals(prescriptionId, that.prescriptionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prescriptionId);
    }
}