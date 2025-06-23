package controller.admin;

import java.util.List;

import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import reponsitory.ServiceReponsitory;
import view.admin.AdminService;

public class ServiceSearchController implements DocumentListener {
    private final AdminService view;

    public ServiceSearchController(AdminService view) {
        this.view = view;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        searchServices();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        searchServices();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        searchServices();
    }

    private void searchServices() {
        String keyword = view.getTfSearch().getText().trim();

        // Nếu từ khóa rỗng, hiển thị toàn bộ danh sách
        if (keyword.isEmpty()) {
            view.loadServiceData();
            return;
        }

        // Tìm kiếm theo từ khóa
        List<Object[]> results = ServiceReponsitory.findServices(keyword);

        JTable table = view.getTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        model.setRowCount(0); // Xóa dữ liệu cũ
        for (Object[] row : results) {
            model.addRow(row);
        }

        table.repaint();
    }
}
