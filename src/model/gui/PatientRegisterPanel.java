/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.gui;

import model.entity.Role;
import model.entity.User;
import model.entity.UserService;
import model.utils.ExceptionUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author son
 */
public class PatientRegisterPanel extends JPanel {
    private UserService userService;

    public PatientRegisterPanel(MainFrame mainFrame) {
        userService = new UserService();
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
        JTextField fullNameField = new JTextField(18);

        // Ngày sinh: dùng JComboBox cho ngày, tháng, năm
        JComboBox<String> dayBox = new JComboBox<>();
        JComboBox<String> monthBox = new JComboBox<>();
        JComboBox<String> yearBox = new JComboBox<>();

        for (int i = 1; i <= 31; i++) dayBox.addItem(String.format("%02d", i));
        for (int i = 1; i <= 12; i++) monthBox.addItem(String.format("%02d", i));
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int i = currentYear; i >= 1900; i--) yearBox.addItem(String.valueOf(i));

        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Nam", "Nu"});
        JTextField addressField = new JTextField(18);

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Tên đăng nhập:"), gbc); gbc.gridx = 1; formPanel.add(usernameField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Họ tên:"), gbc); gbc.gridx = 1; formPanel.add(fullNameField, gbc); y++;
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
                        fullNameField.getText().trim(),
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
    private void registerNewPatient(String username, String fullName, String password, String confirmPassword,
                                    String email, String phone, String dob, String gender, String address, MainFrame mainFrame) {

        // Kiểm tra trường thông tin trống
        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
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

        // Kiểm tra username đã tồn tại
        if (userService.findUserByUsername(username) != null) {
            ExceptionUtils.handleValidationException(this, "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác!");
            return;
        }

        // Đăng ký bệnh nhân qua UserService
        try {
            User newPatient = userService.addPatient(username, password, fullName, email, phone, "");
            newPatient.setDateOfBirth(dob);
            newPatient.setGender(gender);
            newPatient.setAddress(address);
            userService.updateUser(newPatient); // Cập nhật thông tin bổ sung

            // Hiển thị thông báo thành công
            int choice = JOptionPane.showConfirmDialog(this,
                    "Đăng ký thành công! Bạn có muốn đăng nhập ngay không?",
                    "Đăng ký thành công",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                // Đăng nhập trực tiếp
                User user = userService.authenticate(username, password);
                if (user != null && mainFrame != null) {
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
        } catch (Exception ex) {
            ExceptionUtils.handleGeneralException(this, new Exception("Lỗi khi đăng ký: " + ex.getMessage()));
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