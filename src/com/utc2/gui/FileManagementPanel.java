package com.utc2.gui;

import com.utc2.backend.Demo1;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FileManagementPanel extends JPanel {
    private Demo1 danhsach;
    
    public FileManagementPanel() {
        danhsach = new Demo1();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton btnGhiFile = new JButton("Ghi File");
        JButton btnDocFile = new JButton("Đọc File");
        
        // Add action listeners
        btnGhiFile.addActionListener(e -> {
            try {
                danhsach.GhiFile();
                JOptionPane.showMessageDialog(this, "Ghi file thành công");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi ghi file: " + ex.getMessage());
            }
        });
        
        btnDocFile.addActionListener(e -> {
            File file = new File("D:/JAVAOOP/Doancanhan/QLbenhnhangui/DSBENHNHAN.txt");
            if (file.exists()) {
                danhsach.DocFile();
                JOptionPane.showMessageDialog(this, "Đọc file thành công");
            } else {
                JOptionPane.showMessageDialog(this, "File không tồn tại");
            }
        });
        
        buttonPanel.add(btnGhiFile);
        buttonPanel.add(btnDocFile);
        
        // Add components to main panel
        add(buttonPanel, BorderLayout.CENTER);
    }
} 