package controller.admin;

import java.awt.Image;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import reponsitory.EmployeeRepository;
import reponsitory.Patientreponsitory;
import view.admin.AdminEmployee;

public class EmployeeSearchController implements DocumentListener {
	private final AdminEmployee view;


    public EmployeeSearchController (AdminEmployee view) {
        this.view = view;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        searchOfPatient1();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        searchOfPatient1();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        searchOfPatient1();
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

        JTable table = view.getTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        model.setRowCount(0); // Xoá dữ liệu cũ
        for (Object[] row : results) {
            model.addRow(row);
        }

        table.repaint();
    }
    private void searchOfPatient1() {
        String keyword = view.getTfSearch().getText().trim();
        List<Object[]> results = EmployeeRepository.findEmployee(keyword);

        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            Object[] extended = new Object[row.length + 1];
            System.arraycopy(row, 0, extended, 0, row.length);
            results.set(i, extended);
        }
        
        JTable table = view.getTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        model.setRowCount(0); // Xoá dữ liệu cũ
        for (Object[] row : results) {
            model.addRow(row);
        }

        table.repaint();
    }

}
