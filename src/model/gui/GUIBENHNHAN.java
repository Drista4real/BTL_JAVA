package model.gui;

import model.entity.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GUIBENHNHAN extends JFrame {
    private JPanel mainPanel = new JPanel();
    private String[] col = {"Mã bệnh nhân", "Họ tên", "Ngày nhập viện", "Phòng theo yêu cầu", "Loại bảo hiểm", "Mã bảo hiểm"};
    private DefaultTableModel tableModel;

    private JButton btnThem;
    private JButton btnXoa;
    private JButton btnTim;
    private JCheckBox ckbPhongTYC;
    private JComboBox<String> cobLoaiBH;
    private JScrollPane jScrollPane;
    private JLabel labHoten;
    private JLabel labLoaiBH;
    private JLabel labMBN;
    private JLabel labMaYT;
    private JLabel labNNV;
    private JLabel labPhongTYC;
    private JTable tab_DSBENHNHAN;
    private JTextField txtHoten;
    private JTextField txtMABN;
    private JTextField txtMaBHYT;
    private JTextField txtNgaynhapvien;

    public GUIBENHNHAN() {
        this.getContentPane().setBackground(new Color(240, 240, 240));
        initComponents();
        loadDataTable();
        setSize(600, 600);
        setTitle("Quản lý bệnh nhân");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        labMBN = new JLabel("Mã bệnh nhân:");
        labHoten = new JLabel("Họ tên:");
        labNNV = new JLabel("Ngày nhập viện:");
        labLoaiBH = new JLabel("Loại bảo hiểm:");
        labMaYT = new JLabel("Mã bảo hiểm:");
        labPhongTYC = new JLabel("Phòng theo yêu cầu:");

        txtMABN = new JTextField(15);
        txtHoten = new JTextField(15);
        txtNgaynhapvien = new JTextField(15);
        cobLoaiBH = new JComboBox<>(new String[]{"BHYT", "Không có"});
        txtMaBHYT = new JTextField(15);
        ckbPhongTYC = new JCheckBox();

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(labMBN, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtMABN, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(labHoten, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtHoten, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(labNNV, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtNgaynhapvien, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(labLoaiBH, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(cobLoaiBH, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(labMaYT, gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(txtMaBHYT, gbc);
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(labPhongTYC, gbc);
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(ckbPhongTYC, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnThem = new JButton("Thêm");
        btnXoa = new JButton("Xóa");
        btnTim = new JButton("Tìm");
        buttonPanel.add(btnThem);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnTim);

        tableModel = new DefaultTableModel(col, 0);
        tab_DSBENHNHAN = new JTable(tableModel);
        jScrollPane = new JScrollPane(tab_DSBENHNHAN);

        add(jScrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        btnThem.addActionListener(e -> btnThemActionPerformed());
        btnXoa.addActionListener(e -> btnXoaActionPerformed());
        btnTim.addActionListener(e -> btnTimActionPerformed());
        tab_DSBENHNHAN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab_DSBENHNHANMouseClicked();
            }
        });
    }

    private void loadDataTable() {
        tableModel.setRowCount(0);
        List<User> patients = PatientManagementDAO.getAllPatients();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (User user : patients) {
            BENHNHAN patient = PatientManagementDAO.getPatientById(user.getUserId());
            if (patient != null) {
                String maBH = patient instanceof BENHNHANBAOHIEMYTE ? ((BENHNHANBAOHIEMYTE) patient).getMSBH() : "";
                Object[] row = {
                        patient.getMABN(),
                        patient.getHoten(),
                        patient.getNgaynhapvien() != null ? patient.getNgaynhapvien().format(formatter) : "",
                        patient.getPhongTYC() ? "Có" : "Không",
                        patient.getLoaiBH() == 'y' ? "BHYT" : "Không có",
                        maBH
                };
                tableModel.addRow(row);
            }
        }
    }

    private BENHNHAN themBenhNhan() {
        try {
            String maBN = txtMABN.getText().trim();
            String hoTen = txtHoten.getText().trim();
            String ngayNhapVienStr = txtNgaynhapvien.getText().trim();
            String loaiBH = (String) cobLoaiBH.getSelectedItem();
            String maBH = txtMaBHYT.getText().trim();
            boolean phongTYC = ckbPhongTYC.isSelected();

            if (maBN.isEmpty() || hoTen.isEmpty() || ngayNhapVienStr.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập đầy đủ mã bệnh nhân, họ tên và ngày nhập viện");
            }

            LocalDate ngayNhapVien;
            try {
                ngayNhapVien = LocalDate.parse(ngayNhapVienStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Ngày nhập viện không đúng định dạng (dd/MM/yyyy)");
            }

            return new BENHNHANBAOHIEMYTE(
                    loaiBH.equals("BHYT") ? 'y' : 'n',
                    maBN,
                    "",
                    null,
                    hoTen,
                    ngayNhapVien,
                    maBH,
                    phongTYC,
                    null
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm bệnh nhân: " + e.getMessage());
            return null;
        }
    }

    private void btnThemActionPerformed() {
        BENHNHAN benhNhan = themBenhNhan();
        if (benhNhan != null) {
            boolean success = PatientManagementDAO.savePatient(benhNhan);
            if (success) {
                loadDataTable();
                JOptionPane.showMessageDialog(this, "Thêm mới bệnh nhân thành công");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm mới bệnh nhân thất bại");
            }
        }
    }

    private void btnXoaActionPerformed() {
        int row = tab_DSBENHNHAN.getSelectedRow();
        if (row >= 0) {
            String maBN = (String) tab_DSBENHNHAN.getValueAt(row, 0);
            int result = JOptionPane.showConfirmDialog(this, "Bạn có muốn xóa bệnh nhân " + maBN + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                boolean success = PatientManagementDAO.deletePatient(maBN);
                if (success) {
                    loadDataTable();
                    JOptionPane.showMessageDialog(this, "Xóa bệnh nhân thành công");
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa bệnh nhân thất bại");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bệnh nhân để xóa");
        }
    }

    private void btnTimActionPerformed() {
        String maBN = JOptionPane.showInputDialog(this, "Nhập mã bệnh nhân cần tìm:");
        if (maBN == null || maBN.trim().isEmpty()) {
            return;
        }

        BENHNHAN bn = PatientManagementDAO.getPatientById(maBN);
        if (bn == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy bệnh nhân có mã: " + maBN);
            return;
        }

        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String maBH = bn instanceof BENHNHANBAOHIEMYTE ? ((BENHNHANBAOHIEMYTE) bn).getMSBH() : "";
        DefaultTableModel model = (DefaultTableModel) tableModel;
        model.addRow(new Object[]{
                bn.getMABN(),
                bn.getHoten(),
                bn.getNgaynhapvien() != null ? bn.getNgaynhapvien().format(formatter) : "",
                bn.getPhongTYC() ? "Có" : "Không",
                bn.getLoaiBH() == 'y' ? "BHYT" : "Không có",
                maBH
        });
        setValue(bn);
    }

    private void tab_DSBENHNHANMouseClicked() {
        int row = tab_DSBENHNHAN.getSelectedRow();
        if (row >= 0) {
            String maBN = (String) tab_DSBENHNHAN.getValueAt(row, 0);
            BENHNHAN bn = PatientManagementDAO.getPatientById(maBN);
            setValue(bn);
        }
    }

    private void setValue(BENHNHAN bn) {
        if (bn != null) {
            txtMABN.setText(bn.getMABN());
            txtHoten.setText(bn.getHoten());
            txtNgaynhapvien.setText(bn.getNgaynhapvien() != null ? bn.getNgaynhapvien().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            cobLoaiBH.setSelectedItem(bn.getLoaiBH() == 'y' ? "BHYT" : "Không có");
            txtMaBHYT.setText(bn instanceof BENHNHANBAOHIEMYTE ? ((BENHNHANBAOHIEMYTE) bn).getMSBH() : "");
            ckbPhongTYC.setSelected(bn.getPhongTYC());
        } else {
            txtMABN.setText("");
            txtHoten.setText("");
            txtNgaynhapvien.setText("");
            cobLoaiBH.setSelectedItem("Không có");
            txtMaBHYT.setText("");
            ckbPhongTYC.setSelected(false);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.awt.EventQueue.invokeLater(() -> new GUIBENHNHAN().setVisible(true));
    }
}