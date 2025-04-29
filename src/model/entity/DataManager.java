package model.entity;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<User> users = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();

    private DataManager() {}

    public static DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    // User
    public void addUser(User user) { users.add(user); }
    public List<User> getUsers() { return users; }
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.set(i, user);
                break;
            }
        }
    }
    public void removeUser(String username) {
        users.removeIf(u -> u.getUsername().equals(username));
    }

    // Appointment
    public void addAppointment(Appointment appt) { appointments.add(appt); }
    public List<Appointment> getAppointments() { return appointments; }
    public void updateAppointment(Appointment appt) {
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId().equals(appt.getId())) {
                appointments.set(i, appt);
                break;
            }
        }
    }
    public void removeAppointment(String id) {
        appointments.removeIf(a -> a.getId().equals(id));
    }
} 