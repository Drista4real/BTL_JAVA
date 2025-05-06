package model.gui;

import javax.swing.*;
import java.awt.*;
import model.entity.User;
import model.entity.Role;
import model.entity.UserService;
import model.utils.ExceptionUtils;

import java.sql.*;
import java.util.Random;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton forgotPasswordButton;
    private JCheckBox showPasswordCheckBox;
    private MainFrame mainFrame;
    private Image backgroundImage;
    private Role currentRole;
    private UserService userService;

    public LoginPanel(MainFrame mainFrame, Role role) {
        this.mainFrame = mainFrame;
        this.currentRole = role;
        this.userService = new UserService();
        loadBackgroundImage();
        initComponents();
    }

    private void loadBackgroundImage() {
        try {
            java.net.URL imageUrl = getClass().getClassLoader().getResource("main/background_login.jpg");
            if (imageUrl != null) {
                backgroundImage = new ImageIcon(imageUrl).getImage();
            } else {
                System.err.println("Background image not found at: main/background_login.jpg");
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.setColor(new Color(255, 255, 255, 0));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel loginFormPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(255, 255, 255, 180));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        loginFormPanel.setOpaque(false);
        loginFormPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200, 150)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Hệ thống quản lý bệnh nhân");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 87, 146));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginFormPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setForeground(new Color(60, 60, 60));
        loginFormPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.addActionListener(e -> passwordField.requestFocus());
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginFormPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setForeground(new Color(60, 60, 60));
        loginFormPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.addActionListener(e -> login());
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginFormPanel.add(passwordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        showPasswordCheckBox = new JCheckBox("Hiển thị mật khẩu");
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setForeground(new Color(60, 60, 60));
        showPasswordCheckBox.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '•');
        });
        loginFormPanel.add(showPasswordCheckBox, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        loginButton = new JButton("Đăng nhập");
        styleButton(loginButton);
        forgotPasswordButton = new JButton("Quên mật khẩu?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(new Color(0, 87, 146));
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addActionListener(e -> login());
        forgotPasswordButton.addActionListener(e -> showForgotPasswordDialog());

        buttonPanel.add(loginButton);

        if (currentRole == Role.PATIENT) {
            JButton btnRegister = new JButton("Đăng ký");
            styleButton(btnRegister);
            btnRegister.addActionListener(e -> {
                if (mainFrame != null) {
                    mainFrame.setContentPane(new PatientRegisterPanel(mainFrame));
                    mainFrame.revalidate();
                }
            });
            buttonPanel.add(btnRegister);
        }

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        loginFormPanel.add(buttonPanel, gbc);

        gbc.gridy = 5;
        loginFormPanel.add(forgotPasswordButton, gbc);

        add(loginFormPanel);

        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 87, 146));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 35));
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userService.authenticate(username, password);

        if (user != null && user.getRole() == currentRole) {
            System.out.println("Đăng nhập với vai trò " + currentRole.getDisplayName() + " thành công");
            mainFrame.handleUserLogin(user);
        } else if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Tài khoản này không có quyền đăng nhập với vai trò " + currentRole.getDisplayName(),
                    "Không có quyền truy cập",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không đúng!",
                    "Đăng nhập thất bại",
                    JOptionPane.ERROR_MESSAGE);
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
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

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

            try (Connection conn = DriverManager.getConnection(
                    userService.getClass().getDeclaredField("DB_URL").get(null).toString(),
                    userService.getClass().getDeclaredField("DB_USER").get(null).toString(),
                    userService.getClass().getDeclaredField("DB_PASSWORD").get(null).toString())) {
                String sql = "SELECT UserID, Email, PhoneNumber FROM UserAccounts WHERE UserName = ? AND Email = ? AND PhoneNumber = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, email);
                    stmt.setString(3, phone);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String newPassword = generateTemporaryPassword();
                        sql = "UPDATE UserAccounts SET Password = ? WHERE UserName = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(sql)) {
                            updateStmt.setString(1, newPassword);
                            updateStmt.setString(2, username);
                            updateStmt.executeUpdate();
                            JOptionPane.showMessageDialog(this, "Mật khẩu mới của bạn là: " + newPassword);
                        }
                    } else {
                        ExceptionUtils.handleValidationException(this, "Thông tin không chính xác!");
                    }
                }
            } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
                ExceptionUtils.handleValidationException(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            }
        }
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}