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
import java.sql.DatabaseMetaData;
import java.util.Date;

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
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    System.out.println("Thực hiện đăng nhập với username: " + username);

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        System.out.println("Kết nối đến cơ sở dữ liệu thành công!");
        
        // Debug: Kiểm tra tài khoản trong bảng UserAccounts
        String checkUserSql = "SELECT UserName, Password, Role FROM UserAccounts";
        PreparedStatement checkStmt = conn.prepareStatement(checkUserSql);
        ResultSet checkRs = checkStmt.executeQuery();
        System.out.println("Danh sách tài khoản trong hệ thống:");
        while (checkRs.next()) {
            String dbUser = checkRs.getString("UserName");
            String dbPass = checkRs.getString("Password");
            String dbRole = checkRs.getString("Role");
            System.out.println("Username: " + dbUser + ", Password: " + dbPass + ", Role: " + dbRole);
        }
        
        // Truy vấn đăng nhập sửa lại để sử dụng dấu bằng chính xác cho cả username và password
        String sql = "SELECT * FROM UserAccounts WHERE UserName = ? AND Password = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);
        System.out.println("Truy vấn: " + sql);
        System.out.println("Với tham số: Username=" + username + ", Password=" + password);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String userId = rs.getString("UserID");
            String fullName = rs.getString("FullName");
            String email = rs.getString("Email");
            String phone = rs.getString("PhoneNumber");
            String dbRole = rs.getString("Role");
            
            System.out.println("Đăng nhập thành công! UserID=" + userId + ", FullName=" + fullName + ", Role=" + dbRole);
            
            // Xác định vai trò bằng cách sử dụng phương thức fromDbValue của enum Role
            Role userRole;
            try {
                userRole = Role.fromDbValue(dbRole);
                System.out.println("Vai trò người dùng: " + userRole);
            } catch (Exception e) {
                System.out.println("Lỗi xác định vai trò: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Vai trò người dùng không hợp lệ: " + dbRole, 
                                           "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Tạo đối tượng User
            User user = new User(username, password, fullName, email, phone, userRole);
            
            // Nếu là bệnh nhân, lấy thêm thông tin từ bảng Patients
            if (userRole == Role.PATIENT) {
                try {
                    System.out.println("Lấy thông tin bệnh nhân cho UserID: " + userId);
                    String patientSql = "SELECT * FROM Patients WHERE UserID = ?";
                    PreparedStatement patientStmt = conn.prepareStatement(patientSql);
                    patientStmt.setString(1, userId);
                    ResultSet patientRs = patientStmt.executeQuery();
                    
                    if (patientRs.next()) {
                        String patientId = patientRs.getString("PatientID");
                        Date dobDate = patientRs.getDate("DateOfBirth");
                        String dob = dobDate != null ? dobDate.toString() : "";
                        String gender = patientRs.getString("Gender");
                        String address = patientRs.getString("Address");
                        String illnessInfo = patientRs.getString("IllnessInfo");
                        
                        System.out.println("Tìm thấy thông tin bệnh nhân: PatientID=" + patientId);
                        
                        user.setPatientId(patientId);
                        user.setDateOfBirth(dob);
                        user.setGender(gender);
                        user.setAddress(address != null ? address : "");
                        user.setIllnessInfo(illnessInfo != null ? illnessInfo : "");
                        
                        // Kiểm tra bảo hiểm
                        String insuranceSql = "SELECT * FROM Insurance WHERE PatientID = ?";
                        PreparedStatement insuranceStmt = conn.prepareStatement(insuranceSql);
                        insuranceStmt.setString(1, patientId);
                        ResultSet insuranceRs = insuranceStmt.executeQuery();
                        
                        if (insuranceRs.next()) {
                            String policyNumber = insuranceRs.getString("PolicyNumber");
                            Date expDate = insuranceRs.getDate("ExpirationDate");
                            String expDateStr = expDate != null ? expDate.toString() : "";
                            
                            user.setHasInsurance(true);
                            user.setInsuranceId(policyNumber);
                            user.setInsuranceExpDate(expDateStr);
                            
                            System.out.println("Thông tin bảo hiểm: PolicyNumber=" + policyNumber);
                        } else {
                            user.setHasInsurance(false);
                            System.out.println("Không tìm thấy thông tin bảo hiểm");
                        }
                        
                        // Mở giao diện bệnh nhân
                        System.out.println("Mở giao diện bệnh nhân...");
                        SwingUtilities.invokeLater(() -> {
                            try {
                                PatientMainFrame patientFrame = new PatientMainFrame(user);
                                patientFrame.setVisible(true);
                                if (mainFrame != null) {
                                    mainFrame.dispose();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Lỗi khi mở giao diện bệnh nhân: " + e.getMessage(), 
                                                           "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                        return;
                    } else {
                        System.out.println("Không tìm thấy thông tin bệnh nhân cho UserID: " + userId);
                        JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin bệnh nhân trong cơ sở dữ liệu!", 
                                                   "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    System.out.println("Lỗi SQL khi truy vấn thông tin bệnh nhân: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi truy vấn thông tin bệnh nhân: " + e.getMessage(), 
                                               "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else if (userRole == Role.DOCTOR) {
                System.out.println("Đăng nhập bác sĩ thành công, mở giao diện bác sĩ...");
                mainFrame.setCurrentUser(user);
                mainFrame.showMainContent();
                return;
            }
        } else {
            // Kiểm tra xem username có tồn tại không
            String checkUsernameSql = "SELECT Password, Role FROM UserAccounts WHERE UserName = ?";
            PreparedStatement checkUsernameStmt = conn.prepareStatement(checkUsernameSql);
            checkUsernameStmt.setString(1, username);
            ResultSet checkUsernameRs = checkUsernameStmt.executeQuery();
            
            if (checkUsernameRs.next()) {
                String correctPassword = checkUsernameRs.getString("Password");
                String userRole = checkUsernameRs.getString("Role");
                System.out.println("Tìm thấy username '" + username + "' với role '" + userRole + 
                                  "' nhưng mật khẩu không đúng!");
                System.out.println("Mật khẩu trong DB: " + correctPassword);
                System.out.println("Mật khẩu nhập vào: " + password);
                JOptionPane.showMessageDialog(this, "Mật khẩu không đúng!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            } else {
                System.out.println("Không tìm thấy tài khoản với username: " + username);
                JOptionPane.showMessageDialog(this, "Tên đăng nhập không tồn tại!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        }
    } catch (SQLException e) {
        System.out.println("Lỗi SQL: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi kết nối đến cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        System.out.println("Lỗi không xác định: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        if (dbRole == null) {
            throw new IllegalArgumentException("Vai trò không được để trống!");
        }
        switch (dbRole.trim()) {
            case "Bac si":
                return "DOCTOR";
            case "Benh nhan":
                return "PATIENT";
            default:
                throw new IllegalArgumentException("Vai trò không hợp lệ: " + dbRole);
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
    
    
// Thêm phương thức sau vào lớp LoginPanel
private void checkDatabase() {
    System.out.println("--------- KIỂM TRA CƠ SỞ DỮ LIỆU ---------");
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        System.out.println("1. Kết nối thành công đến: " + DB_URL);
        
        // Kiểm tra bảng UserAccounts
        String query = "SELECT COUNT(*) AS total FROM UserAccounts";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("2. Tổng số bản ghi trong UserAccounts: " + total);
            }
        }
        
        // Kiểm tra tài khoản bệnh nhân
        query = "SELECT UserName, Password, Role FROM UserAccounts WHERE Role = 'PATIENT'";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("3. Danh sách tài khoản bệnh nhân:");
            int count = 0;
            while (rs.next()) {
                count++;
                String userName = rs.getString("UserName");
                String password = rs.getString("Password");
                String role = rs.getString("Role");
                System.out.println("   " + count + ". UserName: " + userName + 
                                   ", Password: " + password + ", Role: " + role);
            }
            if (count == 0) {
                System.out.println("   Không tìm thấy tài khoản bệnh nhân nào!");
            }
        }
        
        // Kiểm tra bảng trong cơ sở dữ liệu
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet tables = meta.getTables(null, null, "%", new String[] {"TABLE"});
        System.out.println("4. Danh sách các bảng trong cơ sở dữ liệu:");
        int tableCount = 0;
        while (tables.next()) {
            tableCount++;
            System.out.println("   " + tableCount + ". " + tables.getString("TABLE_NAME"));
        }
        if (tableCount == 0) {
            System.out.println("   Không tìm thấy bảng nào trong cơ sở dữ liệu!");
        }
        
    } catch (SQLException e) {
        System.out.println("LỖI KẾT NỐI CƠ SỞ DỮ LIỆU: " + e.getMessage());
        e.printStackTrace();
    }
    System.out.println("----------------------------------------");
}
}