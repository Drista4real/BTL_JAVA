package com.utc2.gui;

import javax.swing.*;
import java.awt.*;
import com.utc2.entity.User;

public class PaymentPanel extends JPanel {
    public PaymentPanel(User user) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Thanh toán"));
        setBackground(Color.WHITE);

        // Dữ liệu demo, có thể thay bằng danh sách hóa đơn thực tế
        String[] columns = {"Mã hóa đơn", "Nội dung", "Số tiền", "Trạng thái"};
        Object[][] data = {
            {"HD001", "Khám bệnh", "200.000đ", "Chưa thanh toán"},
            {"HD002", "Thuốc", "150.000đ", "Đã thanh toán"}
        };
        JTable table = new JTable(data, columns);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton payBtn = new JButton("Thanh toán hóa đơn đã chọn");
        payBtn.setBackground(new Color(41, 128, 185));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng thanh toán!"));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(payBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }
} 