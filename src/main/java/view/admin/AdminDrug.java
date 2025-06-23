package view.admin;

import model.Drug;
import reponsitory.DrugReponsitory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class AdminDrug extends JPanel {

    private JTextField tfSearch;
    private JTable table;
    private JButton btnAdd;
    private DefaultTableModel tableModel;

    public AdminDrug() {
        initComponents();
        loadDrugData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(400, 40));
        headerPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Danh sách thuốc");
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

        String[] columnNames = {"Mã số", "Tên thuốc", "Mô tả", "Giá", "Số lượng còn"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        // Tùy chỉnh tiêu đề cột: nền xanh, chữ trắng in đậm
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setBackground(new Color(0, 102, 204)); // màu xanh
                label.setForeground(Color.WHITE);            // chữ trắng
                label.setFont(new Font("Arial", Font.BOLD, 13)); // in đậm
                label.setHorizontalAlignment(SwingConstants.CENTER); // căn giữa
                return label;
            }
        });
        // Căn giữa nội dung trong bảng
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

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

    public void loadDrugData() {
        tableModel.setRowCount(0);
        DecimalFormat formatter = new DecimalFormat("#,###");
        List<Drug> drugs = DrugReponsitory.getListDrug();
        for (Drug drug : drugs) {
            tableModel.addRow(new Object[]{
                    drug.getId(),
                    drug.getName(),
                    drug.getDescription(),
                    formatter.format(drug.getPrice()) + " VND",
                    drug.getStockQuantity()
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

    public void reloadTable() {
        loadDrugData();
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
            JFrame frame = new JFrame("Quản lý thuốc");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new AdminDrug());
            frame.setVisible(true);
        });
    }
}
