package classes;

import javax.swing.*;
import java.awt.*;
import model.entity.User;

public class PersonalInfoPanel extends JPanel {
    public PersonalInfoPanel(User user) {
        setLayout(new GridLayout(5, 2, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Thông tin cá nhân"));
        setBackground(Color.WHITE);

        add(new JLabel("Họ tên:"));
        add(new JLabel(user.getFullName()));
        add(new JLabel("Email:"));
        add(new JLabel(user.getEmail()));
        add(new JLabel("Số điện thoại:"));
        add(new JLabel(user.getPhone()));
        // Thêm các trường khác nếu cần
    }
} 