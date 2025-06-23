package view.durgStore;

import reponsitory.DrugReponsitory;
import reponsitory.ServiceReponsitory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;

public class DrugBillPanel extends JPanel {

    private JLabel idBill, namePat, nameDen, price, status;
    private JTable tableService, tableMedicine;
    private JButton buttonQRCode, buttonCash;

    public DrugBillPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        panelHeader.setBackground(Color.WHITE);

        JPanel panelTopInfo = new JPanel(new GridBagLayout());
        panelTopInfo.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        JLabel labelTitle = new JLabel("Chi tiết hóa đơn");
        labelTitle.setFont(new Font("Arial", Font.BOLD, 14));
        labelTitle.setForeground(new Color(51, 51, 255));
        labelTitle.setBackground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        panelTopInfo.add(labelTitle, gbc);

        JPanel panelInfo = new JPanel(new GridLayout(2, 5, 10, 5));
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel[] labels = {
                new JLabel("Mã HD:"), new JLabel("Tên BN:"), new JLabel("Tên BS:"),
                new JLabel("Tổng tiền:"), new JLabel("Trạng thái:")
        };
        for (JLabel label : labels) {
            label.setBackground(Color.WHITE);
            label.setOpaque(true);
            panelInfo.add(label);
        }

        idBill = new JLabel("001");
        namePat = new JLabel("Đàm hoàng lam");
        nameDen = new JLabel("Hoàng Minh");
        price = new JLabel("300 000 VND");
        status = new JLabel("Chưa thanh toán");

        JLabel[] values = {idBill, namePat, nameDen, price, status};
        for (JLabel value : values) {
            value.setBackground(Color.WHITE);
            value.setOpaque(true);
            panelInfo.add(value);
        }

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        panelTopInfo.add(panelInfo, gbc);

        panelHeader.add(panelTopInfo, BorderLayout.CENTER);
        add(panelHeader, BorderLayout.NORTH);

        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(Color.WHITE);

        JPanel tablePanel = new JPanel(new GridLayout(2, 1));
        tablePanel.setBackground(Color.WHITE);

        JPanel panelMedicine = new JPanel(new BorderLayout());
        panelMedicine.setBackground(Color.WHITE);
        JLabel labelMedicine = new JLabel("Danh sách thuốc");
        labelMedicine.setFont(new Font("Arial", Font.BOLD, 12));
        labelMedicine.setForeground(new Color(51, 51, 255));
        labelMedicine.setBackground(Color.WHITE);
        labelMedicine.setOpaque(true);
        panelMedicine.add(labelMedicine, BorderLayout.NORTH);

        String[] columnsMedicine = {"STT", "Tên thuốc", "Số lượng", "Đơn giá", "Thành tiền"};
        Object[][] dataMedicine = {{null, null, null, null, null}};
        tableMedicine = new JTable(dataMedicine, columnsMedicine);
        JTableHeader header1 = tableMedicine.getTableHeader();
        header1.setBackground(new Color(0, 120, 215));
        header1.setForeground(Color.WHITE);
        header1.setFont(new Font("Arial", Font.BOLD, 12));
        tableMedicine.setBackground(Color.WHITE);
        tableMedicine.setOpaque(true);

        // set chiều dài
        int[] widths1 = {40, 200, 80, 100, 120};
        for (int i = 0; i < widths1.length; i++) {
            TableColumn column = tableMedicine.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths1[i]);
        }

        JScrollPane scrollPane1 = new JScrollPane(tableMedicine);
        scrollPane1.getViewport().setBackground(Color.WHITE);
        scrollPane1.setBackground(Color.WHITE);
        panelMedicine.add(scrollPane1, BorderLayout.CENTER);

        JPanel panelService = new JPanel(new BorderLayout());
        panelService.setBackground(Color.WHITE);
        JLabel labelService = new JLabel("Danh sách dịch vụ");
        labelService.setFont(new Font("Arial", Font.BOLD, 12));
        labelService.setForeground(new Color(51, 51, 255));
        labelService.setBackground(Color.WHITE);
        labelService.setOpaque(true);
        panelService.add(labelService, BorderLayout.NORTH);

        String[] columnsService = {"STT", "Tên dịch vụ", "Số lượng", "Đơn giá", "Thành tiền"};
        Object[][] dataService = {{null, null, null, null, null}};
        tableService = new JTable(dataService, columnsService);
        JTableHeader header2 = tableService.getTableHeader();
        header2.setBackground(new Color(0, 120, 215));
        header2.setForeground(Color.WHITE);
        header2.setFont(new Font("Arial", Font.BOLD, 12));
        tableService.setBackground(Color.WHITE);
        tableService.setOpaque(true);
        //set độ dài
        int[] widths2 = {40, 200, 80, 100, 120};
        for (int i = 0; i < widths2.length; i++) {
            TableColumn column = tableService.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths2[i]);
        }

        JScrollPane scrollPane2 = new JScrollPane(tableService);
        scrollPane2.getViewport().setBackground(Color.WHITE);
        scrollPane2.setBackground(Color.WHITE);
        panelService.add(scrollPane2, BorderLayout.CENTER);

        tablePanel.add(panelMedicine);
        tablePanel.add(panelService);
        panelCenter.add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        buttonCash = new JButton("Tiền mặt");
        buttonCash.setBackground(new Color(51, 255, 51));
        buttonCash.setForeground(Color.WHITE);
        buttonCash.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(buttonCash);

        buttonQRCode = new JButton("Quét mã");
        buttonQRCode.setBackground(new Color(51, 255, 51));
        buttonQRCode.setForeground(Color.WHITE);
        buttonQRCode.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(buttonQRCode);

        panelCenter.add(buttonPanel, BorderLayout.SOUTH);
        add(panelCenter, BorderLayout.CENTER);
    }

    public JLabel getIdBill() {
        return idBill;
    }

    public void setIdBill(JLabel idBill) {
        this.idBill = idBill;
    }

    public JLabel getNamePat() {
        return namePat;
    }

    public void setNamePat(JLabel namePat) {
        this.namePat = namePat;
    }

    public JLabel getNameDen() {
        return nameDen;
    }

    public void setNameDen(JLabel nameDen) {
        this.nameDen = nameDen;
    }

    public JLabel getPrice() {
        return price;
    }

    public void setPrice(JLabel price) {
        this.price = price;
    }

    public JLabel getStatus() {
        return status;
    }

    public void setStatus(JLabel status) {
        this.status = status;
    }

    public JTable getTableService() {
        return tableService;
    }

    public void setTableService(JTable tableService) {
        this.tableService = tableService;
    }

    public JTable getTableMedicine() {
        return tableMedicine;
    }

    public void setTableMedicine(JTable tableMedicine) {
        this.tableMedicine = tableMedicine;
    }

    public JButton getButtonQRCode() {
        return buttonQRCode;
    }

    public void setButtonQRCode(JButton buttonQRCode) {
        this.buttonQRCode = buttonQRCode;
    }

    public JButton getButtonCash() {
        return buttonCash;
    }

    public void setButtonCash(JButton buttonCash) {
        this.buttonCash = buttonCash;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chi tiết hóa đơn bệnh nhân");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new DrugBillPanel());
        frame.setSize(840, 520);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public void loadPrescriptionData(String idPre) {
        java.util.List<Object[]> drugs = DrugReponsitory.getListDrugFromPre(idPre);
        java.util.List<Object[]> services = ServiceReponsitory.getListServiceFromPre(idPre);

        String[] columns = {"STT", "Tên", "Số lượng", "Đơn giá", "Thành tiền"};

        DefaultTableModel modelDrug = new DefaultTableModel(columns, 0);
        for (Object[] row : drugs) {
            modelDrug.addRow(row);
        }
        tableMedicine.setModel(modelDrug);

        DefaultTableModel modelService = new DefaultTableModel(columns, 0);
        for (Object[] row : services) {
            modelService.addRow(row);
        }
        tableService.setModel(modelService);
        // Renderer căn giữa cho bảng
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tableMedicine.getColumnCount(); i++) {
            tableMedicine.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        for (int i = 0; i < tableService.getColumnCount(); i++) {
            tableService.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

    }

}
