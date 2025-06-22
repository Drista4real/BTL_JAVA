package model;


public class Examination {
    private int id;
    private int patientId;
    private int doctorId;
    private String status;

    // getters và setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

//public class Examination {
//    private int id;               // ID của cuộc khám bệnh
////    private int doctorId;         // ID bác sĩ
////    private int patientId;        // ID bệnh nhân
//    private String status;        // Trạng thái cuộc khám (Chưa khám / Đang khám)
//    private String patientName;   // Tên bệnh nhân (dành cho hiển thị)
//    private String doctorName;    // Tên bác sĩ (dành cho hiển thị)
//
//    // Constructor
//    public Examination(int id, int doctorId, int patientId, String status, String patientName, String doctorName) {
//        this.id = id;
////        this.doctorId = doctorId;
////        this.patientId = patientId;
//        this.status = status;
//        this.patientName = patientName;
//        this.doctorName = doctorName;
//    }
//
//    // Getter and Setter methods
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
////    public int getDoctorId() {
////        return doctorId;
////    }
////
////    public void setDoctorId(int doctorId) {
////        this.doctorId = doctorId;
////    }
////
////    public int getPatientId() {
////        return patientId;
////    }
////
////    public void setPatientId(int patientId) {
////        this.patientId = patientId;
////    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getPatientName() {
//        return patientName;
//    }
//
//    public void setPatientName(String patientName) {
//        this.patientName = patientName;
//    }
//
//    public String getDoctorName() {
//        return doctorName;
//    }
//
//    public void setDoctorName(String doctorName) {
//        this.doctorName = doctorName;
//    }
//
//    // To string method for easy display
//    @Override
//    public String toString() {
//        return "Examination{" +
//                "id=" + id +
////                ", doctorId=" + doctorId +
////                ", patientId=" + patientId +
//                ", status='" + status + '\'' +
//                ", patientName='" + patientName + '\'' +
//                ", doctorName='" + doctorName + '\'' +
//                '}';
//    }
//}
//
