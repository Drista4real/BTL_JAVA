package service;

import java.sql.Date;

public interface IEmployeeService {
    boolean updateEmployee(int id, String name, Date birthDate, String address,
                           int gender, String phone, String idCard,
                           String username, String password,
                           double salary, String role);

    boolean insertEmployee(String name, Date birthDate, String address, int gender,
                           String phone, String cccd, String username, String password,
                           double salary, int year, String role);
}
