package model.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Appointment {
    private String id;
    private LocalDate date;
    private LocalTime time;
    private String doctor;
    private String patient;
    private String reason;
    private AppointmentStatus status;
    private PaymentStatus paymentStatus;
    private String entryId; // Liên kết với MedicalEntry

    public enum AppointmentStatus {
        PENDING("Chờ khám"),
        COMPLETED("Đã khám"),
        CANCELLED("Hủy");

        private final String displayValue;

        AppointmentStatus(String displayValue) {
            this.displayValue = displayValue;
        }

        public String getDisplayValue() {
            return displayValue;
        }

        public static AppointmentStatus fromString(String text) {
            for (AppointmentStatus status : AppointmentStatus.values()) {
                if (status.displayValue.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Không tìm thấy trạng thái với giá trị: " + text);
        }
    }

    public enum PaymentStatus {
        PAID("Đã thanh toán"),
        UNPAID("Chưa thanh toán");

        private final String displayValue;

        PaymentStatus(String displayValue) {
            this.displayValue = displayValue;
        }

        public String getDisplayValue() {
            return displayValue;
        }

        public static PaymentStatus fromString(String text) {
            for (PaymentStatus status : PaymentStatus.values()) {
                if (status.displayValue.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Không tìm thấy trạng thái thanh toán với giá trị: " + text);
        }
    }

    public Appointment() {
        this.entryId = null;
    }

    public Appointment(String id, String dateStr, String timeStr, String doctor, String patient, String reason,
                       String statusStr, String paymentStatusStr) {
        this.id = id;
        this.date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        this.doctor = doctor;
        this.patient = patient;
        this.reason = reason;
        this.status = AppointmentStatus.fromString(statusStr);
        this.paymentStatus = PaymentStatus.fromString(paymentStatusStr);
        this.entryId = null;
    }

    public Appointment(String id, LocalDate date, LocalTime time, String doctor, String patient, String reason,
                       AppointmentStatus status, PaymentStatus paymentStatus) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.doctor = doctor;
        this.patient = patient;
        this.reason = reason;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.entryId = null;
    }

    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getDoctor() { return doctor; }
    public String getPatient() { return patient; }
    public String getReason() { return reason; }
    public AppointmentStatus getStatus() { return status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }

    public String getStatusDisplay() { return status != null ? status.getDisplayValue() : null; }
    public String getPaymentStatusDisplay() { return paymentStatus != null ? paymentStatus.getDisplayValue() : null; }

    public String getDateString() {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;
    }

    public String getTimeString() {
        return time != null ? time.format(DateTimeFormatter.ofPattern("HH:mm")) : null;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID không được để trống");
        }
        this.id = id;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Ngày không được để trống");
        }
        this.date = date;
    }

    public void setDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Ngày không được để trống");
        }
        this.date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void setTime(LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Thời gian không được để trống");
        }
        this.time = time;
    }

    public void setTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Thời gian không được để trống");
        }
        this.time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public void setDoctor(String doctor) {
        if (doctor == null || doctor.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên bác sĩ không được để trống");
        }
        this.doctor = doctor;
    }

    public void setPatient(String patient) {
        if (patient == null || patient.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên bệnh nhân không được để trống");
        }
        this.patient = patient;
    }

    public void setReason(String reason) { this.reason = reason; }

    public void setStatus(AppointmentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Trạng thái không được để trống");
        }
        this.status = status;
    }

    public void setStatus(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được để trống");
        }
        this.status = AppointmentStatus.fromString(statusStr);
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new IllegalArgumentException("Trạng thái thanh toán không được để trống");
        }
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentStatus(String paymentStatusStr) {
        if (paymentStatusStr == null || paymentStatusStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái thanh toán không được để trống");
        }
        this.paymentStatus = PaymentStatus.fromString(paymentStatusStr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + id + '\'' +
                ", date=" + getDateString() +
                ", time=" + getTimeString() +
                ", doctor='" + doctor + '\'' +
                ", patient='" + patient + '\'' +
                ", reason='" + reason + '\'' +
                ", status=" + getStatusDisplay() +
                ", paymentStatus=" + getPaymentStatusDisplay() +
                ", entryId='" + entryId + '\'' +
                '}';
    }
}