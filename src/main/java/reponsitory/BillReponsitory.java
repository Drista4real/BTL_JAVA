package reponsitory;

import Utils.JDBCUtil;

import java.io.IOException;
import java.sql.*;

public class BillReponsitory {
    public static void updatePaymentStatusToPaid(int prescriptionId) {
        try (Connection con = JDBCUtil.getConnection()) {
            String sql = "UPDATE Prescription SET paymentStatus = 'Đã thanh toán' WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, prescriptionId);
            pst.executeUpdate();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int getPriceInPre(int id_pre) {
        try {
            Connection con = JDBCUtil.getConnection();

            String sql = "SELECT " +
                    "( " +
                    "    (SELECT COALESCE(SUM(s.price * psd.quantity), 0) " +
                    "     FROM PrescriptionServiceDetail psd " +
                    "     JOIN Service s ON psd.service_id = s.id " +
                    "     WHERE psd.prescription_id = ?) " +
                    "  + " +
                    "    (SELECT COALESCE(SUM(d.price * pd.quantity), 0) " +
                    "     FROM PrescriptionDrugDetail pd " +
                    "     JOIN Drug d ON pd.drug_id = d.id " +
                    "     WHERE pd.prescription_id = ?) " +
                    ") AS TongTien";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id_pre);
            pst.setInt(2, id_pre);

            ResultSet rs = pst.executeQuery();
            int total = 0;
            if (rs.next()) {
                total = rs.getInt("TongTien");
            }

            rs.close();
            pst.close();
            con.close();

            return total;

        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
