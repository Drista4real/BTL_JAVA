package model;

import java.util.ArrayList;
import java.util.List;

public class Prescription {
    private int id;
    private String doctorName;
    private String patientName;
    private String diagnosis;
    private String treatment;
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public List<Drug> getDrugList() {
		return drugList;
	}

	public void setDrugList(List<Drug> drugList) {
		this.drugList = drugList;
	}

	private List<Drug> drugList = new ArrayList<>();

    public Prescription(int id, String doctorName, String patientName, String diagnosis, String treatment) {
        this.id = id;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }
}