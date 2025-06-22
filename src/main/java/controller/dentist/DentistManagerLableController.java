package controller.dentist;

import view.listPanelMain.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DentistManagerLableController implements MouseListener {
    private MainFrame view;
    public DentistManagerLableController(MainFrame view) {
        this.view = view;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JLabel clickedLabel = (JLabel) e.getComponent();
        String name = clickedLabel.getName();

        if (name == null) return;
        System.out.println("Đã click: " + name);

        switch (name) {
            case "Schedule":
                // Lấy acc/pass từ LoginPanel
                String acc = view.getLoginPanel().getAcc();
                String pass = view.getLoginPanel().getPass();

                // Load lại bảng bệnh nhân cho đúng bác sĩ
                view.getMainPanel().getDentistListPatient().reloadTableData(acc, pass);

                // Chuyển panel
                view.getMainPanel().getCardLayout().show(view.getMainPanel().getCenterPanel(), "Patient1");
                break;
            case "Patients":
                switchDentistPatient2Panel();
                break;
            case "Feature 1":
                System.out.println("Hiện đang trống");
                break;
            case "Feature 2":
                System.out.println("Hiện đang trống");
                break;
            case "Home":
                switchDentistIntroducePanel();
                break;
            case "Login":
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Bạn có muốn đăng xuất không?",
                        "Xác nhận đăng xuất",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    switchReceptionistLoginPanel();
                }
                break;
        }
    }

    // Sửa các hàm chuyển panel

    public void switchDentistIntroducePanel() {
        view.getMainPanel().getCardLayout().show(view.getMainPanel().getCenterPanel(), "Introduce");
    }

    public void switchDentistPatient1Panel() {
        view.getMainPanel().getDentistListPatient().reloadTableData(view.getLoginPanel().getAcc(), view.getLoginPanel().getPass());
        view.getMainPanel().getCardLayout().show(view.getMainPanel().getCenterPanel(), "Patient1");
    }

    public void switchDentistPatient2Panel() {
        view.getMainPanel().getDentistListPatient2().reloadPatientList();
        view.getMainPanel().getCardLayout().show(view.getMainPanel().getCenterPanel(), "Patient2");
    }
    public void switchReceptionistLoginPanel(){
        view.getLoginPanel().resetUser();
        view.getCardLayout().show(view.getContainerPanel(), "LoginPanel");
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    private JLabel selectedLabel = null;

    public void setLabelEvent(JLabel... labels) {
        for (JLabel label : labels) {
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (selectedLabel != null) {
                        selectedLabel.setBackground(Color.WHITE);
                        selectedLabel.setOpaque(false);
                        selectedLabel.setBorder(null);
                        selectedLabel.repaint();
                    }
                    // Kiểm tra nếu là Home
                    if ("Home".equals(label.getName())) {
                        selectedLabel = null;
                    } else {
                        selectedLabel = label;
                        selectedLabel.setBackground(Color.LIGHT_GRAY);
                        selectedLabel.setOpaque(true);
                        // Thêm viền
                        selectedLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
                        selectedLabel.repaint();
                    }
                }
            });
        }
    }

}

