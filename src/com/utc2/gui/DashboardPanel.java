package com.utc2.gui;

import com.utc2.backend.Demo1;
import com.utc2.entity.BENHNHAN;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
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
        setBackground(new Color(248, 249, 250)); // #F8F9FA
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
            BorderFactory.createLineBorder(new Color(227, 242, 253)), // #E3F2FD
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Header panel với màu nền
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(227, 242, 253)); // #E3F2FD
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80)); // #2C3E50
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Table với màu nền xen kẽ
        JTable table = new JTable(new Object[0][columns.length], columns) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 249, 249));
                }
                return comp;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setBackground(new Color(227, 242, 253)); // #E3F2FD
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Đặt kích thước cột cố định
        if (title.equals("Lịch hẹn hôm nay")) {
            table.getColumnModel().getColumn(0).setPreferredWidth(100); // Giờ khám
            table.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên bệnh nhân
            table.getColumnModel().getColumn(2).setPreferredWidth(100); // Bác sĩ
            table.getColumnModel().getColumn(3).setPreferredWidth(150); // Ghi chú
        } else if (title.equals("Bệnh nhân cần chú ý")) {
            table.getColumnModel().getColumn(0).setPreferredWidth(150); // Tên bệnh nhân
            table.getColumnModel().getColumn(1).setPreferredWidth(200); // Lý do
            table.getColumnModel().getColumn(2).setPreferredWidth(100); // Mức độ
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addStatCard(JPanel panel, String title, String value) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(new Color(248, 249, 250)); // #F8F9FA
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(93, 173, 226)), // #5DADE2
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(44, 62, 80)); // #2C3E50
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(new Color(93, 173, 226)); // #5DADE2
        
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