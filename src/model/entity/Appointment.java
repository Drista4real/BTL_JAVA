package model.entity;

public class Appointment {
    private String id;
    private String date;
    private String time;
    private String doctor;
    private String patient; // username hoặc tên bệnh nhân
    private String reason;
    private String status; // Chờ khám, Đã khám, Hủy
    private String paymentStatus; // Đã thanh toán, Chưa thanh toán

    public Appointment(String id, String date, String time, String doctor, String patient, String reason, String status, String paymentStatus) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.doctor = doctor;
        this.patient = patient;
        this.reason = reason;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public String getId() { return id; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDoctor() { return doctor; }
    public String getPatient() { return patient; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public String getPaymentStatus() { return paymentStatus; }

    public void setId(String id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    public void setPatient(String patient) { this.patient = patient; }
    public void setReason(String reason) { this.reason = reason; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
} 