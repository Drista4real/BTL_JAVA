package com.utc2.gui;

import com.utc2.backend.Demo1;
import com.utc2.entity.BENHNHAN;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SearchPanel extends JPanel {
    private Demo1 danhsach;
    private DefaultTableModel tableModel;
    private JTable searchTable;
    
    public SearchPanel() {
        danhsach = new Demo1();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Tìm kiếm");
        
        searchPanel.add(new JLabel("Mã bệnh nhân:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        
        // Create table
        String[] columns = {"Mã bệnh nhân", "Họ tên", "Ngày nhập viện", "Phòng theo yêu cầu", "Loại bảo hiểm"};
        tableModel = new DefaultTableModel(columns, 0);
        searchTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(searchTable);
        
        // Add action listener
        btnSearch.addActionListener(e -> {
            String maBN = txtSearch.getText().trim();
            if (!maBN.isEmpty()) {
                BENHNHAN bn = danhsach.Tim(maBN);
                if (bn != null) {
                    tableModel.setRowCount(0);
                    Object[] row = {
                        bn.getMABN(),
                        bn.getHoten(),
                        bn.getNgaynhapvien(),
                        bn.getPhongTYC(),
                        bn.getLoaiBH()
                    };
                    tableModel.addRow(row);
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy bệnh nhân");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã bệnh nhân");
            }
        });
        
        // Add components to main panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
} 