package model.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Admission {
    private String admissionId;
    private String patientId;
    private LocalDate admissionDate;
    private String doctorId;
    private String roomId;
    private LocalDate dischargeDate;
    private String notes;

    public Admission() {
        this.admissionId = "";
        this.patientId = "";
        this.admissionDate = null;
        this.doctorId = "";
        this.roomId = "";
        this.dischargeDate = null;
        this.notes = "";
    }

    public Admission(String admissionId, String patientId, LocalDate admissionDate,
                     String doctorId, String roomId, String notes) {
        this.admissionId = admissionId != null ? admissionId : "";
        this.patientId = patientId != null ? patientId : "";
        this.admissionDate = admissionDate;
        this.doctorId = doctorId != null ? doctorId : "";
        this.roomId = roomId != null ? roomId : "";
        this.notes = notes != null ? notes : "";
        this.dischargeDate = null;
    }

    public Admission(String admissionId, String patientId, LocalDate admissionDate,
                     String doctorId, String roomId, LocalDate dischargeDate, String notes) {
        this.admissionId = admissionId != null ? admissionId : "";
        this.patientId = patientId != null ? patientId : "";
        this.admissionDate = admissionDate;
        this.doctorId = doctorId != null ? doctorId : "";
        this.roomId = roomId != null ? roomId : "";
        this.dischargeDate = dischargeDate;
        this.notes = notes != null ? notes : "";
    }

    public Admission(String admissionId, String patientId, String admissionDateStr,
                     String doctorId, String roomId, String dischargeDateStr, String notes) {
        this.admissionId = admissionId != null ? admissionId : "";
        this.patientId = patientId != null ? patientId : "";
        this.doctorId = doctorId != null ? doctorId : "";
        this.roomId = roomId != null ? roomId : "";
        this.notes = notes != null ? notes : "";

        if (admissionDateStr != null && !admissionDateStr.trim().isEmpty()) {
            this.admissionDate = LocalDate.parse(admissionDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else {
            this.admissionDate = null;
        }

        if (dischargeDateStr != null && !dischargeDateStr.trim().isEmpty()) {
            this.dischargeDate = LocalDate.parse(dischargeDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            if (this.admissionDate != null && this.dischargeDate.isBefore(this.admissionDate)) {
                throw new IllegalArgumentException("Ngày xuất viện không thể trước ngày nhập viện");
            }
        } else {
            this.dischargeDate = null;
        }
    }

    // Getters
    public String getAdmissionId() { return admissionId; }
    public String getPatientId() { return patientId; }
    public LocalDate getAdmissionDate() { return admissionDate; } // Fixed getter
    public String getDoctorId() { return doctorId; }
    public String getRoomId() { return roomId; }
    public LocalDate getDischargeDate() { return dischargeDate; }
    public String getNotes() { return notes; }

    public String getAdmissionDateString() {
        return admissionDate != null ? admissionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public String getDischargeDateString() {
        return dischargeDate != null ? dischargeDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    // Setters
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
        this.notes = notes != null ? notes : "";
    }

    public boolean isDischarged() {
        return dischargeDate != null;
    }

    public long getHospitalStayDays() {
        if (admissionDate == null) {
            return 0;
        }
        LocalDate endDate = dischargeDate != null ? dischargeDate : LocalDate.now();
        return java.time.temporal.ChronoUnit.DAYS.between(admissionDate, endDate) + 1;
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