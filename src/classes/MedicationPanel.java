package classes;


import model.entity.Medication;
import model.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel quản lý danh sách thuốc trong hệ thống y tế
 */
public class MedicationPanel extends JPanel {

    private User currentUser;

    private List<Medication> medicationList;
    private Medication currentMedication;

    private JTable medicationTable;
    private DefaultTableModel medicationTableModel;

    // Các label hiển thị chi tiết thuốc
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel manufacturerLabel;
    private JLabel dosageFormLabel;
    private JTextArea descriptionArea;
    private JTextArea sideEffectsArea;

    /**
     * Constructor
     * @param user Người dùng hiện tại
     */
    public MedicationPanel(User user) {
        this.currentUser = user;
        this.medicationList = new ArrayList<>();
        this.currentMedication = null;

        initializeUI();
    }

    /**
     * Constructor với danh sách thuốc
     * @param user Người dùng hiện tại
     * @param medications Danh sách thuốc
     */
    public MedicationPanel(User user, List<Medication> medications) {
        this.currentUser = user;
        this.medicationList = new ArrayList<>(medications);
        if (!medications.isEmpty()) {
            this.currentMedication = medications.get(0);
        }
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(250);

        JPanel topPanel = createMedicationListPanel();
        JPanel bottomPanel = createMedicationDetailPanel();

        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createMedicationListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách thuốc"));

        String[] columnNames = {"ID", "Tên thuốc", "Nhà sản xuất", "Dạng bào chế"};
        medicationTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicationTable = new JTable(medicationTableModel);
        medicationTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        medicationTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        medicationTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        medicationTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        medicationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = medicationTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < medicationList.size()) {
                    currentMedication = medicationList.get(selectedRow);
                    updateMedicationDetail();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(medicationTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Thêm mới");
        JButton editButton = new JButton("Chỉnh sửa");
        JButton deleteButton = new JButton("Xóa");

        addButton.addActionListener(e -> addMedication());
        editButton.addActionListener(e -> editMedication());
        deleteButton.addActionListener(e -> deleteMedication());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        updateTableData();

        return panel;
    }

    private JPanel createMedicationDetailPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin thuốc"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5,5,5,5);

        idLabel = new JLabel();
        nameLabel = new JLabel();
        manufacturerLabel = new JLabel();
        dosageFormLabel = new JLabel();

        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);

        sideEffectsArea = new JTextArea(3, 30);
        sideEffectsArea.setLineWrap(true);
        sideEffectsArea.setWrapStyleWord(true);
        sideEffectsArea.setEditable(false);
        JScrollPane sideEffectsScrollPane = new JScrollPane(sideEffectsArea);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã thuốc:"), gbc);
        gbc.gridx = 1;
        panel.add(idLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tên thuốc:"), gbc);
        gbc.gridx = 1;
        panel.add(nameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Nhà sản xuất:"), gbc);
        gbc.gridx = 1;
        panel.add(manufacturerLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Dạng bào chế:"), gbc);
        gbc.gridx = 1;
        panel.add(dosageFormLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        panel.add(descScrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Tác dụng phụ:"), gbc);
        gbc.gridx = 1;
        panel.add(sideEffectsScrollPane, gbc);

        return panel;
    }

    private void updateTableData() {
        medicationTableModel.setRowCount(0);
        for (Medication med : medicationList) {
            Object[] row = new Object[] {
                    med.getMedicationId(),
                    med.getName(),
                    med.getManufacturer(),
                    med.getDosageForm()
            };
            medicationTableModel.addRow(row);
        }
        if (medicationTable.getRowCount() > 0) {
            medicationTable.setRowSelectionInterval(0, 0);
        }
    }

    private void updateMedicationDetail() {
        if (currentMedication == null) {
            idLabel.setText("");
            nameLabel.setText("");
            manufacturerLabel.setText("");
            dosageFormLabel.setText("");
            descriptionArea.setText("");
            sideEffectsArea.setText("");
            return;
        }

        idLabel.setText(currentMedication.getMedicationId());
        nameLabel.setText(currentMedication.getName());
        manufacturerLabel.setText(currentMedication.getManufacturer());
        dosageFormLabel.setText(currentMedication.getDosageForm());
        descriptionArea.setText(currentMedication.getDescription() != null ? currentMedication.getDescription() : "");
        sideEffectsArea.setText(currentMedication.getSideEffects() != null ? currentMedication.getSideEffects() : "");
    }

    private void addMedication() {
        MedicationFormDialog dialog = new MedicationFormDialog(null);
        dialog.setVisible(true);
        Medication newMed = dialog.getMedication();
        if (newMed != null) {
            medicationList.add(newMed);
            updateTableData();
            medicationTable.setRowSelectionInterval(medicationList.size() - 1, medicationList.size() - 1);
        }
    }

    private void editMedication() {
        if (currentMedication == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc để chỉnh sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        MedicationFormDialog dialog = new MedicationFormDialog(currentMedication);
        dialog.setVisible(true);
        Medication updatedMed = dialog.getMedication();
        if (updatedMed != null) {
            // Cập nhật thuốc trong danh sách
            int index = medicationList.indexOf(currentMedication);
            if (index >= 0) {
                medicationList.set(index, updatedMed);
                currentMedication = updatedMed;
                updateTableData();
                medicationTable.setRowSelectionInterval(index, index);
            }
        }
    }

    private void deleteMedication() {
        if (currentMedication == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa thuốc này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            medicationList.remove(currentMedication);
            currentMedication = null;
            updateTableData();
            updateMedicationDetail();
        }
    }

    /**
     * Dialog nhập liệu thuốc (thêm/sửa)
     */
    private static class MedicationFormDialog extends JDialog {
        private JTextField nameField;
        private JTextField manufacturerField;
        private JTextField dosageFormField;
        private JTextArea descriptionArea;
        private JTextArea sideEffectsArea;

        private Medication medication;

        private boolean saved = false;

        public MedicationFormDialog(Medication med) {
            setTitle(med == null ? "Thêm thuốc mới" : "Chỉnh sửa thuốc");
            setModal(true);
            setSize(400, 400);
            setLocationRelativeTo(null);
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.anchor = GridBagConstraints.WEST;

            JLabel nameLabel = new JLabel("Tên thuốc:");
            nameField = new JTextField(25);

            JLabel manufacturerLabel = new JLabel("Nhà sản xuất:");
            manufacturerField = new JTextField(25);

            JLabel dosageFormLabel = new JLabel("Dạng bào chế:");
            dosageFormField = new JTextField(25);

            JLabel descriptionLabel = new JLabel("Mô tả:");
            descriptionArea = new JTextArea(4, 25);
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            JScrollPane descScrollPane = new JScrollPane(descriptionArea);

            JLabel sideEffectsLabel = new JLabel("Tác dụng phụ:");
            sideEffectsArea = new JTextArea(3, 25);
            sideEffectsArea.setLineWrap(true);
            sideEffectsArea.setWrapStyleWord(true);
            JScrollPane sideEffectsScrollPane = new JScrollPane(sideEffectsArea);

            gbc.gridx = 0; gbc.gridy = 0;
            add(nameLabel, gbc);
            gbc.gridx = 1;
            add(nameField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            add(manufacturerLabel, gbc);
            gbc.gridx = 1;
            add(manufacturerField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            add(dosageFormLabel, gbc);
            gbc.gridx = 1;
            add(dosageFormField, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            add(descriptionLabel, gbc);
            gbc.gridx = 1;
            add(descScrollPane, gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            add(sideEffectsLabel, gbc);
            gbc.gridx = 1;
            add(sideEffectsScrollPane, gbc);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Lưu");
            JButton cancelButton = new JButton("Hủy");

            saveButton.addActionListener(e -> {
                if (validateInput()) {
                    saved = true;
                    if (med == null) {
                        medication = new Medication(
                                nameField.getText().trim(),
                                descriptionArea.getText().trim(),
                                manufacturerField.getText().trim(),
                                dosageFormField.getText().trim(),
                                sideEffectsArea.getText().trim()
                        );
                    } else {
                        medication = new Medication(
                                med.getMedicationId(),
                                nameField.getText().trim(),
                                descriptionArea.getText().trim(),
                                manufacturerField.getText().trim(),
                                dosageFormField.getText().trim(),
                                sideEffectsArea.getText().trim()
                        );
                    }
                    dispose();
                }
            });

            cancelButton.addActionListener(e -> {
                saved = false;
                medication = null;
                dispose();
            });

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            gbc.gridx = 0; gbc.gridy = 5;
            gbc.gridwidth = 2;
            add(buttonPanel, gbc);

            if (med != null) {
                nameField.setText(med.getName());
                manufacturerField.setText(med.getManufacturer());
                dosageFormField.setText(med.getDosageForm());
                descriptionArea.setText(med.getDescription());
                sideEffectsArea.setText(med.getSideEffects());
            }
        }

        private boolean validateInput() {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên thuốc không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (manufacturerField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhà sản xuất không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (dosageFormField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Dạng bào chế không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }

        public Medication getMedication() {
            return saved ? medication : null;
        }
    }
}

