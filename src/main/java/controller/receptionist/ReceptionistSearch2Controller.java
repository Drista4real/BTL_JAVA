package controller.receptionist;

import reponsitory.Patientreponsitory;
import view.receptionistPanel.ReceptionistCalendarPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ReceptionistSearch2Controller implements DocumentListener {
    private final ReceptionistCalendarPanel view;

    public ReceptionistSearch2Controller(ReceptionistCalendarPanel view) {
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

        // Gán icon vào từng dòng
        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            Object[] extended = new Object[row.length + 1];
            System.arraycopy(row, 0, extended, 0, row.length);
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
