package view.login;

import model.Employee;
import Utils.JDBCUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginPanel extends JPanel {
    private final JButton loginButton;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private String acc,pass;
    private Employee employee;
    public LoginPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 400));

        // Form đăng nhập
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setPreferredSize(new Dimension(400, 400));
        leftPanel.setBackground(Color.WHITE);

        // Logo
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/iconhome.png"));
        Image scaledImage = icon.getImage().getScaledInstance(150, 121, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(scaledImage));
        logo.setBounds(125, 10, 150, 121);
        leftPanel.add(logo);

        // Tiêu đề Đăng nhập
        JLabel loginTitle = new JLabel("ĐĂNG NHẬP", JLabel.CENTER);
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        loginTitle.setForeground(new Color(0, 102, 204));
        loginTitle.setBounds(100, 110, 200, 30);
        leftPanel.add(loginTitle);

        // Icon user
        ImageIcon userIconRaw = new ImageIcon(getClass().getResource("/img/username.png"));
        Image scaledUser = userIconRaw.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JLabel userIcon = new JLabel(new ImageIcon(scaledUser));
        userIcon.setBounds(74, 143, 24, 24);
        leftPanel.add(userIcon);

         usernameField = new JTextField();
        usernameField.setBounds(100, 140, 220, 32);
        usernameField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLUE));
        leftPanel.add(usernameField);

        // Icon password
        ImageIcon passIconRaw = new ImageIcon(getClass().getResource("/img/padlock.png"));
        Image scaledPass = passIconRaw.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JLabel passIcon = new JLabel(new ImageIcon(scaledPass));
        passIcon.setBounds(74, 185, 24, 24);
        leftPanel.add(passIcon);

        // Password field
         passwordField = new JPasswordField();
        passwordField.setBounds(100, 180, 200, 32);
        passwordField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLUE));
        leftPanel.add(passwordField);

        // Eye icon
        ImageIcon eyeIconRaw = new ImageIcon(getClass().getResource("/img/eye.png"));
        Image scaledEye = eyeIconRaw.getImage().getScaledInstance(23, 23, Image.SCALE_SMOOTH);
        JLabel eyeIcon = new JLabel(new ImageIcon(scaledEye));
        eyeIcon.setBounds(300, 185, 23, 23);
        eyeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Đổi chuột thành bàn tay
        leftPanel.add(eyeIcon);

        // Xử lý click để hiện/ẩn mật khẩu
        eyeIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            private boolean showingPassword = false; // trạng thái ẩn/hiện

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showingPassword = !showingPassword;
                if (showingPassword) {
                    passwordField.setEchoChar((char) 0); // Hiện mật khẩu
                } else {
                    passwordField.setEchoChar('•');
                }
            }
        });

        // Nút Đăng nhập
        loginButton = new JButton("Đăng nhập");
        loginButton.setBounds(125, 240, 150, 40);
        loginButton.setBackground(new Color(0, 206, 209));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        loginButton.setOpaque(true);
        loginButton.setContentAreaFilled(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);

        leftPanel.add(loginButton);

        // Panel phải: ảnh bác sĩ
        JPanel rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon doctorImg = new ImageIcon(getClass().getResource("/img/doctorLogin.png"));
                Image img = doctorImg.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        rightPanel.setPreferredSize(new Dimension(400, 400));

        // Add panels vào chính LoginPanel
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Employee getEmployee() {
//        usernameField passwordField
        acc=usernameField.getText();
        pass=passwordField.getText();
        try {
            Connection con= JDBCUtil.getConnection();
            Statement st=con.createStatement();
            String sql = "SELECT * FROM Employee WHERE username = '" + acc + "' AND password = '" + pass + "';";
            ResultSet rs=st.executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("name").equals("") || rs.getString("name")==null) {
                    employee=null;
                    break;
                }
                employee=new Employee(rs.getInt("id"),rs.getString("name"),rs.getDate("birthDate"),rs.getString("address"),rs.getInt("gender"),rs.getString("phoneNumber"),rs.getString("idCard"),rs.getString("username"),rs.getString("password"),rs.getDouble("salary"),rs.getInt("experienceYears"),rs.getString("role"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employee;
    }

    public void setUser(Employee employee) {
        this.employee = employee;
    }
    public void resetUser(){
        usernameField.setText("");
        passwordField.setText("");
        acc="";
        pass="";
        employee=null;
    }
}
