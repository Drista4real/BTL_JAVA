package controller.dentist;

import Utils.JDBCUtil;
import model.DrugDose;
import model.Service;
import reponsitory.*;
import service.ExportToPDF;
import view.listPanelMain.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DentistManagerButtonController implements ActionListener {
    private MainFrame view;
    private int id_pre;

    public DentistManagerButtonController(MainFrame view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.out.println(command);
        if (command.equals("Thêm dịch vụ")) {
            switchDentistServicePanel();
        } else if (command.equals("Thêm danh sách thuốc")) {
            switchDentistAddPrescriptionPanelPanel();
        } else if (command.equals("Xuất đơn thuốc")){
            boolean isDrugListEmpty = view.getMainPanel().getAddPrescriptionPanel().getListDrugDose().isEmpty();
            boolean isServiceListEmpty = view.getMainPanel().getServicePanel().getTableModel().getRowCount() == 0;

            if (isDrugListEmpty && isServiceListEmpty) {
                JOptionPane.showMessageDialog(view, "Không có dịch vụ hoặc đơn thuốc nào để xuất!");
                return;
            }

            // Nếu chỉ thuốc rỗng (không có thuốc)
            if (isDrugListEmpty) {
                JOptionPane.showMessageDialog(view, "Danh sách thuốc đang trống. Không thể xuất đơn thuốc.");
                return;
            }

            switchDentistPatient1Panel();
            int id_patient = Patientreponsitory.getIdPatient(view.getMainPanel().getDentistExaminationPanel().getSdtPatient());
            int id_doctor = DentistReponsitory.getIdDentistLogin(view.getLoginPanel().getAcc(), view.getLoginPanel().getPass());
            ExamReponsitory.updateExam(id_doctor + "", id_patient + "");
            ExportToPDF.prescriptionToPDF(id_pre + "");
        } else if (command.equals("Hủy")) {
            switchDentistPatient1Panel();
        }
    }
    public void switchDentistAddPrescriptionPanelPanel() {
        view.getMainPanel().getServicePanel().readServiceTableData();
        view.getMainPanel().getServicePanel().showText();

        boolean isServiceEmpty = view.getMainPanel().getServicePanel().getTableModel().getRowCount() == 0;
        boolean isDrugEmpty = view.getMainPanel().getAddPrescriptionPanel().getListDrugDose().isEmpty();

        if (isServiceEmpty && isDrugEmpty) {
            JOptionPane.showMessageDialog(view, "Vui lòng thêm ít nhất một dịch vụ hoặc thuốc trước khi kê đơn!");
            return;
        }

        view.getMainPanel().getCardLayout().show(view.getMainPanel().getCenterPanel(), "addPrescriptionPanel");
    }

    public void switchDentistServicePanel() {
        view.getMainPanel().getDentistExaminationPanel().setInfo();//set thong tin benh nhan
        if(view.getMainPanel().getDentistExaminationPanel().getTfSymptom().getText().equals("")||
                view.getMainPanel().getDentistExaminationPanel().getTreatment().equals("")||
                view.getMainPanel().getDentistExaminationPanel().getTfDiagnosis().getText().equals("")){

            JOptionPane.showMessageDialog(view, "Vui lòng điền đầy đủ thông tin !");
        } else {
            view.getMainPanel().getCardLayout().show(view.getMainPanel().getCenterPanel(), "servicePanel");
        }
    }
    public void switchDentistPatient1Panel() {
        //them vao co so du lieu
        int id_patient = Patientreponsitory.getIdPatient(view.getMainPanel().getDentistExaminationPanel().getSdtPatient());
        int id_doctor = DentistReponsitory.getIdDentistLogin(view.getLoginPanel().getAcc(), view.getLoginPanel().getPass());
        String symptom = view.getMainPanel().getDentistExaminationPanel().getSymptom();
        String diagnosis = view.getMainPanel().getDentistExaminationPanel().getDiagnosis();
        String treatment = view.getMainPanel().getDentistExaminationPanel().getTreatment();

        // luôn tạo prescription mới, cập nhật lại id_pre
        this.id_pre = PrescriptionReponsitory.insertPrescription(id_doctor, id_patient, diagnosis, treatment, symptom, "Uống thuốc đầy đủ");
        System.out.println("DEBUG: id_pre mới được tạo = " + id_pre);


        List<DrugDose> listDrug = view.getMainPanel().getAddPrescriptionPanel().getListDrugDose();
        List<Integer> listID = new ArrayList<>();  //đảm bảo luôn tạo list mới, không tái sử dụng


        Connection con = null;

        try {
            con = JDBCUtil.getConnection();
            String sql = "SELECT id FROM Drug WHERE name = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            for (DrugDose drug : listDrug) {
                pst.setString(1, drug.getName());
                try (ResultSet res = pst.executeQuery()) { // Auto-close res sau mỗi vòng
                    if (res.next()) {
                        listID.add(res.getInt("id"));
                    }
                }
            }
            pst.close();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < listID.size(); i++) {
            System.out.println(listID.get(i));
        }
        for (int i = 0; i < listID.size(); i++) {
            PrescriptionReponsitory.insertPrescriptionDetail(id_pre,listID.get(i),(listDrug.get(i).getDoseMor()+listDrug.get(i).getDoseNoon()+listDrug.get(i).getDoseAfternoon())*7,listDrug.get(i).getDoseMor(),listDrug.get(i).getDoseNoon(),listDrug.get(i).getDoseAfternoon());
        }

        //them dich vu vao csdl
        Map<Service, Integer> listSer = view.getMainPanel().getServicePanel().getListSevice();

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT id FROM Service WHERE name = ?")) {

            for (Map.Entry<Service, Integer> entry : listSer.entrySet()) {
                String serviceName = entry.getKey().getName();
                int quantity = entry.getValue();

                int serviceId = -1;
                pst.setString(1, serviceName);
                try (ResultSet res = pst.executeQuery()) {
                    if (res.next()) {
                        serviceId = res.getInt("id");
                    }
                }

                if (serviceId != -1) {
                    ServiceReponsitory.addPrescriptionServiceDetail(id_pre, serviceId, quantity);
                }
            }

        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }


        //xóa hết các text khi chèn thành công
        view.getMainPanel().getDentistExaminationPanel().resetInfo();
        view.getMainPanel().getAddPrescriptionPanel().resetInfor();
        view.getMainPanel().getServicePanel().resetInfor();

        view.getMainPanel().getAddPrescriptionPanel().setListDrugDose(new ArrayList<>());
        view.getMainPanel().getAddPrescriptionPanel().showText();
        view.getMainPanel().getCardLayout().show(view.getMainPanel().getCenterPanel(), "Patient1");

    }
}
