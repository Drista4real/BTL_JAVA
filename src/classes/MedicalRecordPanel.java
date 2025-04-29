package com.utc2.gui;

import javax.swing.*;
import java.awt.*;
import com.utc2.entity.User;

public class MedicalRecordPanel extends JPanel {
    public MedicalRecordPanel(User user) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Bệnh án"));
        setBackground(Color.WHITE);

        JTextArea illnessArea = new JTextArea(user.getIllnessInfo());
        illnessArea.setEditable(false);
        illnessArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        illnessArea.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(illnessArea);
        add(scrollPane, BorderLayout.CENTER);

        // Ghi chú bác sĩ
        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBackground(Color.WHITE);
        notePanel.setBorder(BorderFactory.createTitledBorder("Ghi chú của bác sĩ"));
        JTextArea noteArea = new JTextArea(user.getNote());
        noteArea.setEditable(false);
        noteArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        noteArea.setBackground(new Color(245, 245, 245));
        notePanel.add(new JScrollPane(noteArea), BorderLayout.CENTER);
        notePanel.setPreferredSize(new Dimension(0, 80));
        add(notePanel, BorderLayout.SOUTH);
    }
} 