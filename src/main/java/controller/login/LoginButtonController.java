package controller.login;

import reponsitory.DentistReponsitory;
import view.listPanelMain.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginButtonController implements ActionListener {
    private MainFrame view;
    public LoginButtonController(MainFrame view) {
        this.view = view;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd=e.getActionCommand();
        if (cmd.equals("Đăng nhập")){
            if (view.getLoginPanel().getEmployee()==null){
                JOptionPane.showMessageDialog(
                        view,
                        "Tên đăng nhập hoặc mật khẩu không đúng!",
                        "Đăng nhập thất bại",
                        JOptionPane.ERROR_MESSAGE
                );
            }else {
                view.getLoginPanel().getEmployee().showInfo();
                swicthPanel();
                view.getMainPanel().getDentistListPatient().setId_doctor(DentistReponsitory.getIdDentistLogin(view.getLoginPanel().getAcc(),view.getLoginPanel().getPass())+"");
            }
        }
    }
    public void swicthPanel(){
        if (view.getLoginPanel().getEmployee() != null) {
            String nameD[]=view.getLoginPanel().getEmployee().getName().split(" ");
            switch (view.getLoginPanel().getEmployee().getRole()) {
                case "Bác sĩ":
                    view.getCardLayout().show(view.getContainerPanel(), "DentistPanel");
                    switchDentistIntroducePanel();
                    view.getMainPanel().getDentistTaskbar().getLblDoctorName().setText(nameD[nameD.length - 1]);
                    break;
                case "Lễ tân":
                    view.getCardLayout().show(view.getContainerPanel(), "ReceptionistPanel");
                    view.getReceptionistPanel().getDentistTaskbar().getLblDoctorName().setText(nameD[nameD.length - 1]);
                    break;
                case "Nhân viên quầy thuốc":
                    view.getCardLayout().show(view.getContainerPanel(), "drugstore");
                    view.getDrugStorePanel().getDentistTaskbar().getLblDoctorName().setText(nameD[nameD.length - 1]);
                    break;
                case "Admin":
                    view.getCardLayout().show(view.getContainerPanel(), "AdminPanel");
                    view.getAdminPanel().getDentistTaskbar().getLblDoctorName().setText(nameD[nameD.length - 1]);
            }
        }
    }
    public void switchDentistIntroducePanel() {
        view.getMainPanel().getCardLayout().show(view.getMainPanel().getCenterPanel(), "Introduce");
    }
}
