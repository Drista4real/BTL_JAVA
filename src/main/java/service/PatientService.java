package service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Patient;
import reponsitory.Patientreponsitory;

//service/PatientService.java
public class PatientService {
 private Patientreponsitory patientRepository = new Patientreponsitory();

 public List<Patient> getAllPatients() throws FileNotFoundException, ClassNotFoundException, IOException {
     try {
         return patientRepository.getAllPatientsL();
     } catch (SQLException e) {
         e.printStackTrace(); // hoặc log lỗi
         return new ArrayList<>();
     }
 }
 public boolean addPatient(Patient patient) throws SQLException, IOException, ClassNotFoundException {
     return patientRepository.addPatient(patient);
 }

 public int addPatientAndReturnId(Patient patient) throws SQLException, ClassNotFoundException, IOException {
	    return patientRepository.addPatientAndReturnId(patient);
	}
 
 public boolean isPhoneExists(String phone) throws SQLException, ClassNotFoundException, FileNotFoundException, IOException {
	    return Patientreponsitory.isPhoneExists(phone);
	}

	public boolean isIdCardExists(String idCard) throws SQLException, ClassNotFoundException, FileNotFoundException, IOException {
	    return Patientreponsitory.isIdCardExists(idCard);
	}
}

