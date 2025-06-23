package controller.admin;

import view.listPanelMain.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LableController implements MouseListener {
    private final MainFrame view;

    public LableController(MainFrame view) {
        this.view = view;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JLabel clickedLabel = (JLabel) e.getComponent();
        String name = clickedLabel.getName();

        if (name == null) return;
        System.out.println("Đã click: " + name);

        switch (name) {
            case "ListPatient":
                switchPatient();
                break;
            case "ListService":
                switchService();
                break;
            case "ListDrug":
                switchDrug();
                break;
            case "ListEmployee":
                switchEmployee();
                break;
            case "TK":
                switchTK();
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

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void switchReceptionistLoginPanel() {
        view.getLoginPanel().resetUser();
        view.getCardLayout().show(view.getContainerPanel(), "LoginPanel");
    }

    public void switchPatient() {
        view.getAdminPanel().getAdminPatient().loadPatientData();
        view.getAdminPanel().getCardLayout().show(view.getAdminPanel().getCenterPanel(), "adminPatient");
    }

    public void switchService() {
        view.getAdminPanel().getAdminService().loadServiceData();
        view.getAdminPanel().getCardLayout().show(view.getAdminPanel().getCenterPanel(), "adminService");
    }

    public void switchDrug() {
        view.getAdminPanel().getAdminDrug().loadDrugData();
        view.getAdminPanel().getCardLayout().show(view.getAdminPanel().getCenterPanel(), "adminDrug");
    }

    public void switchEmployee() {
        view.getAdminPanel().getAdminEmployee().loadEmployeeData();
        view.getAdminPanel().getCardLayout().show(view.getAdminPanel().getCenterPanel(), "adminEmployee");
    }

    public void switchTK() {
        view.getAdminPanel().getCardLayout().show(view.getAdminPanel().getCenterPanel(), "adminStatistical");
    }

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
