package view.receptionistPanel;

import javax.swing.*;

import controller.receptionist.FollowupController;
import model.Doctor;
import model.Patient;
import reponsitory.DoctorRepository;
import reponsitory.Patientreponsitory;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class FollowupPanel extends JPanel {
    private int patientId;
    private JTextField txtName, txtPhone, txtBirthDate, txtIdCard, txtAdress;
    private JComboBox<String> genderCombo;
    private JComboBox<Doctor> doctorCombo;
    private JButton btnAdd, btnCancel;

    public FollowupPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Thêm Lịch Hẹn");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(10, 10, 300, 30);
        add(lblTitle);

        JLabel lblName = new JLabel("Họ và tên:");
        lblName.setFont(new Font("Arial", Font.ITALIC, 14));
        lblName.setBounds(30, 50, 100, 20);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(30, 70, 220, 30);
        txtName.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(txtName);

        JLabel lblPhone = new JLabel("Số điện thoại:");
        lblPhone.setFont(new Font("Arial", Font.ITALIC, 14));
        lblPhone.setBounds(280, 50, 100, 20);
        add(lblPhone);

        txtPhone = new JTextField();
        txtPhone.setBounds(280, 70, 220, 30);
        txtPhone.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(txtPhone);

        JLabel lblBirthDate = new JLabel("Ngày sinh (dd/MM/yyyy):");
        lblBirthDate.setFont(new Font("Arial", Font.ITALIC, 14));
        lblBirthDate.setBounds(30, 110, 200, 20);
        add(lblBirthDate);

        txtBirthDate = new JTextField();
        txtBirthDate.setBounds(30, 130, 220, 30);
        txtBirthDate.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(txtBirthDate);

        JLabel lblGender = new JLabel("Giới tính:");
        lblGender.setFont(new Font("Arial", Font.ITALIC, 14));
        lblGender.setBounds(280, 110, 100, 20);
        add(lblGender);

        genderCombo = new JComboBox<>(new String[]{"Nam", "Nữ"});
        genderCombo.setBounds(280, 130, 220, 30);
        add(genderCombo);

        JLabel lblIdCard = new JLabel("CCCD:");
        lblIdCard.setFont(new Font("Arial", Font.ITALIC, 14));
        lblIdCard.setBounds(30, 170, 100, 20);
        add(lblIdCard);

        txtIdCard = new JTextField();
        txtIdCard.setBounds(30, 190, 470, 30);
        txtIdCard.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(txtIdCard);

        JLabel lblAdress = new JLabel("Địa chỉ:");
        lblAdress.setFont(new Font("Arial", Font.ITALIC, 14));
        lblAdress.setBounds(30, 230, 100, 20);
        add(lblAdress);

        txtAdress = new JTextField();
        txtAdress.setBounds(30, 250, 470, 30);
        txtAdress.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(txtAdress);

        JLabel lblDoctor = new JLabel("Bác sĩ:");
        lblDoctor.setFont(new Font("Arial", Font.ITALIC, 14));
        lblDoctor.setBounds(30, 290, 100, 20);
        add(lblDoctor);

        doctorCombo = new JComboBox<>();
        doctorCombo.setBounds(30, 310, 470, 30);
        doctorCombo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(doctorCombo);

        loadDoctors();

        btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancel.setBackground(new Color(0, 123, 255));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setBounds(150, 370, 90, 35);
        add(btnCancel);

        btnAdd = new JButton("Thêm Lịch Hẹn");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBounds(260, 370, 240, 35);
        add(btnAdd);

        ActionListener followupController = new FollowupController(this);
        btnAdd.addActionListener(followupController);
    }

    public void clearFields() {
        txtName.setText("");
        txtPhone.setText("");
        txtBirthDate.setText("");
        txtAdress.setText("");
        doctorCombo.setSelectedIndex(-1);
    }

    // Getters
    public JTextField getTxtName() { return txtName; }
    public JTextField getTxtPhone() { return txtPhone; }
    public JTextField txtBirthDate() { return txtBirthDate; }
    public JComboBox<String> getGenderCombo() { return genderCombo; }
    public JTextField getTxtIdCard() { return txtIdCard; }
    public JTextField getTxtAdress() { return txtAdress; }
    public JComboBox<Doctor> getDoctorCombo() { return doctorCombo; }
    public JButton getBtnAddPatient() { return btnAdd; }
    public int getPatientId() { return patientId; }

    // Setters
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public void addBtnAddListener(ActionListener listener) {
        btnAdd.addActionListener(listener);
    }

    public void addBtnCancelListener(ActionListener listener) {
        btnCancel.addActionListener(listener);
    }

    public void dispose() {
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void loadDoctors() {
        try {
            List<Doctor> doctors = DoctorRepository.getAllDoctors();
            for (Doctor doctor : doctors) {
                doctorCombo.addItem(doctor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách bác sĩ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Doctor getSelectedDoctor() {
        return (Doctor) doctorCombo.getSelectedItem();
    }

    public void setSelectedDoctor(Doctor doctor) {
        doctorCombo.setSelectedItem(doctor);
    }

    public void loadPatientInfo(int patientId) {
        this.setPatientId(patientId);
        try {
            Patient patient = Patientreponsitory.getPatientById(patientId);
            if (patient != null) {
                txtName.setText(patient.getName());
                txtPhone.setText(patient.getPhoneNumber());

                Date birthDate = patient.getBirthDate();
                if (birthDate != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    txtBirthDate.setText(sdf.format(birthDate));
                }

                txtIdCard.setText(patient.getIdCard());
                txtAdress.setText(patient.getAddress());

                genderCombo.setSelectedItem(patient.getGender() == 1 ? "Nam" : "Nữ");
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin bệnh nhân với ID: " + patientId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin bệnh nhân.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
