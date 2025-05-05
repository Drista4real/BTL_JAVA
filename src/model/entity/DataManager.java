package model.entity;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance = new DataManager();
    private List<User> users = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();

    private DataManager() {
        // Private constructor to enforce singleton pattern
    }

    public static DataManager getInstance() {
        return instance;
    }

    // User management methods
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.set(i, user);
                break;
            }
        }
    }

    public void deleteUser(String username) {
        users.removeIf(u -> u.getUsername().equals(username));
    }

    // Appointment management methods
    public List<Appointment> getAppointments() {
        return new ArrayList<>(appointments);
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public void updateAppointment(Appointment appointment) {
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId().equals(appointment.getId())) {
                appointments.set(i, appointment);
                break;
            }
        }
    }

    public void deleteAppointment(String appointmentId) {
        appointments.removeIf(a -> a.getId().equals(appointmentId));
    }
}