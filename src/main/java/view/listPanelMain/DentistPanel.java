package view.listPanelMain;

import view.doctorPanel.*;
import view.receptionistPanel.ReceptionistCalendarPanel;

import javax.swing.*;
import java.awt.*;

public class DentistPanel extends Panel {
    private JPanel centerPanel;
    private CardLayout cardLayout;
    private DoctorMenuPanel doctorMenuPanel;
    private DoctorTaskbar doctorTaskbar;
    // Thêm các Panel khác ở đây, khi cần thì xóa panel cũ rồi add mới vào, vị trí nó nằm ở center
    private DoctorIntroducePanel doctorIntroducePanel;
    private DoctorExaminationPanel doctorExaminationPanel;
    private DoctorListPatient1Panel dentistListPatient;
    private DoctorListPatient2Panel dentistListPatient2;
    private ReceptionistCalendarPanel receptionistCalendarPanel;
    private AddPrescriptionPanel addPrescriptionPanel;
    private ServicePanel servicePanel;


    public DentistPanel() {
        initComponents();
    }

    private void initComponents() {
        // Khởi tạo các thành phần
        doctorMenuPanel = new DoctorMenuPanel();
        doctorTaskbar = new DoctorTaskbar();

        dentistListPatient = new DoctorListPatient1Panel();
        doctorExaminationPanel = new DoctorExaminationPanel();
        doctorIntroducePanel = new DoctorIntroducePanel();
        dentistListPatient2 = new DoctorListPatient2Panel();
        receptionistCalendarPanel = new ReceptionistCalendarPanel();
        addPrescriptionPanel = new AddPrescriptionPanel();
        servicePanel = new ServicePanel();

        // Layout chính
        setLayout(new BorderLayout());

        // Menu bên trái
        doctorMenuPanel.setPreferredSize(new Dimension(170, 450));
        doctorMenuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(102, 102, 102)));
        add(doctorMenuPanel, BorderLayout.WEST);

        // Taskbar trên cùng
        doctorTaskbar.setPreferredSize(new Dimension(125, 40));
        add(doctorTaskbar, BorderLayout.NORTH);

        // Cài đặt centerPanel với CardLayout
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setPreferredSize(new Dimension(700, 300));

        centerPanel.add(doctorIntroducePanel, "Introduce");
        centerPanel.add(dentistListPatient, "Patient1");
        centerPanel.add(dentistListPatient2, "Patient2");
        centerPanel.add(doctorExaminationPanel, "Examination");
        centerPanel.add(addPrescriptionPanel, "addPrescriptionPanel");
        centerPanel.add(servicePanel, "servicePanel");
        // sau này cần add thêm ExaminationPanel hoặc CalendarPanel thì add luôn ở đây

        add(centerPanel, BorderLayout.CENTER); //Chỉ add centerPanel
    }

    public DoctorMenuPanel getDentistMenuPanel() {
        return doctorMenuPanel;
    }

    public void setDentistMenuPanel(DoctorMenuPanel doctorMenuPanel) {
        this.doctorMenuPanel = doctorMenuPanel;
    }

    public DoctorTaskbar getDentistTaskbar() {
        return doctorTaskbar;
    }

    public void setDentistTaskbar(DoctorTaskbar doctorTaskbar) {
        this.doctorTaskbar = doctorTaskbar;
    }

    public DoctorIntroducePanel getDentistIntroducePanel() {
        return doctorIntroducePanel;
    }

    public void setDentistIntroducePanel(DoctorIntroducePanel doctorIntroducePanel) {
        this.doctorIntroducePanel = doctorIntroducePanel;
    }

    public DoctorExaminationPanel getDentistExaminationPanel() {
        return doctorExaminationPanel;
    }

    public void setDentistExaminationPanel(DoctorExaminationPanel doctorExaminationPanel) {
        this.doctorExaminationPanel = doctorExaminationPanel;
    }

    public DoctorListPatient1Panel getDentistListPatient() {
        return dentistListPatient;
    }

    public void setDentistListPatient(DoctorListPatient1Panel dentistListPatient) {
        this.dentistListPatient = dentistListPatient;
    }

    public DoctorListPatient2Panel getDentistListPatient2() {
        return dentistListPatient2;
    }

    public void setDentistListPatient2(DoctorListPatient2Panel dentistListPatient2) {
        this.dentistListPatient2 = dentistListPatient2;
    }

    public ReceptionistCalendarPanel getReceptionistCalendarPanel() {
        return receptionistCalendarPanel;
    }

    public void setReceptionistCalendarPanel(ReceptionistCalendarPanel receptionistCalendarPanel) {
        this.receptionistCalendarPanel = receptionistCalendarPanel;
    }
    public JPanel getCenterPanel() {
        return centerPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public void setCenterPanel(JPanel centerPanel) {
        this.centerPanel = centerPanel;
    }

    public void setCardLayout(CardLayout cardLayout) {
        this.cardLayout = cardLayout;
    }

    public AddPrescriptionPanel getAddPrescriptionPanel() {
        return addPrescriptionPanel;
    }

    public void setAddPrescriptionPanel(AddPrescriptionPanel addPrescriptionPanel) {
        this.addPrescriptionPanel = addPrescriptionPanel;
    }

    public ServicePanel getServicePanel() {
        return servicePanel;
    }

    public void setServicePanel(ServicePanel servicePanel) {
        this.servicePanel = servicePanel;
    }
}

