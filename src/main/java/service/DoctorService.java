package service;

import model.Doctor;
import model.Examination;
import reponsitory.DoctorRepository;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DoctorService implements IDoctorService {
    private DoctorRepository doctorRepository;

    public DoctorService() {
        this.doctorRepository = new DoctorRepository();
    }

    // 1. Lấy toàn bộ danh sách bác sĩ
    public List<Doctor> getAllDoctors() throws SQLException, IOException, ClassNotFoundException {
        return doctorRepository.getAllDoctors();
    }

    // 2. Thêm mới một phiếu khám
//    public boolean addExamination(int doctorId, int patientId) throws SQLException, IOException, ClassNotFoundException {
//        return doctorRepository.addExamination(doctorId, patientId);
//    }
    public boolean addExamination(Examination examination) throws SQLException, IOException, ClassNotFoundException {
        return doctorRepository.addExamination(examination);
    }
}
