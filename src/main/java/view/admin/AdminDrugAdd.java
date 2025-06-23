package view.admin;

import javax.swing.*;
import java.awt.*;

public class AdminDrugAdd extends JPanel {

    private JTextField tfName, tfDescription, tfPrice, tfStock;
    private JButton btnAdd;

    public AdminDrugAdd() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Header =====
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(400, 40));
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        titlePanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Thêm thuốc mới");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(new Color(0, 51, 255));

        titlePanel.add(lblTitle, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        // Center Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(500, 150));

        formPanel.add(new JLabel("Tên thuốc"));
        formPanel.add(new JLabel("Mô tả"));
        tfName = new JTextField();
        tfDescription = new JTextField();
        formPanel.add(tfName);
        formPanel.add(tfDescription);

        formPanel.add(new JLabel("Giá"));
        formPanel.add(new JLabel("Số lượng còn"));
        tfPrice = new JTextField();
        tfStock = new JTextField();
        formPanel.add(tfPrice);
        formPanel.add(tfStock);

        JPanel wrapperCenter = new JPanel(); // dùng wrapper để dễ đặt layout chính
        wrapperCenter.setLayout(new FlowLayout(FlowLayout.CENTER));
        wrapperCenter.setBackground(Color.WHITE);
        wrapperCenter.add(formPanel);
        add(wrapperCenter, BorderLayout.CENTER);

        // ===== Footer Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setPreferredSize(new Dimension(400, 40));
        buttonPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("Thêm thuốc");
        btnAdd.setBackground(new Color(0, 200, 0));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(btnAdd);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public JTextField getTfName() {
        return tfName;
    }

    public void setTfName(JTextField tfName) {
        this.tfName = tfName;
    }

    public JTextField getTfDescription() {
        return tfDescription;
    }

    public void setTfDescription(JTextField tfDescription) {
        this.tfDescription = tfDescription;
    }

    public JTextField getTfPrice() {
        return tfPrice;
    }

    public void setTfPrice(JTextField tfPrice) {
        this.tfPrice = tfPrice;
    }

    public JTextField getTfStock() {
        return tfStock;
    }

    public void setTfStock(JTextField tfStock) {
        this.tfStock = tfStock;
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public void setBtnAdd(JButton btnAdd) {
        this.btnAdd = btnAdd;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Thông tin thuốc");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 350);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new AdminDrugAdd());
        frame.setVisible(true);
    }
}
