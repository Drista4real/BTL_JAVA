package controller.admin;

import view.admin.AdminDrugAdd;
import view.admin.AdminDrugEdit;
import view.admin.AdminEmployeeEdit;
import view.listPanelMain.MainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.JOptionPane;

import service.DrugService;
import service.EmployeeService;

public class DrugButtonController implements ActionListener {
    private MainFrame view;

    public DrugButtonController(MainFrame view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        System.out.println("Đã click: "+action);
        if (action.equals("Chỉnh sửa")) {
            try {
				switchEdit();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        } else if (action.equals("Xóa")) {

        } else if(action.equals("Thêm")){
            switchAdd();
        } else if (action.equals("Xác nhận")){
        	updateValue();
        } else if(action.equals("Thêm thuốc")){
        	insertDrug();
        }
    }
    private void insertDrug() {
    	AdminDrugAdd addPanel = view.getAdminPanel().getAdminDrugAdd();

        String name = addPanel.getTfName().getText().trim();
        String description = addPanel.getTfDescription().getText().trim();
        String priceText = addPanel.getTfPrice().getText().trim();
        String stockText = addPanel.getTfStock().getText().trim();

        // Kiểm tra ràng buộc cơ bản
        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ tên, giá và số lượng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);

            DrugService service = new DrugService();
            boolean success = service.insertDrug(name, description, price, stock);

            if (success) {
                JOptionPane.showMessageDialog(view, "Thêm thuốc thành công!");
                // Optional: reset form hoặc chuyển về danh sách
                addPanel.getTfName().setText("");
                addPanel.getTfDescription().setText("");
                addPanel.getTfPrice().setText("");
                addPanel.getTfStock().setText("");
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thuốc thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Giá hoặc số lượng không hợp lệ!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
		
	}

	private void updateValue() {
    	AdminDrugEdit editPanel = view.getAdminPanel().getAdminDrugEdit();
        int id = Integer.parseInt(editPanel.getLblId().getText().replace("Mã số: ", ""));

        String name = editPanel.getTfName().getText().trim();
        String description = editPanel.getTfDescription().getText().trim();
        double price = Double.parseDouble(editPanel.getTfPrice().getText().trim());
        int stock = Integer.parseInt(editPanel.getTfStock().getText().trim());

        // Gọi Service cập nhật
        DrugService service = new DrugService();
        boolean success = service.updateDrug(id, name, description, price, stock);

        if (success) {
            JOptionPane.showMessageDialog(view, "Cập nhật thuốc thành công!");
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
		
	}

	public void switchEdit() throws ParseException{
    	view.getAdminPanel().getAdminDrugEdit().loadData(view.getAdminPanel().getAdminDrugInfo().getDrugId());
    	
        view.getAdminPanel().getCardLayout().show(view.getAdminPanel().getCenterPanel(),"adminDrugEdit");
    }
    public void switchAdd(){
        view.getAdminPanel().getCardLayout().show(view.getAdminPanel().getCenterPanel(),"adminDrugAdd");
    }
}
