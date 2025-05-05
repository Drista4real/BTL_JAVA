package model.gui;

import model.entity.HospitalRoom;
import model.entity.Role;
import model.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DoctorHospitalRoomPanel extends JPanel {

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
    private JComboBox<String> roomComboBox;
    private JLabel totalBedsLabel;
    private JLabel occupiedBedsLabel;
    private JLabel availableBedsLabel;

    // Bảng hiển thị danh sách giường
    private JTable bedTable;
    private DefaultTableModel bedTableModel;

    // Bảng hiển thị lịch sử sử dụng phòng
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    private boolean doctorMode;

    // Kết nối cơ sở dữ liệu
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement?useUnicode=true&characterEncoding=UTF-8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "050705";

    // Đồng bộ định dạng thời gian với HospitalRoom
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DoctorHospitalRoomPanel(User user) {
        this.currentUser = user;
        this.doctorMode = isDoctor(user);
        this.roomList = new ArrayList<>();
        initializeUI();
        loadRoomsFromDatabase();
    }

    public DoctorHospitalRoomPanel(User user, List<HospitalRoom> roomList) {
        this.currentUser = user;
        this.doctorMode = isDoctor(user);
        this.roomList = roomList != null ? new ArrayList<>(roomList) : new ArrayList<>();
        if (!roomList.isEmpty()) {
            this.hospitalRoom = roomList.get(0);
        }
        initializeUI();
        loadRoomsFromDatabase();
    }

    public DoctorHospitalRoomPanel(User user, HospitalRoom hospitalRoom) {
        this.currentUser = user;
        this.doctorMode = isDoctor(user);
        this.hospitalRoom = hospitalRoom;
        this.roomList = new ArrayList<>();
        if (hospitalRoom != null) {
            this.roomList.add(hospitalRoom);
        }
        initializeUI();
        loadRoomsFromDatabase();
    }

    private boolean isDoctor(User user) {
        return user != null && user.getRole() == Role.DOCTOR;
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel roomInfoPanel = createRoomInfoPanel();
        tabbedPane.addTab("Thông tin phòng", roomInfoPanel);
        JPanel bedManagementPanel = createBedManagementPanel();
        tabbedPane.addTab("Quản lý giường bệnh", bedManagementPanel);
        JPanel historyPanel = createHistoryPanel();
        tabbedPane.addTab("Lịch sử sử dụng", historyPanel);

        add(tabbedPane, BorderLayout.CENTER);

        if (!roomList.isEmpty() && roomComboBox.getItemCount() > 0) {
            roomComboBox.setSelectedIndex(0);
            updateRoomInfoDisplay();
            updateBedTableData();
            updateHistoryTableData();
            updateStatsDisplay();
        }
    }

    private JPanel createRoomInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel roomSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel roomSelectLabel = new JLabel("Chọn phòng:");
        roomComboBox = new JComboBox<>();
        updateRoomSelectionComboBox();
        roomComboBox.addActionListener(e -> {
            int selectedIndex = roomComboBox.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < roomList.size()) {
                hospitalRoom = roomList.get(selectedIndex);
                updateRoomInfoDisplay();
                updateBedTableData();
                updateHistoryTableData();
                updateStatsDisplay();
            }
        });

        roomSelectionPanel.add(roomSelectLabel);
        roomSelectionPanel.add(roomComboBox);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin cơ bản"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        roomNumberField = new JTextField(10);
        roomTypeComboBox = new JComboBox<>(HospitalRoom.RoomType.values());
        statusComboBox = new JComboBox<>(HospitalRoom.RoomStatus.values());
        capacityField = new JTextField(5);
        priceField = new JTextField(10);
        departmentField = new JTextField(15);
        floorField = new JTextField(5);
        buildingField = new JTextField(10);

        updateRoomInfoDisplay();

        boolean canEdit = doctorMode;
        roomNumberField.setEditable(false);
        roomTypeComboBox.setEnabled(canEdit);
        statusComboBox.setEnabled(canEdit);
        capacityField.setEditable(false);
        priceField.setEditable(canEdit);
        departmentField.setEditable(canEdit);
        floorField.setEditable(false);
        buildingField.setEditable(false);

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

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Thống kê"));

        totalBedsLabel = new JLabel("Tổng số giường: 0");
        occupiedBedsLabel = new JLabel("Đã sử dụng: 0");
        availableBedsLabel = new JLabel("Còn trống: 0");
        updateStatsDisplay();

        statsPanel.add(totalBedsLabel);
        statsPanel.add(occupiedBedsLabel);
        statsPanel.add(availableBedsLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (canEdit) {
            JButton saveButton = new JButton("Lưu thông tin");
            saveButton.addActionListener(e -> saveRoomInfo());
            buttonPanel.add(saveButton);

            JButton maintenanceButton = new JButton("Bảo trì phòng");
            maintenanceButton.addActionListener(e -> setRoomMaintenance());
            buttonPanel.add(maintenanceButton);

            JButton cleaningButton = new JButton("Vệ sinh phòng");
            cleaningButton.addActionListener(e -> setRoomCleaning());
            buttonPanel.add(cleaningButton);

            JButton completeButton = new JButton("Hoàn thành bảo trì/vệ sinh");
            completeButton.addActionListener(e -> completeMaintenanceOrCleaning());
            buttonPanel.add(completeButton);
        }

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        centerPanel.add(statsPanel, BorderLayout.CENTER);

        panel.add(roomSelectionPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBedManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] columnNames = {"Số giường", "Trạng thái", "Bệnh nhân", "Thời gian nhập viện", "Ghi chú"};
        bedTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 && doctorMode;
            }
        };

        bedTable = new JTable(bedTableModel);
        bedTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        bedTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        bedTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        bedTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        bedTable.getColumnModel().getColumn(4).setPreferredWidth(200);

        updateBedTableData();

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

        if (doctorMode) {
            JButton admitButton = new JButton("Nhập viện");
            admitButton.addActionListener(e -> {
                admitPatient(patientIdField.getText().trim(), doctorIdField.getText().trim(), reasonArea.getText().trim());
                patientIdField.setText("");
                doctorIdField.setText("");
                reasonArea.setText("");
            });

            JButton dischargeButton = new JButton("Xuất viện");
            dischargeButton.addActionListener(e -> {
                dischargePatient(dischargePatientIdField.getText().trim(), dischargeNotesArea.getText().trim());
                dischargePatientIdField.setText("");
                dischargeNotesArea.setText("");
            });

            JButton updateNotesButton = new JButton("Cập nhật ghi chú");
            updateNotesButton.addActionListener(e -> updateBedNotes());

            buttonPanel.add(admitButton);
            buttonPanel.add(dischargeButton);
            buttonPanel.add(updateNotesButton);
        }

        JPanel functionsWrapperPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        functionsWrapperPanel.add(admitPanel);
        functionsWrapperPanel.add(dischargePanel);

        functionPanel.add(functionsWrapperPanel, BorderLayout.CENTER);
        functionPanel.add(buttonPanel, BorderLayout.SOUTH);

        admitPanel.setEnabled(doctorMode);
        dischargePanel.setEnabled(doctorMode);
        for (Component comp : admitPanel.getComponents()) {
            comp.setEnabled(doctorMode);
        }
        for (Component comp : dischargePanel.getComponents()) {
            comp.setEnabled(doctorMode);
        }

        panel.add(new JScrollPane(bedTable), BorderLayout.CENTER);
        panel.add(functionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

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

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc"));

        JTextField patientFilterField = new JTextField(15);
        JButton filterButton = new JButton("Lọc theo bệnh nhân");
        filterButton.addActionListener(e -> filterHistoryByPatient(patientFilterField.getText().trim()));

        JButton clearFilterButton = new JButton("Xóa bộ lọc");
        clearFilterButton.addActionListener(e -> {
            patientFilterField.setText("");
            updateHistoryTableData();
        });

        filterPanel.add(new JLabel("Mã bệnh nhân:"));
        filterPanel.add(patientFilterField);
        filterPanel.add(filterButton);
        filterPanel.add(clearFilterButton);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadRoomsFromDatabase() {
        roomList.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM HospitalRooms");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String sqlRoomType = rs.getString("RoomType");
                HospitalRoom.RoomType roomType;
                switch (sqlRoomType) {
                    case "Tieu chuan":
                        roomType = HospitalRoom.RoomType.NORMAL;
                        break;
                    case "VIP":
                        roomType = HospitalRoom.RoomType.VIP;
                        break;
                    case "ICU":
                        roomType = HospitalRoom.RoomType.INTENSIVE_CARE;
                        break;
                    case "Cap cuu":
                        roomType = HospitalRoom.RoomType.EMERGENCY;
                        break;
                    case "Phau thuat":
                        roomType = HospitalRoom.RoomType.SURGERY;
                        break;
                    default:
                        throw new IllegalArgumentException("Loại phòng không hợp lệ: " + sqlRoomType);
                }

                String sqlStatus = rs.getString("Status");
                HospitalRoom.RoomStatus status;
                switch (sqlStatus) {
                    case "Trong":
                        status = HospitalRoom.RoomStatus.AVAILABLE;
                        break;
                    case "Dang su dung":
                    case "Day":
                        status = HospitalRoom.RoomStatus.OCCUPIED;
                        break;
                    case "Bao tri":
                        status = HospitalRoom.RoomStatus.MAINTENANCE;
                        break;
                    case "Dang ve sinh":
                        status = HospitalRoom.RoomStatus.CLEANING;
                        break;
                    case "Da dat truoc":
                        status = HospitalRoom.RoomStatus.RESERVED;
                        break;
                    default:
                        throw new IllegalArgumentException("Trạng thái phòng không hợp lệ: " + sqlStatus);
                }

                HospitalRoom room = new HospitalRoom(
                        rs.getString("RoomNumber"),
                        roomType,
                        rs.getInt("TotalBeds"),
                        rs.getDouble("PricePerDay"),
                        rs.getString("Department"),
                        String.valueOf(rs.getInt("FloorNumber")),
                        rs.getString("Building")
                );
                // Sử dụng setter để gán giá trị
                room.setRoomId(rs.getString("RoomID"));
                room.setStatus(status);

                // Tải danh sách giường và lịch sử sử dụng
                loadBedsForRoom(conn, room);
                loadUsageHistoryForRoom(conn, room);
                roomList.add(room);
            }
            if (roomList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phòng nào trong cơ sở dữ liệu.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            } else {
                hospitalRoom = roomList.get(0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        updateRoomSelectionComboBox();
        if (!roomList.isEmpty()) {
            roomComboBox.setSelectedIndex(0);
            updateRoomInfoDisplay();
            updateBedTableData();
            updateHistoryTableData();
            updateStatsDisplay();
        }
    }

    private void loadBedsForRoom(Connection conn, HospitalRoom room) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Beds WHERE RoomID = ?");
        stmt.setString(1, room.getRoomId());
        ResultSet rs = stmt.executeQuery();
        List<HospitalRoom.Bed> beds = new ArrayList<>();
        while (rs.next()) {
            HospitalRoom.Bed bed = new HospitalRoom.Bed(rs.getString("BedNumber"));
            bed.setBedId(rs.getString("BedID"));
            bed.setAvailable(rs.getBoolean("IsAvailable"));
            bed.setNotes(rs.getString("Notes") != null ? rs.getString("Notes") : "");
            if (!rs.getBoolean("IsAvailable")) {
                bed.setPatientId(rs.getString("PatientID"));
                Timestamp occupiedSince = rs.getTimestamp("OccupiedSince");
                if (occupiedSince != null) {
                    bed.setOccupiedSince(occupiedSince.toLocalDateTime());
                }
            }
            beds.add(bed);
        }
        room.setBeds(beds);
    }

    private void loadUsageHistoryForRoom(Connection conn, HospitalRoom room) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM RoomUsageRecords WHERE RoomID = ?");
        stmt.setString(1, room.getRoomId());
        ResultSet rs = stmt.executeQuery();
        List<HospitalRoom.RoomUsageRecord> history = new ArrayList<>();
        while (rs.next()) {
            HospitalRoom.RoomUsageRecord record = new HospitalRoom.RoomUsageRecord(
                    rs.getString("PatientID"),
                    rs.getString("BedID"),
                    rs.getString("DoctorInCharge"),
                    rs.getString("Reason")
            );
            record.setRecordId(rs.getString("RecordID"));
            record.setCheckInTime(rs.getTimestamp("CheckInTime").toLocalDateTime());
            record.setNotes(rs.getString("Notes") != null ? rs.getString("Notes") : "");
            Timestamp checkOutTime = rs.getTimestamp("CheckOutTime");
            if (checkOutTime != null) {
                record.setCheckOutTime(checkOutTime.toLocalDateTime());
            }
            history.add(record);
        }
        room.setUsageHistory(history);
    }

    private void saveRoomInfo() {
        if (hospitalRoom == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            HospitalRoom.RoomType roomType = (HospitalRoom.RoomType) roomTypeComboBox.getSelectedItem();
            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                throw new NumberFormatException("Giá phòng phải lớn hơn 0");
            }
            String department = departmentField.getText().trim();
            if (department.isEmpty()) {
                throw new IllegalArgumentException("Khoa không được để trống");
            }

            hospitalRoom.setRoomType(roomType);
            hospitalRoom.setPricePerDay(price);
            hospitalRoom.setDepartment(department);

            String sqlRoomType;
            switch (roomType) {
                case NORMAL:
                    sqlRoomType = "Tieu chuan";
                    break;
                case VIP:
                    sqlRoomType = "VIP";
                    break;
                case INTENSIVE_CARE:
                    sqlRoomType = "ICU";
                    break;
                case EMERGENCY:
                    sqlRoomType = "Cap cuu";
                    break;
                case SURGERY:
                    sqlRoomType = "Phau thuat";
                    break;
                default:
                    throw new IllegalArgumentException("Loại phòng không hợp lệ");
            }

            PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE HospitalRooms SET RoomType = ?, PricePerDay = ?, Department = ? WHERE RoomID = ?");
            pstmt.setString(1, sqlRoomType);
            pstmt.setDouble(2, price);
            pstmt.setString(3, department);
            pstmt.setString(4, hospitalRoom.getRoomId());
            pstmt.executeUpdate();

            updateRoomInfoDisplay();
            JOptionPane.showMessageDialog(this, "Đã lưu thông tin phòng", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu thông tin: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setRoomMaintenance() {
        if (hospitalRoom == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            hospitalRoom.setUnderMaintenance();
            statusComboBox.setSelectedItem(HospitalRoom.RoomStatus.MAINTENANCE);

            PreparedStatement pstmt = conn.prepareStatement("UPDATE HospitalRooms SET Status = ? WHERE RoomID = ?");
            pstmt.setString(1, "Bao tri");
            pstmt.setString(2, hospitalRoom.getRoomId());
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Đã chuyển phòng sang trạng thái bảo trì", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setRoomCleaning() {
        if (hospitalRoom == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            hospitalRoom.setUnderCleaning();
            statusComboBox.setSelectedItem(HospitalRoom.RoomStatus.CLEANING);

            PreparedStatement pstmt = conn.prepareStatement("UPDATE HospitalRooms SET Status = ? WHERE RoomID = ?");
            pstmt.setString(1, "Dang ve sinh");
            pstmt.setString(2, hospitalRoom.getRoomId());
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Đã chuyển phòng sang trạng thái vệ sinh", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void completeMaintenanceOrCleaning() {
        if (hospitalRoom == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            hospitalRoom.completeMaintenanceOrCleaning();
            statusComboBox.setSelectedItem(hospitalRoom.getStatus());

            String sqlStatus = hospitalRoom.getStatus() == HospitalRoom.RoomStatus.AVAILABLE ? "Trong" : "Dang su dung";
            PreparedStatement pstmt = conn.prepareStatement("UPDATE HospitalRooms SET Status = ? WHERE RoomID = ?");
            pstmt.setString(1, sqlStatus);
            pstmt.setString(2, hospitalRoom.getRoomId());
            pstmt.executeUpdate();

            updateStatsDisplay();
            JOptionPane.showMessageDialog(this, "Đã hoàn thành bảo trì/vệ sinh", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void admitPatient(String patientId, String doctorId, String reason) {
        if (hospitalRoom == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (patientId.isEmpty() || doctorId.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Kiểm tra patientId và doctorId có tồn tại
            PreparedStatement checkPatientStmt = conn.prepareStatement("SELECT PatientID FROM Patients WHERE PatientID = ?");
            checkPatientStmt.setString(1, patientId);
            if (!checkPatientStmt.executeQuery().next()) {
                throw new IllegalArgumentException("Mã bệnh nhân không tồn tại");
            }

            PreparedStatement checkDoctorStmt = conn.prepareStatement("SELECT UserID FROM UserAccounts WHERE UserID = ? AND Role = 'Bac si'");
            checkDoctorStmt.setString(1, doctorId);
            if (!checkDoctorStmt.executeQuery().next()) {
                throw new IllegalArgumentException("Mã bác sĩ không tồn tại hoặc không phải bác sĩ");
            }

            HospitalRoom.Bed assignedBed = hospitalRoom.admitPatient(patientId, doctorId, reason);

            // Cập nhật giường
            PreparedStatement bedStmt = conn.prepareStatement(
                    "UPDATE Beds SET PatientID = ?, OccupiedSince = ?, IsAvailable = ?, Notes = ? WHERE BedID = ?");
            bedStmt.setString(1, assignedBed.getPatientId());
            bedStmt.setTimestamp(2, Timestamp.valueOf(assignedBed.getOccupiedSince()));
            bedStmt.setBoolean(3, assignedBed.isAvailable());
            bedStmt.setString(4, assignedBed.getNotes());
            bedStmt.setString(5, assignedBed.getBedId());
            bedStmt.executeUpdate();

            // Lưu bản ghi sử dụng phòng
            HospitalRoom.RoomUsageRecord record = hospitalRoom.getUsageHistory().get(hospitalRoom.getUsageHistory().size() - 1);
            PreparedStatement recordStmt = conn.prepareStatement(
                    "INSERT INTO RoomUsageRecords (RecordID, RoomID, PatientID, BedID, CheckInTime, DoctorInCharge, Reason, Notes) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            recordStmt.setString(1, record.getRecordId());
            recordStmt.setString(2, hospitalRoom.getRoomId());
            recordStmt.setString(3, record.getPatientId());
            recordStmt.setString(4, record.getBedId());
            recordStmt.setTimestamp(5, Timestamp.valueOf(record.getCheckInTime()));
            recordStmt.setString(6, record.getDoctorInCharge());
            recordStmt.setString(7, record.getReason());
            recordStmt.setString(8, record.getNotes());
            recordStmt.executeUpdate();

            // Cập nhật số giường trống và trạng thái phòng
            int availableBeds = hospitalRoom.getAvailableBedCount();
            String status = availableBeds == 0 ? "Day" : "Dang su dung";
            PreparedStatement roomStmt = conn.prepareStatement(
                    "UPDATE HospitalRooms SET AvailableBeds = ?, Status = ? WHERE RoomID = ?");
            roomStmt.setInt(1, availableBeds);
            roomStmt.setString(2, status);
            roomStmt.setString(3, hospitalRoom.getRoomId());
            roomStmt.executeUpdate();

            updateBedTableData();
            updateHistoryTableData();
            updateStatsDisplay();
            statusComboBox.setSelectedItem(hospitalRoom.getStatus());

            JOptionPane.showMessageDialog(this, "Đã nhập viện bệnh nhân " + patientId + " vào giường " + assignedBed.getBedNumber(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException | SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void dischargePatient(String patientId, String notes) {
        if (hospitalRoom == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã bệnh nhân", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            HospitalRoom.Bed bed = hospitalRoom.findPatientBed(patientId);
            if (bed == null) {
                throw new IllegalArgumentException("Không tìm thấy bệnh nhân trong phòng");
            }

            hospitalRoom.dischargePatient(patientId, notes);

            // Cập nhật giường
            PreparedStatement bedStmt = conn.prepareStatement(
                    "UPDATE Beds SET PatientID = ?, OccupiedSince = ?, IsAvailable = ?, Notes = ? WHERE BedID = ?");
            bedStmt.setObject(1, null);
            bedStmt.setObject(2, null);
            bedStmt.setBoolean(3, true);
            bedStmt.setString(4, bed.getNotes());
            bedStmt.setString(5, bed.getBedId());
            bedStmt.executeUpdate();

            // Cập nhật bản ghi sử dụng phòng
            HospitalRoom.RoomUsageRecord record = hospitalRoom.getUsageHistory().stream()
                    .filter(r -> r.getPatientId().equals(patientId) && r.getCheckOutTime() != null)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy bản ghi sử dụng"));
            PreparedStatement recordStmt = conn.prepareStatement(
                    "UPDATE RoomUsageRecords SET CheckOutTime = ?, Notes = ? WHERE RecordID = ?");
            recordStmt.setTimestamp(1, Timestamp.valueOf(record.getCheckOutTime()));
            recordStmt.setString(2, record.getNotes());
            recordStmt.setString(3, record.getRecordId());
            recordStmt.executeUpdate();

            // Cập nhật số giường trống và trạng thái phòng
            int availableBeds = hospitalRoom.getAvailableBedCount();
            String status = availableBeds == hospitalRoom.getCapacity() ? "Trong" : "Dang su dung";
            PreparedStatement roomStmt = conn.prepareStatement(
                    "UPDATE HospitalRooms SET AvailableBeds = ?, Status = ? WHERE RoomID = ?");
            roomStmt.setInt(1, availableBeds);
            roomStmt.setString(2, status);
            roomStmt.setString(3, hospitalRoom.getRoomId());
            roomStmt.executeUpdate();

            updateBedTableData();
            updateHistoryTableData();
            updateStatsDisplay();
            statusComboBox.setSelectedItem(hospitalRoom.getStatus());

            JOptionPane.showMessageDialog(this, "Đã xuất viện bệnh nhân " + patientId, "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateBedNotes() {
        if (hospitalRoom == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = bedTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giường", "Chưa chọn giường", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bedNumber = (String) bedTableModel.getValueAt(selectedRow, 0);
        String newNotes = (String) bedTableModel.getValueAt(selectedRow, 4);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            for (HospitalRoom.Bed bed : hospitalRoom.getBeds()) {
                if (bed.getBedNumber().equals(bedNumber)) {
                    bed.setNotes(newNotes);
                    PreparedStatement stmt = conn.prepareStatement("UPDATE Beds SET Notes = ? WHERE BedID = ?");
                    stmt.setString(1, newNotes);
                    stmt.setString(2, bed.getBedId());
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Đã cập nhật ghi chú cho giường " + bedNumber,
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Không tìm thấy giường " + bedNumber, "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateRoomSelectionComboBox() {
        if (roomComboBox != null) {
            roomComboBox.removeAllItems();
            if (roomList != null) {
                for (HospitalRoom room : roomList) {
                    roomComboBox.addItem(room.getRoomNumber() + " - " + room.getRoomType().getDisplayName());
                }
            }
        }
    }

    private void updateRoomInfoDisplay() {
        if (hospitalRoom == null) {
            roomNumberField.setText("");
            capacityField.setText("");
            priceField.setText("");
            departmentField.setText("");
            floorField.setText("");
            buildingField.setText("");
            roomTypeComboBox.setSelectedItem(null);
            statusComboBox.setSelectedItem(null);
        } else {
            roomNumberField.setText(hospitalRoom.getRoomNumber());
            roomTypeComboBox.setSelectedItem(hospitalRoom.getRoomType());
            statusComboBox.setSelectedItem(hospitalRoom.getStatus());
            capacityField.setText(String.valueOf(hospitalRoom.getCapacity()));
            priceField.setText(String.valueOf(hospitalRoom.getPricePerDay()));
            departmentField.setText(hospitalRoom.getDepartment());
            floorField.setText(hospitalRoom.getFloor());
            buildingField.setText(hospitalRoom.getBuilding());
        }
    }

    private void updateStatsDisplay() {
        if (hospitalRoom == null) {
            totalBedsLabel.setText("Tổng số giường: 0");
            occupiedBedsLabel.setText("Đã sử dụng: 0");
            availableBedsLabel.setText("Còn trống: 0");
        } else {
            int totalBeds = hospitalRoom.getCapacity();
            int availableBeds = hospitalRoom.getAvailableBedCount();
            int occupiedBeds = totalBeds - availableBeds;
            totalBedsLabel.setText("Tổng số giường: " + totalBeds);
            occupiedBedsLabel.setText("Đã sử dụng: " + occupiedBeds);
            availableBedsLabel.setText("Còn trống: " + availableBeds);
        }
    }

    private void updateBedTableData() {
        bedTableModel.setRowCount(0);
        if (hospitalRoom != null) {
            for (HospitalRoom.Bed bed : hospitalRoom.getBeds()) {
                String status = bed.isAvailable() ? "Trống" : "Đã sử dụng";
                String patientId = bed.isAvailable() ? "" : bed.getPatientId();
                String occupiedSince = bed.isAvailable() ? "" :
                        bed.getOccupiedSince() != null ? bed.getOccupiedSince().format(DATE_TIME_FORMATTER) : "";
                bedTableModel.addRow(new Object[]{
                        bed.getBedNumber(),
                        status,
                        patientId,
                        occupiedSince,
                        bed.getNotes()
                });
            }
        }
    }

    private void updateHistoryTableData() {
        historyTableModel.setRowCount(0);
        if (hospitalRoom != null) {
            for (HospitalRoom.RoomUsageRecord record : hospitalRoom.getUsageHistory()) {
                String checkOutTime = record.getCheckOutTime() != null ?
                        record.getCheckOutTime().format(DATE_TIME_FORMATTER) : "";
                historyTableModel.addRow(new Object[]{
                        record.getPatientId(),
                        record.getBedId(),
                        record.getCheckInTime().format(DATE_TIME_FORMATTER),
                        checkOutTime,
                        record.getDoctorInCharge(),
                        record.getReason(),
                        record.getNotes()
                });
            }
        }
    }

    private void filterHistoryByPatient(String patientId) {
        historyTableModel.setRowCount(0);
        if (hospitalRoom != null) {
            for (HospitalRoom.RoomUsageRecord record : hospitalRoom.getUsageHistory()) {
                if (patientId.isEmpty() || record.getPatientId().equalsIgnoreCase(patientId)) {
                    String checkOutTime = record.getCheckOutTime() != null ?
                            record.getCheckOutTime().format(DATE_TIME_FORMATTER) : "";
                    historyTableModel.addRow(new Object[]{
                            record.getPatientId(),
                            record.getBedId(),
                            record.getCheckInTime().format(DATE_TIME_FORMATTER),
                            checkOutTime,
                            record.getDoctorInCharge(),
                            record.getReason(),
                            record.getNotes()
                    });
                }
            }
        }
    }
}