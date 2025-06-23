package controller.receptionist;

import reponsitory.Patientreponsitory;
import view.receptionistPanel.ShowPatientsReceptionistPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ReceptionistSearchController implements DocumentListener {

    private final ShowPatientsReceptionistPanel view;

    public ReceptionistSearchController(ShowPatientsReceptionistPanel view) {
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
        String keyword = view.getTxtSearch().getText().trim();
        List<Object[]> results = Patientreponsitory.getPatientsChar(keyword);

        JTable table = view.getPatientInfoTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        JTable table2 = view.getPatientActionTable();
        DefaultTableModel model2 = (DefaultTableModel) table2.getModel();

        model.setRowCount(0);
        for (Object[] row : results) {
            model.addRow(row);
        }

        table.repaint();

        model2.setRowCount(0);
        for (Object[] row : results) {
            model2.addRow(new Object[]{"Tái Khám", "Lịch hẹn mới", "✎"});
        }

        table.repaint();
        table2.repaint();
    }

}
