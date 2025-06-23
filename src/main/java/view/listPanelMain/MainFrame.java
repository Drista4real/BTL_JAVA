package view.listPanelMain;

import controller.admin.*;
import controller.dentist.*;
import controller.durgStore.ButtonPaymentController;
import controller.durgStore.DrugMenuController;
import controller.durgStore.ListBillRadioButtonController;
import controller.durgStore.ListBillTableController;
import controller.login.LoginButtonController;
import controller.receptionist.ReceptionTableController;
import controller.receptionist.ReceptionistLableController;
import controller.receptionist.ReceptionistSearch2Controller;
import controller.receptionist.ReceptionistSearchController;
import view.login.LoginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

public class MainFrame extends JFrame {
    private JPanel containerPanel;
    private CardLayout cardLayout;
    private LoginPanel loginPanel;
    private DentistPanel mainPanel;
    private ReceptionistPanel receptionistPanel;
    private DrugStorePanel drugStorePanel;
    private AdminPanel adminPanel;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        // Khởi tạo layout và panel
        loginPanel = new LoginPanel();
        mainPanel = new DentistPanel();
        receptionistPanel = new ReceptionistPanel();
        drugStorePanel =new DrugStorePanel();
        adminPanel = new AdminPanel();

        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);

        // Thêm các card vào containerPanel
        containerPanel.add(loginPanel, "LoginPanel");
        containerPanel.add(mainPanel, "DentistPanel");
        containerPanel.add(receptionistPanel, "ReceptionistPanel");
        containerPanel.add(drugStorePanel,"drugstore");
        containerPanel.add(adminPanel, "AdminPanel");
        // Set layout chính cho MainFrame
        setLayout(new BorderLayout());
        add(containerPanel, BorderLayout.CENTER);

        // Ban đầu show login
        cardLayout.show(containerPanel, "LoginPanel");

        // Nếu muốn chuyển panel theo role lúc đăng nhập xong kiểm tra
        addListeners();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    //Thêm sự kiện
    public void addListeners() {
        this.mainPanel.getDentistTaskbar().getLblDoctorName().setName("Login");
        this.mainPanel.getDentistMenuPanel().getLblHome().setName("Home");
        this.mainPanel.getDentistMenuPanel().getLblSchedule().setName("Schedule");
        this.mainPanel.getDentistMenuPanel().getLblPatients().setName("Patients");
        this.mainPanel.getDentistMenuPanel().getLblFeature1().setName("Feature 1");
        this.mainPanel.getDentistMenuPanel().getLblFeature2().setName("Feature 2");

        //add xử lí cho menu
        DentistManagerLableController ml = new DentistManagerLableController(this);
        this.mainPanel.getDentistTaskbar().getLblDoctorName().addMouseListener(ml);
        this.mainPanel.getDentistMenuPanel().getLblHome().addMouseListener(ml);
        this.mainPanel.getDentistMenuPanel().getLblSchedule().addMouseListener(ml);
        this.mainPanel.getDentistMenuPanel().getLblPatients().addMouseListener(ml);
        this.mainPanel.getDentistMenuPanel().getLblFeature1().addMouseListener(ml);
        this.mainPanel.getDentistMenuPanel().getLblFeature2().addMouseListener(ml);
        ml.setLabelEvent(this.mainPanel.getDentistMenuPanel().getLblHome(),
                this.mainPanel.getDentistMenuPanel().getLblSchedule(),
                this.mainPanel.getDentistMenuPanel().getLblPatients(),
                this.mainPanel.getDentistMenuPanel().getLblFeature1(),
                this.mainPanel.getDentistMenuPanel().getLblFeature2());

        //add xử lí khi ấn vào bệnh nhân có trong lịch
        DentistManagerTableController ma=new DentistManagerTableController(this);
        this.mainPanel.getDentistListPatient().getTblPatients().addMouseListener(ma);
//        this.mainPanel.getDentistListPatient2().getTblPatients().addMouseListener(ma);
        //add xử lí khi tìm kiếm
        this.mainPanel.getDentistListPatient().getTfSearch().getDocument().addDocumentListener(
                new DentistPatientSearch1Controller(this.mainPanel.getDentistListPatient())
        );
        this.mainPanel.getDentistListPatient2().getTfSearch().getDocument().addDocumentListener(
                new DentistPatient2SearchController(this.mainPanel.getDentistListPatient2())
        );
        this.receptionistPanel.getShowPatientsReceptionistPanel().getTxtSearch().getDocument().addDocumentListener(
                new ReceptionistSearchController(this.receptionistPanel.getShowPatientsReceptionistPanel())
        );
        this.receptionistPanel.getReceptionistCalendarPanel().getTfSearch().getDocument().addDocumentListener(
                new ReceptionistSearch2Controller(this.receptionistPanel.getReceptionistCalendarPanel())
        );
        //set name cho các label menu của lễ tân
        this.receptionistPanel.getDentistTaskbar().getLblDoctorName().setName("Login");
        this.receptionistPanel.getReceptionistMenuPanel().getLblHome().setName("Home");//intro
        this.receptionistPanel.getReceptionistMenuPanel().getLblSchedule().setName("Schedule");// xem lịch khám
        this.receptionistPanel.getReceptionistMenuPanel().getLblPatients().setName("Patients");//xem bệnh nhân
        this.receptionistPanel.getReceptionistMenuPanel().getLblFeature1().setName("addPatient");//thêm bệnh nhân
        //add xử lí khi ấn menu trong giao diện lễ tân
        ReceptionistLableController mlr=new ReceptionistLableController(this);
        this.receptionistPanel.getDentistTaskbar().getLblDoctorName().addMouseListener(mlr);
        this.receptionistPanel.getReceptionistMenuPanel().getLblHome().addMouseListener(mlr);
        this.receptionistPanel.getReceptionistMenuPanel().getLblSchedule().addMouseListener(mlr);
        this.receptionistPanel.getReceptionistMenuPanel().getLblPatients().addMouseListener(mlr);
        this.receptionistPanel.getReceptionistMenuPanel().getLblFeature1().addMouseListener(mlr);
        mlr.setLabelEvent(this.receptionistPanel.getReceptionistMenuPanel().getLblHome(),
                this.receptionistPanel.getReceptionistMenuPanel().getLblSchedule(),
                this.receptionistPanel.getReceptionistMenuPanel().getLblPatients(),
                this.receptionistPanel.getReceptionistMenuPanel().getLblFeature1());

        //add xử lí khi button dentist bị ấn
        ActionListener ald=new DentistManagerButtonController(this);
        this.mainPanel.getDentistExaminationPanel().getBtnAddService().addActionListener(ald);
        this.mainPanel.getDentistExaminationPanel().getBtnCancel().addActionListener(ald);
        this.mainPanel.getServicePanel().getBtnAddDrugs().addActionListener(ald);
        //chưa thấy nút hủy cua dich vu
        this.mainPanel.getAddPrescriptionPanel().getBtnAddDrugs().addActionListener(ald);
        this.mainPanel.getAddPrescriptionPanel().getBtnConfirm().addActionListener(ald);
        //
        ActionListener all=new LoginButtonController(this);
        this.loginPanel.getLoginButton().addActionListener(all);

        ActionListener allb=new ListBillRadioButtonController(this);
        this.drugStorePanel.getListBillPanel().getRbHT().addActionListener(allb);
        this.drugStorePanel.getListBillPanel().getRbChuaHT().addActionListener(allb);
        //thêm sự kiện khi click vào sửa, thêm lịch khám và tái khám
        MouseListener mlrep=new ReceptionTableController(this);
        this.receptionistPanel.getShowPatientsReceptionistPanel().getPatientActionTable().addMouseListener(mlrep);
        //thêm sự kiện khi thanh toán
        ListBillTableController lbtc=new ListBillTableController(this);
        this.drugStorePanel.getListBillPanel().getTblPatients().addMouseListener(lbtc);
        //set name cho label của nvqt
        this.drugStorePanel.getDrugStoreMenuPanel().getLblHome().setName("Home");
        this.drugStorePanel.getDentistTaskbar().getLblDoctorName().setName("Login");
        this.drugStorePanel.getDrugStoreMenuPanel().getLblBill().setName("Bills");
        this.drugStorePanel.getDrugStoreMenuPanel().getLblListDrug().setName("ListMedicine");

        DrugMenuController dmct=new DrugMenuController(this);
        this.drugStorePanel.getDrugStoreMenuPanel().getLblHome().addMouseListener(dmct);
        this.drugStorePanel.getDrugStoreMenuPanel().getLblBill().addMouseListener(dmct);
        this.drugStorePanel.getDrugStoreMenuPanel().getLblListDrug().addMouseListener(dmct);
        this.drugStorePanel.getDentistTaskbar().getLblDoctorName().addMouseListener(dmct);
        dmct.setLabelEvent(this.drugStorePanel.getDrugStoreMenuPanel().getLblHome(),
                this.drugStorePanel.getDrugStoreMenuPanel().getLblBill(),
                this.drugStorePanel.getDrugStoreMenuPanel().getLblListDrug());

        ButtonPaymentController bp=new ButtonPaymentController(this);
        this.drugStorePanel.getBillPanel().getButtonCash().addActionListener(bp);
        this.drugStorePanel.getBillPanel().getButtonQRCode().addActionListener(bp);

        this.adminPanel.getAdminMenuPanel().getLblHome().setName("Home");
        this.adminPanel.getAdminMenuPanel().getLblEmp().setName("ListEmployee");
        this.adminPanel.getAdminMenuPanel().getLblDrugs().setName("ListDrug");
        this.adminPanel.getAdminMenuPanel().getLblService().setName("ListService");
        this.adminPanel.getAdminMenuPanel().getLblStatistic().setName("TK");
        this.adminPanel.getAdminMenuPanel().getLblPatient().setName("ListPatient");
        this.adminPanel.getDentistTaskbar().getLblDoctorName().setName("Login");
        //them controller cho admin
        LableController lb=new LableController(this);
        this.adminPanel.getAdminMenuPanel().getLblHome().addMouseListener(lb);
        this.adminPanel.getAdminMenuPanel().getLblEmp().addMouseListener(lb);
        this.adminPanel.getAdminMenuPanel().getLblPatient().addMouseListener(lb);
        this.adminPanel.getAdminMenuPanel().getLblService().addMouseListener(lb);
        this.adminPanel.getAdminMenuPanel().getLblStatistic().addMouseListener(lb);
        this.adminPanel.getDentistTaskbar().getLblDoctorName().addMouseListener(lb);
        this.adminPanel.getAdminMenuPanel().getLblDrugs().addMouseListener(lb);
        lb.setLabelEvent(this.adminPanel.getAdminMenuPanel().getLblHome(),
                this.adminPanel.getAdminMenuPanel().getLblEmp(),
                this.adminPanel.getAdminMenuPanel().getLblPatient(),
                this.adminPanel.getAdminMenuPanel().getLblService(),
                this.adminPanel.getAdminMenuPanel().getLblStatistic(),
                this.adminPanel.getAdminMenuPanel().getLblDrugs());

        EmployeeButtonController ebtc=new EmployeeButtonController(this);
        this.adminPanel.getAdminEmployee().getBtnAdd().addActionListener(ebtc);
        this.adminPanel.getAdminEmployee().getTfSearch().getDocument().addDocumentListener(
                new EmployeeSearchController(this.adminPanel.getAdminEmployee())
        );
        this.adminPanel.getAdminEmployeeInfo().getBtnDelete().addActionListener(ebtc);
        this.adminPanel.getAdminEmployeeInfo().getBtnEdit().addActionListener(ebtc);
        this.adminPanel.getAdminEmployeeEdit().getBtnConf().addActionListener(ebtc);
        this.adminPanel.getAdminEmployeeAdd().getBtnAdd().addActionListener(ebtc);

        ServiceButtonController sbtc=new ServiceButtonController(this);
        this.adminPanel.getAdminService().getBtnAdd().addActionListener(sbtc);
        this.adminPanel.getAdminServiceInfo().getBtnDelete().addActionListener(sbtc);
        this.adminPanel.getAdminServiceInfo().getBtnEdit().addActionListener(sbtc);
        this.adminPanel.getAdminServiceEdit().getBtnConf().addActionListener(sbtc);
        this.adminPanel.getAdminServiceAdd().getBtnAdd().addActionListener(ebtc);

        DrugButtonController dbcl=new DrugButtonController(this);
        this.adminPanel.getAdminDrug().getBtnAdd().addActionListener(dbcl);
        this.adminPanel.getAdminDrugInfo().getBtnDelete().addActionListener(dbcl);
        this.adminPanel.getAdminDrugEdit().getBtnConf().addActionListener(dbcl);
        this.adminPanel.getAdminDrugInfo().getBtnEdit().addActionListener(dbcl);
        this.adminPanel.getAdminDrugAdd().getBtnAdd().addActionListener(dbcl);
        // controller table
        EmployeeTableController etct=new EmployeeTableController(this);
        this.adminPanel.getAdminEmployee().getTable().addMouseListener(etct);
        ServiceTableController stct=new ServiceTableController(this);
        this.adminPanel.getAdminService().getTable().addMouseListener(stct);
        DrugTableController dtcl=new DrugTableController(this);
        this.adminPanel.getAdminDrug().getTable().addMouseListener(dtcl);
    }

    public DentistPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(DentistPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public ReceptionistPanel getReceptionistPanel() {
        return receptionistPanel;
    }

    public void setReceptionistPanel(ReceptionistPanel receptionistPanel) {
        this.receptionistPanel = receptionistPanel;
    }

    public LoginPanel getLoginPanel() {
        return loginPanel;
    }

    public void setLoginPanel(LoginPanel loginPanel) {
        this.loginPanel = loginPanel;
    }

    public JPanel getContainerPanel() {
        return containerPanel;
    }

    public void setContainerPanel(JPanel containerPanel) {
        this.containerPanel = containerPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public void setCardLayout(CardLayout cardLayout) {
        this.cardLayout = cardLayout;
    }

    public DrugStorePanel getDrugStorePanel() {
        return drugStorePanel;
    }

    public void setDrugStorePanel(DrugStorePanel drugStorePanel) {
        this.drugStorePanel = drugStorePanel;
    }

    public AdminPanel getAdminPanel() {
        return adminPanel;
    }

    public void setAdminPanel(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
