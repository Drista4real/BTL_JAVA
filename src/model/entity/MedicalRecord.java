package model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MedicalRecord {
    private String recordId;
    private String patientId; // Tham chiếu đến User.username của bệnh nhân
    private LocalDate creationDate;
    private List<MedicalEntry> medicalHistory;

    // Thông tin hồ sơ bệnh án
    private String bloodType; // Nhóm máu
    private Double height; // Chiều cao (cm)
    private Double weight; // Cân nặng (kg)
    private String allergies; // Thông tin dị ứng
    private String chronicConditions; // Bệnh mãn tính
    private String familyMedicalHistory; // Tiền sử bệnh gia đình
    private String additionalNotes; // Ghi chú bổ sung

    // Constructor
    public MedicalRecord(String patientId) {
        this.recordId = UUID.randomUUID().toString();
        this.patientId = patientId;
        this.creationDate = LocalDate.now();
        this.medicalHistory = new ArrayList<>();
        this.allergies = "";
        this.chronicConditions = "";
        this.familyMedicalHistory = "";
        this.additionalNotes = "";
    }

    // Constructor đầy đủ
    public MedicalRecord(String recordId, String patientId, LocalDate creationDate,
                         String bloodType, Double height, Double weight,
                         String allergies, String chronicConditions,
                         String familyMedicalHistory, String additionalNotes) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.creationDate = creationDate;
        this.medicalHistory = new ArrayList<>();
        this.bloodType = bloodType;
        this.height = height;
        this.weight = weight;
        this.allergies = allergies;
        this.chronicConditions = chronicConditions;
        this.familyMedicalHistory = familyMedicalHistory;
        this.additionalNotes = additionalNotes;
    }

    // Lớp nội bộ để lưu trữ mỗi lần khám và chuẩn đoán
    public static class MedicalEntry {
        private String entryId;
        private LocalDateTime date;
        private String doctorId; // Tham chiếu đến User.username của bác sĩ
        private String symptoms; // Triệu chứng
        private String diagnosis; // Chuẩn đoán
        private String treatmentPlan; // Kế hoạch điều trị
        private String medications; // Thuốc kê đơn
        private String labResults; // Kết quả xét nghiệm
        private String followUpInstructions; // Hướng dẫn tái khám
        private String notes; // Ghi chú

        public MedicalEntry(String doctorId, String symptoms, String diagnosis,
                            String treatmentPlan, String medications) {
            this.entryId = UUID.randomUUID().toString();
            this.date = LocalDateTime.now();
            this.doctorId = doctorId;
            this.symptoms = symptoms;
            this.diagnosis = diagnosis;
            this.treatmentPlan = treatmentPlan;
            this.medications = medications;
            this.labResults = "";
            this.followUpInstructions = "";
            this.notes = "";
        }

        // Constructor đầy đủ
        public MedicalEntry(String entryId, LocalDateTime date, String doctorId,
                            String symptoms, String diagnosis, String treatmentPlan,
                            String medications, String labResults,
                            String followUpInstructions, String notes) {
            this.entryId = entryId;
            this.date = date;
            this.doctorId = doctorId;
            this.symptoms = symptoms;
            this.diagnosis = diagnosis;
            this.treatmentPlan = treatmentPlan;
            this.medications = medications;
            this.labResults = labResults;
            this.followUpInstructions = followUpInstructions;
            this.notes = notes;
        }

        // Getters and Setters
        public String getEntryId() {
            return entryId;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public String getDoctorId() {
            return doctorId;
        }

        public String getSymptoms() {
            return symptoms;
        }

        public void setSymptoms(String symptoms) {
            this.symptoms = symptoms;
        }

        public String getDiagnosis() {
            return diagnosis;
        }

        public void setDiagnosis(String diagnosis) {
            this.diagnosis = diagnosis;
        }

        public String getTreatmentPlan() {
            return treatmentPlan;
        }

        public void setTreatmentPlan(String treatmentPlan) {
            this.treatmentPlan = treatmentPlan;
        }

        public String getMedications() {
            return medications;
        }

        public void setMedications(String medications) {
            this.medications = medications;
        }

        public String getLabResults() {
            return labResults;
        }

        public void setLabResults(String labResults) {
            this.labResults = labResults;
        }

        public String getFollowUpInstructions() {
            return followUpInstructions;
        }

        public void setFollowUpInstructions(String followUpInstructions) {
            this.followUpInstructions = followUpInstructions;
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
            return "Ngày khám: " + date.format(formatter) +
                    "\nBác sĩ: " + doctorId +
                    "\nTriệu chứng: " + symptoms +
                    "\nChuẩn đoán: " + diagnosis +
                    "\nKế hoạch điều trị: " + treatmentPlan +
                    "\nThuốc kê đơn: " + medications +
                    "\nKết quả xét nghiệm: " + labResults +
                    "\nHướng dẫn tái khám: " + followUpInstructions +
                    "\nGhi chú: " + notes;
        }
    }

    // Phương thức thêm lần khám mới
    public void addMedicalEntry(MedicalEntry entry) {
        this.medicalHistory.add(entry);
    }

    // Phương thức tạo lần khám mới
    public MedicalEntry createNewMedicalEntry(String doctorId, String symptoms,
                                              String diagnosis, String treatmentPlan,
                                              String medications) {
        MedicalEntry entry = new MedicalEntry(doctorId, symptoms, diagnosis,
                treatmentPlan, medications);
        this.medicalHistory.add(entry);
        return entry;
    }

    // Phương thức lấy lần khám gần nhất
    public MedicalEntry getLatestEntry() {
        if (medicalHistory.isEmpty()) {
            return null;
        }
        return medicalHistory.get(medicalHistory.size() - 1);
    }

    // Phương thức lấy lần khám theo id
    public MedicalEntry getEntryById(String entryId) {
        for (MedicalEntry entry : medicalHistory) {
            if (entry.getEntryId().equals(entryId)) {
                return entry;
            }
        }
        return null;
    }

    // Getters and Setters
    public String getRecordId() {
        return recordId;
    }

    public String getPatientId() {
        return patientId;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public List<MedicalEntry> getMedicalHistory() {
        return new ArrayList<>(medicalHistory); // Trả về bản sao để tránh sửa đổi trực tiếp
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    // Tính BMI (Body Mass Index)
    public Double getBMI() {
        if (height == null || weight == null || height <= 0) {
            return null;
        }
        // BMI = weight(kg) / (height(m) * height(m))
        return weight / ((height / 100) * (height / 100));
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getChronicConditions() {
        return chronicConditions;
    }

    public void setChronicConditions(String chronicConditions) {
        this.chronicConditions = chronicConditions;
    }

    public String getFamilyMedicalHistory() {
        return familyMedicalHistory;
    }

    public void setFamilyMedicalHistory(String familyMedicalHistory) {
        this.familyMedicalHistory = familyMedicalHistory;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    // Phương thức cập nhật thông tin cơ bản
    public void updateBasicInfo(String bloodType, Double height, Double weight) {
        this.bloodType = bloodType;
        this.height = height;
        this.weight = weight;
    }

    // Phương thức cập nhật tiền sử bệnh
    public void updateMedicalHistory(String allergies, String chronicConditions,
                                     String familyMedicalHistory) {
        this.allergies = allergies;
        this.chronicConditions = chronicConditions;
        this.familyMedicalHistory = familyMedicalHistory;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("Hồ sơ y tế:\n");
        sb.append("ID hồ sơ: ").append(recordId).append("\n");
        sb.append("ID bệnh nhân: ").append(patientId).append("\n");
        sb.append("Ngày tạo: ").append(creationDate.format(formatter)).append("\n");
        sb.append("Nhóm máu: ").append(bloodType != null ? bloodType : "Chưa có thông tin").append("\n");
        sb.append("Chiều cao: ").append(height != null ? height + " cm" : "Chưa có thông tin").append("\n");
        sb.append("Cân nặng: ").append(weight != null ? weight + " kg" : "Chưa có thông tin").append("\n");
        sb.append("BMI: ").append(getBMI() != null ? String.format("%.2f", getBMI()) : "Chưa có thông tin").append("\n");
        sb.append("Dị ứng: ").append(allergies != null && !allergies.isEmpty() ? allergies : "Không có").append("\n");
        sb.append("Bệnh mãn tính: ").append(chronicConditions != null && !chronicConditions.isEmpty() ? chronicConditions : "Không có").append("\n");
        sb.append("Tiền sử bệnh gia đình: ").append(familyMedicalHistory != null && !familyMedicalHistory.isEmpty() ? familyMedicalHistory : "Không có").append("\n");
        sb.append("Ghi chú bổ sung: ").append(additionalNotes != null && !additionalNotes.isEmpty() ? additionalNotes : "Không có").append("\n");

        return sb.toString();
    }

    // Xuất lịch sử khám chữa bệnh
    public String printMedicalHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("LỊCH SỬ KHÁM BỆNH\n");
        sb.append("Bệnh nhân: ").append(patientId).append("\n\n");

        if (medicalHistory.isEmpty()) {
            sb.append("Chưa có lịch sử khám bệnh");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (int i = medicalHistory.size() - 1; i >= 0; i--) {
                MedicalEntry entry = medicalHistory.get(i);
                sb.append("--- Lần khám ").append(medicalHistory.size() - i).append(" ---\n");
                sb.append("Ngày: ").append(entry.getDate().format(formatter)).append("\n");
                sb.append("Bác sĩ: ").append(entry.getDoctorId()).append("\n");
                sb.append("Triệu chứng: ").append(entry.getSymptoms()).append("\n");
                sb.append("Chuẩn đoán: ").append(entry.getDiagnosis()).append("\n");
                sb.append("Kế hoạch điều trị: ").append(entry.getTreatmentPlan()).append("\n");
                sb.append("Thuốc: ").append(entry.getMedications()).append("\n");
                if (entry.getLabResults() != null && !entry.getLabResults().isEmpty()) {
                    sb.append("Kết quả xét nghiệm: ").append(entry.getLabResults()).append("\n");
                }
                if (entry.getFollowUpInstructions() != null && !entry.getFollowUpInstructions().isEmpty()) {
                    sb.append("Hướng dẫn tái khám: ").append(entry.getFollowUpInstructions()).append("\n");
                }
                if (entry.getNotes() != null && !entry.getNotes().isEmpty()) {
                    sb.append("Ghi chú: ").append(entry.getNotes()).append("\n");
                }
                sb.append("\n");
            }


        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalRecord record = (MedicalRecord) o;
        return Objects.equals(recordId, record.recordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordId);
    }
}


