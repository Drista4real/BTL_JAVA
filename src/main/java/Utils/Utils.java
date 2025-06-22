package Utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class Utils {
	public static Date parseDate(String dateStr) {
        try {
        	java.util.Date utilDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
            return new Date(utilDate.getTime());
        } catch (Exception e) {
            System.out.println("Invalid date format. Please enter in dd/MM/yyyy.");
            return null;
        }
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        return Pattern.matches("0\\d{9}", phoneNumber);
    }

    public static boolean validateIDCard(String idCard) {
        return Pattern.matches("\\d{12}", idCard);
    }

    public static void main(String[] args) {
        System.out.println(validatePhoneNumber("0123456789")); // true
        System.out.println(validatePhoneNumber("1123456789")); // false
        System.out.println(validateIDCard("123456789012")); // true
        System.out.println(validateIDCard("abc456789012")); // false
    }
}
