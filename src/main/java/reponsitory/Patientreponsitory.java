package reponsitory;

import Utils.JDBCUtil;
import model.Patient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Patientreponsitory {

    public static List<Object[]> getAllPatients() {
        List<Object[]> list = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection()) {
            String query =
                    "SELECT p.*, e.status AS examStatus " +
                            "FROM Patient p " +
                            "LEFT JOIN Examination e ON p.id = e.patient_id";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            int stt = 1;
            while (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phoneNumber");
                int genderInt = rs.getInt("gender");
                String gender = (genderInt == 1) ? "Nam" : "Nữ";

                // Tính tuổi từ birthDate
                int age = 0;
                Date birthDate = rs.getDate("birthDate");
                if (birthDate != null) {
                    age = java.time.Period.between(
                            birthDate.toLocalDate(),
                            java.time.LocalDate.now()
                    ).getYears();
                }

                String status = rs.getString("examStatus");
                if (status == null) {
                    status = "Chưa khám";  // mặc định nếu chưa có cuộc khám nào
                }

                list.add(new Object[]{
                        stt++,
                        name,
                        phone,
                        gender,
                        age,
                        status,
                        null
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<Object[]> getAllBillPatients() {
        List<Object[]> list = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection()) {
            String query = "SELECT \n" +
                    "    pr.id AS MaHoaDon,\n" +
                    "    p.name AS TenBenhNhan,\n" +
                    "    TIMESTAMPDIFF(YEAR, p.birthDate, CURDATE()) AS Tuoi,\n" +
                    "    p.phoneNumber AS SoDienThoai,\n" +
                    "    CASE \n" +
                    "        WHEN p.gender = 1 THEN 'Nam'\n" +
                    "        WHEN p.gender = 0 THEN 'Nữ'\n" +
                    "        ELSE 'Khác'\n" +
                    "    END AS GioiTinh,\n" +
                    "    pr.paymentStatus AS TrangThaiThanhToan,\n" +
                    "    (\n" +
                    "        (SELECT COALESCE(SUM(s.price * psd.quantity), 0) \n" +
                    "         FROM PrescriptionServiceDetail psd \n" +
                    "         JOIN Service s ON psd.service_id = s.id \n" +
                    "         WHERE psd.prescription_id = pr.id) \n" +
                    "      + \n" +
                    "        (SELECT COALESCE(SUM(d.price * pd.quantity), 0) \n" +
                    "         FROM PrescriptionDrugDetail pd \n" +
                    "         JOIN Drug d ON pd.drug_id = d.id \n" +
                    "         WHERE pd.prescription_id = pr.id)\n" +
                    "    ) AS TongTien,\n" +
                    "    YEAR(p.birthDate) AS NamSinh\n" +
                    "FROM \n" +
                    "    Patient p\n" +
                    "LEFT JOIN \n" +
                    "    Prescription pr ON p.id = pr.patient_id\n" +
                    "GROUP BY \n" +
                    "    pr.id, p.name, p.birthDate, p.phoneNumber, p.gender, pr.paymentStatus\n" +
                    "ORDER BY \n" +
                    "    pr.id ASC;";


            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            DecimalFormat formatter = new DecimalFormat("#,###");

            while (rs.next()) {
                String paymentStatus = rs.getString("TrangThaiThanhToan");
                if (paymentStatus != null &&
                        (paymentStatus.equals("Chưa thanh toán") || paymentStatus.equals("Đã thanh toán"))) {

                    int invoiceId = rs.getInt("MaHoaDon");
                    String name = rs.getString("TenBenhNhan");
                    int age = rs.getInt("Tuoi");
                    String phone = rs.getString("SoDienThoai");
                    String genderText = rs.getString("GioiTinh");
                    double totalAmountRaw = rs.getDouble("TongTien");
                    String totalAmountFormatted = formatter.format(totalAmountRaw);
                    int birthYear = rs.getInt("NamSinh");

                    list.add(new Object[]{
                            invoiceId,
                            name,
                            phone,
                            genderText,
                            age,
                            totalAmountFormatted + " VND",
                            paymentStatus,
                            null
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<Patient> getListPatients() {
        List<Patient> list = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection()) {
            String query = "SELECT * FROM Patient";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            int stt = 1;
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Date birthDate = rs.getDate("birthDate");
                String address = rs.getString("address");
                int genderInt = rs.getInt("gender");
                String gender = (genderInt == 1) ? "Nam" : "Nữ";
                String phone = rs.getString("phoneNumber");
                String idCard = rs.getString("idCard");

                // Tính tuổi từ birthDate
                int age = 0;
                if (birthDate != null) {
                    age = java.time.Period.between(
                            birthDate.toLocalDate(),
                            java.time.LocalDate.now()
                    ).getYears();
                }
                Patient a=new Patient(id,name,birthDate,address,genderInt,phone,idCard);
                list.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public static List<Object[]> getPatientsChar(String charFind) {
        List<Object[]> list = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection()) {
            String query =
                    "SELECT p.*, e.status AS examStatus " +
                            "FROM Patient p " +
                            "LEFT JOIN Examination e ON p.id = e.patient_id " +
                            "WHERE p.phoneNumber LIKE ? OR p.name LIKE ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, charFind + "%");
            stmt.setString(2, charFind + "%");

            ResultSet rs = stmt.executeQuery();

            int stt = 1;
            while (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phoneNumber");
                int genderInt = rs.getInt("gender");
                String gender = (genderInt == 1) ? "Nam" : "Nữ";

                // Tính tuổi từ birthDate
                int age = 0;
                Date birthDate = rs.getDate("birthDate");
                if (birthDate != null) {
                    age = java.time.Period.between(
                            birthDate.toLocalDate(),
                            java.time.LocalDate.now()
                    ).getYears();
                }

                String status = rs.getString("examStatus");
                if (status == null) {
                    status = "Chưa khám";
                }

                list.add(new Object[]{
                        stt++,
                        name,
                        phone,
                        gender,
                        age,
                        status,
                        null
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<Object[]> getPatientsCharofDentist(String charFind, String id_doctor) {
        List<Object[]> list = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection()) {
            String query = "SELECT " +
                    "    p.name, p.phoneNumber, p.gender, p.birthDate, e.status " +
                    "FROM Examination e " +
                    "JOIN Patient p ON e.patient_id = p.id " +
                    "WHERE e.doctor_id = ? " +
                    "AND (" +
                    "      LOWER(p.name) LIKE ? " +
                    "   OR LOWER(p.phoneNumber) LIKE ?" +
                    ")";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(id_doctor));
            String keyword = "%" + charFind.toLowerCase() + "%";
            stmt.setString(2, keyword);
            stmt.setString(3, keyword);

            ResultSet rs = stmt.executeQuery();
            int stt = 1;

            while (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phoneNumber");
                int genderInt = rs.getInt("gender");
                String gender = (genderInt == 1) ? "Nam" : "Nữ";

                int age = 0;
                Date birthDate = rs.getDate("birthDate");
                if (birthDate != null) {
                    age = java.time.Period.between(
                            birthDate.toLocalDate(),
                            java.time.LocalDate.now()
                    ).getYears();
                }

                String status = rs.getString("status");
                list.add(new Object[]{
                        stt++,
                        name,
                        phone,
                        gender,
                        age,
                        status,
                        null // Cột "Khám"
                });
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static int getIdPatient(String phoneNumber) {
        try {
            String sql = "SELECT id FROM Patient WHERE phoneNumber LIKE ?";
            Connection conn = JDBCUtil.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, phoneNumber);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return -1;
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Object[]> getPatientOfDentist(String id_dentist) {
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = JDBCUtil.getConnection()) {
            String query = "SELECT " +
                    "    p.name AS TenBenhNhan, " +
                    "    p.phoneNumber AS SoDienThoai, " +
                    "    CASE " +
                    "        WHEN p.gender = 1 THEN 'Nam' " +
                    "        WHEN p.gender = 0 THEN 'Nữ' " +
                    "        ELSE 'Khác' " +
                    "    END AS GioiTinh, " +
                    "    TIMESTAMPDIFF(YEAR, p.birthDate, CURDATE()) AS Tuoi, " +
                    "    e.status AS TrangThaiKham " +
                    "FROM Examination e " +
                    "JOIN Patient p ON e.patient_id = p.id " +
                    "WHERE e.doctor_id = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(id_dentist));
            ResultSet rs = stmt.executeQuery();

            int stt = 1;
            while (rs.next()) {
                String name = rs.getString("TenBenhNhan");
                String phone = rs.getString("SoDienThoai");
                String gender = rs.getString("GioiTinh");
                int age = rs.getInt("Tuoi");
                String status = rs.getString("TrangThaiKham");

                list.add(new Object[]{stt++, name, phone, gender, age, status});
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public static void main(String[] args) {
        Patientreponsitory patientDAO = new Patientreponsitory();
        System.out.println(patientDAO.getIdPatient("0987654321"));
    }
    //code của Lam
    public Patientreponsitory() {
        // TODO Auto-generated constructor stub
    }


    public List<Patient> findByPhonePrefix(String phonePrefix) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM Patient WHERE phoneNumber LIKE ?";
        try (PreparedStatement ps = JDBCUtil.getConnection().prepareStatement(query)) {
            ps.setString(1, phonePrefix + "%"); // Chỉ tìm những số bắt đầu bằng prefix
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
        }
        return patients;
    }


    // 2. Lấy toàn bộ danh sách bệnh nhân
    public List<Patient> getAllPatientsL() throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM Patient";
        try (Statement stmt = JDBCUtil.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
        }
        return patients;
    }

    // 3. Thêm mới bệnh nhân
    public boolean addPatient(Patient patient) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        String query = "INSERT INTO Patient (name, birthDate, address, gender, phoneNumber, idCard) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = JDBCUtil.getConnection().prepareStatement(query)) {
            ps.setString(1, patient.getName());
            ps.setDate(2, patient.getBirthDate());
            ps.setString(3, patient.getAddress());
            ps.setInt(4, patient.getGender());
            ps.setString(5, patient.getPhoneNumber());
            ps.setString(6, patient.getIdCard());
            return ps.executeUpdate() > 0;
        }
    }

    // 4. Cập nhật thông tin bệnh nhân
    public static boolean updatePatient(Patient patient) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        String query = "UPDATE Patient SET name = ?, birthDate = ?, address = ?, gender = ?, phoneNumber = ?, idCard = ? WHERE id = ?";
        try (PreparedStatement ps = JDBCUtil.getConnection().prepareStatement(query)) {
            ps.setString(1, patient.getName());
            ps.setDate(2, patient.getBirthDate());
            ps.setString(3, patient.getAddress());
            ps.setInt(4, patient.getGender());
            ps.setString(5, patient.getPhoneNumber());
            ps.setString(6, patient.getIdCard());
            ps.setInt(7, patient.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // 5. Xoá bệnh nhân theo ID
    public boolean deletePatient(int id) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        String query = "DELETE FROM Patient WHERE id = ?";
        try (PreparedStatement ps = JDBCUtil.getConnection().prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Hàm hỗ trợ để convert từ ResultSet sang Patient
    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        Date birthDate = rs.getDate("birthDate");
        String address = rs.getString("address");
        int gender = rs.getInt("gender");
        String phoneNumber = rs.getString("phoneNumber");
        String idCard = rs.getString("idCard");

        return new Patient(id, name, birthDate, address, gender, phoneNumber, idCard);
    }

    // 6. Lấy bệnh nhân mới nhất
    public Patient getLatestPatient() throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        String query = "SELECT * FROM Patient ORDER BY id DESC LIMIT 1";  // Sắp xếp theo id giảm dần
        try (PreparedStatement ps = JDBCUtil.getConnection().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return extractPatientFromResultSet(rs);
            }
        }
        return null;  // Nếu không có bệnh nhân nào
    }

    // 3.1. Thêm mới bệnh nhân và trả về ID mới
    public int addPatientAndReturnId(Patient patient) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        String query = "INSERT INTO Patient (name, birthDate, address, gender, phoneNumber, idCard) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, patient.getName());
            ps.setDate(2, patient.getBirthDate());
            ps.setString(3, patient.getAddress());
            ps.setInt(4, patient.getGender());
            ps.setString(5, patient.getPhoneNumber());
            ps.setString(6, patient.getIdCard());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Thêm bệnh nhân thất bại, không có dòng nào bị ảnh hưởng.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Trả về id mới được sinh ra
                } else {
                    throw new SQLException("Không lấy được ID bệnh nhân mới.");
                }
            }
        }
    }

    public static Patient getPatientById(int id) {
        Patient patient = null;

        try (Connection conn = JDBCUtil.getConnection()) {
            String sql = "SELECT * FROM Patient WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                patient = new Patient();
                patient.setId(rs.getInt("id"));
                patient.setName(rs.getString("name"));
                patient.setPhone(rs.getString("phoneNumber"));
                patient.setBirthDate(rs.getDate("birthDate"));
                patient.setGender(rs.getInt("gender"));
                patient.setIdCard(rs.getString("idCard"));
                patient.setAddress(rs.getString("address"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return patient;
    }

    public static boolean isPhoneExists(String phone) throws SQLException, ClassNotFoundException, FileNotFoundException, IOException {
        String sql = "SELECT 1 FROM Patient WHERE phoneNumber = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public static boolean isIdCardExists(String idCard) throws SQLException, ClassNotFoundException, FileNotFoundException, IOException {
        String sql = "SELECT 1 FROM Patient WHERE idCard = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idCard);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
}
