package view.admin;

import javax.swing.*;

import com.toedter.calendar.JDateChooser;

import reponsitory.EmployeeRepository;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AdminEmployeeEdit extends JPanel {
    private final JComboBox<String> cbRole;
    private final JComboBox<String> cbGender;
    private JTextField tfName, tfPhone, tfBirth,
            tfAddress, tfCCCD, tfSalary,
            tfUsername, tfPassword;
    private JLabel lblId;
    private JButton btnConf;
    private JDateChooser dateChooser;

    public AdminEmployeeEdit() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Panel tiêu đề
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(400, 40));
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        titlePanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Sửa thông tin nhân viên");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitle.setForeground(new Color(0, 51, 255));

        lblId = new JLabel("Mã số: 001");
        lblId.setFont(new Font("Arial", Font.ITALIC, 12));

        titlePanel.add(lblTitle, BorderLayout.WEST);
        titlePanel.add(lblId, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        // Form thông tin
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 5, 5));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(500, 350));

        tfName = new JTextField();
        tfPhone = new JTextField();
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd-MM-yyyy");
        Calendar today = Calendar.getInstance();

        // Tính ngày nhỏ nhất: hôm nay - 60 năm
        Calendar min = (Calendar) today.clone();
        min.add(Calendar.YEAR, -60);

        // Tính ngày lớn nhất: hôm nay - 18 năm
        Calendar max = (Calendar) today.clone();
        max.add(Calendar.YEAR, -18);

        // Đặt giới hạn
        dateChooser.setMinSelectableDate(min.getTime());
        dateChooser.setMaxSelectableDate(max.getTime());
        
        cbGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        tfAddress = new JTextField();
        tfCCCD = new JTextField();
        tfSalary = new JTextField();
        cbRole = new JComboBox<>(new String[]{"Lễ tân", "Bác sĩ", "Nhân viên quầy thuốc", "Quản lí"});
        tfUsername = new JTextField();
        tfPassword = new JTextField();

        formPanel.add(new JLabel("Họ và tên"));
        formPanel.add(new JLabel("Số điện thoại"));
        formPanel.add(tfName); formPanel.add(tfPhone);

        formPanel.add(new JLabel("Ngày sinh"));
        formPanel.add(new JLabel("Giới tính"));
        formPanel.add(dateChooser); formPanel.add(cbGender);

        formPanel.add(new JLabel("Địa chỉ"));
        formPanel.add(new JLabel("CCCD"));
        formPanel.add(tfAddress); formPanel.add(tfCCCD);

        formPanel.add(new JLabel("Hệ số lương"));
        formPanel.add(new JLabel("Chức vụ"));
        formPanel.add(tfSalary); formPanel.add(cbRole);

        formPanel.add(new JLabel("Tài khoản"));
        formPanel.add(new JLabel("Mật khẩu"));
        formPanel.add(tfUsername); formPanel.add(tfPassword);

        // Căn giữa form
        JPanel wrapperCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapperCenter.setBackground(Color.WHITE);
        wrapperCenter.add(formPanel);
        add(wrapperCenter, BorderLayout.CENTER);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setPreferredSize(new Dimension(400, 40));
        buttonPanel.setBackground(Color.WHITE);

        btnConf = new JButton("Xác nhận");
        btnConf.setBackground(Color.GREEN);
        btnConf.setForeground(Color.WHITE);
        btnConf.setFont(new Font("Arial", Font.BOLD, 12));

        buttonPanel.add(btnConf);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public JComboBox<String> getCbRole() {
        return cbRole;
    }

    public JComboBox<String> getCbGender() {
        return cbGender;
    }

    public JTextField getTfName() {
        return tfName;
    }

    public void setTfName(JTextField tfName) {
        this.tfName = tfName;
    }

    public JTextField getTfPhone() {
        return tfPhone;
    }

    public void setTfPhone(JTextField tfPhone) {
        this.tfPhone = tfPhone;
    }

    public JTextField getTfBirth() {
        return tfBirth;
    }

    public void setTfBirth(JTextField tfBirth) {
        this.tfBirth = tfBirth;
    }

    public JTextField getTfAddress() {
        return tfAddress;
    }

    public void setTfAddress(JTextField tfAddress) {
        this.tfAddress = tfAddress;
    }

    public JTextField getTfCCCD() {
        return tfCCCD;
    }

    public void setTfCCCD(JTextField tfCCCD) {
        this.tfCCCD = tfCCCD;
    }

    public JTextField getTfSalary() {
        return tfSalary;
    }

    public void setTfSalary(JTextField tfSalary) {
        this.tfSalary = tfSalary;
    }

    public JTextField getTfUsername() {
        return tfUsername;
    }

    public void setTfUsername(JTextField tfUsername) {
        this.tfUsername = tfUsername;
    }

    public JTextField getTfPassword() {
        return tfPassword;
    }

    public void setTfPassword(JTextField tfPassword) {
        this.tfPassword = tfPassword;
    }

    public JLabel getLblId() {
        return lblId;
    }

    public void setLblId(JLabel lblId) {
        this.lblId = lblId;
    }

    public JButton getBtnConf() {
        return btnConf;
    }

    public void setBtnConf(JButton btnConf) {
        this.btnConf = btnConf;
    }

    
    public JDateChooser getDateChooser() {
		return dateChooser;
	}

	public void setDateChooser(JDateChooser dateChooser) {
		this.dateChooser = dateChooser;
	}

	public void loadData(int id) throws ParseException {
        Object[] emp = EmployeeRepository.findById(id);
        if (emp != null) {
            lblId.setText("Mã số: " + emp[0]);
            tfName.setText((String) emp[1]);
            tfPhone.setText((String) emp[2]);
//            tfBirth.setText((String) emp[3]);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse((String) emp[3]);
            // Set vào JDateChooser
            dateChooser.setDate(date);
            cbGender.setSelectedItem(emp[4]);
            tfAddress.setText((String) emp[5]);
            tfCCCD.setText((String) emp[6]);
            tfSalary.setText(String.valueOf(emp[7]));
            cbRole.setSelectedItem(emp[8]);
            tfUsername.setText((String) emp[9]);
            tfPassword.setText((String) emp[10]);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên có ID = " + id,
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Sửa thông tin nhân viên");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new AdminEmployeeEdit());
        frame.setVisible(true);
    }
}
