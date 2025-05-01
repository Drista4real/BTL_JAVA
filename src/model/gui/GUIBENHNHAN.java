package model.gui;

import model.backend.Demo1;
import model.entity.BENHNHAN;
import model.entity.BENHNHANBAOHIEMYTE;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GUI quản lý bệnh nhân
 */
public class GUIBENHNHAN extends javax.swing.JFrame {
    private JPanel mainPanel = new JPanel();
    Demo1 danhsach = new Demo1(mainPanel);
    String col[] = {"Mã bệnh nhân", "Họ tên", "Ngày nhập viện", " Phòng theo yêu cầu", "Loại bảo hiểm"};
    DefaultTableModel tableModel;
    DefaultTableModel model;

    private java.awt.Button btnDocFile;
    private java.awt.Button btnGhiFile;
    private java.awt.Button btnThem;
    private java.awt.Button btnTim;
    private java.awt.Button btnXoa;
    private javax.swing.JCheckBox ckbPhongTYC;
    private java.awt.Choice cobLoaiBH;
    private javax.swing.JScrollPane jScrollPane;
    private java.awt.Label labHoten;
    private java.awt.Label labLoaiBH;
    private java.awt.Label labMBN;
    private java.awt.Label labMaXH;
    private java.awt.Label labMaYT;
    private java.awt.Label labNNV;
    private java.awt.Label labPhongTYC;
    private javax.swing.JTable tab_DSBENHNHAN;
    private javax.swing.JTextField txtHoten;
    private javax.swing.JTextField txtMABN;
    private javax.swing.JTextField txtMaBHXH;
    private javax.swing.JTextField txtMaBHYT;
    private javax.swing.JTextField txtNgaynhapvien;

    public GUIBENHNHAN() {
        this.getContentPane().setBackground(new Color(240, 240, 240));
        initComponents();
        this.cobLoaiBH.addItem("y");
        // Xóa lựa chọn 'x' vì không còn lớp BENHNHANBAOHIEMXAHOI
        // this.cobLoaiBH.addItem("x");

        danhsach = new Demo1(mainPanel);
        try {
            danhsach.DocFile();
            LoadDataTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi đọc file: " + e.getMessage());
        }
        setSize(600, 600);

        // Ẩn trường không cần thiết vì không còn BHXH
        labMaXH.setVisible(false);
        txtMaBHXH.setVisible(false);
    }

    public void LoadDataTable() {
        tableModel = new DefaultTableModel(col, 0);
        for (BENHNHAN bn : this.danhsach.getDanhsach().values()) {
            SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");

            Object[] row = {bn.getMABN(), bn.getHoten(), fmd.format(bn.getNgaynhapvien()), bn.getPhongTYC(), bn.getLoaiBH()};
            tableModel.addRow(row);
        }

        this.tab_DSBENHNHAN.setModel(tableModel);
    }

    public BENHNHAN ThemBenhnhan() {
        BENHNHAN benhnhan = null;
        try {
            SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
            Date NgayNV = fmd.parse(txtNgaynhapvien.getText());

            // Chỉ tạo BENHNHANBAOHIEMYTE vì BENHNHANBAOHIEMXAHOI đã bị xóa
            benhnhan = new BENHNHANBAOHIEMYTE('y', txtMABN.getText(), "", null, txtHoten.getText(), NgayNV, txtMaBHYT.getText(), ckbPhongTYC.isSelected());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày nhập viện không đúng định dạng (dd/MM/yyyy)");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm bệnh nhân: " + e.getMessage());
        }
        return benhnhan;
    }

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {
        BENHNHAN benhnhan = ThemBenhnhan();
        if (benhnhan != null) {
            this.danhsach.NhapGUI(benhnhan);
            try {
                this.danhsach.GhiFile();
                // Bỏ gọi getDashboardPanel() để tránh lỗi
                // Nếu muốn, bạn có thể thêm kiểm tra và gọi an toàn như sau:
                /*
                try {
                    if (model.gui.MainFrame.getInstance() != null) {
                        model.gui.MainFrame.getInstance().getDashboardPanel().refreshStats();
                    }
                } catch (NoSuchMethodError | NullPointerException ex) {
                    // Bỏ qua nếu chưa có phương thức hoặc lỗi
                }
                */
            } catch (IOException ex) {
                Logger.getLogger(GUIBENHNHAN.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, "Thêm mới bệnh nhân thành công");
        } else {
            JOptionPane.showMessageDialog(this, "Thêm mới bệnh nhân thất bại");
        }
        LoadDataTable();
    }

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {
        int result = JOptionPane.showConfirmDialog(this, "Bạn có xóa thông tin này không!", "Thông báo", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            this.danhsach.Xoa(txtMABN.getText());
            try {
                this.danhsach.GhiFile();
                // Bỏ gọi getDashboardPanel() để tránh lỗi
            } catch (IOException ex) {
                Logger.getLogger(GUIBENHNHAN.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, "Thông tin bệnh nhân đã xóa thành công");
        } else {
            JOptionPane.showMessageDialog(this, "Thông tin bệnh nhân không được xóa");
        }
        LoadDataTable();
    }

    public void setvalue(BENHNHAN bn) {
        if (bn != null) {
            SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
            String NgayNV = fmd.format(bn.getNgaynhapvien());
            txtMaBHYT.setText(null);
            txtMaBHXH.setText(null);

            txtMABN.setText(bn.getMABN());
            txtHoten.setText(bn.getHoten());

            // Chỉ còn BENHNHANBAOHIEMYTE
            if (bn instanceof BENHNHANBAOHIEMYTE) {
                txtMaBHYT.setText(((BENHNHANBAOHIEMYTE) bn).getMSBH());
            }

            txtNgaynhapvien.setText(NgayNV);
            cobLoaiBH.select(String.valueOf(bn.getLoaiBH()));
            ckbPhongTYC.setSelected(bn.getPhongTYC());
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy bệnh nhân");
        }
    }

    private void btnTimActionPerformed(java.awt.event.ActionEvent evt) {
        String maBN = JOptionPane.showInputDialog(this, "Nhập mã bệnh nhân cần tìm:");
        if (maBN == null || maBN.trim().isEmpty()) {
            return;
        }

        BENHNHAN bn = danhsach.Tim(maBN);
        if (bn == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy bệnh nhân có mã: " + maBN);
            return;
        }

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Mã bệnh nhân");
        model.addColumn("Họ tên");
        model.addColumn("Ngày nhập viện");
        model.addColumn("Phòng theo yêu cầu");
        model.addColumn("Loại bảo hiểm");
        model.addColumn("Mã bảo hiểm");

        SimpleDateFormat fmd = new SimpleDateFormat("dd/MM/yyyy");
        String maBH = "";

        if (bn instanceof BENHNHANBAOHIEMYTE) {
            maBH = ((BENHNHANBAOHIEMYTE) bn).getMSBH();
        }

        model.addRow(new Object[]{
                bn.getMABN(),
                bn.getHoten(),
                fmd.format(bn.getNgaynhapvien()),
                bn.getPhongTYC() ? "Có" : "Không",
                bn.getLoaiBH() == 'y' ? "BHYT" : "",
                maBH
        });

        tab_DSBENHNHAN.setModel(model);
        setvalue(bn);
    }

    private void tab_DSBENHNHANMouseClicked(java.awt.event.MouseEvent evt) {
        int row = tab_DSBENHNHAN.getSelectedRow();
        if (row < 0) return;
        String Mabn = tab_DSBENHNHAN.getValueAt(row, 0).toString();

        setvalue(this.danhsach.Tim(Mabn));
    }

    private void btnGhiFileActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            danhsach.GhiFile();
        } catch (IOException ex) {
            Logger.getLogger(GUIBENHNHAN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void btnDocFileActionPerformed(java.awt.event.ActionEvent evt) {
        danhsach.DocFile();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Mã BN");
        model.addColumn("Họ Tên");
        model.addColumn("Ngày nhập viện");
        model.addColumn("Phòng YC");
        model.addColumn("Loại BH");
        model.addColumn("Tính Tiền");

        for (BENHNHAN bn : this.danhsach.getDanhsach().values()) {
            model.addRow(bn.toArray());
        }
        tab_DSBENHNHAN.setModel(model);
        JOptionPane.showMessageDialog(this, "Đã Đọc File Thành Công");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jScrollPane = new javax.swing.JScrollPane();
        tab_DSBENHNHAN = new javax.swing.JTable();
        btnThem = new java.awt.Button();
        btnXoa = new java.awt.Button();
        btnTim = new java.awt.Button();
        btnGhiFile = new java.awt.Button();
        btnDocFile = new java.awt.Button();
        labMBN = new java.awt.Label();
        labHoten = new java.awt.Label();
        labNNV = new java.awt.Label();
        labLoaiBH = new java.awt.Label();
        labMaYT = new java.awt.Label();
        labMaXH = new java.awt.Label();
        labPhongTYC = new java.awt.Label();
        txtMABN = new javax.swing.JTextField();
        txtHoten = new javax.swing.JTextField();
        txtNgaynhapvien = new javax.swing.JTextField();
        cobLoaiBH = new java.awt.Choice();
        txtMaBHYT = new javax.swing.JTextField();
        txtMaBHXH = new javax.swing.JTextField();
        ckbPhongTYC = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quản lý bệnh nhân");

        tab_DSBENHNHAN.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"Mã bệnh nhân", "Họ tên", "Ngày nhập viện", "Phòng theo yêu cầu", "Loại bảo hiểm"}
        ));
        tab_DSBENHNHAN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab_DSBENHNHANMouseClicked(evt);
            }
        });
        jScrollPane.setViewportView(tab_DSBENHNHAN);

        btnThem.setLabel("Thêm");
        btnThem.addActionListener(this::btnThemActionPerformed);

        btnXoa.setLabel("Xóa");
        btnXoa.addActionListener(this::btnXoaActionPerformed);

        btnTim.setLabel("Tìm");
        btnTim.addActionListener(this::btnTimActionPerformed);

        btnGhiFile.setLabel("Ghi File");
        btnGhiFile.addActionListener(this::btnGhiFileActionPerformed);

        btnDocFile.setLabel("Đọc File");
        btnDocFile.addActionListener(this::btnDocFileActionPerformed);

        labMBN.setText("Mã bệnh nhân:");
        labHoten.setText("Họ tên:");
        labNNV.setText("Ngày nhập viện:");
        labLoaiBH.setText("Loại bảo hiểm:");
        labMaYT.setText("Mã BHYT:");
        labMaXH.setText("Mã BHXH:");
        labPhongTYC.setText("Phòng theo yêu cầu:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(labMBN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtMABN, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(labHoten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtHoten, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(labNNV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtNgaynhapvien, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(labLoaiBH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(cobLoaiBH, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(labMaYT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtMaBHYT, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(labMaXH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtMaBHXH, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(labPhongTYC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(ckbPhongTYC)))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnTim, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnGhiFile, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnDocFile, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labMBN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtMABN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labHoten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtHoten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labNNV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtNgaynhapvien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labLoaiBH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(cobLoaiBH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labMaYT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtMaBHYT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labMaXH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtMaBHXH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labPhongTYC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(ckbPhongTYC)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnTim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnGhiFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnDocFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }
}
