package com.utc2.gui;

import com.utc2.backend.Demo1;
import com.utc2.entity.BENHNHAN;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private Demo1 danhsach;
    private Map<String, JLabel> statLabels;
    
    public DashboardPanel() {
        danhsach = new Demo1(this);
        statLabels = new HashMap<>();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Tạo panel tiêu đề
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Tổng quan hệ thống");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        
        // Tạo panel thống kê
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tạo các card thống kê
        addStatCard(statsPanel, "Tổng số bệnh nhân", "0", new Color(0, 120, 215));
        addStatCard(statsPanel, "Bệnh nhân BHYT", "0", new Color(40, 167, 69));
        addStatCard(statsPanel, "Bệnh nhân BHXH", "0", new Color(255, 193, 7));
        addStatCard(statsPanel, "Phòng theo yêu cầu", "0", new Color(220, 53, 69));
        
        // Thêm các thành phần vào panel chính
        add(headerPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        
        // Cập nhật thống kê
        updateStats();
    }
    
    private void addStatCard(JPanel panel, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        panel.add(card);
        statLabels.put(title, valueLabel);
    }
    
    private void updateStats() {
        int totalPatients = danhsach.getDanhsach().size();
        int bhytCount = 0;
        int bhxhCount = 0;
        int phongYCCount = 0;

        for (BENHNHAN bn : danhsach.getDanhsach().values()) {
            if (bn.getLoaiBH() == 'y') {
                bhytCount++;
            } else {
                bhxhCount++;
            }
            if (bn.getPhongTYC()) {
                phongYCCount++;
            }
        }

        statLabels.get("Tổng số bệnh nhân").setText(String.valueOf(totalPatients));
        statLabels.get("Bệnh nhân BHYT").setText(String.valueOf(bhytCount));
        statLabels.get("Bệnh nhân BHXH").setText(String.valueOf(bhxhCount));
        statLabels.get("Phòng theo yêu cầu").setText(String.valueOf(phongYCCount));
    }

    public void refreshStats() {
        updateStats();
    }
} 