/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.gui;

import model.entity.Role;
import model.entity.User;
import model.utils.ExceptionUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author son
 */
public class PatientRegisterPanel extends JPanel {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Pha2k5@";
    
    public PatientRegisterPanel(MainFrame mainFrame) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        // Khung màu xanh
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2, true),
            "Đăng ký tài khoản bệnh nhân",
            TitledBorder.CENTER, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 18),
            new Color(41, 128, 185)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField usernameField = new JTextField(18);
        JPasswordField passwordField = new JPasswordField(18);
        JPasswordField confirmPasswordField = new JPasswordField(18);
        JTextField emailField = new JTextField(18);
        JTextField phoneField = new JTextField(18);

        // Ngày sinh: dùng JSpinner kiểu Date
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dobSpinner = new JSpinner(dateModel);
        dobSpinner.setEditor(new JSpinner.DateEditor(dobSpinner, "yyyy-MM-dd"));

        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Nam", "Nu"});
        JTextField addressField = new JTextField(18);

        JComboBox<String> dayBox = new JComboBox<>();
        JComboBox<String> monthBox = new JComboBox<>();
        JComboBox<String> yearBox = new JComboBox<>();

        for (int i = 1; i <= 31; i++) dayBox.addItem(String.format("%02d", i));
        for (int i = 1; i <= 12; i++) monthBox.addItem(String.format("%02d", i));
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int i = currentYear; i >= 1900; i--) yearBox.addItem(String.valueOf(i));

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Tên đăng nhập:"), gbc); gbc.gridx = 1; formPanel.add(usernameField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Mật khẩu:"), gbc); gbc.gridx = 1; formPanel.add(passwordField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Xác nhận mật khẩu:"), gbc); gbc.gridx = 1; formPanel.add(confirmPasswordField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Email:"), gbc); gbc.gridx = 1; formPanel.add(emailField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Số điện thoại:"), gbc); gbc.gridx = 1; formPanel.add(phoneField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Ngày sinh:"), gbc);
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dobPanel.setBackground(Color.WHITE);
        dobPanel.add(dayBox); dobPanel.add(new JLabel(" / "));
        dobPanel.add(monthBox); dobPanel.add(new JLabel(" / "));
        dobPanel.add(yearBox);
        gbc.gridx = 1; formPanel.add(dobPanel, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Giới tính:"), gbc); gbc.gridx = 1; formPanel.add(genderBox, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Nơi cư trú:"), gbc); gbc.gridx = 1; formPanel.add(addressField, gbc); y++;

        // Nút Đăng ký và Cancel
        JButton btnRegister = new JButton("Đăng ký");
        JButton btnCancel = new JButton("Cancel");
        styleButton(btnRegister);
        styleButton(btnCancel);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnRegister);
        btnPanel.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = y+1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnPanel, gbc);

        // Đưa formPanel vào giữa
        add(formPanel);

        // Xử lý nút Đăng ký
        btnRegister.addActionListener(e -> {
            try {
                registerNewPatient(
                    usernameField.getText().trim(),
                    new String(passwordField.getPassword()),
                    new String(confirmPasswordField.getPassword()),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    yearBox.getSelectedItem() + "-" + monthBox.getSelectedItem() + "-" + dayBox.getSelectedItem(),
                    (String) genderBox.getSelectedItem(),
                    addressField.getText().trim(),
                    mainFrame
                );
            } catch (Exception ex) {
                ExceptionUtils.handleGeneralException(this, ex);
            }
        });

        // Xử lý nút Cancel
        btnCancel.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.setContentPane(new LoginPanel(mainFrame, Role.PATIENT));
                mainFrame.revalidate();
            }
        });
    }

    /**
     * Xử lý đăng ký người dùng mới
     */
    private void registerNewPatient(String username, String password, String confirmPassword, 
            String email, String phone, String dob, String gender, String address, MainFrame mainFrame) {
        
        // Kiểm tra trường thông tin trống
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || 
            email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            ExceptionUtils.handleValidationException(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        
        // Kiểm tra mật khẩu khớp
        if (!password.equals(confirmPassword)) {
            ExceptionUtils.handleValidationException(this, "Mật khẩu xác nhận không khớp!");
            return;
        }
        
        // Kiểm tra định dạng email
        if (!ExceptionUtils.validateEmail(this, email)) {
            return;
        }
        
        // Kiểm tra định dạng số điện thoại
        if (!ExceptionUtils.validatePhone(this, phone)) {
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Kiểm tra username đã tồn tại chưa
            if (isUsernameExists(conn, username)) {
                ExceptionUtils.handleValidationException(this, "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác!");
                return;
            }
            
            conn.setAutoCommit(false);  // Bắt đầu transaction
            
            try {
                // 1. Thêm vào UserAccounts
                String userId = "U" + System.currentTimeMillis();
                addUserAccount(conn, userId, username, password, email, phone);
                
                // 2. Thêm vào Patients
                String patientId = "P" + System.currentTimeMillis();
                addPatientRecord(conn, patientId, userId, username, dob, gender, phone, address);
                
                // Xác nhận transaction
                conn.commit();
                
                // Hiển thị thông báo thành công
                int choice = JOptionPane.showConfirmDialog(this, 
                    "Đăng ký thành công! Bạn có muốn đăng nhập ngay không?", 
                    "Đăng ký thành công", 
                    JOptionPane.YES_NO_OPTION);
                    
                if (choice == JOptionPane.YES_OPTION) {
                    // Tạo user object và đăng nhập trực tiếp
                    User user = new User(username, password, username, email, phone, Role.PATIENT,
                                       dob, gender, address, "", false);
                    if (mainFrame != null) {
                        mainFrame.dispose();
                        new PatientMainFrame(user).setVisible(true);
                    }
                } else {
                    // Trở về màn hình đăng nhập
                    if (mainFrame != null) {
                        mainFrame.setContentPane(new LoginPanel(mainFrame, Role.PATIENT));
                        mainFrame.revalidate();
                    }
                }
            } catch (SQLException ex) {
                // Nếu có lỗi, rollback transaction
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            ExceptionUtils.handleGeneralException(this, new Exception("Lỗi kết nối hoặc thao tác với cơ sở dữ liệu: " + ex.getMessage()));
        }
    }
    
    /**
     * Kiểm tra username đã tồn tại trong DB chưa
     */
    private boolean isUsernameExists(Connection conn, String username) throws SQLException {
        try (PreparedStatement checkStmt = conn.prepareStatement(
            "SELECT UserID FROM UserAccounts WHERE UserName = ?")) {
            checkStmt.setString(1, username);
            try (ResultSet checkRs = checkStmt.executeQuery()) {
                return checkRs.next();
            }
        }
    }
    
    /**
     * Thêm bản ghi vào bảng UserAccounts
     */
    private void addUserAccount(Connection conn, String userId, String username, String password, 
            String email, String phone) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, Password) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, userId);
            ps.setString(2, username);
            ps.setString(3, username);
            ps.setString(4, "Benh nhan");
            ps.setString(5, email);
            ps.setString(6, phone);
            ps.setString(7, password);
            ps.executeUpdate();
        }
    }
    
    /**
     * Thêm bản ghi vào bảng Patients
     */
    private void addPatientRecord(Connection conn, String patientId, String userId, String fullName, 
            String dob, String gender, String phone, String address) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())")) {
            ps.setString(1, patientId);
            ps.setString(2, userId);
            ps.setString(3, fullName);
            ps.setString(4, dob);
            ps.setString(5, gender);
            ps.setString(6, phone);
            ps.setString(7, address);
            ps.executeUpdate();
        }
    }

    private void styleButton(JButton button) {
        Color btnColor = new Color(41, 128, 185);
        button.setBackground(btnColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(btnColor.darker(), 1, true));
        button.setPreferredSize(new Dimension(120, 36));
    }
}