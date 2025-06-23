package view.admin;

import javax.swing.*;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class AdminEmployeeAdd extends JPanel {
	private JTextField tfName, tfPhone, tfBirth, tfAddress, tfCCCD, tfSalary, tfUsername, tfPassword, tfYear;
	private JComboBox<String> cbRole;
	private JComboBox<String> cbGender;
	private JLabel lblId;
	private JButton btnAdd;
	private JDateChooser dateChooser;

	public AdminEmployeeAdd() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		// Panel tiêu đề
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setPreferredSize(new Dimension(400, 40));
		titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		titlePanel.setBackground(Color.WHITE);

		JLabel lblTitle = new JLabel("Sửa thông tin nhân viên");
		lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
		lblTitle.setForeground(new Color(0, 51, 255));

		lblId = new JLabel("Mã số: 001");
		lblId.setFont(new Font("Arial", Font.ITALIC, 12));

		titlePanel.add(lblTitle, BorderLayout.WEST);
		titlePanel.add(lblId, BorderLayout.EAST);
		add(titlePanel, BorderLayout.NORTH);

		// Form thông tin
		JPanel formPanel = new JPanel(new GridLayout(12, 2, 5, 5));
		formPanel.setBackground(Color.WHITE);
		formPanel.setPreferredSize(new Dimension(500, 400));

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

		cbGender = new JComboBox<>(new String[] { "Nam", "Nữ" });
		tfAddress = new JTextField();
		tfCCCD = new JTextField();
		tfSalary = new JTextField();
		cbRole = new JComboBox<>(new String[] { "Lễ tân", "Bác sĩ", "Nhân viên quầy thuốc", "Admin" });
		tfUsername = new JTextField();
		tfPassword = new JTextField();
		tfYear = new JTextField();

		btnAdd = new JButton("Thêm nhân viên");
		btnAdd.setBackground(new Color(51, 255, 51));
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
		btnAdd.setFocusPainted(false);

		formPanel.add(new JLabel("Họ và tên"));
		formPanel.add(new JLabel("Số điện thoại"));
		formPanel.add(tfName);
		formPanel.add(tfPhone);

		formPanel.add(new JLabel("Ngày sinh"));
		formPanel.add(new JLabel("Giới tính"));
		formPanel.add(dateChooser);
		formPanel.add(cbGender); // Dùng JComboBox ở đây

		formPanel.add(new JLabel("Địa chỉ"));
		formPanel.add(new JLabel("CCCD"));
		formPanel.add(tfAddress);
		formPanel.add(tfCCCD);

		formPanel.add(new JLabel("Hệ số lương"));
		formPanel.add(new JLabel("Chức vụ"));
		formPanel.add(tfSalary);
		formPanel.add(cbRole);

		formPanel.add(new JLabel("Tài khoản"));
		formPanel.add(new JLabel("Mật khẩu"));
		formPanel.add(tfUsername);
		formPanel.add(tfPassword);

		formPanel.add(new JLabel("Số năm kinh nghiệm"));
		formPanel.add(new JLabel("Thêm nhân viên"));
		formPanel.add(tfYear);
		formPanel.add(btnAdd);

		// Đưa form ra giữa
		JPanel wrapperCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
		wrapperCenter.setBackground(Color.WHITE);
		wrapperCenter.add(formPanel);
		add(wrapperCenter, BorderLayout.CENTER);
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

	public JTextField getTfYear() {
		return tfYear;
	}

	public void setTfYear(JTextField tfYear) {
		this.tfYear = tfYear;
	}

	public JComboBox<String> getCbRole() {
		return cbRole;
	}

	public void setCbRole(JComboBox<String> cbRole) {
		this.cbRole = cbRole;
	}

	public JComboBox<String> getCbGender() {
		return cbGender;
	}

	public void setCbGender(JComboBox<String> cbGender) {
		this.cbGender = cbGender;
	}

	public JLabel getLblId() {
		return lblId;
	}

	public void setLblId(JLabel lblId) {
		this.lblId = lblId;
	}

	public JButton getBtnAdd() {
		return btnAdd;
	}

	public void setBtnAdd(JButton btnAdd) {
		this.btnAdd = btnAdd;
	}

	public JDateChooser getDateChooser() {
		return dateChooser;
	}

	public void setDateChooser(JDateChooser dateChooser) {
		this.dateChooser = dateChooser;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Test AdminEmployeeAdd");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(new AdminEmployeeAdd());
		frame.setVisible(true);
	}

}
