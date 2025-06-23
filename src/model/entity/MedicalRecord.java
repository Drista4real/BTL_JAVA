package model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

public class MedicalRecord {
    private String recordId;
    private String patientId;
    private LocalDate creationDate;
    private List<MedicalEntry> medicalHistory;
    private String bloodType;
    private Double height;
    private Double weight;
    private String allergies;
    private String chronicConditions;
    private String familyMedicalHistory;
    private String additionalNotes;

    private static final Set<String> VALID_BLOOD_TYPES = new HashSet<>(List.of(
            "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"
    ));

    public MedicalRecord(String patientId) {
        this.recordId = UUID.randomUUID().toString();
        this.patientId = validateNonEmpty(patientId, "Patient ID cannot be empty");
        this.creationDate = LocalDate.now();
        this.medicalHistory = new ArrayList<>();
        this.allergies = "";
        this.chronicConditions = "";
        this.familyMedicalHistory = "";
        this.additionalNotes = "";
    }

    public MedicalRecord(String recordId, String patientId, LocalDate creationDate,
                         String bloodType, Double height, Double weight,
                         String allergies, String chronicConditions,
                         String familyMedicalHistory, String additionalNotes) {
        this.recordId = validateNonEmpty(recordId, "Record ID cannot be empty");
        this.patientId = validateNonEmpty(patientId, "Patient ID cannot be empty");
        this.creationDate = validateCreationDate(creationDate);
        this.medicalHistory = new ArrayList<>();
        setBloodType(bloodType);
        setHeight(height);
        setWeight(weight);
        this.allergies = allergies != null ? allergies : "";
        this.chronicConditions = chronicConditions != null ? chronicConditions : "";
        this.familyMedicalHistory = familyMedicalHistory != null ? familyMedicalHistory : "";
        this.additionalNotes = additionalNotes != null ? additionalNotes : "";
    }

    public static class MedicalEntry {
        private String entryId;
        private LocalDateTime date;
        private String doctorId;
        private String symptoms;
        private String diagnosis;
        private String treatmentPlan;
        private String medications;
        private String labResults;
        private String followUpInstructions;
        private String notes;
        private String lifestyleRecommendations;

        public MedicalEntry(String doctorId, String symptoms, String diagnosis,
                            String treatmentPlan, String medications) {
            this.entryId = UUID.randomUUID().toString();
            this.date = LocalDateTime.now();
            this.doctorId = validateNonEmpty(doctorId, "Doctor ID cannot be empty");
            this.symptoms = symptoms != null ? symptoms : "";
            this.diagnosis = diagnosis != null ? diagnosis : "";
            this.treatmentPlan = treatmentPlan != null ? treatmentPlan : "";
            this.medications = medications != null ? medications : "";
            this.labResults = "";
            this.followUpInstructions = "";
            this.notes = "";
            this.lifestyleRecommendations = "";
        }

        public MedicalEntry(String entryId, LocalDateTime date, String doctorId,
                            String symptoms, String diagnosis, String treatmentPlan,
                            String medications, String labResults,
                            String followUpInstructions, String notes,
                            String lifestyleRecommendations) {
            this.entryId = validateNonEmpty(entryId, "Entry ID cannot be empty");
            this.date = validateNonNull(date, "Date cannot be null");
            this.doctorId = validateNonEmpty(doctorId, "Doctor ID cannot be empty");
            this.symptoms = symptoms != null ? symptoms : "";
            this.diagnosis = diagnosis != null ? diagnosis : "";
            this.treatmentPlan = treatmentPlan != null ? treatmentPlan : "";
            this.medications = medications != null ? medications : "";
            this.labResults = labResults != null ? labResults : "";
            this.followUpInstructions = followUpInstructions != null ? followUpInstructions : "";
            this.notes = notes != null ? notes : "";
            this.lifestyleRecommendations = lifestyleRecommendations != null ? lifestyleRecommendations : "";
        }

        public String getEntryId() { return entryId; }
        public LocalDateTime getDate() { return date; }
        public String getDoctorId() { return doctorId; }
        public String getSymptoms() { return symptoms; }
        public void setSymptoms(String symptoms) { this.symptoms = symptoms != null ? symptoms : ""; }
        public String getDiagnosis() { return diagnosis; }
        public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis != null ? diagnosis : ""; }
        public String getTreatmentPlan() { return treatmentPlan; }
        public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan != null ? treatmentPlan : ""; }
        public String getMedications() { return medications; }
        public void setMedications(String medications) { this.medications = medications != null ? medications : ""; }
        public String getLabResults() { return labResults; }
        public void setLabResults(String labResults) { this.labResults = labResults != null ? labResults : ""; }
        public String getFollowUpInstructions() { return followUpInstructions; }
        public void setFollowUpInstructions(String followUpInstructions) { this.followUpInstructions = followUpInstructions != null ? followUpInstructions : ""; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes != null ? notes : ""; }
        public String getLifestyleRecommendations() { return lifestyleRecommendations; }
        public void setLifestyleRecommendations(String lifestyleRecommendations) { this.lifestyleRecommendations = lifestyleRecommendations != null ? lifestyleRecommendations : ""; }

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
                    "\nGhi chú: " + notes +
                    "\nLời khuyên: " + lifestyleRecommendations;
        }
    }

    public void addMedicalEntry(MedicalEntry entry) {
        validateNonNull(entry, "Medical entry cannot be null");
        this.medicalHistory.add(entry);
    }

    public void removeMedicalEntry(String entryId) {
        medicalHistory.removeIf(entry -> entry.getEntryId().equals(entryId));
    }

    public MedicalEntry createNewMedicalEntry(String doctorId, String symptoms,
                                              String diagnosis, String treatmentPlan,
                                              String medications) {
        MedicalEntry entry = new MedicalEntry(doctorId, symptoms, diagnosis, treatmentPlan, medications);
        this.medicalHistory.add(entry);
        return entry;
    }

    public MedicalEntry getLatestEntry() {
        return medicalHistory.isEmpty() ? null : medicalHistory.get(medicalHistory.size() - 1);
    }

    public MedicalEntry getEntryById(String entryId) {
        for (MedicalEntry entry : medicalHistory) {
            if (entry.getEntryId().equals(entryId)) {
                return entry;
            }
        }
        return null;
    }

    public String getRecordId() { return recordId; }
    public String getPatientId() { return patientId; }
    public LocalDate getCreationDate() { return creationDate; }
    public List<MedicalEntry> getMedicalHistory() { return new ArrayList<>(medicalHistory); }
    public String getBloodType() { return bloodType; }

    public void setBloodType(String bloodType) {
        if (bloodType != null && !bloodType.trim().isEmpty() && !VALID_BLOOD_TYPES.contains(bloodType)) {
            throw new IllegalArgumentException("Invalid blood type. Must be one of: " + VALID_BLOOD_TYPES);
        }
        this.bloodType = bloodType;
    }

    public Double getHeight() { return height; }

    public void setHeight(Double height) {
        if (height != null && height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        this.height = height;
    }

    public Double getWeight() { return weight; }

    public void setWeight(Double weight) {
        if (weight != null && weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        this.weight = weight;
    }

    public Double getBMI() {
        if (height == null || weight == null || height <= 0) {
            return null;
        }
        return weight / ((height / 100) * (height / 100));
    }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies != null ? allergies : ""; }
    public String getChronicConditions() { return chronicConditions; }
    public void setChronicConditions(String chronicConditions) { this.chronicConditions = chronicConditions != null ? chronicConditions : ""; }
    public String getFamilyMedicalHistory() { return familyMedicalHistory; }
    public void setFamilyMedicalHistory(String familyMedicalHistory) { this.familyMedicalHistory = familyMedicalHistory != null ? familyMedicalHistory : ""; }
    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes != null ? additionalNotes : ""; }

    public void updateBasicInfo(String bloodType, Double height, Double weight) {
        setBloodType(bloodType);
        setHeight(height);
        setWeight(weight);
    }

    public void updateMedicalHistory(String allergies, String chronicConditions,
                                     String familyMedicalHistory) {
        setAllergies(allergies);
        setChronicConditions(chronicConditions);
        setFamilyMedicalHistory(familyMedicalHistory);
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
                if (entry.getLifestyleRecommendations() != null && !entry.getLifestyleRecommendations().isEmpty()) {
                    sb.append("Lời khuyên: ").append(entry.getLifestyleRecommendations()).append("\n");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static String validateNonEmpty(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    private static <T> T validateNonNull(T value, String errorMessage) {
        if (value == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    private LocalDate validateCreationDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Creation date cannot be null");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Creation date cannot be in the future");
        }
        return date;
    }
}