package view.admin;

import javax.swing.*;

import reponsitory.EmployeeRepository;

import java.awt.*;

public class AdminEmployeeInfo extends JPanel {
	private int employeeId;
    private JTextField tfName, tfPhone, tfBirth,
            tfAddress, tfCCCD, tfSalary, tfRole,
            tfUsername, tfPassword, tfGender;
    private JLabel lblId;
    private JButton btnDelete, btnEdit;

    public AdminEmployeeInfo() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Panel tiêu đề
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(400, 40));
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        titlePanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Thông tin nhân viên");
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

        tfName = new JTextField(); tfPhone = new JTextField();
        tfBirth = new JTextField(); tfGender = new JTextField();
        tfAddress = new JTextField(); tfCCCD = new JTextField();
        tfSalary = new JTextField(); tfRole = new JTextField();
        tfUsername = new JTextField(); tfPassword = new JTextField();

        formPanel.add(new JLabel("Họ và tên"));
        formPanel.add(new JLabel("Số điện thoại"));
        formPanel.add(tfName); formPanel.add(tfPhone);

        formPanel.add(new JLabel("Ngày sinh"));
        formPanel.add(new JLabel("Giới tính"));
        formPanel.add(tfBirth); formPanel.add(tfGender);

        formPanel.add(new JLabel("Địa chỉ"));
        formPanel.add(new JLabel("CCCD"));
        formPanel.add(tfAddress); formPanel.add(tfCCCD);

        formPanel.add(new JLabel("Hệ số lương"));
        formPanel.add(new JLabel("Chức vụ"));
        formPanel.add(tfSalary); formPanel.add(tfRole);

        formPanel.add(new JLabel("Tài khoản"));
        formPanel.add(new JLabel("Mật khẩu"));
        formPanel.add(tfUsername); formPanel.add(tfPassword);

        // Căn giữa
        JPanel wrapperCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapperCenter.setBackground(Color.WHITE);
        wrapperCenter.add(formPanel);
        add(wrapperCenter, BorderLayout.CENTER);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setPreferredSize(new Dimension(400, 40));
        buttonPanel.setBackground(Color.WHITE);

        btnDelete = new JButton("Xóa");
        btnDelete.setBackground(new Color(0, 255, 0));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Arial", Font.BOLD, 12));

        btnEdit = new JButton("Chỉnh sửa");
        btnEdit.setBackground(new Color(0, 255, 0));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font("Arial", Font.BOLD, 12));

        buttonPanel.add(btnDelete);
        buttonPanel.add(btnEdit);
        add(buttonPanel, BorderLayout.SOUTH);
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

    public JTextField getTfRole() {
        return tfRole;
    }

    public void setTfRole(JTextField tfRole) {
        this.tfRole = tfRole;
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

    public JTextField getCbGender() {
        return tfGender;
    }

    public void setCbGender(JTextField tfGender) {
        this.tfGender = tfGender;
    }

    public JLabel getLblId() {
        return lblId;
    }

    public void setLblId(JLabel lblId) {
        this.lblId = lblId;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public void setBtnDelete(JButton btnDelete) {
        this.btnDelete = btnDelete;
    }

    public JButton getBtnEdit() {
        return btnEdit;
    }

    public void setBtnEdit(JButton btnEdit) {
        this.btnEdit = btnEdit;
    }

    public int getEmployeeId() {
    	return this.employeeId;
    }
    
    public void setEmployeeID(int x) {
    	this.employeeId=x;
    }
    
    public void loadData(int id) {
    	this.employeeId=id;
        Object[] emp = EmployeeRepository.findById(id);
        if (emp != null) {
            lblId.setText("Mã số: " + emp[0]);
            tfName.setText((String) emp[1]);
            tfPhone.setText((String) emp[2]);
            tfBirth.setText((String) emp[3]);
            tfGender.setText((String) emp[4]);
            tfAddress.setText((String) emp[5]);
            tfCCCD.setText((String) emp[6]);
            tfSalary.setText(String.valueOf(emp[7]));
            tfRole.setText((String) emp[8]);
            tfUsername.setText((String) emp[9]);
            tfPassword.setText((String) emp[10]);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên có ID = " + id,
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Thông tin nhân viên");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new AdminEmployeeInfo());
        frame.setVisible(true);
    }
}
