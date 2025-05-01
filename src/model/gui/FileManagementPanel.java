package model.gui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

public class FileManagementPanel extends JPanel {
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream input = FileManagementPanel.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("Không tìm thấy tệp database.properties");
            }
            props.load(input);
            DB_URL = props.getProperty("url");
            DB_USER = props.getProperty("username");
            DB_PASSWORD = props.getProperty("password");
            Class.forName(props.getProperty("driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Lỗi khi tải cấu hình cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public FileManagementPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton btnExport = new JButton("Xuất dữ liệu");
        JButton btnImport = new JButton("Nhập dữ liệu");

        btnExport.addActionListener(e -> {
            try {
                exportData();
                JOptionPane.showMessageDialog(this, "Xuất dữ liệu thành công");
            } catch (SQLException | IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất dữ liệu: " + ex.getMessage());
            }
        });

        btnImport.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng nhập dữ liệu chưa được triển khai");
        });

        buttonPanel.add(btnExport);
        buttonPanel.add(btnImport);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void exportData() throws SQLException, IOException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             FileWriter writer = new FileWriter("data/Patients_export.sql")) {
            String sql = "SELECT p.PatientID, p.FullName, p.DateOfBirth, p.Gender, p.PhoneNumber, p.Address, p.CreatedAt, " +
                    "i.PolicyNumber FROM Patients p LEFT JOIN Insurance i ON p.PatientID = i.PatientID " +
                    "WHERE i.Provider = 'BHYT' OR i.Provider IS NULL";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            writer.write("INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, CreatedAt) VALUES\n");
            boolean first = true;
            while (rs.next()) {
                String patientID = rs.getString("PatientID");
                String userID = "U" + System.currentTimeMillis() + "_" + patientID;
                String fullName = rs.getString("FullName").replace("'", "''");
                String dateOfBirth = rs.getString("DateOfBirth");
                String gender = rs.getString("Gender");
                String phoneNumber = rs.getString("PhoneNumber") != null ? rs.getString("PhoneNumber").replace("'", "''") : "";
                String address = rs.getString("Address") != null ? rs.getString("Address").replace("'", "''") : "";
                String createdAt = rs.getString("CreatedAt");

                if (!first) {
                    writer.write(",\n");
                }
                writer.write(String.format("('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                        patientID, userID, fullName, dateOfBirth, gender, phoneNumber, address, createdAt));
                first = false;
            }
            writer.write(";\n");

            rs.beforeFirst();
            writer.write("\nINSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, Status) VALUES\n");
            first = true;
            while (rs.next()) {
                String policyNumber = rs.getString("PolicyNumber");
                if (policyNumber != null) {
                    String patientID = rs.getString("PatientID");
                    String insuranceID = "I" + System.currentTimeMillis() + "_" + patientID;
                    String startDate = rs.getString("CreatedAt");
                    String expirationDate = "2026-01-01";

                    if (!first) {
                        writer.write(",\n");
                    }
                    writer.write(String.format("('%s', '%s', 'BHYT', '%s', '%s', '%s', 'Hoạt Động')",
                            insuranceID, patientID, policyNumber.replace("'", "''"), startDate, expirationDate));
                    first = false;
                }
            }
            writer.write(";\n");
        }
    }
}