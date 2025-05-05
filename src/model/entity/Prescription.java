package model.entity;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Prescription {
    private String prescriptionId;
    private String patientId;
    private String doctorId;
    private LocalDate prescriptionDate;
    private List<PrescriptionDetail> details;

    public Prescription() {
        this.details = new ArrayList<>();
    }

    public Prescription(String patientId, String doctorId, LocalDate prescriptionDate) {
        this.prescriptionId = generatePrescriptionId();
        this.patientId = patientId;
        this.setDoctorId(doctorId); // Sử dụng setter để kiểm tra
        this.prescriptionDate = prescriptionDate;
        this.details = new ArrayList<>();
    }

    public Prescription(String prescriptionId, String patientId, String doctorId, LocalDate prescriptionDate,
                        List<PrescriptionDetail> details) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.setDoctorId(doctorId); // Sử dụng setter để kiểm tra
        this.prescriptionDate = prescriptionDate;
        this.details = details != null ? details : new ArrayList<>();
    }

    private String generatePrescriptionId() {
        String newId = "PRE-001";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false",
                "root", "050705");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT PrescriptionID FROM Prescriptions ORDER BY CAST(SUBSTRING(PrescriptionID, 5) AS SIGNED) DESC LIMIT 1")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String lastId = rs.getString("PrescriptionID");
                int numericPart = Integer.parseInt(lastId.substring(4)) + 1;
                newId = String.format("PRE-%03d", numericPart);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newId;
    }

    public void addPrescriptionDetail(PrescriptionDetail detail) {
        if (detail != null) {
            detail.setPrescriptionId(this.prescriptionId);
            this.details.add(detail);
        }
    }

    public double calculateTotalCost() {
        return details.stream()
                .mapToDouble(PrescriptionDetail::calculateCost)
                .sum();
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor ID cannot be empty");
        }

        // Kiểm tra xem doctorId có tồn tại trong bảng UserAccounts với vai trò 'Bac si' hay không
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false",
                "root", "050705");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT UserID FROM UserAccounts WHERE UserID = ? AND Role = 'Bac si'")) {
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new IllegalArgumentException("Invalid Doctor ID: " + doctorId + " does not exist or is not a doctor");
            }
            this.doctorId = doctorId;
        } catch (SQLException e) {
            throw new RuntimeException("Error validating Doctor ID: " + e.getMessage());
        }
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public List<PrescriptionDetail> getDetails() {
        return details;
    }

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