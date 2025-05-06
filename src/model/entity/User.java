package model.entity;

import java.util.Objects;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.database.databaseConnection;

public class User {
    private String userId;
    private String id;  // Thay đổi từ int thành String
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role; // DOCTOR, PATIENT
    private String note; // Ghi chú của bác sĩ
    private String illnessInfo; // Thông tin bệnh tình
    private String dateOfBirth; // Ngày sinh
    private String gender;     // Giới tính
    private String address;    // Nơi cư trú
    private String cccd;       // Số CCCD
    private boolean hasInsurance; // Có giấy BHYT không

    // Thêm các thuộc tính mới
    private String patientId;  // Mã bệnh nhân
    private String insuranceId; // Mã BHYT
    private String insuranceExpDate; // Ngày hết hạn BHYT

    public User(String userName, String storedPassword, String fullName, String email, String phoneNumber, Role role) {
        this(userName, storedPassword, fullName, email, phoneNumber, role, "", "", "", "", false);
        this.userId = ""; // Mặc định, cần setUserId() sau khi tạo
    }

    public User(String userId, String username, String password, String fullName, String email, String phoneNumber, Role role) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        this.userId = userId;
        this.username = username != null ? username : "";
        this.password = password != null ? password : "";
        this.fullName = fullName != null ? fullName : "";
        this.email = email != null ? email : "";
        this.phoneNumber = phoneNumber != null ? phoneNumber : "";
        this.role = role != null ? role : Role.PATIENT;
    }

    public User(String username, String password, String fullName, String email, String phone, Role role,
                String dateOfBirth, String gender, String address, String cccd, boolean hasInsurance) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.note = "";
        this.illnessInfo = "";
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.cccd = cccd;
        this.hasInsurance = hasInsurance;
        this.patientId = null;
        this.insuranceId = null;
        this.insuranceExpDate = null;
    }

    // Constructor for authentication with role
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User() {

    }

    // Getters
    public String getUserId() { return userId; }
    // Getters and setters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Role getRole() { return role; }
    // Setters
    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        this.userId = userId;
    }

    public String getNote() { return note; }
    public String getIllnessInfo() { return illnessInfo; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getCccd() { return cccd; }
    public boolean isHasInsurance() { return hasInsurance; }

    // Getters và setters cho các thuộc tính mới
    public String getPatientId() { return patientId; }
    public String getInsuranceId() { return insuranceId; }
    public String getInsuranceExpDate() { return insuranceExpDate; }

    public void setId(String id) { this.id = id; }
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName : "";
    }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public void setHasInsurance(boolean hasInsurance) { this.hasInsurance = hasInsurance; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public void setInsuranceId(String insuranceId) { this.insuranceId = insuranceId; }
    public void setInsuranceExpDate(String insuranceExpDate) { this.insuranceExpDate = insuranceExpDate; }

public boolean authenticate(String username, String password, Role role) {
    try {
        Connection conn = databaseConnection.getConnection();
        String query;

        // Sử dụng dbValue trong enum Role thay vì giá trị cứng
        if (role == Role.DOCTOR) {
            query = "SELECT * FROM UserAccounts WHERE UserName = ? AND Password = ? AND Role = ?";
        } else {
            query = "SELECT * FROM UserAccounts WHERE UserName = ? AND Password = ? AND Role = ?";
        }

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.setString(3, role.getDbValue()); // Sử dụng giá trị từ enum

        System.out.println("SQL Query: " + query);
        System.out.println("Parameters: username=" + username + ", role=" + role.getDbValue());

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            // User found, populate user fields
            this.id = rs.getString("UserID");  // Không cần chuyển đổi
            this.username = rs.getString("UserName");
            this.password = rs.getString("Password");
            this.fullName = rs.getString("FullName");
            this.email = rs.getString("Email");
            this.phoneNumber = rs.getString("PhoneNumber");
            this.role = Role.fromDbValue(rs.getString("Role"));

            rs.close();
            stmt.close();
            conn.close();
            return true;
        }

        rs.close();
        stmt.close();
        conn.close();
        return false;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
    public void setEmail(String email) {
        this.email = email != null ? email : "";
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber != null ? phoneNumber : "";
    }

    public void setRole(Role role) {
        this.role = role != null ? role : Role.PATIENT;
    }

    public void setIllnessInfo(String illnessInfo) {
        this.illnessInfo = illnessInfo != null ? illnessInfo : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role=" + role +
                ", note='" + note + '\'' +
                ", illnessInfo='" + illnessInfo + '\'' +
                '}';
    }
}