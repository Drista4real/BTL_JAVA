package view.dentistPanel;

import Utils.CustomDocumentFilter;
import reponsitory.ServiceReponsitory;
import model.Service;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicePanel extends JPanel {

    private JLabel lblIntroTitle;
    private JLabel lblIntroContent;
    private JLabel lblPhone;
    private JLabel lblFacebook;
    private JLabel lblTikTok;
    private JLabel lblEmail;
    private JPanel contactInfoPanel;
    private JButton btnAddDrugs;
    private JComboBox<String> cboServiceName;
    JTextField txtQuantity;
    private Map<Service,Integer> listSevice=new HashMap<Service,Integer>();
    private DefaultTableModel tableModel;

    public ServicePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Tiêu đề ---
        lblIntroTitle = new JLabel("  Dịch vụ sử dụng");
        lblIntroTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblIntroTitle.setPreferredSize(new Dimension(200, 30));
        add(lblIntroTitle, BorderLayout.PAGE_START);

        // --- Panel chính chứa form và bảng ---
        JPanel servicePanel = new JPanel(new BorderLayout());

        // --- Form bên trái ---
        JPanel formPanel = new JPanel(null);  // dùng layout tự do để dễ căn chỉnh
        formPanel.setPreferredSize(new Dimension(230, 260));
        formPanel.setBackground(Color.WHITE);

        JLabel lblServiceName = new JLabel("Tên dịch vụ:");
        lblServiceName.setBounds(10, 25, 100, 25);
        lblServiceName.setFont(new Font("Arial", Font.ITALIC, 16));

        String[] serviceList = {"Test1", "Test2", "Test3", "Test4"};
        cboServiceName = new JComboBox<>(serviceList);
        cboServiceName.setBounds(10, 50, 200, 35);
        cboServiceName.setFont(new Font("Arial", Font.PLAIN, 14));
        cboServiceName.setBackground(Color.WHITE);

        cboServiceName.removeAllItems();
        List<Service> serviceListnew = ServiceReponsitory.getListService();
        for (Service service : serviceListnew) {
            cboServiceName.addItem(service.getName());
        }

        JLabel lblQuantity = new JLabel("Số lượng:");
        lblQuantity.setBounds(10, 120, 100, 25);
        lblQuantity.setFont(new Font("Arial", Font.ITALIC, 16));
        txtQuantity = new JTextField();
        txtQuantity.setBounds(10, 145, 200, 35);
        txtQuantity.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ((AbstractDocument) txtQuantity.getDocument()).setDocumentFilter(new CustomDocumentFilter("\\d+"));
        cboServiceName.addActionListener(e -> {
            txtQuantity.setText("");
        });
        JButton btnConfirm = new JButton("Xác nhận");
        btnConfirm.setBounds(10, 215, 100, 30);
        btnConfirm.setBackground(new Color(0, 123, 255));
        btnConfirm.setForeground(Color.WHITE);

        formPanel.add(lblServiceName);
        formPanel.add(cboServiceName);
        formPanel.add(lblQuantity);
        formPanel.add(txtQuantity);
        formPanel.add(btnConfirm);

        // --- Bảng bên phải ---
        tableModel = new DefaultTableModel(
                new Object[]{"STT", "Tên dịch vụ", "Số lượng", "Xóa"}, 0
        );
        JTable serviceTable = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serviceTable.setRowHeight(20);
        serviceTable.setBackground(Color.WHITE);              // Nền bảng
        serviceTable.setForeground(Color.BLACK);              // Màu chữ
        serviceTable.setSelectionBackground(new Color(200, 230, 255)); // Nền khi chọn dòng
        serviceTable.setGridColor(Color.LIGHT_GRAY);          // Màu viền ô

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
        serviceTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT
        serviceTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        serviceTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Số lượng
        serviceTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Xóa
        serviceTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // STT
        serviceTable.getColumnModel().getColumn(1).setPreferredWidth(180); // Tên dịch vụ
        serviceTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // Số lượng
        serviceTable.getColumnModel().getColumn(3).setPreferredWidth(40);  // Xóa
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        // --- Kết hợp form và bảng ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(formPanel, BorderLayout.WEST);
        bottomPanel.add(tableScroll, BorderLayout.CENTER);

        // --- Nút thêm danh sách thuốc ---
        btnAddDrugs = new JButton("Thêm danh sách thuốc");
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
                        ((DefaultTableModel) serviceTable.getModel()).removeRow(row);

                        // Cập nhật lại STT
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            tableModel.setValueAt(i + 1, i, 0);
                        }
                    }
                }
            }
        });

        // --- Xử lý nút xác nhận ---
        btnConfirm.addActionListener(e -> {
            String serviceName = cboServiceName.getSelectedItem().toString().trim();
            String quantityStr = txtQuantity.getText().trim();

            if (serviceName.isEmpty() || quantityStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên.");
                return;
            }

            // --- Ràng buộc số lượng ---
            switch (serviceName) {
                case "Khám răng tổng quát":
                case "Niềng răng":
                case "Chữa viêm tủy":
                    if (quantity != 1) {
                        JOptionPane.showMessageDialog(this, serviceName + " chỉ được nhập số lượng = 1.");
                        txtQuantity.setText("1");
                        return;
                    }
                    break;
                case "Trám răng sâu":
                    if (quantity < 1 || quantity > 5) {
                        JOptionPane.showMessageDialog(this, "Trám răng sâu chỉ cho phép số lượng từ 1 đến 5.");
                        return;
                    }
                    break;
                case "Tẩy trắng răng":
                    if (quantity != 1) {
                        JOptionPane.showMessageDialog(this, "Tẩy trắng răng chỉ thực hiện 1 lần mỗi lần hẹn.");
                        txtQuantity.setText("1");
                        return;
                    }
                    break;
                case "Nhổ răng":
                    if (quantity < 1 || quantity > 4) {
                        JOptionPane.showMessageDialog(this, "Nhổ răng chỉ cho phép số lượng từ 1 đến 4.");
                        return;
                    }
                    break;
                default:
                    break;
            }

            // Kiểm tra nếu đã tồn tại thì ghi đè
            DefaultTableModel model = (DefaultTableModel) serviceTable.getModel();
            boolean updated = false;
            for (int i = 0; i < model.getRowCount(); i++) {
                String existingService = model.getValueAt(i, 1).toString();
                if (existingService.equalsIgnoreCase(serviceName)) {
                    model.setValueAt(quantity, i, 2); // Ghi đè lại số lượng
                    updated = true;
                    JOptionPane.showMessageDialog(this, "Dịch vụ đã tồn tại, thông tin số lượng đã được cập nhật.");
                    break;
                }
            }

            // Nếu chưa có thì thêm mới
            if (!updated) {
                int stt = model.getRowCount() + 1;
                model.addRow(new Object[]{stt, serviceName, quantity, "x"});
            }

            txtQuantity.setText("");
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

    public JButton getBtnAddDrugs() {
        return btnAddDrugs;
    }

    public void setBtnAddDrugs(JButton btnAddDrugs) {
        this.btnAddDrugs = btnAddDrugs;
    }

    public JComboBox<String> getCboServiceName() {
        return cboServiceName;
    }

    public void setCboServiceName(JComboBox<String> cboServiceName) {
        this.cboServiceName = cboServiceName;
    }

    public JTextField getTxtQuantity() {
        return txtQuantity;
    }

    public void setTxtQuantity(JTextField txtQuantity) {
        this.txtQuantity = txtQuantity;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public Map<Service, Integer> getListSevice() {
        return listSevice;
    }
    public void setListSevice(Map<Service, Integer> listSevice) {
        this.listSevice = listSevice;
    }
    public Map<Service, Integer> readServiceTableData() {
        listSevice.clear();
        // Duyệt toàn bộ dòng trong tableModel
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Service serviceName = new Service(tableModel.getValueAt(i, 1).toString());
            int quantity = Integer.parseInt(tableModel.getValueAt(i, 2).toString());

            listSevice.put(serviceName, quantity);
        }
        return listSevice;
    }
    public void showText(){
        for (Map.Entry<Service, Integer> entry : listSevice.entrySet()) {
            Service key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println("Tên dich vu: " + key.getName() + ", Số lượng: " + value);
        }
    }
    public void resetInfor(){
        txtQuantity.setText("");
        tableModel.setRowCount(0);
        listSevice.clear();
    }

}
