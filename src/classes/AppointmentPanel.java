package classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import model.entity.User;

public class AppointmentPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;

    public AppointmentPanel(User user) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Đặt lịch khám"));
        setBackground(Color.WHITE);

        // Form nhập thông tin lịch khám
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField dateField = new JTextField(10);
        JTextField timeField = new JTextField(8);
        JTextField doctorField = new JTextField(15);
        JTextField reasonField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Ngày (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; formPanel.add(dateField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Giờ (HH:mm):"), gbc);
        gbc.gridx = 1; formPanel.add(timeField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Bác sĩ:"), gbc);
        gbc.gridx = 1; formPanel.add(doctorField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Lý do:"), gbc);
        gbc.gridx = 1; formPanel.add(reasonField, gbc);

        JButton bookBtn = new JButton("Đặt lịch mới");
        bookBtn.setBackground(new Color(41, 128, 185));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(bookBtn, gbc);

        // Bảng lịch hẹn
        String[] columns = {"Ngày", "Giờ", "Bác sĩ", "Lý do"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(table);

        // Sự kiện đặt lịch
        bookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateField.getText().trim();
                String time = timeField.getText().trim();
                String doctor = doctorField.getText().trim();
                String reason = reasonField.getText().trim();
                if (date.isEmpty() || time.isEmpty() || doctor.isEmpty() || reason.isEmpty()) {
                    JOptionPane.showMessageDialog(AppointmentPanel.this, "Vui lòng nhập đầy đủ thông tin!");
                    return;
                }
                tableModel.addRow(new Object[]{date, time, doctor, reason});
                dateField.setText("");
                timeField.setText("");
                doctorField.setText("");
                reasonField.setText("");
                JOptionPane.showMessageDialog(AppointmentPanel.this, "Đặt lịch thành công!");
            }
        });

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
} 