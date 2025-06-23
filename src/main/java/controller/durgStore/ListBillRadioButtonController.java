package controller.durgStore;

import view.listPanelMain.MainFrame;

import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListBillRadioButtonController implements ActionListener {
    private MainFrame view;
    public ListBillRadioButtonController(MainFrame view) {
        this.view = view;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        filterTable();
    }
    public void filterTable() {
        String status = "";
        if (view.getDrugStorePanel().getListBillPanel().getRbHT().isSelected()) {
            status = "Đã thanh toán";
        } else if (view.getDrugStorePanel().getListBillPanel().getRbChuaHT().isSelected()) {
            status = "Chưa thanh toán";
        }

        DefaultTableModel newModel = (DefaultTableModel) view.getDrugStorePanel().getListBillPanel().getTblPatients().getModel();
        newModel.setRowCount(0); // clear all

        for (Object[] row : view.getDrugStorePanel().getListBillPanel().getData()) {
            if (row[6] != null && row[6].toString().equalsIgnoreCase(status)) {
                newModel.addRow(row);
            }
        }
    }
}
