package controller.durgStore;

import Utils.ZaloPayQRPanel;
import kong.unirest.json.JSONObject;
import reponsitory.BillReponsitory;
import reponsitory.DrugReponsitory;
import service.ExportToPDF;
import view.listPanelMain.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonPaymentController implements ActionListener {
    private MainFrame view;
    public int id;

    public ButtonPaymentController(MainFrame mainFrame) {
        this.view = mainFrame;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals("Tiền mặt")) {
            switchPaySus();
        } else if (actionCommand.equals("Quét mã")) {
            String idStr = view.getDrugStorePanel().getBillPanel().getIdBill().getText();
            id = Integer.parseInt(idStr);
            String content = "Thanh toán hóa đơn #" + id;

            ZaloPayQRPanel qrPanel = new ZaloPayQRPanel(BillReponsitory.getPriceInPre(id), content);
            view.getDrugStorePanel().setZaloPayQRPanel(qrPanel);
            view.getDrugStorePanel().getCenterPanel().add(qrPanel,"QR");

            view.getDrugStorePanel().getCardLayout().show(
                    view.getDrugStorePanel().getCenterPanel(), "QR"
            );
            qrPanel.checkThisOrderStatusAsync(this);

        }
    }
    public void switchPaySus() {
        String idStr = view.getDrugStorePanel().getBillPanel().getIdBill().getText();
        int id = Integer.parseInt(idStr);

        BillReponsitory.updatePaymentStatusToPaid(id);

        view.getDrugStorePanel().getBillConfPanel().getStatus().setText("Đã thanh toán");

        ExportToPDF.billToPDF(id+"");

        view.getDrugStorePanel().getCardLayout().show(view.getDrugStorePanel().getCenterPanel(), "BillConf");
        // Cập nhật số lượng thuốc
        DefaultTableModel model = (DefaultTableModel) view.getDrugStorePanel().getBillPanel().getTableMedicine().getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String drugName = model.getValueAt(i, 1).toString();        // Tên thuốc
            int quantity = Integer.parseInt(model.getValueAt(i, 2).toString());  // Số lượng
            DrugReponsitory.updateDrugQuantity(drugName, quantity);
        }
    }
    public MainFrame getView() {
        return view;
    }

    public void setView(MainFrame view) {
        this.view = view;
    }
}
