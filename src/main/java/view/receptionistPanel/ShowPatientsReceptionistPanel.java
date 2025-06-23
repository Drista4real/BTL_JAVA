package view.receptionistPanel;

import reponsitory.Patientreponsitory;
import model.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ShowPatientsReceptionistPanel extends JPanel {

    private JTextField txtSearch;
    private JTable patientInfoTable;
    private JTable patientActionTable;
    private DefaultTableModel infoTableModel;
    private DefaultTableModel actionTableModel;

    public ShowPatientsReceptionistPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Danh Sách Bệnh Nhân");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        topPanel.add(lblTitle, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);

        JLabel lblSearch = new JLabel("Tìm kiếm: ");
        lblSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPanel.add(lblSearch);

        txtSearch = new JTextField("");
        txtSearch.setPreferredSize(new Dimension(200, 30));
        txtSearch.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        txtSearch.setForeground(Color.GRAY);
        searchPanel.add(txtSearch);

        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        // Table 1 - Patient Info
        infoTableModel = new DefaultTableModel(
                new Object[]{"Mã BN", "Tên Bệnh Nhân", "SĐT", "Giới tính", "Tuổi"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientInfoTable = new JTable(infoTableModel);
        patientInfoTable.setRowHeight(30);
        patientInfoTable.setBackground(Color.WHITE);
        patientInfoTable.setForeground(Color.BLACK);
        patientInfoTable.setSelectionBackground(new Color(0, 123, 255));
        patientInfoTable.setGridColor(Color.LIGHT_GRAY);
        patientInfoTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        patientInfoTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        patientInfoTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        patientInfoTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        patientInfoTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        JTableHeader infoHeader = patientInfoTable.getTableHeader();
        infoHeader.setPreferredSize(new Dimension(0, 30));
        infoHeader.setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setBackground(new Color(0, 123, 255));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Arial", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < patientInfoTable.getColumnCount(); i++) {
            patientInfoTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        infoHeader.setReorderingAllowed(false);
        infoHeader.setResizingAllowed(false);

        JScrollPane infoTableScroll = new JScrollPane(patientInfoTable);
        infoTableScroll.setBorder(null);
        infoTableScroll.getViewport().setBackground(Color.WHITE);

        // Table 2 - Action Buttons
        actionTableModel = new DefaultTableModel(
                new Object[]{"Tái Khám", "Lịch hẹn mới", "Sửa"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        patientActionTable = new JTable(actionTableModel);
        patientActionTable.setRowHeight(30);
        patientActionTable.setBackground(Color.WHITE);
        patientActionTable.setForeground(Color.BLACK);
        patientActionTable.setSelectionBackground(new Color(200, 230, 255));
        patientActionTable.setGridColor(Color.LIGHT_GRAY);

        JTableHeader actionHeader = patientActionTable.getTableHeader();
        actionHeader.setPreferredSize(new Dimension(0, 30));
        actionHeader.setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setBackground(new Color(0, 123, 255));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Arial", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });

        for (int i = 0; i < patientActionTable.getColumnCount(); i++) {
            patientActionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        patientActionTable.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer("Tái Khám", new Color(0, 153, 51)));
        patientActionTable.getColumnModel().getColumn(1).setCellRenderer(new ButtonRenderer("Lịch hẹn mới", new Color(0, 153, 51)));
        patientActionTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer("Sửa", new Color(0, 153, 51)));
        
        patientActionTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        patientActionTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        patientActionTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        actionHeader.setReorderingAllowed(false);
        actionHeader.setResizingAllowed(false);

        JScrollPane actionTableScroll = new JScrollPane(patientActionTable);
        actionTableScroll.setBorder(null);
        actionTableScroll.getViewport().setBackground(Color.WHITE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, infoTableScroll, actionTableScroll);
        splitPane.setResizeWeight(0.6);
        splitPane.setOneTouchExpandable(false);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);

        tablePanel.add(splitPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        reloadPatientList();
    }

    private class ButtonRenderer extends DefaultTableCellRenderer {
        private final String buttonText;
        private final Color backgroundColor;

        public ButtonRenderer(String buttonText, Color backgroundColor) {
            this.buttonText = buttonText;
            this.backgroundColor = backgroundColor;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = new JButton(buttonText);
            button.setBackground(backgroundColor);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            if ("Sửa".equals(buttonText)) {
                button.setFont(new Font("Arial", Font.PLAIN, 12));
            }

            return button;
        }
    }

    public JTextField getTxtSearch() { return txtSearch; }
    public void setTxtSearch(JTextField txtSearch) { this.txtSearch = txtSearch; }
    public JTable getPatientInfoTable() { return patientInfoTable; }
    public JTable getPatientActionTable() { return patientActionTable; }
    public DefaultTableModel getInfoTableModel() { return infoTableModel; }
    public DefaultTableModel getActionTableModel() { return actionTableModel; }

    public void reloadPatientList() {
        infoTableModel.setRowCount(0);
        actionTableModel.setRowCount(0);
        List<Patient> patients = Patientreponsitory.getListPatients();
        for (Patient patient : patients) {
            int age = 0;
            if (patient.getBirthDate() != null) {
                age = java.time.Period.between(
                        patient.getBirthDate().toLocalDate(),
                        java.time.LocalDate.now()
                ).getYears();
            }
            String gender = patient.getGender() == 1 ? "Nam" : "Nữ";
            infoTableModel.addRow(new Object[]{
                    patient.getId(),
                    patient.getName(),
                    patient.getPhoneNumber(),
                    gender,
                    age
            });
            actionTableModel.addRow(new Object[]{"Tái Khám", "Lịch hẹn mới", "Sửa"});
        }
    }
}