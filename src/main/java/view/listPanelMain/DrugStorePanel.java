package view.listPanelMain;

import Utils.ZaloPayQRPanel;
import view.doctorPanel.DoctorIntroducePanel;
import view.doctorPanel.DoctorTaskbar;
import view.durgStore.*;

import javax.swing.*;
import java.awt.*;

public class DrugStorePanel extends JPanel {
    private JPanel centerPanel;
    private CardLayout cardLayout;
    private DoctorTaskbar doctorTaskbar;
    private DrugStoreMenuPanel drugStoreMenuPanel;
    private ListDrugPanel listDrugPanel;
    private ListBillPanel listBillPanel;
    private DrugBillPanel billPanel;
    private DrugBillConfPanel billConfPanel;
    private DoctorIntroducePanel doctorIntroducePanel;
    private ZaloPayQRPanel zaloPayQRPanel;

    public DrugStorePanel() {
        initComponents();
    }

    private void initComponents() {
        // Khởi tạo các thành phần
        drugStoreMenuPanel=new DrugStoreMenuPanel();
        doctorTaskbar = new DoctorTaskbar();

        listBillPanel = new ListBillPanel();
        listDrugPanel=new ListDrugPanel();
        billPanel=new DrugBillPanel();
        billConfPanel  =new DrugBillConfPanel();
        doctorIntroducePanel =new DoctorIntroducePanel();
        zaloPayQRPanel=new ZaloPayQRPanel(2000,"Thanh toán hóa đơn #1");
        // Layout chính
        setLayout(new BorderLayout());

        // Menu bên trái
        drugStoreMenuPanel.setPreferredSize(new Dimension(170, 450));
        drugStoreMenuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(102, 102, 102)));
        add(drugStoreMenuPanel, BorderLayout.WEST);

        // Taskbar trên cùng
        doctorTaskbar.setPreferredSize(new Dimension(125, 40));
        add(doctorTaskbar, BorderLayout.NORTH);

        // Cài đặt centerPanel với CardLayout
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setPreferredSize(new Dimension(700, 300));

        centerPanel.add(doctorIntroducePanel,"IntroducePanel");
        centerPanel.add(listBillPanel,"Bills");
        centerPanel.add(listDrugPanel,"Drugs");
        centerPanel.add(billPanel,"Bill");
        centerPanel.add(billConfPanel,"BillConf");
        centerPanel.add(zaloPayQRPanel,"QR");

        // sau này cần add thêm ExaminationPanel hoặc CalendarPanel thì add luôn ở đây

        add(centerPanel, BorderLayout.CENTER); //Chỉ add centerPanel
    }

    public JPanel getCenterPanel() {
        return centerPanel;
    }

    public void setCenterPanel(JPanel centerPanel) {
        this.centerPanel = centerPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public void setCardLayout(CardLayout cardLayout) {
        this.cardLayout = cardLayout;
    }

    public DoctorTaskbar getDentistTaskbar() {
        return doctorTaskbar;
    }

    public void setDentistTaskbar(DoctorTaskbar doctorTaskbar) {
        this.doctorTaskbar = doctorTaskbar;
    }

    public DrugStoreMenuPanel getDrugStoreMenuPanel() {
        return drugStoreMenuPanel;
    }

    public void setDrugStoreMenuPanel(DrugStoreMenuPanel drugStoreMenuPanel) {
        this.drugStoreMenuPanel = drugStoreMenuPanel;
    }

    public ListDrugPanel getListDrugPanel() {
        return listDrugPanel;
    }

    public void setListDrugPanel(ListDrugPanel listDrugPanel) {
        this.listDrugPanel = listDrugPanel;
    }

    public ListBillPanel getListBillPanel() {
        return listBillPanel;
    }

    public void setListBillPanel(ListBillPanel listBillPanel) {
        this.listBillPanel = listBillPanel;
    }

    public DrugBillConfPanel getBillConfPanel() {
        return billConfPanel;
    }

    public void setBillConfPanel(DrugBillConfPanel billConfPanel) {
        this.billConfPanel = billConfPanel;
    }

    public DrugBillPanel getBillPanel() {
        return billPanel;
    }

    public void setBillPanel(DrugBillPanel billPanel) {
        this.billPanel = billPanel;
    }

    public ZaloPayQRPanel getZaloPayQRPanel() {
        return zaloPayQRPanel;
    }

    public void setZaloPayQRPanel(ZaloPayQRPanel zaloPayQRPanel) {
        this.zaloPayQRPanel = zaloPayQRPanel;
    }

    public DoctorIntroducePanel getDentistIntroducePanel() {
        return doctorIntroducePanel;
    }

    public void setDentistIntroducePanel(DoctorIntroducePanel doctorIntroducePanel) {
        this.doctorIntroducePanel = doctorIntroducePanel;
    }
}
