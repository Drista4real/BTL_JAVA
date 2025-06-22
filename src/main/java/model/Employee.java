package model;

import java.sql.Date;

public class Employee extends Person {
    protected String username;
    protected String password;
    protected double salary;
    protected int yearOfEcperience;
    protected String role; // "Receptionist", "Doctor", "Pharmacist"


    public Employee(int id, String name, Date birthDate, String address, int gender, String phoneNumber, String idCard,
			String username, String password, double salary, int yearOfEcperience, String role) {
		super(id, name, birthDate, address, gender, phoneNumber, idCard);
		this.username = username;
		this.password = password;
		this.salary = salary;
		this.yearOfEcperience = yearOfEcperience;
		this.role = role;
	}

	public void showInfo() {
        super.showInfo();
        System.out.println("Role: " + role + " | Salary: " + salary);
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
