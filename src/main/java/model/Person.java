package model;

import java.sql.Date;

import Utils.Utils;

public abstract class Person {
	protected int id;
    protected String name;
    protected Date birthDate;
    protected String address;
    protected int gender; // 0: Male, 1: Female
    protected String phoneNumber;
    protected String idCard;

    public Person(int id, String name, Date birthDate, String address, int gender, String phoneNumber, String idCard) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
        this.gender = gender;
        
        if (!Utils.validatePhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number.");
        }
        this.phoneNumber = phoneNumber;
        
        if (!Utils.validateIDCard(idCard)) {
            throw new IllegalArgumentException("Invalid ID card.");
        }
        this.idCard = idCard;
    }

    public Person() {}
    
    public int getId() { return id; }
    public String getName() { return name; }
    public Date getBirthDate() { return birthDate; }
    public String getAddress() { return address; }
    public int getGender() { return gender; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getIdCard() { return idCard; }

    public void showInfo() {
        System.out.println("ID: " + id + " | Name: " + name + " | Birth Date: " + Utils.formatDate(birthDate) + " | Address: " + address);
    }
}
