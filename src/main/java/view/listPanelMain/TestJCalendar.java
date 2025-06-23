package view.listPanelMain;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class TestJCalendar {
    public static void main(String[] args) {
        // Tạo cửa sổ
        JFrame frame = new JFrame("Demo JCalendar - Chọn ngày sinh");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new FlowLayout());

        // Tạo JDateChooser
        JLabel label = new JLabel("Ngày sinh:");
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd-MM-yyyy"); // Định dạng hiển thị

        // Nút để lấy ngày đã chọn
        JButton btnGetDate = new JButton("Lấy ngày");
        btnGetDate.addActionListener(e -> {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate != null) {
                JOptionPane.showMessageDialog(frame, "Bạn đã chọn: " + selectedDate.toString());
            } else {
                JOptionPane.showMessageDialog(frame, "Chưa chọn ngày.");
            }
        });

        // Thêm vào frame
        frame.add(label);
        frame.add(dateChooser);
        frame.add(btnGetDate);

        frame.setLocationRelativeTo(null); // Căn giữa màn hình
        frame.setVisible(true);
    }
}
