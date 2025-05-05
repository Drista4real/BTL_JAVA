package model.entity;

import model.utils.ExceptionUtils;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

public class MedicalRecordService {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "050705";
    private final JPanel parentPanel;

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không thể tải driver MySQL: " + e.getMessage());
        }
    }

    public MedicalRecordService(JPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    public MedicalRecord loadMedicalRecordByPatientId(String patientId) {
        MedicalRecord medicalRecord = null;
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            // Lấy thông tin cơ bản của bệnh nhân từ bảng Patients
            String patientQuery = "SELECT PatientID, FullName, DateOfBirth, IllnessInfo, Height, Weight, BloodType FROM Patients WHERE PatientID = ?";
            try (PreparedStatement psPatient = conn.prepareStatement(patientQuery)) {
                psPatient.setString(1, patientId);
                ResultSet rsPatient = psPatient.executeQuery();
                if (rsPatient.next()) {
                    String recordId = UUID.randomUUID().toString();
                    String fullName = rsPatient.getString("FullName");
                    LocalDate dateOfBirth = rsPatient.getDate("DateOfBirth") != null ?
                            rsPatient.getDate("DateOfBirth").toLocalDate() : LocalDate.now().minusYears(30);
                    String illnessInfo = rsPatient.getString("IllnessInfo");
                    Double height = rsPatient.getDouble("Height");
                    if (rsPatient.wasNull()) height = null;
                    Double weight = rsPatient.getDouble("Weight");
                    if (rsPatient.wasNull()) weight = null;
                    String bloodType = rsPatient.getString("BloodType");

                    medicalRecord = new MedicalRecord(
                            recordId,
                            patientId,
                            dateOfBirth,
                            bloodType,
                            height,
                            weight,
                            illnessInfo != null ? illnessInfo : "Không có thông tin dị ứng",
                            illnessInfo != null ? illnessInfo : "Không có bệnh mãn tính",
                            "Không có thông tin",
                            "Không có ghi chú"
                    );
                } else {
                    JOptionPane.showMessageDialog(parentPanel, "Không tìm thấy bệnh nhân với ID: " + patientId, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }

            // Lấy các bản ghi y tế từ bảng MedicalRecords
            String recordsQuery = "SELECT RecordID, DoctorID, Diagnosis, Treatment, RecordDate, Notes, LifestyleRecommendations FROM MedicalRecords WHERE PatientID = ?";
            try (PreparedStatement psRecords = conn.prepareStatement(recordsQuery)) {
                psRecords.setString(1, patientId);
                ResultSet rsRecords = psRecords.executeQuery();
                while (rsRecords.next()) {
                    String entryId = rsRecords.getString("RecordID");
                    LocalDate recordDate = rsRecords.getDate("RecordDate") != null ?
                            rsRecords.getDate("RecordDate").toLocalDate() : LocalDate.now();
                    String doctorId = rsRecords.getString("DoctorID");
                    String diagnosis = rsRecords.getString("Diagnosis");
                    String treatment = rsRecords.getString("Treatment");
                    String notes = rsRecords.getString("Notes");
                    String lifestyleRecommendations = rsRecords.getString("LifestyleRecommendations");

                    MedicalRecord.MedicalEntry entry = new MedicalRecord.MedicalEntry(
                            entryId,
                            recordDate.atStartOfDay(),
                            doctorId,
                            diagnosis,
                            diagnosis,
                            treatment,
                            "",
                            "",
                            notes != null ? notes : "",
                            notes != null ? notes : "",
                            lifestyleRecommendations != null ? lifestyleRecommendations : ""
                    );
                    medicalRecord.addMedicalEntry(entry);
                }
            }
        } catch (SQLException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
            return null;
        }
        return medicalRecord;
    }

    public void saveMedicalEntry(MedicalRecord.MedicalEntry entry, String patientId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, Diagnosis, Treatment, RecordDate, Notes, LifestyleRecommendations) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, entry.getEntryId());
                ps.setString(2, patientId);
                ps.setString(3, entry.getDoctorId());
                ps.setString(4, entry.getDiagnosis());
                ps.setString(5, entry.getTreatmentPlan());
                ps.setDate(6, java.sql.Date.valueOf(entry.getDate().toLocalDate()));
                ps.setString(7, entry.getNotes());
                ps.setString(8, entry.getLifestyleRecommendations());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
            throw e;
        }
    }
}