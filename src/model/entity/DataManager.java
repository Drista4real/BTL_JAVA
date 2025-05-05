package model.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;
    private List<User> users = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private List<Admission> admissions = new ArrayList<>();
    private List<MedicalRecord> medicalRecords = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();
    private List<Invoice> invoices = new ArrayList<>();

    private DataManager() {
        // Khởi tạo dữ liệu mẫu
        initSampleData();
    }

    /**
     * Khởi tạo dữ liệu mẫu cho ứng dụng
     */
    private void initSampleData() {
        // Thêm bác sĩ mẫu
        if (users.isEmpty()) {
            User doctor1 = new User("doctor1", "password", "Nguyễn Văn A", "doctora@example.com", "0123456789", Role.DOCTOR);
            User doctor2 = new User("doctor2", "password", "Trần Thị B", "doctorb@example.com", "0123456788", Role.DOCTOR);
            User patient1 = new User("patient1", "password", "Lê Văn C", "patientc@example.com", "0123456787", Role.PATIENT);
            User patient2 = new User("patient2", "password", "Phạm Thị D", "patientd@example.com", "0123456786", Role.PATIENT);
            
            users.add(doctor1);
            users.add(doctor2);
            users.add(patient1);
            users.add(patient2);
        }
    }

    public static DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    // User
    public void addUser(User user) { users.add(user); }
    public List<User> getUsers() { return users; }
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.set(i, user);
                break;
            }
        }
    }
    public void removeUser(String username) {
        users.removeIf(u -> u.getUsername().equals(username));
    }
    
    public User getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
    
    public List<User> getDoctors() {
        return users.stream()
                .filter(u -> u.getRole() == Role.DOCTOR)
                .collect(Collectors.toList());
    }

    // Appointment
    public void addAppointment(Appointment appt) { appointments.add(appt); }
    public List<Appointment> getAppointments() { return appointments; }
    public void updateAppointment(Appointment appt) {
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId().equals(appt.getId())) {
                appointments.set(i, appt);
                break;
            }
        }
    }
    public void removeAppointment(String id) {
        appointments.removeIf(a -> a.getId().equals(id));
    }
    
    public List<Appointment> getAppointmentsByPatient(String patientUsername) {
        return appointments.stream()
                .filter(a -> a.getPatient().equals(patientUsername))
                .collect(Collectors.toList());
    }
    
    public Appointment getAppointmentById(String id) {
        return appointments.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    // Admission
    public void addAdmission(Admission admission) { admissions.add(admission); }
    public List<Admission> getAdmissions() { return admissions; }
    public void updateAdmission(Admission admission) {
        for (int i = 0; i < admissions.size(); i++) {
            if (admissions.get(i).getAdmissionId().equals(admission.getAdmissionId())) {
                admissions.set(i, admission);
                break;
            }
        }
    }
    public void removeAdmission(String id) {
        admissions.removeIf(a -> a.getAdmissionId().equals(id));
    }
    
    public List<Admission> getAdmissionsByPatient(String patientId) {
        return admissions.stream()
                .filter(a -> a.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }
    
    public Admission getAdmissionById(String id) {
        return admissions.stream()
                .filter(a -> a.getAdmissionId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    // MedicalRecord
    public void addMedicalRecord(MedicalRecord record) { medicalRecords.add(record); }
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public MedicalRecord getMedicalRecordByPatient(String patientId) {
        return medicalRecords.stream()
                .filter(r -> r.getPatientId().equals(patientId))
                .findFirst()
                .orElse(null);
    }
    
    // Prescription
    public void addPrescription(Prescription prescription) { prescriptions.add(prescription); }
    public List<Prescription> getPrescriptions() { return prescriptions; }
    public List<Prescription> getPrescriptionsByPatient(String patientId) {
        return prescriptions.stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }
    
    // Invoice
    public void addInvoice(Invoice invoice) { invoices.add(invoice); }
    public List<Invoice> getInvoices() { return invoices; }
    public List<Invoice> getInvoicesByPatient(String patientId) {
        return invoices.stream()
                .filter(i -> i.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }
    
    /**
     * Tạo cuộc hẹn mới
     * 
     * @param date Ngày hẹn
     * @param time Thời gian hẹn
     * @param doctor Bác sĩ
     * @param patient Bệnh nhân
     * @param reason Lý do khám
     * @return Cuộc hẹn đã được tạo
     */
    public Appointment createAppointment(LocalDate date, LocalTime time, String doctor, String patient, String reason) {
        Appointment appointment = new Appointment(
            UUID.randomUUID().toString(),
            date,
            time,
            doctor,
            patient,
            reason,
            Appointment.AppointmentStatus.PENDING,
            Appointment.PaymentStatus.UNPAID
        );
        appointments.add(appointment);
        return appointment;
    }
    
    /**
     * Tạo lần nhập viện mới
     * 
     * @param patientId Mã bệnh nhân
     * @param doctorId Mã bác sĩ
     * @param roomId Mã phòng
     * @param notes Ghi chú
     * @return Lần nhập viện đã được tạo
     */
    public Admission createAdmission(String patientId, String doctorId, String roomId, String notes) {
        Admission admission = new Admission(
            UUID.randomUUID().toString(),
            patientId,
            LocalDate.now(),
            doctorId,
            roomId,
            notes
        );
        admissions.add(admission);
        return admission;
    }
}