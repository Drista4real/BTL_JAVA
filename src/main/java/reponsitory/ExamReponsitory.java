package reponsitory;

import Utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ExamReponsitory {
    public static void updateExam(String id_doctor, String id_patient) {
        try (Connection conn = JDBCUtil.getConnection()) {
            String sql = "UPDATE Examination " +
                    "SET status = 'Đã khám' " +
                    "WHERE doctor_id = ? AND patient_id = ?";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(id_doctor));
            pst.setInt(2, Integer.parseInt(id_patient));
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Đã cập nhật trạng thái khám thành 'Đã khám' cho bệnh nhân " + id_patient);
            } else {
                System.out.println("Không tìm thấy cuộc khám để cập nhật.");
            }

            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
