package Utils;

public class ValidationUtils {

    public static boolean isValidName(String name) {
        return name != null && name.matches("^[a-zA-ZÀ-ỹ\\s]+$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^0[0-9]{9}$");
    }

    

    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 4;
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 5;
    }

	public static boolean isPhoneValid(String phone) {
		return phone != null && phone.matches("^0[0-9]{9}$");
	}

	public static boolean isCCCDValid(String idCard) {
		return idCard.matches("^\\d{12}$"); // cho phép null hoặc 12 số
	    
	}

    // ... thêm các validate khác nếu cần
}
