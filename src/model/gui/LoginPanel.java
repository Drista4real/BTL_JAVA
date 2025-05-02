package model.gui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.entity.User;
import model.entity.Role;
import model.utils.ExceptionUtils;
import java.util.Random;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton forgotPasswordButton;
    private JCheckBox showPasswordCheckBox;
    private MainFrame mainFrame;
    private Image backgroundImage;
    private Role currentRole;

    // Thông tin kết nối cơ sở dữ liệu từ database.properties
    private static String DB_DRIVER;
    private static String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement?allowPublicKeyRetrieval=true&useSSL=false";
    private static String DB_USER;
    private static String DB_PASSWORD;

    static {
        // Tải cấu hình từ database.properties
        Properties props = new Properties();
        try (InputStream input = LoginPanel.class.getClassLoader().getResourceAsStream("main/database.properties")) {
            if (input == null) {
                throw new IOException("Không tìm thấy tệp database.properties");
            }
            props.load(input);
            DB_DRIVER = props.getProperty("driver");

            // Thêm tham số characterEncoding=UTF-8 vào URL kết nối
            DB_URL = props.getProperty("url");
            if (!DB_URL.contains("characterEncoding")) {
                if (DB_URL.contains("?")) {
                    DB_URL += "&characterEncoding=UTF-8";
                } else {
                    DB_URL += "?characterEncoding=UTF-8";
                }
            }

            DB_USER = props.getProperty("username");
            DB_PASSWORD = props.getProperty("password");

            // Tải driver JDBC
            Class.forName(DB_DRIVER);

            System.out.println("URL kết nối sau khi sửa: " + DB_URL);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Lỗi khi tải cấu hình cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public LoginPanel(MainFrame mainFrame, Role role) {
        this.mainFrame = mainFrame;
        this.currentRole = role;
        loadBackgroundImage();
        initComponents();
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = new ImageIcon("resource/images/ảnh_nền_login.jpg").getImage();
        } catch (Exception e) {
            System.out.println("Không thể tải hình nền: " + e.getMessage());
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
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
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

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Thêm code kiểm tra kết nối
            System.out.println("Kết nối cơ sở dữ liệu thành công!");

            String sql = "SELECT UserID, UserName, FullName, Email, PhoneNumber, Role, Password FROM UserAccounts WHERE UserName = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            System.out.println("Đang tìm kiếm người dùng: " + username);
            ResultSet rs = stmt.executeQuery();


            if (rs.next()) {
                String storedPassword = rs.getString("Password");
                if (password.equals(storedPassword)) {
                    Role role = Role.valueOf(mapRoleToEnum(rs.getString("Role")));
                    User user = new User(
                            rs.getString("UserName"),
                            storedPassword,
                            rs.getString("FullName"),
                            rs.getString("Email"),
                            rs.getString("PhoneNumber"),
                            role
                    );

                    if (role == Role.DOCTOR) {
                        SwingUtilities.invokeLater(() -> {
                            JFrame doctorFrame = new DoctorMainFrame(user);
                            doctorFrame.setVisible(true);
                            Window w = SwingUtilities.getWindowAncestor(this);
                            if (w != null) w.dispose();
                        });
                    } else if (role == Role.PATIENT) {
                        SwingUtilities.invokeLater(() -> {
                            JFrame patientFrame = new PatientMainFrame(user);
                            patientFrame.setVisible(true);
                            Window w = SwingUtilities.getWindowAncestor(this);
                            if (w != null) w.dispose();
                        });
                    }

                    // Xóa thông tin đăng nhập
                    usernameField.setText("");
                    passwordField.setText("");
                    showPasswordCheckBox.setSelected(false);
                    passwordField.setEchoChar('•');
                } else {
                    JOptionPane.showMessageDialog(this, "Mật khẩu không đúng!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập không tồn tại!");
            }
        } catch (SQLException e) {
            ExceptionUtils.handleValidationException(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
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

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT UserID, Email, PhoneNumber FROM UserAccounts WHERE UserName = ? AND Email = ? AND PhoneNumber = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, phone);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String newPassword = generateTemporaryPassword();
                    sql = "UPDATE UserAccounts SET Password = ? WHERE UserName = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, newPassword);
                    stmt.setString(2, username);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Mật khẩu mới của bạn là: " + newPassword);
                } else {
                    ExceptionUtils.handleValidationException(this, "Thông tin không chính xác!");
                }
            } catch (SQLException e) {
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

    private String mapRoleToEnum(String dbRole) {
        System.out.println("Vai trò từ DB: " + dbRole);

        // Kiểm tra nếu chuỗi chứa 'Bác' hoặc 'bác' ở bất kỳ định dạng nào
        if (dbRole != null && (dbRole.contains("ác") || dbRole.toLowerCase().contains("bac") ||
                dbRole.contains("B") || dbRole.contains("s"))) {
            System.out.println("Xác định là bác sĩ");
            return "DOCTOR";
        }
        // Kiểm tra nếu chuỗi chứa 'Bệnh' hoặc 'bệnh' ở bất kỳ định dạng nào
        else if (dbRole != null && (dbRole.contains("ệnh") || dbRole.toLowerCase().contains("benh") ||
                dbRole.contains("nhân") || dbRole.toLowerCase().contains("nhan"))) {
            System.out.println("Xác định là bệnh nhân");
            return "PATIENT";
        } else {
            // Trong trường hợp không thể xác định, kiểm tra mã byte
            System.out.println("Không xác định được vai trò, hiển thị mã byte:");
            for (byte b : dbRole.getBytes()) {
                System.out.print(b + " ");
            }
            System.out.println();

            // Thử dựa vào ký tự đầu tiên để phân biệt
            char firstChar = dbRole.charAt(0);
            if (firstChar == 'B' || firstChar == 'b') {
                System.out.println("Giả định là bác sĩ dựa vào ký tự đầu");
                return "DOCTOR";
            } else {
                throw new IllegalArgumentException("Vai trò không hợp lệ: " + dbRole);
            }
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