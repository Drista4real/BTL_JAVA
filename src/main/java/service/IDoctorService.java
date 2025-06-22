package service;

import model.Doctor;
import model.Examination;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IDoctorService {
    List<Doctor> getAllDoctors() throws SQLException, IOException, ClassNotFoundException;
    boolean addExamination(Examination examination) throws SQLException, IOException, ClassNotFoundException;
}