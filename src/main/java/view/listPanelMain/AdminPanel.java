package view.listPanelMain;

import view.admin.*;
import view.dentistPanel.*;
import view.receptionistPanel.ReceptionistCalendarPanel;
import view.receptionistPanel.ReceptionistMenuPanel;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends Panel {
    private JPanel centerPanel;
    private CardLayout cardLayout;
    private DentistTaskbar dentistTaskbar;
    private AdminMenuPanel adminMenuPanel;
    // Thêm các Panel khác ở đây, khi cần thì xóa panel cũ rồi add mới vào, vị trí nó nằm ở center
    private DentistIntroducePanel dentistIntroducePanel;


    private AdminDrug adminDrug;
    private AdminDrugAdd adminDrugAdd;
    private AdminDrugEdit adminDrugEdit;
    private AdminDrugInfo adminDrugInfo;

    private AdminEmployee adminEmployee;
    private AdminEmployeeAdd adminEmployeeAdd;
    private AdminEmployeeEdit adminEmployeeEdit;
    private AdminEmployeeInfo adminEmployeeInfo;

    private AdminPatient adminPatient;

    private AdminService adminService;
    private AdminServiceAdd adminServiceAdd;
    private AdminServiceEdit adminServiceEdit;
    private AdminServiceInfo adminServiceInfo;

    private AdminStatistical adminStatistical;
    private AdminTaskbarPanel adminTaskbarPanel;


    public AdminPanel() {
        initComponents();
    }

    private void initComponents() {

        // Khởi tạo các thành phần
        dentistTaskbar = new DentistTaskbar();
        dentistIntroducePanel = new DentistIntroducePanel();

        adminDrug = new AdminDrug();
        adminDrugAdd = new AdminDrugAdd();
        adminDrugEdit = new AdminDrugEdit();
        adminDrugInfo = new AdminDrugInfo();
        adminEmployee = new AdminEmployee();
        adminEmployeeAdd = new AdminEmployeeAdd();
        adminEmployeeEdit = new AdminEmployeeEdit();
        adminEmployeeInfo = new AdminEmployeeInfo();
        adminMenuPanel = new AdminMenuPanel();
        adminPatient = new AdminPatient();
        adminService = new AdminService();
        adminServiceAdd = new AdminServiceAdd();
        adminServiceEdit = new AdminServiceEdit();
        adminServiceInfo = new AdminServiceInfo();
        adminStatistical = new AdminStatistical();
//        adminTaskbarPanel = new AdminTaskbarPanel();

        // Layout chính
        setLayout(new BorderLayout());

        // Menu bên trái
        adminMenuPanel.setPreferredSize(new Dimension(170, 450));
        adminMenuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(102, 102, 102)));
        add(adminMenuPanel, BorderLayout.WEST);

        // Taskbar trên cùng
        dentistTaskbar.setPreferredSize(new Dimension(125, 40));
        add(dentistTaskbar, BorderLayout.NORTH);

        // Cài đặt centerPanel với CardLayout
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setPreferredSize(new Dimension(700, 300));

        centerPanel.add(dentistIntroducePanel, "Introduce");
        centerPanel.add(adminDrug,"adminDrug");
        centerPanel.add(adminDrugAdd,"adminDrugAdd");
        centerPanel.add(adminDrugEdit, "adminDrugEdit");
        centerPanel.add(adminDrugInfo, "adminDrugInfo");

        centerPanel.add(adminEmployee, "adminEmployee");
        centerPanel.add(adminEmployeeAdd, "adminEmployeeAdd");
        centerPanel.add(adminEmployeeEdit, "adminEmployeeEdit");
        centerPanel.add(adminEmployeeInfo, "adminEmployeeInfo");

        centerPanel.add(adminPatient, "adminPatient");

        centerPanel.add(adminService, "adminService");
        centerPanel.add(adminServiceAdd, "adminServiceAdd");
        centerPanel.add(adminServiceEdit, "adminServiceEdit");
        centerPanel.add(adminServiceInfo, "adminServiceInfo");

        centerPanel.add(adminStatistical, "adminStatistical");

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

    public DentistTaskbar getDentistTaskbar() {
        return dentistTaskbar;
    }

    public void setDentistTaskbar(DentistTaskbar dentistTaskbar) {
        this.dentistTaskbar = dentistTaskbar;
    }

    public AdminMenuPanel getAdminMenuPanel() {
        return adminMenuPanel;
    }

    public void setAdminMenuPanel(AdminMenuPanel adminMenuPanel) {
        this.adminMenuPanel = adminMenuPanel;
    }

    public DentistIntroducePanel getDentistIntroducePanel() {
        return dentistIntroducePanel;
    }

    public void setDentistIntroducePanel(DentistIntroducePanel dentistIntroducePanel) {
        this.dentistIntroducePanel = dentistIntroducePanel;
    }

    public AdminDrug getAdminDrug() {
        return adminDrug;
    }

    public void setAdminDrug(AdminDrug adminDrug) {
        this.adminDrug = adminDrug;
    }

    public AdminDrugAdd getAdminDrugAdd() {
        return adminDrugAdd;
    }

    public void setAdminDrugAdd(AdminDrugAdd adminDrugAdd) {
        this.adminDrugAdd = adminDrugAdd;
    }

    public AdminDrugEdit getAdminDrugEdit() {
        return adminDrugEdit;
    }

    public void setAdminDrugEdit(AdminDrugEdit adminDrugEdit) {
        this.adminDrugEdit = adminDrugEdit;
    }

    public AdminDrugInfo getAdminDrugInfo() {
        return adminDrugInfo;
    }

    public void setAdminDrugInfo(AdminDrugInfo adminDrugInfo) {
        this.adminDrugInfo = adminDrugInfo;
    }

    public AdminEmployee getAdminEmployee() {
        return adminEmployee;
    }

    public void setAdminEmployee(AdminEmployee adminEmployee) {
        this.adminEmployee = adminEmployee;
    }

    public AdminEmployeeAdd getAdminEmployeeAdd() {
        return adminEmployeeAdd;
    }

    public void setAdminEmployeeAdd(AdminEmployeeAdd adminEmployeeAdd) {
        this.adminEmployeeAdd = adminEmployeeAdd;
    }

    public AdminEmployeeEdit getAdminEmployeeEdit() {
        return adminEmployeeEdit;
    }

    public void setAdminEmployeeEdit(AdminEmployeeEdit adminEmployeeEdit) {
        this.adminEmployeeEdit = adminEmployeeEdit;
    }

    public AdminEmployeeInfo getAdminEmployeeInfo() {
        return adminEmployeeInfo;
    }

    public void setAdminEmployeeInfo(AdminEmployeeInfo adminEmployeeInfo) {
        this.adminEmployeeInfo = adminEmployeeInfo;
    }

    public AdminPatient getAdminPatient() {
        return adminPatient;
    }

    public void setAdminPatient(AdminPatient adminPatient) {
        this.adminPatient = adminPatient;
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    public AdminServiceAdd getAdminServiceAdd() {
        return adminServiceAdd;
    }

    public void setAdminServiceAdd(AdminServiceAdd adminServiceAdd) {
        this.adminServiceAdd = adminServiceAdd;
    }

    public AdminServiceEdit getAdminServiceEdit() {
        return adminServiceEdit;
    }

    public void setAdminServiceEdit(AdminServiceEdit adminServiceEdit) {
        this.adminServiceEdit = adminServiceEdit;
    }

    public AdminServiceInfo getAdminServiceInfo() {
        return adminServiceInfo;
    }

    public void setAdminServiceInfo(AdminServiceInfo adminServiceInfo) {
        this.adminServiceInfo = adminServiceInfo;
    }

    public AdminStatistical getAdminStatistical() {
        return adminStatistical;
    }

    public void setAdminStatistical(AdminStatistical adminStatistical) {
        this.adminStatistical = adminStatistical;
    }

    public AdminTaskbarPanel getAdminTaskbarPanel() {
        return adminTaskbarPanel;
    }

    public void setAdminTaskbarPanel(AdminTaskbarPanel adminTaskbarPanel) {
        this.adminTaskbarPanel = adminTaskbarPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hệ thống quản lý - Giao diện Admin");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600); // hoặc kích thước phù hợp hơn nếu bạn muốn
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new AdminPanel());
            frame.setVisible(true);
        });
    }
}

