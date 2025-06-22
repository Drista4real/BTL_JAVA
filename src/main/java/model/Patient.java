package model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Patient extends Person {
 private MedicalRecord medicalRecord;

 public Patient(int id, String name, Date birthDate, String address, int gender, String phoneNumber, String idCard) {
     super(id, name, birthDate, address, gender, phoneNumber, idCard);
     this.medicalRecord = new MedicalRecord(id, name);  // Mỗi bệnh nhân sẽ có một bệnh án
 }

 public Patient() {
	// TODO Auto-generated constructor stub
}


public Patient(int id, String name, java.util.Date birthUtilDate, String address, int gender, String phone,
		String idCard) {
	// TODO Auto-generated constructor stub
}

public MedicalRecord getMedicalRecord() {
     return medicalRecord;
 }

 public void setMedicalRecord(MedicalRecord medicalRecord) {
     this.medicalRecord = medicalRecord;
 }

 @Override
 public void showInfo() {
     super.showInfo();
     System.out.println("Medical Record ID: " + medicalRecord.getId());
 }

 public void setId(int int1) {
		this.id=int1;
		
	}

	public void setName(String string) {
		this.name=string;
		
	}

	public void setPhone(String string) {
		this.phoneNumber=string;
		
	}

	public void setBirthDate(Date  string) {
		this.birthDate=string;
	}

	public void setGender(int string) {
		this.gender=string;
		
	}

	public void setIdCard(String string) {
		this.idCard=string;
		
	}

	public void setAddress(String string) {
		this.address=string;
		
	}
}

