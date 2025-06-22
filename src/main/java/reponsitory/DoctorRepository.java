package reponsitory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Utils.JDBCUtil;
import model.Doctor;
import model.Employee;
import model.Examination;

public class DoctorRepository {
//    private List<Doctor> doctors = new ArrayList<>();

    public DoctorRepository() {
//        loadDoctorsFromDatabase();
    }
    public  static List<Doctor>  getAllDoctors() throws SQLException, IOException, ClassNotFoundException {
        List<Doctor> doctors = new ArrayList<>();
        String query = "SELECT e.id, e.name, e.birthDate, e.address, e.gender, e.phoneNumber, " +
                       "e.idCard, e.username, e.password, e.salary, e.experienceYears, e.role, d.specialty " +
                       "FROM Employee e INNER JOIN Doctor d ON e.id = d.id";

        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                doctors.add(new Doctor(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDate("birthDate"),
                    rs.getString("address"),
                    rs.getInt("gender"),
                    rs.getString("phoneNumber"),
                    rs.getString("idCard"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getDouble("salary"),
                    rs.getInt("experienceYears"),
                    rs.getString("specialty")
                ));
            }
        }
        return doctors;
    }
//    public List<Doctor> getDoctors() {
//        return doctors;
//    }
//
//    public void setDoctors(List<Doctor> doctors) {
//        this.doctors = doctors;
//    }
//
//    private void loadDoctorsFromDatabase() {
//        try (Connection conn = JDBCUtil.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery("SELECT e.id, e.name, e.birthDate, e.address, e.gender, e.phoneNumber, e.idCard, e.username, e.password, e.salary,e.experienceYears, e.role, d.specialty FROM Employee e INNER JOIN Doctor d ON e.id = d.id")) {
//            while (rs.next()) {
//                doctors.add(new Doctor(
//                    rs.getInt("id"),
//                    rs.getString("name"),
//                    rs.getDate("birthDate"),
//                    rs.getString("address"),
//                    rs.getInt("gender"),
//                    rs.getString("phoneNumber"),
//                    rs.getString("idCard"),
//                    rs.getString("username"),
//                    rs.getString("password"),
//                    rs.getDouble("salary"),
//                    rs.getInt("experienceYears"),
//                    rs.getString("specialty")
//                ));
//            }
//        } catch (SQLException | IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//    
 // Phương thức trả về danh sách tên bác sĩ
    public List<String> getDoctorNames() {
        List<String> doctorNames = new ArrayList<>();
        String query = "SELECT e.name FROM Employee e INNER JOIN Doctor d ON e.id = d.id";
        
        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                doctorNames.add(rs.getString("name"));
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return doctorNames;
    }
    
 // 4. Thêm phiếu khám mới (nếu cần)
    public boolean addExamination(Examination examination) throws SQLException, IOException, ClassNotFoundException {
        String query = "INSERT INTO Examination (doctor_id, patient_id, status) VALUES (?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, examination.getDoctorId());
            ps.setInt(2, examination.getPatientId());
            ps.setString(3, examination.getStatus());
            return ps.executeUpdate() > 0;
        }
    }
    
}
