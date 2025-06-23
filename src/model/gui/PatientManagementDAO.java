package model.gui;

import model.entity.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientManagementDAO {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Pha2k5@";
    private static final UserService userService = new UserService();

    public static List<Admission> getAllAdmissions() {
        List<Admission> admissions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "SELECT * FROM Admissions";
            try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    admissions.add(new Admission(
                            rs.getString("AdmissionID"),
                            rs.getString("PatientID"),
                            rs.getDate("AdmissionDate") != null ? rs.getDate("AdmissionDate").toLocalDate() : null,
                            rs.getString("DoctorID"),
                            rs.getString("RoomID"),
                            rs.getDate("DischargeDate") != null ? rs.getDate("DischargeDate").toLocalDate() : null,
                            rs.getString("Notes")
                    ));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return admissions;
    }

    public static List<Admission> getAdmissionsByDoctor(String doctorId) {
        List<Admission> admissions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "SELECT * FROM Admissions WHERE DoctorID = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, doctorId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        admissions.add(new Admission(
                                rs.getString("AdmissionID"),
                                rs.getString("PatientID"),
                                rs.getDate("AdmissionDate") != null ? rs.getDate("AdmissionDate").toLocalDate() : null,
                                rs.getString("DoctorID"),
                                rs.getString("RoomID"),
                                rs.getDate("DischargeDate") != null ? rs.getDate("DischargeDate").toLocalDate() : null,
                                rs.getString("Notes")
                        ));
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return admissions;
    }

    public static boolean saveAdmission(Admission admission) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "INSERT INTO Admissions (AdmissionID, PatientID, DoctorID, RoomID, AdmissionDate, DischargeDate, Notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "DoctorID = VALUES(DoctorID), RoomID = VALUES(RoomID), AdmissionDate = VALUES(AdmissionDate), " +
                    "DischargeDate = VALUES(DischargeDate), Notes = VALUES(Notes)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, admission.getAdmissionId());
                ps.setString(2, admission.getPatientId());
                ps.setString(3, admission.getDoctorId());
                ps.setString(4, admission.getRoomId());
                ps.setDate(5, admission.getAdmissionDate() != null ? java.sql.Date.valueOf(admission.getAdmissionDate()) : null);
                ps.setDate(6, admission.getDischargeDate() != null ? java.sql.Date.valueOf(admission.getDischargeDate()) : null);
                ps.setString(7, admission.getNotes());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteAdmission(String admissionId) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "DELETE FROM Admissions WHERE AdmissionID = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, admissionId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static BENHNHAN getPatientById(String patientId) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "SELECT p.*, i.PolicyNumber, a.AdmissionID, a.RoomID, a.AdmissionDate, a.DischargeDate, a.Notes " +
                    "FROM Patients p " +
                    "LEFT JOIN Insurance i ON p.PatientID = i.PatientID " +
                    "LEFT JOIN Admissions a ON p.PatientID = a.PatientID " +
                    "WHERE p.PatientID = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, patientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Admission admission = null;
                        if (rs.getString("AdmissionID") != null) {
                            admission = new Admission(
                                    rs.getString("AdmissionID"),
                                    rs.getString("PatientID"),
                                    rs.getDate("AdmissionDate") != null ? rs.getDate("AdmissionDate").toLocalDate() : null,
                                    rs.getString("DoctorID"),
                                    rs.getString("RoomID"),
                                    rs.getDate("DischargeDate") != null ? rs.getDate("DischargeDate").toLocalDate() : null,
                                    rs.getString("Notes")
                            );
                        }
                        return new BENHNHANBAOHIEMYTE(
                                rs.getString("PolicyNumber") != null ? 'y' : 'n',
                                rs.getString("PatientID"),
                                rs.getString("Notes"),
                                null, // LichHen not stored in DB
                                rs.getString("FullName"),
                                rs.getDate("CreatedAt") != null ? rs.getDate("CreatedAt").toLocalDate() : null,
                                rs.getString("PolicyNumber"),
                                rs.getString("RoomID") != null && rs.getString("RoomID").equals("R002"),
                                admission
                        );
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean savePatient(BENHNHAN patient) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "INSERT INTO Patients (PatientID, FullName, CreatedAt) " +
                    "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE FullName = VALUES(FullName), CreatedAt = VALUES(CreatedAt)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, patient.getMABN());
                ps.setString(2, patient.getHoten());
                ps.setDate(3, patient.getNgaynhapvien() != null ? java.sql.Date.valueOf(patient.getNgaynhapvien()) : null);
                boolean success = ps.executeUpdate() > 0;

                if (success && patient instanceof BENHNHANBAOHIEMYTE) {
                    BENHNHANBAOHIEMYTE bhytPatient = (BENHNHANBAOHIEMYTE) patient;
                    if (bhytPatient.getLoaiBH() == 'y' && !bhytPatient.getMSBH().isEmpty()) {
                        return saveInsurance(patient.getMABN(), bhytPatient.getMSBH());
                    }
                }
                return success;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deletePatient(String patientId) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "DELETE FROM Patients WHERE PatientID = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, patientId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> getAllPatients() {
        List<User> patients = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "SELECT UserID FROM UserAccounts WHERE Role = 'Benh nhan'";
            try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = userService.getUserById(rs.getString("UserID"));
                    if (user != null) {
                        patients.add(user);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return patients;
    }

    public static List<User> getAllDoctors() {
        List<User> doctors = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "SELECT UserID FROM UserAccounts WHERE Role = 'Bac si'";
            try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = userService.getUserById(rs.getString("UserID"));
                    if (user != null) {
                        doctors.add(user);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    public static String[] getPatientMedicalInfo(String patientId) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "SELECT Diagnosis, Treatment FROM MedicalRecords WHERE PatientID = ? ORDER BY RecordDate DESC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, patientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new String[]{rs.getString("Diagnosis"), rs.getString("Treatment")};
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new String[]{"", ""};
    }

    public static boolean saveMedicalRecord(String patientId, String doctorId, String diagnosis, String treatment, String notes) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, Diagnosis, Treatment, RecordDate, Notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, "MR" + System.currentTimeMillis());
                ps.setString(2, patientId);
                ps.setString(3, doctorId);
                ps.setString(4, diagnosis);
                ps.setString(5, treatment);
                ps.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
                ps.setString(7, notes);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePatientNotes(String patientId, String notes) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "UPDATE Admissions SET Notes = ? WHERE PatientID = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, notes);
                ps.setString(2, patientId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveInsurance(String patientId, String policyNumber) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, Status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE PolicyNumber = VALUES(PolicyNumber)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, "I" + patientId);
                ps.setString(2, patientId);
                ps.setString(3, "BHYT");
                ps.setString(4, policyNumber);
                ps.setDate(5, java.sql.Date.valueOf(LocalDate.now().minusYears(1)));
                ps.setDate(6, java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
                ps.setString(7, "Hoat Dong");
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}