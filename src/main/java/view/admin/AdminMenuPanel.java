package view.admin;

import javax.swing.*;
import java.awt.*;

public class AdminMenuPanel extends JPanel {

    private JLabel lblHome;
    private JLabel lblPatient;
    private JLabel lblEmp;
    private JLabel lblDrugs;
    private JLabel lblService;
    private JLabel lblStatistic;

    public AdminMenuPanel() {
        initComponents();
    }

    private void initComponents() {
        // Cài đặt kích thước panel
        setPreferredSize(new Dimension(200, 450));
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        // Cài đặt constraints mặc định
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;

        // Logo
        lblHome = new JLabel();
        lblHome.setOpaque(true);
        lblHome.setBackground(Color.WHITE);
        lblHome.setHorizontalAlignment(SwingConstants.CENTER);
        lblHome.setPreferredSize(new Dimension(175, 131));
        lblHome.setMinimumSize(new Dimension(175, 131));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/iconhome.png"));
            Image scaledImage = icon.getImage().getScaledInstance(150, 121, Image.SCALE_SMOOTH);
            lblHome.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            lblHome.setText("Trang chủ");
            lblHome.setFont(new Font("Arial", Font.BOLD, 14));
        }

        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(lblHome, gbc);

        // Reset lại cho các label tiếp theo
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        //Các menu label
        lblPatient = createMenuLabel("DS bệnh nhân", 1, gbc,"/img/group.png");
        lblPatient.setOpaque(true);
        lblPatient.setBackground(Color.WHITE);

        lblEmp = createMenuLabel("DS nhân viên", 2, gbc,"/img/group.png");
        lblEmp.setOpaque(true);
        lblEmp.setBackground(Color.WHITE);

        lblDrugs = createMenuLabel("DS thuốc", 3, gbc,"/img/medicine.png");
        lblDrugs.setOpaque(true);
        lblDrugs.setBackground(Color.WHITE);

        lblService = createMenuLabel("DS dịch vụ", 4, gbc,"/img/list.png");
        lblService.setOpaque(true);
        lblService.setBackground(Color.WHITE);

        //thêm thống kê
        lblStatistic = createMenuLabel("Thống kê", 5, gbc, "/img/statistics.png");
        lblStatistic.setOpaque(true);
        lblStatistic.setBackground(Color.WHITE);

        // Thêm 1 thành phần đẩy nội dung lên trên (tránh dồn xuống giữa)
        gbc.gridy = 5;
        gbc.weighty = 1;
        add(Box.createVerticalGlue(), gbc);
    }

    private JLabel createMenuLabel(String text, int row, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setOpaque(false);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setPreferredSize(new Dimension(180, 30));

        gbc.gridy = row;
        gbc.weighty = 0;
        add(label, gbc);

        return label;
    }
    private JLabel createMenuLabel(String text, int row, GridBagConstraints gbc, String iconPath) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setOpaque(false);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setPreferredSize(new Dimension(180, 30));

        // Load icon nếu có
        if (iconPath != null) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                Image scaled = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH); // ảnh vuông
                label.setIcon(new ImageIcon(scaled));
                label.setIconTextGap(10); // khoảng cách giữa icon và chữ
            } catch (Exception e) {
                System.err.println("Không load được icon: " + iconPath);
            }
        }

        gbc.gridy = row;
        gbc.weighty = 0;
        add(label, gbc);

        return label;
    }

    public JLabel getLblHome() {
        return lblHome;
    }

    public void setLblHome(JLabel lblHome) {
        this.lblHome = lblHome;
    }

    public JLabel getLblPatient() {
        return lblPatient;
    }

    public void setLblPatient(JLabel lblPatient) {
        this.lblPatient = lblPatient;
    }

    public JLabel getLblEmp() {
        return lblEmp;
    }

    public void setLblEmp(JLabel lblEmp) {
        this.lblEmp = lblEmp;
    }

    public JLabel getLblDrugs() {
        return lblDrugs;
    }

    public void setLblDrugs(JLabel lblDrugs) {
        this.lblDrugs = lblDrugs;
    }

    public JLabel getLblService() {
        return lblService;
    }

    public void setLblService(JLabel lblService) {
        this.lblService = lblService;
    }

    public JLabel getLblStatistic() {
        return lblStatistic;
    }

    public void setLblStatistic(JLabel lblStatistic) {
        this.lblStatistic = lblStatistic;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.getContentPane().add(new AdminMenuPanel());
    }
}

