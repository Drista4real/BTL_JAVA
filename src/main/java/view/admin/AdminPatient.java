package view.admin;

import model.Patient;
import reponsitory.Patientreponsitory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class AdminPatient extends JPanel {

    private JTextField tfSearch;
    private JTable table;
    private JButton btnAdd;

    public AdminPatient() {
        initComponents();
        loadPatientData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(400, 40));
        headerPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Danh sách bệnh nhân");
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

        table = new JTable(new DefaultTableModel(
                new Object[][] {
                        {"tét", "tét", "tét", "tét", "tét", "tét"},
                },
                new String[] { "Mã số", "Tên bệnh nhân", "Số điện thoại", "Giới tính", "Ngày sinh", "Địa chỉ" }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        // Set độ rộng cột hợp lý
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // Mã số
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Số điện thoại
        table.getColumnModel().getColumn(3).setPreferredWidth(70);  // Giới tính
        table.getColumnModel().getColumn(4).setPreferredWidth(50);  // Tuổi
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // Chức vụ

        table.setRowHeight(26);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);
        // Tùy chỉnh tên cột: nền xanh, chữ trắng, in đậm, căn giữa
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
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
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setPreferredSize(new Dimension(400, 40));
        footerPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("Thêm");
        btnAdd.setBackground(new Color(51, 255, 51));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setFocusPainted(false);

//        footerPanel.add(btnAdd);
//        add(footerPanel, BorderLayout.SOUTH);
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
    // Hàm cập nhật lại dữ liệu bảng
    public void loadPatientData() {
        List<Patient> patients = Patientreponsitory.getListPatients();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Patient e : patients) {
            String genderStr = e.getGender() == 1 ? "Nam" : "Nữ";
            int age = calculateAge(e.getBirthDate());

            model.addRow(new Object[]{
                    e.getId(),
                    e.getName(),
                    e.getPhoneNumber(),
                    genderStr,
                    e.getBirthDate(),
                    e.getAddress(),
            });
        }
    }
    private int calculateAge(Date birthDate) {
        LocalDate birth = birthDate.toLocalDate();
        return Period.between(birth, LocalDate.now()).getYears();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý nhân viên");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new AdminPatient());
            frame.setVisible(true);
        });
    }
}
