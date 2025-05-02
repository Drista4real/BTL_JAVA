/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.gui;

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
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String dob = yearBox.getSelectedItem() + "-" + monthBox.getSelectedItem() + "-" + dayBox.getSelectedItem();
            String gender = (String) genderBox.getSelectedItem();
            String address = addressField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() ||
                phone.isEmpty() || dob.isEmpty() || gender.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
                return;
            }

            // Lưu vào SQL
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/PatientManagement", "root", "Pha2k5@")) {
                // 1. Thêm vào UserAccounts
                String userId = "U" + System.currentTimeMillis();
                PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, Password) VALUES (?, ?, ?, ?, ?, ?, ?)");
                ps1.setString(1, userId);
                ps1.setString(2, username);
                ps1.setString(3, username); // Hoặc cho phép nhập họ tên riêng
                ps1.setString(4, "Benh nhan");
                ps1.setString(5, email);
                ps1.setString(6, phone);
                ps1.setString(7, password);
                ps1.executeUpdate();

                // 2. Thêm vào Patients
                String patientId = "P" + System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())");
                ps2.setString(1, patientId);
                ps2.setString(2, userId);
                ps2.setString(3, username); // Hoặc cho phép nhập họ tên riêng
                ps2.setString(4, dob);
                ps2.setString(5, gender);
                ps2.setString(6, phone);
                ps2.setString(7, address);
                ps2.executeUpdate();

                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng đăng nhập.");
                if (mainFrame != null) {
                    mainFrame.setContentPane(new LoginPanel(mainFrame, model.entity.Role.PATIENT));
                    mainFrame.revalidate();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi đăng ký: " + ex.getMessage());
            }
        });

        // Xử lý nút Cancel
        btnCancel.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.setContentPane(new LoginPanel(mainFrame, model.entity.Role.PATIENT));
                mainFrame.revalidate();
            }
        });
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
