package view.admin;

import model.Service;
import reponsitory.ServiceReponsitory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class AdminService extends JPanel {

    private JTextField tfSearch;
    private JTable table;
    private JButton btnAdd;
    private DefaultTableModel tableModel;

    public AdminService() {
        initComponents();
        loadServiceData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(400, 40));
        headerPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Danh sách dịch vụ");
        lblTitle.setForeground(new Color(0, 51, 255));
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        tfSearch = new JTextField();
        tfSearch.setPreferredSize(new Dimension(170, 25));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(tfSearch);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã số", "Tên dịch vụ", "Giá"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);

        // Căn giữa dữ liệu
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Tùy chỉnh tiêu đề cột
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = new JLabel(value.toString());
                lbl.setOpaque(true);
                lbl.setBackground(new Color(0, 102, 204)); // nền xanh
                lbl.setForeground(Color.WHITE);           // chữ trắng
                lbl.setFont(new Font("Arial", Font.BOLD, 13)); // in đậm
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setPreferredSize(new Dimension(400, 40));
        footerPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("Thêm");
        btnAdd.setBackground(new Color(51, 255, 51));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setFocusPainted(false);

        footerPanel.add(btnAdd);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void loadServiceData() {
        tableModel.setRowCount(0);
        DecimalFormat formatter = new DecimalFormat("#,###");
        List<Service> services = ServiceReponsitory.getListService();

        for (Service s : services) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    formatter.format(s.getPrice()) + " VND"
            });
        }
    }

    public JTable getTable() {
        return table;
    }

    public JTextField getTfSearch() {
        return tfSearch;
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public void setTfSearch(JTextField tfSearch) {
        this.tfSearch = tfSearch;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public void setBtnAdd(JButton btnAdd) {
        this.btnAdd = btnAdd;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý dịch vụ");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new AdminService());
            frame.setVisible(true);
        });
    }
}
