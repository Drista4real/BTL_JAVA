package model.gui;

import model.utils.ExceptionUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class PatientManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable patientTable;
    private JTextField txtMABN, txtHoten, txtNgaynhapvien, txtMaBH, txtDateOfBirth, txtPhoneNumber, txtAddress;
    private JCheckBox ckbPhongTYC;
    private JComboBox<String> cobGender;
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream input = PatientManagementPanel.class.getClassLoader().getResourceAsStream("database.properties")) {
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

    public PatientManagementPanel() {
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Panel tiêu đề
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Quản lý bệnh nhân");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel);

        // Panel nội dung
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel bảng
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columns = {"Mã bệnh nhân", "Họ tên", "Ngày nhập viện", "Phòng theo yêu cầu", "Mã BHYT"};
        tableModel = new DefaultTableModel(columns, 0);
        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(30);
        patientTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(patientTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Panel biểu mẫu
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin bệnh nhân"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormField(formPanel, gbc, "Mã bệnh nhân:", txtMABN = new JTextField(20), 0);
        addFormField(formPanel, gbc, "Họ tên:", txtHoten = new JTextField(20), 1);
        addFormField(formPanel, gbc, "Ngày sinh (dd/MM/yyyy):", txtDateOfBirth = new JTextField(20), 2);
        addFormField(formPanel, gbc, "Giới tính:", cobGender = new JComboBox<>(new String[]{"Nam", "Nữ"}), 3);
        addFormField(formPanel, gbc, "Số điện thoại:", txtPhoneNumber = new JTextField(20), 4);
        addFormField(formPanel, gbc, "Địa chỉ:", txtAddress = new JTextField(20), 5);
        addFormField(formPanel, gbc, "Ngày nhập viện (dd/MM/yyyy):", txtNgaynhapvien = new JTextField(20), 6);
        addFormField(formPanel, gbc, "Mã BHYT:", txtMaBH = new JTextField(20), 7);

        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Phòng theo yêu cầu:"), gbc);
        gbc.gridx = 1;
        ckbPhongTYC = new JCheckBox();
        formPanel.add(ckbPhongTYC, gbc);

        // Panel nút
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

        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(formPanel, BorderLayout.EAST);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

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
        tableModel.setRowCount(0);
        SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT p.PatientID, p.FullName, p.CreatedAt, i.PolicyNumber " +
                    "FROM Patients p LEFT JOIN Insurance i ON p.PatientID = i.PatientID " +
                    "WHERE i.Provider = 'BHYT' OR i.Provider IS NULL";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String patientID = rs.getString("PatientID");
                String fullName = rs.getString("FullName");
                Date createdAt = rs.getDate("CreatedAt");
                String policyNumber = rs.getString("PolicyNumber");

                String sqlPhongTYC = "SELECT COUNT(*) FROM Admissions a JOIN HospitalRooms r ON a.RoomID = r.RoomID " +
                        "WHERE a.PatientID = ? AND r.RoomType = 'VIP'";
                PreparedStatement stmtPhongTYC = conn.prepareStatement(sqlPhongTYC);
                stmtPhongTYC.setString(1, patientID);
                ResultSet rsPhongTYC = stmtPhongTYC.executeQuery();
                boolean phongTYC = rsPhongTYC.next() && rsPhongTYC.getInt(1) > 0;

                Object[] row = {
                        patientID,
                        fullName,
                        createdAt != null ? fmd.format(createdAt) : "",
                        phongTYC ? "Có" : "Không",
                        policyNumber != null ? policyNumber : ""
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void themBenhNhan() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (txtMABN.getText().trim().isEmpty() || txtHoten.getText().trim().isEmpty() ||
                    txtNgaynhapvien.getText().trim().isEmpty() || txtDateOfBirth.getText().trim().isEmpty() ||
                    txtPhoneNumber.getText().trim().isEmpty()) {
                ExceptionUtils.handleValidationException(this, "Vui lòng nhập đầy đủ thông tin bắt buộc");
                return;
            }

            SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
            Date ngayNV, dob;
            try {
                ngayNV = fmd.parse(txtNgaynhapvien.getText());
                dob = fmd.parse(txtDateOfBirth.getText());
            } catch (java.text.ParseException e) {  // Thay vì Exception
                ExceptionUtils.handleParseException(this, e);
                return;
            }


            String patientID = txtMABN.getText();
            String fullName = txtHoten.getText();
            String gender = (String) cobGender.getSelectedItem();
            String phoneNumber = txtPhoneNumber.getText();
            String address = txtAddress.getText();
            String maBH = txtMaBH.getText();
            boolean phongTYC = ckbPhongTYC.isSelected();

            String sqlCheck = "SELECT COUNT(*) FROM Patients WHERE PatientID = ?";
            PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setString(1, patientID);
            ResultSet rsCheck = stmtCheck.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                ExceptionUtils.handleValidationException(this, "Mã bệnh nhân đã tồn tại");
                return;
            }

            String sqlPatient = "INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, CreatedAt) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmtPatient = conn.prepareStatement(sqlPatient);
            String userID = "U" + System.currentTimeMillis();
            stmtPatient.setString(1, patientID);
            stmtPatient.setString(2, userID);
            stmtPatient.setString(3, fullName);
            stmtPatient.setDate(4, new java.sql.Date(dob.getTime()));
            stmtPatient.setString(5, gender);
            stmtPatient.setString(6, phoneNumber);
            stmtPatient.setString(7, address);
            stmtPatient.setDate(8, new java.sql.Date(ngayNV.getTime()));
            stmtPatient.executeUpdate();

            if (!maBH.trim().isEmpty()) {
                String sqlInsurance = "INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, Status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmtInsurance = conn.prepareStatement(sqlInsurance);
                String insuranceID = "I" + System.currentTimeMillis();
                stmtInsurance.setString(1, insuranceID);
                stmtInsurance.setString(2, patientID);
                stmtInsurance.setString(3, "BHYT");
                stmtInsurance.setString(4, maBH);
                stmtInsurance.setDate(5, new java.sql.Date(new Date().getTime()));
                stmtInsurance.setDate(6, new java.sql.Date(new Date().getTime() + 365L * 24 * 60 * 60 * 1000));
                stmtInsurance.setString(7, "Hoạt Động");
                stmtInsurance.executeUpdate();
            }

            if (phongTYC) {
                String sqlRoom = "SELECT RoomID FROM HospitalRooms WHERE RoomType = 'VIP' AND AvailableBeds > 0 LIMIT 1";
                PreparedStatement stmtRoom = conn.prepareStatement(sqlRoom);
                ResultSet rsRoom = stmtRoom.executeQuery();
                if (rsRoom.next()) {
                    String roomID = rsRoom.getString("RoomID");
                    String sqlAdmission = "INSERT INTO Admissions (AdmissionID, PatientID, RoomID, AdmissionDate) " +
                            "VALUES (?, ?, ?, ?)";
                    PreparedStatement stmtAdmission = conn.prepareStatement(sqlAdmission);
                    String admissionID = "A" + System.currentTimeMillis();
                    stmtAdmission.setString(1, admissionID);
                    stmtAdmission.setString(2, patientID);
                    stmtAdmission.setString(3, roomID);
                    stmtAdmission.setDate(4, new java.sql.Date(ngayNV.getTime()));
                    stmtAdmission.executeUpdate();

                    String sqlUpdateRoom = "UPDATE HospitalRooms SET AvailableBeds = AvailableBeds - 1 WHERE RoomID = ?";
                    PreparedStatement stmtUpdateRoom = conn.prepareStatement(sqlUpdateRoom);
                    stmtUpdateRoom.setString(1, roomID);
                    stmtUpdateRoom.executeUpdate();
                } else {
                    JOptionPane.showMessageDialog(this, "Không còn phòng VIP trống");
                }
            }

            loadDataToTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm bệnh nhân thành công");
        } catch (SQLException e) {
            ExceptionUtils.handleValidationException(this, "Lỗi khi thêm bệnh nhân: " + e.getMessage());
        }
    }

    private void xoaBenhNhan() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            ExceptionUtils.handleValidationException(this, "Vui lòng chọn bệnh nhân cần xóa");
            return;
        }

        String patientID = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa bệnh nhân này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sqlInsurance = "DELETE FROM Insurance WHERE PatientID = ?";
                PreparedStatement stmtInsurance = conn.prepareStatement(sqlInsurance);
                stmtInsurance.setString(1, patientID);
                stmtInsurance.executeUpdate();

                String sqlAdmission = "DELETE FROM Admissions WHERE PatientID = ?";
                PreparedStatement stmtAdmission = conn.prepareStatement(sqlAdmission);
                stmtAdmission.setString(1, patientID);
                stmtAdmission.executeUpdate();

                String sqlPatient = "DELETE FROM Patients WHERE PatientID = ?";
                PreparedStatement stmtPatient = conn.prepareStatement(sqlPatient);
                stmtPatient.setString(1, patientID);
                stmtPatient.executeUpdate();

                loadDataToTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Xóa bệnh nhân thành công");
            } catch (SQLException e) {
                ExceptionUtils.handleValidationException(this, "Lỗi khi xóa bệnh nhân: " + e.getMessage());
            }
        }
    }

    private void suaBenhNhan() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            ExceptionUtils.handleValidationException(this, "Vui lòng chọn bệnh nhân cần sửa");
            return;
        }

        String patientID = (String) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (txtHoten.getText().trim().isEmpty() || txtNgaynhapvien.getText().trim().isEmpty() ||
                    txtDateOfBirth.getText().trim().isEmpty() || txtPhoneNumber.getText().trim().isEmpty()) {
                ExceptionUtils.handleValidationException(this, "Vui lòng nhập đầy đủ thông tin bắt buộc");
                return;
            }

            SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
            Date ngayNV, dob;
            try {
                ngayNV = fmd.parse(txtNgaynhapvien.getText());
                dob = fmd.parse(txtDateOfBirth.getText());
            } catch (java.text.ParseException e) {  // Thay vì Exception
                ExceptionUtils.handleParseException(this, e);
                return;
            }


            String fullName = txtHoten.getText();
            String gender = (String) cobGender.getSelectedItem();
            String phoneNumber = txtPhoneNumber.getText();
            String address = txtAddress.getText();
            String maBH = txtMaBH.getText();
            boolean phongTYC = ckbPhongTYC.isSelected();

            String sqlPatient = "UPDATE Patients SET FullName = ?, DateOfBirth = ?, Gender = ?, PhoneNumber = ?, Address = ?, CreatedAt = ? " +
                    "WHERE PatientID = ?";
            PreparedStatement stmtPatient = conn.prepareStatement(sqlPatient);
            stmtPatient.setString(1, fullName);
            stmtPatient.setDate(2, new java.sql.Date(dob.getTime()));
            stmtPatient.setString(3, gender);
            stmtPatient.setString(4, phoneNumber);
            stmtPatient.setString(5, address);
            stmtPatient.setDate(6, new java.sql.Date(ngayNV.getTime()));
            stmtPatient.setString(7, patientID);
            stmtPatient.executeUpdate();

            String sqlCheckInsurance = "SELECT COUNT(*) FROM Insurance WHERE PatientID = ?";
            PreparedStatement stmtCheckInsurance = conn.prepareStatement(sqlCheckInsurance);
            stmtCheckInsurance.setString(1, patientID);
            ResultSet rsCheckInsurance = stmtCheckInsurance.executeQuery();
            boolean hasInsurance = rsCheckInsurance.next() && rsCheckInsurance.getInt(1) > 0;

            if (!maBH.trim().isEmpty()) {
                if (hasInsurance) {
                    String sqlUpdateInsurance = "UPDATE Insurance SET Provider = 'BHYT', PolicyNumber = ? WHERE PatientID = ?";
                    PreparedStatement stmtUpdateInsurance = conn.prepareStatement(sqlUpdateInsurance);
                    stmtUpdateInsurance.setString(1, maBH);
                    stmtUpdateInsurance.setString(2, patientID);
                    stmtUpdateInsurance.executeUpdate();
                } else {
                    String sqlInsertInsurance = "INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, Status) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmtInsertInsurance = conn.prepareStatement(sqlInsertInsurance);
                    String insuranceID = "I" + System.currentTimeMillis();
                    stmtInsertInsurance.setString(1, insuranceID);
                    stmtInsertInsurance.setString(2, patientID);
                    stmtInsertInsurance.setString(3, "BHYT");
                    stmtInsertInsurance.setString(4, maBH);
                    stmtInsertInsurance.setDate(5, new java.sql.Date(new Date().getTime()));
                    stmtInsertInsurance.setDate(6, new java.sql.Date(new Date().getTime() + 365L * 24 * 60 * 60 * 1000));
                    stmtInsertInsurance.setString(7, "Hoạt Động");
                    stmtInsertInsurance.executeUpdate();
                }
            } else if (hasInsurance) {
                String sqlDeleteInsurance = "DELETE FROM Insurance WHERE PatientID = ?";
                PreparedStatement stmtDeleteInsurance = conn.prepareStatement(sqlDeleteInsurance);
                stmtDeleteInsurance.setString(1, patientID);
                stmtDeleteInsurance.executeUpdate();
            }

            String sqlCheckAdmission = "SELECT COUNT(*) FROM Admissions WHERE PatientID = ?";
            PreparedStatement stmtCheckAdmission = conn.prepareStatement(sqlCheckAdmission);
            stmtCheckAdmission.setString(1, patientID);
            ResultSet rsCheckAdmission = stmtCheckAdmission.executeQuery();
            boolean hasAdmission = rsCheckAdmission.next() && rsCheckAdmission.getInt(1) > 0;

            if (phongTYC) {
                if (!hasAdmission) {
                    String sqlRoom = "SELECT RoomID FROM HospitalRooms WHERE RoomType = 'VIP' AND AvailableBeds > 0 LIMIT 1";
                    PreparedStatement stmtRoom = conn.prepareStatement(sqlRoom);
                    ResultSet rsRoom = stmtRoom.executeQuery();
                    if (rsRoom.next()) {
                        String roomID = rsRoom.getString("RoomID");
                        String sqlAdmission = "INSERT INTO Admissions (AdmissionID, PatientID, RoomID, AdmissionDate) " +
                                "VALUES (?, ?, ?, ?)";
                        PreparedStatement stmtAdmission = conn.prepareStatement(sqlAdmission);
                        String admissionID = "A" + System.currentTimeMillis();
                        stmtAdmission.setString(1, admissionID);
                        stmtAdmission.setString(2, patientID);
                        stmtAdmission.setString(3, roomID);
                        stmtAdmission.setDate(4, new java.sql.Date(ngayNV.getTime()));
                        stmtAdmission.executeUpdate();

                        String sqlUpdateRoom = "UPDATE HospitalRooms SET AvailableBeds = AvailableBeds - 1 WHERE RoomID = ?";
                        PreparedStatement stmtUpdateRoom = conn.prepareStatement(sqlUpdateRoom);
                        stmtUpdateRoom.setString(1, roomID);
                        stmtUpdateRoom.executeUpdate();
                    } else {
                        JOptionPane.showMessageDialog(this, "Không còn phòng VIP trống");
                    }
                }
            } else if (hasAdmission) {
                String sqlDeleteAdmission = "DELETE FROM Admissions WHERE PatientID = ?";
                PreparedStatement stmtDeleteAdmission = conn.prepareStatement(sqlDeleteAdmission);
                stmtDeleteAdmission.setString(1, patientID);
                stmtDeleteAdmission.executeUpdate();
            }

            loadDataToTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Sửa thông tin bệnh nhân thành công");
        } catch (SQLException e) {
            ExceptionUtils.handleValidationException(this, "Lỗi khi sửa bệnh nhân: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtMABN.setText("");
        txtHoten.setText("");
        txtDateOfBirth.setText("");
        cobGender.setSelectedIndex(0);
        txtPhoneNumber.setText("");
        txtAddress.setText("");
        txtNgaynhapvien.setText("");
        txtMaBH.setText("");
        ckbPhongTYC.setSelected(false);
    }
}