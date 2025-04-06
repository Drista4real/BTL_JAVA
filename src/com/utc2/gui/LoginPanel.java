package com.utc2.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import com.utc2.entity.User;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import com.utc2.utils.ExceptionUtils;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton forgotPasswordButton;
    private JCheckBox rememberPasswordCheckBox;
    private Map<String, User> users;
    private MainFrame mainFrame;
    private static final String REMEMBER_FILE = "data/remember_password.txt";

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.users = new HashMap<>();
        initComponents();
        // Thêm tài khoản admin mặc định
        users.put("admin", new User("admin", "admin", "Administrator", "admin@example.com", "0123456789", "admin"));
        loadRememberedPassword();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Đăng nhập hệ thống");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(new JLabel("Tên đăng nhập:"), gbc);

        usernameField = new JTextField(20);
        usernameField.addActionListener(e -> passwordField.requestFocus());
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Mật khẩu:"), gbc);

        passwordField = new JPasswordField(20);
        passwordField.addActionListener(e -> login());
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Remember password checkbox
        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 10, 10);  // Giảm khoảng cách phía trên
        gbc.anchor = GridBagConstraints.WEST;  // Căn lề trái
        rememberPasswordCheckBox = new JCheckBox("Ghi nhớ mật khẩu");
        add(rememberPasswordCheckBox, gbc);

        // Reset insets và anchor
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginButton = new JButton("Đăng nhập");
        registerButton = new JButton("Đăng ký");
        forgotPasswordButton = new JButton("Quên mật khẩu?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(Color.BLUE);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> showRegisterDialog());
        forgotPasswordButton.addActionListener(e -> showForgotPasswordDialog());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Forgot password link
        gbc.gridy = 5;
        add(forgotPasswordButton, gbc);

        // Set focus ban đầu vào ô username
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            if (rememberPasswordCheckBox.isSelected()) {
                saveRememberedPassword(username, password);
            } else {
                clearRememberedPassword();
            }
            mainFrame.setCurrentUser(user);
            mainFrame.showMainContent();
            // Xóa thông tin đăng nhập
            usernameField.setText("");
            passwordField.setText("");
            rememberPasswordCheckBox.setSelected(false);
        } else {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!");
        }
    }

    private void showForgotPasswordDialog() {
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Tên đăng nhập:"));
        panel.add(usernameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Quên mật khẩu",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();

            if (username.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                ExceptionUtils.handleValidationException(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            if (!ExceptionUtils.validateEmail(this, email)) {
                return;
            }

            if (!ExceptionUtils.validatePhone(this, phone)) {
                return;
            }

            User user = users.get(username);
            if (user != null && user.getEmail().equals(email) && user.getPhone().equals(phone)) {
                String newPassword = generateTemporaryPassword();
                user.setPassword(newPassword);
                JOptionPane.showMessageDialog(this, "Mật khẩu mới của bạn là: " + newPassword);
            } else {
                ExceptionUtils.handleValidationException(this, "Thông tin không chính xác!");
            }
        }
    }

    private String generateTemporaryPassword() {
        // Tạo mật khẩu ngẫu nhiên 8 ký tự
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void saveRememberedPassword(String username, String password) {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(REMEMBER_FILE))) {
                writer.println(username);
                writer.println(password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRememberedPassword() {
        File file = new File(REMEMBER_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(REMEMBER_FILE))) {
                String username = reader.readLine();
                String password = reader.readLine();
                if (username != null && password != null) {
                    usernameField.setText(username);
                    passwordField.setText(password);
                    rememberPasswordCheckBox.setSelected(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearRememberedPassword() {
        File file = new File(REMEMBER_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    private void showRegisterDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField fullNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Tên đăng nhập:"));
        panel.add(usernameField);
        panel.add(new JLabel("Mật khẩu:"));
        panel.add(passwordField);
        panel.add(new JLabel("Họ tên:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Đăng ký tài khoản",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                ExceptionUtils.handleValidationException(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            if (users.containsKey(username)) {
                ExceptionUtils.handleValidationException(this, "Tên đăng nhập đã tồn tại!");
                return;
            }

            if (!ExceptionUtils.validateEmail(this, email)) {
                return;
            }

            if (!ExceptionUtils.validatePhone(this, phone)) {
                return;
            }

            users.put(username, new User(username, password, fullName, email, phone, "user"));
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
        }
    }
} 