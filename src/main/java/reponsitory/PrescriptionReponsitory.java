package reponsitory;

import Utils.JDBCUtil;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class PrescriptionReponsitory {
    public static int getIdPrescriptionNext() {
        try {
            Connection con = JDBCUtil.getConnection();
            Statement st=con.createStatement();
            ResultSet rs=st.executeQuery("SELECT AUTO_INCREMENT\n" +
                    "FROM INFORMATION_SCHEMA.TABLES\n" +
                    "WHERE TABLE_SCHEMA = 'phongkhamnhakhoa'\n" +
                    "  AND TABLE_NAME = 'Prescription';");
            rs.next();
            return rs.getInt(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static int insertPrescription(int doctor_id, int patient_id, String diagnosis, String treatment, String symptom, String advice) {
        try {
            Connection con = JDBCUtil.getConnection();

            String sql = "INSERT INTO Prescription (doctor_id, patient_id, diagnosis, treatment, symptom, advice, preDate) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // CHỈNH ĐÂY

            pst.setInt(1, doctor_id);
            pst.setInt(2, patient_id);
            pst.setString(3, diagnosis);
            pst.setString(4, treatment);
            pst.setString(5, symptom);
            pst.setString(6, advice);
            pst.setTimestamp(7, Timestamp.valueOf(java.time.LocalDateTime.now()));

            int rowsInserted = pst.executeUpdate();

            int generatedId = -1;
            if (rowsInserted > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1); // lấy id vừa tạo
                }
                rs.close();
                System.out.println("Insert đơn thuốc thành công! ID mới: " + generatedId);
            }

            pst.close();
            con.close();

            return generatedId;
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void insertPrescriptionDetail(int prescription_id, int drug_id, int quantity, int morningDose, int noonDose, int eveningDose) {
        try {
            Connection con = JDBCUtil.getConnection();

            // Ghi rõ cột cần insert
            String sql = "INSERT INTO PrescriptionDrugDetail (prescription_id, drug_id, quantity, morningDose, noonDose, eveningDose) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, prescription_id);
            pst.setInt(2, drug_id);
            pst.setInt(3, quantity);
            pst.setInt(4, morningDose);
            pst.setInt(5, noonDose);
            pst.setInt(6, eveningDose);

            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Insert chi tiết thuốc thành công!");
            }

            pst.close();
            con.close();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        PrescriptionReponsitory prescriptionDAO = new PrescriptionReponsitory();
        System.out.println(Date.valueOf(LocalDate.now()));
    }
}
