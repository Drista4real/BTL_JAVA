package view.dentistPanel;

import Utils.CustomDocumentFilter;
import reponsitory.DrugReponsitory;
import model.Drug;
import model.DrugDose;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;

public class AddPrescriptionPanel extends JPanel {

    private JLabel lblIntroTitle;
    private JLabel lblIntroContent;
    private JLabel lblPhone;
    private JLabel lblFacebook;
    private JLabel lblTikTok;
    private JLabel lblEmail;
    private JPanel contactInfoPanel;
    private JButton btnConfirm;
    private JButton btnAddDrugs;
    private JComboBox<String> comboMedicinename;
    private JTextField txtMorning;
    private JTextField txtNoon;
    private JTextField txtAfternoon;
    private JTextField txtQuantity;
    private List<DrugDose> listDrugDose=new ArrayList<>();
    private JTable serviceTable;

    public AddPrescriptionPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Tiêu đề ---
        lblIntroTitle = new JLabel("  Thêm đơn thuốc");
        lblIntroTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblIntroTitle.setPreferredSize(new Dimension(200, 30));
        add(lblIntroTitle, BorderLayout.PAGE_START);

        // --- Panel chính chứa form và bảng ---
        JPanel servicePanel = new JPanel(new BorderLayout());

        // --- Form bên trái ---
        JPanel formPanel = new JPanel(null);  // dùng layout tự do để dễ căn chỉnh
        formPanel.setPreferredSize(new Dimension(230, 260));
        formPanel.setBackground(Color.WHITE);

        JLabel lblMedicinename = new JLabel("Tên thuốc:");
        lblMedicinename.setBounds(10, 25, 100, 25);
        lblMedicinename.setFont(new Font("Arial", Font.ITALIC, 16));

        //JCombo Box nhập tên thuốc
        String[] medicineList = {"Test1", "Test2", "Test3", "Test4"};
        comboMedicinename = new JComboBox<>(medicineList);
        comboMedicinename.setBounds(10, 50, 200, 35);
        comboMedicinename.setFont(new Font("Arial", Font.PLAIN, 14));
        comboMedicinename.setBackground(Color.WHITE);

        comboMedicinename.removeAllItems();
        List<Drug> listDrug = DrugReponsitory.getListDrug();
        for (Drug service : listDrug) {
            comboMedicinename.addItem(service.getName());
        }

        txtQuantity = new JTextField();
        
        JLabel lblMorning = new JLabel("Sáng:");
        lblMorning.setBounds(10, 100, 50, 25);
        lblMorning.setFont(new Font("Arial", Font.ITALIC, 14));
        txtMorning = new JTextField();
        txtMorning.setBounds(10, 125, 40, 35);
        txtMorning.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ((AbstractDocument) txtMorning.getDocument()).setDocumentFilter(new CustomDocumentFilter("[0-2]"));

        JLabel lblNoon = new JLabel("Trưa:");
        lblNoon.setBounds(80, 100, 50, 25);
        lblNoon.setFont(new Font("Arial", Font.ITALIC, 14));
        txtNoon = new JTextField();
        txtNoon.setBounds(80, 125, 40, 35);
        txtNoon.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ((AbstractDocument) txtNoon.getDocument()).setDocumentFilter(new CustomDocumentFilter("[0-2]"));

        JLabel lblAfternoon = new JLabel("Chiều:");
        lblAfternoon.setBounds(150, 100, 50, 25);
        lblAfternoon.setFont(new Font("Arial", Font.ITALIC, 14));
        txtAfternoon = new JTextField();
        txtAfternoon.setBounds(150, 125, 40, 35);
        txtAfternoon.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ((AbstractDocument) txtAfternoon.getDocument()).setDocumentFilter(new CustomDocumentFilter("[0-2]"));

        formPanel.add(lblMorning);
        formPanel.add(txtMorning);
        formPanel.add(lblNoon);
        formPanel.add(txtNoon);
        formPanel.add(lblAfternoon);
        formPanel.add(txtAfternoon);

        JLabel lblQuantity = new JLabel("Số lượng:");
        lblQuantity.setBounds(10, 170, 100, 25);
        lblQuantity.setFont(new Font("Arial", Font.ITALIC, 16));
        txtQuantity.setBounds(10, 195, 200, 35);
        txtQuantity.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        txtQuantity.setEditable(false);

        btnConfirm = new JButton("Xác nhận");
        btnConfirm.setBounds(10, 245, 100, 30);
        btnConfirm.setBackground(new Color(0, 123, 255));
        btnConfirm.setForeground(Color.WHITE);

        // Gắn listener
        txtMorning.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateQuantity(); }
            public void removeUpdate(DocumentEvent e) { calculateQuantity(); }
            public void changedUpdate(DocumentEvent e) { calculateQuantity(); }
        });

        txtNoon.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateQuantity(); }
            public void removeUpdate(DocumentEvent e) { calculateQuantity(); }
            public void changedUpdate(DocumentEvent e) { calculateQuantity(); }
        });

        txtAfternoon.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateQuantity(); }
            public void removeUpdate(DocumentEvent e) { calculateQuantity(); }
            public void changedUpdate(DocumentEvent e) { calculateQuantity(); }
        });

        formPanel.add(lblMedicinename);
        formPanel.add(comboMedicinename);
        formPanel.add(lblQuantity);
        formPanel.add(txtQuantity);
        formPanel.add(btnConfirm);

        // --- Bảng bên phải ---
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"STT", "Tên thuốc", "Số lượng", "Xóa"}, 0
        );
        serviceTable = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serviceTable.setRowHeight(20);
        serviceTable.setBackground(Color.WHITE);
        serviceTable.setForeground(Color.BLACK);
        serviceTable.setSelectionBackground(new Color(200, 230, 255));
        serviceTable.setGridColor(Color.LIGHT_GRAY);

        // Xóa viền ngoài của bảng
        serviceTable.setBorder(null);

        // Header
        JTableHeader header = serviceTable.getTableHeader();
        header.setBackground(new Color(0, 102, 204));
        header.setForeground(Color.white);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane tableScroll = new JScrollPane(serviceTable);
        tableScroll.setPreferredSize(new Dimension(500, 100)); // <= Giới hạn chiều cao
        tableScroll.getViewport().setBackground(Color.WHITE);  // Nền vùng cuộn
        // Tạo renderer căn giữa
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Áp dụng renderer cho các cột: STT (0), Số lượng (2), Xóa (3)
        serviceTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        serviceTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        serviceTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        serviceTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); 
        serviceTable.getColumnModel().getColumn(0).setPreferredWidth(40); 
        serviceTable.getColumnModel().getColumn(1).setPreferredWidth(100); 
        serviceTable.getColumnModel().getColumn(2).setPreferredWidth(140);  
        serviceTable.getColumnModel().getColumn(3).setPreferredWidth(40);  
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        // --- Kết hợp form và bảng ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(formPanel, BorderLayout.WEST);
        bottomPanel.add(tableScroll, BorderLayout.CENTER);

        // --- Nút thêm danh sách thuốc ---
        btnAddDrugs = new JButton("Xuất đơn thuốc");
        btnAddDrugs.setBackground(Color.GREEN);
        btnAddDrugs.setForeground(Color.WHITE);
        btnAddDrugs.setFocusPainted(false);
        btnAddDrugs.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel drugPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        drugPanel.add(btnAddDrugs);

        // --- Gắn các phần vào servicePanel ---
        servicePanel.add(bottomPanel, BorderLayout.CENTER);
        servicePanel.add(drugPanel, BorderLayout.SOUTH);

        // --- Thêm vào giao diện chính ---
        add(servicePanel, BorderLayout.CENTER);
        
        servicePanel.setBackground(Color.WHITE);
        bottomPanel.setBackground(Color.WHITE);
        drugPanel.setBackground(Color.WHITE);
        
        serviceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = serviceTable.rowAtPoint(e.getPoint());
                int col = serviceTable.columnAtPoint(e.getPoint());

                // Nếu click vào cột "Xóa"
                if (col == 3) {
                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Bạn có chắc muốn xóa dịch vụ này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        String drugName = tableModel.getValueAt(row, 1).toString();  // lấy tên thuốc ở dòng vừa xóa

                        // Xóa khỏi bảng
                        ((DefaultTableModel) serviceTable.getModel()).removeRow(row);
                        listDrugDose.removeIf(drug -> drug.getName().equalsIgnoreCase(drugName));
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            tableModel.setValueAt(i + 1, i, 0);
                        }
                    }

                }
            }
        });

        btnConfirm.addActionListener(e -> {
            String medicineName = comboMedicinename.getSelectedItem().toString().trim();
            String quantityStr = txtQuantity.getText().trim();

            if (medicineName.isEmpty() || quantityStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            int quantity = Integer.parseInt(quantityStr);

            // Lấy thông tin thuốc từ cơ sở dữ liệu
            List<Drug> drugList = DrugReponsitory.getListDrug();
            Drug selectedDrug = null;
            for (Drug drug : drugList) {
                if (drug.getName().equalsIgnoreCase(medicineName)) {
                    selectedDrug = drug;
                    break;
                }
            }

            if (selectedDrug == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin thuốc trong cơ sở dữ liệu!");
                return;
            }

            if (selectedDrug.getStockQuantity() < quantity) {
                JOptionPane.showMessageDialog(this, "Thuốc '" + medicineName + "' trong kho chỉ còn " +
                        selectedDrug.getStockQuantity() + " viên. Không đủ để kê " + quantity + " viên.");
                return;
            }

            // Kiểm tra trùng thuốc và cập nhật luôn
            DefaultTableModel model = (DefaultTableModel) serviceTable.getModel();
            boolean updated = false;
            for (int i = 0; i < model.getRowCount(); i++) {
                String existingMedicineName = model.getValueAt(i, 1).toString();
                if (existingMedicineName.equalsIgnoreCase(medicineName)) {
                    model.setValueAt(quantityStr, i, 2); // Ghi đè số lượng
                    updated = true;
                    JOptionPane.showMessageDialog(this, "Thuốc đã tồn tại, số lượng đã được cập nhật.");
                    break;
                }
            }

            if (!updated) {
                int stt = model.getRowCount() + 1;
                model.addRow(new Object[]{stt, medicineName, quantityStr, "x"});
            }

            // Cập nhật lại vào listDrugDose
            int morning = txtMorning.getText().isEmpty() ? 0 : Integer.parseInt(txtMorning.getText());
            int noon = txtNoon.getText().isEmpty() ? 0 : Integer.parseInt(txtNoon.getText());
            int afternoon = txtAfternoon.getText().isEmpty() ? 0 : Integer.parseInt(txtAfternoon.getText());

            // Xóa nếu đã có, thêm bản mới
            listDrugDose.removeIf(drug -> drug.getName().equalsIgnoreCase(medicineName));
            listDrugDose.add(new DrugDose(medicineName, morning, noon, afternoon));
        });


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

    public JButton getBtnConfirm() {
        return btnConfirm;
    }

    public void setBtnConfirm(JButton btnConfirm) {
        this.btnConfirm = btnConfirm;
    }

    public JButton getBtnAddDrugs() {
        return btnAddDrugs;
    }

    public void setBtnAddDrugs(JButton btnAddDrugs) {
        this.btnAddDrugs = btnAddDrugs;
    }

    public JComboBox<String> getComboMedicinename() {
        return comboMedicinename;
    }

    public void setComboMedicinename(JComboBox<String> comboMedicinename) {
        this.comboMedicinename = comboMedicinename;
    }

    public JTextField getTxtMorning() {
        return txtMorning;
    }

    public void setTxtMorning(JTextField txtMorning) {
        this.txtMorning = txtMorning;
    }

    public JTextField getTxtNoon() {
        return txtNoon;
    }

    public void setTxtNoon(JTextField txtNoon) {
        this.txtNoon = txtNoon;
    }

    public JTextField getTxtAfternoon() {
        return txtAfternoon;
    }

    public void setTxtAfternoon(JTextField txtAfternoon) {
        this.txtAfternoon = txtAfternoon;
    }

    public JTextField getTxtQuantity() {
        return txtQuantity;
    }

    public void setTxtQuantity(JTextField txtQuantity) {
        this.txtQuantity = txtQuantity;
    }

    // Hàm tính liều lượng
    private void calculateQuantity() {
        int morning = txtMorning.getText().isEmpty() ? 0 : Integer.parseInt(txtMorning.getText());
        int noon = txtNoon.getText().isEmpty() ? 0 : Integer.parseInt(txtNoon.getText());
        int afternoon = txtAfternoon.getText().isEmpty() ? 0 : Integer.parseInt(txtAfternoon.getText());

        if (morning > 2) {
            morning = 0;
            SwingUtilities.invokeLater(() -> txtMorning.setText("0"));
        }
        if (noon > 2) {
            noon = 0;
            SwingUtilities.invokeLater(() -> txtNoon.setText("0"));
        }
        if (afternoon > 2) {
            afternoon = 0;
            SwingUtilities.invokeLater(() -> txtAfternoon.setText("0"));
        }

        int quantity = (morning + noon + afternoon) * 7;
        txtQuantity.setText(String.valueOf(quantity));
    }


    public void showText(){
        for (DrugDose list:listDrugDose){
            System.out.println(list);
        }
    }

    public List<DrugDose> getListDrugDose() {
        return listDrugDose;
    }

    public void setListDrugDose(List<DrugDose> listDrugDose) {
        this.listDrugDose = listDrugDose;
    }
    public void resetInfor(){
        txtMorning.setText("");
        txtNoon.setText("");
        txtAfternoon.setText("");
        txtQuantity.setText("");

        ((DefaultTableModel) serviceTable.getModel()).setRowCount(0);
    }

    public JTable getServiceTable() {
        return serviceTable;
    }

    public void setServiceTable(JTable serviceTable) {
        this.serviceTable = serviceTable;
    }
}
