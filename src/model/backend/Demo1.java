package model.backend;

import model.entity.BENHNHAN;
import model.entity.BENHNHANBAOHIEMYTE;
import model.entity.Admission;
import model.utils.ExceptionUtils;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Hashtable;
import javax.swing.JPanel;

public class Demo1 {
    private Hashtable<String, BENHNHAN> Danhsach;
    private JPanel parentPanel;
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "050705";

    public Hashtable<String, BENHNHAN> getDanhsach() {
        return Danhsach;
    }

    public void setDanhsach(Hashtable<String, BENHNHAN> Danhsach) {
        this.Danhsach = Danhsach;
    }

    public Demo1(JPanel parentPanel) {
        this.Danhsach = new Hashtable<>();
        this.parentPanel = parentPanel;
    }

    public void GhiFile() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String patientQuery = "INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            String insuranceQuery = "INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String admissionQuery = "INSERT INTO Admissions (AdmissionID, PatientID, DoctorID, RoomID, AdmissionDate, Notes) VALUES (?, ?, ?, ?, ?, ?)";

            for (BENHNHAN bn : Danhsach.values()) {
                if (bn instanceof BENHNHANBAOHIEMYTE) {
                    BENHNHANBAOHIEMYTE patient = (BENHNHANBAOHIEMYTE) bn;

                    // Insert into Patients
                    try (PreparedStatement psPatient = conn.prepareStatement(patientQuery)) {
                        psPatient.setString(1, patient.getMABN());
                        psPatient.setString(2, "U" + patient.getMABN()); // Giả định UserID
                        psPatient.setString(3, patient.getHoten());
                        psPatient.setDate(4, java.sql.Date.valueOf(LocalDate.now().minusYears(30))); // Giả định DOB
                        psPatient.setString(5, "Nam"); // Giả định giới tính
                        psPatient.setString(6, "09" + patient.getMABN().substring(1)); // Giả định số điện thoại
                        psPatient.setString(7, "Unknown Address"); // Giả định địa chỉ
                        psPatient.setDate(8, patient.getNgaynhapvien() != null ? java.sql.Date.valueOf(patient.getNgaynhapvien()) : null);
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
                            psAdmission.setString(3, "U001"); // Giả định DoctorID
                            psAdmission.setString(4, patient.getPhongTYC() ? "R002" : "R001"); // R002 là VIP
                            psAdmission.setDate(5, java.sql.Date.valueOf(patient.getNgaynhapvien()));
                            psAdmission.setString(6, patient.getGhiChu());
                            psAdmission.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
            throw new SQLException("Lỗi khi ghi dữ liệu vào cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public void DocFile() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            Danhsach.clear();

            String query = "SELECT p.PatientID, p.FullName, p.CreatedAt, i.PolicyNumber, a.RoomID, a.AdmissionDate, a.Notes " +
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
                    boolean phongtyc = rs.getString("RoomID") != null && rs.getString("RoomID").equals("R002");
                    char loaiBH = (mabh != null && !mabh.isEmpty()) ? 'y' : 'x';
                    String ghichu = rs.getString("Notes");

                    Admission admission = null;
                    if (rs.getString("RoomID") != null) {
                        admission = new Admission(); // Giả định lớp Admission
                        // Cần thêm logic để gán các thuộc tính cho admission
                    }

                    BENHNHANBAOHIEMYTE benhnhan = new BENHNHANBAOHIEMYTE(
                            loaiBH, mabn, ghichu, null, hoten, ngaynhapvien, mabh, phongtyc, admission
                    );
                    Danhsach.put(mabn, benhnhan);
                    System.out.println("Đã thêm bệnh nhân: " + mabn);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
        }
    }

    public void NhapGUI(BENHNHAN benhnhan) {
        Danhsach.put(benhnhan.getMABN(), benhnhan);
        try {
            GhiFile(); // Lưu vào cơ sở dữ liệu ngay sau khi thêm
        } catch (SQLException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
        }
    }

    public void SuaGUI(BENHNHAN benhnhan) {
        Danhsach.replace(benhnhan.getMABN(), benhnhan);
        try {
            // Cập nhật cơ sở dữ liệu
            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                Class.forName(DRIVER);
                String patientQuery = "UPDATE Patients SET FullName = ?, CreatedAt = ? WHERE PatientID = ?";
                String insuranceQuery = "UPDATE Insurance SET PolicyNumber = ? WHERE PatientID = ?";
                String admissionQuery = "UPDATE Admissions SET RoomID = ?, AdmissionDate = ?, Notes = ? WHERE PatientID = ?";

                if (benhnhan instanceof BENHNHANBAOHIEMYTE) {
                    BENHNHANBAOHIEMYTE patient = (BENHNHANBAOHIEMYTE) benhnhan;

                    // Cập nhật Patients
                    try (PreparedStatement psPatient = conn.prepareStatement(patientQuery)) {
                        psPatient.setString(1, patient.getHoten());
                        psPatient.setDate(2, patient.getNgaynhapvien() != null ? java.sql.Date.valueOf(patient.getNgaynhapvien()) : null);
                        psPatient.setString(3, patient.getMABN());
                        psPatient.executeUpdate();
                    }

                    // Cập nhật Insurance
                    try (PreparedStatement psInsurance = conn.prepareStatement(insuranceQuery)) {
                        psInsurance.setString(1, patient.getMSBH());
                        psInsurance.setString(2, patient.getMABN());
                        psInsurance.executeUpdate();
                    }

                    // Cập nhật Admissions
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
        } catch (SQLException | ClassNotFoundException e) {
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
            Class.forName(DRIVER);
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
        } catch (SQLException | ClassNotFoundException e) {
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
                Class.forName(DRIVER);
                String query = "INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Reason, Status) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, "AP" + System.currentTimeMillis());
                    ps.setString(2, mabn);
                    ps.setString(3, "U001"); // Giả định DoctorID
                    ps.setTimestamp(4, java.sql.Timestamp.valueOf(NgayLichHen.replace("YYYY", "yyyy")));
                    ps.setString(5, "Lịch hẹn định kỳ");
                    ps.setString(6, "Cho xac nhan");
                    ps.executeUpdate();
                }

                // Cập nhật lịch hẹn trong đối tượng
                LocalDate lichHen = java.sql.Timestamp.valueOf(NgayLichHen.replace("YYYY", "yyyy"))
                        .toLocalDateTime().toLocalDate();
                benhnhan.setLichHen(lichHen);
                System.out.println("Lịch hẹn đã được đặt cho bệnh nhân " + benhnhan.getHoten() + " vào " + NgayLichHen);
            } catch (SQLException | ClassNotFoundException e) {
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
            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                Class.forName(DRIVER);
                String query = "INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, Diagnosis, Treatment, RecordDate, Notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, "MR" + System.currentTimeMillis());
                    ps.setString(2, mabn);
                    ps.setString(3, "U001"); // Giả định DoctorID
                    ps.setString(4, "N/A"); // Giả định chẩn đoán
                    ps.setString(5, "N/A"); // Giả định điều trị
                    ps.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
                    ps.setString(7, GhiChumoi);
                    ps.executeUpdate();
                }
            } catch (SQLException | ClassNotFoundException e) {
                ExceptionUtils.handleGeneralException(parentPanel, e);
                throw new Exception("Lỗi khi ghi chú: " + e.getMessage());
            }

            benhnhan.setGhiChu(GhiChumoi);
            if (benhnhan.getGhiChu() != null && !benhnhan.getGhiChu().isEmpty()) {
                System.out.println("Ghi chú của Bác Sĩ: " + benhnhan.getGhiChu());
            } else {
                System.out.println("Không có ghi chú của Bác Sĩ");
            }
        } else {
            throw new Exception("Bệnh nhân không tồn tại");
        }
    }

    public void TongLichHen() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName(DRIVER);
            String query = "SELECT COUNT(*) FROM Appointments WHERE Status = 'Da xac nhan' OR Status = 'Cho xac nhan'";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    int tong = rs.getInt(1);
                    System.out.println("Tổng lịch hẹn đã được đặt: " + tong);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            ExceptionUtils.handleGeneralException(parentPanel, e);
        }
    }
}