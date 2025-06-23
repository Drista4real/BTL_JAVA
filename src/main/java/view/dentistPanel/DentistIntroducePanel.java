package view.dentistPanel;

import javax.swing.*;
import java.awt.*;

public class DentistIntroducePanel extends JPanel {

    private JLabel lblIntroTitle;
    private JLabel lblIntroContent;
    private JLabel lblPhone;
    private JLabel lblFacebook;
    private JLabel lblTikTok;
    private JLabel lblEmail;
    private JPanel contactInfoPanel;

    public DentistIntroducePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        //Tiêu đề trên cùng
        lblIntroTitle = new JLabel("   Giới thiệu về chúng tôi");
        lblIntroTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        lblIntroTitle.setForeground(new Color(51, 51, 255));
        lblIntroTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblIntroTitle.setPreferredSize(new Dimension(125, 25));
        add(lblIntroTitle, BorderLayout.PAGE_START);

        //Nội dung chính (ảnh)
        lblIntroContent = new JLabel();
        lblIntroContent.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/intro.png"));
            Image scaled = icon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
            lblIntroContent.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            lblIntroContent.setText("Không tìm thấy ảnh");
            lblIntroContent.setFont(new Font("Arial", Font.BOLD, 14));
        }
        add(lblIntroContent, BorderLayout.CENTER);

        // Panel chứa thông tin liên hệ
        contactInfoPanel = new JPanel();
        contactInfoPanel.setPreferredSize(new Dimension(100, 70));
        contactInfoPanel.setBackground(Color.WHITE);

        lblPhone = new JLabel("0980092876");
        lblFacebook = new JLabel("NhaKhoaLMK");
        lblTikTok = new JLabel("@nhakhoalmk");
        lblEmail = new JLabel("nhakhoalmk@gmail.com");

        setLabelIcon(lblPhone, "/img/phone.png", 18, 18);
        setLabelIcon(lblFacebook, "/img/face.png", 18, 18);
        setLabelIcon(lblTikTok, "/img/tik.png", 18, 18);
        setLabelIcon(lblEmail, "/img/gmail.png", 18, 18);

        // Layout cho contactInfoPanel
        GroupLayout layout = new GroupLayout(contactInfoPanel);
        contactInfoPanel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Layout ngang
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addGap(95);

        GroupLayout.ParallelGroup leftGroup = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
        leftGroup.addComponent(lblPhone, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE);
        leftGroup.addComponent(lblFacebook, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE);
        hGroup.addGroup(leftGroup);

        hGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE);

        GroupLayout.ParallelGroup rightGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        rightGroup.addComponent(lblTikTok, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE);
        rightGroup.addComponent(lblEmail, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE);
        hGroup.addGroup(rightGroup);
        hGroup.addGap(95);

        layout.setHorizontalGroup(hGroup);

        // Layout dọc
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGap(18);
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblPhone)
                .addComponent(lblTikTok));
        vGroup.addGap(18);
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblFacebook)
                .addComponent(lblEmail));

        layout.setVerticalGroup(vGroup);

        // Thêm khoảng cách phía dưới
        JPanel paddedBottomPanel = new JPanel(new BorderLayout());
        paddedBottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0)); // Cách đáy 30px
        paddedBottomPanel.add(contactInfoPanel, BorderLayout.CENTER);
        paddedBottomPanel.setBackground(Color.WHITE);

        add(paddedBottomPanel, BorderLayout.PAGE_END);
    }

    private void setLabelIcon(JLabel label, String iconPath, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
            label.setIconTextGap(8);
        } catch (Exception e) {
            System.err.println("Không tìm thấy icon: " + iconPath);
        }
    }

    // Getters và Setters
    public JLabel getLblIntroTitle() { return lblIntroTitle; }
    public void setLblIntroTitle(JLabel lblIntroTitle) { this.lblIntroTitle = lblIntroTitle; }
    public JLabel getLblIntroContent() { return lblIntroContent; }
    public void setLblIntroContent(JLabel lblIntroContent) { this.lblIntroContent = lblIntroContent; }
    public JLabel getLblPhone() { return lblPhone; }
    public void setLblPhone(JLabel lblPhone) { this.lblPhone = lblPhone; }
    public JLabel getLblFacebook() { return lblFacebook; }
    public void setLblFacebook(JLabel lblFacebook) { this.lblFacebook = lblFacebook; }
    public JLabel getLblTikTok() { return lblTikTok; }
    public void setLblTikTok(JLabel lblTikTok) { this.lblTikTok = lblTikTok; }
    public JLabel getLblEmail() { return lblEmail; }
    public void setLblEmail(JLabel lblEmail) { this.lblEmail = lblEmail; }
    public JPanel getContactInfoPanel() { return contactInfoPanel; }
    public void setContactInfoPanel(JPanel contactInfoPanel) { this.contactInfoPanel = contactInfoPanel; }
}
