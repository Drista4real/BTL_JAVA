package model.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Lớp quản lý thông tin và chức năng liên quan đến phòng bệnh.
 */
public class HospitalRoom {
    // Các hằng số định nghĩa loại phòng và trạng thái
    public enum RoomType {
        NORMAL("Phòng thường"), 
        INTENSIVE_CARE("Phòng chăm sóc đặc biệt"), 
        VIP("Phòng VIP"), 
        EMERGENCY("Phòng cấp cứu"), 
        SURGERY("Phòng phẫu thuật");
        
        private final String displayName;
        
        RoomType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum RoomStatus {
        AVAILABLE("Trống"), 
        OCCUPIED("Đã có bệnh nhân"), 
        MAINTENANCE("Đang bảo trì"), 
        CLEANING("Đang vệ sinh"), 
        RESERVED("Đã đặt trước");
        
        private final String displayName;
        
        RoomStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Thông tin cơ bản của phòng
    private String roomId;
    private String roomNumber;
    private RoomType roomType;
    private RoomStatus status;
    private int capacity; // Số giường tối đa
    private double pricePerDay; // Giá phòng mỗi ngày
    private String department; // Khoa phòng
    private String floor; // Tầng
    private String building; // Tòa nhà
    
    // Danh sách các giường và bệnh nhân trong phòng
    private List<Bed> beds;
    
    // Lịch sử sử dụng phòng
    private List<RoomUsageRecord> usageHistory;
    
    /**
     * Lớp đại diện cho một giường bệnh trong phòng
     */
    public static class Bed {
        private String bedId;
        private String bedNumber;
        private String patientId; // ID của bệnh nhân đang sử dụng giường (null nếu trống)
        private LocalDateTime occupiedSince; // Thời gian bệnh nhân bắt đầu sử dụng
        private boolean isAvailable;
        private String notes;
        
        public Bed(String bedNumber) {
            this.bedId = UUID.randomUUID().toString();
            this.bedNumber = bedNumber;
            this.patientId = null;
            this.occupiedSince = null;
            this.isAvailable = true;
            this.notes = "";
        }
        
        // Phương thức đưa bệnh nhân vào giường
        public void assignPatient(String patientId) {
            if (isAvailable) {
                this.patientId = patientId;
                this.occupiedSince = LocalDateTime.now();
                this.isAvailable = false;
            } else {
                throw new IllegalStateException("Giường đã có bệnh nhân sử dụng");
            }
        }
        
        // Phương thức giải phóng giường
        public void releaseBed() {
            this.patientId = null;
            this.occupiedSince = null;
            this.isAvailable = true;
        }
        
        // Getters and Setters
        public String getBedId() {
            return bedId;
        }
        
        public String getBedNumber() {
            return bedNumber;
        }
        
        public String getPatientId() {
            return patientId;
        }
        
        public LocalDateTime getOccupiedSince() {
            return occupiedSince;
        }
        
        public boolean isAvailable() {
            return isAvailable;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
        
        @Override
        public String toString() {
            if (isAvailable) {
                return "Giường " + bedNumber + " - Trống";
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return "Giường " + bedNumber + " - Bệnh nhân: " + patientId + 
                       " (từ " + occupiedSince.format(formatter) + ")";
            }
        }
    }
    
    /**
     * Lớp ghi lại lịch sử sử dụng phòng
     */
    public static class RoomUsageRecord {
        private String recordId;
        private String patientId;
        private String bedId;
        private LocalDateTime checkInTime;
        private LocalDateTime checkOutTime;
        private String doctorInCharge; // Bác sĩ phụ trách
        private String reason; // Lý do nhập viện
        private String notes;
        
        public RoomUsageRecord(String patientId, String bedId, String doctorInCharge, String reason) {
            this.recordId = UUID.randomUUID().toString();
            this.patientId = patientId;
            this.bedId = bedId;
            this.checkInTime = LocalDateTime.now();
            this.checkOutTime = null;
            this.doctorInCharge = doctorInCharge;
            this.reason = reason;
            this.notes = "";
        }
        
        // Hoàn thành ghi nhận sử dụng phòng
        public void completeUsage(String notes) {
            this.checkOutTime = LocalDateTime.now();
            this.notes = notes;
        }
        
        // Getters and Setters
        public String getRecordId() {
            return recordId;
        }
        
        public String getPatientId() {
            return patientId;
        }
        
        public String getBedId() {
            return bedId;
        }
        
        public LocalDateTime getCheckInTime() {
            return checkInTime;
        }
        
        public LocalDateTime getCheckOutTime() {
            return checkOutTime;
        }
        
        public String getDoctorInCharge() {
            return doctorInCharge;
        }
        
        public void setDoctorInCharge(String doctorInCharge) {
            this.doctorInCharge = doctorInCharge;
        }
        
        public String getReason() {
            return reason;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
        
        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            StringBuilder sb = new StringBuilder();
            sb.append("Bệnh nhân: ").append(patientId)
              .append("\nNhập viện: ").append(checkInTime.format(formatter))
              .append("\nBác sĩ phụ trách: ").append(doctorInCharge)
              .append("\nLý do: ").append(reason);
            
            if (checkOutTime != null) {
                sb.append("\nXuất viện: ").append(checkOutTime.format(formatter));
            }
            
            if (notes != null && !notes.isEmpty()) {
                sb.append("\nGhi chú: ").append(notes);
            }
            
            return sb.toString();
        }
    }
    
    // Constructor
    public HospitalRoom(String roomNumber, RoomType roomType, int capacity, 
                        double pricePerDay, String department, String floor, String building) {
        this.roomId = UUID.randomUUID().toString();
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = RoomStatus.AVAILABLE;
        this.capacity = capacity;
        this.pricePerDay = pricePerDay;
        this.department = department;
        this.floor = floor;
        this.building = building;
        
        // Khởi tạo danh sách giường
        this.beds = new ArrayList<>(capacity);
        for (int i = 1; i <= capacity; i++) {
            this.beds.add(new Bed(roomNumber + "-" + i));
        }
        
        this.usageHistory = new ArrayList<>();
    }
    
    // Phương thức nhận bệnh nhân vào phòng
    public Bed admitPatient(String patientId, String doctorInCharge, String reason) {
        // Kiểm tra xem có giường trống không
        for (Bed bed : beds) {
            if (bed.isAvailable()) {
                // Đặt bệnh nhân vào giường
                bed.assignPatient(patientId);
                
                // Tạo bản ghi sử dụng phòng
                RoomUsageRecord usageRecord = new RoomUsageRecord(patientId, bed.getBedId(), doctorInCharge, reason);
                usageHistory.add(usageRecord);
                
                // Cập nhật trạng thái phòng
                updateRoomStatus();
                
                return bed;
            }
        }
        
        throw new IllegalStateException("Phòng không còn giường trống");
    }
    
    // Phương thức xuất viện cho bệnh nhân
    public void dischargePatient(String patientId, String notes) {
        boolean patientFound = false;
        
        // Tìm giường của bệnh nhân
        for (Bed bed : beds) {
            if (!bed.isAvailable() && Objects.equals(bed.getPatientId(), patientId)) {
                bed.releaseBed();
                patientFound = true;
                
                // Cập nhật bản ghi sử dụng phòng
                for (int i = usageHistory.size() - 1; i >= 0; i--) {
                    RoomUsageRecord record = usageHistory.get(i);
                    if (Objects.equals(record.getPatientId(), patientId) && record.getCheckOutTime() == null) {
                        record.completeUsage(notes);
                        break;
                    }
                }
                
                break;
            }
        }
        
        if (!patientFound) {
            throw new IllegalArgumentException("Không tìm thấy bệnh nhân trong phòng");
        }
        
        // Cập nhật trạng thái phòng
        updateRoomStatus();
    }
    
    // Phương thức cập nhật trạng thái phòng dựa trên số giường đã sử dụng
    private void updateRoomStatus() {
        boolean hasPatient = false;
        boolean allBedsOccupied = true;
        
        for (Bed bed : beds) {
            if (!bed.isAvailable()) {
                hasPatient = true;
            } else {
                allBedsOccupied = false;
            }
        }
        
        // Nếu trạng thái hiện tại không phải là đang bảo trì hoặc vệ sinh
        if (status != RoomStatus.MAINTENANCE && status != RoomStatus.CLEANING) {
            if (!hasPatient) {
                status = RoomStatus.AVAILABLE;
            } else if (allBedsOccupied) {
                status = RoomStatus.OCCUPIED;
            } else {
                status = RoomStatus.OCCUPIED;
            }
        }
    }
    
    // Phương thức đặt phòng
    public void reserveRoom() {
        if (status == RoomStatus.AVAILABLE) {
            status = RoomStatus.RESERVED;
        } else {
            throw new IllegalStateException("Phòng không trong trạng thái trống để đặt");
        }
    }
    
    // Phương thức chuyển phòng sang trạng thái bảo trì
    public void setUnderMaintenance() {
        if (getOccupiedBedCount() == 0) {
            status = RoomStatus.MAINTENANCE;
        } else {
            throw new IllegalStateException("Không thể bảo trì phòng đang có bệnh nhân");
        }
    }
    
    // Phương thức chuyển phòng sang trạng thái vệ sinh
    public void setUnderCleaning() {
        if (getOccupiedBedCount() == 0) {
            status = RoomStatus.CLEANING;
        } else {
            throw new IllegalStateException("Không thể vệ sinh phòng đang có bệnh nhân");
        }
    }
    
    // Phương thức hoàn thành bảo trì hoặc vệ sinh
    public void completeMaintenanceOrCleaning() {
        if (status == RoomStatus.MAINTENANCE || status == RoomStatus.CLEANING) {
            status = RoomStatus.AVAILABLE;
        }
    }
    
    // Phương thức lấy số giường đã sử dụng
    public int getOccupiedBedCount() {
        int count = 0;
        for (Bed bed : beds) {
            if (!bed.isAvailable()) {
                count++;
            }
        }
        return count;
    }
    
    // Phương thức lấy số giường còn trống
    public int getAvailableBedCount() {
        return capacity - getOccupiedBedCount();
    }
    
    // Phương thức kiểm tra xem phòng có đầy không
    public boolean isFull() {
        return getOccupiedBedCount() == capacity;
    }
    
    // Phương thức kiểm tra xem phòng có trống hoàn toàn không
    public boolean isEmpty() {
        return getOccupiedBedCount() == 0;
    }
    
    // Phương thức tìm bệnh nhân trong phòng
    public Bed findPatientBed(String patientId) {
        for (Bed bed : beds) {
            if (!bed.isAvailable() && Objects.equals(bed.getPatientId(), patientId)) {
                return bed;
            }
        }
        return null;
    }
    
    // Phương thức lấy danh sách bệnh nhân trong phòng
    public List<String> getPatientsList() {
        List<String> patients = new ArrayList<>();
        for (Bed bed : beds) {
            if (!bed.isAvailable() && bed.getPatientId() != null) {
                patients.add(bed.getPatientId());
            }
        }
        return patients;
    }
    
    // Getters and Setters
    public String getRoomId() {
        return roomId;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public RoomType getRoomType() {
        return roomType;
    }
    
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
    
    public RoomStatus getStatus() {
        return status;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public double getPricePerDay() {
        return pricePerDay;
    }
    
    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getFloor() {
        return floor;
    }
    
    public String getBuilding() {
        return building;
    }
    
    public List<Bed> getBeds() {
        return new ArrayList<>(beds); // Trả về bản sao để tránh sửa đổi trực tiếp
    }
    
    public List<RoomUsageRecord> getUsageHistory() {
        return new ArrayList<>(usageHistory); // Trả về bản sao để tránh sửa đổi trực tiếp
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Phòng: ").append(roomNumber)
          .append(" (").append(roomType.getDisplayName()).append(")\n")
          .append("Tòa nhà: ").append(building)
          .append(", Tầng: ").append(floor)
          .append(", Khoa: ").append(department).append("\n")
          .append("Trạng thái: ").append(status.getDisplayName())
          .append(", Giá: ").append(String.format("%,.0f", pricePerDay)).append(" VND/ngày\n")
          .append("Số giường: ").append(capacity)
          .append(" (Đã sử dụng: ").append(getOccupiedBedCount())
          .append(", Còn trống: ").append(getAvailableBedCount()).append(")\n");
          
        return sb.toString();
    }
    
    // Phương thức lấy thông tin chi tiết về phòng và các giường
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder(toString());
        
        sb.append("\nDanh sách giường:\n");
        for (Bed bed : beds) {
            sb.append("- ").append(bed.toString()).append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HospitalRoom room = (HospitalRoom) o;
        return Objects.equals(roomId, room.roomId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }
}