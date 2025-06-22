package controller.dentist;

import view.listPanelMain.MainFrame;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DentistManagerTableController extends MouseAdapter{
    private MainFrame view;
    public DentistManagerTableController(MainFrame view) {
        this.view = view;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        JTable table = (JTable) e.getSource();

        // Kiểm tra double-click
        if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
            int row = table.getSelectedRow();
            String status = table.getValueAt(row, 5).toString(); // Cột 5 là Trạng thái
            view.getMainPanel().getDentistExaminationPanel().setSdtPatient(table.getValueAt(row, 2).toString());
            if ("Đã khám".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(null, "Bệnh nhân này đã được khám!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Tiếp tục nếu là "Chưa khám"
            String tenBN = table.getValueAt(row, 1).toString();
            System.out.println("Đã double-click bệnh nhân: " + tenBN);

            // Gọi panel khám
            switchDentistExaminationPanel(
                    table.getValueAt(row, 1).toString(),  // Tên
                    table.getValueAt(row, 2).toString(),  // SĐT
                    table.getValueAt(row, 3).toString(),  // Giới tính
                    table.getValueAt(row, 4).toString(),  // Tuổi
                    table.getValueAt(row, 5).toString()   // Trạng thái
            );
        }
    }


    public MainFrame getView() {
        return view;
    }

    public void setView(MainFrame view) {
        this.view = view;
    }
    public void switchDentistExaminationPanel(String name, String phone, String agenda, String age, String status) {
        view.getMainPanel().getDentistExaminationPanel().setNamePatient(name);
        view.getMainPanel().getDentistExaminationPanel().setAgePatient(age);
        view.getMainPanel().getCardLayout().show(view.getMainPanel().getCenterPanel(), "Examination");
    }
}
