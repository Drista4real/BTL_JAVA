package controller.admin;

import java.util.List;

import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import reponsitory.Patientreponsitory;
import view.admin.AdminPatient;

public class PatientSearchController implements DocumentListener {
    private final AdminPatient view;

    public PatientSearchController(AdminPatient view) {
        this.view = view;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        searchPatients();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        searchPatients();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        searchPatients();
    }

    private void searchPatients() {
        String keyword = view.getTfSearch().getText().trim();

        // Nếu từ khóa rỗng, hiển thị toàn bộ danh sách
        if (keyword.isEmpty()) {
            view.loadPatientData();
            return;
        }

        // Tìm kiếm theo từ khóa
        List<Object[]> results = Patientreponsitory.findPatientsForAdmin(keyword);

        JTable table = view.getTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        model.setRowCount(0); // Xóa dữ liệu cũ
        for (Object[] row : results) {
            model.addRow(row);
        }

        table.repaint();
    }
}
