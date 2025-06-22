package model;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {
	private int id;
    private String patientName;
    private List<Prescription> prescriptions = new ArrayList<>();

    public MedicalRecord(int id, String patientName) {
        this.id = id;
        this.patientName = patientName;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public List<Prescription> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(List<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}
}
