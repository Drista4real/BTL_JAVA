package view.durgStore;

import model.Drug;
import reponsitory.DrugReponsitory;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class ListDrugPanel extends JPanel {

    private JPanel headerPanel;
    private JLabel lblTitle, lblSearch;
    private JTextField tfSearch;
    private JTable tblDrugs;
    private JScrollPane scrollPane;
    Object[][] data;

    public ListDrugPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        //Header Panel
        headerPanel = new JPanel();
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
        //Dòng này không quan trọng do nó sẽ được "kéo" dựa trên JFrame
        headerPanel.setPreferredSize(new Dimension(641, 40));

        lblTitle = new JLabel("Danh sách thuốc");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(51, 51, 255));

        lblSearch = new JLabel("Tìm kiếm");
        lblSearch.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSearch.setForeground(Color.BLACK);
        tfSearch = new JTextField();
        tfSearch.setPreferredSize(new Dimension(200, 25));

        GroupLayout headerLayout = new GroupLayout(headerPanel);
        headerPanel.setLayout(headerLayout);
        headerLayout.setAutoCreateGaps(true);
        headerLayout.setAutoCreateContainerGaps(true);

        headerPanel.setBackground(Color.WHITE);

        GroupLayout.SequentialGroup hGroup = headerLayout.createSequentialGroup();
        GroupLayout.ParallelGroup vGroup = headerLayout.createParallelGroup(GroupLayout.Alignment.CENTER);

        // Horizontal Group
        hGroup.addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE);
        hGroup.addGap(50, 50, Short.MAX_VALUE);
        hGroup.addComponent(lblSearch);
        hGroup.addGap(10);
        hGroup.addComponent(tfSearch, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE);

        //Vertical Group
        vGroup.addComponent(lblTitle);
        vGroup.addComponent(lblSearch);
        vGroup.addComponent(tfSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);

        //Set layout
        headerLayout.setHorizontalGroup(hGroup);
        headerLayout.setVerticalGroup(vGroup);


        add(headerPanel, BorderLayout.PAGE_START);

        String[] columnNames = {"Mã thuốc", "Tên thuốc", "Số lượng còn", "Đơn vị tính", "Đơn giá"};

        List<Drug> list = DrugReponsitory.getListAllDrug();
        data = new Object[list.size()][5];
        DecimalFormat formatter = new DecimalFormat("#,###");

        for (int i = 0; i < list.size(); i++) {
            Drug drug = list.get(i);
            data[i][0] = drug.getId();
            data[i][1] = drug.getName();
            data[i][2] = drug.getStockQuantity();
            data[i][3] = "Vỉ";
            data[i][4] = formatter.format(drug.getPrice()) + " VND";
        }


        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            final boolean[] canEdit = new boolean[]{
                    false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        tblDrugs = new JTable(model);
        tblDrugs.setRowHeight(28);
        tblDrugs.setFillsViewportHeight(true);
        tblDrugs.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Tô màu cho tiêu đề cột
        JTableHeader tableHeader = tblDrugs.getTableHeader();
        tableHeader.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = new JLabel(value.toString());
                lbl.setOpaque(true);
                lbl.setBackground(new Color(0, 102, 204));
                lbl.setForeground(Color.WHITE);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Arial", Font.BOLD, 13));
                lbl.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0, 102, 204)));
                return lbl;
            }
        });

        scrollPane = new JScrollPane(tblDrugs);
        add(scrollPane, BorderLayout.CENTER);

        // Renderer trắng cho ô dữ liệu
        DefaultTableCellRenderer whiteCenterRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setOpaque(true);

                if (isSelected) {
                    lbl.setBackground(table.getSelectionBackground());
                    lbl.setForeground(table.getSelectionForeground());
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(Color.BLACK);
                }

                lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                return lbl;
            }
        };


        DefaultTableCellRenderer iconRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = new JLabel();
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (value instanceof Icon) {
                    lbl.setIcon((Icon) value);
                }
                lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                return lbl;
            }
        };

        for (int i = 0; i < tblDrugs.getColumnCount(); i++) {
            tblDrugs.getColumnModel().getColumn(i).setCellRenderer(whiteCenterRenderer);
        }

        // Kích thước từng cột
        tblDrugs.getColumnModel().getColumn(0).setPreferredWidth(40);   // Mã thuốc
        tblDrugs.getColumnModel().getColumn(1).setPreferredWidth(150);  // Tên
        tblDrugs.getColumnModel().getColumn(2).setPreferredWidth(120);  // sl
        tblDrugs.getColumnModel().getColumn(3).setPreferredWidth(60);   // dv tính
        tblDrugs.getColumnModel().getColumn(4).setPreferredWidth(50);   // giá
    }

    public JPanel getHeaderPanel() {
        return headerPanel;
    }

    public void setHeaderPanel(JPanel headerPanel) {
        this.headerPanel = headerPanel;
    }

    public JLabel getLblTitle() {
        return lblTitle;
    }

    public void setLblTitle(JLabel lblTitle) {
        this.lblTitle = lblTitle;
    }

    public JLabel getLblSearch() {
        return lblSearch;
    }

    public void setLblSearch(JLabel lblSearch) {
        this.lblSearch = lblSearch;
    }

    public JTextField getTfSearch() {
        return tfSearch;
    }

    public void setTfSearch(JTextField tfSearch) {
        this.tfSearch = tfSearch;
    }

    public JTable getTblDrugs() {
        return tblDrugs;
    }

    public void setTblDrugs(JTable tblDrugs) {
        this.tblDrugs = tblDrugs;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public Object[][] getData() {
        return data;
    }

    public void setData(Object[][] data) {
        this.data = data;
    }
    public void reloadTableData() {
        List<Drug> list = DrugReponsitory.getListAllDrug();
        DecimalFormat formatter = new DecimalFormat("#,###");

        DefaultTableModel model = (DefaultTableModel) tblDrugs.getModel();
        model.setRowCount(0); // Xóa hết dữ liệu cũ

        for (Drug drug : list) {
            model.addRow(new Object[]{
                    drug.getId(),
                    drug.getName(),
                    drug.getStockQuantity(),
                    "Vỉ",
                    formatter.format(drug.getPrice()) + " VND"
            });
        }

        tblDrugs.repaint();
    }

}


