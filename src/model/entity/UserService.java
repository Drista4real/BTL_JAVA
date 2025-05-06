package model.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class UserService {
    private static String DB_DRIVER;
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    static {
        // Load configuration from database.properties
        Properties props = new Properties();
        try (InputStream input = UserService.class.getClassLoader().getResourceAsStream("main/database.properties")) {
            if (input == null) {
                throw new IOException("Cannot find database.properties");
            }
            props.load(input);
            DB_DRIVER = props.getProperty("driver");
            DB_URL = props.getProperty("url");
            if (!DB_URL.contains("characterEncoding")) {
                DB_URL += DB_URL.contains("?") ? "&characterEncoding=UTF-8" : "?characterEncoding=UTF-8";
            }
            DB_USER = props.getProperty("username");
            DB_PASSWORD = props.getProperty("password");

            // Load JDBC driver
            Class.forName(DB_DRIVER);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading database configuration: " + e.getMessage());
        }
    }

    /**
     * Thêm bác sĩ mới vào hệ thống
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @param fullName Họ tên đầy đủ
     * @param email Email
     * @param phone Số điện thoại
     * @param department Khoa/bộ phận
     * @return Đối tượng User đã được thêm
     */
    public User addDoctor(String username, String password, String fullName,
                          String email, String phone, String department) {
        // Tạo ID tự động cho bác sĩ
        String userId = "BS" + generateRandomId();

        User doctor = new User(userId, username, password, Role.DOCTOR, fullName, phone);
        doctor.setEmail(email);
        addUser(doctor, department, null);
        return doctor;
    }

    /**
     * Thêm bệnh nhân mới vào hệ thống
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @param fullName Họ tên đầy đủ
     * @param email Email
     * @param phone Số điện thoại
     * @param illnessInfo Thông tin bệnh lý
     * @return Đối tượng User đã được thêm
     */
    public User addPatient(String username, String password, String fullName,
                           String email, String phone, String illnessInfo) {
        // Tạo ID tự động cho bệnh nhân
        String userId = "BN" + generateRandomId();

        User patient = new User(userId, username, password, Role.PATIENT, fullName, phone);
        patient.setEmail(email);
        addUser(patient, null, illnessInfo);
        return patient;
    }

    /**
     * Tạo ID ngẫu nhiên gồm 4 ký tự số
     */
    private String generateRandomId() {
        return String.format("%04d", (int)(Math.random() * 9000) + 1000);
    }

    /**
     * Tìm người dùng theo tên đăng nhập
     */
    public User getUserByUsername(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT ua.*, p.IllnessInfo FROM UserAccounts ua " +
                    "LEFT JOIN Patients p ON ua.UserID = p.UserID WHERE ua.UserName = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return createUserFromResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Xác thực người dùng khi đăng nhập
     */
    public User authenticate(String username, String password) {
        System.out.println("Đang xác thực người dùng: " + username);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT ua.*, p.IllnessInfo FROM UserAccounts ua " +
                    "LEFT JOIN Patients p ON ua.UserID = p.UserID WHERE ua.UserName = ? AND ua.Password = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, password); // Note: In production, use hashed passwords
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User user = createUserFromResultSet(rs);
                        System.out.println("Tìm thấy người dùng: " + user.getUsername() + ", vai trò: " + user.getRole());

                        // Fetch additional patient info
                        if (user.getRole() == Role.PATIENT) {
                            String patientQuery = "SELECT PatientID, DateOfBirth, Gender, Address, Height, Weight, BloodType FROM Patients WHERE UserID = ?";
                            try (PreparedStatement patientStmt = conn.prepareStatement(patientQuery)) {
                                patientStmt.setString(1, user.getUserId());
                                ResultSet patientRs = patientStmt.executeQuery();
                                if (patientRs.next()) {
                                    user.setPatientId(patientRs.getString("PatientID"));
                                    user.setDateOfBirth(patientRs.getString("DateOfBirth"));
                                    user.setGender(patientRs.getString("Gender"));
                                    user.setAddress(patientRs.getString("Address"));
                                    user.setHeight(patientRs.getDouble("Height"));
                                    user.setWeight(patientRs.getDouble("Weight"));
                                    user.setBloodType(patientRs.getString("BloodType"));
                                }
                            }

                            // Fetch insurance info
                            String insuranceQuery = "SELECT PolicyNumber, ExpirationDate FROM Insurance WHERE PatientID = ?";
                            try (PreparedStatement insuranceStmt = conn.prepareStatement(insuranceQuery)) {
                                insuranceStmt.setString(1, user.getPatientId());
                                ResultSet insuranceRs = insuranceStmt.executeQuery();
                                if (insuranceRs.next()) {
                                    user.setHasInsurance(true);
                                    user.setInsuranceId(insuranceRs.getString("PolicyNumber"));
                                    user.setInsuranceExpDate(insuranceRs.getString("ExpirationDate"));
                                } else {
                                    user.setHasInsurance(false);
                                }
                            }
                        }

                        System.out.println("Xác thực thành công!");
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi xác thực: " + e.getMessage());
        }
        System.out.println("Không tìm thấy người dùng hoặc mật khẩu sai: " + username);
        return null;
    }

    /**
     * Bí danh của getUserByUsername
     */
    public User findUserByUsername(String username) {
        return getUserByUsername(username);
    }

    /**
     * Đếm số lượng người dùng theo vai trò
     */
    public int countUsersByRole(Role role) {
        int count = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(*) FROM UserAccounts WHERE Role = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, role.getDisplayName());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Get a user by ID
     */
    public User getUserById(String userId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT ua.*, p.IllnessInfo FROM UserAccounts ua " +
                    "LEFT JOIN Patients p ON ua.UserID = p.UserID WHERE ua.UserID = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return createUserFromResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT ua.*, p.IllnessInfo FROM UserAccounts ua " +
                    "LEFT JOIN Patients p ON ua.UserID = p.UserID";
            try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(createUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Update a user's information
     */
    public boolean updateUser(User user) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Update UserAccounts
            String query1 = "UPDATE UserAccounts SET UserName = ?, FullName = ?, Email = ?, PhoneNumber = ?, Role = ?, Note = ? WHERE UserID = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(query1)) {
                ps1.setString(1, user.getUsername());
                ps1.setString(2, user.getFullName());
                ps1.setString(3, user.getEmail());
                ps1.setString(4, user.getPhoneNumber());
                ps1.setString(5, user.getRole().getDisplayName());
                ps1.setString(6, user.getNote() != null ? user.getNote() : "");
                ps1.setString(7, user.getUserId());
                ps1.executeUpdate();
            }
            // Update Patients (if user is a patient)
            if (user.getRole() == Role.PATIENT) {
                String query2 = "UPDATE Patients SET FullName = ?, PhoneNumber = ?, IllnessInfo = ? WHERE UserID = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(query2)) {
                    ps2.setString(1, user.getFullName());
                    ps2.setString(2, user.getPhoneNumber());
                    ps2.setString(3, user.getIllnessInfo() != null ? user.getIllnessInfo() : "");
                    ps2.setString(4, user.getUserId());
                    ps2.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Add a new user
     */
    public boolean addUser(User user, String note, String illnessInfo) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Insert into UserAccounts
            String query1 = "INSERT INTO UserAccounts (UserID, UserName, Password, FullName, Email, PhoneNumber, Role, Note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps1 = conn.prepareStatement(query1)) {
                ps1.setString(1, user.getUserId());
                ps1.setString(2, user.getUsername());
                ps1.setString(3, user.getPassword());
                ps1.setString(4, user.getFullName());
                ps1.setString(5, user.getEmail());
                ps1.setString(6, user.getPhoneNumber());
                ps1.setString(7, user.getRole().getDisplayName());
                ps1.setString(8, note != null ? note : "");
                ps1.executeUpdate();
            }
            // Insert into Patients (if user is a patient)
            if (user.getRole() == Role.PATIENT) {
                String query2 = "INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, CreatedAt, IllnessInfo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(query2)) {
                    ps2.setString(1, user.getUserId()); // Assume PatientID = UserID
                    ps2.setString(2, user.getUserId());
                    ps2.setString(3, user.getFullName());
                    ps2.setString(4, "1990-01-01"); // Default DOB
                    ps2.setString(5, "Nam"); // Default gender
                    ps2.setString(6, user.getPhoneNumber());
                    ps2.setString(7, ""); // Default address
                    ps2.setString(8, java.time.LocalDate.now().toString());
                    ps2.setString(9, illnessInfo != null ? illnessInfo : "");
                    ps2.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a user by ID
     */
    public boolean deleteUser(String userId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Delete from Patients (if exists)
            String query1 = "DELETE FROM Patients WHERE UserID = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(query1)) {
                ps1.setString(1, userId);
                ps1.executeUpdate();
            }
            // Delete from UserAccounts
            String query2 = "DELETE FROM UserAccounts WHERE UserID = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(query2)) {
                ps2.setString(1, userId);
                return ps2.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to create User from ResultSet
     */
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        String roleStr = rs.getString("Role");
        Role role = Role.fromDbValue(roleStr);
        if (role == null) {
            System.err.println("Invalid role: " + roleStr + ", defaulting to DOCTOR");
            role = Role.DOCTOR; // Default if role is unrecognized
        }

        User user = new User(
                rs.getString("UserID"),
                rs.getString("UserName"),
                rs.getString("Password"),
                role,
                rs.getString("FullName"),
                rs.getString("PhoneNumber")
        );
        user.setEmail(rs.getString("Email"));
        user.setNote(rs.getString("Note"));
        if (role == Role.PATIENT) {
            user.setIllnessInfo(rs.getString("IllnessInfo"));
        }
        return user;
    }
}