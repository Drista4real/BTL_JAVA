package com.utc2.entity;

public class User {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private Role role; // DOCTOR, PATIENT
    private String note; // Ghi chú của bác sĩ
    private String illnessInfo; // Thông tin bệnh tình

    public User(String username, String password, String fullName, String email, String phone, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.note = "";
        this.illnessInfo = "";
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

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(Role role) { this.role = role; }
    public void setNote(String note) { this.note = note; }
    public void setIllnessInfo(String illnessInfo) { this.illnessInfo = illnessInfo; }
} 