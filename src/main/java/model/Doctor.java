package model;

import java.sql.Date;
import java.util.LinkedList;
import java.util.Queue;

public class Doctor extends Employee {
    private String specialty;
    private Queue<Patient> patientQueue;

    public Doctor(int id, String name, Date birthDate, String address, int gender, String phoneNumber, String idCard,
                  String username, String password, double salary, int yearsOfExperience, String specialty) {
        super(id, name, birthDate, address, gender, phoneNumber, idCard, username, password, salary, yearsOfExperience, "Doctor");
        this.specialty = specialty;
        this.patientQueue = new LinkedList<>(); // Sửa lỗi khởi tạo Queue
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
	public String toString() {
		return this.name;
	}

	public Queue<Patient> getPatientQueue() { // Sửa kiểu trả về
        return patientQueue;
    }

    public void setPatientQueue(Queue<Patient> patientQueue) { // Sửa kiểu tham số
        this.patientQueue = patientQueue;
    }

    public void addPatientToQueue(Patient patient) { // Sửa kiểu tham số
        patientQueue.add(patient);
    }

    public void processNextPatient() {
        if (!patientQueue.isEmpty()) {
            Patient nextPatient = patientQueue.poll();
            System.out.println("Processing patient: " + nextPatient.getName());
        } else {
            System.out.println("No patients in queue.");
        }
    }
}
