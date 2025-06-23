package view.receptionistPanel;

import reponsitory.Patientreponsitory;
import model.Patient;
import view.listPanelMain.ReceptionistPanel;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.function.Consumer;

public class ModifyPatient extends JPanel {
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtAge;
    private JTextField txtGender;
    private JTextField txtAddress;
    private JButton btnModify;
    private ReceptionistPanel receptionistPanel;
    private Patient patient;
    private Consumer<Void> onSaveListener;

    public ModifyPatient(ReceptionistPanel receptionistPanel, Patient patient) {
        this.receptionistPanel = receptionistPanel;
        this.patient = patient;
        initComponents();
        populateFields();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Chỉnh Sửa Bệnh Nhân");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(lblTitle, gbc);

        JLabel lblName = new JLabel("Họ và tên:");
        lblName.setFont(new Font("Arial", Font.ITALIC, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(lblName, gbc);

        txtName = new JTextField(28);
        txtName.setFont(new Font("Arial", Font.PLAIN, 16));
        txtName.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(txtName, gbc);

        JLabel lblPhone = new JLabel("Số điện thoại:");
        lblPhone.setFont(new Font("Arial", Font.ITALIC, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblPhone, gbc);

        txtPhone = new JTextField(28);
        txtPhone.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPhone.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(txtPhone, gbc);

        JLabel lblAge = new JLabel("Tuổi:");
        lblAge.setFont(new Font("Arial", Font.ITALIC, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lblAge, gbc);

        txtAge = new JTextField(28);
        txtAge.setFont(new Font("Arial", Font.PLAIN, 16));
        txtAge.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(txtAge, gbc);

        JLabel lblGender = new JLabel("Giới tính:");
        lblGender.setFont(new Font("Arial", Font.ITALIC, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lblGender, gbc);

        txtGender = new JTextField(28);
        txtGender.setFont(new Font("Arial", Font.PLAIN, 16));
        txtGender.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(txtGender, gbc);

        JLabel lblAddress = new JLabel("Địa chỉ:");
        lblAddress.setFont(new Font("Arial", Font.ITALIC, 16));
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(lblAddress, gbc);

        txtAddress = new JTextField(28);
        txtAddress.setFont(new Font("Arial", Font.PLAIN, 16));
        txtAddress.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(txtAddress, gbc);

        btnModify = new JButton("Chỉnh sửa");
        btnModify.setFont(new Font("Arial", Font.BOLD, 16));
        btnModify.setBackground(new Color(0, 123, 255));
        btnModify.setForeground(Color.WHITE);
        btnModify.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnModify, gbc);

        btnModify.addActionListener(e -> {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String ageStr = txtAge.getText().trim();
            String gender = txtGender.getText().trim();
            String address = txtAddress.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || ageStr.isEmpty() || gender.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
                if (age < 0) {
                    JOptionPane.showMessageDialog(this, "Tuổi phải là số không âm.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tuổi phải là số nguyên hợp lệ.");
                return;
            }

            int genderInt = gender.equalsIgnoreCase("Nam") ? 1 : (gender.equalsIgnoreCase("Nữ") ? 0 : -1);
            if (genderInt == -1) {
                JOptionPane.showMessageDialog(this, "Giới tính phải là 'Nam' hoặc 'Nữ'.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn chỉnh sửa thông tin bệnh nhân này?",
                    "Xác nhận chỉnh sửa",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.OK_OPTION) {
                LocalDate birthDate = LocalDate.now().minusYears(age);
                Patient updatedPatient = new Patient(
                        patient.getId(),
                        name,
                        Date.valueOf(birthDate),
                        address,
                        genderInt,
                        phone,
                        patient.getIdCard()
                );

                try {
                    Patientreponsitory.updatePatient(updatedPatient);
                    JOptionPane.showMessageDialog(this, "Chỉnh sửa bệnh nhân thành công!");
                    if (onSaveListener != null) {
                        onSaveListener.accept(null);
                    }
                    receptionistPanel.getShowPatientsReceptionistPanel().reloadPatientList();
                    receptionistPanel.getCardLayout().show(receptionistPanel.getCenterPanel(), "ShowPatients");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi chỉnh sửa bệnh nhân: " + ex.getMessage());
                }
            }
        });
    }

    private void populateFields() {
        if (patient != null) {
            txtName.setText(patient.getName());
            txtPhone.setText(patient.getPhoneNumber());
            txtAddress.setText(patient.getAddress());
            txtGender.setText(patient.getGender() == 1 ? "Nam" : "Nữ");
            if (patient.getBirthDate() != null) {
                int age = java.time.Period.between(
                        patient.getBirthDate().toLocalDate(),
                        java.time.LocalDate.now()
                ).getYears();
                txtAge.setText(String.valueOf(age));
            } else {
                txtAge.setText("0");
            }
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtPhone.setText("");
        txtAge.setText("");
        txtGender.setText("");
        txtAddress.setText("");
    }

    public void setOnSaveListener(Consumer<Void> listener) {
        this.onSaveListener = listener;
    }

    public JTextField getTxtName() { return txtName; }
    public void setTxtName(JTextField txtName) { this.txtName = txtName; }
    public JTextField getTxtPhone() { return txtPhone; }
    public void setTxtPhone(JTextField txtPhone) { this.txtPhone = txtPhone; }
    public JTextField getTxtAge() { return txtAge; }
    public void setTxtAge(JTextField txtAge) { this.txtAge = txtAge; }
    public JTextField getTxtGender() { return txtGender; }
    public void setTxtGender(JTextField txtGender) { this.txtGender = txtGender; }
    public JTextField getTxtAddress() { return txtAddress; }
    public void setTxtAddress(JTextField txtAddress) { this.txtAddress = txtAddress; }
    public JButton getBtnModify() { return btnModify; }
    public void setBtnModify(JButton btnModify) { this.btnModify = btnModify; }
}