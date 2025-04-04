package com.utc2.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    public MainFrame() {
        setTitle("Hệ thống quản lý bệnh nhân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Create main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnPatient = new JButton("Quản lý bệnh nhân");
        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnFile = new JButton("Quản lý file");
        
        // Add action listeners
        btnPatient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "PATIENT");
            }
        });
        
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "SEARCH");
            }
        });
        
        btnFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "FILE");
            }
        });
        
        navPanel.add(btnPatient);
        navPanel.add(btnSearch);
        navPanel.add(btnFile);
        
        // Add panels to main panel
        mainPanel.add(new PatientManagementPanel(), "PATIENT");
        mainPanel.add(new SearchPanel(), "SEARCH");
        mainPanel.add(new FileManagementPanel(), "FILE");
        
        // Add components to frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
} 