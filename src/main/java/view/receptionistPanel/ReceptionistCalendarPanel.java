package view.receptionistPanel;

import reponsitory.Patientreponsitory;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ReceptionistCalendarPanel extends JPanel {

    private JPanel headerPanel;
    private JLabel lblTitle, lblSearch;
    private JTextField tfSearch;
    private JTable tblPatients;
    private JScrollPane scrollPane;
    Object[][] data;

    public ReceptionistCalendarPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        //Header Panel
        headerPanel = new JPanel();
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
        headerPanel.setPreferredSize(new Dimension(641, 40));

        lblTitle = new JLabel("Bệnh nhân đang chờ khám");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(51, 51, 255));

        lblSearch = new JLabel("Tìm kiếm");
        lblSearch.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSearch.setForeground(Color.BLACK);
        tfSearch = new JTextField("");
        tfSearch.setPreferredSize(new Dimension(200, 25));
        tfSearch.setForeground(Color.GRAY); // Placeholder color

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

        String[] columnNames = {"STT", "Tên bệnh nhân", "Số điện thoại", "Giới tính", "Tuổi", "Trạng thái"};

        Patientreponsitory dao = new Patientreponsitory();
        List<Object[]> list = dao.getAllPatients();

        data = list.toArray(new Object[0][]);


        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            final boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) return Icon.class;
                return String.class;
            }
        };

        tblPatients = new JTable(model);
        tblPatients.setRowHeight(28);
        tblPatients.setFillsViewportHeight(true);
        tblPatients.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Tô màu cho tiêu đề cột
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

//        DefaultTableCellRenderer iconRenderer = new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value,
//                                                           boolean isSelected, boolean hasFocus,
//                                                           int row, int column) {
//                JLabel lbl = new JLabel();
//                lbl.setHorizontalAlignment(SwingConstants.CENTER);
//                if (value instanceof Icon) {
//                    lbl.setIcon((Icon) value);
//                }
//                lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
//                return lbl;
//            }
//        };

        for (int i = 0; i < tblPatients.getColumnCount(); i++) {
            tblPatients.getColumnModel().getColumn(i).setCellRenderer(whiteCenterRenderer);
        }

        // Kích thước từng cột
        tblPatients.getColumnModel().getColumn(0).setPreferredWidth(40);   // STT
        tblPatients.getColumnModel().getColumn(1).setPreferredWidth(150);  // Tên
        tblPatients.getColumnModel().getColumn(2).setPreferredWidth(120);  // SĐT
        tblPatients.getColumnModel().getColumn(3).setPreferredWidth(60);   // Giới tính
        tblPatients.getColumnModel().getColumn(4).setPreferredWidth(50);   // Tuổi
        tblPatients.getColumnModel().getColumn(5).setPreferredWidth(100);  // Trạng thái
//        tblPatients.getColumnModel().getColumn(6).setPreferredWidth(60);   // Khám
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

}
