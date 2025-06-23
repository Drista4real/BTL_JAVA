package controller.receptionist;

import model.Patient;
import reponsitory.Patientreponsitory;
import view.listPanelMain.MainFrame;
import view.receptionistPanel.ModifyPatient;
import view.receptionistPanel.NewAppointmentPanel;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.Period;

public class ReceptionTableController implements MouseListener {
    private MainFrame view;

    public ReceptionTableController(MainFrame view) {
        this.view = view;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = view.getReceptionistPanel().getShowPatientsReceptionistPanel().getPatientActionTable().rowAtPoint(e.getPoint());
        int col = view.getReceptionistPanel().getShowPatientsReceptionistPanel().getPatientActionTable().columnAtPoint(e.getPoint());
        
        if (row >= 0) {
            int patientId = (int) view.getReceptionistPanel().getShowPatientsReceptionistPanel().getPatientInfoTable().getValueAt(row, 0);
            String patientName = view.getReceptionistPanel().getShowPatientsReceptionistPanel().getPatientInfoTable().getValueAt(row, 1).toString();
            
            if (col == 0) {
                // Xử lý nút "Tái Khám"
                view.getReceptionistPanel().getFollowupPanel().loadPatientInfo(patientId);
                view.getReceptionistPanel().getCardLayout().show(view.getReceptionistPanel().getCenterPanel(), "Followup");
            } else if (col == 1) {
                // Xử lý nút "Lịch hẹn mới"
                Patient patient = Patientreponsitory.getPatientById(patientId);
                if (patient != null) {
                    NewAppointmentPanel newAppointmentPanel = view.getReceptionistPanel().getNewAppointmentPanel();
                    // Điền thông tin bệnh nhân vào các trường văn bản
                    newAppointmentPanel.getTxtName().setText(patient.getName());
                    newAppointmentPanel.getTxtPhone().setText(patient.getPhoneNumber());
                    
                    // Tính tuổi từ birthDate
                    int age = 0;
                    if (patient.getBirthDate() != null) {
                        age = Period.between(
                                patient.getBirthDate().toLocalDate(),
                                LocalDate.now()
                        ).getYears();
                    }
                    newAppointmentPanel.getTxtAge().setText(String.valueOf(age));
                    
                    // Chuyển đổi giới tính
                    String gender = patient.getGender() == 1 ? "Nam" : "Nữ";
                    newAppointmentPanel.getTxtGender().setText(gender);
                    
                    newAppointmentPanel.getTxtAdress().setText(patient.getAddress() != null ? patient.getAddress() : "");
                    
                    // Chuyển sang panel NewAppointment
                    view.getReceptionistPanel().getCardLayout().show(view.getReceptionistPanel().getCenterPanel(), "NewAppointment");
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy thông tin bệnh nhân: " + patientName);
                }
            } else if (col == 2) {
                // Xử lý nút "Sửa"
                Patient patient = Patientreponsitory.getPatientById(patientId);
                if (patient != null) {
                    ModifyPatient modifyPanel = new ModifyPatient(view.getReceptionistPanel(), patient);
                    view.getReceptionistPanel().getCenterPanel().add(modifyPanel, "ModifyPatient");
                    view.getReceptionistPanel().getCardLayout().show(view.getReceptionistPanel().getCenterPanel(), "ModifyPatient");
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy thông tin bệnh nhân: " + patientName);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}