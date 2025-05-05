package model.backend;

import model.entity.BENHNHAN;
import model.entity.BENHNHANBAOHIEMYTE;
import model.entity.Admission;
import model.entity.MedicalRecord;
import model.utils.ExceptionUtils;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.UUID;
import javax.swing.JPanel;

public class Demo1 {
    private Hashtable<String, BENHNHAN> Danhsach;
    private JPanel parentPanel;
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "050705";
    private final model.entity.MedicalRecordService medicalRecordService;

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không thể tải driver MySQL: " + e.getMessage());
        }
    }

    public Hashtable<String, BENHNHAN> getDanhsach() {
        return Danhsach;
    }

    public void setDanhsach(Hashtable<String, BENHNHAN> Danhsach) {
        this.Danhsach = Danhsach;
    }

    public Demo1(JPanel parentPanel) {
        this.Danhsach = new Hashtable<>();
        this.parentPanel = parentPanel;
        this.medicalRecordService = new model.entity.MedicalRecordService(parentPanel);
    }

    public void GhiFile() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String patientQuery = "INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, CreatedAt, Height, Weight, BloodType) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String insuranceQuery = "INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String admissionQuery = "INSERT INTO Admissions (AdmissionID, PatientID, DoctorID, RoomID, AdmissionDate, Notes) VALUES (?, ?, ?, ?, ?, ?)";

            for (BENHNHAN bn : Danhsach.values()) {
                if (bn instanceof BENHNHANBAOHIEMYTE) {
                    BENHNHANBAOHIEMYTE patient = (BENHNHANBAOHIEMYTE) bn;

                    // Insert into Patients
                    try (PreparedStatement psPatient = conn.prepareStatement(patientQuery)) {
                        psPatient.setString(1, patient.getMABN());
                        psPatient.setString(2, "U" + patient.getMABN());
                        psPatient.setString(3, patient.getHoten());
                        psPatient.setDate(4, java.sql.Date.valueOf(LocalDate.now().minusYears(30)));
                        psPatient.setString(5, "Nam");
                        psPatient.setString(6, "09" + patient.getMABN().substring(1));
                        psPatient.setString(7, "Unknown Address");
                        psPatient.setDate(8, patient.getNgaynhapvien() != null ? java.sql.Date.valueOf(patient.getNgaynhapvien()) : null);
                        psPatient.setDouble(9, 170.0); // Giả định Height
                        psPatient.setDouble(10, 65.0); // Giả định Weight
                        psPatient.setString(11, "A+"); // Giả định BloodType
                        psPatient.executeUpdate();
                    }

                    // Insert into Insurance
                    if (patient.getMSBH() != null && !patient.getMSBH().isEmpty()) {
                        try (PreparedStatement psInsurance = conn.prepareStatement(insuranceQuery)) {
                            psInsurance.setString(1, "I" + patient.getMABN());
                            psInsurance.setString(2, patient.getMABN());
                            psInsurance.setString(3, "BHYT");
                            psInsurance.setString(4, patient.getMSBH());
                            psInsurance.setDate(5, java.sql.Date.valueOf(LocalDate.now().minusYears(1)));
                            psInsurance.setDate(6, java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
                            psInsurance.setString(7, "Hoat Dong");
                            psInsurance.executeUpdate();
                        }
                    }

                    // Insert into Admissions
                    if (patient.getAdmission() != null) {
                        try (PreparedStatement psAdmission = conn.prepareStatement(admissionQuery)) {
                            psAdmission.setString(1, "AD" + patient.getMABN());
                            psAdmission.setString(2, patient.getMABN());
                            psAdmission.setString(3, "U001");
                            psAdmission.setString(4, patient.getPhongTYC() ? "R002" : "R001");
                            psAdmission.setDate(5, java.sql.Date.valueOf(patient.getNgaynhapvien()));
                            psAdmission.setString(6, patient.getGhiChu());
                            psAdmission.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
            throw new SQLException("Lỗi khi ghi dữ liệu vào cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public void DocFile() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Danhsach.clear();

            String query = "SELECT p.PatientID, p.FullName, p.CreatedAt, i.PolicyNumber, a.AdmissionID, a.DoctorID, a.RoomID, a.AdmissionDate, a.Notes " +
                    "FROM Patients p " +
                    "LEFT JOIN Insurance i ON p.PatientID = i.PatientID " +
                    "LEFT JOIN Admissions a ON p.PatientID = a.PatientID";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String mabn = rs.getString("PatientID");
                    String hoten = rs.getString("FullName");
                    LocalDate ngaynhapvien = rs.getDate("AdmissionDate") != null ?
                            rs.getDate("AdmissionDate").toLocalDate() : null;
                    String mabh = rs.getString("PolicyNumber");
                    String admissionId = rs.getString("AdmissionID");
                    String doctorId = rs.getString("DoctorID");
                    String roomId = rs.getString("RoomID");
                    String admissionDateStr = rs.getDate("AdmissionDate") != null ?
                            rs.getDate("AdmissionDate").toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
                    String notes = rs.getString("Notes");
                    boolean phongtyc = roomId != null && roomId.equals("R002");
                    char loaiBH = (mabh != null && !mabh.isEmpty()) ? 'y' : 'x';

                    Admission admission = null;
                    if (admissionId != null && roomId != null) {
                        admission = new Admission(admissionId, mabn, admissionDateStr, doctorId != null ? doctorId : "U001", roomId, notes);
                    }

                    BENHNHANBAOHIEMYTE benhnhan = new BENHNHANBAOHIEMYTE(
                            loaiBH, mabn, notes, null, hoten, ngaynhapvien, mabh, phongtyc, admission
                    );
                    Danhsach.put(mabn, benhnhan);
                    System.out.println("Đã thêm bệnh nhân: " + mabn);
                }
            }
        } catch (SQLException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
        }
    }

    public void NhapGUI(BENHNHAN benhnhan) {
        Danhsach.put(benhnhan.getMABN(), benhnhan);
        try {
            GhiFile();
        } catch (SQLException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
        }
    }

    public void SuaGUI(BENHNHAN benhnhan) {
        Danhsach.replace(benhnhan.getMABN(), benhnhan);
        try {
            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                String patientQuery = "UPDATE Patients SET FullName = ?, CreatedAt = ?, Height = ?, Weight = ?, BloodType = ? WHERE PatientID = ?";
                String insuranceQuery = "UPDATE Insurance SET PolicyNumber = ? WHERE PatientID = ?";
                String admissionQuery = "UPDATE Admissions SET RoomID = ?, AdmissionDate = ?, Notes = ? WHERE PatientID = ?";

                if (benhnhan instanceof BENHNHANBAOHIEMYTE) {
                    BENHNHANBAOHIEMYTE patient = (BENHNHANBAOHIEMYTE) benhnhan;

                    try (PreparedStatement psPatient = conn.prepareStatement(patientQuery)) {
                        psPatient.setString(1, patient.getHoten());
                        psPatient.setDate(2, patient.getNgaynhapvien() != null ? java.sql.Date.valueOf(patient.getNgaynhapvien()) : null);
                        psPatient.setDouble(3, 170.0); // Giả định Height
                        psPatient.setDouble(4, 65.0); // Giả định Weight
                        psPatient.setString(5, "A+"); // Giả định BloodType
                        psPatient.setString(6, patient.getMABN());
                        psPatient.executeUpdate();
                    }

                    try (PreparedStatement psInsurance = conn.prepareStatement(insuranceQuery)) {
                        psInsurance.setString(1, patient.getMSBH());
                        psInsurance.setString(2, patient.getMABN());
                        psInsurance.executeUpdate();
                    }

                    if (patient.getAdmission() != null) {
                        try (PreparedStatement psAdmission = conn.prepareStatement(admissionQuery)) {
                            psAdmission.setString(1, patient.getPhongTYC() ? "R002" : "R001");
                            psAdmission.setDate(2, java.sql.Date.valueOf(patient.getNgaynhapvien()));
                            psAdmission.setString(3, patient.getGhiChu());
                            psAdmission.setString(4, patient.getMABN());
                            psAdmission.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
        }
    }

    public BENHNHAN Tim(String mabn) {
        if (mabn == null || mabn.trim().isEmpty()) {
            return null;
        }
        mabn = mabn.trim();
        return Danhsach.get(mabn);
    }

    public void Xoa(String mabn) {
        Danhsach.remove(mabn);
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String[] queries = {
                    "DELETE FROM Admissions WHERE PatientID = ?",
                    "DELETE FROM Insurance WHERE PatientID = ?",
                    "DELETE FROM Patients WHERE PatientID = ?"
            };

            for (String query : queries) {
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, mabn);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
        }
    }

    public void TongtienTL() {
        double TongYT = 0, TongXH = 0, Tong = 0;
        for (BENHNHAN vbn : Danhsach.values()) {
            if (vbn instanceof BENHNHANBAOHIEMYTE) {
                TongYT += vbn.TinhhoadonVP();
            } else {
                TongXH += vbn.TinhhoadonVP();
            }
        }
        Tong = TongYT + TongXH;
        System.out.println("Tổng tiền: BHYT = " + TongYT + ", Không BHYT = " + TongXH + ", Tổng = " + Tong);
    }

    public void DatLichHen(String mabn, String NgayLichHen) throws Exception {
        BENHNHAN benhnhan = Tim(mabn);
        if (benhnhan != null) {
            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                String query = "INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Reason, Status) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, "AP" + System.currentTimeMillis());
                    ps.setString(2, mabn);
                    ps.setString(3, "U001");
                    ps.setTimestamp(4, java.sql.Timestamp.valueOf(NgayLichHen.replace("YYYY", "yyyy")));
                    ps.setString(5, "Lịch hẹn định kỳ");
                    ps.setString(6, "Cho xac nhan");
                    ps.executeUpdate();
                }

                LocalDate lichHen = java.sql.Timestamp.valueOf(NgayLichHen.replace("YYYY", "yyyy"))
                        .toLocalDateTime().toLocalDate();
                benhnhan.setLichHen(lichHen);
                System.out.println("Lịch hẹn đã được đặt cho bệnh nhân " + benhnhan.getHoten() + " vào " + NgayLichHen);
            } catch (SQLException e) {
                ExceptionUtils.handleGeneralException(parentPanel, e);
                throw new Exception("Lỗi khi đặt lịch hẹn: " + e.getMessage());
            }
        } else {
            throw new Exception("Bệnh nhân không tồn tại");
        }
    }

    public void GhiChuBS(String mabn, String GhiChumoi) throws Exception {
        BENHNHAN benhnhan = Tim(mabn);
        if (benhnhan != null) {
            MedicalRecord record = medicalRecordService.loadMedicalRecordByPatientId(mabn);
            if (record != null) {
                MedicalRecord.MedicalEntry entry = new MedicalRecord.MedicalEntry(
                        UUID.randomUUID().toString(),
                        LocalDateTime.now(),
                        "U001",
                        "",
                        "N/A",
                        "N/A",
                        "",
                        "",
                        GhiChumoi,
                        GhiChumoi,
                        ""
                );
                try {
                    medicalRecordService.saveMedicalEntry(entry, mabn);
                    record.addMedicalEntry(entry);
                    benhnhan.setGhiChu(GhiChumoi);
                    System.out.println("Ghi chú của Bác Sĩ: " + benhnhan.getGhiChu());
                } catch (SQLException e) {
                    ExceptionUtils.handleGeneralException(parentPanel, e);
                    throw new Exception("Lỗi khi ghi chú: " + e.getMessage());
                }
            } else {
                throw new Exception("Không thể tải hồ sơ y tế cho bệnh nhân: " + mabn);
            }
        } else {
            throw new Exception("Bệnh nhân không tồn tại");
        }
    }

    public void TongLichHen() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "SELECT COUNT(*) FROM Appointments WHERE Status = 'Da xac nhan' OR Status = 'Cho xac nhan'";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    int tong = rs.getInt(1);
                    System.out.println("Tổng lịch hẹn đã được đặt: " + tong);
                }
            }
        } catch (SQLException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
        }
    }
}