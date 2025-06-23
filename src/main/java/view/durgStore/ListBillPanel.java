package view.durgStore;
//package view.durgStore;

import reponsitory.Patientreponsitory;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ListBillPanel extends JPanel {

    private JPanel headerPanel;
    private JLabel lblTitle;
    private JRadioButton rbHT, rbChuaHT;
    private ButtonGroup groupStatus;
    private JTable tblPatients;
    private JScrollPane scrollPane;
    private Object[][] data;
    private DefaultTableModel model;
    private String sdt;


    public ListBillPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        headerPanel = new JPanel();
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
        headerPanel.setPreferredSize(new Dimension(641, 40));
        headerPanel.setBackground(Color.WHITE);

        lblTitle = new JLabel("Danh sách hóa đơn hôm nay");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(51, 51, 255));

        // Radio buttons
        rbHT = new JRadioButton("HT");
        rbChuaHT = new JRadioButton("Chưa HT");

        rbHT.setBackground(Color.WHITE);
        rbChuaHT.setBackground(Color.WHITE);

        rbHT.setFont(new Font("Arial", Font.PLAIN, 13));
        rbChuaHT.setFont(new Font("Arial", Font.PLAIN, 13));

        groupStatus = new ButtonGroup();
        groupStatus.add(rbHT);
        groupStatus.add(rbChuaHT);

        // Header Layout
        GroupLayout headerLayout = new GroupLayout(headerPanel);
        headerPanel.setLayout(headerLayout);
        headerLayout.setAutoCreateGaps(true);
        headerLayout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = headerLayout.createSequentialGroup();
        GroupLayout.ParallelGroup vGroup = headerLayout.createParallelGroup(GroupLayout.Alignment.CENTER);

        hGroup.addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE);
        hGroup.addGap(30, 30, Short.MAX_VALUE);
        hGroup.addComponent(rbHT);
        hGroup.addComponent(rbChuaHT);

        vGroup.addComponent(lblTitle);
        vGroup.addComponent(rbHT);
        vGroup.addComponent(rbChuaHT);

        headerLayout.setHorizontalGroup(hGroup);
        headerLayout.setVerticalGroup(vGroup);

        add(headerPanel, BorderLayout.PAGE_START);

        // Table data
        String[] columnNames = {"Mã HD", "Tên bệnh nhân", "Số điện thoại", "Giới tính", "Tuổi", "Tổng tiền", "Trạng thái"};
        Patientreponsitory dao = new Patientreponsitory();
        List<Object[]> list = dao.getAllBillPatients();

        data = list.toArray(new Object[0][]);

        model = new DefaultTableModel(data, columnNames) {
            final boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) return Icon.class;
                return String.class;
            }
        };

        tblPatients = new JTable(model);
        tblPatients.setRowHeight(28);
        tblPatients.setFillsViewportHeight(true);
        tblPatients.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Table Header
        JTableHeader tableHeader = tblPatients.getTableHeader();
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

        scrollPane = new JScrollPane(tblPatients);
        add(scrollPane, BorderLayout.CENTER);

        // Table cell renderer
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

        for (int i = 0; i < tblPatients.getColumnCount(); i++) {
            tblPatients.getColumnModel().getColumn(i).setCellRenderer(whiteCenterRenderer);
        }

        tblPatients.getColumnModel().getColumn(0).setPreferredWidth(40);   // STT
        tblPatients.getColumnModel().getColumn(1).setPreferredWidth(150);  // Tên
        tblPatients.getColumnModel().getColumn(2).setPreferredWidth(120);  // SĐT
        tblPatients.getColumnModel().getColumn(3).setPreferredWidth(60);   // Giới tính
        tblPatients.getColumnModel().getColumn(4).setPreferredWidth(50);   // Tuổi
        tblPatients.getColumnModel().getColumn(5).setPreferredWidth(100);  // Tổng tiền
        tblPatients.getColumnModel().getColumn(6).setPreferredWidth(100);  // Trạng thái
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

    public JRadioButton getRbHT() {
        return rbHT;
    }

    public void setRbHT(JRadioButton rbHT) {
        this.rbHT = rbHT;
    }

    public JRadioButton getRbChuaHT() {
        return rbChuaHT;
    }

    public void setRbChuaHT(JRadioButton rbChuaHT) {
        this.rbChuaHT = rbChuaHT;
    }

    public ButtonGroup getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(ButtonGroup groupStatus) {
        this.groupStatus = groupStatus;
    }

    public JTable getTblPatients() {
        return tblPatients;
    }

    public void setTblPatients(JTable tblPatients) {
        this.tblPatients = tblPatients;
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

    public DefaultTableModel getModel() {
        return model;
    }

    public void setModel(DefaultTableModel model) {
        this.model = model;
    }
    public void reloadTableData() {
        Patientreponsitory dao = new Patientreponsitory();
        List<Object[]> list = dao.getAllBillPatients();

        data = list.toArray(new Object[0][]);
        model.setDataVector(data, new String[]{
                "Mã HD", "Tên bệnh nhân", "Số điện thoại", "Giới tính", "Tuổi", "Tổng tiền", "Trạng thái"
        });

        tblPatients.setModel(model);

        // Cập nhật lại renderer căn giữa
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

        for (int i = 0; i < tblPatients.getColumnCount(); i++) {
            tblPatients.getColumnModel().getColumn(i).setCellRenderer(whiteCenterRenderer);
        }
    }
}
