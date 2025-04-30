package model.entity;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Lớp Admission (Quản lý nhập viện bệnh nhân)
 * Lưu trữ thông tin về một lần nhập viện của bệnh nhân trong hệ thống quản lý bệnh viện
 */
public class Admission {
    private String admissionId;      // Mã nhập viện
    private String patientId;        // Mã bệnh nhân
    private LocalDate admissionDate; // Ngày nhập viện
    private String doctorId;         // Mã bác sĩ phụ trách
    private String roomId;           // Mã phòng bệnh
    private LocalDate dischargeDate; // Ngày xuất viện
    private String notes;            // Ghi chú

    /**
     * Constructor cho trường hợp bệnh nhân mới nhập viện (chưa có ngày xuất viện)
     *
     * @param admissionId   Mã nhập viện
     * @param patientId     Mã bệnh nhân
     * @param admissionDate Ngày nhập viện
     * @param doctorId      Mã bác sĩ phụ trách
     * @param roomId        Mã phòng bệnh
     * @param notes         Ghi chú
     */
    public Admission(String admissionId, String patientId, LocalDate admissionDate,
                     String doctorId, String roomId, String notes) {
        this.admissionId = admissionId;
        this.patientId = patientId;
        this.admissionDate = admissionDate;
        this.doctorId = doctorId;
        this.roomId = roomId;
        this.notes = notes;
        this.dischargeDate = null; // Khi mới nhập viện, chưa có ngày xuất viện
    }

    /**
     * Constructor đầy đủ cho trường hợp đã có thông tin xuất viện
     *
     * @param admissionId   Mã nhập viện
     * @param patientId     Mã bệnh nhân
     * @param admissionDate Ngày nhập viện
     * @param doctorId      Mã bác sĩ phụ trách
     * @param roomId        Mã phòng bệnh
     * @param dischargeDate Ngày xuất viện
     * @param notes         Ghi chú
     */
    public Admission(String admissionId, String patientId, LocalDate admissionDate,
                     String doctorId, String roomId, LocalDate dischargeDate, String notes) {
        this.admissionId = admissionId;
        this.patientId = patientId;
        this.admissionDate = admissionDate;
        this.doctorId = doctorId;
        this.roomId = roomId;
        this.dischargeDate = dischargeDate;
        this.notes = notes;
    }

    /**
     * Constructor với kiểu dữ liệu chuỗi cho ngày tháng (để tương thích ngược)
     *
     * @param admissionId     Mã nhập viện
     * @param patientId       Mã bệnh nhân
     * @param admissionDateStr Ngày nhập viện (dạng chuỗi)
     * @param doctorId        Mã bác sĩ phụ trách
     * @param roomId          Mã phòng bệnh
     * @param dischargeDateStr Ngày xuất viện (dạng chuỗi, có thể null)
     * @param notes           Ghi chú
     */
    public Admission(String admissionId, String patientId, String admissionDateStr,
                     String doctorId, String roomId, String dischargeDateStr, String notes) {
        this.admissionId = admissionId;
        this.patientId = patientId;
        this.admissionDate = LocalDate.parse(admissionDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.doctorId = doctorId;
        this.roomId = roomId;
        this.notes = notes;

        if (dischargeDateStr != null && !dischargeDateStr.trim().isEmpty()) {
            this.dischargeDate = LocalDate.parse(dischargeDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else {
            this.dischargeDate = null;
        }
    }

    // Getters
    public String getAdmissionId() {
        return admissionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getRoomId() {
        return roomId;
    }

    public LocalDate getDischargeDate() {
        return dischargeDate;
    }

    public String getNotes() {
        return notes;
    }

    /**
     * Lấy ngày nhập viện dưới dạng chuỗi (dd/MM/yyyy)
     * @return Chuỗi ngày nhập viện
     */
    public String getAdmissionDateString() {
        return admissionDate != null ? admissionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;
    }

    /**
     * Lấy ngày xuất viện dưới dạng chuỗi (dd/MM/yyyy)
     * @return Chuỗi ngày xuất viện hoặc null nếu chưa xuất viện
     */
    public String getDischargeDateString() {
        return dischargeDate != null ? dischargeDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;
    }

    // Setters với validation
    public void setAdmissionId(String admissionId) {
        if (admissionId == null || admissionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhập viện không được để trống");
        }
        this.admissionId = admissionId;
    }

    public void setPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bệnh nhân không được để trống");
        }
        this.patientId = patientId;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        if (admissionDate == null) {
            throw new IllegalArgumentException("Ngày nhập viện không được để trống");
        }
        if (dischargeDate != null && admissionDate.isAfter(dischargeDate)) {
            throw new IllegalArgumentException("Ngày nhập viện không thể sau ngày xuất viện");
        }
        this.admissionDate = admissionDate;
    }

    public void setAdmissionDate(String admissionDateStr) {
        if (admissionDateStr == null || admissionDateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Ngày nhập viện không được để trống");
        }
        LocalDate date = LocalDate.parse(admissionDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (dischargeDate != null && date.isAfter(dischargeDate)) {
            throw new IllegalArgumentException("Ngày nhập viện không thể sau ngày xuất viện");
        }
        this.admissionDate = date;
    }

    public void setDoctorId(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bác sĩ không được để trống");
        }
        this.doctorId = doctorId;
    }

    public void setRoomId(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng bệnh không được để trống");
        }
        this.roomId = roomId;
    }

    public void setDischargeDate(LocalDate dischargeDate) {
        if (dischargeDate != null && admissionDate != null && dischargeDate.isBefore(admissionDate)) {
            throw new IllegalArgumentException("Ngày xuất viện không thể trước ngày nhập viện");
        }
        this.dischargeDate = dischargeDate;
    }

    public void setDischargeDate(String dischargeDateStr) {
        if (dischargeDateStr == null || dischargeDateStr.trim().isEmpty()) {
            this.dischargeDate = null;
            return;
        }

        LocalDate date = LocalDate.parse(dischargeDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (admissionDate != null && date.isBefore(admissionDate)) {
            throw new IllegalArgumentException("Ngày xuất viện không thể trước ngày nhập viện");
        }
        this.dischargeDate = date;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Kiểm tra xem bệnh nhân đã xuất viện chưa
     * @return true nếu đã xuất viện, false nếu chưa
     */
    public boolean isDischarged() {
        return dischargeDate != null;
    }

    /**
     * Tính số ngày nằm viện
     * @return Số ngày nằm viện tính đến nay hoặc đến ngày xuất viện
     */
    public long getHospitalStayDays() {
        if (admissionDate == null) {
            return 0;
        }

        LocalDate endDate = dischargeDate != null ? dischargeDate : LocalDate.now();
        return java.time.temporal.ChronoUnit.DAYS.between(admissionDate, endDate) + 1; // +1 để tính cả ngày nhập viện
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admission admission = (Admission) o;
        return Objects.equals(admissionId, admission.admissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(admissionId);
    }

    @Override
    public String toString() {
        return "Admission{" +
                "admissionId='" + admissionId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", admissionDate=" + getAdmissionDateString() +
                ", doctorId='" + doctorId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", dischargeDate=" + getDischargeDateString() +
                ", notes='" + notes + '\'' +
                '}';
    }
}
