package controller.dentist;

import reponsitory.Patientreponsitory;
import view.dentistPanel.DentistListPatient1Panel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DentistPatientSearch2Controller implements DocumentListener {

    private final DentistListPatient1Panel view;


    public DentistPatientSearch2Controller(DentistListPatient1Panel view) {
        this.view = view;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        search();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        search();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        search();
    }

    private void search() {
        String keyword = view.getTfSearch().getText().trim();
        List<Object[]> results = Patientreponsitory.getPatientsChar(keyword);

        // Load icon xem
        ImageIcon seeIcon = null;
        try {
            seeIcon = new ImageIcon(getClass().getResource("/img/see.png"));
            Image scaled = seeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            seeIcon = new ImageIcon(scaled);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh see.png");
        }

        // Gán icon vào từng dòng
        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            Object[] extended = new Object[row.length + 1];
            System.arraycopy(row, 0, extended, 0, row.length);
            extended[row.length] = seeIcon;
            results.set(i, extended);
        }

        JTable table = view.getTblPatients();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        model.setRowCount(0); // Xoá dữ liệu cũ
        for (Object[] row : results) {
            model.addRow(row);
        }

        table.repaint();
    }
    private void searchOfPatient1() {
        String keyword = view.getTfSearch().getText().trim();
        List<Object[]> results = Patientreponsitory.getPatientsCharofDentist(keyword, view.getId_doctor());

        // Load icon xem
        ImageIcon seeIcon = null;
        try {
            seeIcon = new ImageIcon(getClass().getResource("/img/see.png"));
            Image scaled = seeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            seeIcon = new ImageIcon(scaled);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh see.png");
        }

        // Gán icon vào từng dòng
        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            Object[] extended = new Object[row.length + 1];
            System.arraycopy(row, 0, extended, 0, row.length);
            extended[row.length] = seeIcon;
            results.set(i, extended);
        }

        JTable table = view.getTblPatients();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        model.setRowCount(0); // Xoá dữ liệu cũ
        for (Object[] row : results) {
            model.addRow(row);
        }

        table.repaint();
    }

}
