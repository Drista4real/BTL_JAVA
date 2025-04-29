package com.utc2.gui;

import javax.swing.*;
import java.awt.*;
import com.utc2.entity.User;

public class PrescriptionPanel extends JPanel {
    public PrescriptionPanel(User user) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Đơn thuốc"));
        setBackground(Color.WHITE);

        // Dữ liệu demo, có thể thay bằng danh sách đơn thuốc thực tế
        String[] columns = {"Tên thuốc", "Liều lượng", "Cách dùng"};
        Object[][] data = {
            {"Paracetamol", "500mg", "2 viên/ngày"},
            {"Amoxicillin", "250mg", "3 viên/ngày"}
        };
        JTable table = new JTable(data, columns);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
} 