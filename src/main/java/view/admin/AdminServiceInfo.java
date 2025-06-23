package view.admin;

import javax.swing.*;
import java.awt.*;

public class AdminServiceInfo extends JPanel {

    private JTextField tfName, tfPrice;
    private JLabel lblId;
    private JButton btnDelete, btnEdit;

    public AdminServiceInfo() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Header =====
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(400, 40));
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        titlePanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Thông tin dịch vụ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(new Color(0, 51, 255));

        lblId = new JLabel("Mã số: 001");
        lblId.setFont(new Font("Arial", Font.ITALIC, 12));

        titlePanel.add(lblTitle, BorderLayout.WEST);
        titlePanel.add(lblId, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        // Center Form
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(500, 100));

        formPanel.add(new JLabel("Tên dịch vụ"));
        formPanel.add(new JLabel("Giá"));
        tfName = new JTextField();
        formPanel.add(tfName);
        tfPrice = new JTextField();
        formPanel.add(tfPrice);

        JPanel wrapperCenter = new JPanel(); // dùng wrapper để dễ đặt layout chính
        wrapperCenter.setLayout(new FlowLayout(FlowLayout.CENTER));
        wrapperCenter.setBackground(Color.WHITE);
        wrapperCenter.add(formPanel);
        add(wrapperCenter, BorderLayout.CENTER);

        // ===== Footer Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setPreferredSize(new Dimension(400, 40));
        buttonPanel.setBackground(Color.WHITE);

        btnDelete = new JButton("Xóa");
        btnDelete.setBackground(new Color(0, 200, 0));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Arial", Font.BOLD, 12));

        btnEdit = new JButton("Chỉnh sửa");
        btnEdit.setBackground(new Color(0, 200, 0));
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

    public JTextField getTfPrice() {
        return tfPrice;
    }

    public void setTfPrice(JTextField tfPrice) {
        this.tfPrice = tfPrice;
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Thông tin thuốc");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 350);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new AdminServiceInfo());
        frame.setVisible(true);
    }
}
