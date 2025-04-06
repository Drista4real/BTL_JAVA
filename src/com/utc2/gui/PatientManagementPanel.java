package com.utc2.gui;

import com.utc2.backend.Demo1;
import com.utc2.entity.BENHNHAN;
import com.utc2.entity.BENHNHANBAOHIEMYTE;
import com.utc2.entity.BENHNHANBAOHIEMXAHOI;
import com.utc2.utils.ExceptionUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
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
        danhsach = new Demo1(this);
        try {
            danhsach.DocFile();
        } catch (Exception e) {
            ExceptionUtils.handleGeneralException(this, e);
        }
        initComponents();
        loadDataToTable();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Quản lý bệnh nhân");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        
        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columns = {"Mã bệnh nhân", "Họ tên", "Ngày nhập viện", "Phòng theo yêu cầu", "Loại bảo hiểm"};
        tableModel = new DefaultTableModel(columns, 0);
        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(30);
        patientTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(patientTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin bệnh nhân"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add form components
        addFormField(formPanel, gbc, "Mã bệnh nhân:", txtMABN = new JTextField(20), 0);
        addFormField(formPanel, gbc, "Họ tên:", txtHoten = new JTextField(20), 1);
        addFormField(formPanel, gbc, "Ngày nhập viện:", txtNgaynhapvien = new JTextField(20), 2);
        addFormField(formPanel, gbc, "Loại bảo hiểm:", cobLoaiBH = new JComboBox<>(new String[]{"y", "x"}), 3);
        addFormField(formPanel, gbc, "Mã BHYT:", txtMaBHYT = new JTextField(20), 4);
        addFormField(formPanel, gbc, "Mã BHXH:", txtMaBHXH = new JTextField(20), 5);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Phòng theo yêu cầu:"), gbc);
        gbc.gridx = 1;
        ckbPhongTYC = new JCheckBox();
        formPanel.add(ckbPhongTYC, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnThem = createStyledButton("Thêm");
        JButton btnXoa = createStyledButton("Xóa");
        JButton btnSua = createStyledButton("Sửa");
        JButton btnClear = createStyledButton("Xóa form");
        
        btnThem.addActionListener(e -> themBenhNhan());
        btnXoa.addActionListener(e -> xoaBenhNhan());
        btnSua.addActionListener(e -> suaBenhNhan());
        btnClear.addActionListener(e -> clearForm());
        
        buttonPanel.add(btnThem);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnClear);
        
        // Add components to content panel
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(formPanel, BorderLayout.EAST);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent component, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 35));
        button.setBackground(new Color(0, 120, 215));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return button;
    }
    
    private void loadDataToTable() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
        
        for (BENHNHAN bn : danhsach.getDanhsach().values()) {
            Object[] row = {
                bn.getMABN(),
                bn.getHoten(),
                fmd.format(bn.getNgaynhapvien()),
                bn.getPhongTYC() ? "Có" : "Không",
                bn.getLoaiBH()
            };
            tableModel.addRow(row);
        }
    }
    
    private void themBenhNhan() {
        try {
            // Kiểm tra các trường bắt buộc
            if (txtMABN.getText().trim().isEmpty() || txtHoten.getText().trim().isEmpty() || 
                txtNgaynhapvien.getText().trim().isEmpty()) {
                ExceptionUtils.handleValidationException(this, "Vui lòng nhập đầy đủ thông tin bắt buộc");
                return;
            }

            // Parse ngày nhập viện
            SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
            Date NgayNV;
            try {
                NgayNV = fmd.parse(txtNgaynhapvien.getText());
            } catch (ParseException e) {
                ExceptionUtils.handleParseException(this, e);
                return;
            }

            // Tạo đối tượng bệnh nhân
            BENHNHAN benhnhan = null;
            if (cobLoaiBH.getSelectedItem().equals("y")) {
                if (txtMaBHYT.getText().trim().isEmpty()) {
                    ExceptionUtils.handleValidationException(this, "Vui lòng nhập mã BHYT");
                    return;
                }
                benhnhan = new BENHNHANBAOHIEMYTE('y', txtMABN.getText(), txtHoten.getText(), 
                    NgayNV, txtMaBHYT.getText(), ckbPhongTYC.isSelected());
            } else {
                if (txtMaBHXH.getText().trim().isEmpty()) {
                    ExceptionUtils.handleValidationException(this, "Vui lòng nhập mã BHXH");
                    return;
                }
                benhnhan = new BENHNHANBAOHIEMXAHOI('x', txtMABN.getText(), txtHoten.getText(), 
                    NgayNV, txtMaBHXH.getText(), ckbPhongTYC.isSelected());
            }

            // Thêm bệnh nhân vào danh sách
            danhsach.NhapGUI(benhnhan);
            
            // Lưu vào file
            try {
                danhsach.GhiFile();
            } catch (IOException e) {
                ExceptionUtils.handleFileException(this, e);
                return;
            }
            
            // Cập nhật bảng
            loadDataToTable();

            // Xóa form
            clearForm();
            
            JOptionPane.showMessageDialog(this, "Thêm bệnh nhân thành công");
        } catch (Exception e) {
            ExceptionUtils.handleGeneralException(this, e);
        }
    }
    
    private void xoaBenhNhan() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            ExceptionUtils.handleValidationException(this, "Vui lòng chọn bệnh nhân cần xóa");
            return;
        }
        
        String maBN = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa bệnh nhân này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            danhsach.Xoa(maBN);
            try {
                danhsach.GhiFile();
            } catch (IOException e) {
                ExceptionUtils.handleFileException(this, e);
                return;
            }
            loadDataToTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Xóa bệnh nhân thành công");
        }
    }
    
    private void suaBenhNhan() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            ExceptionUtils.handleValidationException(this, "Vui lòng chọn bệnh nhân cần sửa");
            return;
        }
        
        try {
            String maBN = (String) tableModel.getValueAt(selectedRow, 0);
            BENHNHAN bn = danhsach.Tim(maBN);
            
            if (bn != null) {
                // Cập nhật thông tin
                bn.setHoten(txtHoten.getText());
                try {
                    bn.setNgaynhapvien(new SimpleDateFormat("dd/MM/yyyy").parse(txtNgaynhapvien.getText()));
                } catch (ParseException e) {
                    ExceptionUtils.handleParseException(this, e);
                    return;
                }
                bn.setPhongTYC(ckbPhongTYC.isSelected());
                
                if (bn instanceof BENHNHANBAOHIEMYTE) {
                    ((BENHNHANBAOHIEMYTE)bn).setMSBH(txtMaBHYT.getText());
                } else {
                    ((BENHNHANBAOHIEMXAHOI)bn).setMBHXH(txtMaBHXH.getText());
                }
                
                danhsach.SuaGUI(bn);
                try {
                    danhsach.GhiFile();
                } catch (IOException e) {
                    ExceptionUtils.handleFileException(this, e);
                    return;
                }
                loadDataToTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Sửa thông tin bệnh nhân thành công");
            }
        } catch (Exception e) {
            ExceptionUtils.handleGeneralException(this, e);
        }
    }
    
    private void clearForm() {
        txtMABN.setText("");
        txtHoten.setText("");
        txtNgaynhapvien.setText("");
        cobLoaiBH.setSelectedIndex(0);
        txtMaBHYT.setText("");
        txtMaBHXH.setText("");
        ckbPhongTYC.setSelected(false);
    }
} 