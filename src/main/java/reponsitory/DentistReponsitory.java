package reponsitory;

import Utils.JDBCUtil;

import java.io.IOException;
import java.sql.*;

public class DentistReponsitory {
    public static int getIdDentistLogin(String acc, String pass) {
        try {
            Connection con = JDBCUtil.getConnection();
            String sql = "SELECT id FROM Employee WHERE username = ? AND password = ? AND role = 'Bác sĩ'";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, acc);
            pst.setString(2, pass);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return -1; // Không tìm thấy tài khoản
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getNameDenFormBill(String idBill){
        try {
            Connection con = JDBCUtil.getConnection();
            Statement st= con.createStatement();
            ResultSet rs = st.executeQuery("SELECT e.name AS TenBacSi\n" +
                    "FROM Prescription pr\n" +
                    "JOIN Doctor d ON pr.doctor_id = d.id\n" +
                    "JOIN Employee e ON d.id = e.id\n" +
                    "WHERE pr.id =" + idBill);
            rs.next();
            return rs.getString("TenBacSi");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public static void main(String[] args) {
        System.out.println(DentistReponsitory.getIdDentistLogin("bacsia","password"));
    }
}
