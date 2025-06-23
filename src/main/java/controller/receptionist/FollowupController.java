package controller.receptionist;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import model.Patient;
import model.Doctor;
import model.Examination;
import service.PatientService;
import service.DoctorService;
import view.receptionistPanel.AddPatientPanel;
import view.receptionistPanel.FollowupPanel;
import Utils.Utils;

public class FollowupController implements ActionListener {
    private PatientService patientService;
    private DoctorService doctorService;
    private FollowupPanel followupPanel;

    public FollowupController(FollowupPanel followupPanel) {
        this.patientService = new PatientService();
        this.doctorService = new DoctorService();
        this.followupPanel = followupPanel;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String src =e.getActionCommand();
		System.out.println("bạn nhấn nút "+src);
		if (src.equals("Thêm Lịch Hẹn")) {
          String name = followupPanel.getTxtName().getText();
          String birth = followupPanel.txtBirthDate().getText();
          String idCard=followupPanel.getTxtIdCard().getText();
          String address = followupPanel.getTxtAdress().getText();
          int gender = followupPanel.getGenderCombo().getSelectedIndex(); // 0: Nam, 1: Nữ
          String phone = followupPanel.getTxtPhone().getText();

          if (name.isEmpty() || birth.isEmpty() || address.isEmpty() || phone.isEmpty()) {
              JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin.");
              return;
          }

          if (!Utils.validatePhoneNumber(phone)) {
              JOptionPane.showMessageDialog(null, "Số điện thoại không hợp lệ.");
              return;
          }

          java.sql.Date sqlDate = Utils.parseDate(birth);
          if (sqlDate == null) {
              JOptionPane.showMessageDialog(null, "Ngày sinh không đúng định dạng.");
              return;
          }
          if (!Utils.validateIDCard(idCard)) {
              JOptionPane.showMessageDialog(null, "CCCD không hợp lệ.");
              return;
          } 
          // Lấy bác sĩ được chọn
          Doctor selectedDoctor = followupPanel.getSelectedDoctor();
          if (selectedDoctor == null) {
              JOptionPane.showMessageDialog(null, "Vui lòng chọn bác sĩ.");
              return;
          }
         

 //         Patient newPatient = new Patient(followupPanel.getPatientId(), name, sqlDate, address, gender, phone, idCard);

          try {
              // Thêm bệnh nhân vào DB và lấy id bệnh nhân mới
                  Examination examination = new Examination();
                  examination.setPatientId(followupPanel.getPatientId());
                  examination.setDoctorId(selectedDoctor.getId());
                  examination.setStatus("Chưa khám");

                  boolean addExamSuccess = doctorService.addExamination(examination);
                  if (addExamSuccess) {
                      JOptionPane.showMessageDialog(null, "Thêm lịch hẹn thành công!");
                  } else {
                      JOptionPane.showMessageDialog(null, "Thêm lịch hẹn thất bại!");
                  }

//                  followupPanel.dispose(); // Đóng cửa sổ
          } catch (ClassNotFoundException | SQLException | IOException ex) {
              ex.printStackTrace();
              JOptionPane.showMessageDialog(null, "Có lỗi xảy ra");
      }
	}
	}	

//    public static void main(String[] args) {
//        new AddPatientController(); // Khởi tạo controller
//    }
}

