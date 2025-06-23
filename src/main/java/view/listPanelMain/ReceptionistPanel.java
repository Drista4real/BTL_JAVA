package view.listPanelMain;

import view.doctorPanel.*;
import view.receptionistPanel.*;

import javax.swing.*;
import java.awt.*;

public class ReceptionistPanel extends JPanel{
    private JPanel centerPanel;
    private CardLayout cardLayout;
    private ReceptionistMenuPanel receptionistMenuPanel;
    private DoctorTaskbar doctorTaskbar;
    // Thêm các Panel khác ở đây, khi cần thì xóa panel cũ rồi add mới vào, vị trí nó nằm ở center
    private ReceptionistCalendarPanel receptionistCalendarPanel;
    private AddPatientPanel addPatientPanel;
    private ShowPatientsReceptionistPanel showPatientsReceptionistPanel;
    private FollowupPanel followupPanel;
    private NewAppointmentPanel newAppointmentPanel;
    private DoctorIntroducePanel doctorIntroducePanel;

    public ReceptionistPanel() {
        initComponents();
    }

    private void initComponents() {
        // Khởi tạo các thành phần
        receptionistMenuPanel = new ReceptionistMenuPanel();
        doctorTaskbar = new DoctorTaskbar();

        receptionistCalendarPanel = new ReceptionistCalendarPanel();
        addPatientPanel= new AddPatientPanel();
        showPatientsReceptionistPanel = new ShowPatientsReceptionistPanel();
        followupPanel = new FollowupPanel();
        newAppointmentPanel = new NewAppointmentPanel();
        doctorIntroducePanel = new DoctorIntroducePanel();

        // Layout chính
        setLayout(new BorderLayout());

        // Menu bên trái
        receptionistMenuPanel.setPreferredSize(new Dimension(170, 450));
        receptionistMenuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(102, 102, 102)));
        add(receptionistMenuPanel, BorderLayout.WEST);

        // Taskbar trên cùng
        doctorTaskbar.setPreferredSize(new Dimension(125, 40));
        add(doctorTaskbar, BorderLayout.NORTH);

        // Cài đặt centerPanel với CardLayout
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setPreferredSize(new Dimension(700, 300));

        centerPanel.add(doctorIntroducePanel,"DentistIntroduce");
        centerPanel.add(receptionistCalendarPanel,"Calendar");
        centerPanel.add(addPatientPanel,"AddPatient");
        centerPanel.add(showPatientsReceptionistPanel,"ShowPatientsReceptionist");
        centerPanel.add(followupPanel,"Followup");
        centerPanel.add(newAppointmentPanel,"NewAppointment");

        // sau này cần add thêm ExaminationPanel hoặc CalendarPanel thì add luôn ở đây

        add(centerPanel, BorderLayout.CENTER); //Chỉ add centerPanel
    }

    public DoctorTaskbar getDentistTaskbar() {
        return doctorTaskbar;
    }

    public void setDentistTaskbar(DoctorTaskbar doctorTaskbar) {
        this.doctorTaskbar = doctorTaskbar;
    }

    public ReceptionistMenuPanel getReceptionistMenuPanel() {
        return receptionistMenuPanel;
    }

    public void setReceptionistMenuPanel(ReceptionistMenuPanel receptionistMenuPanel) {
        this.receptionistMenuPanel = receptionistMenuPanel;
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

    public AddPatientPanel getAddPatientPanel() {
        return addPatientPanel;
    }

    public void setAddPatientPanel(AddPatientPanel addPatientPanel) {
        this.addPatientPanel = addPatientPanel;
    }

    public ShowPatientsReceptionistPanel getShowPatientsReceptionistPanel() {
        return showPatientsReceptionistPanel;
    }

    public void setShowPatientsReceptionistPanel(ShowPatientsReceptionistPanel showPatientsReceptionistPanel) {
        this.showPatientsReceptionistPanel = showPatientsReceptionistPanel;
    }

    public FollowupPanel getFollowupPanel() {
        return followupPanel;
    }

    public void setFollowupPanel(FollowupPanel followupPanel) {
        this.followupPanel = followupPanel;
    }

    public NewAppointmentPanel getNewAppointmentPanel() {
        return newAppointmentPanel;
    }

    public void setNewAppointmentPanel(NewAppointmentPanel newAppointmentPanel) {
        this.newAppointmentPanel = newAppointmentPanel;
    }
}
