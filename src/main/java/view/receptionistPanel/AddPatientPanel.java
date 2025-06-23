package view.receptionistPanel;

import java.util.Calendar;
import java.util.List;
import model.Doctor;
import reponsitory.DoctorRepository;
import javax.swing.*;

import com.toedter.calendar.JDateChooser;

import controller.receptionist.AddPatientController;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionListener;

public class AddPatientPanel extends JPanel {

    private JTextField txtName;
    private JTextField txtPhone;
//    private JTextField txtBirthDate;
    private JComboBox<String> genderCombo;
    private JTextField txtIdCard;
    private JTextField txtAdress;
    private JComboBox<Doctor> doctorCombo;
    private JDateChooser dateChooser;
    private JButton btnAdd, btnCancel;
    private JPanel contentPanel;

    // Kích thước mặc định ban đầu
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 450;

    public AddPatientPanel() {
        initComponents();
    }

    private void initComponents() {
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout()); // Sử dụng GridBagLayout để căn giữa

        // Panel chứa nội dung với GroupLayout
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        GroupLayout layout = new GroupLayout(contentPanel);
        contentPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel lblTitle = new JLabel("Thêm Bệnh Nhân");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18)); // Giảm font từ 20

        JLabel lblName = new JLabel("Họ và tên:");
        lblName.setFont(new Font("Arial", Font.ITALIC, 14)); // Giảm font từ 16
        txtName = new JTextField();
        txtName.setPreferredSize(new Dimension(180, 35)); // Giảm từ 220, giảm chiều cao

        JLabel lblPhone = new JLabel("Số điện thoại:");
        lblPhone.setFont(new Font("Arial", Font.ITALIC, 14));
        txtPhone = new JTextField();
        txtPhone.setPreferredSize(new Dimension(180, 35));

        JLabel lblBirthDate = new JLabel("Ngày sinh:");
        lblBirthDate.setFont(new Font("Arial", Font.ITALIC, 14));
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd-MM-yyyy");
        Calendar today = Calendar.getInstance();

        // Tính ngày nhỏ nhất: hôm nay - 60 năm
        Calendar min = (Calendar) today.clone();
        min.add(Calendar.YEAR, -60);

        // Tính ngày lớn nhất: hôm nay - 18 năm
        Calendar max = (Calendar) today.clone();
        max.add(Calendar.YEAR, -16);
     // Đặt giới hạn
        dateChooser.setMinSelectableDate(min.getTime());
        dateChooser.setMaxSelectableDate(max.getTime());
        
        
        JLabel lblGender = new JLabel("Giới tính:");
        lblGender.setFont(new Font("Arial", Font.ITALIC, 14));
        genderCombo = new JComboBox<>(new String[]{"Nữ", "Nam"});
        genderCombo.setPreferredSize(new Dimension(180, 35));

        JLabel lblIdCard = new JLabel("CCCD:");
        lblIdCard.setFont(new Font("Arial", Font.ITALIC, 14));
        txtIdCard = new JTextField();
        txtIdCard.setPreferredSize(new Dimension(0, 35));

        JLabel lblAdress = new JLabel("Địa chỉ:");
        lblAdress.setFont(new Font("Arial", Font.ITALIC, 14));
        txtAdress = new JTextField();
        txtAdress.setPreferredSize(new Dimension(0, 35));

        JLabel lblDoctor = new JLabel("Bác sĩ:");
        lblDoctor.setFont(new Font("Arial", Font.ITALIC, 14));
        doctorCombo = new JComboBox<>();
        doctorCombo.setPreferredSize(new Dimension(0, 35));

        btnAdd = new JButton("Thêm Bệnh Nhân và Lịch Hẹn");
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setPreferredSize(new Dimension(200, 35)); // Giữ nút đủ lớn

        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(0, 123, 255));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(80, 35)); // Giữ nút đủ lớn

        // Load danh sách bác sĩ
        loadDoctors();

        // Thiết lập layout ngang & dọc cho contentPanel
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(lblTitle)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(lblName)
                        .addComponent(txtName, 180, 180, 180) // Đồng bộ với preferredSize
                        .addComponent(lblBirthDate)
                        .addComponent(dateChooser, 180, 180, 180))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(lblPhone)
                        .addComponent(txtPhone, 180, 180, 180)
                        .addComponent(lblGender)
                        .addComponent(genderCombo, 180, 180, 180)))
                .addComponent(lblIdCard)
                .addComponent(txtIdCard, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblAdress)
                .addComponent(txtAdress, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDoctor)
                .addComponent(doctorCombo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(lblTitle)
                .addGap(8) // Giảm khoảng cách từ 10
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(lblPhone))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPhone, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBirthDate)
                    .addComponent(lblGender))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                    .addComponent(genderCombo, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                .addComponent(lblIdCard)
                .addComponent(txtIdCard, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblAdress)
                .addComponent(txtAdress, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblDoctor)
                .addComponent(doctorCombo, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                .addGap(15) // Giảm khoảng cách từ 20
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
        );

        // Đặt kích thước mặc định cho contentPanel
        contentPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        // Thêm contentPanel vào panel chính với GridBagConstraints để căn giữa
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc

.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(contentPanel, gbc);

        // Lắng nghe sự kiện thay đổi kích thước cửa sổ
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustLayout();
            }
        });

        ActionListener addPatientController = new AddPatientController(this);
        btnAdd.addActionListener(addPatientController);
    }

    private void adjustLayout() {
        Dimension parentSize = getSize();
        int contentWidth = Math.min(DEFAULT_WIDTH, parentSize.width - 20); // Giảm padding từ 40
        int contentHeight = Math.min(DEFAULT_HEIGHT, parentSize.height - 20);
        contentPanel.setPreferredSize(new Dimension(contentWidth, contentHeight));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void clearFields() {
        txtName.setText("");
        txtPhone.setText("");
        txtAdress.setText("");
        doctorCombo.setSelectedIndex(-1);
    }

    public JTextField getTxtName() { return txtName; }
    public JTextField getTxtPhone() { return txtPhone; }
    public JDateChooser getDateChooser() {
		return dateChooser;
	}

	public void setDateChooser(JDateChooser dateChooser) {
		this.dateChooser = dateChooser;
	}
	public JComboBox<String> getGenderCombo() { return genderCombo; }
    public void setGenderCombo(JComboBox<String> genderCombo) { this.genderCombo = genderCombo; }
    public JTextField getTxtIdCard() { return txtIdCard; }
    public JTextField getTxtAdress() { return txtAdress; }
    public JComboBox<Doctor> getDoctorCombo() { return doctorCombo; }
    public JButton getBtnAddPatient() { return btnAdd; }

    public void addBtnAddListener(ActionListener listener) {
        btnAdd.addActionListener(listener);
    }

    public void addBtnCancelListener(ActionListener listener) {
        btnCancel.addActionListener(listener);
    }

    public void dispose() {
        // Placeholder if needed
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
}