package model.entity;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VitalSignsService {
    private static final String URL = "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "050705";
    private final JPanel parentPanel;

    public VitalSignsService(JPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    public List<VitalSigns> loadVitalSignsByPatientId(String patientId) throws SQLException {
        List<VitalSigns> vitalSignsList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM VitalSigns WHERE PatientId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, patientId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        VitalSigns vitalSigns = new VitalSigns(
                                rs.getString("Id"),
                                rs.getString("PatientId"),
                                rs.getDouble("Temperature"),
                                rs.getInt("SystolicBP"),
                                rs.getInt("DiastolicBP"),
                                rs.getInt("HeartRate"),
                                rs.getInt("SpO2"),
                                rs.getTimestamp("RecordTime").toLocalDateTime()
                        );
                        vitalSignsList.add(vitalSigns);
                    }
                }
            }
        }
        return vitalSignsList;
    }

    public void saveVitalSigns(VitalSigns vitalSigns) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "INSERT INTO VitalSigns (Id, PatientId, Temperature, SystolicBP, DiastolicBP, HeartRate, SpO2, RecordTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, vitalSigns.getId());
                stmt.setString(2, vitalSigns.getPatientId());
                stmt.setDouble(3, vitalSigns.getTemperature());
                stmt.setInt(4, vitalSigns.getSystolicBP());
                stmt.setInt(5, vitalSigns.getDiastolicBP());
                stmt.setInt(6, vitalSigns.getHeartRate());
                stmt.setInt(7, vitalSigns.getSpO2());
                stmt.setTimestamp(8, Timestamp.valueOf(vitalSigns.getRecordTime()));
                stmt.executeUpdate();
            }
        }
    }

    public String generateVitalSignsId() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "SELECT COUNT(*) FROM VitalSigns";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return "VS-" + String.format("%03d", count + 1);
                }
            }
        }
        return "VS-001";
    }
}