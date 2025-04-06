package com.utc2.utils;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionUtils {
    private static final Logger LOGGER = Logger.getLogger(ExceptionUtils.class.getName());
    
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
} 