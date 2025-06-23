package view.receptionistPanel;

import javax.swing.*;
import java.awt.*;

public class ReceptionistMenuPanel extends JPanel {

    private JLabel lblHome;
    private JLabel lblSchedule;
    private JLabel lblPatients;
    private JLabel lblAddPatient;
    private JLabel lblFeature2;

    public ReceptionistMenuPanel() {
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
//        lblSchedule.setOpaque(true);
        lblPatients = createMenuLabel("Xem bệnh nhân", 1, gbc,"/img/icontooth.png");
        lblAddPatient = createMenuLabel("Thêm bệnh nhân", 2, gbc,"/img/add.png");
        lblSchedule = createMenuLabel("Xem lịch khám", 3, gbc,"/img/calendar.png");
        lblFeature2 = createMenuLabel("Tính năng khác", 4, gbc,"/img/see.png");

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

    public JLabel getLblSchedule() {
        return lblSchedule;
    }

    public void setLblSchedule(JLabel lblSchedule) {
        this.lblSchedule = lblSchedule;
    }

    public JLabel getLblPatients() {
        return lblPatients;
    }

    public void setLblPatients(JLabel lblPatients) {
        this.lblPatients = lblPatients;
    }

    public JLabel getLblFeature1() {
        return lblAddPatient;
    }

    public void setLblFeature1(JLabel lblAddPatient) {
        this.lblAddPatient = lblAddPatient;
    }

    public JLabel getLblFeature2() {
        return lblFeature2;
    }

    public void setLblFeature2(JLabel lblFeature2) {
        this.lblFeature2 = lblFeature2;
    }
}
