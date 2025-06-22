package controller.admin;

import view.listPanelMain.MainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServiceButtonController implements ActionListener {
    private MainFrame view;

    public ServiceButtonController(MainFrame view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        System.out.println("Đã click: "+action);
        if (action.equals("Chỉnh sửa")) {
            switchEdit();
        } else if (action.equals("Xóa")) {

        } else if(action.equals("Thêm")){
            switchAdd();
        } else if (action.equals("Xác nhận")){

        }else if(action.equals("Thêm dịch vụ")){

        }
    }
    public void switchEdit(){
        view.getAdminPanel().getCardLayout().show(view.getAdminPanel().getCenterPanel(),"adminEmployeeEdit");
    }
    public void switchAdd(){
        view.getAdminPanel().getCardLayout().show(view.getAdminPanel().getCenterPanel(),"adminServiceAdd");
    }
}
