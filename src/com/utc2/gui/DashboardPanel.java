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
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel thống kê
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        
        addStatCard(statsPanel, "Tổng số bệnh nhân", "0");
        addStatCard(statsPanel, "Bệnh nhân BHYT", "0");
        addStatCard(statsPanel, "Bệnh nhân BHXH", "0");
        addStatCard(statsPanel, "Phòng theo yêu cầu", "0");
        
        // Panel dưới
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        bottomPanel.setBackground(Color.WHITE);
        
        // Panel lịch hẹn
        JPanel appointmentPanel = createTablePanel("Lịch hẹn hôm nay", 
            new String[]{"Giờ khám", "Tên bệnh nhân", "Bác sĩ", "Ghi chú"});
        
        // Panel bệnh nhân cần chú ý
        JPanel attentionPanel = createTablePanel("Bệnh nhân cần chú ý",
            new String[]{"Tên bệnh nhân", "Lý do", "Mức độ"});
        
        bottomPanel.add(appointmentPanel);
        bottomPanel.add(attentionPanel);
        
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Cập nhật thống kê
        updateStats();
    }
    
    private JPanel createTablePanel(String title, String[] columns) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JTable table = new JTable(new Object[0][columns.length], columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setEnabled(false);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addStatCard(JPanel panel, String title, String value) {
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