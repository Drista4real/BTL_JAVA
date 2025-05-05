package model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.database.databaseConnection;

public class User {
    private String id;  // Thay đổi từ int thành String
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
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

    public User(String username, String password, String fullName, String email, String phone, Role role,
                String dateOfBirth, String gender, String address, String cccd, boolean hasInsurance) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
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

    // Constructor cũ để tương thích
    public User(String username, String password, String fullName, String email, String phone, Role role) {
        this(username, password, fullName, email, phone, role, "", "", "", "", false);
    }
    
    // Constructor for authentication with role
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User() {

    }

    // Getters and setters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
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
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(Role role) { this.role = role; }
    public void setNote(String note) { this.note = note; }
    public void setIllnessInfo(String illnessInfo) { this.illnessInfo = illnessInfo; }
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
            this.phone = rs.getString("PhoneNumber");
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
}