package view.admin;

import javax.swing.*;

import model.Drug;
import service.DrugService;

import java.awt.*;

public class AdminDrugEdit extends JPanel {

    private JTextField tfName, tfDescription, tfPrice, tfStock;
    private JLabel lblId;
    private JButton btnConf;

    public AdminDrugEdit() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Header =====
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(400, 40));
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        titlePanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Thông tin thuốc");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(new Color(0, 51, 255));

        lblId = new JLabel("Mã số: 001");
        lblId.setFont(new Font("Arial", Font.ITALIC, 12));

        titlePanel.add(lblTitle, BorderLayout.WEST);
        titlePanel.add(lblId, BorderLayout.EAST);
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

        btnConf = new JButton("Xác nhận");
        btnConf.setBackground(new Color(0, 200, 0));
        btnConf.setForeground(Color.WHITE);
        btnConf.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(btnConf);
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Thông tin thuốc");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 350);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new AdminDrugEdit());
        frame.setVisible(true);
    }

    public void loadData(int id) {
	    DrugService service = new DrugService();
	    Drug drug = service.getDrugDetail(id);

	    if (drug != null) {
	        lblId.setText("Mã số: " + drug.getId());
	        tfName.setText(drug.getName());
	        tfDescription.setText(drug.getDescription());
	        tfPrice.setText(String.valueOf(drug.getPrice()));
	        tfStock.setText(String.valueOf(drug.getStockQuantity()));
	    } else {
	        JOptionPane.showMessageDialog(this, "Không tìm thấy thuốc có ID = " + id, "Lỗi", JOptionPane.ERROR_MESSAGE);
	    }
    }
}
