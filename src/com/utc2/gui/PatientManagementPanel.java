package com.utc2.gui;

import com.utc2.backend.Demo1;
import com.utc2.entity.BENHNHAN;
import com.utc2.entity.BENHNHANBAOHIEMXAHOI;
import com.utc2.entity.BENHNHANBAOHIEMYTE;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientManagementPanel extends JPanel {
    private Demo1 danhsach;
    private DefaultTableModel tableModel;
    private JTable patientTable;
    private JTextField txtMABN, txtHoten, txtNgaynhapvien, txtMaBHYT, txtMaBHXH;
    private JComboBox<String> cobLoaiBH;
    private JCheckBox ckbPhongTYC;
    
    public PatientManagementPanel() {
        danhsach = new Demo1();
        initComponents();
        loadDataTable();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Create table
        String[] columns = {"Mã bệnh nhân", "Họ tên", "Ngày nhập viện", "Phòng theo yêu cầu", "Loại bảo hiểm"};
        tableModel = new DefaultTableModel(columns, 0);
        patientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(patientTable);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add form components
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã bệnh nhân:"), gbc);
        gbc.gridx = 1;
        txtMABN = new JTextField(20);
        formPanel.add(txtMABN, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        txtHoten = new JTextField(20);
        formPanel.add(txtHoten, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Ngày nhập viện:"), gbc);
        gbc.gridx = 1;
        txtNgaynhapvien = new JTextField(20);
        formPanel.add(txtNgaynhapvien, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Loại bảo hiểm:"), gbc);
        gbc.gridx = 1;
        cobLoaiBH = new JComboBox<>(new String[]{"y", "x"});
        formPanel.add(cobLoaiBH, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Mã BHYT:"), gbc);
        gbc.gridx = 1;
        txtMaBHYT = new JTextField(20);
        formPanel.add(txtMaBHYT, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Mã BHXH:"), gbc);
        gbc.gridx = 1;
        txtMaBHXH = new JTextField(20);
        formPanel.add(txtMaBHXH, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Phòng theo yêu cầu:"), gbc);
        gbc.gridx = 1;
        ckbPhongTYC = new JCheckBox();
        formPanel.add(ckbPhongTYC, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        JButton btnThem = new JButton("Thêm");
        JButton btnXoa = new JButton("Xóa");
        
        btnThem.addActionListener(e -> themBenhNhan());
        btnXoa.addActionListener(e -> xoaBenhNhan());
        
        buttonPanel.add(btnThem);
        buttonPanel.add(btnXoa);
        
        // Add components to main panel
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.WEST);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadDataTable() {
        tableModel.setRowCount(0);
        for (BENHNHAN bn : danhsach.getDanhsach().values()) {
            SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
            Object[] row = {
                bn.getMABN(),
                bn.getHoten(),
                fmd.format(bn.getNgaynhapvien()),
                bn.getPhongTYC(),
                bn.getLoaiBH()
            };
            tableModel.addRow(row);
        }
    }
    
    private void themBenhNhan() {
        try {
            SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
            Date NgayNV = fmd.parse(txtNgaynhapvien.getText());
            
            BENHNHAN benhnhan;
            if (cobLoaiBH.getSelectedItem().equals("y")) {
                benhnhan = new BENHNHANBAOHIEMYTE('y', txtMABN.getText(), txtHoten.getText(), 
                    NgayNV, txtMaBHYT.getText(), ckbPhongTYC.isSelected());
            } else {
                benhnhan = new BENHNHANBAOHIEMXAHOI('x', txtMABN.getText(), txtHoten.getText(), 
                    NgayNV, txtMaBHXH.getText(), ckbPhongTYC.isSelected());
            }
            
            danhsach.NhapGUI(benhnhan);
            danhsach.GhiFile();
            loadDataTable();
            JOptionPane.showMessageDialog(this, "Thêm mới bệnh nhân thành công");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Thêm mới bệnh nhân thất bại: " + e.getMessage());
        }
    }
    
    private void xoaBenhNhan() {
        int row = patientTable.getSelectedRow();
        if (row >= 0) {
            String maBN = (String) tableModel.getValueAt(row, 0);
            int result = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa bệnh nhân này?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                danhsach.Xoa(maBN);
                try {
                    danhsach.GhiFile();
                    loadDataTable();
                    JOptionPane.showMessageDialog(this, "Xóa bệnh nhân thành công");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi ghi file: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bệnh nhân cần xóa");
        }
    }
} 