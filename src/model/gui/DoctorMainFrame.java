package model.gui;

import javax.swing.*;
import java.awt.*;
import model.entity.User;
import java.awt.image.BufferedImage;
import javax.swing.table.DefaultTableModel;
import model.entity.DataManager;
import model.entity.Appointment;
import model.entity.Role;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class DoctorMainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton currentButton;
    private User currentUser;
    private JLabel userNameLabel;
    private JLabel avatarLabel;
    private JPanel navPanel;
    
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream input = DoctorMainFrame.class.getClassLoader().getResourceAsStream("main/database.properties")) {
            if (input == null) {
                throw new IOException("Không tìm thấy tệp database.properties");
            }
            props.load(input);
            DB_URL = props.getProperty("url");
            DB_USER = props.getProperty("username");
            DB_PASSWORD = props.getProperty("password");
            Class.forName(props.getProperty("driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Lỗi khi tải cấu hình cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public DoctorMainFrame(User user) {
        // Giữ phần còn lại của constructor không thay đổi
    }

    // Giữ các phương thức khác không thay đổi
}

// Cập nhật các class con để sử dụng DB_URL, DB_USER, DB_PASSWORD từ DoctorMainFrame
class DoctorDashboardPanel extends JPanel {
    // Sửa để sử dụng DoctorMainFrame.DB_URL, DoctorMainFrame.DB_USER, DoctorMainFrame.DB_PASSWORD thay cho giá trị cứng
}

class DoctorPatientListPanel extends JPanel {
    // Sửa để sử dụng DoctorMainFrame.DB_URL, DoctorMainFrame.DB_USER, DoctorMainFrame.DB_PASSWORD thay cho giá trị cứng
}

class DoctorAppointmentListPanel extends JPanel {
    // Sửa để sử dụng DoctorMainFrame.DB_URL, DoctorMainFrame.DB_USER, DoctorMainFrame.DB_PASSWORD thay cho giá trị cứng
}