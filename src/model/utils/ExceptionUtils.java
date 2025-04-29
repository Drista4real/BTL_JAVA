package model.utils;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ExceptionUtils {
    private static final Logger LOGGER = Logger.getLogger(ExceptionUtils.class.getName());
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PHONE_PATTERN = "^(0|\\+84)[0-9]{9}$";
    
    public static void handleFileException(JPanel panel, IOException e) {
        String message = "Lỗi khi thao tác với file: " + e.getMessage();
        LOGGER.log(Level.SEVERE, message, e);
        JOptionPane.showMessageDialog(panel, message, "Lỗi File", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void handleParseException(JPanel panel, ParseException e) {
        String message = "Lỗi định dạng ngày tháng. Vui lòng nhập theo định dạng dd/MM/yyyy";
        LOGGER.log(Level.SEVERE, message, e);
        JOptionPane.showMessageDialog(panel, message, "Lỗi Định dạng", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void handleGeneralException(JPanel panel, Exception e) {
        String message = "Đã xảy ra lỗi: " + e.getMessage();
        LOGGER.log(Level.SEVERE, message, e);
        JOptionPane.showMessageDialog(panel, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void handleValidationException(JPanel panel, String message) {
        LOGGER.log(Level.WARNING, message);
        JOptionPane.showMessageDialog(panel, message, "Lỗi Xác thực", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean validateEmail(JPanel panel, String email) {
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            handleValidationException(panel, "Email không hợp lệ! Vui lòng nhập đúng định dạng (vd: example@email.com)");
            return false;
        }
        return true;
    }

    public static boolean validatePhone(JPanel panel, String phone) {
        if (!Pattern.matches(PHONE_PATTERN, phone)) {
            handleValidationException(panel, "Số điện thoại không hợp lệ! Vui lòng nhập đúng định dạng (vd: 0123456789 hoặc +84123456789)");
            return false;
        }
        return true;
    }
} 