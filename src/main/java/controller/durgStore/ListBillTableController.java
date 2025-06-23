package controller.durgStore;

import reponsitory.DentistReponsitory;
import view.listPanelMain.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ListBillTableController extends MouseAdapter {
    private MainFrame view;
    public ListBillTableController(MainFrame view) {
        this.view = view;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        JTable table = (JTable) e.getSource();
        if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
            int row = table.getSelectedRow();
            String paymentStatus = table.getValueAt(row, 6).toString();

            if ("Đã thanh toán".equalsIgnoreCase(paymentStatus)) {
                JOptionPane.showMessageDialog(null, "Hóa đơn đã thanh toán, không thể sửa đổi!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String tenBN = table.getValueAt(row, 1).toString();
            System.out.println("Đã double-click bệnh nhân: " + tenBN);

            // Gọi giao diện xem hóa đơn
            switchDrugBillPanel(
                    table.getValueAt(row, 0).toString(),  // Mã hóa đơn
                    table.getValueAt(row, 1).toString(),  // Tên bệnh nhân
                    DentistReponsitory.getNameDenFormBill(table.getValueAt(row, 0).toString()),
                    table.getValueAt(row, 5).toString(),  // Tổng tiền
                    table.getValueAt(row, 6).toString()   // Trạng thái
            );
            switchDrugConfBillPanel(
                    table.getValueAt(row, 0).toString(),  // Mã hóa đơn
                    table.getValueAt(row, 1).toString(),  // Tên bệnh nhân
                    DentistReponsitory.getNameDenFormBill(table.getValueAt(row, 0).toString()),
                    table.getValueAt(row, 5).toString(),  // Tổng tiền
                    "Đã thanh toán"   // Trạng thái
            );
//            int idPre = Integer.parseInt(table.getValueAt(row, 0).toString());
//            BillDao.updatePaymentStatusToPaid(idPre);

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setValueAt("Đã thanh toán", row, 6); // Cập nhật cột 6 (trạng thái) tại dòng đang chọn

            view.getDrugStorePanel().getBillPanel().loadPrescriptionData(table.getValueAt(row, 0).toString());
            view.getDrugStorePanel().getBillConfPanel().loadPrescriptionData(table.getValueAt(row, 0).toString());
        }

    }
    public MainFrame getView() {
        return view;
    }

    public void setView(MainFrame view) {
        this.view = view;
    }
    public void switchDrugBillPanel(String id, String namePat, String nameDen, String price, String status) {
        view.getDrugStorePanel().getBillPanel().getIdBill().setText(id);
        view.getDrugStorePanel().getBillPanel().getNamePat().setText(namePat);
        view.getDrugStorePanel().getBillPanel().getNameDen().setText(nameDen);
        view.getDrugStorePanel().getBillPanel().getPrice().setText(String.valueOf(price));
        view.getDrugStorePanel().getBillPanel().getStatus().setText(status);
        view.getDrugStorePanel().getCardLayout().show(view.getDrugStorePanel().getCenterPanel(),"Bill");
    }
    public void switchDrugConfBillPanel(String id, String namePat, String nameDen, String price, String status) {
        view.getDrugStorePanel().getBillConfPanel().getIdBill().setText(id);
        view.getDrugStorePanel().getBillConfPanel().getNamePat().setText(namePat);
        view.getDrugStorePanel().getBillConfPanel().getNameDen().setText(nameDen);
        view.getDrugStorePanel().getBillConfPanel().getPrice().setText(String.valueOf(price));
        view.getDrugStorePanel().getBillConfPanel().getStatus().setText(status);
    }
}
