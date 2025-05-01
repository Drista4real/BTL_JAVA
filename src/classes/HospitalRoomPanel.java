package classes;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.entity.HospitalRoom;
import model.entity.HospitalRoom.Bed;
import model.entity.HospitalRoom.RoomUsageRecord;
import model.entity.User;
import model.entity.Role;

/**
 * Panel hiển thị và quản lý thông tin phòng bệnh
 */
public class HospitalRoomPanel extends JPanel {
    private User currentUser;
    private HospitalRoom hospitalRoom;
    private List<HospitalRoom> roomList;

    // Các thành phần giao diện
    private JTextField roomNumberField;
    private JComboBox<HospitalRoom.RoomType> roomTypeComboBox;
    private JComboBox<HospitalRoom.RoomStatus> statusComboBox;
    private JTextField capacityField;
    private JTextField priceField;
    private JTextField departmentField;
    private JTextField floorField;
    private JTextField buildingField;

    // Bảng hiển thị danh sách giường
    private JTable bedTable;
    private DefaultTableModel bedTableModel;

    // Bảng hiển thị lịch sử sử dụng phòng
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    // Chế độ người dùng (true = bác sĩ, false = bệnh nhân)
    private boolean doctorMode;

    /**
     * Constructor cho HospitalRoomPanel với thông tin người dùng hiện tại
     * @param user Người dùng đang đăng nhập
     */
    public HospitalRoomPanel(User user) {
        this.currentUser = user;
        // Xác định chế độ người dùng dựa vào vai trò
        this.doctorMode = isDoctor(user);

        // Khởi tạo danh sách phòng rỗng
        this.roomList = new ArrayList<>();

        initializeUI();
    }

    /**
     * Constructor cho HospitalRoomPanel với một danh sách phòng
     * @param user Người dùng đang đăng nhập
     * @param roomList Danh sách phòng cần hiển thị
     */
    public HospitalRoomPanel(User user, List<HospitalRoom> roomList) {
        this.currentUser = user;
        this.doctorMode = isDoctor(user);
        this.roomList = roomList;

        if (!roomList.isEmpty()) {
            this.hospitalRoom = roomList.get(0);
        }

        initializeUI();
    }

    /**
     * Constructor nhận trực tiếp đối tượng HospitalRoom
     * @param user Người dùng đang đăng nhập
     * @param hospitalRoom Phòng bệnh cần hiển thị
     */
    public HospitalRoomPanel(User user, HospitalRoom hospitalRoom) {
        this.currentUser = user;
        this.doctorMode = isDoctor(user);
        this.hospitalRoom = hospitalRoom;

        this.roomList = new ArrayList<>();
        if (hospitalRoom != null) {
            this.roomList.add(hospitalRoom);
        }

        initializeUI();
    }

    /**
     * Thiết lập danh sách phòng
     * @param roomList Danh sách phòng mới
     */
    public void setRoomList(List<HospitalRoom> roomList) {
        this.roomList = roomList;
        if (!roomList.isEmpty() && (hospitalRoom == null || !roomList.contains(hospitalRoom))) {
            this.hospitalRoom = roomList.get(0);
        }
        updateRoomSelectionComboBox();
        updateRoomInfoDisplay();
        updateBedTableData();
        updateHistoryTableData();
    }

    /**
     * Thêm phòng vào danh sách
     * @param room Phòng cần thêm
     */
    public void addRoom(HospitalRoom room) {
        if (room != null) {
            if (roomList == null) {
                roomList = new ArrayList<>();
            }
            roomList.add(room);
            if (hospitalRoom == null) {
                hospitalRoom = room;
            }
            updateRoomSelectionComboBox();
        }
    }

    /**
     * Cập nhật combobox chọn phòng
     */
    private void updateRoomSelectionComboBox() {
        // Tìm combobox trong giao diện
        for (Component comp : this.getComponents()) {
            if (comp instanceof JTabbedPane) {
                JTabbedPane tabbedPane = (JTabbedPane) comp;
                Component firstTab = tabbedPane.getComponentAt(0);
                if (firstTab instanceof JPanel) {
                    JPanel roomInfoPanel = (JPanel) firstTab;
                    // Tìm trong panel bên trên
                    for (Component infoComp : roomInfoPanel.getComponents()) {
                        if (infoComp instanceof JPanel) {
                            JPanel selectionPanel = (JPanel) infoComp;
                            for (Component selComp : selectionPanel.getComponents()) {
                                if (selComp instanceof JComboBox) {
                                    JComboBox<String> roomComboBox = (JComboBox<String>) selComp;
                                    roomComboBox.removeAllItems();
                                    for (HospitalRoom room : roomList) {
                                        roomComboBox.addItem(room.getRoomNumber() + " - " + room.getRoomType().getDisplayName());
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Kiểm tra xem người dùng có phải là bác sĩ không
     * @param user Người dùng cần kiểm tra
     * @return true nếu là bác sĩ, false nếu không phải
     */
    private boolean isDoctor(User user) {
        if (user == null || user.getRole() == null) return false;

        // Kiểm tra vai trò
        return Role.DOCTOR.equals(user.getRole());
    }

    /**
     * Khởi tạo giao diện người dùng
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel chính sử dụng JTabbedPane để chia thông tin theo tab
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab thông tin cơ bản
        JPanel roomInfoPanel = createRoomInfoPanel();
        tabbedPane.addTab("Thông tin phòng", roomInfoPanel);

        // Tab quản lý giường bệnh
        JPanel bedManagementPanel = createBedManagementPanel();
        tabbedPane.addTab("Quản lý giường bệnh", bedManagementPanel);

        // Tab lịch sử sử dụng
        JPanel historyPanel = createHistoryPanel();
        tabbedPane.addTab("Lịch sử sử dụng", historyPanel);

        // Thêm vào panel chính
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Tạo panel chứa thông tin cơ bản của phòng
     * @return JPanel chứa thông tin cơ bản
     */
    private JPanel createRoomInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel chọn phòng
        JPanel roomSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel roomSelectLabel = new JLabel("Chọn phòng:");

        JComboBox<String> roomComboBox = new JComboBox<>();
        for (HospitalRoom room : roomList) {
            roomComboBox.addItem(room.getRoomNumber() + " - " + room.getRoomType().getDisplayName());
        }

        roomComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = roomComboBox.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < roomList.size()) {
                    hospitalRoom = roomList.get(selectedIndex);
                    updateRoomInfoDisplay();
                    updateBedTableData();
                    updateHistoryTableData();
                }
            }
        });

        roomSelectionPanel.add(roomSelectLabel);
        roomSelectionPanel.add(roomComboBox);

        // Panel thông tin cơ bản
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin cơ bản"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Tạo các trường thông tin
        roomNumberField = new JTextField(10);
        roomTypeComboBox = new JComboBox<>(HospitalRoom.RoomType.values());
        statusComboBox = new JComboBox<>(HospitalRoom.RoomStatus.values());
        capacityField = new JTextField(5);
        priceField = new JTextField(10);
        departmentField = new JTextField(15);
        floorField = new JTextField(5);
        buildingField = new JTextField(10);

        // Đổ dữ liệu vào các trường
        updateRoomInfoDisplay();

        // Thiết lập quyền chỉnh sửa dựa vào vai trò
        boolean canEdit = doctorMode;
        roomNumberField.setEditable(false); // Không cho phép thay đổi số phòng
        roomTypeComboBox.setEnabled(canEdit);
        statusComboBox.setEnabled(canEdit);
        capacityField.setEditable(false); // Không cho phép thay đổi số giường
        priceField.setEditable(canEdit);
        departmentField.setEditable(canEdit);
        floorField.setEditable(false); // Không cho phép thay đổi tầng
        buildingField.setEditable(false); // Không cho phép thay đổi tòa nhà

        // Thêm các trường vào panel
        gbc.gridx = 0; gbc.gridy = 0; infoPanel.add(new JLabel("Số phòng:"), gbc);
        gbc.gridx = 1; infoPanel.add(roomNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; infoPanel.add(new JLabel("Loại phòng:"), gbc);
        gbc.gridx = 1; infoPanel.add(roomTypeComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; infoPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; infoPanel.add(statusComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3; infoPanel.add(new JLabel("Số giường:"), gbc);
        gbc.gridx = 1; infoPanel.add(capacityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; infoPanel.add(new JLabel("Giá phòng (VND/ngày):"), gbc);
        gbc.gridx = 1; infoPanel.add(priceField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; infoPanel.add(new JLabel("Khoa:"), gbc);
        gbc.gridx = 3; infoPanel.add(departmentField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; infoPanel.add(new JLabel("Tầng:"), gbc);
        gbc.gridx = 3; infoPanel.add(floorField, gbc);

        gbc.gridx = 2; gbc.gridy = 2; infoPanel.add(new JLabel("Tòa nhà:"), gbc);
        gbc.gridx = 3; infoPanel.add(buildingField, gbc);

        // Panel thống kê
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Thống kê"));

        JLabel totalBedsLabel = new JLabel("Tổng số giường: " + (hospitalRoom != null ? hospitalRoom.getCapacity() : 0));
        JLabel occupiedBedsLabel = new JLabel("Đã sử dụng: " + (hospitalRoom != null ? hospitalRoom.getOccupiedBedCount() : 0));
        JLabel availableBedsLabel = new JLabel("Còn trống: " + (hospitalRoom != null ? hospitalRoom.getAvailableBedCount() : 0));

        statsPanel.add(totalBedsLabel);
        statsPanel.add(occupiedBedsLabel);
        statsPanel.add(availableBedsLabel);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        if (canEdit) {
            JButton saveButton = new JButton("Lưu thông tin");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveRoomInfo();
                }
            });
            buttonPanel.add(saveButton);

            JButton maintenanceButton = new JButton("Bảo trì phòng");
            maintenanceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setRoomMaintenance();
                }
            });
            buttonPanel.add(maintenanceButton);

            JButton cleaningButton = new JButton("Vệ sinh phòng");
            cleaningButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setRoomCleaning();
                }
            });
            buttonPanel.add(cleaningButton);

            JButton completeButton = new JButton("Hoàn thành bảo trì/vệ sinh");
            completeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    completeMaintenanceOrCleaning();
                }
            });
            buttonPanel.add(completeButton);
        }

        // Thêm các panel vào panel chính
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        centerPanel.add(statsPanel, BorderLayout.CENTER);

        panel.add(roomSelectionPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo panel quản lý giường bệnh
     * @return JPanel quản lý giường bệnh
     */
    private JPanel createBedManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Tạo bảng hiển thị danh sách giường
        String[] columnNames = {"Số giường", "Trạng thái", "Bệnh nhân", "Thời gian nhập viện", "Ghi chú"};
        bedTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 && doctorMode; // Chỉ cho phép chỉnh sửa cột ghi chú nếu là bác sĩ
            }
        };

        bedTable = new JTable(bedTableModel);
        bedTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        bedTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        bedTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        bedTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        bedTable.getColumnModel().getColumn(4).setPreferredWidth(200);

        updateBedTableData();

        // Panel chức năng quản lý giường
        JPanel functionPanel = new JPanel(new BorderLayout(10, 10));

        JPanel admitPanel = new JPanel(new GridBagLayout());
        admitPanel.setBorder(BorderFactory.createTitledBorder("Nhập viện bệnh nhân"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField patientIdField = new JTextField(15);
        JTextField doctorIdField = new JTextField(15);
        JTextArea reasonArea = new JTextArea(3, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0; admitPanel.add(new JLabel("Mã bệnh nhân:"), gbc);
        gbc.gridx = 1; admitPanel.add(patientIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; admitPanel.add(new JLabel("Mã bác sĩ:"), gbc);
        gbc.gridx = 1; admitPanel.add(doctorIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; admitPanel.add(new JLabel("Lý do nhập viện:"), gbc);
        gbc.gridx = 1; admitPanel.add(new JScrollPane(reasonArea), gbc);

        JPanel dischargePanel = new JPanel(new GridBagLayout());
        dischargePanel.setBorder(BorderFactory.createTitledBorder("Xuất viện bệnh nhân"));

        JTextField dischargePatientIdField = new JTextField(15);
        JTextArea dischargeNotesArea = new JTextArea(3, 20);
        dischargeNotesArea.setLineWrap(true);
        dischargeNotesArea.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0; dischargePanel.add(new JLabel("Mã bệnh nhân:"), gbc);
        gbc.gridx = 1; dischargePanel.add(dischargePatientIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dischargePanel.add(new JLabel("Ghi chú xuất viện:"), gbc);
        gbc.gridx = 1; dischargePanel.add(new JScrollPane(dischargeNotesArea), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Các chức năng chỉ dành cho bác sĩ
        if (doctorMode) {
            JButton admitButton = new JButton("Nhập viện");
            admitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    admitPatient(patientIdField.getText().trim(),
                            doctorIdField.getText().trim(),
                            reasonArea.getText().trim());
                    // Xóa dữ liệu nhập sau khi thêm
                    patientIdField.setText("");
                    doctorIdField.setText("");
                    reasonArea.setText("");
                }
            });

            JButton dischargeButton = new JButton("Xuất viện");
            dischargeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dischargePatient(dischargePatientIdField.getText().trim(),
                            dischargeNotesArea.getText().trim());
                    // Xóa dữ liệu nhập sau khi xuất viện
                    dischargePatientIdField.setText("");
                    dischargeNotesArea.setText("");
                }
            });

            JButton updateNotesButton = new JButton("Cập nhật ghi chú");
            updateNotesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateBedNotes();
                }
            });

            buttonPanel.add(admitButton);
            buttonPanel.add(dischargeButton);
            buttonPanel.add(updateNotesButton);
        }

        // Thêm vào panel chức năng
        JPanel functionsWrapperPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        functionsWrapperPanel.add(admitPanel);
        functionsWrapperPanel.add(dischargePanel);

        functionPanel.add(functionsWrapperPanel, BorderLayout.CENTER);
        functionPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Thiết lập truy cập
        admitPanel.setEnabled(doctorMode);
        dischargePanel.setEnabled(doctorMode);

        // Các thành phần trong admitPanel
        for (Component comp : admitPanel.getComponents()) {
            comp.setEnabled(doctorMode);
        }

        // Các thành phần trong dischargePanel
        for (Component comp : dischargePanel.getComponents()) {
            comp.setEnabled(doctorMode);
        }

        // Thêm vào panel chính
        panel.add(new JScrollPane(bedTable), BorderLayout.CENTER);
        panel.add(functionPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo panel hiển thị lịch sử sử dụng phòng
     * @return JPanel hiển thị lịch sử
     */
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Tạo bảng hiển thị lịch sử sử dụng phòng
        String[] columnNames = {"Mã bệnh nhân", "Giường", "Nhập viện", "Xuất viện", "Bác sĩ phụ trách", "Lý do", "Ghi chú"};
        historyTableModel = new DefaultTableModel(columnNames, 0);

        historyTable = new JTable(historyTableModel);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(200);
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(200);

        updateHistoryTableData();

        // Panel chức năng lọc lịch sử
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc"));

        JTextField patientFilterField = new JTextField(15);
        JButton filterButton = new JButton("Lọc theo bệnh nhân");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterHistoryByPatient(patientFilterField.getText().trim());
            }
        });

        JButton clearFilterButton = new JButton("Xóa bộ lọc");
        clearFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                patientFilterField.setText("");
                updateHistoryTableData();
            }
        });

        filterPanel.add(new JLabel("Mã bệnh nhân:"));
        filterPanel.add(patientFilterField);
        filterPanel.add(filterButton);
        filterPanel.add(clearFilterButton);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Cập nhật hiển thị thông tin phòng khi chọn phòng khác
     */
    private void updateRoomInfoDisplay() {
        if (hospitalRoom == null) {
            // Xóa trắng nếu không có phòng được chọn
            roomNumberField.setText("");
            capacityField.setText("");
            priceField.setText("");
            departmentField.setText("");
            floorField.setText("");
            buildingField.setText("");
            return;
        }

        roomNumberField.setText(hospitalRoom.getRoomNumber());
        roomTypeComboBox.setSelectedItem(hospitalRoom.getRoomType());
        statusComboBox.setSelectedItem(hospitalRoom.getStatus());
        capacityField.setText(String.valueOf(hospitalRoom.getCapacity()));
        priceField.setText(String.valueOf(hospitalRoom.getPricePerDay()));
        departmentField.setText(hospitalRoom.getDepartment());
        floorField.setText(hospitalRoom.getFloor());
        buildingField.setText(hospitalRoom.getBuilding());
    }

    /**
     * Cập nhật dữ liệu bảng giường bệnh
     */
    private void updateBedTableData() {
        // Xóa dữ liệu cũ
        bedTableModel.setRowCount(0);

        if (hospitalRoom == null) return;

        // Thêm dữ liệu mới
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Bed bed : hospitalRoom.getBeds()) {
            Object[] rowData = new Object[5];
            rowData[0] = bed.getBedNumber();
            rowData[1] = bed.isAvailable() ? "Trống" : "Đã sử dụng";
            rowData[2] = bed.getPatientId() != null ? bed.getPatientId() : "";
            rowData[3] = bed.getOccupiedSince() != null ? bed.getOccupiedSince().format(formatter) : "";
            rowData[4] = bed.getNotes();

            bedTableModel.addRow(rowData);
        }
    }

    /**
     * Cập nhật dữ liệu bảng lịch sử sử dụng
     */
    private void updateHistoryTableData() {
        // Xóa dữ liệu cũ
        historyTableModel.setRowCount(0);

        if (hospitalRoom == null) return;

        // Thêm dữ liệu mới
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (RoomUsageRecord record : hospitalRoom.getUsageHistory()) {
            Object[] rowData = new Object[7];
            rowData[0] = record.getPatientId();

            // Tìm số giường từ ID giường
            String bedNumber = "";
            for (Bed bed : hospitalRoom.getBeds()) {
                if (bed.getBedId().equals(record.getBedId())) {
                    bedNumber = bed.getBedNumber();
                    break;
                }
            }

            rowData[1] = bedNumber;
            rowData[2] = record.getCheckInTime().format(formatter);
            rowData[3] = record.getCheckOutTime() != null ? record.getCheckOutTime().format(formatter) : "Đang điều trị";
            rowData[4] = record.getDoctorInCharge();
            rowData[5] = record.getReason();
            rowData[6] = record.getNotes();

            historyTableModel.addRow(rowData);
        }
    }

    /**
     * Lọc lịch sử theo bệnh nhân
     * @param patientId Mã bệnh nhân cần lọc
     */
    private void filterHistoryByPatient(String patientId) {
        if (patientId.isEmpty()) {
            updateHistoryTableData();
            return;
        }

        // Xóa dữ liệu cũ
        historyTableModel.setRowCount(0);

        if (hospitalRoom == null) return;

        // Thêm dữ liệu mới đã lọc
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (RoomUsageRecord record : hospitalRoom.getUsageHistory()) {
            if (record.getPatientId().equals(patientId)) {
                Object[] rowData = new Object[7];
                rowData[0] = record.getPatientId();

                // Tìm số giường từ ID giường
                String bedNumber = "";
                for (Bed bed : hospitalRoom.getBeds()) {
                    if (bed.getBedId().equals(record.getBedId())) {
                        bedNumber = bed.getBedNumber();
                        break;
                    }
                }

                rowData[1] = bedNumber;
                rowData[2] = record.getCheckInTime().format(formatter);
                rowData[3] = record.getCheckOutTime() != null ? record.getCheckOutTime().format(formatter) : "Đang điều trị";
                rowData[4] = record.getDoctorInCharge();
                rowData[5] = record.getReason();
                rowData[6] = record.getNotes();

                historyTableModel.addRow(rowData);
            }
        }
    }

    /**
     * Lưu thông tin phòng
     */
    private void saveRoomInfo() {
        if (hospitalRoom == null) return;

        try {
            // Lấy thông tin từ các trường
            HospitalRoom.RoomType roomType = (HospitalRoom.RoomType) roomTypeComboBox.getSelectedItem();
            double price = Double.parseDouble(priceField.getText().trim());
            String department = departmentField.getText().trim();

            // Cập nhật thông tin
            hospitalRoom.setRoomType(roomType);
            hospitalRoom.setPricePerDay(price);
            hospitalRoom.setDepartment(department);

            // Cập nhật lại hiển thị
            updateRoomInfoDisplay();

            JOptionPane.showMessageDialog(this,
                    "Đã lưu thông tin phòng",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Giá phòng phải là số",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lưu thông tin: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Chuyển phòng sang trạng thái bảo trì
     */
    private void setRoomMaintenance() {
        if (hospitalRoom == null) return;

        try {
            hospitalRoom.setUnderMaintenance();
            statusComboBox.setSelectedItem(HospitalRoom.RoomStatus.MAINTENANCE);

            JOptionPane.showMessageDialog(this,
                    "Đã chuyển phòng sang trạng thái bảo trì",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Chuyển phòng sang trạng thái vệ sinh
     */
    private void setRoomCleaning() {
        if (hospitalRoom == null) return;

        try {
            hospitalRoom.setUnderCleaning();
            statusComboBox.setSelectedItem(HospitalRoom.RoomStatus.CLEANING);

            JOptionPane.showMessageDialog(this,
                    "Đã chuyển phòng sang trạng thái vệ sinh",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Hoàn thành bảo trì hoặc vệ sinh
     */
    private void completeMaintenanceOrCleaning() {
        if (hospitalRoom == null) return;

        try {
            hospitalRoom.completeMaintenanceOrCleaning();
            statusComboBox.setSelectedItem(hospitalRoom.getStatus());

            JOptionPane.showMessageDialog(this,
                    "Đã hoàn thành bảo trì/vệ sinh và chuyển phòng về trạng thái sẵn sàng",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Nhập viện bệnh nhân
     * @param patientId Mã bệnh nhân
     * @param doctorId Mã bác sĩ phụ trách
     * @param reason Lý do nhập viện
     */
    private void admitPatient(String patientId, String doctorId, String reason) {
        if (hospitalRoom == null) return;

        if (patientId.isEmpty() || doctorId.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đủ thông tin bệnh nhân, bác sĩ và lý do nhập viện",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Bed assignedBed = hospitalRoom.admitPatient(patientId, doctorId, reason);

            // Cập nhật hiển thị
            updateBedTableData();
            updateHistoryTableData();

            JOptionPane.showMessageDialog(this,
                    "Đã nhập viện bệnh nhân " + patientId + " vào giường " + assignedBed.getBedNumber(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xuất viện bệnh nhân
     * @param patientId Mã bệnh nhân cần xuất viện
     * @param notes Ghi chú xuất viện
     */
    private void dischargePatient(String patientId, String notes) {
        if (hospitalRoom == null) return;

        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mã bệnh nhân cần xuất viện",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            hospitalRoom.dischargePatient(patientId, notes);

            // Cập nhật hiển thị
            updateBedTableData();
            updateHistoryTableData();

            JOptionPane.showMessageDialog(this,
                    "Đã xuất viện bệnh nhân " + patientId,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật ghi chú cho giường
     */
    private void updateBedNotes() {
        if (hospitalRoom == null) return;

        int selectedRow = bedTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một giường để cập nhật ghi chú",
                    "Chưa chọn giường",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bedNumber = (String) bedTableModel.getValueAt(selectedRow, 0);
        String newNotes = (String) bedTableModel.getValueAt(selectedRow, 4);

        // Tìm giường tương ứng và cập nhật ghi chú
        for (Bed bed : hospitalRoom.getBeds()) {
            if (bed.getBedNumber().equals(bedNumber)) {
                bed.setNotes(newNotes);
                JOptionPane.showMessageDialog(this,
                        "Đã cập nhật ghi chú cho giường " + bedNumber,
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    /**
     * Lấy đối tượng phòng hiện tại
     * @return Đối tượng phòng hiện tại
     */
    public HospitalRoom getCurrentRoom() {
        return hospitalRoom;
    }

    /**
     * Thiết lập phòng hiện tại
     * @param room Phòng cần thiết lập
     */
    public void setCurrentRoom(HospitalRoom room) {
        this.hospitalRoom = room;
        updateRoomInfoDisplay();
        updateBedTableData();
        updateHistoryTableData();
    }

    /**
     * Lấy danh sách phòng
     * @return Danh sách phòng
     */
    public List<HospitalRoom> getRoomList() {
        return roomList;
    }
}