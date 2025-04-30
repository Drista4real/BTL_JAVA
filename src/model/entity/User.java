package model.entity;

public class User {
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
    }

    // Constructor cũ để tương thích
    public User(String username, String password, String fullName, String email, String phone, Role role) {
        this(username, password, fullName, email, phone, role, "", "", "", "", false);
    }

    // Getters and setters
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
} 