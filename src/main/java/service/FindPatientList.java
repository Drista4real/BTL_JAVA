package service;

import Utils.JDBCUtil;
import model.Patient;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FindPatientList {
    public static List<Patient> listPatients(String cphoneNumber){
        List<Patient> listPatientFind=new ArrayList<>();
        try {
            Connection connection= JDBCUtil.getConnection();
            Statement statement=connection.createStatement();
            String query="select * from Patient where phoneNumber like '"+cphoneNumber+"%' or name like '"+cphoneNumber+"%'";
            System.out.println(query);
            ResultSet rs=statement.executeQuery(query);
            int id; String name; Date birthDate; String address; int gender; String phoneNumber; String idCard;
            while(rs.next()){
                id=rs.getInt("id");
                name=rs.getString("name");
                birthDate=rs.getDate("birthDate");
                address=rs.getString("address");
                gender=rs.getInt("gender");
                phoneNumber=rs.getString("phoneNumber");
                idCard=rs.getString("idCard");

                listPatientFind.add(new Patient(id, name, birthDate, address, gender, phoneNumber, idCard));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listPatientFind;
    }

    public static void main(String[] args) {
        List<Patient> listPatientFind=FindPatientList.listPatients("098");
        for (Patient patient : listPatientFind) {
            patient.showInfo();
        }
    }
}
