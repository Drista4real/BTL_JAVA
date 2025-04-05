package com.utc2.gui;

import com.utc2.backend.Demo1;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private Demo1 danhsach;
    private Map<String, JLabel> statLabels;
    
    public DashboardPanel() {
        danhsach = new Demo1();
        statLabels = new HashMap<>();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Tổng quan hệ thống");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        
        // Create stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create stat cards
        addStatCard(statsPanel, "Tổng số bệnh nhân", "0", new Color(0, 120, 215));
        addStatCard(statsPanel, "Bệnh nhân BHYT", "0", new Color(40, 167, 69));
        addStatCard(statsPanel, "Bệnh nhân BHXH", "0", new Color(255, 193, 7));
        addStatCard(statsPanel, "Phòng theo yêu cầu", "0", new Color(220, 53, 69));
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        
        // Update stats
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
        // TODO: Update stats from danhsach
        statLabels.get("Tổng số bệnh nhân").setText("0");
        statLabels.get("Bệnh nhân BHYT").setText("0");
        statLabels.get("Bệnh nhân BHXH").setText("0");
        statLabels.get("Phòng theo yêu cầu").setText("0");
    }
} 