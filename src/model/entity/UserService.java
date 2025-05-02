package model.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=UTF-8&useUnicode=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "050705";

    public UserService() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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

        User doctor = new User(userId, username, password, fullName, email, phone, Role.DOCTOR);
        doctor.setNote(department);

        addUser(doctor);
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

        User patient = new User(userId, username, password, fullName, email, phone, Role.PATIENT);
        patient.setIllnessInfo(illnessInfo);

        addUser(patient);
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
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Xác thực người dùng khi đăng nhập
     */
    public User authenticate(String username, String password) {
        System.out.println("Đang xác thực người dùng: " + username);
        User user = getUserByUsername(username);
        if (user != null) {
            System.out.println("Tìm thấy người dùng: " + user.getUsername() + ", vai trò: " + user.getRole());
            if (user.getPassword().equals(password)) {
                System.out.println("Xác thực thành công!");
                return user;
            } else {
                System.out.println("Sai mật khẩu!");
            }
        } else {
            System.out.println("Không tìm thấy người dùng với tên đăng nhập: " + username);
        }
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
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getRole() == role) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get a user by ID
     */
    public User getUserById(String userId) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "SELECT ua.*, p.IllnessInfo FROM UserAccounts ua " +
                    "LEFT JOIN Patients p ON ua.UserID = p.UserID WHERE ua.UserID = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Xác định vai trò
                        Role role;
                        String roleStr = rs.getString("Role");
                        if (roleStr == null || roleStr.trim().isEmpty()) {
                            role = Role.DOCTOR; // Vai trò mặc định
                        } else {
                            roleStr = roleStr.trim();
                            if (roleStr.equalsIgnoreCase("benh nhan") || roleStr.equalsIgnoreCase("Benh nhan")) {
                                role = Role.PATIENT;
                            } else if (roleStr.equalsIgnoreCase("bac si") || roleStr.equalsIgnoreCase("Bac si")) {
                                role = Role.DOCTOR;
                            } else {
                                System.err.println("Vai trò không hợp lệ: " + roleStr);
                                role = Role.DOCTOR; // Mặc định nếu vai trò không nhận diện được
                            }
                        }
                        User user = new User(
                                rs.getString("UserID"),
                                rs.getString("UserName"),
                                rs.getString("Password"),
                                rs.getString("FullName"),
                                rs.getString("Email"),
                                rs.getString("PhoneNumber"),
                                role
                        );
                        user.setNote(rs.getString("Note") != null ? rs.getString("Note") : "");
                        user.setIllnessInfo(rs.getString("IllnessInfo") != null ? rs.getString("IllnessInfo") : "");
                        return user;
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
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "SELECT ua.*, p.IllnessInfo FROM UserAccounts ua " +
                    "LEFT JOIN Patients p ON ua.UserID = p.UserID";
            try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Xác định vai trò
                    Role role;
                    String roleStr = rs.getString("Role");
                    if (roleStr == null || roleStr.trim().isEmpty()) {
                        role = Role.DOCTOR; // Vai trò mặc định
                    } else {
                        roleStr = roleStr.trim();
                        if (roleStr.equalsIgnoreCase("benh nhan") || roleStr.equalsIgnoreCase("Benh nhan")) {
                            role = Role.PATIENT;
                        } else if (roleStr.equalsIgnoreCase("bac si") || roleStr.equalsIgnoreCase("Bac si")) {
                            role = Role.DOCTOR;
                        } else {
                            System.err.println("Vai trò không hợp lệ: " + roleStr);
                            role = Role.DOCTOR; // Mặc định nếu vai trò không nhận diện được
                        }
                    }

                    User user = new User(
                            rs.getString("UserID"),
                            rs.getString("UserName"),
                            rs.getString("Password"),
                            rs.getString("FullName"),
                            rs.getString("Email"),
                            rs.getString("PhoneNumber"),
                            role
                    );
                    user.setNote(rs.getString("Note") != null ? rs.getString("Note") : "");
                    user.setIllnessInfo(rs.getString("IllnessInfo") != null ? rs.getString("IllnessInfo") : "");
                    users.add(user);
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
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            // Update UserAccounts
            String query1 = "UPDATE UserAccounts SET UserName = ?, FullName = ?, Email = ?, PhoneNumber = ?, Role = ?, Note = ? WHERE UserID = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(query1)) {
                ps1.setString(1, user.getUsername());
                ps1.setString(2, user.getFullName());
                ps1.setString(3, user.getEmail());
                ps1.setString(4, user.getPhoneNumber());
                ps1.setString(5, user.getRole() == Role.PATIENT ? "Benh nhan" : "Bac si");
                ps1.setString(6, user.getNote());
                ps1.setString(7, user.getUserId());
                ps1.executeUpdate();
            }
            // Update Patients (if user is a patient)
            if (user.getRole() == Role.PATIENT) {
                String query2 = "UPDATE Patients SET IllnessInfo = ? WHERE UserID = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(query2)) {
                    ps2.setString(1, user.getIllnessInfo());
                    ps2.setString(2, user.getUserId());
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
    public boolean addUser(User user) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            // Insert into UserAccounts
            String query1 = "INSERT INTO UserAccounts (UserID, UserName, Password, FullName, Email, PhoneNumber, Role, Note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps1 = conn.prepareStatement(query1)) {
                ps1.setString(1, user.getUserId());
                ps1.setString(2, user.getUsername());
                ps1.setString(3, user.getPassword());
                ps1.setString(4, user.getFullName());
                ps1.setString(5, user.getEmail());
                ps1.setString(6, user.getPhoneNumber());
                ps1.setString(7, user.getRole() == Role.PATIENT ? "Benh nhan" : "Bac si");
                ps1.setString(8, user.getNote());
                ps1.executeUpdate();
            }
            // Insert into Patients (if user is a patient)
            if (user.getRole() == Role.PATIENT) {
                String query2 = "INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, CreatedAt, IllnessInfo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(query2)) {
                    ps2.setString(1, user.getUserId()); // Assume PatientID = UserID
                    ps2.setString(2, user.getUserId());
                    ps2.setString(3, user.getFullName());
                    ps2.setString(4, "1990-01-01"); // Default DOB, adjust as needed
                    ps2.setString(5, "Nam"); // Default gender, adjust as needed
                    ps2.setString(6, user.getPhoneNumber());
                    ps2.setString(7, ""); // Default address
                    ps2.setString(8, java.time.LocalDate.now().toString());
                    ps2.setString(9, user.getIllnessInfo());
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
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
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
}