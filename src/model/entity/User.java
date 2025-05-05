package model.entity;

import java.util.Objects;

public class User {
    private String userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;
    private String note; // Added for UserService compatibility
    private String illnessInfo; // Added for UserService compatibility

    public User(String userName, String storedPassword, String fullName, String email, String phoneNumber, Role role) {
        this.userId = ""; // Mặc định, cần setUserId() sau khi tạo
        this.username = userName != null ? userName : "";
        this.password = storedPassword != null ? storedPassword : "";
        this.fullName = fullName != null ? fullName : "";
        this.email = email != null ? email : "";
        this.phoneNumber = phoneNumber != null ? phoneNumber : "";
        this.role = role != null ? role : Role.PATIENT;
        this.note = "";
        this.illnessInfo = "";
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
        this.note = "";
        this.illnessInfo = "";
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Role getRole() { return role; }
    public String getNote() { return note; } // Added
    public String getIllnessInfo() { return illnessInfo; } // Added

    // Setters
    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        this.userId = userId;
    }

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

    public void setEmail(String email) {
        this.email = email != null ? email : "";
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber != null ? phoneNumber : "";
    }

    public void setRole(Role role) {
        this.role = role != null ? role : Role.PATIENT;
    }

    public void setNote(String note) { // Added
        this.note = note != null ? note : "";
    }

    public void setIllnessInfo(String illnessInfo) { // Added
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
        return "User{" +
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