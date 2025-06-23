package view.receptionistPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import reponsitory.DoctorRepository;

public class NewAppointmentPanel extends JPanel {

    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtAge;
    private JTextField txtGender;
    private JTextField txtAdress;
    private JComboBox<String> cmbDoctor;
    private JButton btnAddPatient;

    public NewAppointmentPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Lịch khám mới");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(10, 10, 200, 30);
        add(lblTitle);

        JLabel lblName = new JLabel("Họ và tên:");
        lblName.setFont(new Font("Arial", Font.ITALIC, 16));
        lblName.setBounds(50, 50, 100, 20);
        txtName = new JTextField();
        txtName.setBounds(50, 80, 250, 30);
        txtName.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(lblName);
        add(txtName);

        JLabel lblPhone = new JLabel("Số điện thoại:");
        lblPhone.setFont(new Font("Arial", Font.ITALIC, 16));
        lblPhone.setBounds(370, 50, 100, 20);
        txtPhone = new JTextField();
        txtPhone.setBounds(370, 80, 230, 30);
        txtPhone.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(lblPhone);
        add(txtPhone);

        JLabel lblAge = new JLabel("Tuổi:");
        lblAge.setFont(new Font("Arial", Font.ITALIC, 16));
        lblAge.setBounds(50, 130, 100, 20);
        txtAge =	new JTextField();
        txtAge.setBounds(50, 160, 250, 30);
        txtAge.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(lblAge);
        add(txtAge);

        JLabel lblGender = new JLabel("Giới tính:");
        lblGender.setFont(new Font("Arial", Font.ITALIC, 16));
        lblGender.setBounds(370, 130, 100, 20);
        txtGender = new JTextField();
        txtGender.setBounds(370, 160, 230, 30);
        txtGender.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(lblGender);
        add(txtGender);

        JLabel lblAdress = new JLabel("Địa chỉ:");
        lblAdress.setFont(new Font("Arial", Font.ITALIC, 16));
        lblAdress.setBounds(50, 210, 100, 20);
        txtAdress = new JTextField();
        txtAdress.setBounds(50, 240, 550, 30);
        txtAdress.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(lblAdress);
        add(txtAdress);

        JLabel lblDoctor = new JLabel("Bác sĩ:");
        lblDoctor.setFont(new Font("Arial", Font.ITALIC, 16));
        lblDoctor.setBounds(50, 290, 100, 20);

        // Lấy danh sách tên bác sĩ từ DoctorRepository
        DoctorRepository doctorRepository = new DoctorRepository();
        List<String> doctorNames;
        try {
            doctorNames = doctorRepository.getDoctorNames();
            doctorNames.add(0, "-- Chọn bác sĩ --"); // Thêm tùy chọn mặc định
        } catch (Exception e) {
            e.printStackTrace();
            doctorNames = List.of("-- Chọn bác sĩ --"); // Danh sách mặc định nếu có lỗi
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách bác sĩ: " + e.getMessage());
        }

        cmbDoctor = new JComboBox<>(doctorNames.toArray(new String[0]));
        cmbDoctor.setBounds(50, 320, 550, 30);
        cmbDoctor.setBackground(Color.WHITE);
        cmbDoctor.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbDoctor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(lblDoctor);
        add(cmbDoctor);

        btnAddPatient = new JButton("Thêm Lịch Hẹn");
        btnAddPatient.setFont(new Font("Arial", Font.BOLD, 14));
        btnAddPatient.setBackground(new Color(0, 123, 255));
        btnAddPatient.setForeground(Color.WHITE);
        btnAddPatient.setBounds(450, 370, 150, 40);
        btnAddPatient.setFocusPainted(false);
        add(btnAddPatient);

        btnAddPatient.addActionListener(e -> {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String age = txtAge.getText().trim();
            String gender = txtGender.getText().trim();
            String adress = txtAdress.getText().trim();
            String doctor = (String) cmbDoctor.getSelectedItem();

            if (name.isEmpty() || phone.isEmpty() || age.isEmpty() || gender.isEmpty() || adress.isEmpty()
                    || cmbDoctor.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            JOptionPane.showMessageDialog(this, "Thêm bệnh nhân thành công!");
            clearFields();
        });
    }

    private void clearFields() {
        txtName.setText("");
        txtPhone.setText("");
        txtAge.setText("");
        txtGender.setText("");
        txtAdress.setText("");
        cmbDoctor.setSelectedIndex(0);
    }

    // Getters và Setters
    public JTextField getTxtName() { return txtName; }
    public JTextField getTxtPhone() { return txtPhone; }
    public JTextField getTxtAge() { return txtAge; }
    public JTextField getTxtGender() { return txtGender; }
    public JTextField getTxtAdress() { return txtAdress; }
    public JComboBox<String> getCmbDoctor() { return cmbDoctor; }
    public JButton getBtnAddPatient() { return btnAddPatient; }
}