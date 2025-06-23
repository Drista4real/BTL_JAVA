package controller.admin;

import java.util.List;

import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import reponsitory.DrugReponsitory;
import view.admin.AdminDrug;

public class DrugSearchController implements DocumentListener {
    private final AdminDrug view;

    public DrugSearchController(AdminDrug view) {
        this.view = view;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        searchDrugs();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        searchDrugs();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        searchDrugs();
    }

    private void searchDrugs() {
        String keyword = view.getTfSearch().getText().trim();

        // Nếu từ khóa rỗng, hiển thị toàn bộ danh sách
        if (keyword.isEmpty()) {
            view.loadDrugData();
            return;
        }

        // Tìm kiếm theo từ khóa
        List<Object[]> results = DrugReponsitory.findDrugs(keyword);

        JTable table = view.getTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        model.setRowCount(0); // Xóa dữ liệu cũ
        for (Object[] row : results) {
            model.addRow(row);
        }

        table.repaint();
    }
}
